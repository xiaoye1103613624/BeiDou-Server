#!/usr/bin/env python3
"""
Append missing WZ nodes from SOURCE client Data into TARGET client Data.

Strategy A: TARGET missing entire .img -> shutil copy whole file.
Strategy B: both have file -> orange-wz MCP paste_nodes with SKIP (append only).

Parallelism (--workers N, default 1):
  - Different rel paths merge concurrently via ThreadPoolExecutor.
  - Same target .img path uses a per-file threading.Lock (one writer at a time).
  - Each worker thread owns a dedicated MCP session (thread-local client).
  - All MCP tool calls share a global semaphore (--mcp-concurrent, default 1)
    because orange-wz may not handle concurrent requests safely.
  - save_node completes before the per-file lock is released.
  - Logging and progress counters are thread-safe; --skip-done remains idempotent.

Usage:
  python append_img_nodes.py --phase string
  python append_img_nodes.py --phase item-cash --limit 10
  python append_img_nodes.py --rel String/Cash.img
  python append_img_nodes.py --phase other-conflicts --workers 8 --skip-done
"""
from __future__ import annotations

import argparse
import hashlib
import json
import shutil
import sys
import threading
import time
import urllib.request
from concurrent.futures import ThreadPoolExecutor, as_completed
from contextlib import contextmanager
from dataclasses import dataclass, field
from datetime import datetime
from pathlib import Path

GMS_KEY = {
    "name": "083-GMS",
    "userKeyBase64": "EwAAAFIAAAAqAAAAWwAAAAgAAAACAAAAEAAAAGAAAAAGAAAAAgAAAEMAAAAPAAAAtAAAAEsAAAA1AAAABQAAABsAAAAKAAAAXwAAAAkAAAAPAAAAUAAAAAwAAAAbAAAAMwAAAFUAAAABAAAACQAAAFIAAADeAAAAxwAAAB4AAAA=",
    "ivBase64": "TSPHKw==",
}

DEFAULT_SOURCE = Path(r"E:/mxd_soft/2.客户端/083/北斗GMS083_v1.11_ASM版/BeiDou-Client-V15_fix/Data")
DEFAULT_TARGET = Path(r"E:/mxd_soft/2.客户端/083/beidou_client_xiaoye/BeiDou-Client_1/Data")
DEFAULT_MCP = "http://127.0.0.1:10002/mcp"
DEFAULT_BACKUP = Path(r"E:/mxd_soft/2.客户端/083/beidou_client_xiaoye/_img_merge_backup")
DEFAULT_LOG = Path(__file__).resolve().parent / "_append_img_nodes.log"

EXCLUDE_REL = {
    "UI/UIWindow.img",
    "Effect/BasicEff.img",
    "Effect/DamageSkin.img",
}

ITEM_CASH_SKIP = {"0591.img", "0592.img"}


class McpClient:
    def __init__(self, url: str, mcp_gate: threading.Semaphore | None = None, timeout: float = 600):
        self.url = url
        self.session = None
        self.rid = 0
        self._mcp_gate = mcp_gate
        self._rid_lock = threading.Lock()
        self.timeout = timeout

    def call(self, method, params=None):
        with self._rid_lock:
            self.rid += 1
            rid = self.rid
        payload = {"jsonrpc": "2.0", "method": method, "params": params or {}, "id": rid}
        headers = {"Content-Type": "application/json"}
        if self.session:
            headers["Mcp-Session-Id"] = self.session
        req = urllib.request.Request(
            self.url, data=json.dumps(payload).encode(), headers=headers, method="POST"
        )

        def _do_call():
            with urllib.request.urlopen(req, timeout=self.timeout) as resp:
                body = resp.read().decode()
                if "Mcp-Session-Id" in resp.headers:
                    self.session = resp.headers["Mcp-Session-Id"]
                return None if not body.strip() else json.loads(body)

        if self._mcp_gate is not None:
            with self._mcp_gate:
                return _do_call()
        return _do_call()

    def tool(self, name: str, arguments: dict):
        return self.call("tools/call", {"name": name, "arguments": arguments})


