# -*- coding: utf-8 -*-
from __future__ import annotations

import json
import re
import shutil
import urllib.request
from pathlib import Path

SRC_DATA = Path(r"E:\资料\xiaoye\mxd学习\遗忘山谷\Data")
SRC_WZ = Path(r"E:\资料\xiaoye\mxd学习\遗忘山谷\wz")
SRC_ZH = Path(r"E:\资料\xiaoye\mxd学习\遗忘山谷\wz-zh-CN")
DST_DATA = Path(r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data")
DST_SW = Path(r"E:\pro\BeiDou-Server_xy\gms-server\wz-zh-CN")

GMS_KEY = {
    "name": "083-GMS",
    "userKeyBase64": (
        "EwAAAFIAAAAqAAAAWwAAAAgAAAACAAAAEAAAAGAAAAAGAAAAAgAAAEMAAAAPAAAAtAAAAEsAAAA1"
        "AAAABQAAABsAAAAKAAAAXwAAAAkAAAAPAAAAUAAAAAwAAAAbAAAAMwAAAFUAAAABAAAACQAAAFIAAADeAAAAxwAAAB4AAAA="
    ),
    "ivBase64": "TSPHKw==",
}
MCP = "http://127.0.0.1:10002/mcp"


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


def kids(c, root, node=""):
    r = c.tool("list_children", {"rootPath": root, "nodePath": node, "autoParse": True})
    ch = ((r or {}).get("result") or {}).get("structuredContent", {}).get("children") or []
    return [x.get("name") for x in ch if isinstance(x, dict) and x.get("name")]


def tm_of(c, root, portal_id):
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


def main():
    # XML string map search
    for p in [SRC_WZ / "String.wz" / "Map.img.xml", SRC_ZH / "String.wz" / "Map.img.xml"]:
        t = p.read_text(encoding="utf-8", errors="replace")
        ids = sorted(set(re.findall(r'name="(10006\d+)"', t)))
        print(p, "count", len(ids), "sample", ids[:10])
        if "遗忘" in t:
            print("  has 遗忘")
        # nearby context for 10006000
        i = t.find("10006000")
        print("  idx", i, "ctx", t[max(0, i - 120) : i + 80].replace("\n", " ") if i >= 0 else None)

    c = Mcp()
    c.call(
        "initialize",
        {
            "protocolVersion": "2024-11-05",
            "capabilities": {},
            "clientInfo": {"name": "fix", "version": "1"},
        },
    )
    c.call("notifications/initialized", {})

    # inspect String/Map structure
    src = str((SRC_DATA / "String/Map.img").resolve())
    dst = str((DST_DATA / "String/Map.img").resolve())
    c.tool("unload_all", {})
    c.tool("load_files", {"paths": [src, dst], "key": GMS_KEY})
    top = kids(c, src, "")
    print("String/Map src top", top[:30], "n=", len(top))
    for name in top[:15]:
        sub = kids(c, src, name)
        hits = [x for x in sub if "10006" in x or x.startswith("1000")]
        print(f"  {name}/ kids={len(sub)} hit1000={hits[:10]}")
        if hits or any("忘" in x or "谷" in x for x in sub):
            print("   sample", sub[:20])
    # search deeper for 10006000
    found = []

    def walk(node, depth):
        if depth > 3 or len(found) > 5:
            return
        for k in kids(c, src, node):
            path = f"{node}/{k}" if node else k
            if k == "10006000" or k.startswith("10006"):
                found.append(path)
            if depth < 3:
                walk(path, depth + 1)

    walk("", 0)
    print("found paths", found)

    # portals
    for rel, tm in [
        ("Map/Map/Map1/101020000.img", "10006060"),
        ("Map/Map/Map1/105040305.img", "10006071"),
    ]:
        s = str((SRC_DATA / rel).resolve())
        t = str((DST_DATA / rel).resolve())
        c.tool("unload_all", {})
        c.tool("load_files", {"paths": [s, t], "key": GMS_KEY})
        print("===", rel)
        for side, root in [("SRC", s), ("DST", t)]:
            pids = kids(c, root, "portal")
            print(side, "portal ids", pids)
            for pid in pids:
                info = tm_of(c, root, pid)
                if str(info.get("tm")) == tm or info.get("pn") == "down00":
                    print(" ", side, pid, info)

    # If dst portal busy but wrong tm, we can update values OR create_child_node new portal
    # Try create_child_node for free id and set properties by paste of subtree?
    # Alternative: use XML merge on server already done; for client, find free id and
    # copy_nodes then... paste keeps source name. 
    # Workaround: create_img temp, paste portal into temp under free name? Too heavy.
    # Better: delete not allowed — use create_child_node imgdir + copy each leaf?
    # Simplest fix for busy id: check if DST already has ANY portal with tm=valley.
    # If not, create_child_node name=freeId type=imgdir under portal, then copy children from src portal.

    for rel, tm, prefer_id in [
        ("Map/Map/Map1/101020000.img", "10006060", "0"),
        ("Map/Map/Map1/105040305.img", "10006071", "4"),
    ]:
        s = str((SRC_DATA / rel).resolve())
        t = str((DST_DATA / rel).resolve())
        c.tool("unload_all", {})
        c.tool("load_files", {"paths": [s, t], "key": GMS_KEY})
        dst_pids = kids(c, t, "portal")
        has = False
        for pid in dst_pids:
            info = tm_of(c, t, pid)
            if str(info.get("tm")) == tm:
                has = True
                print(f"HAS {rel} portal {pid} -> {tm}")
                break
        if has:
            continue
        # allocate free id
        n = 0
        while str(n) in set(dst_pids):
            n += 1
        free = str(n)
        print(f"CREATE portal {free} on {rel} from src {prefer_id}")
        # create imgdir
        r = c.tool(
            "create_child_node",
            {"rootPath": t, "nodePath": "portal", "type": "imgdir", "name": free, "autoParse": True},
        )
        print(" create", r)
        # copy each child property from source portal
        for leaf in kids(c, s, f"portal/{prefer_id}"):
            c.tool(
                "copy_nodes",
                {"sources": [{"rootPath": s, "nodePath": f"portal/{prefer_id}/{leaf}"}], "autoParse": True},
            )
            paste = c.tool(
                "paste_nodes",
                {"rootPath": t, "nodePath": f"portal/{free}", "strategy": "SKIP", "autoParse": True},
            )
            print("  leaf", leaf, "ok", not (paste or {}).get("result", {}).get("isError"))
        snap = Path(t).with_suffix(Path(t).suffix + ".presave_valley")
        shutil.copy2(t, snap)
        save = c.tool("save_node", {"rootPath": t, "autoParse": True})
        print("SAVE", rel, save, "size", Path(t).stat().st_size)
        if Path(t).stat().st_size < 100:
            shutil.copy2(snap, t)
        snap.unlink(missing_ok=True)

    c.tool("unload_all", {})


if __name__ == "__main__":
    main()
