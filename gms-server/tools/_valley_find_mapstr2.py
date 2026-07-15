# -*- coding: utf-8 -*-
import json, urllib.request, shutil
from pathlib import Path

GMS_KEY = {
    "name": "083-GMS",
    "userKeyBase64": "EwAAAFIAAAAqAAAAWwAAAAgAAAACAAAAEAAAAGAAAAAGAAAAAgAAAEMAAAAPAAAAtAAAAEsAAAA1AAAABQAAABsAAAAKAAAAXwAAAAkAAAAPAAAAUAAAAAwAAAAbAAAAMwAAAFUAAAABAAAACQAAAFIAAADeAAAAxwAAAB4AAAA=",
    "ivBase64": "TSPHKw==",
}
MCP = "http://127.0.0.1:10002/mcp"
SRC = str(Path(r"E:\资料\xiaoye\mxd学习\遗忘山谷\Data\String\Map.img").resolve())
DST = str(Path(r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data\String\Map.img").resolve())
DST_P = Path(DST)

session = None
rid = 0

def call(method, params=None):
    global session, rid
    rid += 1
    headers = {"Content-Type": "application/json", "Accept": "application/json, text/event-stream"}
    if session:
        headers["Mcp-Session-Id"] = session
    req = urllib.request.Request(MCP, data=json.dumps({"jsonrpc":"2.0","method":method,"params":params or {},"id":rid}).encode(), headers=headers, method="POST")
    with urllib.request.urlopen(req, timeout=300) as resp:
        if "Mcp-Session-Id" in resp.headers:
            session = resp.headers["Mcp-Session-Id"]
        return json.loads(resp.read().decode() or "null")

def tool(name, args):
    return call("tools/call", {"name": name, "arguments": args})

def tree(root, node="", depth=2):
    r = tool("get_node_tree_json", {"rootPath": root, "nodePath": node, "maxDepth": depth, "autoParse": True})
    return ((r or {}).get("result") or {}).get("structuredContent", {}).get("tree") or {}

def collect(node, prefix=""):
    out = []
    name = node.get("name", "")
    path = f"{prefix}/{name}" if prefix else name
    # skip root name Map.img in path building for children
    if node.get("nodePath") is not None:
        path = node.get("nodePath") or ""
    if name.startswith("010006"):
        out.append(node.get("nodePath") or name)
    for ch in node.get("children") or []:
        out.extend(collect(ch, path))
    return out

call("initialize", {"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"c","version":"1"}})
call("notifications/initialized", {})
tool("unload_all", {})
tool("load_files", {"paths": [SRC, DST], "key": GMS_KEY})
t = tree(SRC, "", 3)
print("top children", [c.get("name") for c in (t.get("children") or [])])
found = collect(t)
print("found under depth3", found)
# scan each top at depth 2 more carefully
hits = []
for ch in t.get("children") or []:
    name = ch["name"]
    t2 = tree(SRC, name, 2)
    f2 = collect(t2)
    if f2:
        print("hits in", name, f2)
        hits.extend(f2)
print("all hits", hits)

# if still empty, try get_node_detail on known path from XML parent context
# XML context near 010006000 - street under some region. Grep-like via searching all tops for 010006000 exist
for ch in t.get("children") or []:
    name = ch["name"]
    # list via detail
    r = tool("list_children", {"rootPath": SRC, "nodePath": name, "autoParse": True})
    kids = [x.get("name") for x in (((r or {}).get("result") or {}).get("structuredContent", {}).get("children") or []) if x.get("name")]
    h = [k for k in kids if k.startswith("010006")]
    if h:
        print("list_children hit", name, h)
        hits.extend([f"{name}/{k}" for k in h])

if hits:
    # append missing to dst
    dst_tree_hits = []
    td = tree(DST, "", 3)
    dst_tree_hits = collect(td)
    for ch in (td.get("children") or []):
        r = tool("list_children", {"rootPath": DST, "nodePath": ch["name"], "autoParse": True})
        kids = [x.get("name") for x in (((r or {}).get("result") or {}).get("structuredContent", {}).get("children") or []) if x.get("name")]
        dst_tree_hits.extend([f"{ch['name']}/{k}" for k in kids if k.startswith("010006")])
    print("dst hits", dst_tree_hits)
    src_ids = {h.split("/")[-1]: h for h in hits}
    dst_ids = {h.split("/")[-1] for h in dst_tree_hits}
    missing = [src_ids[i] for i in src_ids if i not in dst_ids]
    print("missing paths", missing)
    if missing:
        parent = missing[0].rsplit("/", 1)[0]
        # ensure parent
        if parent and parent not in [c.get("name") for c in (td.get("children") or [])]:
            tool("copy_nodes", {"sources": [{"rootPath": SRC, "nodePath": parent}], "autoParse": True})
            tool("paste_nodes", {"rootPath": DST, "nodePath": "", "strategy": "SKIP", "autoParse": True})
        sources = [{"rootPath": SRC, "nodePath": p} for p in missing]
        tool("copy_nodes", {"sources": sources, "autoParse": True})
        paste = tool("paste_nodes", {"rootPath": DST, "nodePath": parent, "strategy": "SKIP", "autoParse": True})
        print("paste", paste)
        shutil.copy2(DST_P, str(DST_P) + ".presave_valley")
        save = tool("save_node", {"rootPath": DST, "autoParse": True})
        print("save", save, "size", DST_P.stat().st_size)
else:
    print("NO HITS - Map string IDs may only exist in XML not in this Map.img binary pack")
