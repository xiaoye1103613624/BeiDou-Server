# 会话修改教训索引

健康向量库：`resource_doc/agent_memory/session_chroma` + `D:\xy_vector_db\session_chroma`（collection `session_lessons`）
入库：`ingest_session_lessons_chroma.py`

| id | kind | summary | stamp |
|----|------|---------|-------|
| `session_correct_01` | correct | 1121015 as Aura Weapon buff: alert2, no hit, V effect kept; Java dropMessage on reject | `VSKILL_BUFF_1121015_20260728` |
| `session_correct_02` | correct | Promote locked live img: stop GameViewer stack, kill MCP java, MCP staging replace, restart ensure-mcp | `VSKILL_PROMOTE_20260727` |
| `session_correct_03` | correct | Pet/Cash missing packs: create_img → copy/paste → save; then rebuild server + restart client | `PET_CASH_MCP_20260728` |
| `session_correct_04` | correct | Harden getLong/unknown skill skip + fix illegal maxHP; rebuild/restart BeiDou.jar | `BOSS_SPAWN_HARDEN_20260728` |
| `session_note_01` | note | Server DB save treats -20/-51/-52..-55 equal; client PE ZRef only ~to -51 | `FIX_APPLY_SHADOW_DBL_OUTER_20260729` |
| `session_wrong_01` | wrong | V FX pilot cloned from 1121008 brandish attack — silent no cast on double-click | `VSKILL_BUFF_1121015_20260728` |
| `session_wrong_02` | wrong | orange-wz save_node reports OK but live .img mtime unchanged under GameViewer/MCP mmap lock | `VSKILL_PROMOTE_20260727` |
| `session_wrong_03` | wrong | MCP save_as to non-existent dest path fails for Pet/Cash pack deploy | `PET_CASH_MCP_20260728` |
| `session_wrong_04` | wrong | Ported boss XML with maxHP=?????? or unmapped MobSkill → spawn NPE / parseLong crash | `BOSS_SPAWN_HARDEN_20260728` |
| `session_wrong_05` | wrong | Expanding Boss HP digit UI to 21 digits broke bar width and info HUD | `BOSS_HP_UI_REVERT_20260728` |
| `session_norm_01` | note | 客户端展示vs服务端校验：展示少请求、变更点同步、悬停零/少请求；经济严校验 | `CLIENT_DISPLAY_VS_SERVER_AUTH_20260803` |
| `session_norm_02` | note | 资源限频+防伪造发包/防复制：意图≠结果；InventoryManipulator权威；日限审计熔断 | `CLIENT_DISPLAY_VS_SERVER_AUTH_20260803` |
| `session_correct_05` | correct | 成长tip最小优化：请求次数上限+退避；正缓存不重复发包；服务端build 3s短缓存 | `GROWTH_TIP_PERF_20260803` |
