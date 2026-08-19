import json
import re
from pathlib import Path

base = Path(r"F:\MXD_dev\BeiDou-Server\gms-server\tools\_wz_xml_fix")
out = base / "child_names"
out.mkdir(parents=True, exist_ok=True)
zh = Path(r"F:\MXD_dev\BeiDou-Server\gms-server\wz-zh-CN")


def tree_sets(js_path):
    with open(js_path, "r", encoding="utf-8") as f:
        d = json.load(f)
    results = d.get("results")
    sets = {}
    if results:
        for r in results:
            tree = r.get("tree") or r
            rp = r.get("rootPath") or tree.get("rootPath")
            names = {c["name"] for c in (tree.get("children") or [])}
            tag = "src" if rp and "扩展改动" in rp else "live"
            leaf = Path(rp).stem
            sets[(tag, leaf)] = names
            (out / f"{tag}_{leaf}_tree.txt").write_text("\n".join(sorted(names)), encoding="utf-8")
        return sets
    tree = d.get("tree") or d
    return {("one", tree.get("name")): {c["name"] for c in (tree.get("children") or [])}}


def xml_top_ids(path: Path):
    ids = []
    depth = 0
    open_re = re.compile(r"<imgdir[\s>]")
    self_re = re.compile(r"<imgdir [^>]*/>")
    close_re = re.compile(r"</imgdir>")
    name_re = re.compile(r'<imgdir name="([^"]+)"')
    for line in path.read_text(encoding="utf-8").splitlines():
        opens = len(open_re.findall(line))
        selfs = len(self_re.findall(line))
        closes = len(close_re.findall(line))
        m = name_re.search(line)
        if m and "/>" not in line[m.end() : m.end() + 3]:
            if depth == 1:
                ids.append(m.group(1))
        depth += opens - selfs - closes
    return ids


sets = tree_sets(
    r"C:\Users\Administrator\.cursor\projects\f-MXD-dev\agent-tools\2ea12ad9-ddf8-4c00-a1c0-745a1dd9cae6.txt"
)
for k, v in sets.items():
    print(k, len(v))

for name in ["QuestInfo", "Act", "Check", "Say"]:
    p = zh / "Quest.wz" / f"{name}.img.xml"
    ids = xml_top_ids(p)
    (base / f"xml_top_{name}.txt").write_text("\n".join(ids), encoding="utf-8")
    print(name, "xml top", len(ids), "first", ids[:3])
