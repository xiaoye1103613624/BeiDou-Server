# -*- coding: utf-8 -*-
"""Scan String.wz item IDs missing icons on Client_1; cross-check V16 and 079."""
from __future__ import annotations

import csv
import json
import os
import re
import subprocess
import sys
import xml.etree.ElementTree as ET
from collections import defaultdict
from pathlib import Path

STRING_WZ = Path(r"E:\pro\BeiDou-Server_xy\gms-server\wz-zh-CN\String.wz")
CLIENT1 = Path(r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data")
V16 = Path(r"E:\mxd_soft\2.客户端\083\BeiDou-ClientV16.1\BeiDou-Client\Data")
WZ079 = Path(r"F:\BaiduNetdiskDownload\20大陆_079整合版\20dalu_079MS\20dalu\wz\Item.wz")
ORANGE_CLASSES = Path(r"E:\pro\orange-wz\target\classes")
ORANGE_CP_TXT = Path(r"E:\pro\orange-wz\target\cp.txt")
OUT_TXT = Path(r"E:\pro\BeiDou-Server_xy\gms-server\tools\_missing_icons_report.txt")
OUT_JSON = Path(r"E:\pro\BeiDou-Server_xy\gms-server\tools\_missing_icons.json")
OUT_CSV = Path(r"E:\pro\BeiDou-Server_xy\gms-server\tools\_missing_icons.csv")
CACHE_DIR = Path(r"E:\pro\BeiDou-Server_xy\gms-server\tools\_icon_scan_cache")

# Focus categories first; Eqp included if feasible
ITEM_CATEGORIES = {
    "Consume": {
        "string_file": "Consume.img.xml",
        "client_subdir": "Item/Consume",
        "079_subdir": "Consume",
    },
    "Cash": {
        "string_file": "Cash.img.xml",
        "client_subdir": "Item/Cash",
        "079_subdir": "Cash",
    },
    "Etc": {
        "string_file": "Etc.img.xml",
        "client_subdir": "Item/Etc",
        "079_subdir": "Etc",
    },
    "Ins": {
        "string_file": "Ins.img.xml",
        "client_subdir": "Item/Install",
        "079_subdir": "Install",
    },
    "Pet": {
        "string_file": "Pet.img.xml",
        "client_subdir": "Item/Pet",
        "079_subdir": "Pet",
        "id_from_filename": True,
    },
}

CJK_RE = re.compile(r"[\u4e00-\u9fff]")
NUM_ID_RE = re.compile(r"^\d{4,10}$")
IMGDIR_NAME_RE = re.compile(r'<imgdir\s+name="(\d{4,10})"', re.I)
NAME_STR_RE = re.compile(r'<string\s+name="name"\s+value="([^"]*)"', re.I)


def build_classpath() -> str:
    extra = ORANGE_CP_TXT.read_text(encoding="utf-8", errors="replace").strip()
    return str(ORANGE_CLASSES) + os.pathsep + extra


def normalize_item_id(raw: str | int) -> int:
    s = str(raw).strip()
    if s.isdigit():
        return int(s)
    raise ValueError(raw)


def looks_like_item_node(name: str) -> bool:
    """Top-level node that looks like a padded item id (7-8 digits typical)."""
    if not NUM_ID_RE.match(name):
        return False
    # skip tiny container names like "info" already filtered; accept 4+ digit
    n = int(name)
    # Maple item ids are typically 1000000+
    return n >= 1000000 or (len(name) >= 7 and n >= 1000)


def parse_string_xml_flat(path: Path) -> dict[int, str]:
    """Parse Consume/Cash/Etc/Ins/Pet style: top-level numeric imgdirs with name."""
    text = path.read_text(encoding="utf-8", errors="replace")
    # Stream-ish: find each imgdir block for numeric ids at indent level of items
    out: dict[int, str] = {}
    # Use ElementTree for correctness
    try:
        root = ET.fromstring(text)
    except ET.ParseError:
        # fallback regex scrape
        for m in re.finditer(
            r'<imgdir\s+name="(\d{4,10})"\s*>\s*<string\s+name="name"\s+value="([^"]*)"',
            text,
            re.I,
        ):
            out[int(m.group(1))] = m.group(2)
        return out

    def walk(node, depth=0):
        name = node.attrib.get("name", "")
        if NUM_ID_RE.match(name) and int(name) >= 1000:
            item_name = ""
            for ch in node:
                if ch.tag == "string" and ch.attrib.get("name") == "name":
                    item_name = ch.attrib.get("value", "")
                    break
            if item_name or name:
                out[int(name)] = item_name
        for ch in node:
            if ch.tag == "imgdir":
                walk(ch, depth + 1)

    walk(root)
    return out


def parse_eqp_xml(path: Path) -> dict[int, tuple[str, str]]:
    """Return id -> (equip_type, name)."""
    text = path.read_text(encoding="utf-8", errors="replace")
    try:
        root = ET.fromstring(text)
    except ET.ParseError as e:
        print(f"Eqp XML parse error: {e}", file=sys.stderr)
        return {}

    out: dict[int, tuple[str, str]] = {}
    eqp = None
    for ch in root:
        if ch.tag == "imgdir" and ch.attrib.get("name") == "Eqp":
            eqp = ch
            break
    if eqp is None:
        eqp = root

    for typenode in eqp:
        if typenode.tag != "imgdir":
            continue
        etype = typenode.attrib.get("name", "")
        if NUM_ID_RE.match(etype):
            continue
        for item in typenode:
            if item.tag != "imgdir":
                continue
            iname = item.attrib.get("name", "")
            if not NUM_ID_RE.match(iname):
                continue
            nm = ""
            for ch in item:
                if ch.tag == "string" and ch.attrib.get("name") == "name":
                    nm = ch.attrib.get("value", "")
                    break
            out[int(iname)] = (etype, nm)
    return out


def has_chinese(s: str) -> bool:
    return bool(CJK_RE.search(s or ""))


def list_img_nodes(img_path: Path, cp: str) -> list[str]:
    try:
        proc = subprocess.run(
            ["java", "-cp", cp, "ListImgNodes", str(img_path)],
            capture_output=True,
            text=True,
            encoding="utf-8",
            errors="replace",
            timeout=180,
            shell=False,
        )
    except Exception as e:
        print(f"  FAIL ListImgNodes {img_path.name}: {e}", file=sys.stderr)
        return []
    if proc.returncode != 0:
        err = (proc.stderr or "")[:200]
        print(f"  WARN ListImgNodes rc={proc.returncode} {img_path.name}: {err}", file=sys.stderr)
    nodes = []
    for line in (proc.stdout or "").splitlines():
        line = line.strip()
        if not line or line.startswith("=="):
            continue
        nodes.append(line)
    return nodes


def dump_client_item_ids(
    data_root: Path,
    subdir: str,
    cp: str,
    cache_key: str,
    id_from_filename: bool = False,
) -> set[int]:
    CACHE_DIR.mkdir(parents=True, exist_ok=True)
    cache_file = CACHE_DIR / f"{cache_key}.json"
    folder = data_root / subdir.replace("/", os.sep)
    if not folder.is_dir():
        print(f"  missing folder: {folder}")
        return set()

    imgs = sorted(folder.glob("*.img"))
    # ignore backups
    imgs = [p for p in imgs if p.suffix == ".img" and p.name.endswith(".img")]

    # cache invalidation by mtimes + count
    sig = f"{len(imgs)}:" + ":".join(
        f"{p.name}:{p.stat().st_mtime_ns}:{p.stat().st_size}" for p in imgs[:5]
    ) + f":last={(imgs[-1].name if imgs else '')}"

    if cache_file.exists():
        try:
            cached = json.loads(cache_file.read_text(encoding="utf-8"))
            if cached.get("sig") == sig:
                return set(cached["ids"])
        except Exception:
            pass

    ids: set[int] = set()
    total = len(imgs)
    for i, img in enumerate(imgs, 1):
        stem = img.stem  # e.g. 0200 or 5000000
        if id_from_filename and NUM_ID_RE.match(stem) and int(stem) >= 1000000:
            ids.add(int(stem))
            # also padded 8-digit variants
            continue
        if i == 1 or i == total or i % 10 == 0 or total <= 100:
            print(f"  [{cache_key}] {i}/{total} {img.name}", flush=True)
        nodes = list_img_nodes(img, cp)
        added = 0
        for n in nodes:
            if looks_like_item_node(n):
                ids.add(int(n))
                added += 1
        if added == 0 and not id_from_filename:
            # fallback: any pure digit node length 7-8
            for n in nodes:
                if n.isdigit() and 7 <= len(n) <= 8:
                    ids.add(int(n))
                    added += 1
        if added == 0 and nodes and not id_from_filename:
            print(f"  WARN no ids from {img.name}, sample={nodes[:5]}", file=sys.stderr)
        # individual item files (Cash sometimes): basename is 8-digit id
        if NUM_ID_RE.match(stem) and looks_like_item_node(stem.zfill(8) if len(stem) < 8 else stem):
            if int(stem) >= 1000000:
                ids.add(int(stem))

    cache_file.write_text(
        json.dumps({"sig": sig, "ids": sorted(ids)}, ensure_ascii=False),
        encoding="utf-8",
    )
    return ids


def dump_eqp_ids(data_root: Path) -> set[int]:
    char = data_root / "Character"
    ids: set[int] = set()
    if not char.is_dir():
        return ids
    for img in char.rglob("*.img"):
        stem = img.stem
        if NUM_ID_RE.match(stem):
            try:
                ids.add(int(stem))
            except ValueError:
                pass
    return ids


def dump_079_ids(subdir: str) -> set[int]:
    folder = WZ079 / subdir
    ids: set[int] = set()
    if not folder.is_dir():
        return ids
    # grep-like: extract imgdir name="0NNNNNNN" from XML
    for xml in folder.rglob("*.xml"):
        try:
            text = xml.read_text(encoding="utf-8", errors="replace")
        except Exception:
            continue
        for m in IMGDIR_NAME_RE.finditer(text):
            name = m.group(1)
            if looks_like_item_node(name) or (NUM_ID_RE.match(name) and int(name) >= 1000000):
                ids.add(int(name))
        # file stem as pet id
        stem = xml.name.replace(".img.xml", "").replace(".xml", "")
        if NUM_ID_RE.match(stem) and int(stem) >= 1000000:
            ids.add(int(stem))
    return ids


def compare_item_files(c1: Path, v16: Path) -> list[dict]:
    """V16 Item .img files missing or smaller on Client_1."""
    diffs = []
    for sub in ("Consume", "Cash", "Etc", "Install", "Pet"):
        d16 = v16 / "Item" / sub
        d1 = c1 / "Item" / sub
        if not d16.is_dir():
            continue
        for img in d16.glob("*.img"):
            if ".bak" in img.name:
                continue
            peer = d1 / img.name
            sz16 = img.stat().st_size
            if not peer.exists():
                diffs.append(
                    {
                        "subdir": sub,
                        "file": img.name,
                        "v16_size": sz16,
                        "client1_size": 0,
                        "status": "missing_on_client1",
                        "v16_path": str(img),
                        "client1_path": str(peer),
                    }
                )
            else:
                sz1 = peer.stat().st_size
                if sz16 > sz1 * 1.05 and sz16 - sz1 > 1024:
                    diffs.append(
                        {
                            "subdir": sub,
                            "file": img.name,
                            "v16_size": sz16,
                            "client1_size": sz1,
                            "status": "smaller_on_client1",
                            "v16_path": str(img),
                            "client1_path": str(peer),
                        }
                    )
    diffs.sort(key=lambda x: (-x["v16_size"], x["subdir"], x["file"]))
    return diffs


def main() -> int:
    cp = build_classpath()
    print("Classpath OK, java ListImgNodes ready")

    report_lines: list[str] = []
    summary: dict = {
        "categories": {},
        "missing": [],
        "file_diffs_v16_vs_client1": [],
    }

    def log(s: str = ""):
        report_lines.append(s)
        try:
            print(s)
        except Exception:
            print(s.encode('ascii', errors='backslashreplace').decode('ascii'))

    log("=== MapleStory Missing Icon Scanner ===")
    log(f"String.wz: {STRING_WZ}")
    log(f"Client_1:  {CLIENT1}")
    log(f"V16:       {V16}")
    log(f"079:       {WZ079}")
    log()

    # Dump client covers for item cats
    c1_cover: dict[str, set[int]] = {}
    v16_cover: dict[str, set[int]] = {}
    wz079_cover: dict[str, set[int]] = {}

    for cat, meta in ITEM_CATEGORIES.items():
        log(f"--- Dumping Client_1 {cat} ---")
        c1_cover[cat] = dump_client_item_ids(
            CLIENT1,
            meta["client_subdir"],
            cp,
            f"client1_{cat}",
            id_from_filename=meta.get("id_from_filename", False),
        )
        log(f"  Client_1 {cat} nodes: {len(c1_cover[cat])}")

        log(f"--- Dumping V16 {cat} ---")
        v16_cover[cat] = dump_client_item_ids(
            V16,
            meta["client_subdir"],
            cp,
            f"v16_{cat}",
            id_from_filename=meta.get("id_from_filename", False),
        )
        log(f"  V16 {cat} nodes: {len(v16_cover[cat])}")

        log(f"--- Dumping 079 {cat} ---")
        wz079_cover[cat] = dump_079_ids(meta["079_subdir"])
        log(f"  079 {cat} ids: {len(wz079_cover[cat])}")

    # Eqp
    log("--- Dumping Client_1 Eqp (Character/*.img) ---")
    c1_eqp = dump_eqp_ids(CLIENT1)
    log(f"  Client_1 equip imgs: {len(c1_eqp)}")
    log("--- Dumping V16 Eqp ---")
    v16_eqp = dump_eqp_ids(V16)
    log(f"  V16 equip imgs: {len(v16_eqp)}")

    all_missing = []
    top50_consume = []

    for cat, meta in ITEM_CATEGORIES.items():
        spath = STRING_WZ / meta["string_file"]
        if not spath.exists():
            log(f"MISSING string file {spath}")
            continue
        items = parse_string_xml_flat(spath)
        # Prefer Chinese-named; still track all
        cn_items = {i: n for i, n in items.items() if has_chinese(n)}
        cover = c1_cover[cat]
        covered = [i for i in cn_items if i in cover]
        missing = [(i, cn_items[i]) for i in sorted(cn_items) if i not in cover]

        on_v16 = [(i, n) for i, n in missing if i in v16_cover[cat]]
        on_079 = [(i, n) for i, n in missing if i in wz079_cover[cat]]
        on_either = [(i, n) for i, n in missing if i in v16_cover[cat] or i in wz079_cover[cat]]

        cat_sum = {
            "string_total": len(items),
            "string_chinese": len(cn_items),
            "client1_nodes": len(cover),
            "client1_covered_cn": len(covered),
            "missing_cn": len(missing),
            "missing_on_v16": len(on_v16),
            "missing_on_079": len(on_079),
            "missing_on_v16_or_079": len(on_either),
        }
        summary["categories"][cat] = cat_sum

        log()
        log(f"===== {cat} =====")
        log(f"  String IDs (all):     {cat_sum['string_total']}")
        log(f"  String IDs (Chinese): {cat_sum['string_chinese']}")
        log(f"  Client_1 item nodes:  {cat_sum['client1_nodes']}")
        log(f"  Covered (CN):         {cat_sum['client1_covered_cn']}")
        log(f"  Missing (CN):         {cat_sum['missing_cn']}")
        log(f"  Missing also on V16:  {cat_sum['missing_on_v16']}")
        log(f"  Missing also on 079:  {cat_sum['missing_on_079']}")
        log(f"  Missing on V16|079:   {cat_sum['missing_on_v16_or_079']}")

        for iid, name in missing:
            rec = {
                "id": iid,
                "name": name,
                "category": cat,
                "on_v16": iid in v16_cover[cat],
                "on_079": iid in wz079_cover[cat],
            }
            all_missing.append(rec)
            summary["missing"].append(rec)

        if cat == "Consume":
            top50_consume = missing[:50]

    # Eqp
    eqp_path = STRING_WZ / "Eqp.img.xml"
    if eqp_path.exists():
        eqp_items = parse_eqp_xml(eqp_path)
        cn_eqp = {i: v for i, v in eqp_items.items() if has_chinese(v[1])}
        missing_eqp = [(i, t, n) for i, (t, n) in sorted(cn_eqp.items()) if i not in c1_eqp]
        on_v16_eqp = [x for x in missing_eqp if x[0] in v16_eqp]
        cat_sum = {
            "string_total": len(eqp_items),
            "string_chinese": len(cn_eqp),
            "client1_nodes": len(c1_eqp),
            "client1_covered_cn": len(cn_eqp) - len(missing_eqp),
            "missing_cn": len(missing_eqp),
            "missing_on_v16": len(on_v16_eqp),
            "missing_on_079": None,
            "missing_on_v16_or_079": len(on_v16_eqp),
        }
        summary["categories"]["Eqp"] = cat_sum
        log()
        log("===== Eqp =====")
        log(f"  String IDs (all):     {cat_sum['string_total']}")
        log(f"  String IDs (Chinese): {cat_sum['string_chinese']}")
        log(f"  Client_1 equip imgs:  {cat_sum['client1_nodes']}")
        log(f"  Covered (CN):         {cat_sum['client1_covered_cn']}")
        log(f"  Missing (CN):         {cat_sum['missing_cn']}")
        log(f"  Missing also on V16:  {cat_sum['missing_on_v16']}")
        for iid, etype, name in missing_eqp:
            rec = {
                "id": iid,
                "name": name,
                "category": f"Eqp/{etype}",
                "on_v16": iid in v16_eqp,
                "on_079": False,
            }
            all_missing.append(rec)
            summary["missing"].append(rec)

    # File diffs
    log()
    log("===== Client_1 vs V16 Item file-level diffs =====")
    diffs = compare_item_files(CLIENT1, V16)
    summary["file_diffs_v16_vs_client1"] = diffs
    missing_files = [d for d in diffs if d["status"] == "missing_on_client1"]
    smaller = [d for d in diffs if d["status"] == "smaller_on_client1"]
    log(f"  V16 .img missing on Client_1: {len(missing_files)}")
    log(f"  V16 .img larger than Client_1: {len(smaller)}")
    log("  --- Wholesale copy candidates (missing on Client_1) ---")
    for d in missing_files[:80]:
        log(f"  COPY {d['subdir']}/{d['file']}  v16={d['v16_size']} -> Client_1")
    if len(missing_files) > 80:
        log(f"  ... and {len(missing_files) - 80} more missing files")
    log("  --- Larger on V16 (possible partial packs) ---")
    for d in smaller[:40]:
        log(
            f"  SMALLER {d['subdir']}/{d['file']}  c1={d['client1_size']} v16={d['v16_size']}"
        )

    log()
    log("===== Top 50 missing Consume (Chinese name) =====")
    for iid, name in top50_consume:
        flags = []
        if iid in v16_cover.get("Consume", set()):
            flags.append("V16")
        if iid in wz079_cover.get("Consume", set()):
            flags.append("079")
        fl = ",".join(flags) if flags else "-"
        log(f"  {iid}\t{name}\t[{fl}]")

    log()
    log()
    log("===== Missing rows: %d (full list in JSON/CSV) =====" % len(all_missing))
    with OUT_CSV.open("w", encoding="utf-8", newline="") as f:
        w = csv.writer(f)
        w.writerow(["id", "category", "name", "on_v16", "on_079"])
        for rec in all_missing:
            w.writerow([rec["id"], rec["category"], rec["name"], int(rec["on_v16"]), int(rec["on_079"])])
    log("CSV: %s" % OUT_CSV)

    log("===== TOTALS =====")
    for cat, cs in summary["categories"].items():
        log(
            f"  {cat}: string_cn={cs['string_chinese']} covered={cs['client1_covered_cn']} "
            f"missing={cs['missing_cn']} on_v16={cs['missing_on_v16']} on_079={cs['missing_on_079']}"
        )

    OUT_TXT.write_text("\n".join(report_lines) + "\n", encoding="utf-8")
    OUT_JSON.write_text(json.dumps(summary, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"\nWrote {OUT_TXT}")
    print(f"Wrote {OUT_JSON}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
