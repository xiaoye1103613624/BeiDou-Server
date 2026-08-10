# -*- coding: utf-8 -*-
"""Port missing booklet Totems (not in BMS) -> Live Client + Server XML. islot=To."""
from __future__ import annotations

import base64
import json
import os
import shutil
import time
import urllib.request
from pathlib import Path
from typing import Any

MCP_URL = "http://127.0.0.1:10002/mcp"
GMS = {
    "name": "083-GMS",
    "ivBase64": "TSPHKw==",
    "userKeyBase64": (
        "EwAAAFIAAAAqAAAAWwAAAAgAAAACAAAAEAAAAGAAAAAGAAAAAgAAAEMA"
        "AAAPAAAAtAAAAEsAAAA1AAAABQAAABsAAAAKAAAAXwAAAAkAAAAPAAAA"
        "UAAAAAwAAAAbAAAAMwAAAFUAAAABAAAACQAAAFIAAADeAAAAxwAAAB4AAAA="
    ),
}

LIVE_TOTEM = Path(
    r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data\Character\Totem"
)
LIVE_ACC = Path(
    r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data\Character\Accessory"
)
SRV_TOTEM = Path(r"E:\pro\BeiDou-Server_xy\gms-server\wz-zh-CN\Character.wz\Totem")
AUDIT = Path(r"E:\pro\BeiDou-Server_xy\tools\soul-eff-extract\out\badge_emblem_audit")
MISS_JSON = AUDIT / "missing_totem_booklet_110.json"
ICON_DIR = AUDIT / "icons"
LOG = AUDIT / "port_booklet_totem_110_log.jsonl"


class Mcp:
    def __init__(self) -> None:
        self.session_id: str | None = None
        self._id = 0
        self._init()

    def _nid(self) -> int:
        self._id += 1
        return self._id

    def _raw(self, body: dict[str, Any], expect_json: bool = True) -> Any:
        req = urllib.request.Request(
            MCP_URL,
            data=json.dumps(body).encode(),
            method="POST",
            headers={
                "Content-Type": "application/json",
                "Accept": "application/json, text/event-stream",
            },
        )
        if self.session_id:
            req.add_header("Mcp-Session-Id", self.session_id)
        with urllib.request.urlopen(req, timeout=600) as resp:
            raw = resp.read().decode("utf-8", "ignore")
            sid = resp.headers.get("Mcp-Session-Id") or resp.headers.get(
                "mcp-session-id"
            )
            if sid:
                self.session_id = sid
        if not expect_json:
            return raw
        if raw.startswith("event:") or "data:" in raw[:60]:
            for line in raw.splitlines():
                if line.startswith("data:"):
                    return json.loads(line[5:].strip())
            raise RuntimeError(raw[:400])
        return json.loads(raw) if raw.strip() else {}

    def _init(self) -> None:
        self._raw(
            {
                "jsonrpc": "2.0",
                "id": self._nid(),
                "method": "initialize",
                "params": {
                    "protocolVersion": "2024-11-05",
                    "capabilities": {},
                    "clientInfo": {"name": "port-booklet-totem-110", "version": "1.0"},
                },
            }
        )
        self._raw(
            {"jsonrpc": "2.0", "method": "notifications/initialized", "params": {}},
            expect_json=False,
        )

    def tool(self, name: str, args: dict[str, Any]) -> Any:
        data = self._raw(
            {
                "jsonrpc": "2.0",
                "id": self._nid(),
                "method": "tools/call",
                "params": {"name": name, "arguments": args},
            }
        )
        if "error" in data:
            raise RuntimeError(f"{name}: {data['error']}")
        result = data.get("result", data)
        if isinstance(result, dict) and result.get("isError"):
            texts = [
                c.get("text", "")
                for c in (result.get("content") or [])
                if isinstance(c, dict)
            ]
            raise RuntimeError(f"{name} isError: {' '.join(texts)[:500]}")
        if isinstance(result, dict) and "structuredContent" in result:
            return result["structuredContent"]
        if isinstance(result, dict) and "content" in result:
            texts = [
                c.get("text", "")
                for c in (result.get("content") or [])
                if isinstance(c, dict) and c.get("type") == "text"
            ]
            text = "\n".join(texts)
            try:
                return json.loads(text)
            except Exception:
                return text
        return result

    def must(self, name: str, args: dict[str, Any]) -> Any:
        return self.tool(name, args)


