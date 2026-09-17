# 资源导入校验记录（orange-wz MCP）

时间：2026-09-18
工具：orange-wz MCP（`http://127.0.0.1:10012/mcp`，serverInfo `orange-wz-http-mcp v1.157.49`）
密钥：名称 `GMS`（iv/userKey 取自本机既有 wz 工具脚本，**不在本仓库留存**）

## 1. 导入前状态

| 检查项 | 结果 |
| --- | --- |
| `Data\Effect\ChatEmoticon.img` | 不存在 |
| `Data\UI\UIWindow.img` 的 `ChatExpression` | 不存在（`find_node` 报"节点路径不存在"） |
| `Data\UI\Basic.img` 的 `BtClose2` / `VScr` | **已存在**（一级节点 75 个，含 `BtClose`、`BtClose2`、`VScr`~`VScr8`、`HScr`~`HScr6`） |
| `BeiDou.exe` 进程 | 未运行（无文件占用） |

## 2. 文件头格式比对（决定"能否直接拷贝"）

| 文件 | 前 12 字节 |
| --- | --- |
| `Data\UI\UIWindow.img` | `73 F8 6C 77 FC 79 83 27 19 58 00 00` |
| `Data\UI\Basic.img` | `73 F8 6C 77 FC 79 83 27 19 58 00 00` |
| `Data\Effect\BasicEff.img` | `73 F8 6C 77 FC 79 83 27 19 58 00 00` |
| 移植包 `wz\Effect\ChatEmoticon.img` | `73 F8 6C 77 FC 79 83 27 19 58 00 00` |
| 移植包 `wz\UI\UIWindow.img` | `73 F8 6C 77 FC 79 83 27 19 58 00 00` |

→ 同源同格式，可跨文件拷贝 / 子树 paste。

## 3. 导入动作与结果

```text
copy_paste_nodes:
  sources = [{rootPath: 移植包 wz\UI\UIWindow.img, nodePath: "ChatExpression"}]
  targets = [{rootPath: Data\UI\UIWindow.img, nodePath: ""}]
  返回: ok=true, pasted=[{name: ChatExpression, type: LIST_PROPERTY}]

save_as: Data\UI\UIWindow.img -> Data\UI\UIWindow.img.new  (ok=true)
os.replace(.new -> live)
```

`ChatExpression` 子节点（合并后，live 内可见）：
`DialogBox`、`open`、`Tab`、`backgrnd`、`Dynamic`、`ltSlotDisplay`、`scroll:emote`、`lbBtOpenOffset`、`ptWindowOffset`

## 4. 导入后校验

### 4.1 UIWindow.img 一级节点集合对比

```text
BAK  : 119
LIVE : 120
added   : ['ChatExpression']
removed : []
```

→ 只新增目标子树，既有 119 个一级节点（Stat / Equip / Item / Skill / WorldMap / DamageSkin / DailyCheckin …）全部保留。

### 4.2 ChatEmoticon.img 结构

```text
Dynamic0 count=100  (100001 … 108027)   has 108027: True
Dynamic1 count=47   (200001 … 208003)
Dynamic2 count=120  (300120, 300001 … 300119)  has 300120: True
Dynamic3 count=3    (301000 … 301002)   has 301002: True
```

抽样帧节点：

```text
Dynamic0/100001/effect/0 -> CANVAS_PROPERTY
Dynamic2/300001/effect/0 -> CANVAS_PROPERTY
Dynamic3/301000/effect/0 -> CANVAS_PROPERTY
```

→ 与客户端 `kDynamic0(100) / kDynamic1(47) / kDynamic2(120) / kDynamic3(3)` 完全一致。

## 5. 校验和

| 文件 | 大小 | SHA256 |
| --- | --- | --- |
| 移植包 `wz\Effect\ChatEmoticon.img` | 9,438,350 | `4b6004ac55eca1efd3c51b41547acb2bd4bb3dce15055b0b79cd0e4460dc7196` |
| `Data\Effect\ChatEmoticon.img`（导入后） | 9,438,350 | `4b6004ac55eca1efd3c51b41547acb2bd4bb3dce15055b0b79cd0e4460dc7196` |
| 移植包 `wz\UI\UIWindow.img`（ChatExpression 源） | 9,969 | `c076116c5e02da79fcc66732491044e6fb9e6a26f209711680af1afcf110bfda` |
| `Data\UI\UIWindow.img`（合并后） | 12,485,763 | `80d26676b828fdcee8c435f7773c5937b32f7d5e3fc600218b89c1ae56caf92b` |
| `Data\UI\UIWindow.img`（导入前备份） | 12,476,680 | `6cf21f75661ed357da0319175980252f0f35297a885886dedc955770778c4005` |

## 6. 踩坑

- `autoParse=false` 时 `list_children` / `get_node_tree_json` 返回空 children（惰性解析）→ 校验必须 `autoParse=true`。
- `save_as` 禁止回写已加载根路径 → 另存 `.new` 后 `os.replace`。
- `.bak_*` 后缀无法被 `load_files` 解析 → 比对旧版需复制成 `.img` 临时文件（已清理）。
- 移植包 `resources\wz\UI.wz\UIWindow.img.xml` 解析失败（"IMG 解析失败: UIWindow.img.xml"）→ 改用二进制 `wz\UI\UIWindow.img`。
