# 潜能显示名：C / B / A / S / SS

> 用户确认 2026-08-04：显示改成玩家俗称；**品阶池保持有上界**（不恢复 095 无上界）。

| grade | 俗称 | 官名（仅日志/文档） | 095 state / 主池 |
|------|------|-------------------|------------------|
| 0 | C | 无 | — |
| 1 | C | 普通 | — |
| 2 | B | 稀有 | state5 · 0/1xxxx |
| 3 | A | 史诗 | state6 · 1/2xxxx |
| 4 | S | 独特 | state7 · 2/3xxxx |
| 5 | SS | 传说 | state8 · 3/4xxxx |

已改：
- `PotentialHyperService.gradeName` → C/B/A/S/SS
- tip 标题只显示字母级（`PotentialGradeLetterGbk`）
- `equipOptionLevel` 对齐 095 客户端 `(req-1)/10+1`
- 玩家 dropMessage 品阶用语

物品：逻辑相关 Consume/Cash 缺口已补 Item+缺省 String（不覆盖已有 String desc）。
