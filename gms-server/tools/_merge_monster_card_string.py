# -*- coding: utf-8 -*-
"""Merge high-version monster card String names into client Consume.img, then export to server XML."""
from __future__ import annotations

import json
import re
import shutil
import subprocess
import sys
import time
import urllib.request
from datetime import datetime
from pathlib import Path

sys.stdout.reconfigure(encoding="utf-8", errors="replace")

MCP = "http://127.0.0.1:10002/mcp"
UK = (
    "EwAAAFIAAAAqAAAAWwAAAAgAAAACAAAAEAAAAGAAAAAGAAAAAgAAAEMAAAAPAAAAtAAAAEsAAAA1"
    "AAAABQAAABsAAAAKAAAAXwAAAAkAAAAPAAAAUAAAAAwAAAAbAAAAMwAAAFUAAAABAAAACQAAAFIAAADeAAAAxwAAAB4AAAA="
)
GMS = {"name": "083-GMS", "userKeyBase64": UK, "ivBase64": "TSPHKw=="}

SRC_IMG = Path(r"E:\资料\xiaoye\mxd学习\高版本怪物卡\Consume.img")
CLIENT_IMG = Path(
    r"E:\mxd_soft\2.客户端\083\BeiDou-ClientV16.1\BeiDou-Client\Data\String\Consume.img"
)
SERVER_XML = Path(r"E:\pro\BeiDou-Server_xy\gms-server\wz-zh-CN\String.wz\Consume.img.xml")
ORANGE = Path(r"E:\pro\orange-wz\target")
TMP = Path(r"E:\pro\BeiDou-Server_xy\gms-server\tools\_monster_card_tmp")
BATCH = 50


class Mcp:
    def __init__(self, timeout=600):
        self.s = None
        self.i = 0
        self.timeout = timeout

    def call(self, method, params=None):
        self.i += 1
        payload = {"jsonrpc": "2.0", "method": method, "params": params or {}, "id": self.i}
        h = {"Content-Type": "application/json", "Accept": "application/json, text/event-stream"}
        if self.s:
            h["Mcp-Session-Id"] = self.s
        req = urllib.request.Request(MCP, data=json.dumps(payload).encode(), headers=h, method="POST")
        with urllib.request.urlopen(req, timeout=self.timeout) as r:
            sid = r.headers.get("Mcp-Session-Id")
            if sid:
                self.s = sid
            body = r.read().decode()
        if not body.strip():
            return None
        try:
            return json.loads(body)
        except json.JSONDecodeError:
            for line in body.splitlines():
                if line.startswith("data:"):
                    return json.loads(line[5:].strip())
            return {"raw": body[:300]}

    def tool(self, n, a):
        return self.call("tools/call", {"name": n, "arguments": a})

    def kids(self, root, node=""):
        r = self.tool(
            "get_node_tree_json",
            {"rootPath": root, "nodePath": node, "maxDepth": 1, "autoParse": True},
        )
        tree = ((r or {}).get("result", {}) or {}).get("structuredContent", {}).get("tree") or {}
        return [x["name"] for x in (tree.get("children") or []) if isinstance(x, dict) and x.get("name")]

    def pasted(self, r):
        return len((((r or {}).get("result") or {}).get("structuredContent") or {}).get("pasted") or [])


def img2xml(src: Path, dst: Path) -> None:
    dst.parent.mkdir(parents=True, exist_ok=True)
    cp = f"{ORANGE / 'classes'};{ORANGE / 'lib'}/*"
    cmd = ["java", "-cp", cp, "orange.wz.MyImg2Xml", str(src), str(dst)]
    r = subprocess.run(cmd, capture_output=True, text=True, timeout=300, cwd=str(ORANGE))
    if r.returncode != 0:
        raise RuntimeError((r.stderr or r.stdout or "img2xml fail")[:500])


def xml_ids(path: Path) -> set[str]:
    text = path.read_text(encoding="utf-8")
    return set(re.findall(r'<imgdir name="(238\d+)">', text))


