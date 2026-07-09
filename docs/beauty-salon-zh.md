# 美容院（Beauty Salon）

> 适用：**GMS v083** 北斗服务端 + **BeiDou-ijl15** 客户端插件  
> **完整文档**：[萧曳冒险岛/美容院-完整实现指南.md](file:///E:/资料/xiaoye/mxd学习/萧曳冒险岛/美容院-完整实现指南.md)

## 1. 功能

| 能力 | 说明 |
|------|------|
| `@美容美发` | 打开美容院 UI（发型/脸型/肤色，各 6 槽） |
| 5920000 | Cash 解锁券，消耗后增加共享槽位数（最多 6） |
| Save / Apply / Delete | 保存、套用、删除造型快照 |

## 2. 服务端文件

| 路径 | 说明 |
|------|------|
| `org.gms.server.beauty.*` | BeautyData, BeautyStorage, BeautyPackets |
| `org.gms.net.server.channel.handlers.BeautyHandler` | 0x174 收包 |
| `org.gms.client.command.commands.gm0.BeautyCommand` | `@美容美发` |
| `db/migration/V1.11.5__beauty.sql` | 建表 + command |
| `db/migration/V1.11.6__beauty_command_rename.sql` | `beauty` → `美容美发` |

## 3. 协议

| 方向 | Opcode | 说明 |
|------|--------|------|
| C↔S | **0x174** | action/respType 首字节；Save/Apply/Delete/Unlock |

## 4. WZ（ContentRoot）

服务端 `gms-server/wz/` 须含：

- `Item.wz/Cash/0592.img.xml` → 5920000
- `String.wz/Cash.img.xml` + `wz-zh-CN/String.wz/Cash.img.xml`

客户端 Case C：`Data/Item/Cash/0592.img`、`Data/UI/New.img`（beautyRoom）。详见仓库外 [WZ-IMG-XML资源同步规范](file:///E:/资料/xiaoye/mxd学习/萧曳冒险岛/WZ-IMG-XML资源同步规范.md)。

## 5. 插件（ezorsia）

- `beautyshop/beautyshop.cpp` — Share 版 UI
- `beautyshop/BeautyShopBridge.cpp` — PacketDispatcher 0x174
- `damageskin/damageskinpicker.cpp` — 5920000 Cash 链式 hook（与 5910000 共存）

## 6. 构建与验证

```powershell
mvn -pl gms-server compile
# 插件 Release → ijl15.dll 565760B（含美容院）
```

游戏内：`@美容美发`、`!item 5920000`、Unlock → Save → Apply → Delete。

## 7. 参考

- [kaentake-compat-layer.md](./kaentake-compat-layer.md) — PacketDispatcher / LazyCompatInit
- [damage-skin-adaptation-zh.md](./damage-skin-adaptation-zh.md) — Case C Cash hook 范例
