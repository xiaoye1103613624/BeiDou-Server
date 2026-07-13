#!/usr/bin/env python3
"""Sync missing basic WZ assets from BeiDou client Data/ to gms-server/wz/."""

from __future__ import annotations

import shutil
import subprocess
import sys
from pathlib import Path

CLIENT_DATA = Path(r"E:\mxd_soft\2.客户端\083\BeiDou-ClientV16.1\BeiDou-Client\Data")
SERVER_WZ = Path(r"E:\pro\BeiDou-Server_xy\gms-server\wz")
ORANGE_WZ_CLASSES = Path(r"E:\pro\orange-wz\target\classes")
ORANGE_WZ_DEPS = Path(r"E:\pro\orange-wz\target\dependency")
STAGING = Path(r"E:\pro\BeiDou-Server_xy\gms-server\tools\_wz_sync_staging")
LOG_FILE = Path(r"E:\pro\BeiDou-Server_xy\gms-server\tools\_wz_sync.log")


def log(msg: str) -> None:
    text = str(msg)
    try:
        print(text)
    except UnicodeEncodeError:
        print(text.encode("utf-8", errors="replace").decode("utf-8", errors="replace"))
    with LOG_FILE.open("a", encoding="utf-8") as f:
        f.write(text + "\n")


def ensure_dirs() -> None:
    if STAGING.exists():
        shutil.rmtree(STAGING)
    STAGING.mkdir(parents=True, exist_ok=True)
    LOG_FILE.write_text("", encoding="utf-8")


def copy_file(src: Path, dst: Path) -> bool:
    if not src.exists():
        log(f"[SKIP] missing: {src}")
        return False
    dst.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(src, dst)
    return True


def stage_maps() -> list[Path]:
    staged: list[Path] = []
    map_jobs = [
        (CLIENT_DATA / "Map" / "Map" / "Map1", "105200*.img"),
        (CLIENT_DATA / "Map" / "Map" / "Map9", "910001000.img"),
    ]
    for src_dir, pattern in map_jobs:
        rel = src_dir.relative_to(CLIENT_DATA)
        for src in sorted(src_dir.glob(pattern)):
            dst = STAGING / rel / src.name
            if copy_file(src, dst):
                staged.append(dst)
    log(f"[STAGE] maps: {len(staged)}")
    return staged


def stage_mobs() -> list[Path]:
    staged: list[Path] = []
    mob_dir = CLIENT_DATA / "Mob"
    patterns = ["890*.img", "891*.img", "892*.img", "893*.img"]
    seen: set[str] = set()
    for pattern in patterns:
        for src in sorted(mob_dir.glob(pattern)):
            if src.name in seen:
                continue
            seen.add(src.name)
            dst = STAGING / "Mob" / src.name
            if copy_file(src, dst):
                staged.append(dst)
    log(f"[STAGE] mobs: {len(staged)}")
    return staged


def stage_npcs() -> list[Path]:
    staged: list[Path] = []
    npc_dir = CLIENT_DATA / "Npc"
    npc_ids = list(range(9031000, 9031017)) + list(range(1064000, 1064036))
    for npc_id in npc_ids:
        src = npc_dir / f"{npc_id}.img"
        dst = STAGING / "Npc" / src.name
        if copy_file(src, dst):
            staged.append(dst)
    log(f"[STAGE] npcs: {len(staged)}")
    return staged


def run_img2xml() -> None:
    classpath = f"{ORANGE_WZ_CLASSES};{ORANGE_WZ_DEPS / '*'}"
    out_dir = STAGING / "_xml_out"
    out_dir.mkdir(parents=True, exist_ok=True)
    cmd = [
        "java",
        "-cp",
        classpath,
        "orange.wz.BatchImg2Xml",
        str(STAGING),
        str(out_dir),
    ]
    log("[RUN] " + " ".join(cmd))
    result = subprocess.run(cmd, capture_output=True, text=True, encoding="utf-8", errors="replace")
    if result.stdout:
        log(result.stdout)
    if result.stderr:
        log(result.stderr)
    if result.returncode != 0:
        raise SystemExit(f"BatchImg2Xml failed with code {result.returncode}")


def install_converted() -> dict[str, int]:
    out_dir = STAGING / "_xml_out"
    counts = {"map": 0, "mob": 0, "npc": 0}

    for xml in out_dir.rglob("*.img.xml"):
        name = xml.name
        map_id = name.replace(".img.xml", "")
        rel_parts = set(xml.relative_to(out_dir).parts)

        if "Mob" in rel_parts:
            target = SERVER_WZ / "Mob.wz" / name
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(xml, target)
            counts["mob"] += 1
        elif "Npc" in rel_parts:
            target = SERVER_WZ / "Npc.wz" / name
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(xml, target)
            counts["npc"] += 1
        elif map_id.startswith("105200"):
            target = SERVER_WZ / "Map.wz" / "Map" / "Map1" / name
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(xml, target)
            counts["map"] += 1
        elif map_id == "910001000":
            target = SERVER_WZ / "Map.wz" / "Map" / "Map9" / name
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(xml, target)
            counts["map"] += 1
        else:
            log(f"[WARN] unclassified: {xml}")

    return counts


def verify() -> None:
    checks = [
        SERVER_WZ / "Map.wz" / "Map" / "Map1" / "105200000.img.xml",
        SERVER_WZ / "Map.wz" / "Map" / "Map9" / "910001000.img.xml",
        SERVER_WZ / "Mob.wz" / "8900000.img.xml",
        SERVER_WZ / "Mob.wz" / "8910000.img.xml",
        SERVER_WZ / "Mob.wz" / "8920000.img.xml",
        SERVER_WZ / "Mob.wz" / "8930000.img.xml",
        SERVER_WZ / "Npc.wz" / "9031000.img.xml",
        SERVER_WZ / "Npc.wz" / "1064012.img.xml",
    ]
    for path in checks:
        status = "OK" if path.exists() else "MISSING"
        log(f"[VERIFY] {status}: {path.name}")


def main() -> None:
    if not CLIENT_DATA.exists():
        raise SystemExit(f"Client Data not found: {CLIENT_DATA}")
    if not ORANGE_WZ_CLASSES.exists():
        raise SystemExit(f"orange-wz classes not found: {ORANGE_WZ_CLASSES}")

    ensure_dirs()
    stage_maps()
    stage_mobs()
    stage_npcs()
    run_img2xml()
    counts = install_converted()
    verify()
    log(f"[DONE] installed map={counts['map']} mob={counts['mob']} npc={counts['npc']}")


if __name__ == "__main__":
    main()
