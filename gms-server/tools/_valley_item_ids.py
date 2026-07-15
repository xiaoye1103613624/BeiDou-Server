# -*- coding: utf-8 -*-
from __future__ import annotations

import re
from pathlib import Path

SRC = Path(r"E:\资料\xiaoye\mxd学习\遗忘山谷")


def main() -> None:
    for f in (SRC / "wz" / "Item.wz" / "Etc").glob("*.img.xml"):
        t = f.read_text(encoding="utf-8", errors="replace")
        ids = sorted(set(re.findall(r'<imgdir name="(4\d+)">', t)))
        custom = [i for i in ids if i.startswith("40009") or i.startswith("40329")]
        print(f.name, "custom", custom, "total_ids", len(ids))

    for f in [
        SRC / "wz" / "String.wz" / "Etc.img.xml",
        SRC / "wz-zh-CN" / "String.wz" / "Etc.img.xml",
        SRC / "wz" / "String.wz" / "Map.img.xml",
        SRC / "wz-zh-CN" / "String.wz" / "Map.img.xml",
        SRC / "wz" / "String.wz" / "Mob.img.xml",
        SRC / "wz" / "String.wz" / "Npc.img.xml",
    ]:
        t = f.read_text(encoding="utf-8", errors="replace")
        print(
            f.relative_to(SRC),
            "4000900",
            "4000900" in t,
            "map10006",
            bool(re.search(r"10006\d+", t)),
            "npc700",
            "700" in t[:500] or '<imgdir name="700">' in t,
        )
        maps = sorted(set(re.findall(r'<imgdir name="(10006\d+)">', t)))
        mobs = sorted(set(re.findall(r'<imgdir name="(5[4-9]|6[0-3]|70000[1-3])">', t)))
        npcs = sorted(set(re.findall(r'<imgdir name="(70[0-7]|800015)">', t)))
        items = sorted(set(re.findall(r'<imgdir name="(40009\d+|40329\d+)">', t)))
        if maps:
            print("  maps", maps)
        if mobs:
            print("  mobs", mobs)
        if npcs:
            print("  npcs", npcs)
        if items:
            print("  items", items)


if __name__ == "__main__":
    main()
