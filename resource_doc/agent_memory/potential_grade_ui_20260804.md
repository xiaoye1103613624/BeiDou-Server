# 潜能品阶 UI（彩框 / Tip / 小徽章）2026-08-04

## 做了什么

1. **背包/装备栏彩框**（对齐 095 `DrawGradeFrame`）  
   - `PatchCall` 钩 `CUIItem::Draw@0x81DEE9`、`CUIEquip::Draw@0x8346F3`  
   - 读：隐藏红 / C灰 / B蓝 / A紫 / S黄 / SS绿  
   - **不用**旧 `PatchJmp`（曾背包闪退）

2. **Tip 微调**  
   - 分隔线改为实心白线（近 095）  
   - 词条子弹改用品阶色  
   - 标题旁画 C/B/A/S/SS 字母小徽章

3. **图标角标小徽章**  
   - 物品图标右上角字母牌（隐藏仅红点）

## 构建

- 脚本：`BeiDou-ijl15/_build_potential_grade_ui.bat`  
- Golden：`golden/ijl15.POTENTIAL_GRADE_UI_20260804.dll`  
- Stamp：`POTENTIAL_GRADE_UI_20260804`

## 验收

1. 有潜能装备在背包/装备栏应有彩框 + 角标  
2. 隐藏潜能：红框 + 红角点，无字母  
3. Tip：标题徽章 + 白分隔线 + 色子弹  
4. 开关背包不闪退（重点回归）
