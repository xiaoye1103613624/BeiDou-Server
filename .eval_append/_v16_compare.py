import os
from pathlib import Path
from collections import defaultdict

REF = Path(r"E:\mxd_soft\2.客户端\083\BeiDou-ClientV16.1\BeiDou-Client\Data")
TGT = Path(r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data")
OUT_DIR = Path(r"E:\pro\BeiDou-Server_xy\.eval_append")
OUT_DIR.mkdir(parents=True, exist_ok=True)

def list_files(root):
    files = set()
    for dirpath, dirnames, filenames in os.walk(root):
        for fn in filenames:
            p = Path(dirpath) / fn
            rel = p.relative_to(root).as_posix()
            files.add(rel)
    return files

def top_dirs(root):
    return sorted([d.name for d in root.iterdir() if d.is_dir()])

def category(rel):
    parts = rel.split("/")
    if not parts:
        return "root"
    top = parts[0]
    if top.endswith(".wz"):
        top = top[:-3]
    return top

ref_dirs = top_dirs(REF)
tgt_dirs = top_dirs(TGT)
ref_files = list_files(REF)
tgt_files = list_files(TGT)

missing = sorted(ref_files - tgt_files)
extra = sorted(tgt_files - ref_files)

uiwindow_note = []
ref_ui = REF / "UI.wz" / "UIWindow.img"
tgt_ui = TGT / "UI.wz" / "UIWindow.img"
uiwindow_excluded = False
if "UI.wz/UIWindow.img" in missing:
    if tgt_ui.exists() and ref_ui.exists():
        rs, ts = ref_ui.stat().st_size, tgt_ui.stat().st_size
        if ts >= rs:
            missing = [m for m in missing if m != "UI.wz/UIWindow.img"]
            uiwindow_excluded = True
            uiwindow_note.append(
                f"Excluded UI.wz/UIWindow.img from missing: Client_1 {ts} >= V16.1 {rs} bytes"
            )

missing_by_cat = defaultdict(list)
for m in missing:
    missing_by_cat[category(m)].append(m)

extra_by_cat = defaultdict(list)
for e in extra:
    extra_by_cat[category(e)].append(e)

cand_path = OUT_DIR / "append_candidates.txt"
v15_cands = set()
if cand_path.exists():
    with open(cand_path, "r", encoding="utf-8", errors="replace") as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith("#"):
                continue
            line = line.replace("\\", "/")
            if "/Data/" in line:
                line = line.split("/Data/", 1)[1]
            elif line.lower().startswith("data/"):
                line = line[5:]
            v15_cands.add(line)

missing_set = set(missing)
v15_in_v16 = {p for p in v15_cands if p in ref_files}
v15_not_in_v16 = v15_cands - v15_in_v16
v15_cand_still_missing_v16 = v15_cands & missing_set
v16_only_missing = missing_set - v15_cands

missing_txt = OUT_DIR / "v16_vs_client1_missing.txt"
with open(missing_txt, "w", encoding="utf-8") as f:
    f.write("# V16.1 has, Client_1 missing\n")
    f.write(f"# REF: {REF}\n")
    f.write(f"# TGT: {TGT}\n")
    f.write(f"# Total missing: {len(missing)}\n\n")
    for m in missing:
        f.write(m + "\n")

cat_order = ["Character","Npc","Map","Mob","Item","String","UI","Effect","Skill","Sound","Etc","Quest","Reactor","Morph","TamingMob","Base"]

def cat_sort_key(c):
    try:
        return cat_order.index(c)
    except ValueError:
        return 999

only_ref = set(ref_dirs) - set(tgt_dirs)
only_tgt = set(tgt_dirs) - set(ref_dirs)
common = sorted(set(ref_dirs) & set(tgt_dirs))

lines = []
lines.append("# V16.1 vs Client_1 (xiaoye) Data 对比摘要\n")
lines.append(f"- **REFERENCE (V16.1)**: `{REF}`")
lines.append(f"- **TARGET (Client_1)**: `{TGT}`")
lines.append("- 生成时间: 2026-07-12\n")

lines.append("## 1. 顶层目录\n")
lines.append(f"- 共有 **{len(common)}** 个: {', '.join(common)}")
if only_ref:
    lines.append(f"- **仅 V16.1**: {', '.join(sorted(only_ref))}")
if only_tgt:
    lines.append(f"- **仅 Client_1**: {', '.join(sorted(only_tgt))}")
if not only_tgt and len(only_ref) <= 1:
    lines.append("- 结论: **基本一致**（除 V16.1 多出的顶层项外）\n")
else:
    lines.append("- 结论: **存在差异**\n")

lines.append("## 2. 文件总数\n")
lines.append("| 侧 | 文件数 |")
lines.append("|----|--------|")
lines.append(f"| V16.1 Data | **{len(ref_files):,}** |")
lines.append(f"| Client_1 Data | **{len(tgt_files):,}** |")
lines.append(f"| V16.1 有而 Client_1 无 | **{len(missing):,}** |")
lines.append(f"| Client_1 有而 V16.1 无（反向） | **{len(extra):,}** |\n")

if uiwindow_note:
    lines.append("## 重要排除说明\n")
    for n in uiwindow_note:
        lines.append(f"- {n}")
    lines.append("- 追加策略应与 CHECKPOINT 一致：保留 Client_1 定制 UIWindow / BasicEff / DamageSkin\n")

lines.append("## 3. Client_1 缺失 — 按类别\n")
lines.append("| 类别 | 缺失数 |")
lines.append("|------|--------|")
sorted_cats = sorted(missing_by_cat.keys(), key=lambda c: (-len(missing_by_cat[c]), cat_sort_key(c)))
for c in sorted_cats:
    lines.append(f"| {c} | {len(missing_by_cat[c]):,} |")
lines.append("")

lines.append("## 4. 缺口最大的类别（Top）\n")
for i, c in enumerate(sorted_cats[:8], 1):
    lines.append(f"{i}. **{c}**: {len(missing_by_cat[c]):,} 个文件")
lines.append("")

lines.append("## 5. Client_1 独有（反向 diff）按类别\n")
lines.append("| 类别 | 独有文件数 |")
lines.append("|------|------------|")
sorted_extra = sorted(extra_by_cat.keys(), key=lambda c: (-len(extra_by_cat[c]), cat_sort_key(c)))
for c in sorted_extra:
    lines.append(f"| {c} | {len(extra_by_cat[c]):,} |")
lines.append("")

lines.append("## 6. 与 V15 append 评估对照\n")
if v15_cands:
    pct = 100.0 * len(v15_in_v16) / len(v15_cands)
    lines.append(f"- 历史 `append_candidates.txt` 条数: **{len(v15_cands):,}**")
    lines.append(f"- 候选路径在 V16.1 中存在: **{len(v15_in_v16):,}** ({pct:.1f}%)")
    lines.append(f"- 候选路径不在 V16.1: **{len(v15_not_in_v16):,}**")
    lines.append(f"- 当前仍「V16 有 / Client_1 无」且曾在 V15 候选: **{len(v15_cand_still_missing_v16):,}**")
    lines.append(f"- V16 缺口但不在 V15 候选（V16 相对 V15 源的新增）: **{len(v16_only_missing):,}**")
    if pct >= 95:
        sup = "V16.1 基本覆盖 V15 追加候选所引用的路径（可视为超集或等价源）"
    else:
        sup = "V16.1 仅部分覆盖 V15 候选，需核对不在 V16 的候选路径"
    lines.append(f"- **结论**: {sup}")
else:
    lines.append("- 未找到 append_candidates.txt\n")

lines.append("\n## 7. 建议\n")
top3 = sorted_cats[:3]
top3_str = "、".join([f"{c}({len(missing_by_cat[c])})" for c in top3])
if len(missing) > 5000:
    rec = f"**值得分批从 V16.1 追加**。优先 {top3_str}；整文件 robocopy 候选可参考历史 41134 规模；合并冲突走 SKIP，保留定制资源。"
elif len(missing) > 500:
    rec = f"有 **中等规模** 缺口（{len(missing):,}），建议按类别增量追加并回归背包/签到/美容等定制功能。"
else:
    rec = f"缺口较小（{len(missing):,}），可按需补缺，不必全量同步 V16.1。"
lines.append(rec + "\n")

lines.append("## 附录：各类别缺失样例（最多 5 条）\n")
for c in sorted_cats[:6]:
    lines.append(f"### {c}\n")
    for m in missing_by_cat[c][:5]:
        lines.append(f"- `{m}`")
    lines.append("")

summary_path = OUT_DIR / "v16_vs_client1_summary.md"
with open(summary_path, "w", encoding="utf-8") as f:
    f.write("\n".join(lines))

print("REF files:", len(ref_files))
print("TGT files:", len(tgt_files))
print("Missing:", len(missing))
print("Extra:", len(extra))
print("Only ref dirs:", sorted(only_ref))
print("UIWindow excluded:", uiwindow_excluded)
for c in sorted_cats[:12]:
    print(f"  missing {c}:", len(missing_by_cat[c]))
print("V15 candidates:", len(v15_cands))
print("V15 in V16:", len(v15_in_v16))
print("Still missing from V15 cand:", len(v15_cand_still_missing_v16))
print("V16-only missing:", len(v16_only_missing))
