# MCP 接入说明（ida-pro-mcp / orange-wz）

> 本机两个 MCP 服务都是 **streamable HTTP**，已常驻运行。IDE 若未加载 MCP 工具，可直连。

## 服务地址

| 服务 | URL | 进程 |
| --- | --- | --- |
| `ida-pro-mcp` | `http://127.0.0.1:13337/mcp` | `ida.exe`（IDA Professional 9.4，`D:\software\IDA Professional 9.4`） |
| `orange-wz` | `http://127.0.0.1:10012/mcp` | `java`（`E:\project\orange-wz\target\...  orange.wz.OrangeWzApplication`） |

`C:\Users\11036\.cursor\mcp.json` 中已配置这两个服务（`type: http`）。
`C:\Users\11036\.codebuddy\mcp.json` 原为 `{"mcpServers": {}}` —— 需补入同样两段才能在 CodeBuddy 侧拿到原生 MCP 工具（写入需授权）。

## 直连方式

标准 MCP JSON-RPC over HTTP：

1. `POST` `initialize`（`protocolVersion: 2025-06-18`），从响应头取 `Mcp-Session-Id`
2. `POST` `notifications/initialized`（通知，无 id）
3. `POST` `tools/list` / `tools/call`，请求头带 `Mcp-Session-Id`、`Accept: application/json, text/event-stream`

响应可能是 `application/json` 或 SSE（`data:` 行）。

## 踩坑记录

1. **PowerShell 会吞掉 `$` 与 `\"`**：命令行里写脚本变量/JSON 会被破坏。脚本一律写文件再执行，参数用**文件**传而不是命令行字符串。
2. **Node 25 是 ESM**：临时脚本用 `.mjs` 时不能 `require('fs')`，改用 `import fs from 'node:fs'`。
3. **orange-wz 状态按 MCP 会话隔离**：`load_files` 后若进程退出，会话变孤儿但**仍持有路径独占锁**（报错 `根路径已被其它会话独占加载 ... holderSession=xxx`），且用 `Mcp-Session-Id` 复用也拿不回根节点。
   - 对策 A：**单个进程内完成 load + 全部查询**（推荐，用批量调用模式）
   - 对策 B：换一个新的副本路径再加载（治标，会累积孤儿会话，但不必重启 orange-wz，避免影响其中可能未保存的改动）
4. **`load_files` 的 `userKeyBase64` 不能为空**：必须由 `orange.wz.provider.WzAESConstant` 的 `WZ_GMS_IV`(4B) 与 `DEFAULT_KEY`(128B) 现算 base64。

## 复用资源

`E:\project\orange-wz` 内已有一批现成工具，改动 WZ 前先查是否已有：

- `orange/wz/MergeDropItemAuraFromWz.java`（本功能直接用）
- `orange/wz/MergeOneFile.java`、`BatchMergeNodes.java`、`MergeDamageRankFromWz.java` 等
