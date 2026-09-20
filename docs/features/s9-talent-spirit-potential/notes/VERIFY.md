# 验收记录 — s9-talent-spirit-potential

日期：2026-09-17

## 自动检查

- `mvn -pl gms-server -DskipTests compile`（JDK 21）：通过（2026-09-17）

## 关键修复（闭环前缺口）

- `ItemFactory.INSERT_EQUIP_SQL`：VALUES 占位符由 52 校正为 51，与列清单 / `bindEquipColumns(1..51)` 对齐，避免装备存档参数未设置。

## 手工检查（需本地 MySQL + 服务启动）

1. 启动后确认 Flyway 执行 `V1.11.60`～`V1.11.63`。
2. 匠人街灵韵 NPC：觉醒 / 重置 / 描述；穿戴后技能窗有加成。
3. 潜能卷 / 魔方 / 放大镜；`!potential give|hyper|cube|magnify|clear`。
4. NPC `9031014`：阶层状态、学习、兑换/购买天赋书。
5. 存档下线再上：`inventoryequipment` 灵韵三列与潜能相关列有值；`xy_character_talent` 有行。
