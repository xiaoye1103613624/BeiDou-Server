# -*- coding: utf-8 -*-
"""Continue orange-wz node paste via HTTP MCP (GUI-shared trees). Do not DELETE session."""
from __future__ import annotations

import json
import os
import sys
import time
import urllib.error
import urllib.request

URL = "http://127.0.0.1:10002/mcp"
BATCH_DIR = r"F:\MXD_dev\BeiDou-Server\gms-server\tools\_wz_xml_fix\copy_batches\split"
STAGING_DIR = r"F:\MXD_dev\BeiDou-Server\gms-server\tools\_wz_xml_fix\merged_img"
LOG_PATH = r"F:\MXD_dev\BeiDou-Server\gms-server\tools\_wz_xml_fix\mcp_paste_rest.log"
MAP_IDS_PATH = r"F:\MXD_dev\BeiDou-Server\gms-server\tools\_wz_xml_fix\client_miss_Map_stubs.txt"
NPC_STUBS_PATH = r"F:\MXD_dev\BeiDou-Server\gms-server\tools\_wz_xml_fix\client_miss_Npc_stubs.txt"

SRC_BASE = r"F:\MXD_dev\扩展改动\妖精学院+列娜海峡\Data"
LIVE_BASE = r"F:\MXD_dev\BeiDou-Client\Data"

LIVE = {
    "QuestInfo": os.path.join(LIVE_BASE, r"Quest\QuestInfo.img"),
    "Act": os.path.join(LIVE_BASE, r"Quest\Act.img"),
    "Check": os.path.join(LIVE_BASE, r"Quest\Check.img"),
    "Say": os.path.join(LIVE_BASE, r"Quest\Say.img"),
    "Etc": os.path.join(LIVE_BASE, r"String\Etc.img"),
    "Npc": os.path.join(LIVE_BASE, r"String\Npc.img"),
    "Map": os.path.join(LIVE_BASE, r"String\Map.img"),
}
SRC = {
    "QuestInfo": os.path.join(SRC_BASE, r"Quest\QuestInfo.img"),
    "Act": os.path.join(SRC_BASE, r"Quest\Act.img"),
    "Check": os.path.join(SRC_BASE, r"Quest\Check.img"),
    "Say": os.path.join(SRC_BASE, r"Quest\Say.img"),
    "Etc": os.path.join(SRC_BASE, r"String\Etc.img"),
    "Npc": os.path.join(SRC_BASE, r"String\Npc.img"),
    "Map": os.path.join(SRC_BASE, r"String\Map.img"),
}

SESSION = None
RPC_ID = 1


def log(msg: str) -> None:
    line = f"{time.strftime('%H:%M:%S')} {msg}"
    print(line, flush=True)
    with open(LOG_PATH, "a", encoding="utf-8") as f:
        f.write(line + "\n")


def http_json(body: dict, session: str | None, timeout: int = 300) -> tuple[dict | list | None, dict]:
    data = json.dumps(body, ensure_ascii=False).encode("utf-8")
    headers = {
        "Content-Type": "application/json; charset=utf-8",
        "Accept": "application/json, text/event-stream",
        "MCP-Protocol-Version": "2025-03-26",
    }
    if session:
        headers["Mcp-Session-Id"] = session
    req = urllib.request.Request(URL, data=data, headers=headers, method="POST")
    try:
        with urllib.request.urlopen(req, timeout=timeout) as resp:
            raw = resp.read().decode("utf-8", "replace")
            hdrs = {k.lower(): v for k, v in resp.headers.items()}
            if not raw.strip():
                return None, hdrs
            if raw.startswith("event:") or raw.lstrip().startswith("data:"):
                for line in raw.splitlines():
                    if line.startswith("data:"):
                        return json.loads(line[5:].strip()), hdrs
            return json.loads(raw), hdrs
    except urllib.error.HTTPError as e:
        err = e.read().decode("utf-8", "replace")
        raise RuntimeError(f"HTTP {e.code}: {err[:2000]}") from e


def rpc(method: str, params=None, timeout: int = 300):
    global RPC_ID
    RPC_ID += 1
    body = {"jsonrpc": "2.0", "id": RPC_ID, "method": method}
    if params is not None:
        body["params"] = params
    return http_json(body, SESSION, timeout=timeout)


def notify(method: str, params=None) -> None:
    body = {"jsonrpc": "2.0", "method": method}
    if params is not None:
        body["params"] = params
    try:
        http_json(body, SESSION, timeout=30)
    except Exception as e:
        log(f"notify {method} ignored: {e}")