class FileLockPool:
    """One threading.Lock per target rel path."""

    def __init__(self):
        self._locks: dict[str, threading.Lock] = {}
        self._meta = threading.Lock()

    @contextmanager
    def acquire(self, rel: str):
        with self._meta:
            lock = self._locks.setdefault(rel, threading.Lock())
        lock.acquire()
        try:
            yield
        finally:
            lock.release()


_tls = threading.local()
_init_lock = threading.Lock()
_init_count = 0


def norm(p: Path) -> str:
    return str(p.resolve()).replace("\\", "/")


def file_hash(path: Path, chunk: int = 1 << 20) -> str:
    h = hashlib.md5()
    with open(path, "rb") as f:
        while True:
            b = f.read(chunk)
            if not b:
                break
            h.update(b)
    return h.hexdigest()


def files_identical(a: Path, b: Path) -> bool:
    """Fast path: same size then MD5; skip MCP when SOURCE and TARGET are byte-identical."""
    try:
        sa, sb = a.stat().st_size, b.stat().st_size
    except OSError:
        return False
    if sa != sb:
        return False
    return file_hash(a) == file_hash(b)


def tool_text(r) -> str:
    if not r:
        return ""
    res = r.get("result", {})
    if res.get("isError"):
        return res["content"][0]["text"]
    sc = res.get("structuredContent")
    if sc:
        return json.dumps(sc, ensure_ascii=False)
    return str(r)


def pasted_count(r) -> int:
    if not r:
        return 0
    sc = r.get("result", {}).get("structuredContent") or {}
    pasted = sc.get("pasted") or []
    return len(pasted)


def top_child_names(c: McpClient, root: str) -> list[str]:
    r = c.tool("get_node_tree_json", {"rootPath": root, "nodePath": "", "maxDepth": 1})
    tree = r.get("result", {}).get("structuredContent", {}).get("tree", {})
    return [ch.get("name") for ch in tree.get("children") or [] if ch.get("name")]


def list_child_names(c: McpClient, root: str, node_path: str) -> list[str]:
    r = c.tool("list_children", {"rootPath": root, "nodePath": node_path, "autoParse": True})
    children = r.get("result", {}).get("structuredContent", {}).get("children", [])
    if children:
        return [ch.get("name") for ch in children if isinstance(ch, dict) and ch.get("name")]
    if node_path == "":
        return top_child_names(c, root)
    return []


@dataclass
class MergeResult:
    rel: str
    mode: str
    nodes_added: int = 0
    message: str = ""
    ok: bool = True


