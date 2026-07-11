# Append merge log — 2026-07-10

## Phase summary

| Phase | Status | Files | Nodes | Log |
|-------|--------|-------|-------|-----|
| String | DONE (prior) | 11 | 1039 | parent session |
| Robocopy whole files | DONE (prior) | 41134 | — | append_candidates.txt |
| item-cash | DONE | 49 | 21 | gms-server/tools/_append_item_cash.log |
| character-conflicts | RUNNING | 7208 | TBD | gms-server/tools/_append_character_merge.log |
| other-conflicts | QUEUED | 15724 | TBD | gms-server/tools/_append_other_merge.log |
| export String.wz | QUEUED (test OK) | 11×2 | — | gms-server/tools/_export_string_wz.log |

## Export paths (String test run verified)

- EN/base: `gms-server/wz/String.wz/*.img.xml` (from SOURCE ASM client)
- zh-CN: `gms-server/wz-zh-CN/String.wz/*.img.xml` (from TARGET merged client)

Files: Item, Cash, Consume, Eqp, Etc, Ins, Map, Mob, Npc, Pet, Skill

## Safety

SKIP strategy only. Excluded: UIWindow, BasicEff, DamageSkin, 0591/0592/590*.

## Monitor

```powershell
Get-Content gms-server/tools/_append_character_merge.log -Tail 5 -Wait
Get-Content gms-server/tools/_run_all_merges.log -Tail 10
```

Pipeline: `python -u gms-server/tools/run_all_merges.py`