def tool(name: str, arguments: dict, timeout: int = 300) -> dict:
    payload, _ = rpc("tools/call", {"name": name, "arguments": arguments}, timeout=timeout)
    if not isinstance(payload, dict):
        raise RuntimeError(f"{name} empty response")
    if "error" in payload:
        raise RuntimeError(f"{name} rpc error: {payload['error']}")
    result = payload.get("result") or {}
    if result.get("isError"):
        text = ""
        for c in result.get("content") or []:
            if isinstance(c, dict) and c.get("type") == "text":
                text = c.get("text") or ""
                break
        raise RuntimeError(f"{name} tool error: {text[:2000]}")
    sc = result.get("structuredContent")
    if isinstance(sc, dict):
        return sc
    # fallback parse text
    for c in result.get("content") or []:
        if isinstance(c, dict) and c.get("type") == "text":
            try:
                parsed = json.loads(c.get("text") or "{}")
                if isinstance(parsed, dict):
                    return parsed
            except json.JSONDecodeError:
                return {"text": c.get("text")}
    return result


def init_session() -> str:
    global SESSION
    body = {
        "jsonrpc": "2.0",
        "id": 1,
        "method": "initialize",
        "params": {
            "protocolVersion": "2025-03-26",
            "capabilities": {},
            "clientInfo": {"name": "mcp_paste_rest", "version": "1"},
        },
    }
    payload, hdrs = http_json(body, None, timeout=30)
    sid = hdrs.get("mcp-session-id")
    if not sid:
        raise RuntimeError(f"no session id headers={hdrs} body={payload}")
    SESSION = sid
    notify("notifications/initialized")
    return sid


def load_batch(kind: str, idx: int) -> list:
    path = os.path.join(BATCH_DIR, f"{kind}_b{idx}.json")
    with open(path, encoding="utf-8") as f:
        return json.load(f)


def paste_count(result: dict) -> int:
    pasted = result.get("pasted")
    if isinstance(pasted, list):
        return len(pasted)
    results = result.get("results")
    if isinstance(results, list):
        n = 0
        for r in results:
            p = r.get("pasted") if isinstance(r, dict) else None
            if isinstance(p, list):
                n += len(p)
            elif p:
                n += 1
        return n
    return 0


def copy_paste(kind: str, sources: list, dest_root: str, dest_node: str) -> tuple[int, int]:
    copy_res = tool("copy_nodes", {"autoParse": True, "sources": sources}, timeout=300)
    copied = int(copy_res.get("copiedCount") or 0)
    paste_res = tool(
        "paste_nodes",
        {
            "autoParse": True,
            "clearClipboard": True,
            "strategy": "SKIP",
            "rootPath": dest_root,
            "nodePath": dest_node,
        },
        timeout=300,
    )
    pasted = paste_count(paste_res)
    log(f"  {kind} copy={copied} paste={pasted} destNode={dest_node!r}")
    return copied, pasted


def run_file_batches(kind: str, start: int, dest_node: str) -> dict:
    totals = {"copied": 0, "pasted": 0, "batches": 0}
    idx = start
    while True:
        path = os.path.join(BATCH_DIR, f"{kind}_b{idx}.json")
        if not os.path.isfile(path):
            break
        sources = load_batch(kind, idx)
        log(f"{kind} batch {idx} n={len(sources)}")
        c, p = copy_paste(kind, sources, LIVE[kind], dest_node)
        totals["copied"] += c
        totals["pasted"] += p
        totals["batches"] += 1
        idx += 1
    return totals


def parent_path(node_path: str) -> str:
    if not node_path:
        return ""
    parts = node_path.replace("\\", "/").split("/")
    if len(parts) <= 1:
        return ""
    return "/".join(parts[:-1])


def match_node_path(matches, want: str) -> str | None:
    if not matches:
        return None
    want_l = want.lower()
    exact = []
    for m in matches:
        if not isinstance(m, dict):
            continue
        name = str(m.get("name") or "")
        np = str(m.get("nodePath") or "")
        if name == want or np == want or np.endswith("/" + want):
            exact.append(np or name)
        elif name.lower() == want_l or np.lower().endswith("/" + want_l):
            exact.append(np or name)
    if exact:
        # prefer shortest path (direct child of region)
        exact.sort(key=lambda s: (s.count("/"), len(s)))
        return exact[0]
    first = matches[0]
    if isinstance(first, dict):
        return str(first.get("nodePath") or first.get("name") or "") or None
    return None


def paste_map_ids() -> dict:
    with open(MAP_IDS_PATH, encoding="utf-8") as f:
        ids = [ln.strip() for ln in f if ln.strip()]
    ok, miss = [], []
    src = SRC["Map"]
    live = LIVE["Map"]
    for mid in ids:
        try:
            found = tool(
                "find_node",
                {"autoParse": True, "rootPath": src, "nodePath": mid},
                timeout=60,
            )
        except Exception:
            found = {}
        np = None
        node = found.get("node") if isinstance(found, dict) else None
        if isinstance(node, dict) and (node.get("nodePath") or node.get("name")):
            np = str(node.get("nodePath") or node.get("name"))
        if not np:
            try:
                sr = tool(
                    "search_node",
                    {
                        "autoParse": True,
                        "rootPath": src,
                        "nodePath": "",
                        "keyword": mid,
                        "searchIn": "name",
                    },
                    timeout=120,
                )
            except Exception as e:
                log(f"  Map {mid} search failed: {e}")
                miss.append(mid)
                continue
            matches = sr.get("matches") or []
            np = match_node_path(matches, mid)
        if not np:
            log(f"  Map {mid} NOT FOUND")
            miss.append(mid)
            continue
        parent = parent_path(np)
        log(f"  Map {mid} srcPath={np} pasteParent={parent!r}")
        try:
            copy_paste("Map", [{"rootPath": src, "nodePath": np}], live, parent)
            ok.append({"id": mid, "nodePath": np, "parent": parent})
        except Exception as e:
            log(f"  Map {mid} paste failed: {e}")
            miss.append(mid)
    return {"ok": ok, "miss": miss, "pasted": len(ok)}