@dataclass
class Runner:
    source: Path
    target: Path
    backup_dir: Path
    mcp_url: str
    dry_run: bool = False
    batch_size: int = 100
    mcp_concurrent: int = 1
    mcp_timeout: float = 600
    log_lines: list[str] = field(default_factory=list)
    _log_lock: threading.Lock = field(default_factory=threading.Lock, repr=False)
    _mcp_gate: threading.Semaphore = field(default_factory=lambda: threading.Semaphore(1), repr=False)

    def __post_init__(self):
        self._mcp_gate = threading.Semaphore(max(1, self.mcp_concurrent))

    def log(self, msg: str, flush_log: Path | None = None, append: bool = False):
        line = f"[{datetime.now():%Y-%m-%d %H:%M:%S}] {msg}"
        with self._log_lock:
            print(line, flush=True)
            self.log_lines.append(line)
            if flush_log is not None:
                flush_log.parent.mkdir(parents=True, exist_ok=True)
                with open(flush_log, "a" if append else "w", encoding="utf-8") as f:
                    f.write(line + "\n")

    def mcp(self) -> McpClient:
        c = McpClient(self.mcp_url, self._mcp_gate, self.mcp_timeout)
        c.call(
            "initialize",
            {
                "protocolVersion": "2024-11-05",
                "capabilities": {},
                "clientInfo": {"name": "append-img-nodes", "version": "1.0"},
            },
        )
        c.call("notifications/initialized", {})
        return c

    def thread_mcp(self) -> McpClient:
        client = getattr(_tls, "client", None)
        if client is None:
            global _init_count
            with _init_lock:
                slot = _init_count
                _init_count += 1
            if slot > 0:
                time.sleep(min(0.25 * slot, 2.0))
            client = self.mcp()
            _tls.client = client
        return client

    def backup_file(self, rel: str):
        src = self.target / rel
        if not src.exists():
            return
        dst = self.backup_dir / rel
        dst.parent.mkdir(parents=True, exist_ok=True)
        if not dst.exists():
            shutil.copy2(src, dst)

    def copy_whole(self, rel: str) -> MergeResult:
        s = self.source / rel
        t = self.target / rel
        if not s.exists():
            return MergeResult(rel, "skip", message="SOURCE 不存在")
        if t.exists():
            return MergeResult(rel, "skip", message="TARGET 已存在")
        if self.dry_run:
            return MergeResult(rel, "copy", message="[dry-run] 将整文件复制")
        t.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(s, t)
        return MergeResult(rel, "copy", nodes_added=1, message="整文件复制完成")


    def _validate_save_or_rollback(self, tgt: str, rel: str, backup_snapshot: Path | None) -> tuple[bool, str]:
        """After save_node, ensure file size >= 100; rollback from snapshot/backup on failure."""
        tgt_path = Path(tgt)
        try:
            size = tgt_path.stat().st_size if tgt_path.exists() else 0
        except OSError as e:
            size = 0
            self.log(f"  save size check error: {e}")
        if size >= 100:
            return True, f"save ok size={size}"
        restored = False
        candidates = []
        if backup_snapshot is not None:
            candidates.append(backup_snapshot)
        candidates.append(self.backup_dir / Path(rel))
        for cand in candidates:
            if cand is None:
                continue
            try:
                if cand.exists() and cand.stat().st_size >= 100:
                    shutil.copy2(cand, tgt_path)
                    restored = True
                    break
            except OSError as e:
                self.log(f"  rollback copy failed from {cand}: {e}")
        status = "ok" if restored else "FAILED"
        msg = f"save corrupted size={size}; rollback={status}"
        self.log(f"  {msg}")
        return False, msg

    def _batch_copy_missing(self, c: McpClient, src: str, tgt: str, tgt_path: str, missing: list[str]) -> int:
        if not missing:
            return 0
        if self.dry_run:
            return len(missing)
        total = 0
        for i in range(0, len(missing), self.batch_size):
            batch = missing[i : i + self.batch_size]
            sources = [{"rootPath": src, "nodePath": (f"{tgt_path}/{n}" if tgt_path else n)} for n in batch]
            c.tool("copy_nodes", {"sources": sources, "autoParse": True})
            paste = c.tool(
                "paste_nodes",
                {"rootPath": tgt, "nodePath": tgt_path, "strategy": "SKIP", "autoParse": True},
            )
            total += pasted_count(paste)
            if paste and paste.get("result", {}).get("isError"):
                self.log(f"  paste error batch {i}: {tool_text(paste)[:200]}")
        snap = None
        tgt_path_obj = Path(tgt)
        if tgt_path_obj.exists() and not self.dry_run:
            snap = tgt_path_obj.with_suffix(tgt_path_obj.suffix + ".presave")
            try:
                shutil.copy2(tgt_path_obj, snap)
            except OSError:
                snap = None
        save = c.tool("save_node", {"rootPath": tgt, "autoParse": True})
        if save and save.get("result", {}).get("isError"):
            self.log(f"  save error: {tool_text(save)[:200]}")
        try:
            rel_guess = str(Path(tgt).resolve().relative_to(self.target.resolve())).replace(chr(92), "/")
        except ValueError:
            rel_guess = Path(tgt).name
        ok, msg = self._validate_save_or_rollback(tgt, rel_guess, snap)
        if snap is not None and snap.exists():
            try:
                snap.unlink()
            except OSError:
                pass
        if not ok:
            self.log(f"  save validation failed: {msg}")
        return total

    def _merge_subtree(self, c: McpClient, src: str, tgt: str, src_path: str, tgt_path: str) -> int:
        src_children = list_child_names(c, src, src_path)
        if not src_children:
            return 0
        tgt_children = set(list_child_names(c, tgt, tgt_path))
        sample = src_children[: min(3, len(src_children))]
        numeric = all((n.isdigit() or (n and n[0].isdigit())) for n in sample)

        if not numeric:
            sub_added = 0
            for cat in src_children:
                sp = f"{src_path}/{cat}" if src_path else cat
                tp = f"{tgt_path}/{cat}" if tgt_path else cat
                if cat in tgt_children:
                    sub_added += self._merge_subtree(c, src, tgt, sp, tp)
                else:
                    if self.dry_run:
                        sub_added += 1
                        continue
                    c.tool("copy_nodes", {"sources": [{"rootPath": src, "nodePath": sp}], "autoParse": True})
                    paste = c.tool(
                        "paste_nodes",
                        {"rootPath": tgt, "nodePath": tgt_path, "strategy": "SKIP", "autoParse": True},
                    )
                    sub_added += pasted_count(paste)
            return sub_added

        missing = [n for n in src_children if n not in tgt_children]
        return self._batch_copy_missing(c, src, tgt, tgt_path, missing)

    def merge_img_nodes(self, c: McpClient, rel: str) -> MergeResult:
        src = norm(self.source / rel)
        tgt = norm(self.target / rel)
        if not Path(src).exists():
            return MergeResult(rel, "skip", message="SOURCE 不存在", ok=False)
        if not Path(tgt).exists():
            return MergeResult(rel, "skip", message="TARGET 不存在，应走整文件复制")

        c.tool("unload_all", {})
        load = c.tool("load_files", {"paths": [src, tgt], "key": GMS_KEY})
        if load and load.get("result", {}).get("isError"):
            return MergeResult(rel, "merge", message=tool_text(load), ok=False)

        src_top = top_child_names(c, src)
        tgt_top = top_child_names(c, tgt)
        if not src_top:
            return MergeResult(rel, "merge", message="SOURCE 无子节点")

        added = 0

        if len(src_top) == 1 and len(tgt_top) == 1 and src_top[0] == tgt_top[0]:
            inner = src_top[0]
            if self.dry_run:
                missing_cats = [c for c in list_child_names(c, src, inner) if c not in set(list_child_names(c, tgt, inner))]
                return MergeResult(rel, "merge", nodes_added=len(missing_cats), message=f"[dry-run] wrapper {inner}")
            c.tool("copy_nodes", {"sources": [{"rootPath": src, "nodePath": inner}], "autoParse": True})
            paste = c.tool(
                "paste_nodes",
                {"rootPath": tgt, "nodePath": "", "strategy": "SKIP", "autoParse": True},
            )
            added = pasted_count(paste)
            snap = Path(tgt).with_suffix(Path(tgt).suffix + ".presave")
            try:
                shutil.copy2(tgt, snap)
            except OSError:
                snap = None
            save = c.tool("save_node", {"rootPath": tgt, "autoParse": True})
            if save and save.get("result", {}).get("isError"):
                if snap is not None and snap.exists():
                    try:
                        snap.unlink()
                    except OSError:
                        pass
                return MergeResult(rel, "merge", nodes_added=added, message=tool_text(save), ok=False)
            ok, msg = self._validate_save_or_rollback(tgt, rel, snap)
            if snap is not None and snap.exists():
                try:
                    snap.unlink()
                except OSError:
                    pass
            c.tool("unload_all", {})
            if not ok:
                return MergeResult(rel, "merge", nodes_added=added, message=msg, ok=False)
            return MergeResult(rel, "merge", nodes_added=added, message=f"wrapper {inner} 追加 {added} 节点")

        sample = src_top[: min(5, len(src_top))]
        if all((n.isdigit() or (n and n[0].isdigit())) for n in sample):
            missing = [n for n in src_top if n not in set(tgt_top)]
            added = self._batch_copy_missing(c, src, tgt, "", missing)
            c.tool("unload_all", {})
            return MergeResult(rel, "merge", nodes_added=added, message=f"flat 追加 {added}/{len(missing)} 节点")

        tgt_set = set(tgt_top)
        for cat in src_top:
            sp = cat
            tp = cat if cat in tgt_set else ""
            added += self._merge_subtree(c, src, tgt, sp, tp)
        if not self.dry_run and added > 0:
            snap = Path(tgt).with_suffix(Path(tgt).suffix + ".presave")
            try:
                shutil.copy2(tgt, snap)
            except OSError:
                snap = None
            save = c.tool("save_node", {"rootPath": tgt, "autoParse": True})
            if save and save.get("result", {}).get("isError"):
                if snap is not None and snap.exists():
                    try:
                        snap.unlink()
                    except OSError:
                        pass
                return MergeResult(rel, "merge", nodes_added=added, message=tool_text(save), ok=False)
            ok, msg = self._validate_save_or_rollback(tgt, rel, snap)
            if snap is not None and snap.exists():
                try:
                    snap.unlink()
                except OSError:
                    pass
            if not ok:
                c.tool("unload_all", {})
                return MergeResult(rel, "merge", nodes_added=added, message=msg, ok=False)
        c.tool("unload_all", {})
        return MergeResult(rel, "merge", nodes_added=added, message=f"多分类追加 {added} 节点")

    def should_exclude(self, rel: str) -> bool:
        rel_norm = rel.replace("\\", "/")
        if rel_norm in EXCLUDE_REL:
            return True
        if rel_norm.startswith("Item/Cash/"):
            name = Path(rel_norm).name
            if name in ITEM_CASH_SKIP or name.startswith("590"):
                return True
        return False

    def process(self, rel: str, c: McpClient) -> MergeResult:
        rel = rel.replace("\\", "/")
        if self.should_exclude(rel):
            return MergeResult(rel, "skip", message="排除列表")

        s, t = self.source / rel, self.target / rel
        if not s.exists():
            return MergeResult(rel, "skip", message="SOURCE 不存在")

        if not t.exists():
            return self.copy_whole(rel)

        if files_identical(s, t):
            return MergeResult(rel, "skip", message="SOURCE/TARGET 字节一致，跳过 MCP")

        self.backup_file(rel)
        return self.merge_img_nodes(c, rel)


