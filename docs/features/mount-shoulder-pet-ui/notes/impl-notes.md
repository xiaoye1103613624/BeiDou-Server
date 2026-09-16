# 实现备注

- 决策：1A（BP20 留护肩）+ 2A（E:\MXD 资源）。
- 编译修复：`EquipSlotPos` 须定义在 `g_shoulderForcedXy` 之前。
- Pet 红槽：`g_petEquipPouchAliased` 不可用 thread_local（naked 绝对寻址）。
- 20260916b：−33 已是宠装时也置禁红标志，避免仅依赖 −133 别名。
