# -*- coding: utf-8 -*-
"""Port missing BMS250 Character equips -> Live Client_1 (.img via orange-wz MCP) + server XML + String."""
from __future__ import annotations

import argparse
import base64
import json
import re
import shutil
import time
import urllib.error
import urllib.request
from pathlib import Path
from typing import Any
from xml.etree import ElementTree as ET

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

BMS_ROOT = Path(
    r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\玩法\BMS250\wz_tms\Character"
)
LIVE_ROOT = Path(
    r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data\Character"
)
SRV_ROOT = Path(r"E:\pro\BeiDou-Server_xy\gms-server\wz-zh-CN\Character.wz")
STRING_EQP = Path(r"E:\pro\BeiDou-Server_xy\gms-server\wz-zh-CN\String.wz\Eqp.img.xml")
AUDIT = Path(
    r"E:\pro\BeiDou-Server_xy\tools\soul-eff-extract\out\badge_emblem_audit"
)
GAP = AUDIT / "bms250_full_gap"
STAGE = AUDIT / "port_stage"
ICON_DIR = AUDIT / "icons"
BOOKLET = AUDIT / "booklet_badge_emblem_totem.json"
LOG = AUDIT / "port_bms250_log.jsonl"

# Priority waves (user asked badge/emblem/totem first, then rest)
WAVES = [
    ["Totem", "Accessory"],
    [
        "Dragon",
        "Mechanic",
        "ArcaneForce",
        "AuthenticForce",
        "Coat",
        "Pants",
        "Shield",
        "Ring",
        "Glove",
        "Cape",
        "Shoes",
        "Cap",
        "Longcoat",
        "PetEquip",
    ],
    ["Weapon", "TamingMob"],
    ["Android", "Bits", "Familiar"],
]

SLOT_OVERRIDE = {
    # (dir, id_prefix_or_None) -> islot
}


def pad_name(item_id: int, familiar: bool = False) -> str:
    if familiar or item_id >= 9000000:
        return f"{item_id}.img"
    return f"0{item_id}.img"


def find_bms_xml(directory: str, item_id: int) -> Path | None:
    d = BMS_ROOT / directory
    cands = [
        d / f"0{item_id}.img.xml",
        d / f"{item_id:08d}.img.xml",
        d / f"{item_id}.img.xml",
        d / f"0{item_id:07d}.img.xml",
    ]
    for c in cands:
        if c.exists():
            return c
    return None


def rewrite_islot(xml_text: str, directory: str, item_id: int) -> str:
    """Force correct islot/vslot for this private server."""
    islot = None
    if directory == "Totem" or (1202000 <= item_id < 1210000):
        islot = "To"
    elif 1180000 <= item_id <= 1189999:
        islot = "Ba"
    # 119 stay as in source (usually Si)
    if not islot:
        return xml_text

    def repl_slot(m: re.Match[str]) -> str:
        return f'{m.group(1)}{islot}{m.group(3)}'

    xml_text = re.sub(
        r'(name="islot"\s+value=")([^"]*)(")',
        repl_slot,
        xml_text,
        count=1,
    )
    xml_text = re.sub(
        r'(name="vslot"\s+value=")([^"]*)(")',
        repl_slot,
        xml_text,
        count=1,
    )
    return xml_text


def minimal_xml(item_id: int, directory: str, islot: str) -> str:
    name = pad_name(item_id, directory == "Familiar")
    return (
        '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>\n'
        f'<imgdir name="{name}">\n'
        "  <imgdir name=\"info\">\n"
        f'    <string name="islot" value="{islot}"/>\n'
        f'    <string name="vslot" value="{islot}"/>\n'
        '    <int name="reqJob" value="0"/>\n'
        '    <int name="reqLevel" value="0"/>\n'
        '    <int name="reqSTR" value="0"/>\n'
        '    <int name="reqDEX" value="0"/>\n'
        '    <int name="reqINT" value="0"/>\n'
        '    <int name="reqLUK" value="0"/>\n'
        '    <int name="tuc" value="0"/>\n'
        '    <int name="price" value="0"/>\n'
        '    <int name="cash" value="0"/>\n'
        "  </imgdir>\n"
        "</imgdir>\n"
    )


