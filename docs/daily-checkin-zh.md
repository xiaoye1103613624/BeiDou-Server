# 每日签到（Daily Check-in）

> 适用：**GMS v083** 北斗服务端 + **BeiDou-ijl15** 客户端插件  
> **完整文档**：[萧曳冒险岛/每日签到-完整实现指南.md](file:///E:/资料/xiaoye/mxd学习/萧曳冒险岛/每日签到-完整实现指南.md)

## 1. 功能

| 项 | 说明 |
|----|------|
| `@签到` / `@daily` | 手动打开签到窗口；冷却中提示剩余时间 |
| 登录自动弹窗 | 等级 ≥10 且当日可领时发送快照 |
| 28 天周期 | 7×4 格；24h 冷却；>48h 断签重置 |

## 2. 服务端文件

| 路径 | 说明 |
|------|------|
| `org.gms.server.dailycheckin.DailyCheckinRewards` | 奖励表（默认 mock 1 meso/天） |
| `org.gms.net.server.channel.handlers.DailyCheckinHandler` | Recv `0x11A` |
| `org.gms.client.command.commands.gm0.CheckinCommand` | `@签到` / `@daily` |
| `db/migration/V1.11.7__daily_checkin.sql` | `characters` 三列 + command_info |
| `org.gms.util.PacketCreator.dailyCheckinSnapshot` | Send `0x17C` |
| `Character.refreshCheckin` / `applyCheckinClaim` | streak 与持久化 |

## 3. 协议（Case C）

| 方向 | Opcode | 说明 |
|------|--------|------|
| C→S | **0x11A** | 领取等动作（空包或 action） |
| S→C | **0x17C** | 窗口快照：currentDay、claimedMask、28×icon、28×tooltip |

## 4. WZ / 客户端

- 源包：`E:\资料\xiaoye\mxd学习\每日签到\wz\UI.wz`（25,375 B）
- Case C **APPEND** `Data/UI/UIWindow.img` → `DailyCheckin/backgrnd`（513×346）；合并后 **12,185,287 B**
- 插件：`ezorsia/dailycheckin/` + `PacketDispatcher::RegisterHandler(0x17C)`；Release `ijl15.dll` **583,168 B**

## 5. 构建与验证

```bash
mvn compile -pl gms-server -am
```

Flyway 启动时应用 `V1.11.7`。联调：登录弹窗 → 领取 → `@签到` 重开 → 与美容院/伤害皮肤并存无冲突。
