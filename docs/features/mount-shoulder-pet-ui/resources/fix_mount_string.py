# -*- coding: utf-8 -*-
"""One-shot: set Eqp string name for mount 1902242."""
from pathlib import Path
import re

NAME_ZH = "飞天坐骑1902242"
NAME_EN = "Sky Mount 1902242"

pat = re.compile(
    r'(      <imgdir name="1902242">\s*<string name="name" value=")([^"]*)("/>\s*</imgdir>)',
    re.M,
)

zh = Path(__file__).resolve().parents[4] / "gms-server/wz-zh-CN/String.wz/Eqp.img.xml"
# parents: resources -> mount-shoulder-pet-ui -> features -> docs -> repo root
# Path: docs/features/mount-shoulder-pet-ui/resources/fix_mount_string.py
# parents[0]=resources, [1]=feature, [2]=features, [3]=docs, [4]=repo
text = zh.read_text(encoding="utf-8")
text2, n = pat.subn(rf"\g<1>{NAME_ZH}\g<3>", text, count=1)
print("zh replacements", n, "path", zh)
if n:
    zh.write_text(text2, encoding="utf-8", newline="\n")
    m = pat.search(text2)
    print("zh now", m.group(2) if m else None, m.group(2).encode("utf-8").hex() if m else None)

en = Path(__file__).resolve().parents[4] / "gms-server/wz/String.wz/Eqp.img.xml"
if en.exists():
    t = en.read_text(encoding="utf-8")
    t2, n2 = pat.subn(rf"\g<1>{NAME_EN}\g<3>", t, count=1)
    print("en replacements", n2)
    if n2:
        en.write_text(t2, encoding="utf-8", newline="\n")
