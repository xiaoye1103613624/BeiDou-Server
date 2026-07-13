import re
from pathlib import Path

src = Path(r"e:/pro/BeiDou-Server_xy/gms-server/tools/_drop_effect_work/BasicEff.dropaura.xml").read_text(
    encoding="utf-8"
)
dst_path = Path(r"e:/pro/BeiDou-Server_xy/gms-server/wz/Effect.wz/BasicEff.img.xml")
dst = dst_path.read_text(encoding="utf-8")


def extract_block(text, name):
    start = text.find(f'    <imgdir name="{name}">')
    if start < 0:
        raise SystemExit(f"missing {name} in src")
    depth = 0
    i = start
    while i < len(text):
        if text.startswith("<imgdir", i):
            depth += 1
            i = text.find(">", i) + 1
            continue
        if text.startswith("</imgdir>", i):
            depth -= 1
            i += len("</imgdir>")
            if depth == 0:
                return text[start:i].rstrip()
            continue
        i += 1
    raise SystemExit(f"unterminated {name}")


blocks = [extract_block(src, "dropItemAura"), extract_block(src, "dropItemEffect")]
for name in ("dropItemAura", "dropItemEffect"):
    start = dst.find(f'    <imgdir name="{name}">')
    if start < 0:
        continue
    end = start
    depth = 0
    i = start
    while i < len(dst):
        if dst.startswith("<imgdir", i):
            depth += 1
            i = dst.find(">", i) + 1
            continue
        if dst.startswith("</imgdir>", i):
            depth -= 1
            i += len("</imgdir>")
            if depth == 0:
                end = i
                break
            continue
        i += 1
    dst = dst[:start] + dst[end:]

insert = "\n" + "\n".join(blocks) + "\n"
idx = dst.rstrip().rfind("</imgdir>")
if idx < 0:
    raise SystemExit("no closing imgdir")
out = dst[:idx] + insert + dst[idx:]
dst_path.write_text(out, encoding="utf-8")
print("server xml updated", dst_path)
print("has dropItemAura", "dropItemAura" in out)
print("has dropItemEffect", "dropItemEffect" in out)
