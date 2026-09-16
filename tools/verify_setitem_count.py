# -*- coding: utf-8 -*-
from pathlib import Path
import re

xml = Path(r"E:/project/BeiDou-Server_s9/gms-server/wz/Etc.wz/SetItemInfo.img.xml").read_text(encoding="utf-8")
top = set(int(m) for m in re.findall(r"\n  <imgdir name=\"(\d+)\">", xml))
names = len(re.findall(r'name="setItemName"', xml))
print("top-level sets", len(top), "min", min(top), "max", max(top))
print("setItemName", names)
# ensure id 1 still Blizzard
m = re.search(r'<imgdir name="1">\s*<string name="setItemName" value="([^"]+)"', xml)
print("set1 name:", m.group(1) if m else None)
m4 = re.search(r'<imgdir name="4">\s*<string name="setItemName" value="([^"]+)"', xml)
print("set4 name:", m4.group(1) if m4 else None)
