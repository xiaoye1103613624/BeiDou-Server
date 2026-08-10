# 开发规范记忆：客户端展示 vs 服务端校验

- **stamp**: `CLIENT_DISPLAY_VS_SERVER_AUTH_20260803`
- **date**: 2026-08-03
- **kind**: norm / architecture
- **tags**: performance, security, tip, inventory, anti-dupe, rate-limit, display-vs-auth
- **canonical_doc**: `resource_doc/开发文档/客户端展示与服务端校验-性能安全规范.md`
- **cursor_rule**: `.cursor/rules/client-display-server-auth.mdc`
- **vector**: ✅ 已写入 `session_lessons` → `resource_doc/agent_memory/session_chroma` + `D:\xy_vector_db\session_chroma`（ids: `session_norm_01` / `session_norm_02` / `session_correct_05`）
- **agentic**: `.agentic_sdlc/corpus/learnings/learning-display-vs-auth-20260803.yml` + `corpus/docs/` 全文镜像

## 摘要

服务端以同步+校验为主；展示能力下放客户端。关键属性变更时间点推送摘要；悬停零/少请求。经济与资源：服务端权威、限频日限、防伪造发包与防复制（意图≠结果、库存事务、幂等移物）。

## 防复制 / 资源限制（可执行要点）

1. 加减物只走 `InventoryManipulator`；脚本 `gainItem` 须空间/任务态/日限校验。
2. 客户端只发意图；结果由服务端算并用 `modifyInventory` 通知。
3. 移物：from/to 校验 + 操作锁 + 重放失败（源已空）。
4. 短时大量获得 → 审计日志 + 可熔断（踢线/锁交易/关脚本）。
5. 复制迹象 → 回滚 + 封禁标记 + 证据日志。
6. 展示 tip 不参与经济反作弊。

## 全文

见 canonical_doc（同仓库路径）。分段 JSONL：`dev_norms_display_vs_auth.jsonl`。
