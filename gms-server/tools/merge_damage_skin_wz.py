import re
from pathlib import Path

REQUIRED = ("NoRed0", "NoRed1", "NoCri0", "NoCri1")
SRC = Path(r"E:/mxd_soft/20dalu_079MS/20dalu/wz/Effect.wz/DamageSkin.img.xml")
SERVER = Path(r"E:/pro/BeiDou-Server_xy/gms-server/wz/Effect.wz/BasicEff.img.xml")

source = SRC.read_text(encoding="utf-8")
server = SERVER.read_text(encoding="utf-8")
if 'name="damageSkin"' in server:
    print("already merged")
    raise SystemExit(0)

# strip outer DamageSkin.img wrapper
m = re.search(r'<imgdir name="DamageSkin\.img">(.*)</imgdir>\s*$', source, re.S)
if not m:
    raise SystemExit("source wrapper not found")
inner_all = m.group(1)

# split top-level skin imgdirs
parts = re.split(r'(?=<imgdir name="\d+">)', inner_all)
blocks = []
for part in parts:
    part = part.strip()
    if not part.startswith('<imgdir name="'):
        continue
    if not all(x in part for x in REQUIRED):
        continue
    blocks.append(part)

if not blocks:
    raise SystemExit("no valid blocks")

payload = '<imgdir name="damageSkin">' + ''.join(blocks) + '</imgdir>'
merged = server.rstrip()
if merged.endswith('</imgdir>'):
    merged = merged[:-len('</imgdir>')] + payload + '</imgdir>'
else:
    raise SystemExit('unexpected server footer')

SERVER.write_text(merged, encoding='utf-8')
print('merged', len(blocks), 'skins; size', SERVER.stat().st_size)
