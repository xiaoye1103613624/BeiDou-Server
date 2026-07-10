#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
北斗 GMS083 — 高版本现金外观装备批量导入

基于 ImportHighVersionCashItems.py 适配：
- 北斗 wz / wz-zh-CN 双 String 合并
- v083 不兼容节点剥离
- 服务端部署时去除 canvas bytedata
- CLI 支持单件试点 (--ids)

用法示例：
  python import_high_version_cash.py --item-type Cape --ids 01102900 --deploy
  python import_high_version_cash.py --item-type Cape --limit 1 --dry-run
"""
from __future__ import annotations

import argparse
import copy
import os
import re
import shutil
import subprocess
import sys
import xml.etree.ElementTree as ET
from pathlib import Path
from typing import Dict, Iterable, List, Optional, Sequence, Set, Tuple

from config_beidou import (
    BEIDOU_ROOT,
    CLIENT_DATA_ROOT,
    CLIENT_IMG_FALLBACK_ROOTS,
    ITEM_TYPE_TO_STRING_CATEGORY,
    ORANGE_WZ_MYXML2IMG,
    OUTPUT_DIR,
    REFERENCE_DIR_DEFAULT,
    SKIP_DIR,
    SOURCE_DIR_DEFAULT,
    STRINGS_SOURCE_EN,
    STRINGS_SOURCE_ZH,
    STRINGS_TARGET_EN,
    STRINGS_TARGET_ZH,
    V083_STRIP_NODE_NAMES,
)


def _write_element(file, element, indent: int = 0) -> None:
    indent_str = " " * indent
    file.write(f"{indent_str}<{element.tag}")
    for attr, value in element.attrib.items():
        file.write(f' {attr}="{value}"')
    if len(element) > 0 or (element.text and element.text.strip()):
        file.write(">\n")
        if element.text and element.text.strip():
            file.write(f"{indent_str}    {element.text.strip()}\n")
        for child in element:
            _write_element(file, child, indent + 4)
        file.write(f"{indent_str}</{element.tag}>\n")
    else:
        file.write(" />\n")


def write_xml_utf8_no_bom(root: ET.Element, output_file: Path) -> None:
    """UTF-8 无 BOM，符合北斗服务端要求。"""
    output_file.parent.mkdir(parents=True, exist_ok=True)
    with open(output_file, "w", encoding="UTF-8", newline="\n") as f:
        f.write('<?xml version="1.0" encoding="UTF-8" standalone="yes"?>\n')
        f.write(f"<{root.tag}")
        for attr, value in root.attrib.items():
            f.write(f' {attr}="{value}"')
        f.write(">\n")
        for child in root:
            _write_element(f, child, indent=4)
        f.write(f"</{root.tag}>\n")


def parse_xml_file(file_path: Path) -> Tuple[Optional[ET.ElementTree], Optional[ET.Element]]:
    try:
        tree = ET.parse(file_path)
        return tree, tree.getroot()
    except Exception as exc:
        print(f"Error parsing {file_path}: {exc}")
        return None, None


def is_cash_item(root: ET.Element) -> bool:
    for info in root.findall("./imgdir[@name='info']"):
        for cash_node in info.findall("./int[@name='cash']"):
            if cash_node.get("value") == "1":
                return True
    return False


def extract_outlink_info(outlink_value: str) -> Tuple[Optional[str], Optional[str]]:
    match = re.search(r"(\d+)\.img/(.*)", outlink_value)
    if match:
        return match.group(1), match.group(2)
    return None, None


def collect_canvas_outlinks(root: ET.Element) -> Dict[ET.Element, Tuple[str, str, str]]:
    """收集所有带 _outlink 的 canvas（079 导出通常无 bytedata，仅有 _outlink/_hash）。"""
    outlinks: Dict[ET.Element, Tuple[str, str, str]] = {}
    for canvas in root.findall(".//canvas"):
        for outlink in canvas.findall("./string[@name='_outlink']"):
            outlink_value = outlink.get("value")
            if not outlink_value:
                break
            file_id, path = extract_outlink_info(outlink_value)
            if file_id:
                outlinks[canvas] = (file_id, path or "", outlink_value)
            break
    return outlinks


def remove_outlink_nodes(root: ET.Element) -> None:
    for parent in root.findall(".//*"):
        for child in list(parent):
            if child.tag == "string" and child.get("name") == "_outlink":
                parent.remove(child)


def strip_v083_incompatible_nodes(root: ET.Element) -> int:
    """删除 v083 不支持的 int/string/imgdir 等命名节点。"""
    removed = 0
    for parent in root.findall(".//*"):
        for child in list(parent):
            name = child.get("name")
            if name in V083_STRIP_NODE_NAMES:
                parent.remove(child)
                removed += 1
    return removed


def strip_bytedata_for_server(root: ET.Element) -> int:
    """服务端 XML 不需要内嵌图片数据。"""
    stripped = 0
    for canvas in root.findall(".//canvas"):
        if "bytedata" in canvas.attrib:
            del canvas.attrib["bytedata"]
            stripped += 1
    return stripped


def copy_bytedata(source_root: Optional[ET.Element], target_root: ET.Element,
                  outlinks: Optional[Dict] = None) -> None:
    for target_canvas in target_root.findall(".//canvas[@bytedata]"):
        canvas_name = target_canvas.get("name")
        outlink_value = None
        for outlink in target_canvas.findall("./string[@name='_outlink']"):
            outlink_value = outlink.get("value")
            break

        if outlink_value:
            file_id, path = extract_outlink_info(outlink_value)
            if not file_id:
                continue
            if outlinks is not None:
                outlinks[target_canvas] = (file_id, path, outlink_value)
                continue
            if source_root is None:
                continue
            if path:
                path_components = path.split("/")
                if path_components[-1] == canvas_name:
                    parent_path = "/".join(path_components[:-1])
                    xpath = _xpath_for_path(parent_path, canvas_name)
                    matching = source_root.findall(xpath)
                    if matching and matching[0].get("bytedata"):
                        _copy_canvas_attrs(matching[0], target_canvas)
                        continue

        if source_root is None:
            continue

        parent_name = None
        for parent in target_root.findall(".//*"):
            for child in parent:
                if child is target_canvas:
                    parent_name = parent.get("name")
                    break
            if parent_name:
                break

        if parent_name and canvas_name:
            xpath = f".//imgdir[@name='{parent_name}']/canvas[@name='{canvas_name}']"
            matching = source_root.findall(xpath)
            if matching and matching[0].get("bytedata"):
                _copy_canvas_attrs(matching[0], target_canvas)
                continue

        if canvas_name:
            matching = source_root.findall(f".//canvas[@name='{canvas_name}']")
            if matching and matching[0].get("bytedata"):
                _copy_canvas_attrs(matching[0], target_canvas)


def _xpath_for_path(parent_path: str, canvas_name: str) -> str:
    if parent_path:
        parts = "/".join(f"imgdir[@name='{comp}']" for comp in parent_path.split("/"))
        return f".//{parts}/canvas[@name='{canvas_name}']"
    return f".//canvas[@name='{canvas_name}']"


def _copy_canvas_attrs(source_canvas: ET.Element, target_canvas: ET.Element) -> None:
    target_canvas.set("bytedata", source_canvas.get("bytedata"))
    if source_canvas.get("width"):
        target_canvas.set("width", source_canvas.get("width"))
    if source_canvas.get("height"):
        target_canvas.set("height", source_canvas.get("height"))


def process_file(file_path: Path, reference_dir: Path, output_dir: Path,
                 server_mode: bool = True) -> Optional[str]:
    file_name = file_path.name
    print(f"Processing cash item: {file_name}...")

    _, root = parse_xml_file(file_path)
    if root is None:
        return None

    canvas_outlinks = collect_canvas_outlinks(root)

    outlinks_by_file: Dict[str, list] = {}
    for canvas, (file_id, path, outlink_value) in canvas_outlinks.items():
        outlinks_by_file.setdefault(file_id, []).append((canvas, path, outlink_value))

    item_id = file_path.stem
    if item_id.endswith(".img"):
        item_id = item_id[:-4]

    reference_file = reference_dir / f"{item_id}.img.xml"
    if reference_file.exists():
        print(f"Using reference file: {reference_file}")
        _, reference_root = parse_xml_file(reference_file)
        if reference_root is not None:
            copy_bytedata(reference_root, root)
    else:
        print(f"Reference file not found: {reference_file}")

    for file_id, outlinks in outlinks_by_file.items():
        outlink_file = reference_dir / f"{file_id}.img.xml"
        if not outlink_file.exists():
            print(f"Outlink file not found: {outlink_file}")
            continue
        print(f"Processing outlinks to {file_id}.img.xml")
        _, outlink_root = parse_xml_file(outlink_file)
        if outlink_root is None:
            continue
        for canvas, path, _ in outlinks:
            canvas_name = canvas.get("name")
            if path:
                path_components = path.split("/")
                if path_components[-1] == canvas_name:
                    parent_path = "/".join(path_components[:-1])
                    xpath = _xpath_for_path(parent_path, canvas_name)
                    matching = outlink_root.findall(xpath)
                    if matching and matching[0].get("bytedata"):
                        _copy_canvas_attrs(matching[0], canvas)
                        continue
            matching = outlink_root.findall(f".//canvas[@name='{canvas_name}']")
            if matching and matching[0].get("bytedata"):
                _copy_canvas_attrs(matching[0], canvas)

    removed = strip_v083_incompatible_nodes(root)
    if removed:
        print(f"  Stripped {removed} v083-incompatible node(s)")

    bytedata_count = len(root.findall(".//canvas[@bytedata]"))
    if bytedata_count:
        print(f"  Resolved bytedata on {bytedata_count} canvas(es)")
    elif canvas_outlinks:
        print(f"  WARN: {len(canvas_outlinks)} _outlink(s) but no bytedata — "
              "REFERENCE 需含 bytedata 或另备客户端 .img")

    client_root = copy.deepcopy(root)
    remove_outlink_nodes(client_root)
    client_dir = output_dir / "client"
    client_xml = client_dir / file_name
    write_xml_utf8_no_bom(client_root, client_xml)

    remove_outlink_nodes(root)

    if server_mode:
        stripped = strip_bytedata_for_server(root)
        if stripped:
            print(f"  Stripped bytedata from {stripped} canvas(es) for server")

    output_file = output_dir / file_name
    write_xml_utf8_no_bom(root, output_file)
    print(f"Saved updated file to {output_file}")
    return item_id


def find_item_string_block(source_root: ET.Element, item_id: str,
                           category: Optional[str] = None) -> Optional[ET.Element]:
    item_id_no_zeros = item_id.lstrip("0")
    categories = (
        [source_root.find(f"./imgdir[@name='Eqp']/imgdir[@name='{category}']")]
        if category
        else source_root.findall("./imgdir[@name='Eqp']/imgdir")
    )
    for cat in categories:
        if cat is None:
            continue
        block = cat.find(f"./imgdir[@name='{item_id_no_zeros}']")
        if block is not None:
            return block
    return None


def get_string_category_for_block(source_root: ET.Element, item_block: ET.Element) -> Optional[str]:
    for cat in source_root.findall("./imgdir[@name='Eqp']/imgdir"):
        if item_block in list(cat):
            return cat.get("name")
    return None


def build_item_string_xml(item_id_no_zeros: str, strings: Dict[str, str]) -> str:
    lines = [f'            <imgdir name="{item_id_no_zeros}">']
    for name, value in strings.items():
        escaped = (
            value.replace("&", "&amp;")
            .replace('"', "&quot;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("'", "&apos;")
        )
        lines.append(f'                <string name="{name}" value="{escaped}" />')
    lines.append("            </imgdir>")
    return "\n".join(lines) + "\n"


def _find_category_bounds(content: str, category_name: str) -> Tuple[int, int]:
    category_start = content.find(f'<imgdir name="{category_name}">')
    if category_start == -1:
        return -1, -1
    open_count = 1
    search_pos = category_start + len(f'<imgdir name="{category_name}">')
    category_end = -1
    while search_pos < len(content):
        open_tag = content.find("<imgdir", search_pos)
        close_tag = content.find("</imgdir>", search_pos)
        if open_tag == -1 and close_tag == -1:
            break
        if open_tag != -1 and (close_tag == -1 or open_tag < close_tag):
            open_count += 1
            search_pos = open_tag + 7
        elif close_tag != -1:
            open_count -= 1
            search_pos = close_tag + 9
            if open_count == 0:
                category_end = close_tag
                break
    return category_start, category_end


def insert_strings_into_target(target_path: Path, category_name: str,
                               items: List[Tuple[str, str]]) -> int:
    """向 Eqp.img.xml 指定分类插入字符串块。"""
    if not items:
        return 0
    try:
        content = target_path.read_text(encoding="UTF-8")
    except Exception as exc:
        print(f"Error reading {target_path}: {exc}")
        return 0

    backup = target_path.with_suffix(target_path.suffix + ".bak")
    backup.write_text(content, encoding="UTF-8")

    category_start, category_end = _find_category_bounds(content, category_name)
    if category_start == -1 or category_end == -1:
        print(f"Warning: category {category_name} not found in {target_path}")
        return 0

    items_sorted = sorted(items, key=lambda x: int(x[0]))
    all_items_xml = "".join(xml for _, xml in items_sorted)

    last_item_end = -1
    search_pos = category_start
    while True:
        item_end = content.find("</imgdir>", search_pos, category_end)
        if item_end == -1 or item_end >= category_end:
            break
        last_item_end = item_end + len("</imgdir>")
        search_pos = last_item_end

    if last_item_end != -1:
        next_line = content.find("\n", last_item_end)
        insert_pos = (next_line + 1) if next_line != -1 and next_line < category_end else last_item_end
    else:
        insert_pos = content.rfind("\n", category_start, category_end)
        if insert_pos == -1 or insert_pos <= category_start:
            insert_pos = category_end

    for item_id, item_xml in items_sorted:
        existing = content.find(f'<imgdir name="{item_id}">', category_start, category_end)
        if existing != -1:
            print(f"  String {item_id} already exists in {target_path.name}, skipping insert")
            all_items_xml = all_items_xml.replace(item_xml, "")

    if not all_items_xml.strip():
        return 0

    content = content[:insert_pos] + all_items_xml + content[insert_pos:]
    target_path.write_text(content, encoding="UTF-8")
    print(f"Inserted {len(items_sorted)} string(s) into {target_path} [{category_name}]")
    return len(items_sorted)


def collect_strings_for_item(item_id: str, item_type: str,
                             source_en: ET.Element,
                             source_zh: Optional[ET.Element]) -> Tuple[Dict[str, str], Dict[str, str]]:
    """英文 + 中文名称；中文源缺失时用英文或占位。"""
    category = ITEM_TYPE_TO_STRING_CATEGORY.get(item_type, item_type)
    en_block = find_item_string_block(source_en, item_id, category)
    zh_block = find_item_string_block(source_zh, item_id, category) if source_zh is not None else None

    en_strings: Dict[str, str] = {}
    zh_strings: Dict[str, str] = {}

    if en_block is not None:
        for s in en_block.findall("./string"):
            en_strings[s.get("name")] = s.get("value", "")

    if zh_block is not None:
        for s in zh_block.findall("./string"):
            zh_strings[s.get("name")] = s.get("value", "")

    if not zh_strings and en_strings:
        zh_strings = dict(en_strings)

    # 中文源与英文相同或仅有 ASCII 名时，加占位后缀
    if zh_strings and en_strings and zh_strings == en_strings:
        if "name" in zh_strings and zh_strings["name"].isascii():
            zh_strings["name"] = f"{zh_strings['name']}（高版本）"

    return en_strings, zh_strings


def deploy_character_xml(item_type: str, file_name: str, output_dir: Path) -> Path:
    dest_dir = SKIP_DIR / item_type
    dest_dir.mkdir(parents=True, exist_ok=True)
    dest = dest_dir / file_name
    shutil.copy2(output_dir / file_name, dest)
    print(f"Deployed Character XML: {dest}")
    return dest


def find_client_img_fallback(item_type: str, item_id: str) -> Optional[Path]:
    """从已知客户端 Data 目录查找含 icon 数据的二进制 .img。"""
    file_name = f"{item_id}.img"
    rel = Path("Character") / item_type / file_name
    candidates: List[Path] = []
    for root in CLIENT_IMG_FALLBACK_ROOTS:
        path = root / rel
        if path.is_file():
            candidates.append(path)
    if not candidates:
        return None
    return max(candidates, key=lambda p: p.stat().st_size)


def xml_has_bytedata(xml_path: Path) -> bool:
    _, root = parse_xml_file(xml_path)
    return root is not None and root.find(".//canvas[@bytedata]") is not None


def build_client_img_from_xml(client_xml: Path, dest: Path, item_id: str) -> bool:
    """用 orange-wz MyXml2Img 将含 bytedata 的 client XML 转为二进制 .img。"""
    if not client_xml.is_file() or not xml_has_bytedata(client_xml):
        return False
    if not ORANGE_WZ_MYXML2IMG.is_dir():
        print(f"  WARN: orange-wz not built at {ORANGE_WZ_MYXML2IMG}")
        return False
    dest.parent.mkdir(parents=True, exist_ok=True)
    cmd = [
        "java", "-cp", str(ORANGE_WZ_MYXML2IMG),
        "orange.wz.MyXml2Img",
        str(client_xml), str(dest), f"{item_id}.img",
    ]
    try:
        result = subprocess.run(cmd, capture_output=True, text=True, check=False)
    except OSError as exc:
        print(f"  WARN: Xml2Img failed: {exc}")
        return False
    if result.returncode != 0:
        print(f"  WARN: Xml2Img exit {result.returncode}: {result.stderr.strip()}")
        return False
    print(f"Built client IMG from XML: {dest} ({dest.stat().st_size} bytes)")
    return True


def deploy_client_img(item_type: str, item_id: str, output_dir: Path,
                      build_from_xml: bool = False) -> Optional[Path]:
    """Case C：将二进制 .img 追加到客户端 Data/Character/{Type}/。"""
    dest_dir = CLIENT_DATA_ROOT / "Character" / item_type
    dest_dir.mkdir(parents=True, exist_ok=True)
    dest = dest_dir / f"{item_id}.img"

    client_xml = output_dir / "client" / f"{item_id}.img.xml"
    if build_from_xml and build_client_img_from_xml(client_xml, dest, item_id):
        return dest

    src = find_client_img_fallback(item_type, item_id)
    if src is None:
        print(f"  WARN: no client .img for {item_id} — 079 XML 无 bytedata，"
              "需从 079 客户端 Data 复制或重新导出含图 XML")
        return None
    shutil.copy2(src, dest)
    print(f"Deployed client IMG: {dest} (from {src}, {src.stat().st_size} bytes)")
    return dest


def load_id_filter(ids_arg: Optional[str], ids_file: Optional[Path]) -> Optional[Set[str]]:
    ids: Set[str] = set()
    if ids_arg:
        for part in ids_arg.split(","):
            part = part.strip()
            if part:
                ids.add(part.zfill(8) if part.isdigit() else part)
    if ids_file and ids_file.exists():
        for line in ids_file.read_text(encoding="UTF-8").splitlines():
            line = line.strip()
            if line and not line.startswith("#"):
                ids.add(line.zfill(8) if line.isdigit() else line)
    return ids if ids else None


def main() -> int:
    parser = argparse.ArgumentParser(description="北斗高版本现金外观装备导入")
    parser.add_argument("--item-type", default="Cape", help="Character.wz 子目录名，如 Cape/Coat")
    parser.add_argument("--source-dir", type=Path, default=None, help="高版本 SOURCE XML 目录")
    parser.add_argument("--reference-dir", type=Path, default=None, help="含 bytedata 的 REFERENCE 目录")
    parser.add_argument("--output-dir", type=Path, default=OUTPUT_DIR)
    parser.add_argument("--strings-source-en", type=Path, default=STRINGS_SOURCE_EN)
    parser.add_argument("--strings-source-zh", type=Path, default=STRINGS_SOURCE_ZH)
    parser.add_argument("--ids", default=None, help="逗号分隔装备 ID，如 01102900")
    parser.add_argument("--ids-file", type=Path, default=None, help="每行一个 ID 的文件")
    parser.add_argument("--limit", type=int, default=0, help="最多处理 N 个（0=不限）")
    parser.add_argument("--deploy", action="store_true", help="写入 wz/Character.wz 与 String")
    parser.add_argument("--dry-run", action="store_true", help="仅处理到 output，不部署")
    parser.add_argument("--build-client-img", action="store_true",
                        help="若 output/client/*.xml 含 bytedata，用 orange-wz 生成 .img")
    parser.add_argument("--include-non-cash", action="store_true", help="不检查 cash=1")
    args = parser.parse_args()

    item_type = args.item_type
    source_dir = args.source_dir or (SOURCE_DIR_DEFAULT / item_type)
    reference_dir = args.reference_dir or (REFERENCE_DIR_DEFAULT / item_type)
    output_dir = args.output_dir / item_type
    skip_dir = SKIP_DIR / item_type
    id_filter = load_id_filter(args.ids, args.ids_file)

    print("=" * 60)
    print("北斗 GMS083 高版本现金外观装备导入")
    print(f"  Item type:     {item_type}")
    print(f"  SOURCE_DIR:    {source_dir}")
    print(f"  REFERENCE_DIR: {reference_dir}")
    print(f"  SKIP_DIR:      {skip_dir}")
    print(f"  OUTPUT_DIR:    {output_dir}")
    print(f"  STRINGS EN:    {STRINGS_TARGET_EN}")
    print(f"  STRINGS ZH:    {STRINGS_TARGET_ZH}")
    if id_filter:
        print(f"  ID filter:     {len(id_filter)} id(s)")
    print("=" * 60)

    if not source_dir.is_dir():
        print(f"ERROR: SOURCE_DIR not found: {source_dir}")
        print("请用 HaRepacker 从高版本客户端导出 Character.wz/{Type}/*.img.xml")
        return 1

    output_dir.mkdir(parents=True, exist_ok=True)

    _, source_strings_en = parse_xml_file(args.strings_source_en)
    if source_strings_en is None:
        print(f"WARNING: STRINGS_SOURCE_EN not parsed: {args.strings_source_en}")

    _, source_strings_zh = parse_xml_file(args.strings_source_zh)
    if source_strings_zh is None:
        source_strings_zh = source_strings_en

    processed_ids: List[str] = []
    total_files = cash_files = skipped_files = removed_files = 0
    limit = args.limit if args.limit > 0 else None

    for file_name in sorted(os.listdir(source_dir)):
        if not file_name.endswith(".img.xml"):
            continue
        if limit is not None and cash_files >= limit:
            break

        item_id = file_name.replace(".img.xml", "").replace(".img", "")
        padded_id = item_id.zfill(8) if item_id.isdigit() else item_id

        if id_filter is not None and padded_id not in id_filter and item_id not in id_filter:
            continue

        total_files += 1
        file_path = source_dir / file_name

        if (skip_dir / file_name).exists():
            print(f"Skipping (exists in server): {file_name}")
            skipped_files += 1
            continue

        _, root = parse_xml_file(file_path)
        if root is None:
            continue
        if not args.include_non_cash and not is_cash_item(root):
            continue

        cash_files += 1
        result_id = process_file(file_path, reference_dir, output_dir, server_mode=True)
        if not result_id:
            continue

        if source_strings_en is not None:
            en_strings, zh_strings = collect_strings_for_item(
                result_id, item_type, source_strings_en, source_strings_zh
            )
            if not en_strings.get("name"):
                out_file = output_dir / file_name
                if out_file.exists():
                    out_file.unlink()
                print(f"Removed output for {result_id} (no strings in SOURCE_Eqp)")
                removed_files += 1
                continue
        processed_ids.append(result_id)

    if processed_ids:
        ids_file_path = output_dir / "processed_item_ids.txt"
        with open(ids_file_path, "w", encoding="UTF-8") as f:
            for pid in processed_ids:
                f.write(f"{pid.lstrip('0')}\n")
        print(f"Wrote {ids_file_path}")

    if processed_ids and args.deploy and not args.dry_run:
        category = ITEM_TYPE_TO_STRING_CATEGORY.get(item_type, item_type)
        en_items: List[Tuple[str, str]] = []
        zh_items: List[Tuple[str, str]] = []

        for pid in processed_ids:
            deploy_character_xml(item_type, f"{pid}.img.xml", output_dir)
            deploy_client_img(item_type, pid, output_dir, build_from_xml=args.build_client_img)
            if source_strings_en is None:
                continue
            en_strings, zh_strings = collect_strings_for_item(
                pid, item_type, source_strings_en, source_strings_zh
            )
            id_nz = pid.lstrip("0")
            if en_strings:
                en_items.append((id_nz, build_item_string_xml(id_nz, en_strings)))
            if zh_strings:
                zh_items.append((id_nz, build_item_string_xml(id_nz, zh_strings)))

        if en_items:
            insert_strings_into_target(STRINGS_TARGET_EN, category, en_items)
        if zh_items:
            insert_strings_into_target(STRINGS_TARGET_ZH, category, zh_items)

    print("\nProcessing Summary:")
    print(f"  Total files scanned:  {total_files}")
    print(f"  Skipped (on server):  {skipped_files}")
    print(f"  Cash items processed: {cash_files}")
    print(f"  No strings removed:   {removed_files}")
    print(f"  Ready to deploy:      {len(processed_ids)}")
    print(f"  Output:               {output_dir}")
    if processed_ids and not args.deploy:
        print("  Run with --deploy to copy into wz/Character.wz and String.wz")

    return 0 if processed_ids or total_files == 0 else 1


if __name__ == "__main__":
    sys.exit(main())
