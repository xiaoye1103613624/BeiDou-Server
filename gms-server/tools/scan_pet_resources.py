# -*- coding: utf-8 -*-
"""Scan local high-version / custom pet resources for growth-system candidates."""
import re
import xml.etree.ElementTree as ET
from pathlib import Path

live = Path(r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data\Item\Pet")
bms_pet = Path(r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\玩法\BMS250\wz_tms\Item\Pet")
server_str = Path(r"E:\pro\BeiDou-Server_xy\gms-server\wz-zh-CN\String.wz\Pet.img.xml")
bms_str = Path(r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\玩法\BMS250\wz_tms\String\Pet.img.xml")
lunar = Path(
    r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\玩法\BMS250\wz_tms\Etc\LunarPetBuff.img.xml"
)
out = Path(r"E:\pro\BeiDou-Server_xy\gms-server\tools")

SHOP = {
    5000077,
    5000256,
    5000261,
    5000264,
    5000285,
    5000290,
    5000052,
    5000094,
    5000096,
    5000221,
    5000288,
    5000289,
    5000013,
    5000017,
    5000022,
    5000072,
    5000081,
    5000205,
    5000217,
    5000227,
    5000324,
    5000214,
    5000251,
    5000268,
}


def load_names(path):
    text = path.read_text(encoding="utf-8", errors="replace")
    return {
        int(m.group(1)): m.group(2)
        for m in re.finditer(
            r'<imgdir name="(500\d+)">\s*<string name="name" value="([^"]*)"', text
        )
    }


def pet_files(dirpath):
    m = {}
    if not dirpath.is_dir():
        return m
    for p in dirpath.iterdir():
        if not p.is_file():
            continue
        mm = re.match(r"(500\d+)", p.name)
        if mm and p.stat().st_size >= 5000:
            m[int(mm.group(1))] = p.stat().st_size
    return m


names = load_names(server_str)
for k, v in load_names(bms_str).items():
    names.setdefault(k, v)

live_map = pet_files(live)
bms_map = pet_files(bms_pet)

tree = ET.parse(lunar)
root = tree.getroot()
pet_list = root.find(".//imgdir[@name='petList']/imgdir[@name='0']")
ids_ordered = []
if pet_list is not None:
    for child in pet_list:
        if child.tag == "int":
            ids_ordered.append(int(child.get("value")))

chains = [ids_ordered[i : i + 3] for i in range(0, len(ids_ordered), 3) if i + 3 <= len(ids_ordered)]

lines = []
lines.append("=== LunarPetBuff evolve chains (groups of 3) ===")
lines.append(f"total pets in list: {len(ids_ordered)}, chains: {len(chains)}")
lines.append("")

ready = []
partial = []
for idx, ch in enumerate(chains, 1):
    parts = []
    ok = 0
    for stage, pid in enumerate(ch, 1):
        nm = names.get(pid, "?")
        in_live = pid in live_map
        in_bms = pid in bms_map
        src = "live" if in_live else ("bms" if in_bms else "MISSING")
        if in_live or in_bms:
            ok += 1
        sz = live_map.get(pid) or bms_map.get(pid) or 0
        parts.append(f"  L{stage} {pid} [{src} {sz // 1024}KB] {nm}")
    lines.append(f"Chain#{idx}")
    lines.extend(parts)
    lines.append("")
    if ok == 3:
        ready.append(ch)
    elif ok > 0:
        partial.append(ch)

high_live = sorted(i for i in live_map if i >= 5000400 and i not in SHOP)
high_named = sum(1 for i in high_live if i in names)
lines.append(f"READY (all 3 stages have assets): {len(ready)}")
lines.append(f"PARTIAL: {len(partial)}")
lines.append("")
lines.append("=== Resource summary ===")
lines.append(
    f"live client Pet imgs (>=5KB): {len(live_map)} total, high>=5000400 excl shop: {len(high_live)}"
)
lines.append(f"high with String name: {high_named}")
lines.append(f"BMS250 Pet imgs (>=5KB): {len(bms_map)}")
lines.append(
    f"BMS-only high: {len([i for i in bms_map if i >= 5000400 and i not in live_map])}"
)
lines.append(f"server/BMS String names >=5000400: {len([i for i in names if i >= 5000400])}")

tsv = ["chain\tstage\tid\tname\tsource\tsizeKB"]
for ci, ch in enumerate(ready, 1):
    for stage, pid in enumerate(ch, 1):
        src = "live" if pid in live_map else "bms"
        sz = (live_map.get(pid) or bms_map.get(pid) or 0) / 1024
        tsv.append(f"{ci}\t{stage}\t{pid}\t{names.get(pid, '?')}\t{src}\t{sz:.1f}")

# Full high inventory with names
inv = ["id\tname\tsource\tsizeKB"]
all_high = sorted(set(i for i in live_map if i >= 5000400) | set(i for i in bms_map if i >= 5000400))
for pid in all_high:
    if pid in SHOP:
        continue
    src = "live" if pid in live_map else "bms"
    sz = (live_map.get(pid) or bms_map.get(pid) or 0) / 1024
    inv.append(f"{pid}\t{names.get(pid, '?')}\t{src}\t{sz:.1f}")

(out / "pet_lunar_evolve_chains.txt").write_text("\n".join(lines), encoding="utf-8")
(out / "pet_lunar_ready_chains.tsv").write_text("\n".join(tsv), encoding="utf-8-sig")
(out / "pet_high_version_inventory.tsv").write_text("\n".join(inv), encoding="utf-8-sig")

print(f"ready_chains={len(ready)} partial={len(partial)} total_chains={len(chains)}")
print(f"high_excl_shop={len(high_live)} named={high_named} inv_rows={len(inv)-1}")
for ci, ch in enumerate(ready[:15], 1):
    ns = " -> ".join(f"{pid}:{names.get(pid, '?')}" for pid in ch)
    print(f"{ci}: {ns}")
print("...")
for ci, ch in enumerate(ready[-5:], len(ready) - 4):
    ns = " -> ".join(f"{pid}:{names.get(pid, '?')}" for pid in ch)
    print(f"{ci}: {ns}")
