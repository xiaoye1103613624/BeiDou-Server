# -*- coding: utf-8 -*-
"""Merge missing ItemOption nodes from richer source (KMS391) into BeiDou ItemOption.img.xml."""
import re
import shutil
from pathlib import Path

SRC = Path(r"F:\BaiduNetdiskDownload\冒险岛资料KMS391\手动搭建端（适合学习研究）\Kms391Server\wz\Item.wz\ItemOption.img.xml")
# resolve via short enumeration if path fails
if not SRC.exists():
    import subprocess
    out = subprocess.check_output(
        'cmd /c "dir /s /b F:\\BaiduNetdiskDownload\\冒险岛~1\\*ItemOption.img.xml"',
        shell=True, text=True, errors="ignore")
    SRC = Path(out.strip().splitlines()[0].strip())

TARGETS = [
    Path(r"E:\pro\BeiDou-Server_xy\gms-server\wz-zh-CN\Item.wz\ItemOption.img.xml"),
    Path(r"E:\pro\BeiDou-Server_xy\gms-server\wz\Item.wz\ItemOption.img.xml"),
]

# Match top-level option nodes: <imgdir name="000001">...</imgdir> nested carefully.
# ItemOption structure: root > optionId > (info + level). Depth of nesting varies.
# Strategy: find all <imgdir name="NNNNNN"> at first child of root by scanning with a stack.

def extract_options(xml):
    # type: (str) -> dict
    # strip root wrapper
    m = re.match(r'^(\s*<\?xml[^>]*>)?\s*<imgdir name="ItemOption\.img">(.*)</imgdir>\s*$', xml, re.S)
    if not m:
        # try without exact end
        start = xml.find('<imgdir name="ItemOption.img">')
        if start < 0:
            raise SystemExit("no ItemOption root")
        body = xml[start + len('<imgdir name="ItemOption.img">'):]
        # remove trailing </imgdir>
        body = re.sub(r'</imgdir>\s*$', '', body)
    else:
        body = m.group(2)

    opts = {}
    i = 0
    n = len(body)
    while i < n:
        mm = re.match(r'\s*<imgdir name="(\d+)">', body[i:])
        if not mm:
            i += 1
            continue
        name = mm.group(1)
        start = i + mm.start()
        # find matching close from after open tag
        pos = i + mm.end()
        depth = 1
        while pos < n and depth > 0:
            nxt_open = body.find("<imgdir", pos)
            nxt_close = body.find("</imgdir>", pos)
            if nxt_close < 0:
                break
            if nxt_open >= 0 and nxt_open < nxt_close:
                depth += 1
                pos = nxt_open + 7
            else:
                depth -= 1
                pos = nxt_close + len("</imgdir>")
        chunk = body[start:pos]
        try:
            oid = int(name)
        except ValueError:
            i = pos
            continue
        opts[oid] = chunk
        i = pos
    return opts

print("SRC", SRC, "exists", SRC.exists())
src_xml = SRC.read_text(encoding="utf-8", errors="ignore")
src_opts = extract_options(src_xml)
print("source options", len(src_opts), "40k+", sum(1 for k in src_opts if k >= 40000))

for dst in TARGETS:
    dst_xml = dst.read_text(encoding="utf-8", errors="ignore")
    dst_opts = extract_options(dst_xml)
    missing = sorted(set(src_opts) - set(dst_opts))
    print(dst.name, "had", len(dst_opts), "missing", len(missing),
          "miss40k", sum(1 for k in missing if k >= 40000))
    if not missing:
        continue
    bak = dst.with_suffix(dst.suffix + ".bak_pre_kms391")
    if not bak.exists():
        shutil.copy2(dst, bak)
        print("backup", bak)
    # rebuild: keep existing BeiDou nodes, append missing from source
    parts = [dst_opts[k] for k in sorted(dst_opts)]
    for k in missing:
        parts.append(src_opts[k])
    new_xml = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>' \
              + '<imgdir name="ItemOption.img">' + "".join(parts) + "</imgdir>\n"
    dst.write_text(new_xml, encoding="utf-8")
    print("wrote", dst, "bytes", dst.stat().st_size, "total opts", len(dst_opts) + len(missing))
