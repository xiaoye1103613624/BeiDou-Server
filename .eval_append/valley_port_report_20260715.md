# 遗忘山谷 → BeiDou-Client_1 移植报告
时间: 2026-07-15

## 结论
绝大部分遗忘山谷资源此前已在 Client_1 / 服务端存在。本次按 append-only 仅补齐缺失的 Map\Obj（及 _Canvas）共 33 个文件；GMS 头一致，未做 Orz 转密。未触碰 Skill/Effect/.bad，未整文件覆盖 String/Quest/Sound/Map.Effect。

## 地图 ID（核心山谷）
| MapID | 名称（服务端/源 String） | 客户端 | 服务端 XML |
|-------|--------------------------|--------|------------|
| 10006000 | 浅层通道 / 遗忘空洞 | OK | OK |
| 10006001 | 洞穴精灵圣域 | OK | OK |
| 10006002 | 洞穴精灵百货商店 | OK | OK |
| 10006010 | 原始森林 I | OK | OK |
| 10006020 | 原始森林 II | OK | OK |
| 10006030 | 原始森林 III | OK | OK |
| 10006031 | 死亡峡谷 | OK | OK |
| 10006040 | 被遗忘的入口 I | OK | OK |
| 10006050 | 被遗忘的入口 II | OK | OK |
| 10006060 | 被遗忘的入口 III | OK | OK |
| 10006070 | 流光尽头 | OK | OK |
| 10006071 | 被遗忘的地牢隧道 | OK | OK |
| 10006080 | 禁忌入口 | OK | OK |
| 10006090 | 险峻下坡 I | OK | OK |
| 10006100 | 险峻下坡 II | OK | OK |
| 10006110 | 险峻下坡 III | OK | OK |
| 10006120 | 腐朽隧道 I | OK | OK |
| 10006121 | 别人的坟墓 | OK | OK |
| 10006152 | 往昔 | OK | OK |

入口联动图（已有传送门，非本次新增）:
- 101020000 魔法密林北部 → portal tm=10006060（Client portal/30，Source portal/0）
- 105040305 森林迷宫5 → portal tm=10006071（Client portal/8）

未移植: 000070000 / String 70000「假遗忘之谷」— 源 Data 无对应 .img，仅有 XML，属占位图。

## 本次新增文件（33）- Map\Obj\cash_preview.img
- Map\Obj\obj_acc1.img
- Map\Obj\obj_door.img
- Map\Obj\obj_effect.img
- Map\Obj\obj_event.img
- Map\Obj\obj_hotel.img
- Map\Obj\obj_house.img
- Map\Obj\obj_houseDR.img
- Map\Obj\obj_houseDW.img
- Map\Obj\obj_houseGS.img
- Map\Obj\obj_houseMC.img
- Map\Obj\obj_housePT.img
- Map\Obj\obj_houseSW.img
- Map\Obj\obj_houseVPT.img
- Map\Obj\obj_insideGS.img
- Map\Obj\obj_job.img
- Map\Obj\obj_login.img
- Map\Obj\obj_prop.img
- Map\Obj\obj_shop.img
- Map\Obj\obj_shop2.img
- Map\Obj\obj_signboard.img
- Map\Obj\obj_vehicle.img
- Map\Obj\_Canvas\etc.img
- Map\Obj\_Canvas\obj_acc1.img
- Map\Obj\_Canvas\obj_event.img
- Map\Obj\_Canvas\obj_hotel.img
- Map\Obj\_Canvas\obj_houseGS.img
- Map\Obj\_Canvas\obj_houseMC.img
- Map\Obj\_Canvas\obj_houseVPT.img
- Map\Obj\_Canvas\obj_insideGS.img
- Map\Obj\_Canvas\obj_login.img
- Map\Obj\_Canvas\obj_shop.img
- Map\Obj\_Canvas\obj_signboard.img

## 已存在（本次 SKIP）
- Map 全部山谷图 + Back(back_grassyCave/decayedCave) + Tile + 核心 Obj(obj_houseGC/insideGC/dungeon/trap/…)
- Mob 21/23/27/54–63/700001–700003
- Npc 700–707 / 800015
- Item Etc 0400（含 04000900+）/ 0403；String Map/Mob/Npc/Etc 已有山谷节点
- Sound Bgm03/Mob、Quest 整包、Map Effect/MapHelper/Physics — 按 lesson 禁止整文件覆盖

## 服务端 wz-zh-CN
Map/Mob/Npc/String/Item 山谷相关均已存在，无需再拷贝地图 XML。Quest 10600/10604 等仍缺失（说明中写 NPC/任务未完整实现）。

## 如何测试 / warp
1. 进游戏后 GM：`!warp 10006000`（或 10006060 / 10006070）
2. 走入口：魔法密林北部左下角 portal→10006060；森林迷宫5右下角→10006071
3. 抽样确认贴图：010006000 / 010006070 / 010006121 客户端 .img 可读（Orz 已能打开 info/life/back）
4. 不建议依赖任务 10600 链（服务端 Quest 节点未合入）

## 日志
- `.eval_append/valley_port_client_20260715.json`
- `.eval_append/valley_orz_check_20260715.json`