def download_icon(item_id: int) -> Path | None:
    ICON_DIR.mkdir(parents=True, exist_ok=True)
    out = ICON_DIR / f"{item_id}.png"
    if out.exists() and out.stat().st_size > 50:
        return out
    url = f"https://mxd.dvg.cn/dbsource/icon/item/{item_id}.png"
    try:
        req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
        with urllib.request.urlopen(req, timeout=8) as resp:
            data = resp.read()
        if len(data) > 50:
            out.write_bytes(data)
            return out
    except Exception:
        pass
    if out.exists():
        out.unlink(missing_ok=True)
    return None


def write_server_xml(item_id: int, level: int) -> Path:
    SRV_TOTEM.mkdir(parents=True, exist_ok=True)
    name = f"0{item_id}.img"
    out = SRV_TOTEM / f"{name}.xml"
    # mild defaults; booklet often level 0
    xml = (
        '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>\n'
        f'<imgdir name="{name}">\n'
        "  <imgdir name=\"info\">\n"
        '    <string name="islot" value="To"/>\n'
        '    <string name="vslot" value="To"/>\n'
        '    <int name="reqJob" value="0"/>\n'
        f'    <int name="reqLevel" value="{int(level) if level else 0}"/>\n'
        '    <int name="reqSTR" value="0"/>\n'
        '    <int name="reqDEX" value="0"/>\n'
        '    <int name="reqINT" value="0"/>\n'
        '    <int name="reqLUK" value="0"/>\n'
        '    <int name="tuc" value="0"/>\n'
        '    <int name="price" value="0"/>\n'
        '    <int name="cash" value="0"/>\n'
        '    <int name="onlyEquip" value="1"/>\n'
        '    <int name="incSTR" value="5"/>\n'
        '    <int name="incDEX" value="5"/>\n'
        '    <int name="incINT" value="5"/>\n'
        '    <int name="incLUK" value="5"/>\n'
        "  </imgdir>\n"
        "</imgdir>\n"
    )
    out.write_text(xml, encoding="utf-8")
    return out


def link_accessory(live_img: Path) -> None:
    LIVE_ACC.mkdir(parents=True, exist_ok=True)
    acc = LIVE_ACC / live_img.name
    try:
        if acc.exists() or acc.is_symlink():
            acc.unlink()
        os.link(live_img, acc)
    except Exception:
        try:
            shutil.copy2(live_img, acc)
        except Exception:
            pass


