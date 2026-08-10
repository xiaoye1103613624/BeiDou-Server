# -*- coding: utf-8 -*-
"""Fix Totem icon origin (0,32) using save_as — save_node drops VECTOR under canvas."""
from __future__ import annotations

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
LIVE = Path(
    r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data\Character\Totem"
)
ACC = Path(
    r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data\Character\Accessory"
)
STAGE = Path(
    r"E:\pro\BeiDou-Server_xy\tools\soul-eff-extract\out\totem_icon_fix\stage"
)
OUT = Path(r"E:\pro\BeiDou-Server_xy\tools\soul-eff-extract\out\totem_icon_fix")
OX, OY = 0, 32


class Mcp:
    def __init__(self) -> None:
        self.session_id = None
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
                    "clientInfo": {"name": "totem-origin-saveas", "version": "1"},
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
            raise RuntimeError(f"{name}: {' '.join(texts)[:400]}")
        if isinstance(result, dict) and "structuredContent" in result:
            return result["structuredContent"]
        return result

    def must(self, name: str, args: dict[str, Any]) -> Any:
        return self.tool(name, args)


def has_origin(mcp: Mcp, root: str) -> bool:
    try:
        d = mcp.must(
            "get_node_detail", {"rootPath": root, "nodePath": "info/icon/origin"}
        )
        val = (d.get("detail") or {}).get("value") or {}
        return val.get("y") is not None
    except Exception:
        return False


def fix_one(mcp: Mcp, live: Path) -> None:
    # work on stage copy
    STAGE.mkdir(parents=True, exist_ok=True)
    stage = STAGE / f"{live.stem}_originfix.img"
    if stage.exists():
        try:
            stage.unlink()
        except Exception:
            pass
    shutil.copy2(live, stage)
    root = str(stage)
    mcp.must("unload_all", {})
    mcp.must("load_files", {"paths": [root], "key": GMS})
    for cname in ("icon", "iconRaw"):
        try:
            mcp.must(
                "delete_node",
                {"rootPath": root, "nodePath": f"info/{cname}/origin"},
            )
        except Exception:
            pass
        mcp.must(
            "create_child_node",
            {
                "rootPath": root,
                "nodePath": f"info/{cname}",
                "name": "origin",
                "type": "VECTOR",
                "x": OX,
                "y": OY,
                "autoParse": True,
            },
        )
        mcp.must(
            "mutate_nodes",
            {
                "operations": [
                    {
                        "op": "set_vector",
                        "rootPath": root,
                        "nodePath": f"info/{cname}/origin",
                        "x": OX,
                        "y": OY,
                    }
                ]
            },
        )
    # CRITICAL: save_as keeps origin; save_node drops it
    mcp.must("save_as", {"rootPath": root, "filePath": root, "key": GMS})
    mcp.must("unload_all", {})
    # verify
    mcp.must("load_files", {"paths": [root], "key": GMS})
    if not has_origin(mcp, root):
        raise RuntimeError("origin not persisted after save_as")
    mcp.must("unload_all", {})
    # deploy
    acc = ACC / live.name
    for t in (acc, live):
        if t.exists():
            try:
                t.unlink()
            except Exception:
                aside = t.with_suffix(t.suffix + f".old_{int(time.time())}")
                t.rename(aside)
    shutil.copy2(stage, live)
    try:
        os.link(live, acc)
    except Exception:
        shutil.copy2(live, acc)


def main() -> None:
    only = os.environ.get("ONLY_IDS", "").strip()
    if only:
        names = [f"0{x.strip()}.img" for x in only.split(",") if x.strip()]
    else:
        # all rebuilt from stage list, plus any missing origin
        names = sorted({p.name for p in STAGE.glob("0120*.img") if "_origin" not in p.name and "_fix" not in p.name and "_new" not in p.name})
        # also include booklet ports that may miss origin: all pkg1 without checking first
        if not names:
            names = [p.name for p in LIVE.glob("0120*.img")]

    print(f"targets={len(names)}", flush=True)
    mcp = Mcp()
    ok = fail = skip = 0
    for i, name in enumerate(names, 1):
        live = LIVE / name
        if not live.exists() or live.read_bytes()[:4] != bytes.fromhex("73f86c77"):
            skip += 1
            continue
        try:
            # quick check: skip if already has origin
            mcp.must("unload_all", {})
            mcp.must("load_files", {"paths": [str(live)], "key": GMS})
            if has_origin(mcp, str(live)):
                skip += 1
                if i % 30 == 0:
                    print(f"[{i}] skip-has-origin {name}", flush=True)
                continue
            mcp.must("unload_all", {})
            fix_one(mcp, live)
            ok += 1
            if i % 10 == 0 or name == "01202024.img":
                print(f"[{i}/{len(names)}] fixed {name}", flush=True)
        except Exception as e:
            fail += 1
            print(f"[{i}] FAIL {name}: {e}", flush=True)
            try:
                mcp = Mcp()
            except Exception:
                time.sleep(1)
                mcp = Mcp()
    # verify 1202024
    p = str(LIVE / "01202024.img")
    mcp.must("unload_all", {})
    mcp.must("load_files", {"paths": [p], "key": GMS})
    print("verify1202024", mcp.must("get_node_detail", {"rootPath": p, "nodePath": "info/icon/origin"}))
    print("cash", mcp.must("get_node_detail", {"rootPath": p, "nodePath": "info/cash"}))
    summary = {"ok": ok, "fail": fail, "skip": skip, "targets": len(names)}
    (OUT / "origin_fix_summary.json").write_text(
        json.dumps(summary, ensure_ascii=False, indent=2), encoding="utf-8"
    )
    print(json.dumps(summary), flush=True)


if __name__ == "__main__":
    main()
