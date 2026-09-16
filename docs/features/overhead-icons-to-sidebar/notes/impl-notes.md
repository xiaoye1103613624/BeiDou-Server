# 实现笔记

## 决策回放

- 范围 A+B+C；呈现 2A（只留侧边栏）
- A 无独立自定义头顶 UI；角色头顶为原版 QuestAlert（`UpdateAutoQuestAlertIcon`）

## 服务端开关语义

`replace_overhead_icons` 缺省 true：

- 抑制 `spawnGuide(true)`
- 登录清 Guide
- ClickGuide → 北斗助手

## 插件默认

`OverheadIcons` 默认 suppress=true，与服务端缺省对齐；世界地图 QuestIcon 不动。
