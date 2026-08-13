# AGENTS.md

本文件是仓库级 Agent 指令入口，适用于整个仓库。具体规则不在此重复维护，请按以下顺序读取并遵守：

## 唯一操作根（S8）

| 用途 | 路径 |
|------|------|
| 服务端（本仓库） | `E:\pro\BeiDou-Server_S8` |
| 插件源码 | `E:\pro\BeiDou-ijl15_S8` |
| 客户端 live | `E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_S8` |

旧 `BeiDou-Server_xy` / `BeiDou-ijl15` / `BeiDou-Client_1` 不再作为日常操作目标。Cursor 规则见 `.cursor/rules/`（含 WZ MCP / IDA 强制规则）。

1. 必须读取 [`CLAUDE.md`](CLAUDE.md)，将其中的项目架构、开发命令、编码规范和注意事项视为所有编码 Agent 的共享指令；其中针对 “Claude” 的表述同样适用于当前 Agent。
2. 如果 [`CLAUDE.local.md`](CLAUDE.local.md) 存在，必须继续读取。它包含本机环境和个人工作流约定；与 `CLAUDE.md` 冲突时，以 `CLAUDE.local.md` 为准。
3. 如果子目录中存在更具体的 `AGENTS.md`，进入该目录工作时还须遵守子目录指令；冲突时以作用域更具体的指令为准。

`CLAUDE.local.md` 是可选且不提交的本地文件。文件不存在时，直接以 `CLAUDE.md` 为准，不应因此阻塞任务。
