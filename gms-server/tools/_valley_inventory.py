# -*- coding: utf-8 -*-
from __future__ import annotations

import re
from pathlib import Path

SRC = Path(r"E:\资料\xiaoye\mxd学习\遗忘山谷")
DST = Path(r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data")
SW = Path(r"E:\pro\BeiDou-Server_xy\gms-server\wz-zh-CN")


def main() -> None:
    map0 = SRC / "wz" / "Map.wz" / "Map" / "Map0"
    osets, tiles, backs = set(), set(), set()
    for f in map0.glob("010006*.img.xml"):
        t = f.read_text(encoding="utf-8", errors="replace")
        osets.update(re.findall(r'<string name="oS" value="([^"]+)"', t))
        tiles.update(re.findall(r'<string name="tS" value="([^"]+)"', t))
        backs.update(re.findall(r'<string name="bS" value="([^"]+)"', t))
    print("oS", sorted(osets))
    print("tS", sorted(tiles))
    print("bS", sorted(backs))

    for name in ["101020000.img.xml", "105040305.img.xml"]:
        f = SRC / "wz" / "Map.wz" / "Map" / "Map1" / name
        t = f.read_text(encoding="utf-8", errors="replace")
        ids = sorted(set(re.findall(r"10006\d+", t)))
        print(name, "tm->", ids)
        for m in re.finditer(
            r'<string name="pn" value="([^"]+)"/>\s*'
            r'<int name="pt" value="(\d+)"/>\s*'
            r'<int name="x" value="(-?\d+)"/>\s*'
            r'<int name="y" value="(-?\d+)"/>\s*'
            r'<int name="tm" value="(\d+)"/>\s*'
            r'<string name="tn" value="([^"]+)"',
            t,
        ):
            if m.group(5).startswith("10006"):
                print("  portal", m.groups())

    # item ids in etc xml snippets
    for rel in ["Item.wz/Etc/0400.img.xml", "Item.wz/Etc/0403.img.xml"]:
        f = SRC / "wz" / rel
        if not f.exists():
            print("missing", rel)
            continue
        t = f.read_text(encoding="utf-8", errors="replace")
        ids = sorted(set(re.findall(r'<imgdir name="(40009\d+|40329\d+)">', t)))
        print(rel, "custom ids", ids)

    sw_sample = sorted((SW / "Map.wz" / "Map" / "Map0").glob("*.img.xml"))[:8]
    print("server Map0 sample", [p.name for p in sw_sample])
    print("server has 010006000", (SW / "Map.wz" / "Map" / "Map0" / "010006000.img.xml").exists())
    print("server has 10006000", (SW / "Map.wz" / "Map" / "Map0" / "10006000.img.xml").exists())


if __name__ == "__main__":
    main()
