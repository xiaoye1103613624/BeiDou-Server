# -*- coding: utf-8 -*-
"""Finalize valley Map strings + entrance portals on Client_1; sync server Map string XML."""
from __future__ import annotations

import json
import re
import shutil
import urllib.request
from datetime import datetime
from pathlib import Path

SRC_DATA = Path(r"E:\资料\xiaoye\mxd学习\遗忘山谷\Data")
SRC_ZH = Path(r"E:\资料\xiaoye\mxd学习\遗忘山谷\wz-zh-CN")
DST_DATA = Path(r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data")
DST_SW = Path(r"E:\pro\BeiDou-Server_xy\gms-server\wz-zh-CN")
LOGP = Path(r"E:\pro\BeiDou-Server_xy\.eval_append") / f"valley_finalize_{datetime.now():%Y%m%d_%H%M%S}.log"

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


def ok(r) -> bool:
    return bool(r) and not ((r.get("result") or {}).get("isError"))


def kids(c: Mcp, root: str, node: str = "") -> list[str]:
    r = c.tool("list_children", {"rootPath": root, "nodePath": node, "autoParse": True})
    ch = ((r or {}).get("result") or {}).get("structuredContent", {}).get("children") or []
    return [x.get("name") for x in ch if isinstance(x, dict) and x.get("name")]


def pasted(r) -> int:
    return len((((r or {}).get("result") or {}).get("structuredContent") or {}).get("pasted") or [])


def tm_of(c: Mcp, root: str, portal_id: str) -> dict:
    r = c.tool(
        "get_node_tree_json",
        {"rootPath": root, "nodePath": f"portal/{portal_id}", "maxDepth": 1, "autoParse": True},
    )
    tree = ((r or {}).get("result") or {}).get("structuredContent", {}).get("tree") or {}
    out = {}
    for ch in tree.get("children") or []:
        if ch.get("name") in ("tm", "pn", "tn", "pt", "x", "y"):
            out[ch["name"]] = ch.get("value")
    return out


def find_010006_parent(c: Mcp, root: str) -> tuple[str, list[str]]:
    for top in kids(c, root, ""):
        sub = kids(c, root, top)
        hits = [x for x in sub if x.startswith("010006")]
        if hits:
            return top, sorted(hits)
        for mid in sub:
            path = f"{top}/{mid}"
            sub2 = kids(c, root, path)
            hits = [x for x in sub2 if x.startswith("010006")]
            if hits:
                return path, sorted(hits)
    return "", []


def append_map_strings(c: Mcp) -> None:
    src = str((SRC_DATA / "String/Map.img").resolve())
    dst_p = DST_DATA / "String/Map.img"
    dst = str(dst_p.resolve())
    c.tool("unload_all", {})
    r = c.tool("load_files", {"paths": [src, dst], "key": GMS_KEY})
    log(f"load Map.img ok={ok(r)}")
    parent, maps = find_010006_parent(c, src)
    log(f"src parent={parent!r} maps={maps}")
    if not maps:
        return
    # ensure parent on target
    if parent and parent not in kids(c, dst, ""):
        # parent may be nested; if top-level missing, paste whole parent folder
        top = parent.split("/")[0]
        if top not in kids(c, dst, ""):
            c.tool("copy_nodes", {"sources": [{"rootPath": src, "nodePath": top}], "autoParse": True})
            paste = c.tool(
                "paste_nodes",
                {"rootPath": dst, "nodePath": "", "strategy": "SKIP", "autoParse": True},
            )
            log(f"paste top folder {top} n={pasted(paste)}")
            # if whole china pasted, may already include maps — recheck
    tgt_maps = set(kids(c, dst, parent)) if parent else set(kids(c, dst, ""))
    missing = [m for m in maps if m not in tgt_maps]
    log(f"missing map strings {missing}")
    if missing:
        sources = [{"rootPath": src, "nodePath": f"{parent}/{m}" if parent else m} for m in missing]
        c.tool("copy_nodes", {"sources": sources, "autoParse": True})
        paste = c.tool(
            "paste_nodes",
            {"rootPath": dst, "nodePath": parent, "strategy": "SKIP", "autoParse": True},
        )
        log(f"paste maps n={pasted(paste)} ok={ok(paste)}")
        snap = dst_p.with_suffix(dst_p.suffix + ".presave_valley")
        shutil.copy2(dst_p, snap)
        save = c.tool("save_node", {"rootPath": dst, "autoParse": True})
        log(f"SAVE Map.img ok={ok(save)} size={dst_p.stat().st_size}")
        if dst_p.stat().st_size < 100:
            shutil.copy2(snap, dst_p)
        snap.unlink(missing_ok=True)
    c.tool("unload_all", {})


def add_portal(c: Mcp, rel: str, tm: str, src_pid: str) -> None:
    src = str((SRC_DATA / rel).resolve())
    dst_p = DST_DATA / rel
    dst = str(dst_p.resolve())
    c.tool("unload_all", {})
    c.tool("load_files", {"paths": [src, dst], "key": GMS_KEY})
    for pid in kids(c, dst, "portal"):
        info = tm_of(c, dst, pid)
        if str(info.get("tm")) == tm:
            log(f"{rel} already portal {pid} -> {tm} {info}")
            c.tool("unload_all", {})
            return
    used = set(kids(c, dst, "portal"))
    free = 0
    while str(free) in used:
        free += 1
    free_s = str(free)
    log(f"{rel} create LIST_PROPERTY portal/{free_s} from src/{src_pid}")
    # try types
    created = False
    for typ in ("LIST_PROPERTY", "PROPERTY", "IMGDIR", "DIRECTORY", "DIR", "CANVAS"):
        r = c.tool(
            "create_child_node",
            {
                "rootPath": dst,
                "nodePath": "portal",
                "type": typ,
                "name": free_s,
                "autoParse": True,
            },
        )
        if ok(r):
            log(f"  created type={typ}")
            created = True
            break
        else:
            err = (((r or {}).get("result") or {}).get("content") or [{}])[0].get("text")
            log(f"  type {typ} fail: {err}")
    if not created:
        # fallback: temp img + rename strategy via paste OVERWRITE on unused?
        log(f"{rel} create failed; try clipboard rename workaround")
        # create temp img, paste portal as free_s at root, then copy to dest
        tmp = Path(r"E:\pro\BeiDou-Server_xy\.eval_append") / f"_tmp_portal_{free_s}.img"
        if tmp.exists():
            tmp.unlink()
        cr = c.tool("create_img_file", {"fileName": str(tmp), "key": GMS_KEY})
        log(f"create_img {ok(cr)} {cr}")
        if ok(cr) and tmp.exists():
            c.tool("unload_all", {})
            c.tool("load_files", {"paths": [src, str(tmp.resolve()), dst], "key": GMS_KEY})
            c.tool("copy_nodes", {"sources": [{"rootPath": src, "nodePath": f"portal/{src_pid}"}], "autoParse": True})
            # paste into temp root — will keep name src_pid
            paste = c.tool(
                "paste_nodes",
                {"rootPath": str(tmp.resolve()), "nodePath": "", "strategy": "SKIP", "autoParse": True},
            )
            log(f"temp paste n={pasted(paste)}")
            # if pasted as src_pid, we need rename — create free on dest by copying from temp after recreating?
            # Instead: on dest, OVERWRITE an unused placeholder — none.
            # Use create on temp with... 
        log("portal workaround incomplete")
        c.tool("unload_all", {})
        return

    for leaf in kids(c, src, f"portal/{src_pid}"):
        c.tool(
            "copy_nodes",
            {"sources": [{"rootPath": src, "nodePath": f"portal/{src_pid}/{leaf}"}], "autoParse": True},
        )
        paste = c.tool(
            "paste_nodes",
            {"rootPath": dst, "nodePath": f"portal/{free_s}", "strategy": "SKIP", "autoParse": True},
        )
        if not ok(paste):
            log(f"  leaf {leaf} err {paste}")
    info = tm_of(c, dst, free_s)
    log(f"  verify {info}")
    snap = dst_p.with_suffix(dst_p.suffix + ".presave_valley")
    shutil.copy2(dst_p, snap)
    save = c.tool("save_node", {"rootPath": dst, "autoParse": True})
    log(f"SAVE {rel} ok={ok(save)} size={dst_p.stat().st_size}")
    if dst_p.stat().st_size < 100:
        shutil.copy2(snap, dst_p)
        log("ROLLBACK")
    snap.unlink(missing_ok=True)
    c.tool("unload_all", {})


def sync_server_map_strings() -> None:
    src = SRC_ZH / "String.wz" / "Map.img.xml"
    dst = DST_SW / "String.wz" / "Map.img.xml"
    if not src.exists() or not dst.exists():
        log("SW Map.img.xml missing")
        return
    st = src.read_text(encoding="utf-8", errors="replace")
    dt = dst.read_text(encoding="utf-8", errors="replace")
    map_ids = sorted(set(re.findall(r'<imgdir name="(010006\d+)">', st)))
    missing = [m for m in map_ids if f'<imgdir name="{m}">' not in dt]
    log(f"SW Map strings src={len(map_ids)} missing={missing}")
    if not missing:
        return
    # detect parent folder name containing 010006000
    m = re.search(
        r'<imgdir name="([^"]+)">(?:(?!</imgdir>).)*?<imgdir name="010006000">',
        st,
        re.S,
    )
    parent = m.group(1) if m else None
    log(f"SW parent={parent}")
    blocks = []
    for mid in missing:
        bm = re.search(rf'(<imgdir name="{mid}">[\s\S]*?</imgdir>)', st)
        if bm:
            blocks.append(bm.group(1))
    if not blocks:
        return
    bak = dst.with_suffix(dst.suffix + f".bak_valley_{datetime.now():%Y%m%d%H%M%S}")
    shutil.copy2(dst, bak)
    if parent and f'<imgdir name="{parent}">' in dt:
        # insert before the closing tag of parent — find parent start and use depth counter
        start = dt.find(f'<imgdir name="{parent}">')
        if start < 0:
            parent = None
        else:
            i = start + len(f'<imgdir name="{parent}">')
            depth = 1
            while i < len(dt) and depth:
                if dt.startswith("<imgdir", i):
                    depth += 1
                    i = dt.find(">", i) + 1
                elif dt.startswith("</imgdir>", i):
                    depth -= 1
                    if depth == 0:
                        break
                    i += len("</imgdir>")
                else:
                    i += 1
            if depth == 0:
                dt = dt[:i] + "\n" + "\n".join(blocks) + "\n" + dt[i:]
                dst.write_text(dt, encoding="utf-8")
                log(f"SW Map.img.xml +{len(blocks)} under {parent} bak={bak.name}")
                return
    idx = dt.rfind("</imgdir>")
    dst.write_text(dt[:idx] + "\n".join(blocks) + "\n" + dt[idx:], encoding="utf-8")
    log(f"SW Map.img.xml +{len(blocks)} root fallback bak={bak.name}")


def main() -> int:
    c = Mcp()
    c.call(
        "initialize",
        {
            "protocolVersion": "2024-11-05",
            "capabilities": {},
            "clientInfo": {"name": "valley-finalize", "version": "1"},
        },
    )
    c.call("notifications/initialized", {})
    append_map_strings(c)
    add_portal(c, "Map/Map/Map1/101020000.img", "10006060", "0")
    add_portal(c, "Map/Map/Map1/105040305.img", "10006071", "4")
    sync_server_map_strings()
    LOGP.write_text("\n".join(LOG) + "\n", encoding="utf-8")
    log(f"DONE {LOGP}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
