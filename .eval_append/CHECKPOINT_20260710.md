# Checkpoint - 2026-07-10 (stable before Data append)

Saved before bulk WZ append/eval work. Use this as rollback reference for client fingerprints and known-good feature set.

## Git baseline

| Repo | Commit | Branch | Push |
|------|--------|--------|------|
| BeiDou-Server (BeiDou-Server_xy) | 781fbb332c | feature/dev_0.0.3 | ahead of origin (not pushed at checkpoint time) |
| BeiDou-ijl15 | 57c59bf | feature/dev_0.0.3 | not pushed |

## Working features (verified stable)

- Higher storage list - UI 463x478, 16 patches OK
- DamageSkin
- Bag
- DailyCheckin
- Synthesizing
- Beauty
- Storage bag

## Client fingerprints (preserve on append)

| Asset | Fingerprint / size marker |
|-------|-----------------------------|
| UIWindow.img | 12240903 |
| BasicEff | 3992551 |
| DamageSkin | 70058716 |
| ijl15.dll | 654848 bytes |

## Cash import

- Item 01102900 import reverted (intentionally rolled back)
- Supporting tools remain in repo (gms-server/tools/import_high_version_cash/, etc.)

## Append evaluation (pre-run stats)

| Metric | Value |
|--------|------|
| Append candidates | 41134 |
| Conflicts (skip) | 23095 |
| WZ tooling | orange-wz / OrzRepacker MCP required for merge/load/save |

Eval files:

- .eval_append/append_candidates.txt - 41134 lines
- .eval_append/conflicts.txt - 23095 lines

## MCP / tooling notes (2026-07-10 session)

### orange-wz (OrzRepacker MCP)

- Source tree: E:\pro\orange-wz
- start-mcp.bat launches D:\software\orzrepacker-v1.155.47\OrzRepacker.exe (echo mentions 10500 - stale; real port is 10002 per application.properties and README)
- Headless start used for this session:
  - CWD: E:\pro\orange-wz\target
  - Command: jre\bin\java -Dorange.gui.enabled=false -javaagent:OrzRepacker.jar -jar OrzRepacker.jar
  - Endpoint: http://127.0.0.1:10002/mcp
- Cursor mcp.json (C:\Users\Administrator\.cursor\mcp.json): ida-pro-mcp + beidou-build only - orange-wz NOT configured (add HTTP server url http://127.0.0.1:10002/mcp for native Cursor attach)

### beidou-build MCP

- Config points to E:\pro\BeiDou-Server_xy\tools\beidou-build-mcp\server.py
- File missing at checkpoint; Cursor mcps/user-beidou-build/STATUS.md reports errored
- Workaround: Maven/shell builds until server.py stub is added (Python mcp package not installed in Anaconda env)

## Next work

1. Keep fingerprints above after any append batch; re-verify 463x478 storage UI + 16 patches.
2. Run append pipeline against append_candidates.txt, skip paths in conflicts.txt.
3. Use orange-wz MCP on port 10002 for load/merge/save; document any port change in this file.

## Append session progress (2026-07-10 19:37 UTC+8)

### Completed phases

| Phase | Files | Nodes added | Log |
|-------|-------|-------------|-----|
| String (prior) | 11 imgs | 1039 | (parent session) |
| Whole-file robocopy | 41134 | — | append_candidates |
| Item/Cash conflicts | 49 | 21 | gms-server/tools/_append_item_cash.log |
| String export (test) | 11 zh + 11 en | — | gms-server/tools/_export_string_wz_test.log |

### Running (background PID via run_all_merges.py)

| Phase | Queue | Rate | Log |
|-------|-------|------|-----|
| Character conflicts | 7208 | ~6s/file (~12h) | gms-server/tools/_append_character_merge.log |
| Other conflicts (queued) | 15724 | after character | gms-server/tools/_append_other_merge.log |
| String export (queued) | 11 imgs | after other | gms-server/tools/_export_string_wz.log |

Pipeline runner: `python -u gms-server/tools/run_all_merges.py`  
Monitor: `Get-Content gms-server/tools/_append_character_merge.log -Tail 5 -Wait`

### Excluded (never overwrite)

- UI/UIWindow.img, Effect/BasicEff.img, Effect/DamageSkin.img
- Item/Cash/0591.img, 0592.img, 590*

### Script updates

- `append_img_nodes.py`: phases `character-conflicts`, `other-conflicts`, `--skip-done`, incremental log
- `export_string_wz.py`: MyImg2Xml export to wz/ + wz-zh-CN (classpath classes;lib/*)
- `run_all_merges.py`: character → other → export chain

## MCP runtime (verified 2026-07-10 18:37 UTC+8)

- Stale OrzRepacker v1.157.48 on 10002 returned HTTP 40001 on POST /mcp; stopped PID 27344.
- Active server: PID 39840, CWD E:\pro\orange-wz\target, 21 MCP tools (load_files, list_children, ...).
- Port 10500: not listening (start-mcp.bat echo is outdated).