def default_islot(directory: str, item_id: int) -> str:
    if directory == "Totem" or 1202000 <= item_id < 1210000:
        return "To"
    if 1180000 <= item_id <= 1189999:
        return "Ba"
    if 1190000 <= item_id <= 1199999:
        return "Si"
    mapping = {
        "Cap": "Cp",
        "Coat": "Ma",
        "Longcoat": "MaPn",
        "Pants": "Pn",
        "Shoes": "So",
        "Glove": "Gv",
        "Cape": "Sr",
        "Shield": "Si",
        "Weapon": "Wp",
        "Ring": "Ri",
        "Accessory": "Af",
        "PetEquip": "Pe",
        "TamingMob": "Tm",
        "Android": "Af",
        "Dragon": "Sd",
        "Mechanic": "Td",
        "Bits": "Bi",
        "Familiar": "Fd",
        "ArcaneForce": "Af",
        "AuthenticForce": "Af",
    }
    return mapping.get(directory, "Af")


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
                    "clientInfo": {"name": "port-bms250-equips", "version": "1.0"},
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
        urllib.request.urlretrieve(url, out)
        if out.exists() and out.stat().st_size > 50:
            return out
    except Exception:
        pass
    if out.exists():
        out.unlink(missing_ok=True)
    return None


def load_booklet_names() -> dict[int, str]:
    names: dict[int, str] = {}
    if not BOOKLET.exists():
        return names
    data = json.loads(BOOKLET.read_text(encoding="utf-8"))
    for key in ("badge", "emblem", "totem"):
        for row in data.get(key) or []:
            try:
                names[int(row["id"])] = str(row.get("name") or "")
            except Exception:
                continue
    return names


