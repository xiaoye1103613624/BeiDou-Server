# -*- coding: utf-8 -*-
import json, urllib.request
from pathlib import Path

GMS_KEY = {
    "name": "083-GMS",
    "userKeyBase64": "EwAAAFIAAAAqAAAAWwAAAAgAAAACAAAAEAAAAGAAAAAGAAAAAgAAAEMAAAAPAAAAtAAAAEsAAAA1AAAABQAAABsAAAAKAAAAXwAAAAkAAAAPAAAAUAAAAAwAAAAbAAAAMwAAAFUAAAABAAAACQAAAFIAAADeAAAAxwAAAB4AAAA=",
    "ivBase64": "TSPHKw==",
}
MCP = "http://127.0.0.1:10002/mcp"
SRC = str(Path(r"E:\资料\xiaoye\mxd学习\遗忘山谷\Data\String\Map.img").resolve())
DST = str(Path(r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data\String\Map.img").resolve())

session = None
rid = 0

def call(method, params=None):
    global session, rid
    rid += 1
    headers = {"Content-Type": "application/json", "Accept": "application/json, text/event-stream"}
    if session:
        headers["Mcp-Session-Id"] = session
    req = urllib.request.Request(MCP, data=json.dumps({"jsonrpc":"2.0","method":method,"params":params or {},"id":rid}).encode(), headers=headers, method="POST")
    with urllib.request.urlopen(req, timeout=120) as resp:
        if "Mcp-Session-Id" in resp.headers:
            session = resp.headers["Mcp-Session-Id"]
        return json.loads(resp.read().decode() or "null")

def tool(name, args):
    return call("tools/call", {"name": name, "arguments": args})

def kids(root, node=""):
    r = tool("list_children", {"rootPath": root, "nodePath": node, "autoParse": True})
    ch = ((r or {}).get("result") or {}).get("structuredContent", {}).get("children") or []
    return [x.get("name") for x in ch if isinstance(x, dict) and x.get("name")]

call("initialize", {"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"c","version":"1"}})
call("notifications/initialized", {})
tool("unload_all", {})
tool("load_files", {"paths": [SRC, DST], "key": GMS_KEY})

found_src, found_dst = [], []
def walk(root, node, depth, bag):
    if depth > 3 or len(bag) > 30:
        return
    for k in kids(root, node):
        path = f"{node}/{k}" if node else k
        if k.startswith("010006"):
            bag.append(path)
        if depth < 3:
            walk(root, path, depth+1, bag)

walk(SRC, "", 0, found_src)
walk(DST, "", 0, found_dst)
print("SRC", found_src)
print("DST", found_dst)
print("missing", sorted(set(x.split("/")[-1] for x in found_src) - set(x.split("/")[-1] for x in found_dst)))