def extract_node_blocks(xml_text: str) -> dict[str, str]:
    """Return {id: full <imgdir>...</imgdir>} for top-level 238* children."""
    out: dict[str, str] = {}
    # Card string nodes are shallow (name and optional desc)
    for m in re.finditer(
        r'(<imgdir name="(238\d+)">\s*(?:<string [^/]*/>\s*)+</imgdir>)',
        xml_text,
        re.S,
    ):
        out[m.group(2)] = m.group(1)
    return out


def merge_xml_nodes(server_xml: Path, source_xml: Path, out_xml: Path) -> tuple[int, list[str]]:
    srv = server_xml.read_text(encoding="utf-8")
    src = source_xml.read_text(encoding="utf-8")
    existing = xml_ids(server_xml)
    blocks = extract_node_blocks(src)
    to_add = sorted(k for k in blocks if k not in existing)
    if not to_add:
        out_xml.write_text(srv, encoding="utf-8")
        return 0, []
    insert = "\n".join(blocks[k] for k in to_add) + "\n"
    # Insert before final closing </imgdir>
    idx = srv.rstrip().rfind("</imgdir>")
    if idx < 0:
        raise RuntimeError("server xml missing closing imgdir")
    merged = srv[:idx] + insert + srv[idx:]
    out_xml.write_text(merged, encoding="utf-8")
    return len(to_add), to_add


def xml2img(src_xml: Path, dst_img: Path) -> None:
    cp = f"{ORANGE / 'classes'};{ORANGE / 'lib'}/*"
    cmd = ["java", "-cp", cp, "orange.wz.MyXml2Img", str(src_xml), str(dst_img), "Consume.img"]
    r = subprocess.run(cmd, capture_output=True, text=True, timeout=300, cwd=str(ORANGE))
    if r.returncode != 0:
        raise RuntimeError((r.stderr or r.stdout or "xml2img fail")[:500])


def mcp_merge_missing(src: Path, tgt: Path, missing: list[str]) -> int:
    c = Mcp()
    init = c.call(
        "initialize",
        {
            "protocolVersion": "2024-11-05",
            "capabilities": {},
            "clientInfo": {"name": "monster-card-string-merge", "version": "1.0"},
        },
    )
    if not init:
        raise RuntimeError("MCP initialize failed")
    c.call("notifications/initialized", {})
    src_n = str(src.resolve())
    tgt_n = str(tgt.resolve())
    c.tool("unload_all", {})
    load = c.tool("load_files", {"paths": [src_n, tgt_n], "key": GMS})
    if load and load.get("result", {}).get("isError"):
        raise RuntimeError(str(load)[:400])

    src_kids = c.kids(src_n)
    tgt_kids = set(c.kids(tgt_n))
    print(f"[mcp] src kids={len(src_kids)} tgt kids={len(tgt_kids)} missing_arg={len(missing)}")

    # Prefer computed missing from MCP live trees
    live_miss = [n for n in src_kids if n not in tgt_kids]
    if missing:
        # intersect with requested (high-version preferred if provided)
        want = set(missing)
        live_miss = [n for n in live_miss if n in want] or live_miss
    print(f"[mcp] live missing={len(live_miss)}")

    added = 0
    for i in range(0, len(live_miss), BATCH):
        batch = live_miss[i : i + BATCH]
        sources = [{"rootPath": src_n, "nodePath": n} for n in batch]
        c.tool("copy_nodes", {"sources": sources, "autoParse": True})
        paste = c.tool(
            "paste_nodes",
            {"rootPath": tgt_n, "nodePath": "", "strategy": "SKIP", "autoParse": True},
        )
        n = c.pasted(paste)
        added += n
        print(f"[mcp] batch {i // BATCH + 1}: pasted={n} total={added}")

    snap = tgt.with_suffix(tgt.suffix + ".presave_cardmerge")
    shutil.copy2(tgt, snap)
    save = c.tool("save_node", {"rootPath": tgt_n, "autoParse": True})
    if save and save.get("result", {}).get("isError"):
        shutil.copy2(snap, tgt)
        raise RuntimeError(f"save failed: {save}")
    size = tgt.stat().st_size
    if size < 100:
        shutil.copy2(snap, tgt)
        raise RuntimeError(f"save corrupted size={size}")
    try:
        snap.unlink()
    except OSError:
        pass
    c.tool("unload_all", {})
    return added


