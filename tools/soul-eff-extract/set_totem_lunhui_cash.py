# -*- coding: utf-8 -*-
"""Set cash=1 on totem-slot 轮回碑石 client .img via orange-wz MCP."""
from __future__ import annotations

import json
import os
import shutil
import time
import urllib.request
from pathlib import Path
from typing import Any, Dict, List, Optional, Set, Tuple

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
LIVE_CHAR = Path(
    r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data\Character"
)
STAGE = Path(
    r"E:\pro\BeiDou-Server_xy\tools\soul-eff-extract\out\lunhui_totem_cash\stage"
)

# Unique physical files (Totem/Accessory 01202193 are hardlinked)
PRIMARY = [
    LIVE_CHAR / "Totem" / "01202193.img",
    LIVE_CHAR / "Accessory" / "01132300.img",
]
# Soft copies / hardlink partners to refresh after primary write
PARTNERS = {
    "01202193.img": [
        LIVE_CHAR / "Totem" / "01202193.img",
        LIVE_CHAR / "Accessory" / "01202193.img",
    ],
    "01132300.img": [
        LIVE_CHAR / "Accessory" / "01132300.img",
    ],
}


class Mcp(object):
    def __init__(self):
        self.session_id = None  # type: Optional[str]
        self._id = 0
        self._init()

    def _nid(self):
        self._id += 1
        return self._id

    def _raw(self, body, expect_json=True):
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
        with urllib.request.urlopen(req, timeout=300) as resp:
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

    def _init(self):
        self._raw(
            {
                "jsonrpc": "2.0",
                "id": self._nid(),
                "method": "initialize",
                "params": {
                    "protocolVersion": "2024-11-05",
                    "capabilities": {},
                    "clientInfo": {"name": "lunhui-totem-cash", "version": "2"},
                },
            }
        )
        self._raw(
            {"jsonrpc": "2.0", "method": "notifications/initialized", "params": {}},
            expect_json=False,
        )

    def tool(self, name, args):
        data = self._raw(
            {
                "jsonrpc": "2.0",
                "id": self._nid(),
                "method": "tools/call",
                "params": {"name": name, "arguments": args},
            }
        )
        if "error" in data:
            raise RuntimeError("%s: %s" % (name, data["error"]))
        result = data.get("result", data)
        if isinstance(result, dict) and result.get("isError"):
            texts = [
                c.get("text", "")
                for c in (result.get("content") or [])
                if isinstance(c, dict)
            ]
            raise RuntimeError("%s: %s" % (name, " ".join(texts)[:400]))
        if isinstance(result, dict) and "structuredContent" in result:
            return result["structuredContent"]
        return result


def resolve_root_path(mcp, live_path):
    # type: (Mcp, Path) -> str
    roots = mcp.tool("list_loaded_roots", {})
    items = []
    if isinstance(roots, dict):
        items = roots.get("roots") or []
    live_s = str(live_path.resolve())
    for it in items:
        if not isinstance(it, dict):
            continue
        rp = it.get("rootPath") or ""
        if rp.replace("/", "\\").lower() == live_s.replace("/", "\\").lower():
            return rp
        if it.get("name") == live_path.name:
            return rp or live_s
    return live_s


def get_cash(mcp, root_path):
    # type: (Mcp, str) -> str
    d = mcp.tool(
        "get_node_detail",
        {"rootPath": root_path, "nodePath": "info/cash", "autoParse": True},
    )
    if isinstance(d, dict):
        for key in ("value", "intValue", "stringValue"):
            if d.get(key) is not None:
                return str(d.get(key))
        node = d.get("node")
        if isinstance(node, dict):
            for key in ("value", "intValue", "stringValue"):
                if node.get(key) is not None:
                    return str(node.get(key))
        # dump briefly
        return json.dumps(d, ensure_ascii=False)[:200]
    return str(d)[:200]


def unlock_replace(src, dests):
    # type: (Path, List[Path]) -> None
    """Replace dest files (possibly hardlinked) with src contents."""
    seen = set()  # type: Set[Tuple[int, int]]
    # Remove all partners first so hardlink count drops
    for d in dests:
        if not d.exists():
            continue
        st = os.stat(d)
        key = (st.st_dev, st.st_ino)
        if key in seen:
            continue
        seen.add(key)
        bak = d.with_suffix(d.suffix + ".bak_pre_cash")
        if not bak.exists():
            try:
                shutil.copy2(str(d), str(bak))
            except Exception as e:
                print("  bak warn", d, e)
        try:
            d.unlink()
        except Exception:
            aside = d.with_suffix(d.suffix + ".old_%d" % int(time.time()))
            d.rename(aside)
            print("  renamed locked", d, "->", aside.name)

    # Write primary then recreate hardlinks/copies
    primary = dests[0]
    primary.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(str(src), str(primary))
    for d in dests[1:]:
        if d.exists():
            continue
        d.parent.mkdir(parents=True, exist_ok=True)
        try:
            os.link(str(primary), str(d))
        except Exception:
            shutil.copy2(str(primary), str(d))


def set_cash_one(mcp, live_path):
    # type: (Mcp, Path) -> None
    if not live_path.exists():
        print("SKIP missing", live_path)
        return
    STAGE.mkdir(parents=True, exist_ok=True)
    stage_path = STAGE / live_path.name
    print("==>", live_path)

    try:
        mcp.tool("unload_all", {})
    except Exception:
        pass
    time.sleep(0.3)

    live_s = str(live_path.resolve())
    mcp.tool("load_files", {"paths": [live_s], "key": GMS})
    root_path = resolve_root_path(mcp, live_path)
    print("  rootPath", root_path)
    before = get_cash(mcp, root_path)
    print("  cash before", before)

    mcp.tool(
        "mutate_nodes",
        {
            "operations": [
                {
                    "op": "set_value",
                    "rootPath": root_path,
                    "nodePath": "info/cash",
                    "type": "INT",
                    "value": "1",
                }
            ]
        },
    )
    mid = get_cash(mcp, root_path)
    print("  cash after mutate", mid)

    mcp.tool(
        "save_as",
        {
            "rootPath": root_path,
            "filePath": str(stage_path.resolve()),
            "unloadAfterSave": True,
            "clearCache": True,
        },
    )
    time.sleep(0.3)
    try:
        mcp.tool("unload_all", {})
    except Exception:
        pass
    time.sleep(0.3)

    if not stage_path.exists() or stage_path.stat().st_size < 200:
        raise RuntimeError("stage missing/tiny: %s" % stage_path)

    partners = PARTNERS.get(live_path.name, [live_path])
    unlock_replace(stage_path, partners)
    print("  deployed partners", [str(p) for p in partners])

    # verify
    mcp.tool("load_files", {"paths": [str(partners[0].resolve())], "key": GMS})
    root2 = resolve_root_path(mcp, partners[0])
    after = get_cash(mcp, root2)
    print("  cash verify", after)
    try:
        mcp.tool("unload_all", {})
    except Exception:
        pass


def main():
    mcp = Mcp()
    seen_ino = set()  # type: Set[Tuple[int, int]]
    for p in PRIMARY:
        if not p.exists():
            print("SKIP", p)
            continue
        st = os.stat(p)
        key = (st.st_dev, st.st_ino)
        if key in seen_ino:
            print("SKIP hardlink dup", p)
            continue
        seen_ino.add(key)
        set_cash_one(mcp, p)
    print("DONE")


if __name__ == "__main__":
    main()
