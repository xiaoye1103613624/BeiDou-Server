# Coloring Prism 验证清单

对照上游 `INTEGRATION_更新内容.md` §5。工程构建与 WZ 合并已完成；下列为实机冒烟（需启动服务端 + BeiDou-Client_S9）。

## 前置

1. Flyway 已跑 `V1.11.19`（tint 列 + `skilltints`）与 `V1.11.64`（DROP `coloring_prism_dye`）。
2. 客户端 `ijl15.dll` 与 `out/Release` 同哈希；`UIWindow.img` 含 `ColorPrism`。
3. Cash 栏有 `5782000`（七彩棱镜），名称非 null。

## 冒烟步骤

| # | 步骤 | 期望 | 结果 |
| --- | --- | --- | --- |
| 1 | 购买/领取棱镜 | Cash 栏图标+中文名「七彩棱镜」 | 待测 |
| 2 | 双击棱镜 | 开窗，预览行走 | 待测 |
| 3 | 拖任意装备入槽 | 图标出现；可拖色相 | 待测 |
| 4 | OK | 消耗 1 棱镜，场景内变色 | 待测 |
| 5 | 重登 | 颜色仍在 | 待测 |
| 6 | 第二角色旁观 | 可见染色 | 待测 |
| 7 | 三滑条归零再 OK | 还原；再 OK 不误耗 | 待测 |
| 8 | Hair / Skin | 无需拖入即可染 | 待测 |
| 9 | Eyes | 眨眼/表情后变色 | 待测 |
| 10 | Items 火焰芯片 | 有特效的点装可染 glow | 待测 |
| 11 | 501xxxx 现金特效 | 可染；剑芯片灰 | 待测 |
| 12 | 披风 ItemEff | 场景内光环同色且无需转身 | 待测 |
| 13 | Skills 拖入+预览 | 施放预览变色 | 待测 |
| 14 | 场景施放 | 自己/他人可见技能染色 | 待测 |
| 15 | 重登技能染色 | `skilltints` 持久 | 待测 |
| 16 | 多段弹道技能 | 整轮弹道染色（非仅前几发） | 待测 |

## 失败速查

- 双击无反应 → 门闸 `0x004863D5` / fusionanvil 分发
- 预览变色、他人不变 → `0x372F` 未进 `PacketDispatcher` 或未 `WeaponTint_Tick`
- 重登丢失 → `ItemFactory` 读写或外观列未进存档
- 仅 Eyes 无效 → face builder `0x00453696` 冲突
- 披风预览染、场景不染 → ItemEff Begin/End 或 Invalidate
