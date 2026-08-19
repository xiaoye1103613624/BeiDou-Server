from pathlib import Path

base = Path(r"F:\MXD_dev\BeiDou-Server\gms-server\tools\_wz_xml_fix")
out = base / "child_names"
batch_dir = base / "copy_batches"
batch_dir.mkdir(exist_ok=True)

SRC_Q = r"F:\MXD_dev\扩展改动\妖精学院+列娜海峡\Data\Quest"
SRC_S = r"F:\MXD_dev\扩展改动\妖精学院+列娜海峡\Data\String"


def load_names(p: Path):
    return [x for x in p.read_text(encoding="utf-8").splitlines() if x.strip()]


pairs = [
    ("QuestInfo", out / "live_QuestInfo_tree.txt", out / "src_QuestInfo_tree.txt", SRC_Q + r"\QuestInfo.img", ""),
    ("Act", out / "live_Act_tree.txt", out / "src_Act_tree.txt", SRC_Q + r"\Act.img", ""),
    ("Check", out / "live_Check_tree.txt", out / "src_Check_tree.txt", SRC_Q + r"\Check.img", ""),
    ("Say", out / "live_Say_tree.txt", out / "src_Say_tree.txt", SRC_Q + r"\Say.img", ""),
    ("Etc", out / "live_Etc_Etc.txt", out / "src_Etc_Etc.txt", SRC_S + r"\Etc.img", "Etc/"),
    ("Npc", out / "live_Npc_tree.txt", out / "src_Npc_tree.txt", SRC_S + r"\Npc.img", ""),
]

# live Npc from earlier dump named live_Npc_root.txt
if not (out / "live_Npc_tree.txt").exists() and (out / "live_Npc_root.txt").exists():
    pairs[-1] = (
        "Npc",
        out / "live_Npc_root.txt",
        out / "src_Npc_tree.txt",
        SRC_S + r"\Npc.img",
        "",
    )

summary = []
for name, live_p, src_p, src_root, prefix in pairs:
    live = set(load_names(live_p))
    src = set(load_names(src_p))
    miss = sorted(src - live, key=lambda x: (len(x), x))
    (base / f"copy_miss_{name}.txt").write_text("\n".join(miss), encoding="utf-8")
    summary.append(f"{name}: live={len(live)} src={len(src)} miss={len(miss)}")
    batch = []
    n = 80
    for i in range(0, len(miss), n):
        chunk = miss[i : i + n]
        sources = [{"rootPath": src_root, "nodePath": prefix + nid} for nid in chunk]
        batch.append(sources)
    import json

    (batch_dir / f"{name}.json").write_text(json.dumps(batch, ensure_ascii=False), encoding="utf-8")
    print(summary[-1], "batches", len(batch))

(base / "copy_summary.txt").write_text("\n".join(summary), encoding="utf-8")
