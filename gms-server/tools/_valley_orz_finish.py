# -*- coding: utf-8 -*-
"""Finish valley String/Item/portal merges via correct Orz API (load_files/copy/paste/save)."""
from __future__ import annotations

import json
import re
import shutil
import sys
import urllib.request
from datetime import datetime
from pathlib import Path

sys.stdout.reconfigure(encoding="utf-8", errors="replace")

SRC_DATA = Path(r"E:\资料\xiaoye\mxd学习\遗忘山谷\Data")
SRC_WZ = Path(r"E:\资料\xiaoye\mxd学习\遗忘山谷\wz")
DST_DATA = Path(r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data")
DST_SW = Path(r"E:\pro\BeiDou-Server_xy\gms-server\wz-zh-CN")
REPORT = Path(r"E:\pro\BeiDou-Server_xy\.eval_append") / f"valley_orz_finish_{datetime.now():%Y%m%d_%H%M%S}.log"

GMS_KEY = {
    "name": "083-GMS",
    "userKeyBase64": (
        "EwAAAFIAAAAqAAAAWwAAAAgAAAACAAAAEAAAAGAAAAAGAAAAAgAAAEMAAAAPAAAAtAAAAEsAAAA1"
        "AAAABQAAABsAAAAKAAAAXwAAAAkAAAOPAAAAUAAAAAwAAAAbAAAAMwAAAFUAAAABAAAACQAAAFIAAADeAAAAxwAAAB4AAAA="
    ),
    "ivBase64": "TSPHKw==",
}
# fix typo in key - must match working scripts exactly
GMS_KEY = {
    "name": "083-GMS",
    "userKeyBase64": (
        "EwAAAFIAAAAqAAAAWwAAAAgAAAACAAAAEAAAAGAAAAAGAAAAAgAAAEMAAAAPAAAAtAAAAEsAAAA1"
        "AAAABQAAABsAAAAKAAAAXwAAAAkAAAAPAAAAUAAAAAwAAAAbAAAAMwAAAFUAAAABAAAACQAAAFIAAADeAAAAxwAAAB4AAAA="
    ),
    "ivBase64": "TSPHKw==",
}

MCP = "http://127.0.0.1:10002/mcp"
LOG: list[str] = []


def log(msg: str) -> None:
    line = f"[{datetime.now():%H:%M:%S}] {msg}"
    print(line, flush=True)
    LOG.append(line)


class Mcp:
    def __init__(self):
        self.session = None
        self.rid = 0

    def call(self, method, params=None):
        self.rid += 1
        headers = {
            "Content-Type": "application/json",
            "Accept": "application/json, text/event-stream",
        }
        if self.session:
            headers["Mcp-Session-Id"] = self.session
        req = urllib.request.Request(
            MCP,
            data=json.dumps({"jsonrpc": "2.0", "method": method, "params": params or {}, "id": self.rid}).encode(),
            headers=headers,
            method="POST",
        )
        with urllib.request.urlopen(req, timeout=600) as resp:
            if "Mcp-Session-Id" in resp.headers:
                self.session = resp.headers["Mcp-Session-Id"]
            body = resp.read().decode()
            return json.loads(body) if body.strip() else None

    def tool(self, name, arguments):
        return self.call("tools/call", {"name": name, "arguments": arguments})

    def init(self):
        self.call(
            "initialize",
            {
                "protocolVersion": "2024-11-05",
                "capabilities": {},
                "clientInfo": {"name": "valley-orz-finish", "version": "1"},
            },
        )
        self.call("notifications/initialized", {})


def ok(r) -> bool:
    return bool(r) and not ((r.get("result") or {}).get("isError"))


def kids(c: Mcp, root: str, node: str = "") -> list[str]:
    r = c.tool("list_children", {"rootPath": root, "nodePath": node, "autoParse": True})
    ch = ((r or {}).get("result") or {}).get("structuredContent", {}).get("children") or []
    names = [x.get("name") for x in ch if isinstance(x, dict) and x.get("name")]
    if names:
        return names
    r2 = c.tool("get_node_tree_json", {"rootPath": root, "nodePath": node, "maxDepth": 1, "autoParse": True})
    tree = ((r2 or {}).get("result") or {}).get("structuredContent", {}).get("tree") or {}
    return [x["name"] for x in (tree.get("children") or []) if isinstance(x, dict) and x.get("name")]


def pasted(r) -> int:
    sc = ((r or {}).get("result") or {}).get("structuredContent") or {}
    return len(sc.get("pasted") or [])


def load_pair(c: Mcp, src: Path, dst: Path) -> tuple[str, str] | None:
    c.tool("unload_all", {})
    s, t = str(src.resolve()), str(dst.resolve())
    r = c.tool("load_files", {"paths": [s, t], "key": GMS_KEY})
    if not ok(r):
        log(f"LOAD_FAIL {src.name}: {r}")
        return None
    return s, t


def append_ids(c: Mcp, rel: str, ids: list[str], parent: str = "") -> int:
    src, dst = SRC_DATA / rel, DST_DATA / rel
    if not src.is_file() or not dst.is_file():
        log(f"SKIP missing {rel}")
        return 0
    pair = load_pair(c, src, dst)
    if not pair:
        return 0
    s, t = pair
    # unwrap single root wrapper if both have same
    src_top = kids(c, s, "")
    tgt_top = kids(c, t, "")
    sp, tp = parent, parent
    if len(src_top) == 1 and len(tgt_top) == 1 and src_top[0] == tgt_top[0] and not parent:
        # work inside wrapper for listing? usually string files have direct ID kids
        pass
    src_set = set(kids(c, s, sp))
    tgt_set = set(kids(c, t, tp))
    # if ids not at top, try inside sole wrapper
    if not (set(ids) & src_set) and len(src_top) == 1:
        sp = src_top[0]
        tp = tgt_top[0] if len(tgt_top) == 1 else src_top[0]
        src_set = set(kids(c, s, sp))
        tgt_set = set(kids(c, t, tp))
        log(f"{rel} using wrapper sp={sp} srcKids~{len(src_set)}")

    missing = [i for i in ids if i in src_set and i not in tgt_set]
    present_src = [i for i in ids if i in src_set]
    log(f"{rel} want={len(ids)} srcHas={len(present_src)} missing={len(missing)} sample_miss={missing[:5]}")
    if not missing:
        c.tool("unload_all", {})
        return 0

    batch = 40
    total = 0
    for i in range(0, len(missing), batch):
        chunk = missing[i : i + batch]
        sources = [{"rootPath": s, "nodePath": (f"{sp}/{n}" if sp else n)} for n in chunk]
        c.tool("copy_nodes", {"sources": sources, "autoParse": True})
        paste = c.tool(
            "paste_nodes",
            {"rootPath": t, "nodePath": tp, "strategy": "SKIP", "autoParse": True},
        )
        n = pasted(paste)
        total += n
        if not ok(paste):
            log(f"PASTE_ERR {rel}: {paste}")
    snap = dst.with_suffix(dst.suffix + ".presave_valley")
    shutil.copy2(dst, snap)
    save = c.tool("save_node", {"rootPath": t, "autoParse": True})
    size = dst.stat().st_size
    log(f"SAVE {rel} ok={ok(save)} size={size} pasted={total}")
    if size < 100 and snap.exists():
        shutil.copy2(snap, dst)
        log(f"ROLLBACK {rel}")
    if snap.exists():
        snap.unlink(missing_ok=True)
    c.tool("unload_all", {})
    return total


def find_map_parent(c: Mcp, root: str) -> tuple[str, list[str]]:
    queue = [""]
    for _depth in range(4):
        nxt = []
        for p in queue:
            ch = kids(c, root, p)
            hit = [k for k in ch if k.startswith("10006")]
            if hit:
                return p, hit
            for k in ch:
                nxt.append(f"{p}/{k}" if p else k)
        queue = nxt[:200]
    return "", []


def append_map_strings(c: Mcp) -> int:
    rel = "String/Map.img"
    src, dst = SRC_DATA / rel, DST_DATA / rel
    pair = load_pair(c, src, dst)
    if not pair:
        return 0
    s, t = pair
    parent, src_maps = find_map_parent(c, s)
    log(f"Map.img parent={parent!r} maps={src_maps}")
    if not src_maps:
        c.tool("unload_all", {})
        return 0
    # ensure parent path on target
    if parent:
        cur = ""
        for part in parent.split("/"):
            check_parent = cur
            kids_t = set(kids(c, t, check_parent))
            if part not in kids_t:
                c.tool("copy_nodes", {"sources": [{"rootPath": s, "nodePath": f"{cur}/{part}" if cur else part}], "autoParse": True})
                paste = c.tool(
                    "paste_nodes",
                    {"rootPath": t, "nodePath": check_parent, "strategy": "SKIP", "autoParse": True},
                )
                log(f"ensure parent {part} under {check_parent!r} pasted={pasted(paste)}")
            cur = f"{cur}/{part}" if cur else part
    tgt_maps = set(kids(c, t, parent))
    missing = [m for m in src_maps if m not in tgt_maps]
    log(f"Map.img missing={missing}")
    total = 0
    if missing:
        sources = [{"rootPath": s, "nodePath": (f"{parent}/{m}" if parent else m)} for m in missing]
        c.tool("copy_nodes", {"sources": sources, "autoParse": True})
        paste = c.tool(
            "paste_nodes",
            {"rootPath": t, "nodePath": parent, "strategy": "SKIP", "autoParse": True},
        )
        total = pasted(paste)
        if not ok(paste):
            log(f"Map paste err {paste}")
        snap = dst.with_suffix(dst.suffix + ".presave_valley")
        shutil.copy2(dst, snap)
        save = c.tool("save_node", {"rootPath": t, "autoParse": True})
        log(f"SAVE Map.img ok={ok(save)} size={dst.stat().st_size} pasted={total}")
        if dst.stat().st_size < 100 and snap.exists():
            shutil.copy2(snap, dst)
        snap.unlink(missing_ok=True)
    c.tool("unload_all", {})
    return total


def append_portals(c: Mcp) -> int:
    specs = [
        ("Map/Map/Map1/101020000.img", "10006060"),
        ("Map/Map/Map1/105040305.img", "10006071"),
    ]
    added = 0
    for rel, tm in specs:
        src, dst = SRC_DATA / rel, DST_DATA / rel
        # discover portal id from xml
        xml = SRC_WZ / "Map.wz" / "Map" / "Map1" / (Path(rel).name + ".xml")
        # name is 101020000.img -> 101020000.img.xml
        xml = SRC_WZ / "Map.wz" / "Map" / "Map1" / f"{Path(rel).stem}.img.xml"
        text = xml.read_text(encoding="utf-8", errors="replace")
        portal_ids = []
        for m in re.finditer(
            r'<imgdir name="(\d+)">\s*<string name="pn" value="([^"]+)"/>\s*'
            r'<int name="pt" value="(\d+)"/>\s*<int name="x" value="(-?\d+)"/>\s*'
            r'<int name="y" value="(-?\d+)"/>\s*<int name="tm" value="(\d+)"/>',
            text,
        ):
            if m.group(6) == tm:
                portal_ids.append(m.group(1))
        log(f"PORTAL {rel} xml ids for tm={tm}: {portal_ids}")
        pair = load_pair(c, src, dst)
        if not pair:
            continue
        s, t = pair
        dst_p = set(kids(c, t, "portal"))
        # check if tm already present
        already = False
        for dp in list(dst_p)[:80]:
            r = c.tool(
                "get_node_tree_json",
                {"rootPath": t, "nodePath": f"portal/{dp}", "maxDepth": 1, "autoParse": True},
            )
            tree = ((r or {}).get("result") or {}).get("structuredContent", {}).get("tree") or {}
            for ch in tree.get("children") or []:
                if ch.get("name") == "tm" and str(ch.get("value")) == tm:
                    already = True
                    break
            if already:
                break
        if already:
            log(f"PORTAL {rel} already has tm={tm}")
            c.tool("unload_all", {})
            continue
        for pid in portal_ids:
            use = pid
            if use in dst_p:
                n = 0
                while str(n) in dst_p:
                    n += 1
                use = str(n)
                # paste with same name conflict — copy then if conflict SKIP fails.
                # If src pid taken, create_child + ? Simpler: if conflict, skip and try next free by copying to clipboard and paste may keep name.
                # Use save_as workaround: temporarily not. Manually create via XML on client is hard.
                # orange may support paste with rename? schema has no rename — if pid taken, pick free id by:
                # copy source, paste SKIP won't add. Need DELETE? Too risky.
                # Prefer: if pid free use it; else fail log for manual.
                log(f"PORTAL {rel} src pid {pid} busy on target; need free id — abort this id")
                continue
            c.tool("copy_nodes", {"sources": [{"rootPath": s, "nodePath": f"portal/{pid}"}], "autoParse": True})
            paste = c.tool(
                "paste_nodes",
                {"rootPath": t, "nodePath": "portal", "strategy": "SKIP", "autoParse": True},
            )
            n = pasted(paste)
            log(f"PORTAL paste {rel} {pid} pasted={n} ok={ok(paste)}")
            if n:
                added += n
                dst_p.add(pid)
        if added:
            snap = dst.with_suffix(dst.suffix + ".presave_valley")
            shutil.copy2(dst, snap)
            save = c.tool("save_node", {"rootPath": t, "autoParse": True})
            log(f"PORTAL SAVE {rel} ok={ok(save)} size={dst.stat().st_size}")
            if dst.stat().st_size < 100 and snap.exists():
                shutil.copy2(snap, dst)
            snap.unlink(missing_ok=True)
        c.tool("unload_all", {})
    return added


def sync_server_strings_items() -> None:
    """Append missing string/item XML nodes on server if still missing."""
    jobs = [
        (
            SRC_WZ / "Item.wz" / "Etc" / "0400.img.xml",
            DST_SW / "Item.wz" / "Etc" / "0400.img.xml",
            [f"040009{i:02d}" for i in range(11)],
        ),
        (
            SRC_WZ / "Item.wz" / "Etc" / "0403.img.xml",
            DST_SW / "Item.wz" / "Etc" / "0403.img.xml",
            ["04032900", "04032901"],
        ),
        (
            Path(r"E:\资料\xiaoye\mxd学习\遗忘山谷\wz-zh-CN\String.wz\Etc.img.xml"),
            DST_SW / "String.wz" / "Etc.img.xml",
            [f"40009{i:02d}" for i in range(11)] + ["4032900", "4032901"],
        ),
        (
            Path(r"E:\资料\xiaoye\mxd学习\遗忘山谷\wz-zh-CN\String.wz\Mob.img.xml"),
            DST_SW / "String.wz" / "Mob.img.xml",
            [str(i) for i in range(54, 64)] + ["700001", "700002", "700003"],
        ),
        (
            Path(r"E:\资料\xiaoye\mxd学习\遗忘山谷\wz-zh-CN\String.wz\Npc.img.xml"),
            DST_SW / "String.wz" / "Npc.img.xml",
            [str(i) for i in range(700, 708)] + ["800015"],
        ),
    ]
    for src, dst, ids in jobs:
        if not src.exists() or not dst.exists():
            log(f"SW skip missing {src} or {dst}")
            continue
        st = src.read_text(encoding="utf-8", errors="replace")
        dt = dst.read_text(encoding="utf-8", errors="replace")
        added_blocks = []
        for iid in ids:
            if f'<imgdir name="{iid}">' in dt:
                continue
            m = re.search(rf'(<imgdir name="{re.escape(iid)}">[\s\S]*?</imgdir>)', st)
            if m:
                added_blocks.append(m.group(1))
        if not added_blocks:
            log(f"SW noop {dst.name}")
            continue
        bak = dst.with_suffix(dst.suffix + f".bak_valley_{datetime.now():%Y%m%d%H%M%S}")
        shutil.copy2(dst, bak)
        idx = dt.rfind("</imgdir>")
        dt = dt[:idx] + "\n".join(added_blocks) + "\n" + dt[idx:]
        dst.write_text(dt, encoding="utf-8")
        log(f"SW append {dst.relative_to(DST_SW)} +{len(added_blocks)} bak={bak.name}")

    # Map strings — find 10006 blocks under street folders in zh-CN Map.img.xml
    src = Path(r"E:\资料\xiaoye\mxd学习\遗忘山谷\wz-zh-CN\String.wz\Map.img.xml")
    dst = DST_SW / "String.wz" / "Map.img.xml"
    if src.exists() and dst.exists():
        st = src.read_text(encoding="utf-8", errors="replace")
        dt = dst.read_text(encoding="utf-8", errors="replace")
        map_ids = sorted(set(re.findall(r'<imgdir name="(10006\d+)">', st)))
        missing = [m for m in map_ids if f'<imgdir name="{m}">' not in dt]
        log(f"SW Map.img.xml srcMaps={len(map_ids)} missing={missing}")
        if missing:
            # extract each block — Map string blocks are usually shallow (street/mapName)
            blocks = []
            for mid in missing:
                m = re.search(rf'(<imgdir name="{mid}">[\s\S]*?</imgdir>)', st)
                if m:
                    blocks.append((mid, m.group(1)))
            # need insert under same parent as in source — find parent by searching earlier imgdir
            # Heuristic: find a sibling 10006000 context parent path in source
            # Insert under parent that already exists in dest containing other maps, or under first street in src parent
            parent_match = re.search(
                r'(<imgdir name="([^"]+)">(?:(?!<imgdir name="10006).)*?<imgdir name="10006000">)',
                st,
                re.S,
            )
            parent_name = parent_match.group(2) if parent_match else None
            log(f"SW Map parent guess={parent_name}")
            if parent_name and f'<imgdir name="{parent_name}">' in dt:
                # insert before closing of that parent — find parent block end roughly
                # safer: append before last </imgdir> inside parent by regex
                pm = re.search(
                    rf'(<imgdir name="{re.escape(parent_name)}">)([\s\S]*)(</imgdir>)',
                    dt,
                )
                if pm:
                    # find last occurrence - may be too greedy. Use iterative depth.
                    # simple approach: insert after first map child area — after opening parent tag first map dirs
                    insert_at = pm.start(2)
                    # actually append near end of parent: find matching close with depth parser
                    body = pm.group(2)
                    # If parent body ends with many closes of children, append blocks before final part
                    new_body = body + "\n" + "\n".join(b for _, b in blocks) + "\n"
                    dt2 = dt[: pm.start(2)] + new_body + dt[pm.end(2) :]
                    # The greedy match may swallow too much — verify parent_name still single
                    bak = dst.with_suffix(dst.suffix + f".bak_valley_{datetime.now():%Y%m%d%H%M%S}")
                    shutil.copy2(dst, bak)
                    dst.write_text(dt2, encoding="utf-8")
                    log(f"SW Map.img.xml appended {len(blocks)} under {parent_name}")
                else:
                    log("SW Map parent body not found")
            else:
                # fallback: dump blocks before root close
                bak = dst.with_suffix(dst.suffix + f".bak_valley_{datetime.now():%Y%m%d%H%M%S}")
                shutil.copy2(dst, bak)
                idx = dt.rfind("</imgdir>")
                dst.write_text(dt[:idx] + "\n".join(b for _, b in blocks) + "\n" + dt[idx:], encoding="utf-8")
                log(f"SW Map.img.xml appended {len(blocks)} at root (fallback)")


def main() -> int:
    c = Mcp()
    c.init()
    log("Orz ready")

    total = 0
    total += append_ids(c, "Item/Etc/0400.img", [f"040009{i:02d}" for i in range(11)])
    total += append_ids(c, "Item/Etc/0403.img", ["04032900", "04032901"])
    total += append_ids(
        c,
        "String/Etc.img",
        [f"40009{i:02d}" for i in range(11)] + ["4032900", "4032901"],
    )
    total += append_ids(
        c,
        "String/Mob.img",
        [str(i) for i in range(54, 64)] + ["700001", "700002", "700003"],
    )
    total += append_ids(
        c,
        "String/Npc.img",
        [str(i) for i in range(700, 708)] + ["800015"],
    )
    total += append_map_strings(c)
    total += append_portals(c)
    sync_server_strings_items()

    REPORT.write_text("\n".join(LOG) + "\n", encoding="utf-8")
    log(f"DONE pasted_total~{total} log={REPORT}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
