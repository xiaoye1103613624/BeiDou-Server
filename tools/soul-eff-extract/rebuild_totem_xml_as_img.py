# -*- coding: utf-8 -*-
"""Rebuild Live Totem .img files that were wrongly saved as XML text (not PKG1).

Root cause of hover crash / missing icon for IDs like 1202024.
Uses orange-wz MCP + booklet icons (ARGB4444). islot/vslot=To.
"""
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
BMS_TOTEM = Path(
    r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\玩法\BMS250\wz_tms\Character\Totem"
)
OUT = Path(r"E:\pro\BeiDou-Server_xy\tools\soul-eff-extract\out\totem_icon_fix")
ICON_DIR = OUT / "icons"
BAK = OUT / "xml_bak"
LOG = OUT / "rebuild_log.jsonl"


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
                    "clientInfo": {"name": "rebuild-totem-xml-img", "version": "1.0"},
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
        return result

    def must(self, name: str, args: dict[str, Any]) -> Any:
        return self.tool(name, args)


def is_xml_disguised(path: Path) -> bool:
    head = path.read_bytes()[:16]
    return head.startswith(b"<?xml") or head.startswith(b"\xef\xbb\xbf<?")


def list_broken() -> list[Path]:
    return sorted(p for p in LIVE_TOTEM.glob("0120*.img") if is_xml_disguised(p))


def download_icon(item_id: int) -> Path | None:
    ICON_DIR.mkdir(parents=True, exist_ok=True)
    out = ICON_DIR / f"{item_id}.png"
    if out.exists() and out.stat().st_size > 50:
        return out
    url = f"https://mxd.dvg.cn/dbsource/icon/item/{item_id}.png"
    try:
        req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
        with urllib.request.urlopen(req, timeout=12) as resp:
            data = resp.read()
        if len(data) > 50:
            out.write_bytes(data)
            return out
    except Exception:
        pass
    return None


def load_info_props(item_id: int) -> dict[str, Any]:
    """Prefer server XML, else BMS; force islot/vslot To."""
    props: dict[str, Any] = {
        "islot": "To",
        "vslot": "To",
        "reqJob": 0,
        "reqLevel": 0,
        "reqSTR": 0,
        "reqDEX": 0,
        "reqINT": 0,
        "reqLUK": 0,
        "tuc": 0,
        "price": 0,
        "cash": 0,
        "onlyEquip": 1,
    }
    for base in (SRV_TOTEM, BMS_TOTEM):
        xml_path = base / f"0{item_id}.img.xml"
        if not xml_path.exists():
            # live may be XML content saved as .img
            live_xml = LIVE_TOTEM / f"0{item_id}.img"
            if base is SRV_TOTEM and live_xml.exists() and is_xml_disguised(live_xml):
                xml_path = live_xml
            else:
                continue
        try:
            root = ET.parse(xml_path).getroot()
            info = root.find("./imgdir[@name='info']")
            if info is None:
                continue
            for child in list(info):
                n = child.attrib.get("name")
                if not n:
                    continue
                tag = child.tag
                v = child.attrib.get("value", "")
                if n in ("islot", "vslot"):
                    continue  # force To
                if tag == "int":
                    try:
                        props[n] = int(v)
                    except ValueError:
                        pass
                elif tag == "string":
                    props[n] = v
            break
        except Exception:
            continue
    props["islot"] = "To"
    props["vslot"] = "To"
    return props


def ensure_server_xml(item_id: int, props: dict[str, Any]) -> None:
    SRV_TOTEM.mkdir(parents=True, exist_ok=True)
    name = f"0{item_id}.img"
    out = SRV_TOTEM / f"{name}.xml"
    lines = [
        '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>',
        f'<imgdir name="{name}">',
        '  <imgdir name="info">',
    ]
    # stable key order: islot first
    order = [
        "islot",
        "vslot",
        "reqJob",
        "reqLevel",
        "reqSTR",
        "reqDEX",
        "reqINT",
        "reqLUK",
        "tuc",
        "price",
        "cash",
        "onlyEquip",
        "tradeBlock",
        "notSale",
        "equipTradeBlock",
        "incSTR",
        "incDEX",
        "incINT",
        "incLUK",
        "incPAD",
        "incMAD",
        "incMHP",
        "incMMP",
        "incSpeed",
        "incJump",
        "incACC",
        "incEVA",
    ]
    seen = set()
    for k in order:
        if k not in props:
            continue
        seen.add(k)
        v = props[k]
        if isinstance(v, int) or (isinstance(v, str) and v.isdigit()):
            lines.append(f'    <int name="{k}" value="{int(v)}"/>')
        else:
            lines.append(f'    <string name="{k}" value="{v}"/>')
    for k, v in props.items():
        if k in seen:
            continue
        if isinstance(v, int):
            lines.append(f'    <int name="{k}" value="{v}"/>')
        else:
            lines.append(f'    <string name="{k}" value="{v}"/>')
    lines += ["  </imgdir>", "</imgdir>", ""]
    out.write_text("\n".join(lines), encoding="utf-8")


def link_accessory(live_img: Path) -> None:
    LIVE_ACC.mkdir(parents=True, exist_ok=True)
    acc = LIVE_ACC / live_img.name
    try:
        if acc.exists() or acc.is_symlink():
            acc.unlink()
        os.link(live_img, acc)
    except Exception:
        shutil.copy2(live_img, acc)


