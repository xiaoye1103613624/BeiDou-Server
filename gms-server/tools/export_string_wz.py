#!/usr/bin/env python3
"""
Export merged client String/*.img to server wz/ (EN/base) and wz-zh-CN/ (Chinese).

Uses orange-wz MyImg2Xml for img -> xml conversion.
"""
from __future__ import annotations

import argparse
import subprocess
import time
from datetime import datetime
from pathlib import Path

DEFAULT_TARGET = Path(r"E:/mxd_soft/2.客户端/083/beidou_client_xiaoye/BeiDou-Client_1/Data")
DEFAULT_SOURCE = Path(r"E:/mxd_soft/2.客户端/083/北斗GMS083_v1.11_ASM版/BeiDou-Client-V15_fix/Data")
DEFAULT_ORANGE = Path(r"E:/pro/orange-wz/target")
DEFAULT_WZ_EN = Path(__file__).resolve().parents[1] / "wz" / "String.wz"
DEFAULT_WZ_ZH = Path(__file__).resolve().parents[1] / "wz-zh-CN" / "String.wz"
DEFAULT_LOG = Path(__file__).resolve().parent / "_export_string_wz.log"

STRING_MERGED = [
    "String/Item.img",
    "String/Cash.img",
    "String/Consume.img",
    "String/Eqp.img",
    "String/Etc.img",
    "String/Ins.img",
    "String/Map.img",
    "String/Mob.img",
    "String/Npc.img",
    "String/Pet.img",
    "String/Skill.img",
]


def img2xml(orange_dir: Path, src_img: Path, dst_xml: Path) -> tuple[bool, str]:
    dst_xml.parent.mkdir(parents=True, exist_ok=True)
    classes = orange_dir / "classes"
    lib = orange_dir / "lib"
    jar = orange_dir / "OrzRepacker.jar"
    if classes.exists() and lib.exists():
        cp = f"{classes};{lib}/*"
    elif jar.exists():
        cp = str(jar)
    else:
        cp = str(classes)
    cmd = ["java", "-cp", cp, "orange.wz.MyImg2Xml", str(src_img), str(dst_xml)]
    try:
        r = subprocess.run(cmd, capture_output=True, text=True, timeout=300, cwd=str(orange_dir))
    except Exception as e:
        return False, str(e)
    if r.returncode != 0:
        return False, (r.stderr or r.stdout or "exit " + str(r.returncode)).strip()[:300]
    return True, "OK"


def main():
    ap = argparse.ArgumentParser(description="Export String.wz img to server XML")
    ap.add_argument("--target", type=Path, default=DEFAULT_TARGET)
    ap.add_argument("--source", type=Path, default=DEFAULT_SOURCE)
    ap.add_argument("--orange", type=Path, default=DEFAULT_ORANGE)
    ap.add_argument("--wz-en", type=Path, default=DEFAULT_WZ_EN)
    ap.add_argument("--wz-zh", type=Path, default=DEFAULT_WZ_ZH)
    ap.add_argument("--log", type=Path, default=DEFAULT_LOG)
    ap.add_argument("--all-string", action="store_true", help="Export all String/*.img in TARGET")
    args = ap.parse_args()

    if args.all_string:
        rels = [f"String/{p.name}" for p in sorted((args.target / "String").glob("*.img"))]
    else:
        rels = [r for r in STRING_MERGED if (args.target / r).exists()]

    lines: list[str] = []
    t0 = time.time()
    ok_en = ok_zh = fail = 0
    exported_en: list[str] = []
    exported_zh: list[str] = []

    def log(msg: str):
        line = f"[{datetime.now():%Y-%m-%d %H:%M:%S}] {msg}"
        print(line)
        lines.append(line)

    log(f"开始导出 {len(rels)} 个 String img")

    for i, rel in enumerate(rels, 1):
        tgt_img = args.target / rel
        src_img = args.source / rel
        name = Path(rel).name
        zh_xml = args.wz_zh / f"{name}.xml"
        en_xml = args.wz_en / f"{name}.xml"

        ok, msg = img2xml(args.orange, tgt_img, zh_xml)
        if ok:
            ok_zh += 1
            exported_zh.append(str(zh_xml))
            log(f"[{i}/{len(rels)}] zh {rel} -> {zh_xml.name}")
        else:
            fail += 1
            log(f"[{i}/{len(rels)}] zh FAIL {rel}: {msg}")

        en_src = src_img if src_img.exists() else tgt_img
        ok, msg = img2xml(args.orange, en_src, en_xml)
        if ok:
            ok_en += 1
            exported_en.append(str(en_xml))
            log(f"[{i}/{len(rels)}] en {rel} ({en_src.parent.name}) -> {en_xml.name}")
        else:
            fail += 1
            log(f"[{i}/{len(rels)}] en FAIL {rel}: {msg}")

    elapsed = time.time() - t0
    log(f"完成: zh={ok_zh} en={ok_en} fail={fail} elapsed={elapsed:.1f}s")
    log(f"wz-zh-CN: {args.wz_zh}")
    log(f"wz EN: {args.wz_en}")
    args.log.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"\n日志: {args.log}")


if __name__ == "__main__":
    main()
