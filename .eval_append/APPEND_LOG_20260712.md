# Append merge log — 2026-07-12 (V16.1 → Client_1)

## Source / Target

| | Path |
|---|------|
| SOURCE (V16.1) | `E:\mxd_soft\2.客户端\083\BeiDou-ClientV16.1\BeiDou-Client\Data` |
| TARGET (Client_1) | `E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data` |

## Phase summary

| Phase | Status | Count | Log |
|-------|--------|-------|-----|
| Pre-flight | DONE | fingerprints checked | `.eval_append/v16_append_run_20260712.log` |
| A — Whole-file append | **DONE** | **36,743** copied, 2 skipped | `.eval_append/v16_append_run_20260712.log` |
| B0 — Conflict scan | **DONE** | **44,816** conflicts | `.eval_append/v16_vs_client1_conflicts.txt` |
| B1 — character-conflicts | **DONE** | 39,151/39,151, +46,262 nodes, fail=1 | `gms-server/tools/_append_character_merge_v16.log` |
| B2 — other-conflicts | **DONE** | 4,913/4,913, +21,322 nodes, fail=0 | `gms-server/tools/_append_other_merge_v16.log` |
| C — export String.wz | **DONE** | zh=11, en=11, fail=0 | `gms-server/tools/_export_string_wz.log` |

## Phase A — copied by category

| Category | Files |
|----------|------:|
| Map | 15,913 |
| Character | 14,469 |
| Npc | 3,339 |
| Mob | 2,913 |
| Item | 89 |
| Etc | 9 |
| Effect | 5 |
| Morph | 4 |
| (root) | 2 |
| **Total** | **36,743** |

Client_1 Data 文件总数：86,491 → **123,234** (+36,743)

## Phase B — conflicts by category

| Category | Conflicts |
|----------|----------:|
| Character | 39,832 |
| Map | 2,643 |
| Mob | 1,142 |
| Npc | 663 |
| Item | 348 |
| Skill | 64 |
| Reactor | 47 |
| Etc | 19 |
| Morph | 16 |
| String | 15 |
| UI | 12 |
| Effect | 8 |
| **Total** | **44,816** |

## Fingerprints (post Phase A)

| Asset | Expected | Actual | Status |
|-------|----------|--------|--------|
| UIWindow.img | 12,240,903 | 12,240,903 | PASS |
| BasicEff.img | 3,992,551 | 4,108,738 | WARN (pre-existing, not touched) |
| DamageSkin.img | 70,058,716 | 70,058,716 | PASS |
| ijl15.dll | 654,848 | 684,544 | WARN (pre-existing, not touched) |

## Safety

- APPEND ONLY, SKIP strategy
- Excluded: UIWindow, BasicEff, DamageSkin, 0591/0592/590*, Base/
- orange-wz MCP: PID 25768 on port 10002 (verified 2026-07-12 13:16)
- B2+C orchestrator PID: **29076** (started 13:16)
- ETA B2: ~50–90 min (4,913 files, 16 workers)

## Pipeline (B2 + C) — COMPLETE

- **PID**: 29076 (exit=0, elapsed ~93 min)
- **B2**: 4,913/4,913 merge, +21,322 nodes, fail=0, 5547s
- **C**: zh=11 + en=11 String.wz export, fail=0, 33s
- **Export paths**:
  - `gms-server/wz/String.wz/` (EN)
  - `gms-server/wz-zh-CN/String.wz/` (中文)

```powershell
# Monitor logs (completed):
Get-Content gms-server/tools/_append_other_merge_v16.log -Tail 5
Get-Content gms-server/tools/_export_string_wz.log -Tail 5
Get-Content gms-server/tools/_run_all_merges.log -Tail 5
```

Orchestrator: `gms-server/tools/run_v16_append.py`  
Merge runner: `gms-server/tools/run_all_merges.py --log-suffix v16`

## New tools added

- `gms-server/tools/append_whole_files.py` — Phase A whole-file copy
- `gms-server/tools/gen_wz_conflicts.py` — conflict list generator
- `gms-server/tools/run_v16_append.py` — V16 pipeline orchestrator
- `append_img_nodes.py` — `--conflicts-file` support
- `run_all_merges.py` — `--source`, `--conflicts-file`, `--log-suffix`