def save_staging() -> dict:
    os.makedirs(STAGING_DIR, exist_ok=True)
    out = {}
    for kind, root in LIVE.items():
        dest = os.path.join(STAGING_DIR, f"{kind}.merged.img")
        log(f"save_as {kind} -> {dest}")
        try:
            res = tool(
                "save_as",
                {
                    "autoParse": True,
                    "rootPath": root,
                    "nodePath": "",
                    "filePath": dest,
                    "unloadAfterSave": False,
                    "clearCache": False,
                },
                timeout=600,
            )
            size = os.path.getsize(dest) if os.path.isfile(dest) else -1
            out[kind] = {"ok": True, "path": dest, "size": size, "result": res}
            log(f"  saved size={size}")
        except Exception as e:
            out[kind] = {"ok": False, "path": dest, "error": str(e)}
            log(f"  SAVE_AS FAIL {kind}: {e}")
    return out


def verify_samples(npc_id: str, map_ok: list) -> dict:
    checks = [
        ("QuestInfo", "8680"),
        ("QuestInfo", "31100"),
        ("QuestInfo", "32196"),
        ("Act", "31100"),
        ("Check", "32196"),
        ("Say", "32196"),
        ("Etc", "Etc/4000302"),
        ("Npc", npc_id),
    ]
    if map_ok:
        checks.append(("Map", map_ok[0]["nodePath"]))
    result = {}
    for kind, np in checks:
        key = f"{kind}:{np}"
        try:
            found = tool(
                "find_node",
                {"autoParse": True, "rootPath": LIVE[kind], "nodePath": np},
                timeout=60,
            )
            node = found.get("node") if isinstance(found, dict) else None
            exists = isinstance(node, dict) and bool(node.get("name") or node.get("nodePath") or node.get("type"))
            result[key] = {"ok": exists, "node": node}
            log(f"verify {key} ok={exists}")
        except Exception as e:
            result[key] = {"ok": False, "error": str(e)}
            log(f"verify {key} FAIL {e}")
    return result


def try_save_live() -> dict:
    out = {}
    for kind, root in LIVE.items():
        log(f"save_node live {kind}")
        try:
            res = tool(
                "save_node",
                {
                    "autoParse": True,
                    "rootPath": root,
                    "nodePath": "",
                    "unloadAfterSave": False,
                    "clearCache": False,
                },
                timeout=600,
            )
            out[kind] = {"ok": True, "result": res}
            log(f"  live save OK {kind}")
        except Exception as e:
            out[kind] = {"ok": False, "error": str(e)}
            log(f"  live save FAIL {kind}: {e}")
    return out


def main() -> int:
    open(LOG_PATH, "w", encoding="utf-8").write("")
    sid = init_session()
    log(f"session={sid}")
    roots = tool("list_loaded_roots", {}, timeout=60)
    log(f"loaded rootCount={roots.get('rootCount')} names={[r.get('name') if isinstance(r, dict) else r for r in (roots.get('roots') or [])]}")
    if int(roots.get("rootCount") or 0) < 10:
        log("ERROR: GUI trees not visible in new session; aborting to protect in-memory pastes")
        return 2

    summary = {}
    summary["QuestInfo"] = run_file_batches("QuestInfo", 5, "")
    summary["Act"] = run_file_batches("Act", 0, "")
    summary["Check"] = run_file_batches("Check", 0, "")
    summary["Say"] = run_file_batches("Say", 0, "")
    summary["Etc"] = run_file_batches("Etc", 0, "Etc")
    summary["Npc"] = run_file_batches("Npc", 0, "")
    summary["Map"] = paste_map_ids()

    staging = save_staging()
    with open(NPC_STUBS_PATH, encoding="utf-8") as f:
        npc_id = next((ln.strip() for ln in f if ln.strip()), "2030016")
    verify = verify_samples(npc_id, summary["Map"].get("ok") or [])
    live_save = try_save_live()

    report = {
        "session": sid,
        "pasted": summary,
        "staging": staging,
        "verify": {k: {"ok": v.get("ok"), "error": v.get("error")} for k, v in verify.items()},
        "liveSave": live_save,
    }
    report_path = r"F:\MXD_dev\BeiDou-Server\gms-server\tools\_wz_xml_fix\mcp_paste_rest_report.json"
    with open(report_path, "w", encoding="utf-8") as f:
        json.dump(report, f, ensure_ascii=False, indent=2)
    log(f"report written {report_path}")
    return 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except Exception as e:
        log(f"FATAL {type(e).__name__}: {e}")
        raise