def rebuild_one(mcp: Mcp, path: Path) -> dict[str, Any]:
    m = re.match(r"0(\d+)\.img$", path.name)
    if not m:
        raise RuntimeError(f"bad name {path.name}")
    item_id = int(m.group(1))
    props = load_info_props(item_id)
    ensure_server_xml(item_id, props)

    BAK.mkdir(parents=True, exist_ok=True)
    bak = BAK / f"{path.name}.xml.txt"
    if path.exists() and is_xml_disguised(path) and not bak.exists():
        shutil.copy2(path, bak)

    # Build in stage to avoid WinError 32 on live locked files
    stage_dir = OUT / "stage"
    stage_dir.mkdir(parents=True, exist_ok=True)
    stage = stage_dir / path.name
    if stage.exists():
        try:
            stage.unlink()
        except Exception:
            pass

    root = str(stage)
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

    ops: list[dict[str, Any]] = []
    for k, v in props.items():
        if isinstance(v, int):
            ops.append(
                {
                    "op": "create_child",
                    "rootPath": root,
                    "nodePath": "info",
                    "name": k,
                    "type": "INT",
                    "value": str(v),
                }
            )
        else:
            ops.append(
                {
                    "op": "create_child",
                    "rootPath": root,
                    "nodePath": "info",
                    "name": k,
                    "type": "STRING",
                    "value": str(v),
                }
            )

    icon = download_icon(item_id)
    has_icon = bool(icon)
    if icon:
        b64 = base64.b64encode(icon.read_bytes()).decode("ascii")
        for cname in ("icon", "iconRaw"):
            ops.append(
                {
                    "op": "create_child",
                    "rootPath": root,
                    "nodePath": "info",
                    "name": cname,
                    "type": "CANVAS",
                    "base64Png": b64,
                    "pngFormat": "ARGB4444",
                }
            )

    mcp.must("mutate_nodes", {"operations": ops})
    mcp.must("save_node", {"rootPath": root})
    try:
        mcp.must("unload_node", {"rootPath": root})
    except Exception:
        try:
            mcp.must("unload_all", {})
        except Exception:
            pass

    head = stage.read_bytes()[:4]
    if head != bytes.fromhex("73f86c77"):
        raise RuntimeError(f"not PKG1 after save: {head.hex()}")

    if os.environ.get("STAGE_ONLY", "").strip() in ("1", "true", "yes"):
        return {"id": item_id, "icon": has_icon, "size": stage.stat().st_size, "staged": True}

    # replace live + accessory
    acc = LIVE_ACC / path.name
    for target in (acc, path):
        if target.exists():
            try:
                target.unlink()
            except PermissionError:
                # rename aside then replace
                aside = target.with_suffix(target.suffix + f".old_{int(time.time())}")
                try:
                    target.rename(aside)
                except Exception as e:
                    raise RuntimeError(f"cannot replace locked {target}: {e}") from e
    shutil.copy2(stage, path)
    link_accessory(path)
    return {"id": item_id, "icon": has_icon, "size": path.stat().st_size}


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    broken = list_broken()
    print(f"broken_xml_as_img={len(broken)}", flush=True)
    only = os.environ.get("ONLY_IDS", "").strip()
    if only:
        want = {f"0{x.strip()}.img" for x in only.split(",") if x.strip()}
        broken = [p for p in broken if p.name in want]
        print(f"filtered={len(broken)}", flush=True)

    LOG.write_text("", encoding="utf-8")
    mcp = Mcp()
    ok = fail = no_icon = 0
    for i, path in enumerate(broken, 1):
        try:
            if i == 1 or i % 25 == 0:
                try:
                    mcp.must("unload_all", {})
                except Exception:
                    mcp = Mcp()
            r = rebuild_one(mcp, path)
            ok += 1
            if not r["icon"]:
                no_icon += 1
            with LOG.open("a", encoding="utf-8") as fh:
                fh.write(json.dumps({"ok": True, **r}, ensure_ascii=False) + "\n")
            if i % 10 == 0 or path.name == "01202024.img":
                print(
                    f"[{i}/{len(broken)}] ok id={r['id']} size={r['size']} icon={r['icon']}",
                    flush=True,
                )
        except Exception as e:
            fail += 1
            with LOG.open("a", encoding="utf-8") as fh:
                fh.write(
                    json.dumps(
                        {"ok": False, "file": path.name, "err": str(e)[:400]},
                        ensure_ascii=False,
                    )
                    + "\n"
                )
            print(f"[{i}/{len(broken)}] FAIL {path.name}: {e}", flush=True)
            try:
                mcp = Mcp()
            except Exception:
                time.sleep(1)
                mcp = Mcp()

    summary = {
        "broken": len(broken),
        "ok": ok,
        "fail": fail,
        "no_icon": no_icon,
        "remain_xml": len(list_broken()),
    }
    (OUT / "rebuild_summary.json").write_text(
        json.dumps(summary, ensure_ascii=False, indent=2), encoding="utf-8"
    )
    print(json.dumps(summary, ensure_ascii=False), flush=True)


if __name__ == "__main__":
    main()