def create_client_img(mcp: Mcp, item_id: int, icon: Path | None) -> Path:
    LIVE_TOTEM.mkdir(parents=True, exist_ok=True)
    live_img = LIVE_TOTEM / f"0{item_id}.img"
    if live_img.exists():
        live_img.unlink()
    root = str(live_img)
    mcp.must("create_img_file", {"fileName": root, "key": GMS})
    mcp.must(
        "create_child_node",
        {
            "rootPath": root,
            "nodePath": "",
            "name": "info",
            "type": "LIST",
            "autoParse": True,
        },
    )
    ops: list[dict[str, Any]] = [
        {
            "op": "create_child",
            "rootPath": root,
            "nodePath": "info",
            "name": "islot",
            "type": "STRING",
            "value": "To",
        },
        {
            "op": "create_child",
            "rootPath": root,
            "nodePath": "info",
            "name": "vslot",
            "type": "STRING",
            "value": "To",
        },
        {
            "op": "create_child",
            "rootPath": root,
            "nodePath": "info",
            "name": "reqLevel",
            "type": "INT",
            "value": "0",
        },
        {
            "op": "create_child",
            "rootPath": root,
            "nodePath": "info",
            "name": "cash",
            "type": "INT",
            "value": "0",
        },
        {
            "op": "create_child",
            "rootPath": root,
            "nodePath": "info",
            "name": "onlyEquip",
            "type": "INT",
            "value": "1",
        },
    ]
    if icon and icon.exists():
        b64 = base64.b64encode(icon.read_bytes()).decode("ascii")
        ops += [
            {
                "op": "create_child",
                "rootPath": root,
                "nodePath": "info",
                "name": "icon",
                "type": "CANVAS",
                "base64Png": b64,
                "pngFormat": "ARGB4444",
            },
            {
                "op": "create_child",
                "rootPath": root,
                "nodePath": "info",
                "name": "iconRaw",
                "type": "CANVAS",
                "base64Png": b64,
                "pngFormat": "ARGB4444",
            },
        ]
    mcp.must("mutate_nodes", {"operations": ops})
    mcp.must("save_node", {"rootPath": root})
    try:
        mcp.must("unload_all", {"hintGc": False})
    except Exception:
        pass
    link_accessory(live_img)
    return live_img


def main() -> None:
    rows = json.loads(MISS_JSON.read_text(encoding="utf-8"))
    # skip already-ported
    rows = [
        r
        for r in rows
        if not (LIVE_TOTEM / f"0{int(r['id'])}.img").exists()
    ]
    LOG.write_text("", encoding="utf-8")
    print(f"items_remaining={len(rows)}", flush=True)
    mcp = Mcp()
    mcp.must("unload_all", {"hintGc": True})
    ok = fail = no_icon = 0
    for idx, r in enumerate(rows, 1):
        iid = int(r["id"])
        try:
            write_server_xml(iid, r.get("level") or 0)
            icon = download_icon(iid)
            if not icon:
                no_icon += 1
            if idx % 20 == 0:
                try:
                    mcp.must("unload_all", {"hintGc": True})
                except Exception:
                    mcp = Mcp()
            create_client_img(mcp, iid, icon)
            ok += 1
            with LOG.open("a", encoding="utf-8") as fh:
                fh.write(
                    json.dumps(
                        {
                            "ok": True,
                            "id": iid,
                            "icon": bool(icon),
                            "t": time.time(),
                        },
                        ensure_ascii=False,
                    )
                    + "\n"
                )
            if idx % 10 == 0 or idx == len(rows):
                print(
                    f"  {idx}/{len(rows)} id={iid} icon={bool(icon)} size={(LIVE_TOTEM / f'0{iid}.img').stat().st_size}",
                    flush=True,
                )
        except Exception as e:
            fail += 1
            err = str(e)
            with LOG.open("a", encoding="utf-8") as fh:
                fh.write(
                    json.dumps(
                        {"ok": False, "id": iid, "err": err[:400], "t": time.time()},
                        ensure_ascii=False,
                    )
                    + "\n"
                )
            print(f"  FAIL {iid}: {e}", flush=True)
            try:
                mcp = Mcp()
            except Exception:
                pass
    stats = {
        "total": len(rows),
        "ok": ok,
        "fail": fail,
        "no_icon": no_icon,
        "live_totem": len(list(LIVE_TOTEM.glob("*.img"))),
        "live_acc_120": len(list(LIVE_ACC.glob("0120*.img"))),
        "srv_totem": len(list(SRV_TOTEM.glob("*.img.xml"))),
    }
    (AUDIT / "port_booklet_totem_110_stats.json").write_text(
        json.dumps(stats, ensure_ascii=False, indent=2), encoding="utf-8"
    )
    print(json.dumps(stats, ensure_ascii=False, indent=2), flush=True)


if __name__ == "__main__":
    main()
