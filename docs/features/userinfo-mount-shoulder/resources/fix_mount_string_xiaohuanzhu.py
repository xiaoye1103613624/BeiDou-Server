# -*- coding: utf-8 -*-
"""Set Eqp string name for mount 1902242 to 小浣猪 (zh) / Hog Mount (en)."""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[4]
NAME_ZH = "小浣猪"
NAME_EN = "Hog Mount"

pat = re.compile(
    r'(      <imgdir name="1902242">\s*<string name="name" value=")([^"]*)("/>\s*</imgdir>)',
    re.M,
)

def patch(path: Path, name: str) -> None:
    text = path.read_text(encoding="utf-8")
    text2, n = pat.subn(rf"\g<1>{name}\g<3>", text, count=1)
    print(path.name, "replacements", n)
    if n:
        path.write_text(text2, encoding="utf-8", newline="\n")
        m = pat.search(text2)
        print("  now", m.group(2) if m else None, m.group(2).encode("utf-8").hex() if m else None)

patch(ROOT / "gms-server/wz-zh-CN/String.wz/Eqp.img.xml", NAME_ZH)
patch(ROOT / "gms-server/wz/String.wz/Eqp.img.xml", NAME_EN)
