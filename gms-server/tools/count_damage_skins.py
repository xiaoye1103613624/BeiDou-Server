import re
from pathlib import Path
path = Path(r"E:/pro/BeiDou-Server_xy/gms-server/wz/Effect.wz/BasicEff.img.xml")
text = path.read_text(encoding="utf-8")
match = re.search(r'name="damageSkin">(.*)</imgdir>\s*$', text, re.S)
block = match.group(1) if match else ""
ids = re.findall(r'<imgdir name="(\d+)">', block)
required = ("NoRed0", "NoRed1", "NoCri0", "NoCri1")
valid = 0
for sid in set(ids):
    part = re.search(r'<imgdir name="%s">(.*?)</imgdir>' % sid, block, re.S)
    if part and all(name in part.group(1) for name in required):
        valid += 1
print("skins_total", len(set(ids)))
print("skins_valid", valid)
print("file_bytes", path.stat().st_size)
