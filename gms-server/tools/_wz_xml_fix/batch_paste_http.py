# -*- coding: utf-8 -*-
"""Batch copy/paste via orange-wz HTTP MCP (separate session)."""
import json
import urllib.request
import sys

URL = "http://127.0.0.1:10002/mcp"
KEY = {
    "name": "083-GMS",
    "ivBase64": "TSPHKw==",
    "userKeyBase64": "EwAAAFIAAAAqAAAAWwAAAAgAAAACAAAAEAAAAGAAAAAGAAAAAgAAAEMAAAAPAAAAtAAAAEsAAAA1AAAABQAAABsAAAAKAAAAXwAAAAkAAAAPAAAAUAAAAAwAAAAbAAAAMwAAAFUAAAABAAAACQAAAFIAAADeAAAAxwAAAB4AAAA=",
}

SPLIT = r"F:\MXD_dev\BeiDou-Server\gms-server\tools\_wz_xml_fix\copy_batches\split"
MERGED = r"F:\MXD_dev\BeiDou-Server\gms-server\tools\_wz_xml_fix\merged_img"
SRC_SAY = r"F:\MXD_dev\扩展改动\妖精学院+列娜海峡\Data\Quest\Say.img"
SRC_NPC = r"F:\MXD_dev\扩展改动\妖精学院+列娜海峡\Data\String\Npc.img"
SAY_BASE = MERGED + r"\Say.base.img"
NPC_BASE = MERGED + r"\Npc.base.img"


def http(payload, session=None):
    data = json.dumps(payload).encode("utf-8")
    h = {
        "Content-Type": "application/json",
        "Accept": "application/json, text/event-stream",
    }
    if session:
        h["Mcp-Session-Id"] = session
    req = urllib.request.Request(URL, data=data, headers=h)
    with urllib.request.urlopen(req, timeout=600) as r:
        sid = r.headers.get("Mcp-Session-Id") or session
        raw = r.read().decode("utf-8")
        if not raw.strip():
            return sid, None
        if raw.startswith("event:") or raw.startswith("data:"):
            for line in raw.splitlines():
                if line.startswith("data:"):
                    return sid, json.loads(line[5:].strip())
            return sid, raw
        return sid, json.loads(raw)


def tool(sid, name, args, tid=1):
    _, res = http(
        {
            "jsonrpc": "2.0",
            "id": tid,
            "method": "tools/call",
            "params": {"name": name, "arguments": args},
        },
        session=sid,
    )
    if res and res.get("result", {}).get("isError"):
        raise RuntimeError("%s failed: %s" % (name, res))
    sc = (res or {}).get("result", {}).get("structuredContent")
    if sc is None:
        # parse text content
        content = (res or {}).get("result", {}).get("content") or []
        for c in content:
            if c.get("type") == "text":
                try:
                    return json.loads(c["text"])
                except Exception:
                    return c["text"]
    return sc if sc is not None else res


def main():
    sid, _ = http(
        {
            "jsonrpc": "2.0",
            "id": 1,
            "method": "initialize",
            "params": {
                "protocolVersion": "2025-03-26",
                "capabilities": {},
                "clientInfo": {"name": "batch-paste", "version": "1"},
            },
        }
    )
    print("session", sid)
    http({"jsonrpc": "2.0", "method": "notifications/initialized", "params": {}}, session=sid)

    # Prefer dedicated work copies so we don't fight Cursor session locks
    import shutil
    import os

    say_work = MERGED + r"\Say.work.img"
    npc_work = MERGED + r"\Npc.work.img"
    shutil.copy2(SAY_BASE, say_work)
    shutil.copy2(NPC_BASE, npc_work)

    paths = [say_work, SRC_SAY, npc_work, SRC_NPC]
    lr = tool(sid, "load_files", {"paths": paths, "key": KEY}, tid=2)
    print("load", lr)

    # Finish remaining Say pastes: all 501 (SKIP is safe; c0/c1 already on Say.base but work is fresh copy of base without those)
    # Say.base currently has c0+c1 pastes in Cursor memory only; on-disk Say.base was NOT save_node'd yet
    # so disk Say.base is still pure live copy. Good - work starts clean, paste ALL 501.

    tid = 100
    for i in range(7):
        sources = json.load(open(os.path.join(SPLIT, "Say_b%d.json" % i), encoding="utf-8"))
        for s in sources:
            s["rootPath"] = SRC_SAY
        cr = tool(sid, "copy_nodes", {"autoParse": True, "sources": sources}, tid=tid)
        tid += 1
        print("Say copy b%d" % i, cr)
        pr = tool(
            sid,
            "paste_nodes",
            {
                "autoParse": True,
                "clearClipboard": True,
                "strategy": "SKIP",
                "rootPath": say_work,
                "nodePath": "",
            },
            tid=tid,
        )
        tid += 1
        pasted = pr.get("pasted") if isinstance(pr, dict) else pr
        n = len(pasted) if isinstance(pasted, list) else pasted
        print("Say paste b%d count" % i, n)

    sr = tool(sid, "save_node", {"autoParse": True, "rootPath": say_work}, tid=tid)
    tid += 1
    print("Say save_node", sr)
    print("Say.work size", os.path.getsize(say_work))

    # Npc all batches
    for i in range(6):
        sources = json.load(open(os.path.join(SPLIT, "Npc_b%d.json" % i), encoding="utf-8"))
        for s in sources:
            s["rootPath"] = SRC_NPC
        cr = tool(sid, "copy_nodes", {"autoParse": True, "sources": sources}, tid=tid)
        tid += 1
        print("Npc copy b%d" % i, cr)
        pr = tool(
            sid,
            "paste_nodes",
            {
                "autoParse": True,
                "clearClipboard": True,
                "strategy": "SKIP",
                "rootPath": npc_work,
                "nodePath": "",
            },
            tid=tid,
        )
        tid += 1
        pasted = pr.get("pasted") if isinstance(pr, dict) else pr
        n = len(pasted) if isinstance(pasted, list) else pasted
        print("Npc paste b%d count" % i, n)

    nr = tool(sid, "save_node", {"autoParse": True, "rootPath": npc_work}, tid=tid)
    print("Npc save_node", nr)
    print("Npc.work size", os.path.getsize(npc_work))

    # verify samples
    for root, path in [
        (say_work, "8680"),
        (say_work, "1000"),
        (say_work, "32196"),
        (npc_work, "700"),
        (npc_work, "2100"),
        (npc_work, "9999800"),
    ]:
        try:
            fr = tool(sid, "find_node", {"autoParse": True, "rootPath": root, "nodePath": path}, tid=tid)
            tid += 1
            print("find", path, "OK", fr.get("node", {}).get("nodePath") if isinstance(fr, dict) else fr)
        except Exception as e:
            print("find", path, "FAIL", e)

    # child counts
    for root, label in [(say_work, "Say"), (npc_work, "Npc")]:
        tr = tool(
            sid,
            "get_node_tree_json",
            {"autoParse": True, "includePng": False, "maxDepth": 1, "rootPath": root},
            tid=tid,
        )
        tid += 1
        tree = tr.get("tree") if isinstance(tr, dict) else None
        ch = (tree or {}).get("children") or []
        print(label, "children", len(ch))

    print("DONE")


if __name__ == "__main__":
    main()