def ensure_string_names(ids_names: dict[int, str]) -> int:
    """Insert missing Eqp Accessory (or category) string nodes. Returns inserted count."""
    if not ids_names:
        return 0
    text = STRING_EQP.read_text(encoding="utf-8")
    # Find Accessory section inside Eqp
    # Typical: <imgdir name="Eqp"> ... <imgdir name="Accessory"> ...
    inserted = 0
    # Work only within Accessory block if possible
    m = re.search(
        r'(<imgdir name="Accessory">)(.*?)(</imgdir>\s*(?:<imgdir name="|</imgdir>\s*$))',
        text,
        re.S,
    )
    if not m:
        # fallback: append before last closing of Eqp — skip if structure unknown
        print("WARN: Accessory section not found in Eqp.img.xml; skip string insert")
        return 0
    head, body, tail = m.group(1), m.group(2), m.group(3)
    for item_id, name in sorted(ids_names.items()):
        if re.search(rf'<imgdir name="{item_id}">', body):
            continue
        safe = (
            name.replace("&", "&amp;")
            .replace('"', "&quot;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
        ) or f"装备{item_id}"
        block = (
            f'\n      <imgdir name="{item_id}">\n'
            f'        <string name="name" value="{safe}"/>\n'
            f'        <string name="desc" value=""/>\n'
            f"      </imgdir>"
        )
        body += block
        inserted += 1
    if inserted:
        new_text = text[: m.start()] + head + body + tail + text[m.end() :]
        bak = STRING_EQP.with_suffix(".img.xml.bak_bms250")
        if not bak.exists():
            shutil.copy2(STRING_EQP, bak)
        STRING_EQP.write_text(new_text, encoding="utf-8")
    return inserted


def write_server_xml(directory: str, item_id: int, xml_text: str) -> Path:
    out_dir = SRV_ROOT / directory
    out_dir.mkdir(parents=True, exist_ok=True)
    # existing convention: 01202193.img.xml
    base = pad_name(item_id, directory == "Familiar")  # e.g. 01202000.img
    out = out_dir / f"{base}.xml"
    out.write_text(xml_text, encoding="utf-8")
    return out


def prepare_stage_xml(directory: str, item_id: int) -> tuple[Path, str]:
    STAGE.mkdir(parents=True, exist_ok=True)
    stage_dir = STAGE / directory
    stage_dir.mkdir(parents=True, exist_ok=True)
    base = pad_name(item_id, directory == "Familiar")
    stage_xml = stage_dir / f"{base}.xml"
    src = find_bms_xml(directory, item_id)
    if src:
        xml_text = src.read_text(encoding="utf-8", errors="ignore")
        # normalize root name
        xml_text = rewrite_islot(xml_text, directory, item_id)
    else:
        xml_text = minimal_xml(item_id, directory, default_islot(directory, item_id))
    stage_xml.write_text(xml_text, encoding="utf-8")
    return stage_xml, xml_text


def _inject_icon_ops(root: str, icon: Path | None) -> list[dict[str, Any]]:
    if not icon or not icon.exists():
        return []
    b64 = base64.b64encode(icon.read_bytes()).decode("ascii")
    return [
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


def port_client_img(
    mcp: Mcp,
    directory: str,
    item_id: int,
    stage_xml: Path,
    icon: Path | None,
) -> Path:
    live_dir = LIVE_ROOT / directory
    live_dir.mkdir(parents=True, exist_ok=True)
    live_img = live_dir / pad_name(item_id, directory == "Familiar")
    islot = default_islot(directory, item_id)

    def finalize_totem_link() -> None:
        if directory != "Totem":
            return
        acc = LIVE_ROOT / "Accessory" / live_img.name
        try:
            if acc.exists() or acc.is_symlink():
                acc.unlink()
            import os

            os.link(live_img, acc)
        except Exception:
            try:
                shutil.copy2(live_img, acc)
            except Exception:
                pass

    # Path A: load staged XML -> mutate icons -> save_as
    try:
        mcp.must("load_files", {"paths": [str(stage_xml)], "key": GMS})
        root = str(stage_xml)
        ops = _inject_icon_ops(root, icon)
        if ops:
            try:
                mcp.must("mutate_nodes", {"operations": ops})
            except Exception:
                if icon and icon.exists():
                    b64 = base64.b64encode(icon.read_bytes()).decode("ascii")
                    mcp.must(
                        "mutate_nodes",
                        {
                            "operations": [
                                {
                                    "op": "set_png",
                                    "rootPath": root,
                                    "nodePath": "info/icon",
                                    "base64Png": b64,
                                    "pngFormat": "ARGB4444",
                                },
                                {
                                    "op": "set_png",
                                    "rootPath": root,
                                    "nodePath": "info/iconRaw",
                                    "base64Png": b64,
                                    "pngFormat": "ARGB4444",
                                },
                            ]
                        },
                    )
        if live_img.exists():
            live_img.unlink()
        mcp.must("save_as", {"rootPath": root, "filePath": str(live_img), "key": GMS})
        try:
            mcp.must("unload_all", {"hintGc": False})
        except Exception:
            pass
        finalize_totem_link()
        return live_img
    except Exception:
        try:
            mcp.must("unload_all", {"hintGc": True})
        except Exception:
            pass

    # Path B: create_img_file + info + icon (works when save_as rejects some XML names)
    if live_img.exists():
        live_img.unlink()
    mcp.must("create_img_file", {"fileName": str(live_img), "key": GMS})
    mcp.must(
        "create_child_node",
        {
            "rootPath": str(live_img),
            "nodePath": "",
            "name": "info",
            "type": "LIST",
            "autoParse": True,
        },
    )
    ops2: list[dict[str, Any]] = [
        {
            "op": "create_child",
            "rootPath": str(live_img),
            "nodePath": "info",
            "name": "islot",
            "type": "STRING",
            "value": islot,
        },
        {
            "op": "create_child",
            "rootPath": str(live_img),
            "nodePath": "info",
            "name": "vslot",
            "type": "STRING",
            "value": islot,
        },
        {
            "op": "create_child",
            "rootPath": str(live_img),
            "nodePath": "info",
            "name": "reqLevel",
            "type": "INT",
            "value": "0",
        },
        {
            "op": "create_child",
            "rootPath": str(live_img),
            "nodePath": "info",
            "name": "cash",
            "type": "INT",
            "value": "0",
        },
    ]
    ops2.extend(_inject_icon_ops(str(live_img), icon))
    mcp.must("mutate_nodes", {"operations": ops2})
    mcp.must("save_node", {"rootPath": str(live_img)})
    try:
        mcp.must("unload_all", {"hintGc": False})
    except Exception:
        pass
    finalize_totem_link()
    return live_img


def miss_ids_for(directory: str) -> list[int]:
    f = GAP / f"miss_live_{directory}.txt"
    if not f.exists():
        return []
    return [int(x) for x in f.read_text(encoding="utf-8").split() if x.strip()]


def log_row(obj: dict[str, Any]) -> None:
    with LOG.open("a", encoding="utf-8") as fh:
        fh.write(json.dumps(obj, ensure_ascii=False) + "\n")


def run_wave(dirs: list[str], limit: int | None, skip_client: bool) -> dict[str, Any]:
    booklet = load_booklet_names()
    mcp = None if skip_client else Mcp()
    if mcp:
        mcp.must("unload_all", {"hintGc": True})
    stats = {
        "ok_client": 0,
        "ok_server": 0,
        "fail": 0,
        "no_icon": 0,
        "string": 0,
        "dirs": {},
    }
    string_buf: dict[int, str] = {}
    n_done = 0
    for directory in dirs:
        ids = miss_ids_for(directory)
        # Also add booklet-only badge/emblem missing from live even if not in BMS miss list
        if directory == "Accessory":
            extra = []
            for key in ("badge", "emblem"):
                for row in (json.loads(BOOKLET.read_text(encoding="utf-8")).get(key) or []):
                    iid = int(row["id"])
                    live = LIVE_ROOT / "Accessory" / pad_name(iid)
                    if not live.exists() and iid not in ids:
                        extra.append(iid)
            ids = sorted(set(ids) | set(extra))
        dstat = {"total": len(ids), "ok": 0, "fail": 0}
        print(f"=== {directory} count={len(ids)} ===", flush=True)
        for idx, item_id in enumerate(ids, 1):
            if limit is not None and n_done >= limit:
                break
            try:
                stage_xml, xml_text = prepare_stage_xml(directory, item_id)
                write_server_xml(directory, item_id, xml_text)
                stats["ok_server"] += 1
                icon = download_icon(item_id)
                if not icon:
                    stats["no_icon"] += 1
                if not skip_client:
                    assert mcp is not None
                    if idx % 25 == 0:
                        mcp.must("unload_all", {"hintGc": True})
                    port_client_img(mcp, directory, item_id, stage_xml, icon)
                    stats["ok_client"] += 1
                name = booklet.get(item_id) or f"{directory}{item_id}"
                # String for accessory-like ids go under Accessory; totems too per provider
                if directory in (
                    "Accessory",
                    "Totem",
                    "Ring",
                ) or item_id // 10000 in (101, 102, 103, 112, 113, 114, 115, 116, 118, 119, 120):
                    string_buf[item_id] = name
                dstat["ok"] += 1
                n_done += 1
                log_row(
                    {
                        "ok": True,
                        "dir": directory,
                        "id": item_id,
                        "icon": bool(icon),
                        "t": time.time(),
                    }
                )
                if idx % 10 == 0 or idx == len(ids):
                    print(
                        f"  {directory} {idx}/{len(ids)} id={item_id} icon={bool(icon)}",
                        flush=True,
                    )
            except Exception as e:
                stats["fail"] += 1
                dstat["fail"] += 1
                n_done += 1
                err = str(e)
                log_row(
                    {
                        "ok": False,
                        "dir": directory,
                        "id": item_id,
                        "err": err[:400],
                        "t": time.time(),
                    }
                )
                print(f"  FAIL {directory} {item_id}: {e}", flush=True)
                if mcp:
                    # Re-init MCP session on transport/session death
                    if "404" in err or "10054" in err or "URLError" in err or "Connection" in err:
                        try:
                            mcp = Mcp()
                            print("  MCP session re-initialized", flush=True)
                        except Exception as e2:
                            print(f"  MCP re-init failed: {e2}", flush=True)
                            mcp = None
                    else:
                        try:
                            mcp.must("unload_all", {"hintGc": True})
                        except Exception:
                            try:
                                mcp = Mcp()
                                print("  MCP session re-initialized after unload fail", flush=True)
                            except Exception:
                                mcp = None
        stats["dirs"][directory] = dstat
        if limit is not None and n_done >= limit:
            break
    stats["string"] = ensure_string_names(string_buf)
    return stats


def main() -> None:
    ap = argparse.ArgumentParser()
    ap.add_argument("--wave", type=int, default=0, help="0=all waves, 1..N specific")
    ap.add_argument("--limit", type=int, default=None)
    ap.add_argument("--skip-client", action="store_true")
    ap.add_argument("--dirs", nargs="*", default=None)
    args = ap.parse_args()
    LOG.write_text("", encoding="utf-8")
    if args.dirs:
        waves = [args.dirs]
    elif args.wave == 0:
        waves = WAVES
    else:
        waves = [WAVES[args.wave - 1]]
    all_stats = []
    for i, w in enumerate(waves, 1):
        print(f"\n######## WAVE {i}: {w} ########\n", flush=True)
        st = run_wave(w, args.limit, args.skip_client)
        all_stats.append(st)
        (AUDIT / f"port_wave_{i}_stats.json").write_text(
            json.dumps(st, ensure_ascii=False, indent=2), encoding="utf-8"
        )
        print(json.dumps(st, ensure_ascii=False, indent=2), flush=True)
        if args.limit:
            break
    (AUDIT / "port_all_stats.json").write_text(
        json.dumps(all_stats, ensure_ascii=False, indent=2), encoding="utf-8"
    )


if __name__ == "__main__":
    main()
