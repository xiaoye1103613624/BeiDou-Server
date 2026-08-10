# 潜能 Hyper — 095 魔方 / 品阶 / 数值对齐

> 对照源：`E:\pro\_095_extract`（`Equip.renewPotential*`、`GameConstants.potentialIDFits`、`InventoryHandler.UseMagnify`、`PlayerStats`）  
> 本服：`PotentialRules095` / `PotentialHyperService` / `ItemOptionProvider` / `ItemOption.img.xml`

## 1. 流程（隐藏 → 鉴定 / 魔方）

| 步骤 | 095 | 本服（当前） |
|------|-----|--------------|
| 潜能卷成功 | `potential1=负品阶`，`potential2` 提示 2/3 线 | `setMainHidden(grade, lines)` |
| 放大镜 2460000~3 | `UseMagnify`：按装等档 roll optionId | `applyMagnify`：同档校验 + `rollOptions` |
| 魔方成功 | **只改隐藏态**（再放大镜） | **`CUBE_RESET_TO_HIDDEN=true`**：同 095 |
| 品阶卷 2049912 | 无此物（私服） | 升阶后同样 **隐藏待鉴定** |
| 附加魔方 2049911 | 无官方 Cash | **立即重随**（独立轨道） |

魔方 / 品阶卷提示：「未鉴定，请用放大镜」。开关：`PotentialHyperConfig.CUBE_RESET_TO_HIDDEN`。

## 2. 魔方权限与升阶率

| ID | 名称 | 可用品阶 | 升阶 |
|----|------|----------|------|
| 5062000（别名 2049910） | 奇迹 | 有潜能且 **非独特**；传说可用但 **压回独特** | 稀有→史诗 ~2%；其它 0.5%；最高独特 |
| 5062001 / 5062100 | 高级/珍贵 | 非传说；可独特 | 稀有 2%；其它 1%；最高独特；非三线时 ~2% 扩三线 |
| 5062002（别名 2049916） | 超级 | 任意已有主潜能 | **8%** 升一阶，可到传说 |
| 2049912 | 品阶提升（私服） | 已鉴定主潜能，&lt;传说 | 固定 +1，成功率随当前品阶递减 |
| 2049911 | 附加魔方 | 已有附加潜能 | 保附加 grade，立即重随 |

## 3. 选项 ID 段（品阶 → 数值族）

本服 grade 1~5 ↔ 095 state 5~8：

| 品阶 | state | 主档（首条 / 10% 二三条） | 降档（约 90% 二三条） |
|------|-------|---------------------------|------------------------|
| 稀有 | 5 | 1xxxx | 0xxxx |
| 史诗 | 6 | 2xxxx | 1xxxx |
| 独特 | 7 | 3xxxx | 2xxxx |
| 传说 | 8 | 4xxxx | 3xxxx |

**品阶不改 potLevel 索引**；高品阶靠抽到更高 ID 族。同后缀例（`ItemOption` lv10 STR）：

| optionId | lv10 incSTR |
|----------|-------------|
| 1 (0xxxx) | 5 |
| 10001 | 10 |
| 20001 | 12 |
| 30001 | 14 |
| 40001 | 18 |

## 4. 数值上下限（potLevel）

- 公式：`equipOptionLevel(req) = clamp(1..20, (req<=0?1:(req-1)/10+1))`（对齐 095 客户端 `(nrLevel-1)/10` 下标 ↔ WZ `level/N`）。
- 例：req 1~10→1，11~20→2，39→4，100→10。
- `computeBonus` / 洗潜过滤 / tip 均用同一索引查 `ItemOption.level.{n}`。
- 同一 optionId：装等越高 → level 越高 → 表内数值越大。
- 不同品阶：不同 optionId 族 → **同装等下 SS 线数值上限高于 B**。

旧式 `req/10` 已废弃（如 req=39 旧=3、现=4）。

显示俗称：C/B/A/S/SS（grade 1~5）；品阶池仍锁对称万段（**不**恢复 095 无上界）。

## 5. 刻意差异 / 已知点

1. **095 UseMagnify 把 state≥8 压成 7**：本服传说鉴定仍走 4xxxx（保留传说数值）。
2. **附加潜能 60xxx**：095 开源无完整 Cash 附加魔方；本服 2049911 立即重随。
3. **2049912 / 灵魂 / 星岩**：私服扩展，非 095 原样。
4. 魔方若需「立即出结果」，将 `CUBE_RESET_TO_HIDDEN=false`（数值规则不变）。

## 6. 验收建议

1. 潜能卷 → 隐藏 → 对应档放大镜 → 稀有多为 1xxxx。  
2. 奇迹：独特拒用；传说→独特隐藏。  
3. 超级：独特可 8%→传说；再鉴定应能出 4xxxx，数值高于同装等稀有。  
4. 同 optionId、req 100 vs 30：`computeBonus` 查 level10 vs level3，数值不同。
