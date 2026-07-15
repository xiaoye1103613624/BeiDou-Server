# -*- coding: utf-8 -*-
"""Import missing high-version card mobs as visual clones of existing templates.

Uses KMS391 XML as optional metadata reference; copies a same-series BeiDou
client .img as placeholder sprites so drops/cards work until real art arrives.
Then emits Flyway SQL for remaining drop_data + monstercarddata rebuild.
"""
from __future__ import annotations

import shutil
from pathlib import Path

ROOT = Path(r"E:/pro/BeiDou-Server_xy")
MISSING = ROOT / "gms-server/tools/missing_mobs_high_version_cards.txt"
CLIENT_MOB = Path(r"E:/mxd_soft/2.客户端/083/BeiDou-ClientV16.1/BeiDou-Client/Data/Mob")
SERVER_MOB = ROOT / "gms-server/wz-zh-CN/Mob.wz"
MIGRATION = ROOT / "gms-server/src/main/resources/db/migration/V1.11.22__missing_monster_card_drops.sql"

# Prefer same-family templates when available
TEMPLATES = {
    2400: 2400000,
    2401: 2400100,
    2402: 2400200,
    2403: 2400000,
    2404: 2400000,
    2405: 2400000,
    2406: 2400000,
    8641: 8641000,
    8642: 8641000,
    8643: 8643000,
    8644: 8643000,
    8645: 8643000,
}


def pick_template(mid: int) -> Path:
    key = mid // 1000
    tid = TEMPLATES.get(key, 2400000)
    p = CLIENT_MOB / f"{tid}.img"
    if not p.exists():
        p = CLIENT_MOB / "2400000.img"
    return p


def main() -> None:
    rows: list[tuple[int, int]] = []
    for line in MISSING.read_text(encoding="utf-8").splitlines():
        if not line or not line[0].isdigit():
            continue
        a, b = line.split(",", 1)
        rows.append((int(a), int(b)))

    copied = 0
    for mid, _cid in rows:
        dst = CLIENT_MOB / f"{mid}.img"
        if dst.exists():
            continue
        src = pick_template(mid)
        shutil.copy2(src, dst)
        copied += 1
        print(f"COPY {src.name} -> {dst.name}")

    print(f"copied={copied} already={len(rows)-copied} total={len(rows)}")

    # SQL for drops (NOT EXISTS) + rebuild monstercarddata for range
    lines = [
        "-- Placeholder-enabled drops for previously missing card mobs.",
        "-- Mob visuals are temporary clones until real high-version art is imported.",
        "DELETE FROM monstercarddata WHERE cardid >= 2386030 AND cardid <= 2387256;",
    ]
    for mid, cid in rows:
        lines.append(
            "INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) "
            f"SELECT {mid}, {cid}, 1, 1, 0, 20000 FROM DUAL "
            f"WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid={mid} AND itemid={cid});"
        )
    lines += [
        "INSERT INTO monstercarddata (cardid, mobid)",
        "SELECT itemid, MIN(dropperid) FROM drop_data",
        "WHERE itemid >= 2386030 AND itemid <= 2387256",
        "GROUP BY itemid;",
        "",
    ]
    MIGRATION.write_text("\n".join(lines), encoding="utf-8")
    print("wrote", MIGRATION)


if __name__ == "__main__":
    main()