def load_lines(path: Path) -> list[str]:
    if not path.exists():
        return []
    return [ln.strip().replace("\\", "/") for ln in path.read_text(encoding="utf-8").splitlines() if ln.strip()]


def string_conflicts() -> list[str]:
    return [
        "String/Cash.img",
        "String/Consume.img",
        "String/Eqp.img",
        "String/Etc.img",
        "String/Ins.img",
        "String/Map.img",
        "String/Mob.img",
        "String/Npc.img",
        "String/Pet.img",
        "String/Skill.img",
    ]


OTHER_CONFLICT_PREFIXES = (
    "Map/",
    "Mob/",
    "Npc/",
    "Reactor/",
    "Skill/",
    "Morph/",
    "Item/",
    "Effect/",
    "UI/",
)


def both_exist(source: Path, target: Path, rels: list[str]) -> list[str]:
    out: list[str] = []
    for rel in rels:
        if (source / rel).exists() and (target / rel).exists():
            out.append(rel)
    return out


def character_conflicts(source: Path, target: Path, conflicts: list[str]) -> list[str]:
    return both_exist(
        source,
        target,
        [r for r in conflicts if r.startswith("Character/") and r.endswith(".img")],
    )


def other_conflicts(source: Path, target: Path, conflicts: list[str]) -> list[str]:
    work: list[str] = []
    for rel in conflicts:
        if not rel.endswith(".img"):
            continue
        if rel.startswith(("String/", "Character/", "Item/Cash/", "Sound/", "Etc/", "Quest/", "TamingMob/")):
            continue
        if rel in EXCLUDE_REL:
            continue
        if not any(rel.startswith(p) for p in OTHER_CONFLICT_PREFIXES):
            continue
        if rel.startswith("Item/Cash/"):
            continue
        work.append(rel)
    return both_exist(source, target, work)


