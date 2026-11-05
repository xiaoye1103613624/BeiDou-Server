# 破功值角色化（A 方案）— 2026-11-05

> 用户裁决：破功值应取**当前角色的伤害上限**（角色属性），不是服务器设置的全局 cap。
> 证据：标题显示 `【破功值：9007199254740992】`（=2⁵³），源自 s9 把
> `Character.limitBreak` 占位成 `DamageLinesConfig.EXACT_INT_CAP`。

## 变更清单（对齐参考端 BeiDou-Server 口径）

| 文件 | 变更 |
| --- | --- |
| `org/gms/client/Character.java` | ① 字段区：`DEFAULT_LIMIT_BREAK=199999`、`MAX_LIMIT_BREAK=2.1e9`，字段默认改角色创角值（原 2⁵³ 占位注明历史）；② 新增 `setLimitBreak`（clamp+即时 `syncLimitBreak` 下行）、`gainLimitBreak`、`getDamage/getPGSXDJ/gainPGSXDJ` 脚本别名、`syncLimitBreak`（0x3730）；③ `fromCharactersDO` 装载 `limitBreak`（NULL→默认，越界夹取）；④ `saveCharToDB` JDBC UPDATE 追加 `limitBreak = ?`（参数 56，id 顺移 57） |
| `org/gms/dao/entity/CharactersDO.java` | 新增 `@Column("limitBreak") Long limitBreak` |
| `org/gms/scripting/AbstractPlayerInteraction.java` | 脚本 API：`getLimitBreak/getDamage/getPGSXDJ/setLimitBreak/gainLimitBreak/gainPGSXDJ` |
| `PlayerLoggedinHandler.java` | 注释修正（值=DB 角色属性，非内存 2⁵³）；下发点不变 |

## DDL（用户授权执行）

```sql
ALTER TABLE characters ADD COLUMN limitBreak BIGINT NOT NULL DEFAULT 199999 AFTER jailexpire;
```

已执行并核验：列存在（`bigint NO / default 199999`），现存角色 Admin/xiaoye 均回填 199999。

## 语义边界（与参考端一致，防误解）

- 服务端伤害 clamp **不变**：`AbstractDealDamageHandler` 仍用 `DamageLinesConfig.clampDamage`（全局 cap），
  long 伤害实验不受影响；参考端同样不在服务端用 limitBreak 夹伤害。
- `limitBreak` 的作用 = **0x3730 下行** → 客户端标题显示 + 客户端 `effective_cap` 取
  `min(配置 cap, 本值, 2^53)` 参与客户端侧饱和（`Damage.h` / `Mob.cpp` 唯一 clamp 点）。
- 变更即时生效：脚本 `setLimitBreak/gainLimitBreak` → `syncLimitBreak()` 立刻下发，不用重登。

## 验证

- `mvn -pl gms-server compile` exit 0；产物 class 内符号核验：
  `setLimitBreak/syncLimitBreak/MAX_LIMIT_BREAK`（Character.class）、`limitBreak`×4（CharactersDO.class）、
  `gainPGSXDJ`（AbstractPlayerInteraction.class）均在。
- `mvn package` exit 0 → `gms-server/target/BeiDou.jar`（07:51，117MB）。
- **待办**：当前运行中的服务端进程（java PID 44760，933MB）仍是旧 jar——需重启后新语义生效。
  重启属你的运维动作（进程命令行在本环境不可读，不盲杀）；重启后登录任一角色，标题应显示
  `【破功值：199999】`，脚本改值即时刷新。

## 预留

- 破功道具触发链（参考端 `useLimitBreakItem` + `getLimitBreakSuccess`）未移植：s9 无对应道具脚本注册，
  需要时以 `pm.gainLimitBreak(inc)` 脚本 API 即可实现同款成长。
