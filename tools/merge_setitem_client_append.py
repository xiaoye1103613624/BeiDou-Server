# -*- coding: utf-8 -*-
"""Merge client SetItemInfo (missing setIds only) into server XML. Decision 1B+2A."""
from __future__ import annotations

import json
import re
from pathlib import Path
from xml.sax.saxutils import escape

CLIENT_TREE = Path(
    r"C:\Users\11036\.cursor\projects\e-project-BeiDou-Server-s9\agent-tools\184c255a-7b55-4ff0-a8aa-9e85675b3ba7.txt"
)
SERVER_XML = Path(
    r"E:\project\BeiDou-Server_s9\gms-server\wz\Etc.wz\SetItemInfo.img.xml"
)
OUT_FRAGMENT = Path(
    r"E:\project\BeiDou-Server_s9\docs\features\set-item-admin\setitem-client-append.xml"
)
ZH_TSV = Path(
    r"E:\project\BeiDou-Server_s9\gms-server\src\main\resources\setitem\set_item_names_zh.tsv"
)


def existing_server_ids(xml: str) -> set[int]:
    return set(
        int(m.group(1))
        for m in re.finditer(
            r'<imgdir name="(\d+)">\s*\n\s*<string name="setItemName"', xml
        )
    )


def xml_value(v) -> str:
    if v is None:
        return ""
    if isinstance(v, bool):
        return "1" if v else "0"
    if isinstance(v, float) and v.is_integer():
        return str(int(v))
    return str(v)


def node_to_xml(node: dict, indent: int) -> list[str]:
    """Convert orange-wz tree node to Cosmic-style XML lines."""
    pad = "  " * indent
    name = node.get("name", "")
    ntype = node.get("type", "")
    children = node.get("children") or []
    value = node.get("value")

    # Skip non-data containers that aren't sets
    if ntype in ("IMAGE",):
        lines: list[str] = []
        for c in children:
            lines.extend(node_to_xml(c, indent))
        return lines

    if ntype == "LIST_PROPERTY" or (children and value is None and ntype not in (
        "STRING_PROPERTY",
        "INT_PROPERTY",
        "SHORT_PROPERTY",
        "LONG_PROPERTY",
        "FLOAT_PROPERTY",
        "DOUBLE_PROPERTY",
        "CANVAS_PROPERTY",
        "SOUND_PROPERTY",
        "UOL_PROPERTY",
        "VECTOR_PROPERTY",
    )):
        lines = [f'{pad}<imgdir name="{escape(str(name))}">']
        for c in children:
            lines.extend(node_to_xml(c, indent + 1))
        lines.append(f"{pad}</imgdir>")
        return lines

    # Leaf property types
    tag = {
        "STRING_PROPERTY": "string",
        "INT_PROPERTY": "int",
        "SHORT_PROPERTY": "short",
        "LONG_PROPERTY": "long",
        "FLOAT_PROPERTY": "float",
        "DOUBLE_PROPERTY": "double",
    }.get(ntype)
    if tag is None:
        # Unknown: if has children treat as imgdir, else skip
        if children:
            lines = [f'{pad}<imgdir name="{escape(str(name))}">']
            for c in children:
                lines.extend(node_to_xml(c, indent + 1))
            lines.append(f"{pad}</imgdir>")
            return lines
        return []

    # Prefer int for numeric shorts that server often stores as int — keep original short/int
    esc_name = escape(str(name))
    esc_val = escape(xml_value(value), {"\"": "&quot;"})
    return [f'{pad}<{tag} name="{esc_name}" value="{esc_val}"/>']


def main() -> None:
    raw = CLIENT_TREE.read_text(encoding="utf-8")
    data = json.loads(raw)
    tree = data["tree"] if isinstance(data, dict) and "tree" in data else data
    children = tree.get("children") or []

    server_xml = SERVER_XML.read_text(encoding="utf-8")
    have = existing_server_ids(server_xml)
    print(f"server existing sets: {len(have)}")

    client_sets = []
    for c in children:
        name = str(c.get("name", ""))
        if name.isdigit():
            client_sets.append((int(name), c))
    print(f"client digit sets: {len(client_sets)}")

    missing = [(sid, node) for sid, node in client_sets if sid not in have]
    missing.sort(key=lambda x: x[0])
    print(f"missing to append: {len(missing)}")

    # Build fragment (indent=1 under root imgdir which uses indent 2 spaces per level;
    # server root children use 2 spaces = indent level 1)
    frag_lines: list[str] = [
        "<!-- appended from BeiDou-Client_S9 Data/Etc/SetItemInfo.img (1B: new setIds only) -->"
    ]
    zh_updates: dict[int, str] = {}
    for sid, node in missing:
        # Extract Chinese name
        for ch in node.get("children") or []:
            if ch.get("name") == "setItemName" and ch.get("value"):
                zh_updates[sid] = str(ch["value"])
                break
        frag_lines.extend(node_to_xml(node, 1))

    OUT_FRAGMENT.parent.mkdir(parents=True, exist_ok=True)
    fragment = "\n".join(frag_lines) + "\n"
    OUT_FRAGMENT.write_text(fragment, encoding="utf-8")
    print(f"wrote fragment {OUT_FRAGMENT} lines={len(frag_lines)}")

    # Merge: insert before final closing </imgdir>
    if not server_xml.rstrip().endswith("</imgdir>"):
        raise SystemExit("unexpected server XML ending")
    # Find last </imgdir>
    idx = server_xml.rstrip().rfind("</imgdir>")
    merged = server_xml[:idx] + fragment + server_xml[idx:]
    SERVER_XML.write_text(merged, encoding="utf-8")
    print(f"merged into {SERVER_XML}")

    # Refresh zh tsv: keep existing, add missing
    zh_map: dict[int, str] = {}
    if ZH_TSV.exists():
        for line in ZH_TSV.read_text(encoding="utf-8").splitlines():
            line = line.strip()
            if not line or "\t" not in line:
                continue
            a, b = line.split("\t", 1)
            zh_map[int(a)] = b
    for sid, name in zh_updates.items():
        zh_map.setdefault(sid, name)
    # Also pull names from ALL client sets into catalog
    for sid, node in client_sets:
        for ch in node.get("children") or []:
            if ch.get("name") == "setItemName" and ch.get("value"):
                zh_map.setdefault(sid, str(ch["value"]))
                break
    lines = [f"{k}\t{zh_map[k]}" for k in sorted(zh_map)]
    ZH_TSV.write_text("\n".join(lines) + "\n", encoding="utf-8")
    docs_tsv = Path(
        r"E:\project\BeiDou-Server_s9\docs\features\set-item-admin\client-set-names-zh.tsv"
    )
    docs_tsv.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"zh catalog size: {len(zh_map)}")

    # Verify
    new_ids = existing_server_ids(SERVER_XML.read_text(encoding="utf-8"))
    print(f"server sets after merge: {len(new_ids)}")
    print(f"sample missing head: {[s for s, _ in missing[:5]]}")
    print(f"kept old id 1: {1 in have and 1 in new_ids}")


if __name__ == "__main__":
    main()