def run_parallel(
    runner: Runner,
    work: list[str],
    workers: int,
    log_path: Path,
    append_log: bool,
) -> dict[str, int]:
    file_locks = FileLockPool()
    stats: dict[str, int] = {"copy": 0, "merge": 0, "skip": 0, "fail": 0, "nodes": 0}
    stats_lock = threading.Lock()
    progress = {"done": 0}
    progress_lock = threading.Lock()
    total = len(work)
    log_append = append_log

    def process_one(rel: str) -> tuple[str, MergeResult | None, str | None]:
        try:
            with file_locks.acquire(rel):
                result = runner.process(rel, runner.thread_mcp())
            return rel, result, None
        except Exception as e:
            return rel, None, str(e)

    with ThreadPoolExecutor(max_workers=workers) as pool:
        futures = {pool.submit(process_one, rel): rel for rel in work}
        for fut in as_completed(futures):
            rel, result, err = fut.result()
            with progress_lock:
                progress["done"] += 1
                idx = progress["done"]
            if err is not None:
                runner.log(f"[{idx}/{total}] {rel} | ERROR | {err}", log_path, append=log_append)
                log_append = True
                with stats_lock:
                    stats["fail"] += 1
                continue
            assert result is not None
            runner.log(
                f"[{idx}/{total}] {rel} | {result.mode} | +{result.nodes_added} | {result.message}",
                log_path,
                append=log_append,
            )
            log_append = True
            with stats_lock:
                stats[result.mode] = stats.get(result.mode, 0) + 1
                stats["nodes"] += result.nodes_added
                if not result.ok:
                    stats["fail"] += 1

    return stats


