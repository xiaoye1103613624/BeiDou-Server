# 头顶灯泡迁入侧边栏

> 状态：已闭环（服务端 + 脚本 + 插件已构建并同步客户端）  
> 适用客户端/协议版本：GMS v83 / BeiDou S9  
> 项目名称：BeiDou-Server_s9 + BeiDou-ijl15  
> 作者/日期：萧曵 / 2026-09-16  
> 产物目录：docs/features/overhead-icons-to-sidebar/

## 1. 背景与目标

- 将角色头顶 QuestAlert 灯泡、新手 Guide 精灵、场上 NPC QuestIcon 三类入口迁到 ijl15 右边栏。
- 场上头顶显示关闭；功能经侧边栏「任务提醒」与北斗助手同步。
- 世界地图任务标记保留。

## 2. 影响范围

| 层级 | 路径/模块 | 变更类型 |
| --- | --- | --- |
| 服务端 | `SidebarTools` TOOL_COUNT=11、`ClickGuideHandler`、`AbstractPlayerInteraction.spawnGuide`、`PlayerLoggedinHandler` | 改 |
| 脚本 | `xy/portal/任务提醒.js`、`北斗助手.js` | 新增/改 |
| 管理端 | 右边栏配置页（自动吃 TOOL_COUNT） | 无代码改 |
| 插件 | `sidetoolbar` 第 11 槽 + `overheadicons` hook | 新增/改 |
| DB | `V1.11.58__overhead_icons_to_sidebar.sql` | 新增 |

## 3. 最终实现逻辑

### 3.1 主流程

1. `game_config.server.replace_overhead_icons`（缺省 true）  
2. `spawnGuide(true)` 被抑制；登录发 `spawnGuide(false)`  
3. `ClickGuide` → `xy/portal/北斗助手`  
4. 侧边栏 toolIndex=10 → `xy/portal/任务提醒`  
5. 插件 hook：`CNpc::SetQuestList` 清空并强制无图标；`UpdateAutoQuestAlertIcon` 走清除分支  

### 3.2 协议

- 既有 `SIDEBAR_TOOL(0xC6)` / `SIDEBAR_CONFIG_SYNC(0x3733)`，count 可到 11。

### 3.3 回滚

- 配置 `replace_overhead_icons=false` 恢复 Guide 发包与 ClickGuide 原逻辑。  
- 插件默认仍屏蔽场上图标（编译期/运行默认 true）；需完整恢复时换旧 dll 或调 `OverheadIcons::SetSuppressFieldIcons(false)`。

## 4. 资源与同步

- 脚本：`scripts(-zh-CN)/BeiDouSpecial/xy/portal/` 与 `gms-server/scripts*` 已对齐。  
- 插件：`ijl15.dll` 已同步 `E:\MXD\BeiDou-Client_S9\ijl15.dll`。

## 5. 编码与 i18n

- 日志：`OverheadIcons.loginClearedGuide` / `OverheadIcons.spawnSuppressed`（zh-CN / en-US）。  
- 插件 tip：任务提醒相关 GBK 转义。

## 6. 产物清单

| 类型 | 路径 | 说明 |
| --- | --- | --- |
| 文档 | README.md | 本文件 |
| 证据 | evidence/ida-20260916.md | IDA 地址与偏移 |
| 构建 | builds/build-index.md | jar/dll 索引 |

## 7. 验证记录

- [x] ijl15 Release 构建成功 → `ezorsia/out/Release/ijl15.dll`
- [x] 已覆盖同步到 `BeiDou-Client_S9`
- [x] `mvn -pl gms-server -DskipTests package`（JDK 21.0.12.1）通过 → `gms-server/target/BeiDou.jar`
- [ ] 进游戏：无头顶灯泡/NPC 灯泡；侧边栏「任务提醒」可开；世界地图标记仍在

## 8. 预留、风险与回滚

- 屏蔽 NPC 头顶后依赖「任务提醒」与世界地图找任务。  
- 旧客户端 dll 不含第 11 槽时忽略 toolIndex=10。
