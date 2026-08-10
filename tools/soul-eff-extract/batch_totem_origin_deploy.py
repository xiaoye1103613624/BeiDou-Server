# -*- coding: utf-8 -*-
"""Rebuild all previously-broken totems with icon origin (0,32) via save_as, then deploy."""
from __future__ import annotations

import base64
import json
import os
import re
import shutil
import time
import urllib.request
import xml.etree.ElementTree as ET
from pathlib import Path

MCP = "http://127.0.0.1:10002/mcp"
GMS = {
    "name": "083-GMS",
    "ivBase64": "TSPHKw==",
    "userKeyBase64": (
        "EwAAAFIAAAAqAAAAWwAAAAgAAAACAAAAEAAAAGAAAAAGAAAAAgAAAEMA"
        "AAAPAAAAtAAAAEsAAAA1AAAABQAAABsAAAAKAAAAXwAAAAkAAAAPAAAA"
        "UAAAAAwAAAAbAAAAMwAAAFUAAAABAAAACQAAAFIAAADeAAAAxwAAAB4AAAA="
    ),
}
LIVE = Path(
    r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data\Character\Totem"
)
ACC = Path(
    r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data\Character\Accessory"
)
SRV = Path(r"E:\pro\BeiDou-Server_xy\gms-server\wz-zh-CN\Character.wz\Totem")
STAGE = Path(
    r"E:\pro\BeiDou-Server_xy\tools\soul-eff-extract\out\totem_icon_fix\stage"
)
ICONS = STAGE.parent / "icons"
OUTOK = STAGE / "originok"
OUTOK.mkdir(parents=True, exist_ok=True)

sid = None
nid = 0


def raw(body, expect=True):
    global sid
    req = urllib.request.Request(
        MCP,
        data=json.dumps(body).encode(),
        method="POST",
        headers={
            "Content-Type": "application/json",
            "Accept": "application/json, text/event-stream",
        },
    )
    if sid:
        req.add_header("Mcp-Session-Id", sid)
    with urllib.request.urlopen(req, timeout=300) as resp:
        data = resp.read().decode("utf-8", "ignore")
        s = resp.headers.get("Mcp-Session-Id") or resp.headers.get("mcp-session-id")
        if s:
            sid = s
    if not expect:
        return data
    if "data:" in data[:50]:
        for line in data.splitlines():
            if line.startswith("data:"):
                return json.loads(line[5:].strip())
    return json.loads(data)


def call(name, args):
    global nid
    nid += 1
    d = raw(
        {
            "jsonrpc": "2.0",
            "id": nid,
            "method": "tools/call",
            "params": {"name": name, "arguments": args},
        }
    )
    r = d.get("result", d)
    if isinstance(r, dict) and r.get("isError"):
        raise RuntimeError(
            "".join(c.get("text", "") for c in (r.get("content") or []))[:300]
        )
    if isinstance(r, dict) and "structuredContent" in r:
        return r["structuredContent"]
    if isinstance(r, dict) and "content" in r:
        for c in r["content"]:
            if c.get("type") == "text":
                try:
                    return json.loads(c["text"])
                except Exception:
                    return c["text"]
    return r


def init():
    global sid, nid
    sid = None
    nid = 0
    raw(
        {
            "jsonrpc": "2.0",
            "id": 1,
            "method": "initialize",
            "params": {
                "protocolVersion": "2024-11-05",
                "capabilities": {},
                "clientInfo": {"name": "origin-batch2", "version": "1"},
            },
        }
    )
    raw(
        {"jsonrpc": "2.0", "method": "notifications/initialized", "params": {}},
        expect=False,
    )


def props_for(iid: int) -> dict:
    p = {
        "islot": "To",
        "vslot": "To",
        "reqJob": 0,
        "reqLevel": 0,
        "cash": 0,
        "onlyEquip": 1,
    }
    xml = SRV / f"0{iid}.img.xml"
    if xml.exists():
        try:
            root = ET.parse(xml).getroot()
            info = root.find("./imgdir[@name='info']")
            for c in list(info or []):
                n = c.attrib.get("name")
                v = c.attrib.get("value", "")
                if not n or n in ("islot", "vslot"):
                    continue
                if c.tag == "int":
                    try:
                        p[n] = int(v)
                    except Exception:
                        pass
                else:
                    p[n] = v
        except Exception:
            pass
    p["islot"] = "To"
    p["vslot"] = "To"
    p["cash"] = int(p.get("cash") or 0)
    return p