def run_sequential(
    runner: Runner,
    work: list[str],
    log_path: Path,
    append_log: bool,
) -> tuple[dict[str, int], McpClient]:
    c = runner.mcp()
    stats: dict[str, int] = {"copy": 0, "merge": 0, "skip": 0, "fail": 0, "nodes": 0}
    log_append = True

    for i, rel in enumerate(work, 1):
        try:
            r = runner.process(rel, c)
            runner.log(f"[{i}/{len(work)}] {rel} | {r.mode} | +{r.nodes_added} | {r.message}", log_path, append=log_append)
            log_append = True
            stats[r.mode] = stats.get(r.mode, 0) + 1
            stats["nodes"] += r.nodes_added
            if not r.ok:
                stats["fail"] += 1
        except Exception as e:
            runner.log(f"[{i}/{len(work)}] {rel} | ERROR | {e}", log_path, append=True)
            stats["fail"] += 1

    c.tool("unload_all", {})
    return stats, c


def load_done_from_log(log_path: Path) -> set[str]:
    done: set[str] = set()
    if not log_path.exists():
        return done
    for line in log_path.read_text(encoding="utf-8", errors="replace").splitlines():
        if "| merge |" in line or "| copy |" in line:
            parts = line.split("] ", 1)
            if len(parts) < 2:
                continue
            chunk = parts[1]
            if "] " not in chunk:
                continue
            rel = chunk.split("] ", 1)[1].split(" | ", 1)[0].strip()
            if rel:
                done.add(rel.replace("\\", "/"))
    return done