def main():
    TMP.mkdir(parents=True, exist_ok=True)
    t0 = time.time()
    print(f"=== monster card string merge @ {datetime.now():%Y-%m-%d %H:%M:%S} ===")

    src_xml = TMP / "source_Consume.img.xml"
    if not src_xml.exists() or src_xml.stat().st_mtime < SRC_IMG.stat().st_mtime:
        print("[1] export source img -> xml")
        img2xml(SRC_IMG, src_xml)
    else:
        print("[1] reuse existing source xml")

    src_ids = xml_ids(src_xml)
    srv_ids = xml_ids(SERVER_XML)
    miss = sorted(src_ids - srv_ids)
    print(f"[info] source cards={len(src_ids)} server cards(238*)={len(srv_ids)} missing={len(miss)}")
    if "2386030" in miss:
        print("[info] 2386030 is among missing")
    if "2387150" in miss:
        print("[info] 2387150 is among missing")

    # Backup client + server
    bak_client = CLIENT_IMG.with_suffix(".img.bak_pre_cardmerge")
    bak_server = SERVER_XML.with_suffix(".xml.bak_pre_cardmerge")
    if not bak_client.exists():
        shutil.copy2(CLIENT_IMG, bak_client)
        print(f"[bak] client -> {bak_client.name}")
    if not bak_server.exists() or bak_server.stat().st_size == SERVER_XML.stat().st_size:
        # refresh if looks identical/old
        shutil.copy2(SERVER_XML, bak_server)
        print(f"[bak] server -> {bak_server.name}")

    mode = "mcp"
    added = 0
    try:
        print("[2] MCP paste SKIP into client Consume.img")
        added = mcp_merge_missing(SRC_IMG, CLIENT_IMG, miss)
        print(f"[2] MCP added={added}")
    except Exception as e:
        print(f"[2] MCP failed: {e}")
        print("[2b] fallback: XML merge + MyXml2Img")
        mode = "xml"
        merged_xml = TMP / "Consume_merged.img.xml"
        added, added_ids = merge_xml_nodes(SERVER_XML, src_xml, merged_xml)
        print(f"[2b] xml nodes to add={added}")
        # Also need client baseline XML
        client_xml = TMP / "client_Consume_before.img.xml"
        print("[2b] export client img -> xml (baseline)")
        img2xml(CLIENT_IMG, client_xml)
        merged2 = TMP / "client_Consume_merged.img.xml"
        added2, _ = merge_xml_nodes(client_xml, src_xml, merged2)
        print(f"[2b] client xml nodes to add={added2}")
        print("[2b] MyXml2Img -> client")
        xml2img(merged2, CLIENT_IMG)
        added = added2
        # Write server from same merged content for consistency
        shutil.copy2(merged2, SERVER_XML)
        print(f"[3] wrote server XML from merged client xml, added={added}")
        print(f"[done] mode={mode} added={added} elapsed={time.time()-t0:.1f}s")
        (TMP / "merge_report.json").write_text(
            json.dumps({"mode": mode, "added": added, "elapsed": time.time() - t0}, indent=2),
            encoding="utf-8",
        )
        return

    print("[3] MyImg2Xml client -> server XML")
    img2xml(CLIENT_IMG, SERVER_XML)
    # verify
    srv2 = xml_ids(SERVER_XML)
    still = [x for x in ("2386030", "2387150") if x not in srv2]
    print(f"[verify] 2386030 in server={('2386030' in srv2)} 2387150={('2387150' in srv2)}")
    if still:
        print(f"[warn] still missing after MCP path: {still}; falling back to XML patch")
        merged_xml = TMP / "Consume_merged.img.xml"
        extra, _ = merge_xml_nodes(SERVER_XML, src_xml, merged_xml)
        shutil.copy2(merged_xml, SERVER_XML)
        xml2img(merged_xml, CLIENT_IMG)
        added += extra
        print(f"[warn-fix] patched extra={extra}")

    print(f"[done] mode={mode} added={added} elapsed={time.time()-t0:.1f}s")
    (TMP / "merge_report.json").write_text(
        json.dumps({"mode": mode, "added": added, "elapsed": time.time() - t0}, indent=2),
        encoding="utf-8",
    )


if __name__ == "__main__":
    main()
