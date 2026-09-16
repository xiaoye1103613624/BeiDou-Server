# -*- coding: utf-8 -*-
"""Export client SetItemInfo nodes missing from server WZ into Cosmic-style XML."""
from __future__ import annotations

import json
import re
import sys
from pathlib import Path
from xml.sax.saxutils import escape

ROOT = Path(r"E:/project/BeiDou-Server_s9")
SERVER_XML = ROOT / "gms-server/wz/Etc.wz/SetItemInfo.img.xml"
OUT_EXTRA = ROOT / "gms-server/src/main/resources/setitem/SetItemInfo.client-extra.xml"
ZH_TSV = ROOT / "gms-server/src/main/resources/setitem/set_item_names_zh.tsv"
CLIENT_TREE = Path(
    r"C:/Users/11036/.cursor/projects/e-project-BeiDou-Server-s9/agent-tools/client_setitem_full_tree.json"
)


def server_set_ids() -> set[int]:
    text = SERVER_XML.read_text(encoding="utf-8")
    # Top-level sets: imgdir whose first string child is setItemName (within ~300 chars)
    ids: set[int] = set()
    for m in re.finditer(r'<imgdir name="(\d+)">', text):
        chunk = text[m.end() : m.end() + 400]
        if 'name="setItemName"' in chunk.split("<imgdir")[0]:
            ids.add(int(m.group(1)))
    return ids


def xml_type_tag(node_type: str, value) -> tuple[str, str] | None:
    """Return (tag, value_str) for leaf properties."""
    t = (node_type or "").upper()
    if t in ("STRING_PROPERTY", "STRING"):
        return "string", escape(str(value) if value is not None else "")
    if t in ("INT_PROPERTY", "INT"):
        return "int", str(int(value))
    if t in ("SHORT_PROPERTY", "SHORT"):
        return "short", str(int(value))
    if t in ("LONG_PROPERTY", "LONG"):
        return "long", str(int(value))
    if t in ("FLOAT_PROPERTY", "FLOAT"):
        return "float", str(value)
    if t in ("DOUBLE_PROPERTY", "DOUBLE"):
        return "double", str(value)
    return None


SKIP_NAMES = {"setItemNameKR"}  # optional; keep jokerPossible/effectLink


def emit_node(node: dict, indent: int, lines: list[str]) -> None:
    name = str(node.get("name", ""))
    ntype = str(node.get("type", ""))
    children = node.get("children") or []
    pad = "  " * indent

    if name in SKIP_NAMES and not children:
        return

    if children or ntype in ("LIST_PROPERTY", "DIRECTORY", "IMAGE", ""):
        # directory-like
        if ntype.endswith("_PROPERTY") and not children and "value" in node:
            leaf = xml_type_tag(ntype, node.get("value"))
            if leaf:
                tag, val = leaf
                lines.append(f'{pad}<{tag} name="{escape(name)}" value="{val}"/>')
            return
        lines.append(f'{pad}<imgdir name="{escape(name)}">')
        for ch in children:
            emit_node(ch, indent + 1, lines)
        lines.append(f"{pad}</imgdir>")
        return

    leaf = xml_type_tag(ntype, node.get("value"))
    if leaf:
        tag, val = leaf
        lines.append(f'{pad}<{tag} name="{escape(name)}" value="{val}"/>')


def main() -> int:
    if not CLIENT_TREE.exists():
        print("MISSING_TREE", CLIENT_TREE, file=sys.stderr)
        return 2

    data = json.loads(CLIENT_TREE.read_text(encoding="utf-8"))
    tree = data.get("tree") or data
    children = tree.get("children") or []
    server = server_set_ids()
    print(f"server_ids={len(server)}")

    missing_nodes = []
    for ch in children:
        name = str(ch.get("name", ""))
        if not name.isdigit():
            continue
        sid = int(name)
        if sid in server:
            continue
        missing_nodes.append(ch)

    print(f"client_digit={sum(1 for c in children if str(c.get('name','')).isdigit())}")
    print(f"missing={len(missing_nodes)}")

    lines = [
        '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>',
        '<imgdir name="SetItemInfo.client-extra">',
    ]
    zh_rows: list[str] = []
    for node in sorted(missing_nodes, key=lambda n: int(n["name"])):
        # emit without wrapping duplicate root name — each child is set imgdir
        emit_node(node, 1, lines)
        # zh name
        for c in node.get("children") or []:
            if c.get("name") == "setItemName" and c.get("value"):
                zh_rows.append(f"{node['name']}\t{c['value']}")
                break

    lines.append("</imgdir>")
    OUT_EXTRA.parent.mkdir(parents=True, exist_ok=True)
    OUT_EXTRA.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"wrote {OUT_EXTRA} bytes={OUT_EXTRA.stat().st_size}")

    # merge zh names into tsv (keep existing)
    existing: dict[int, str] = {}
    if ZH_TSV.exists():
        for line in ZH_TSV.read_text(encoding="utf-8").splitlines():
            line = line.strip()
            if not line or "\t" not in line:
                continue
            a, b = line.split("\t", 1)
            existing[int(a)] = b
    for row in zh_rows:
        a, b = row.split("\t", 1)
        existing[int(a)] = b
    ZH_TSV.write_text(
        "\n".join(f"{k}\t{existing[k]}" for k in sorted(existing)) + "\n",
        encoding="utf-8",
    )
    print(f"zh_tsv={len(existing)}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