def main():
    ap = argparse.ArgumentParser(description="Append missing WZ img nodes SOURCE -> TARGET")
    ap.add_argument("--source", type=Path, default=DEFAULT_SOURCE)
    ap.add_argument("--target", type=Path, default=DEFAULT_TARGET)
    ap.add_argument(
        "--conflicts-file",
        type=Path,
        default=None,
        help="Conflict list (default: .eval_append/conflicts.txt)",
    )
    ap.add_argument("--backup", type=Path, default=DEFAULT_BACKUP)
    ap.add_argument("--mcp", default=DEFAULT_MCP)
    ap.add_argument(
        "--phase",
        choices=[
            "string",
            "item-cash",
            "character",
            "character-conflicts",
            "other-conflicts",
            "all",
            "file",
        ],
        default="string",
    )
    ap.add_argument("--rel", action="append", help="Process specific relative path(s)")
    ap.add_argument("--list", type=Path, help="File with relative paths, one per line")
    ap.add_argument("--limit", type=int, default=0)
    ap.add_argument("--dry-run", action="store_true")
    ap.add_argument("--skip-done", action="store_true", help="Skip rel paths already logged as merge/copy")
    ap.add_argument("--log", type=Path, default=DEFAULT_LOG)
    ap.add_argument("--append-log", action="store_true", help="Append to log file instead of overwrite")
    ap.add_argument(
        "--workers",
        type=int,
        default=1,
        help="Parallel worker threads (use 4-8 for character/other-conflicts; default 1 = sequential)",
    )
    ap.add_argument(
        "--mcp-concurrent",
        type=int,
        default=1,
        help="Max concurrent MCP requests across all workers (default 1; raise only if orange-wz is stable)",
    )
    ap.add_argument(
        "--mcp-timeout",
        type=float,
        default=600,
        help="MCP HTTP read timeout in seconds (default 600)",
    )
    args = ap.parse_args()

    if args.workers < 1:
        ap.error("--workers must be >= 1")
    if args.mcp_concurrent < 1:
        ap.error("--mcp-concurrent must be >= 1")

    runner = Runner(
        source=args.source,
        target=args.target,
        backup_dir=args.backup,
        mcp_url=args.mcp,
        dry_run=args.dry_run,
        mcp_concurrent=args.mcp_concurrent,
        mcp_timeout=args.mcp_timeout,
    )

    work: list[str] = []
    repo_root = Path(__file__).resolve().parents[2]
    conflicts_path = args.conflicts_file or (repo_root / ".eval_append" / "conflicts.txt")
    if args.rel:
        work.extend(args.rel)
    elif args.list:
        work.extend(load_lines(args.list))
    elif args.phase == "string":
        work.append("String/Item.img")
        work.extend(string_conflicts())
    elif args.phase == "item-cash":
        conflicts = load_lines(conflicts_path)
        work = [r for r in conflicts if r.startswith("Item/Cash/") and r.endswith(".img")]
    elif args.phase == "character":
        candidates = load_lines(repo_root / ".eval_append" / "append_candidates.txt")
        work = [r for r in candidates if r.startswith("Character/")]
    elif args.phase == "character-conflicts":
        conflicts = load_lines(conflicts_path)
        work = character_conflicts(args.source, args.target, conflicts)
    elif args.phase == "other-conflicts":
        conflicts = load_lines(conflicts_path)
        work = other_conflicts(args.source, args.target, conflicts)
    elif args.phase == "all":
        candidates = load_lines(repo_root / ".eval_append" / "append_candidates.txt")
        conflicts = load_lines(conflicts_path)
        work = candidates + [c for c in conflicts if c not in candidates]

    if args.skip_done:
        done = load_done_from_log(args.log)
        before = len(work)
        work = [r for r in work if r not in done]
        runner.log(f"skip-done: 跳过 {before - len(work)} 个已完成, 剩余 {len(work)}")

    if args.limit > 0:
        work = work[: args.limit]

    runner.log(
        f"开始处理 {len(work)} 个 img 文件 phase={args.phase} dry_run={args.dry_run} "
        f"workers={args.workers} mcp_concurrent={args.mcp_concurrent} mcp_timeout={args.mcp_timeout}",
        args.log,
        append=args.append_log,
    )

    t0 = time.time()
    if args.workers <= 1:
        stats, _c = run_sequential(runner, work, args.log, args.append_log)
    else:
        stats = run_parallel(runner, work, args.workers, args.log, args.append_log)
    elapsed = time.time() - t0
    summary = (
        f"完成: copy={stats.get('copy',0)} merge={stats.get('merge',0)} "
        f"skip={stats.get('skip',0)} fail={stats.get('fail',0)} "
        f"nodes_added={stats['nodes']} elapsed={elapsed:.1f}s"
    )
    runner.log(summary, args.log, append=True)

    log_body = "\n".join(runner.log_lines) + "\n"
    if not args.append_log:
        args.log.write_text(log_body, encoding="utf-8")
    print(f"\n日志: {args.log}")


if __name__ == "__main__":
    main()
