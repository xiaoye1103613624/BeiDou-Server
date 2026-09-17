# 实现要点与决策记录（Chat Emoticon）

## 1. 决策记录（2026-09-18 用户拍板）

| 决策点 | 结论 |
| --- | --- |
| 交付范围 | 服务端闭环 + 客户端资源落盘（客户端 C++ 源码补丁包不在本次范围） |
| 客户端资源写入 | 允许写入 `E:\MXD\BeiDou-Client_S9\Data`，写入前先备份 |
| 冷却逻辑 | **修正**移植包缺陷：冷却判断放在写入时间戳之前，被拒请求不影响下次可用 |

### 1.1 被修正的冷却缺陷

移植包原实现：

```java
final Long previous = LAST_USE.put(player.getId(), now);   // 先写入
if (previous != null && now - previous < COOLDOWN_MILLIS) {
    return;                                                // 被拒却已刷新时间戳
}
```

连续快点时每次都会刷新 `now`，导致间隔永远 < 1500ms，功能被永久拒绝（滑动窗口失效）。
现网实现改为 `get → 判断 → 通过才 put`，白名单与 1500ms 数值保持不变。

## 2. 客户端硬编码地址表（已静态校验）

目标：`E:\MXD\BeiDou-Client_S9\BeiDou.exe`，ImageBase `0x00400000`，`.text` RVA `0x001000` 大小 `0x6EF000`。

| 符号 | 地址 | 静态证据 |
| --- | --- | --- |
| `kStatusBarInstance` | `0x00BEC208` | .text 立即数引用 ≥8（如 `0x0045B78A`、`0x004906C4`） |
| `kUserLocalInstance` | `0x00BEBF98` | 引用 ≥8（如 `0x0043A914`、`0x0045BA55`） |
| `kUserPoolInstance` | `0x00BEBFA8` | 引用 ≥8（如 `0x0044170D`、`0x005293D2`） |
| `kClientSocketInstance` | `0x00BE7914` | 引用 ≥8（如 `0x00423EAE`、`0x0046BCDF`） |
| `kClientSocketSendPacket` | `0x0049637B` | `mov eax,0xA8126C; call 0xA60B98` + `push ecx/esi/edi; mov edi,ecx` |
| `kClientSocketProcessPacket` | `0x004965F1` | 前 8 字节 == `B8 B0 12 A8 00 E8 9D A5`（签名匹配） |
| `kCreateAnimLayer` | `0x0043EA3E` | `mov eax,0xA7A977; call 0xA60B98` + `sub esp,0x4C` |
| `kSetFont` | `0x0046341A` | `mov eax,0xA7CF5C; call 0xA60B98` + `push ebx/esi/edi` |
| `kUserGetVecCtrl` | `0x004AD42B` | `push ebp; mov ebp,esp; mov eax,[ecx+0x11A4]; mov [esi],eax` |
| `kUserPoolGetUser` | `0x009716ED` | `mov eax,[ecx+8]; mov edx,[esp+4]; cmp [eax+0x11A8],edx; ret 4` |
| `kPlayUiSound` | `0x00989588` | `mov eax,0xAE109C; call 0xA60B98` + `push 0x891` |

说明：`0xA60B98` 是客户端统一的运行时 wrapper（Nexon 代码封装），所有 Kaentake 使用的地址都位于
wrapper 之后的函数入口，调用语义正确。

调用约定核对（`chatemoticon.cpp` 声明 vs 反汇编）：

- `GetUserFn = void*(__thiscall*)(void*, unsigned int)` → 实测 `__thiscall` + `ret 4`（自清栈）✔
- `GetVecCtrlFn = IWzVector2DPtr*(__thiscall*)(void*, IWzVector2DPtr*)` → 实测 `[ecx+0x11A4]` 写入 `[ebp+8]` 出参 ✔
- `SendPacketFn = void(__thiscall*)(void*, const COutPacket&)` → 实测 `mov edi,ecx` 后使用 `edi` ✔

