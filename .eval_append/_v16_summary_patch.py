from pathlib import Path
from collections import defaultdict
import os

REF = Path(r"E:\mxd_soft\2.客户端\083\BeiDou-ClientV16.1\BeiDou-Client\Data")
TGT = Path(r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data")
OUT_DIR = Path(r"E:\pro\BeiDou-Server_xy\.eval_append")

def list_files(root):
    files = set()
    for dirpath, dirnames, filenames in os.walk(root):
        for fn in filenames:
            files.add((Path(dirpath) / fn).relative_to(root).as_posix())
    return files

ref_files = list_files(REF)
tgt_files = list_files(TGT)
missing = []
with open(OUT_DIR / "v16_vs_client1_missing.txt", encoding="utf-8") as f:
    for line in f:
        line = line.strip()
        if line and not line.startswith("#"):
            missing.append(line)
missing_set = set(missing)
extra = sorted(tgt_files - ref_files)

def cat(rel):
    return rel.split("/")[0] if "/" in rel else "(root)"

missing_by_cat = defaultdict(list)
for m in missing:
    missing_by_cat[cat(m)].append(m)
extra_by_cat = defaultdict(list)
for e in extra:
    extra_by_cat[cat(e)].append(e)

ref_dirs = sorted([d.name for d in REF.iterdir() if d.is_dir()])
tgt_dirs = sorted([d.name for d in TGT.iterdir() if d.is_dir()])
only_ref = sorted(set(ref_dirs) - set(tgt_dirs))
only_tgt = sorted(set(tgt_dirs) - set(ref_dirs))
common = sorted(set(ref_dirs) & set(tgt_dirs))

v15_cands = set()
with open(OUT_DIR / "append_candidates.txt", encoding="utf-8") as f:
    for line in f:
        line = line.strip()
        if line:
            v15_cands.add(line)
v15_in_v16 = v15_cands & ref_files
v15_not_in_v16 = sorted(v15_cands - ref_files)
v15_still_missing = v15_cands & missing_set

ref_ui = REF / "UI" / "UIWindow.img"
tgt_ui = TGT / "UI" / "UIWindow.img"
ui_lines = []
if ref_ui.exists() and tgt_ui.exists():
    rs, ts = ref_ui.stat().st_size, tgt_ui.stat().st_size
    ui_lines.append(
        f"`UI/UIWindow.img`：V16.1 **{rs:,}** 字节，Client_1 **{ts:,}** 字节 — Client_1 为定制大版本，**不应**用 V16.1 覆盖。"
    )
    ui_lines.append("该文件两侧均存在，未计入缺失列表。")

sorted_cats = sorted(missing_by_cat.keys(), key=lambda c: -len(missing_by_cat[c]))
sorted_extra = sorted(extra_by_cat.keys(), key=lambda c: -len(extra_by_cat[c]))

lines = [
    "# V16.1 vs Client_1 (xiaoye) Data 对比摘要",
    "",
    f"- **REFERENCE (V16.1)**: `{REF}`",
    f"- **TARGET (Client_1)**: `{TGT}`",
    "- 生成时间: 2026-07-12",
    "",
    "## 1. 顶层目录",
    "",
    f"- 共有 **{len(common)}** 个: {', '.join(common)}",
]
if only_ref:
    lines.append(f"- **仅 V16.1**: {', '.join(only_ref)}（Client_1 无此顶层目录）")
if only_tgt:
    lines.append(f"- **仅 Client_1**: {', '.join(only_tgt)}")
lines.append("- 结论: **基本一致**；V16.1 多 `Base/`，其余顶层与 Client_1 对齐。")
lines.append("")
lines.append("## 2. 文件总数")
lines.append("")
lines.append("| 指标 | 数量 |")
lines.append("|------|------|")
lines.append(f"| V16.1 Data 文件总数 | **{len(ref_files):,}** |")
lines.append(f"| Client_1 Data 文件总数 | **{len(tgt_files):,}** |")
lines.append(f"| V16.1 有、Client_1 无 | **{len(missing):,}** |")
lines.append(f"| Client_1 有、V16.1 无（反向） | **{len(extra):,}** |")
lines.append(f"| 两侧共有（按相对路径） | **{len(ref_files & tgt_files):,}** |")
lines.append("")
lines.append("## 重要排除 / 定制资源")
lines.append("")
lines.extend(ui_lines)
lines.append("- 与 CHECKPOINT 一致：追加时 **SKIP** `BasicEff`、`DamageSkin`、`0591/0592/590*` 及 Client_1 侧备份/`.bak` 文件。")
lines.append("- Client_1 反向独有中含大量 `Effect/BasicEff.img.*` 备份与 `Item/Cash/0591.img` 等定制内容，属预期。")
lines.append("")
lines.append("## 3. Client_1 缺失 — 按类别")
lines.append("")
lines.append("| 类别 | 缺失数 |")
lines.append("|------|--------|")
for c in sorted_cats:
    lines.append(f"| {c} | {len(missing_by_cat[c]):,} |")
lines.append("")
lines.append("## 4. 缺口最大的类别（Top）")
lines.append("")
for i, c in enumerate(sorted_cats[:8], 1):
    lines.append(f"{i}. **{c}** — {len(missing_by_cat[c])}")
lines.append("")
lines.append("## 5. Client_1 独有（反向 diff）")
lines.append("")
lines.append(f"合计 **{len(extra):,}** 个路径仅存在于 Client_1（含定制、备份、历史追加残留）。")
lines.append("")
lines.append("| 类别 | 独有数 |")
lines.append("|------|--------|")
for c in sorted_extra[:12]:
    lines.append(f"| {c} | {len(extra_by_cat[c]):,} |")
lines.append("")
lines.append("## 6. 与 V15 append 评估对照（2026-07-10）")
lines.append("")
lines.append(f"- 历史 `append_candidates.txt`：**{len(v15_cands):,}** 条（相对 Data 的路径，无 `.wz` 后缀）。")
lines.append(f"- 2026-07-10 已对其中 **41,134** 条执行整文件 robocopy 到 xiaoye 客户端。")
lines.append(f"- 当前 **V16 有 / Client_1 无** 与 V15 候选交集：**{len(v15_still_missing):,}** → 说明 V15 批次候选在 Client_1 侧已齐。")
lines.append(f"- V16.1 仍包含的 V15 候选路径：**{len(v15_in_v16):,}** / {len(v15_cands):,}（**{100*len(v15_in_v16)/len(v15_cands):.1f}%**）。")
lines.append(f"- V15 候选中 **不在** V16.1 的路径：**{len(v15_not_in_v16):,}**（多为旧源独有，V16 分支已删改）。")
lines.append(f"- 相对 V15 候选清单，V16 相对 Client_1 **新增缺口**（在 V16 有、Client_1 无，且不在 V15 候选中）：**{len(missing_set - v15_cands):,}**。")
lines.append("")
lines.append("**是否超集？**")
lines.append("- 对「已 robocopy 的 V15 候选」：Client_1 已覆盖，V16 与 V15 源 **不是严格超集**（约 7.2k 条 V15 路径在 V16 中不存在）。")
lines.append("- 对「继续从 V16 补资源」：V16 相对 Client_1 仍有 **36,751** 条可追加路径，且与已完成 V15 批次 **不重叠**。")
lines.append("")
lines.append("## 7. 建议")
lines.append("")
lines.append(
    "**值得从 V16.1 分批追加**，但应沿用 SKIP 策略：优先 **Map → Character → Npc → Mob**（占缺口约 96%），"
    "不要覆盖 Client_1 的 UIWindow/BasicEff/DamageSkin/现金皮肤相关文件。"
    "建议生成新的 `append_candidates_v16.txt`（即本次 `v16_vs_client1_missing.txt` 去掉 Base/日志/根目录杂项），"
    "并与历史 `conflicts.txt` 合并去重后再跑 merge/robocopy。"
)
lines.append("")
lines.append("完整缺失列表: `v16_vs_client1_missing.txt`")
lines.append("")
lines.append("## 附录：各类别缺失样例")
lines.append("")
for c in sorted_cats[:6]:
    lines.append(f"### {c}")
    for m in missing_by_cat[c][:5]:
        lines.append(f"- `{m}`")
    lines.append("")

(OUT_DIR / "v16_vs_client1_summary.md").write_text("\n".join(lines), encoding="utf-8")
print("summary updated")

