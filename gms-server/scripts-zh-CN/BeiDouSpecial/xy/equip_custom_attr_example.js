// 装备自定义属性示例脚本
// 展示如何通过脚本给装备添加自定义词条

function action(mode, type, selection) {
    var player = rm.getPlayer();

    // 示例：在位置-11（武器位）添加自定义属性
    var equipPos = -11;

    // 设置自定义属性：强化等级为10
    player.setEquipCustomAttr(equipPos, "强化等级", "10");

    // 设置多个自定义属性
    player.setEquipCustomAttr(equipPos, "锻造层数", "5");
    player.setEquipCustomAttr(equipPos, "品质等级", "传说");

    // 读取自定义属性
    var enhanceLevel = player.getEquipCustomAttr(equipPos, "强化等级");
    var forgeLevel = player.getEquipCustomAttr(equipPos, "锻造层数");

    player.dropMessage(1, "强化等级：" + enhanceLevel + "，锻造层数：" + forgeLevel);

    // 删除单个自定义属性
    // player.removeEquipCustomAttr(equipPos, "强化等级");

    // 清空所有自定义属性
    // player.clearEquipCustomAttr(equipPos);
}