## 3. 服务端改动明细

| 文件 | 改动 |
| --- | --- |
| `RecvOpcode.java` | `CHAT_EMOTICON(0x11C)`（放在 `DAILY_CHECKIN(0x11A)` 之后；0x11B/0x11C 原为空缺） |
| `SendOpcode.java` | `CHAT_EMOTICON(0x17F)`（放在 `DAILY_CHECKIN(0x17C)` 之后；0x17D~0x17F 原为空缺） |
| `PacketProcessor.java` | `registerHandler(RecvOpcode.CHAT_EMOTICON, new ChatEmoticonHandler());`（import 为通配 `handlers.*`，无需新增 import） |
| `PacketCreator.java` | 新增 `chatEmoticon(int characterId, int emoticonId)`，紧跟 `dailyCheckinSnapshot` 之后 |
| `ChatEmoticonHandler.java` | 新增；校验顺序：角色/地图 → 载荷长度 → ID 白名单 → 冷却 → 广播 |
| `log_zh_CN.properties` / `log_en_US.properties` | 各新增 3 条 key |

`broadcastMessage` 语义核对（`MapleMap.java`）：

```java
public void broadcastMessage(Character source, Packet packet, boolean repeatToSource) {
    broadcastMessage(repeatToSource ? null : source, packet, Double.POSITIVE_INFINITY, source.getPosition());
}
```

`repeatToSource=true` → 排除源为 null → 全图含自己，符合「自己也看到头顶动画」。

## 4. 资源导入执行步骤（已执行）

```text
1. 确认 BeiDou.exe 未运行（tasklist 检查通过）
2. 备份 Data\UI\UIWindow.img -> UIWindow.img.bak_chatemoticon_20260918_065215
3. 拷贝 移植包 wz\Effect\ChatEmoticon.img -> Data\Effect\ChatEmoticon.img（SHA256 一致）
4. orange-wz MCP：
   load_files([移植包 wz\UI\UIWindow.img, live UIWindow.img])
   copy_paste_nodes(sources=[源:ChatExpression], targets=[live:根])
   save_as(live -> UIWindow.img.new)
   unload_all
5. os.replace(UIWindow.img.new, UIWindow.img)
6. 校验：一级节点 119 -> 120，仅新增 ChatExpression；ChatEmoticon.img 四类数量一致
```

注意点（踩坑记录）：

- MCP 的 `list_children` / `get_node_tree_json` 在 `autoParse=false` 时返回空 children（惰性解析），
  校验必须传 `autoParse=true`。
- `save_as` 不能回写已加载根路径，必须另存后再 `os.replace`。
- 扩展名非 `.img/.wz/.xml` 的文件（如 `.bak_xxx`）无法被 `load_files` 加载；比对旧版需复制成 `.img` 临时文件。
- 移植包 `resources\wz\UI.wz\UIWindow.img.xml` 无法被 MCP 解析（"IMG 解析失败"），改用同目录的二进制
  `wz\UI\UIWindow.img` 作为源。

## 5. 后续接入建议（ijl15 / ezorsia）

- 收包：`PacketDispatcher::RegisterHandler(0x17F, handler)`（与 Kaentake 的
  `RegisterCustomPacketHandler` 等价，见 `ezorsia/compat/PacketDispatcher.h`）。
- 发包：复用现有 `COutPacket` 通道写 `0x11C` + int。
- 资源路径与 Kaentake 完全一致（`Effect/ChatEmoticon.img/...`、`UI/UIWindow.img/ChatExpression/...`），
  本次已落盘，无需再导。
- 表情 ID 数组直接取移植包 `chatemoticon.cpp` 的 `kDynamic0~kDynamic3`（勿按连续数字生成）。
- 状态栏按钮偏移沿用 `ChatExpression/lbBtOpenOffset = (520, -60)`；当前客户端已有多分辨率补丁，
  需按 `ComputeButtonPosition()` 的算法以屏幕宽高推导（不要用 `root->Getrx()` 累加）。