def ensure_icon(iid):
    out = ICONS / f"{iid}.png"
    if out.exists() and out.stat().st_size > 50:
        return out
    try:
        req = urllib.request.Request(
            f"https://mxd.dvg.cn/dbsource/icon/item/{iid}.png",
            headers={"User-Agent": "Mozilla/5.0"},
        )
        data = urllib.request.urlopen(req, timeout=12).read()
        if len(data) > 50:
            ICONS.mkdir(parents=True, exist_ok=True)
            out.write_bytes(data)
            return out
    except Exception:
        pass
    return None


def build_one(iid: int) -> Path:
    icon = ensure_icon(iid)
    if not icon:
        raise RuntimeError("no icon")
    b64 = base64.b64encode(icon.read_bytes()).decode()
    props = props_for(iid)
    out = OUTOK / f"0{iid}.img"
    if out.exists():
        try:
            out.unlink()
        except Exception:
            pass
    root = str(out)
    call("unload_all", {})
    call("create_img_file", {"fileName": root, "key": GMS})
    call(
        "create_child_node",
        {
            "rootPath": root,
            "nodePath": "",
            "name": "info",
            "type": "LIST",
            "autoParse": True,
        },
    )
    ops = [
        {
            "op": "create_child",
            "rootPath": root,
            "nodePath": "info",
            "name": k,
            "type": ("INT" if isinstance(v, int) else "STRING"),
            "value": str(v),
        }
        for k, v in props.items()
    ]
    for c in ("icon", "iconRaw"):
        ops.append(
            {
                "op": "create_child",
                "rootPath": root,
                "nodePath": "info",
                "name": c,
                "type": "CANVAS",
                "base64Png": b64,
                "pngFormat": "ARGB4444",
                "autoParse": True,
            }
        )
    call("mutate_nodes", {"operations": ops})
    for c in ("icon", "iconRaw"):
        call(
            "create_child_node",
            {
                "rootPath": root,
                "nodePath": f"info/{c}",
                "name": "origin",
                "type": "VECTOR",
                "x": 0,
                "y": 32,
                "autoParse": True,
            },
        )
        call(
            "mutate_nodes",
            {
                "operations": [
                    {
                        "op": "set_vector",
                        "rootPath": root,
                        "nodePath": f"info/{c}/origin",
                        "x": 0,
                        "y": 32,
                    }
                ]
            },
        )
    call("save_as", {"rootPath": root, "filePath": root, "key": GMS})
    call("unload_all", {})
    return out


def deploy(src: Path, name: str) -> None:
    live = LIVE / name
    acc = ACC / name
    for t in (acc, live):
        if t.exists():
            try:
                t.unlink()
            except Exception:
                aside = t.with_suffix(t.suffix + f".old_{int(time.time())}")
                try:
                    t.rename(aside)
                except Exception as e:
                    raise RuntimeError(f"locked {t}: {e}") from e
    shutil.copy2(src, live)
    try:
        os.link(live, acc)
    except Exception:
        shutil.copy2(live, acc)


def main():
    init()
    targets = []
    for p in STAGE.glob("0120*.img"):
        m = re.match(r"0(\d+)\.img$", p.name)
        if m:
            targets.append(int(m.group(1)))
    targets = sorted(set(targets))
    # skip already-correct 1202024 optionally rebuild anyway
    print(f"targets={len(targets)}", flush=True)
    built = deployed = failed = 0
    for i, iid in enumerate(targets, 1):
        name = f"0{iid}.img"
        try:
            out = OUTOK / name
            if not (out.exists() and out.stat().st_size > 800):
                out = build_one(iid)
                built += 1
            # unload before deploy to avoid locks
            try:
                call("unload_all", {})
            except Exception:
                pass
            deploy(out, name)
            deployed += 1
            if i % 20 == 0 or iid == 1202024:
                print(
                    f"[{i}/{len(targets)}] ok {iid} size={out.stat().st_size}",
                    flush=True,
                )
        except Exception as e:
            failed += 1
            print(f"FAIL {iid}: {e}", flush=True)
            try:
                init()
            except Exception:
                time.sleep(1)
                init()
    summary = {"built": built, "deployed": deployed, "failed": failed, "n": len(targets)}
    (STAGE.parent / "origin_batch_summary.json").write_text(
        json.dumps(summary, ensure_ascii=False, indent=2), encoding="utf-8"
    )
    print(json.dumps(summary), flush=True)


if __name__ == "__main__":
    main()
