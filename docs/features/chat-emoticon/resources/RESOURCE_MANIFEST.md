# 资源清单（Chat Emoticon）

来源包：`E:\MXD\扩展改动\GMS083  Kaentake 聊天表情包移植文档`
目标客户端：`E:\MXD\BeiDou-Client_S9\Data`

## 1. 已导入

| 目标路径 | 来源 | 大小 | SHA256 | 状态 |
| --- | --- | --- | --- | --- |
| `Data\Effect\ChatEmoticon.img` | `wz\Effect\ChatEmoticon.img` | 9,438,350 | `4b6004ac55eca1efd3c51b41547acb2bd4bb3dce15055b0b79cd0e4460dc7196` | 新增（原不存在） |
| `Data\UI\UIWindow.img` ← `ChatExpression` 子树 | `wz\UI\UIWindow.img`（源 9,969 B） | 12,485,763 | `80d26676b828fdcee8c435f7773c5937b32f7d5e3fc600218b89c1ae56caf92b` | 合并（原 12,476,680 B） |

`ChatExpression` 子节点：`DialogBox`、`open`、`Tab`、`backgrnd`、`Dynamic`、`ltSlotDisplay`、`scroll:emote`、
`lbBtOpenOffset`、`ptWindowOffset`

## 2. 未导入（原因）

| 资源 | 原因 |
| --- | --- |
| `UI\Basic.img`（`BtClose2` / `VScr`） | 客户端 Data 已自带（一级节点 75 个，含 `BtClose2`、`VScr`~`VScr8`），覆盖反而有风险 |
| 服务端 `wz\Effect.wz\ChatEmoticon.img.xml` | 服务端不读贴图，只校验 ID 并广播（移植文档第 6.2 节） |
| `resources\wz\UI.wz\UIWindow.img.xml` | MCP 解析失败，且不允许整体覆盖客户端 `UIWindow.img`；改用二进制源子树合并 |

## 3. 备份与回滚

| 备份 | 路径 |
| --- | --- |
| 导入前 UIWindow.img | `E:\MXD\BeiDou-Client_S9\Data\UI\UIWindow.img.bak_chatemoticon_20260918_065215`（12,476,680 B，SHA256 `6cf21f75…`） |

回滚：

```powershell
Copy-Item "E:\MXD\BeiDou-Client_S9\Data\UI\UIWindow.img.bak_chatemoticon_20260918_065215" `
          "E:\MXD\BeiDou-Client_S9\Data\UI\UIWindow.img" -Force
Remove-Item "E:\MXD\BeiDou-Client_S9\Data\Effect\ChatEmoticon.img"
```

## 4. 多端/多语言同步评估

- 表情贴图为语言无关资源，仅需进 `Data\`（中文基础）。
- `EN\` 覆盖层只含文本类 img（String/Quest/UI 文本），本次无文案节点，无需同步。
- 若后续其它客户端目录（如 `BeiDou-ClientV16.1/17.7`）也要启用，需重复本清单第 1 节两项导入。
