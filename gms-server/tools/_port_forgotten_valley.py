# -*- coding: utf-8 -*-
"""Safe append-only port: 遗忘山谷 -> BeiDou-Client_1 (+ wz-zh-CN maps/mobs/npcs)."""
from __future__ import annotations

import json
import re
import shutil
import sys
import time
import urllib.request
from datetime import datetime
from pathlib import Path

sys.stdout.reconfigure(encoding="utf-8", errors="replace")

SRC = Path(r"E:\资料\xiaoye\mxd学习\遗忘山谷")
SRC_DATA = SRC / "Data"
SRC_WZ = SRC / "wz"
DST_DATA = Path(r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data")
DST_SW = Path(r"E:\pro\BeiDou-Server_xy\gms-server\wz-zh-CN")
REPORT_DIR = Path(r"E:\pro\BeiDou-Server_xy\.eval_append")
MCP = "http://127.0.0.1:10002/mcp"

GMS_KEY = {
    "name": "083-GMS",
    "userKeyBase64": (
        "EwAAAFIAAAAqAAAAWwAAAAgAAAACAAAAEAAAAGAAAAAGAAAAAgAAAEMAAAAPAAAAtAAAAEsAAAA1"
        "AAAABQAAABsAAAAKAAAAXwAAAAkAAAAPAAAAUAAAAAwAAAAbAAAAMwAAAFUAAAABAAAACQAAAFIAAADeAAAAxwAAAB4AAAA="
    ),
    "ivBase64": "TSPHKw==",
}

# Only Obj packs referenced by valley Map0 (from XML oS scan)
OBJ_NEEDED = [
    "obj_acc2",
    "obj_acc3",
    "obj_connect",
    "obj_dungeon",
    "obj_guide",
    "obj_houseGC",
    "obj_insideGC",
    "obj_mask",
    "obj_trap",
]

SKIP_WHOLE = {
    "Map/Effect.img",
    "Map/MapHelper.img",
    "Map/Physics.img",
    "Map/WorldMap/SearchExcept.img",
    "Map/WorldMap/SearchExceptForNPC.img",
    "Map/Map/Map1/101020000.img",  # portal-only merge
    "Map/Map/Map1/105040305.img",
    "Sound/Bgm03.img",
    "Sound/Mob.img",
    "Quest/Act.img",
    "Quest/Check.img",
    "Quest/QuestInfo.img",
    "Quest/Say.img",
    "String/Etc.img",
    "String/Map.img",
    "String/Mob.img",
    "String/Npc.img",
    "Item/Etc/0400.img",
    "Item/Etc/0403.img",
    "Map/Obj/cash_preview.img",
}

STRING_NODE_SPECS = [
    # (rel, list of node ids to append if missing)
    ("String/Etc.img", [f"40009{i:02d}" for i in range(11)] + ["4032900", "4032901"]),
    ("String/Mob.img", [str(i) for i in range(54, 64)] + ["700001", "700002", "700003"]),
    ("String/Npc.img", [str(i) for i in range(700, 708)] + ["800015"]),
]

ITEM_NODE_SPECS = [
    ("Item/Etc/0400.img", [f"040009{i:02d}" for i in range(11)]),
    ("Item/Etc/0403.img", ["04032900", "04032901"]),
]

MAP_STRING_IDS = [
    "10006000",
    "10006001",
    "10006002",
    "10006010",
    "10006020",
    "10006030",
    "10006031",
    "10006040",
    "10006050",
    "10006060",
    "10006070",
    "10006071",
    "10006080",
    "10006090",
    "10006100",
    "10006110",
    "10006120",
    "10006121",
    "10006152",
]


class McpClient:
    def __init__(self, timeout: float = 600):
        self.url = MCP
        self.session = None
        self.rid = 0
        self.timeout = timeout

    def call(self, method, params=None):
        self.rid += 1
        payload = {"jsonrpc": "2.0", "method": method, "params": params or {}, "id": self.rid}
        headers = {
            "Content-Type": "application/json",
            "Accept": "application/json, text/event-stream",
        }
        if self.session:
            headers["Mcp-Session-Id"] = self.session
        req = urllib.request.Request(
            self.url, data=json.dumps(payload).encode(), headers=headers, method="POST"
        )
        with urllib.request.urlopen(req, timeout=self.timeout) as resp:
            if "Mcp-Session-Id" in resp.headers:
                self.session = resp.headers["Mcp-Session-Id"]
            body = resp.read().decode()
            return None if not body.strip() else json.loads(body)

    def tool(self, name: str, arguments: dict):
        return self.call("tools/call", {"name": name, "arguments": arguments})

    def init(self):
        self.call(
            "initialize",
            {
                "protocolVersion": "2024-11-05",
                "capabilities": {},
                "clientInfo": {"name": "port-forgotten-valley", "version": "1.0"},
            },
        )
        self.call("notifications/initialized", {})
        # ensure GMS key present (best-effort)
        try:
            self.tool("add_or_update_wz_key", GMS_KEY)
        except Exception:
            pass


def tool_ok(r) -> bool:
    if not r:
        return False
    res = r.get("result", {})
    return not res.get("isError")


def child_names(c: McpClient, root: str, node: str = "") -> list[str]:
    r = c.tool("list_children", {"rootPath": root, "nodePath": node, "autoParse": True})
    children = ((r or {}).get("result", {}) or {}).get("structuredContent", {}).get("children") or []
    names = [ch.get("name") for ch in children if isinstance(ch, dict) and ch.get("name")]
    if names:
        return names
    r2 = c.tool("get_node_tree_json", {"rootPath": root, "nodePath": node, "maxDepth": 1, "autoParse": True})
    tree = ((r2 or {}).get("result", {}) or {}).get("structuredContent", {}).get("tree") or {}
    return [x["name"] for x in (tree.get("children") or []) if isinstance(x, dict) and x.get("name")]


def copy_if_missing(rel: str, stats: dict, log) -> None:
    rel_n = rel.replace("\\", "/")
    if rel_n in SKIP_WHOLE:
        stats["skipped_policy"] += 1
        log(f"SKIP_POLICY {rel_n}")
        return
    src = SRC_DATA / rel_n
    dst = DST_DATA / rel_n
    if not src.is_file():
        stats["missing_src"] += 1
        log(f"MISS_SRC {rel_n}")
        return
    if dst.exists():
        stats["skipped_exists"] += 1
        log(f"SKIP_EXISTS {rel_n}")
        return
    dst.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(src, dst)
    stats["copied"] += 1
    log(f"COPY {rel_n} size={src.stat().st_size}")


def paste_missing_nodes(
    c: McpClient, rel: str, node_ids: list[str], stats: dict, log, parent_prefix: str = ""
) -> None:
    """Append missing top-level (or parent_prefix) children from SRC to DST via Orz SKIP paste."""
    rel_n = rel.replace("\\", "/")
    src = SRC_DATA / rel_n
    dst = DST_DATA / rel_n
    if not src.is_file() or not dst.is_file():
        stats["merge_skip"] += 1
        log(f"MERGE_SKIP no files {rel_n}")
        return

    src_root = str(src.resolve())
    dst_root = str(dst.resolve())
    # open both
    c.tool("load_img", {"path": src_root, "keyName": "083-GMS", "autoParse": True})
    c.tool("load_img", {"path": dst_root, "keyName": "083-GMS", "autoParse": True})

    parent = parent_prefix
    existing = set(child_names(c, dst_root, parent))
    src_kids = set(child_names(c, src_root, parent))
    want = [n for n in node_ids if n in src_kids and n not in existing]
    if not want:
        stats["merge_noop"] += 1
        log(f"MERGE_NOOP {rel_n} already has all {len(node_ids)} nodes (srcHas={len(src_kids & set(node_ids))})")
        return

    # paste in batches
    batch = 50
    added = 0
    for i in range(0, len(want), batch):
        chunk = want[i : i + batch]
        paths = [f"{parent}/{n}" if parent else n for n in chunk]
        # normalize paths (no leading slash)
        paths = [p.lstrip("/") for p in paths]
        r = c.tool(
            "paste_nodes",
            {
                "sourceRootPath": src_root,
                "targetRootPath": dst_root,
                "nodePaths": paths,
                "targetParentPath": parent,
                "conflictPolicy": "SKIP",
                "autoParse": True,
            },
        )
        if not tool_ok(r):
            log(f"MERGE_ERR {rel_n} chunk={chunk[:3]}... {r}")
            stats["merge_err"] += 1
            continue
        sc = ((r or {}).get("result", {}) or {}).get("structuredContent") or {}
        pasted = sc.get("pasted") or []
        added += len(pasted)
        log(f"MERGE {rel_n} pasted={len(pasted)} sample={chunk[:5]}")

    if added:
        rsave = c.tool("save_node", {"rootPath": dst_root, "keyName": "083-GMS"})
        ok = tool_ok(rsave)
        size = dst.stat().st_size if dst.exists() else 0
        log(f"SAVE {rel_n} ok={ok} size={size} added={added}")
        if ok and size >= 100:
            stats["merged_nodes"] += added
        else:
            stats["merge_err"] += 1
    else:
        stats["merge_noop"] += 1


def detect_map_string_parent(c: McpClient, root: str) -> str:
    """Map.img often has street/region folders; find parent that holds 10006*."""
    kids = child_names(c, root, "")
    if any(k.startswith("10006") for k in kids):
        return ""
    # common GMS: maple / etc nesting — scan 1 level
    for k in kids:
        sub = child_names(c, root, k)
        if any(x.startswith("10006") for x in sub):
            return k
        for k2 in sub:
            sub2 = child_names(c, root, f"{k}/{k2}")
            if any(x.startswith("10006") for x in sub2):
                return f"{k}/{k2}"
    # fallback: search src for first 10006000 location by trying common paths
    for cand in ["maple", "禁区", "神秘河", "维托拉", ""]:
        if cand == "":
            continue
        if cand in kids:
            sub = child_names(c, root, cand)
            if any(x.startswith("10006") for x in sub):
                return cand
    return ""


def paste_map_strings(c: McpClient, stats: dict, log) -> None:
    rel = "String/Map.img"
    src = SRC_DATA / rel
    dst = DST_DATA / rel
    if not src.is_file() or not dst.is_file():
        log("MAPSTR skip missing files")
        return
    src_root = str(src.resolve())
    dst_root = str(dst.resolve())
    c.tool("load_img", {"path": src_root, "keyName": "083-GMS", "autoParse": True})
    c.tool("load_img", {"path": dst_root, "keyName": "083-GMS", "autoParse": True})

    # locate in source where 10006000 lives
    def find_parent(root: str) -> tuple[str, list[str]]:
        # BFS depth<=3
        queue = [""]
        for _ in range(4):
            nxt = []
            for p in queue:
                kids = child_names(c, root, p)
                if any(k.startswith("10006") for k in kids):
                    return p, [k for k in kids if k.startswith("10006")]
                for k in kids:
                    nxt.append(f"{p}/{k}" if p else k)
            queue = nxt
        return "", []

    parent, src_maps = find_parent(src_root)
    if not src_maps:
        log("MAPSTR no 10006* in source String/Map.img")
        stats["merge_skip"] += 1
        return
    want = [m for m in MAP_STRING_IDS if m in src_maps or m in set(src_maps)]
    # also take all src 10006*
    want = sorted(set(src_maps) | set(want))
    existing = set(child_names(c, dst_root, parent))
    # ensure parent exists on target; if not, paste whole parent folder from source
    if parent:
        parts = parent.split("/")
        cur = ""
        for part in parts:
            check = f"{cur}/{part}" if cur else part
            kids = set(child_names(c, dst_root, cur))
            if part not in kids:
                r = c.tool(
                    "paste_nodes",
                    {
                        "sourceRootPath": src_root,
                        "targetRootPath": dst_root,
                        "nodePaths": [check],
                        "targetParentPath": cur,
                        "conflictPolicy": "SKIP",
                        "autoParse": True,
                    },
                )
                log(f"MAPSTR ensure_parent {check} ok={tool_ok(r)}")
            cur = check
        existing = set(child_names(c, dst_root, parent))

    missing = [m for m in want if m not in existing]
    if not missing:
        log(f"MAPSTR noop parent={parent} count={len(want)}")
        stats["merge_noop"] += 1
        return
    paths = [f"{parent}/{m}" if parent else m for m in missing]
    r = c.tool(
        "paste_nodes",
        {
            "sourceRootPath": src_root,
            "targetRootPath": dst_root,
            "nodePaths": paths,
            "targetParentPath": parent,
            "conflictPolicy": "SKIP",
            "autoParse": True,
        },
    )
    pasted = len((((r or {}).get("result", {}) or {}).get("structuredContent") or {}).get("pasted") or [])
    c.tool("save_node", {"rootPath": dst_root, "keyName": "083-GMS"})
    log(f"MAPSTR parent={parent} pasted={pasted}/{len(missing)} ok={tool_ok(r)}")
    stats["merged_nodes"] += pasted


def append_entrance_portals(c: McpClient, stats: dict, log) -> None:
    """Copy portal entries that tm -> 10006* from source entrance maps into target."""
    specs = [
        ("Map/Map/Map1/101020000.img", "10006060"),
        ("Map/Map/Map1/105040305.img", "10006071"),
    ]
    for rel, tm in specs:
        src = SRC_DATA / rel
        dst = DST_DATA / rel
        if not src.is_file() or not dst.is_file():
            log(f"PORTAL skip missing {rel}")
            stats["merge_skip"] += 1
            continue
        src_root = str(src.resolve())
        dst_root = str(dst.resolve())
        c.tool("load_img", {"path": src_root, "keyName": "083-GMS", "autoParse": True})
        c.tool("load_img", {"path": dst_root, "keyName": "083-GMS", "autoParse": True})
        # portal node children
        src_portals = child_names(c, src_root, "portal")
        dst_portals = set(child_names(c, dst_root, "portal"))
        # find portal indices whose tm matches via XML side-channel (already known pn=down00)
        # Use get_node_property for each portal
        to_copy = []
        for pid in src_portals:
            r = c.tool(
                "get_node_values",
                {
                    "rootPath": src_root,
                    "nodePath": f"portal/{pid}",
                    "autoParse": True,
                },
            )
            sc = ((r or {}).get("result", {}) or {}).get("structuredContent") or {}
            vals = sc.get("values") or sc.get("properties") or sc
            # try common shapes
            tm_val = None
            pn_val = None
            if isinstance(vals, dict):
                tm_val = vals.get("tm") or (vals.get("properties") or {}).get("tm")
                pn_val = vals.get("pn")
            # fallback: list children values
            if tm_val is None:
                kids = child_names(c, src_root, f"portal/{pid}")
                # read via tree json
                r2 = c.tool(
                    "get_node_tree_json",
                    {
                        "rootPath": src_root,
                        "nodePath": f"portal/{pid}",
                        "maxDepth": 1,
                        "autoParse": True,
                    },
                )
                tree = ((r2 or {}).get("result", {}) or {}).get("structuredContent", {}).get("tree") or {}
                for ch in tree.get("children") or []:
                    if ch.get("name") == "tm":
                        tm_val = str(ch.get("value", ""))
                    if ch.get("name") == "pn":
                        pn_val = str(ch.get("value", ""))
            if str(tm_val) == tm:
                to_copy.append((pid, pn_val))

        if not to_copy:
            # fallback known name down00 — still try paste by scanning XML
            xml = (SRC_WZ / "Map.wz" / "Map" / "Map1" / Path(rel).name).with_suffix(".img.xml")
            if xml.exists():
                text = xml.read_text(encoding="utf-8", errors="replace")
                # find portal imgdir id for tm
                for m in re.finditer(
                    r'<imgdir name="(\d+)">\s*<string name="pn" value="([^"]+)"/>\s*'
                    r'<int name="pt" value="(\d+)"/>\s*<int name="x" value="(-?\d+)"/>\s*'
                    r'<int name="y" value="(-?\d+)"/>\s*<int name="tm" value="(\d+)"/>',
                    text,
                ):
                    if m.group(6) == tm:
                        to_copy.append((m.group(1), m.group(2)))
            log(f"PORTAL {rel} xml_fallback to_copy={to_copy}")

        # allocate new portal ids on target
        new_ids = []
        next_id = 0
        while str(next_id) in dst_portals:
            next_id += 1
        for src_pid, pn in to_copy:
            # skip if target already has portal to same tm
            already = False
            for dp in dst_portals:
                r2 = c.tool(
                    "get_node_tree_json",
                    {
                        "rootPath": dst_root,
                        "nodePath": f"portal/{dp}",
                        "maxDepth": 1,
                        "autoParse": True,
                    },
                )
                tree = ((r2 or {}).get("result", {}) or {}).get("structuredContent", {}).get("tree") or {}
                for ch in tree.get("children") or []:
                    if ch.get("name") == "tm" and str(ch.get("value", "")) == tm:
                        already = True
                        break
                if already:
                    break
            if already:
                log(f"PORTAL {rel} already has tm={tm}")
                continue
            # paste source portal node then rename? paste_nodes keeps name — conflict with id
            # Prefer: paste to portal/ with unique name — if src pid free, use it; else create by paste+?
            use_id = src_pid if src_pid not in dst_portals else str(next_id)
            if use_id != src_pid:
                # Orz may not rename; use copy node API if available
                r = c.tool(
                    "paste_nodes",
                    {
                        "sourceRootPath": src_root,
                        "targetRootPath": dst_root,
                        "nodePaths": [f"portal/{src_pid}"],
                        "targetParentPath": "portal",
                        "conflictPolicy": "SKIP",
                        "autoParse": True,
                        "renameMap": {src_pid: use_id},
                    },
                )
            else:
                r = c.tool(
                    "paste_nodes",
                    {
                        "sourceRootPath": src_root,
                        "targetRootPath": dst_root,
                        "nodePaths": [f"portal/{src_pid}"],
                        "targetParentPath": "portal",
                        "conflictPolicy": "SKIP",
                        "autoParse": True,
                    },
                )
            ok = tool_ok(r)
            log(f"PORTAL paste {rel} src={src_pid}->{use_id} pn={pn} ok={ok}")
            if ok:
                new_ids.append(use_id)
                dst_portals.add(use_id)
                next_id = 0
                while str(next_id) in dst_portals:
                    next_id += 1
                stats["merged_nodes"] += 1
            else:
                stats["merge_err"] += 1
                log(f"PORTAL err {r}")

        if new_ids:
            rsave = c.tool("save_node", {"rootPath": dst_root, "keyName": "083-GMS"})
            log(f"PORTAL save {rel} ok={tool_ok(rsave)} size={dst.stat().st_size}")


def sync_server_xml(stats: dict, log) -> None:
    # Maps
    for f in (SRC_WZ / "Map.wz" / "Map" / "Map0").glob("010006*.img.xml"):
        # server uses 9-digit with leading zeros typically 010006000.img.xml OR 10006000?
        # BeiDou sample uses 000000000.img.xml (9 digits). Client file 010006000 -> id 10006000
        # Convert 010006000.img.xml -> 10006000.img.xml (drop one leading 0) OR keep 010006000?
        # Existing style: 000010000.img.xml for map 10000. So map 10006000 -> 010006000.img.xml
        dst = DST_SW / "Map.wz" / "Map" / "Map0" / f.name
        if dst.exists():
            stats["sw_skip"] += 1
            continue
        dst.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(f, dst)
        stats["sw_copy"] += 1
        log(f"SW_COPY Map0/{f.name}")

    # Do NOT overwrite entrance maps wholesale on server — patch portals via xml merge lightly
    for name, tm in [("101020000.img.xml", "10006060"), ("105040305.img.xml", "10006071")]:
        src = SRC_WZ / "Map.wz" / "Map" / "Map1" / name
        dst = DST_SW / "Map.wz" / "Map" / "Map1" / name
        if not src.exists() or not dst.exists():
            log(f"SW_PORTAL skip {name}")
            continue
        st = src.read_text(encoding="utf-8", errors="replace")
        dt = dst.read_text(encoding="utf-8", errors="replace")
        if f'value="{tm}"' in dt and "10006" in dt:
            # rough: if tm already present ok
            if re.search(rf'<int name="tm" value="{tm}"/>', dt):
                log(f"SW_PORTAL already {name} tm={tm}")
                stats["sw_skip"] += 1
                continue
        # extract portal imgdir block(s) with tm
        blocks = []
        for m in re.finditer(r'<imgdir name="(\d+)">([\s\S]*?)</imgdir>', st):
            body = m.group(2)
            if f'<int name="tm" value="{tm}"/>' in body and "<string name=\"pn\"" in body:
                # only direct portal children pattern (pn early)
                if body.strip().startswith("<string name=\"pn\"") or "<string name=\"pn\"" in body[:200]:
                    blocks.append(f'<imgdir name="{m.group(1)}">{body}</imgdir>')
        if not blocks:
            log(f"SW_PORTAL no block {name}")
            continue
        # pick unique new ids
        used = set(re.findall(r'<imgdir name="(\d+)">', dt.split("<imgdir name=\"portal\">")[-1][:50000] if "portal" in dt else ""))
        # simpler: insert before </imgdir> of portal section
        portal_m = re.search(r'(<imgdir name="portal">)([\s\S]*?)(</imgdir>\s*(?:<imgdir name="|</imgdir>\s*$))', dt)
        if not portal_m:
            # try find portal and last closing
            idx = dt.find('<imgdir name="portal">')
            if idx < 0:
                log(f"SW_PORTAL no portal dir {name}")
                continue
            # find matching close at depth — fallback append before final root close
            log(f"SW_PORTAL complex structure, skip auto for {name} — manual check")
            stats["sw_skip"] += 1
            continue
        insert = []
        next_id = 0
        portal_body = portal_m.group(2)
        used = set(re.findall(r'<imgdir name="(\d+)">', portal_body))
        for block in blocks:
            # renumber
            m = re.match(r'<imgdir name="(\d+)">', block)
            oid = m.group(1)
            nid = oid
            if nid in used:
                while str(next_id) in used:
                    next_id += 1
                nid = str(next_id)
                block = re.sub(r'<imgdir name="\d+">', f'<imgdir name="{nid}">', block, count=1)
            used.add(nid)
            insert.append(block)
            log(f"SW_PORTAL {name} add id={nid} tm={tm}")
        new_body = portal_body + "\n" + "\n".join(insert) + "\n"
        dt2 = dt[: portal_m.start(2)] + new_body + dt[portal_m.end(2) :]
        bak = dst.with_suffix(dst.suffix + f".bak_valley_{datetime.now():%Y%m%d%H%M%S}")
        shutil.copy2(dst, bak)
        dst.write_text(dt2, encoding="utf-8")
        stats["sw_copy"] += 1
        log(f"SW_PORTAL wrote {name} bak={bak.name}")

    # Mobs / Npcs
    for folder, pat in [("Mob.wz", "*.img.xml"), ("Npc.wz", "*.img.xml")]:
        for f in (SRC_WZ / folder).glob(pat):
            dst = DST_SW / folder / f.name
            if dst.exists():
                stats["sw_skip"] += 1
                continue
            dst.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(f, dst)
            stats["sw_copy"] += 1
            log(f"SW_COPY {folder}/{f.name}")

    # Item nodes in server XML (04000900 etc.) — append missing imgdirs only
    for fname, ids in [("0400.img.xml", [f"040009{i:02d}" for i in range(11)]), ("0403.img.xml", ["04032900", "04032901"])]:
        src = SRC_WZ / "Item.wz" / "Etc" / fname
        dst = DST_SW / "Item.wz" / "Etc" / fname
        if not src.exists() or not dst.exists():
            log(f"SW_ITEM skip {fname}")
            continue
        st = src.read_text(encoding="utf-8", errors="replace")
        dt = dst.read_text(encoding="utf-8", errors="replace")
        added = 0
        for iid in ids:
            if f'<imgdir name="{iid}">' in dt:
                continue
            m = re.search(rf'<imgdir name="{iid}">[\s\S]*?</imgdir>\s*(?=<imgdir name="|</imgdir>\s*$)', st)
            # nested depth: take until matching close at depth 1 — simpler greedy with non-greedy for shallow items
            m = re.search(rf'(<imgdir name="{iid}">[\s\S]*?</imgdir>)', st)
            if not m:
                log(f"SW_ITEM missing src node {iid}")
                continue
            block = m.group(1)
            # insert before final root close
            # root is <imgdir ... name="0400.img"> ... </imgdir>
            idx = dt.rfind("</imgdir>")
            if idx < 0:
                continue
            dt = dt[:idx] + block + "\n" + dt[idx:]
            added += 1
        if added:
            bak = dst.with_suffix(dst.suffix + f".bak_valley_{datetime.now():%Y%m%d%H%M%S}")
            shutil.copy2(dst, bak)
            dst.write_text(dt, encoding="utf-8")
            stats["sw_copy"] += 1
            log(f"SW_ITEM {fname} added={added}")
        else:
            stats["sw_skip"] += 1
            log(f"SW_ITEM {fname} noop")

    # String snippets for Etc/Mob/Npc/Map — append missing dirs if absent (zh-CN)
    string_jobs = [
        ("String.wz/Etc.img.xml", [f"40009{i:02d}" for i in range(11)] + ["4032900", "4032901"]),
        ("String.wz/Mob.img.xml", [str(i) for i in range(54, 64)] + ["700001", "700002", "700003"]),
        ("String.wz/Npc.img.xml", [str(i) for i in range(700, 708)] + ["800015"]),
    ]
    for rel, ids in string_jobs:
        src = SRC / "wz-zh-CN" / rel
        if not src.exists():
            src = SRC_WZ / rel
        dst = DST_SW / rel
        if not src.exists() or not dst.exists():
            log(f"SW_STR skip {rel}")
            continue
        st = src.read_text(encoding="utf-8", errors="replace")
        dt = dst.read_text(encoding="utf-8", errors="replace")
        added = 0
        for iid in ids:
            if f'<imgdir name="{iid}">' in dt:
                continue
            m = re.search(rf'(<imgdir name="{re.escape(iid)}">[\s\S]*?</imgdir>)', st)
            if not m:
                continue
            idx = dt.rfind("</imgdir>")
            dt = dt[:idx] + m.group(1) + "\n" + dt[idx:]
            added += 1
        if added:
            bak = dst.with_suffix(dst.suffix + f".bak_valley_{datetime.now():%Y%m%d%H%M%S}")
            shutil.copy2(DST_SW / rel, bak)
            dst.write_text(dt, encoding="utf-8")
            stats["sw_copy"] += 1
            log(f"SW_STR {rel} added={added}")
        else:
            stats["sw_skip"] += 1


def main() -> int:
    ts = datetime.now().strftime("%Y%m%d_%H%M%S")
    log_path = REPORT_DIR / f"valley_port_{ts}.log"
    report_path = REPORT_DIR / f"valley_port_{ts}.json"
    lines: list[str] = []

    def log(msg: str) -> None:
        line = f"[{datetime.now():%H:%M:%S}] {msg}"
        print(line, flush=True)
        lines.append(line)

    stats = {
        "copied": 0,
        "skipped_exists": 0,
        "skipped_policy": 0,
        "missing_src": 0,
        "merged_nodes": 0,
        "merge_noop": 0,
        "merge_err": 0,
        "merge_skip": 0,
        "sw_copy": 0,
        "sw_skip": 0,
    }

    log(f"SOURCE={SRC}")
    log(f"TARGET={DST_DATA}")

    # 1) whole-file append lists
    wholes: list[str] = []
    # maps
    for f in (SRC_DATA / "Map" / "Map" / "Map0").glob("010006*.img"):
        wholes.append(f"Map/Map/Map0/{f.name}")
    # back/tile
    for f in (SRC_DATA / "Map" / "Back").glob("*.img"):
        wholes.append(f"Map/Back/{f.name}")
    for f in (SRC_DATA / "Map" / "Tile").glob("*.img"):
        wholes.append(f"Map/Tile/{f.name}")
    # obj + canvas for needed only
    for name in OBJ_NEEDED:
        wholes.append(f"Map/Obj/{name}.img")
        canvas = SRC_DATA / "Map" / "Obj" / "_Canvas" / f"{name}.img"
        if canvas.exists():
            wholes.append(f"Map/Obj/_Canvas/{name}.img")
    # mobs / npcs
    for f in (SRC_DATA / "Mob").glob("*.img"):
        wholes.append(f"Mob/{f.name}")
    for f in (SRC_DATA / "Npc").glob("*.img"):
        wholes.append(f"Npc/{f.name}")

    for rel in wholes:
        copy_if_missing(rel, stats, log)

    # 2) Orz node merges
    c = McpClient()
    try:
        c.init()
        log("Orz MCP ready")
    except Exception as e:
        log(f"Orz init failed: {e}")
        c = None

    if c:
        # probe load one map to verify encryption
        probe = DST_DATA / "Map" / "Map" / "Map0" / "010006000.img"
        if probe.exists():
            r = c.tool("load_img", {"path": str(probe.resolve()), "keyName": "083-GMS", "autoParse": True})
            log(f"PROBE load 010006000 ok={tool_ok(r)}")

        for rel, ids in ITEM_NODE_SPECS:
            paste_missing_nodes(c, rel, ids, stats, log)
        for rel, ids in STRING_NODE_SPECS:
            paste_missing_nodes(c, rel, ids, stats, log)
        paste_map_strings(c, stats, log)
        append_entrance_portals(c, stats, log)

    # 3) server xml
    sync_server_xml(stats, log)

    report = {
        "ts": ts,
        "stats": stats,
        "maps": sorted(
            p.stem.lstrip("0") or "0"
            for p in (SRC_DATA / "Map" / "Map" / "Map0").glob("010006*.img")
        ),
        "warp_hint": ["10006000", "10006060", "10006071"],
        "entrances": {"101020000": "10006060", "105040305": "10006071"},
        "log": str(log_path),
    }
    REPORT_DIR.mkdir(parents=True, exist_ok=True)
    log_path.write_text("\n".join(lines) + "\n", encoding="utf-8")
    report_path.write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8")
    log(f"DONE stats={stats}")
    log(f"report={report_path}")
    return 0 if stats["merge_err"] == 0 else 2


if __name__ == "__main__":
    raise SystemExit(main())
