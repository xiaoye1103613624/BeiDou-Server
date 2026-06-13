/**等级奖励领取脚本
 * 根据玩家等级可领取对应配置的奖励（道具、金币、点卷、抵用券等）
 * 每个角色每个等级奖励只能领取一次
 *
 *
 * ---By hanmburger
 */
var status = -1;
var rewards; // 玩家可领取的奖励列表
var RewardDO; // LevelRewardDO 类引用

// 判断是否为装备类物品（不可堆叠，需逐件发放）
// 冒险岛物品ID规则：1xxxxxx 为装备类
function isEquipment(itemId) {
    return itemId >= 1000000 && itemId < 2000000;
}

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }

    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0) {
        var playerLevel = cm.getPlayer().getLevel();

        // 查询所有启用的等级奖励配置（直连数据库，不走缓存）
        var allRewards;
        try {
            allRewards = Java.type('org.gms.config.LevelRewardManager').queryEnabledRewards();
        } catch (e) {
            cm.sendOk("等级奖励系统暂不可用，请联系管理员。");
            cm.dispose();
            return;
        }

        // 读取已领取记录
        var claimedStr = cm.getCharacterExtendValue("level_reward_claimed");
        var claimedList = [];
        if (claimedStr && claimedStr !== "null" && claimedStr !== "") {
            try {
                claimedList = JSON.parse(claimedStr);
            } catch (e) {
                claimedList = [];
            }
        }

        // 筛选：等级足够 且 未领取
        rewards = [];
        for (var i = 0; i < allRewards.size(); i++) {
            var r = allRewards.get(i);
            if (r.getLevel() <= playerLevel && claimedList.indexOf(r.getId()) === -1) {
                rewards.push(r);
            }
        }

        if (rewards.length === 0) {
            cm.sendOk("当前没有可领取的等级奖励！\r\n（已达标的奖励均已领取，或无可用的等级奖励配置）");
            cm.dispose();
            return;
        }

        // 构造菜单
        var text = "#e#b等级奖励领取#k#n\r\n";
        text += "当前等级：#rLv." + playerLevel + "#k\r\n\r\n";
        text += "请选择要领取的等级奖励：\r\n";
        for (var j = 0; j < rewards.length; j++) {
            var rw = rewards[j];
            var detail = "Lv." + rw.getLevel() + " — ";
            var hasContent = false;
            if (rw.getMeso() > 0) {
                detail += "金币×" + rw.getMeso() + " ";
                hasContent = true;
            }
            if (rw.getNxCredit() > 0) {
                detail += "点卷×" + rw.getNxCredit() + " ";
                hasContent = true;
            }
            if (rw.getMaplePoint() > 0) {
                detail += "抵用券×" + rw.getMaplePoint() + " ";
                hasContent = true;
            }
            if (rw.getNxPrepaid() > 0) {
                detail += "信用券×" + rw.getNxPrepaid() + " ";
                hasContent = true;
            }
            // 查询道具列表
            var items;
            try {
                items = Java.type('org.gms.config.LevelRewardManager').queryRewardItems(rw.getId());
            } catch (e) {
                items = null;
            }
            if (items && items.size() > 0) {
                for (var k = 0; k < items.size(); k++) {
                    detail += "#i" + items.get(k).getItemId() + "# ×" + items.get(k).getQuantity() + " ";
                    hasContent = true;
                }
            }
            if (!hasContent) {
                detail += "（空奖励）";
            }
            text += "#L" + j + "#" + detail + "#l\r\n";
        }
        cm.sendSimple(text);

    } else if (status == 1) {
        // 执行发放
        var chosen = rewards[selection];
        if (!chosen) {
            cm.sendOk("选择无效！");
            cm.dispose();
            return;
        }

        var meso = chosen.getMeso() || 0;
        var nxCredit = chosen.getNxCredit() || 0;
        var maplePoint = chosen.getMaplePoint() || 0;
        var nxPrepaid = chosen.getNxPrepaid() || 0;

        // 查询道具列表
        var rewardItems;
        try {
            rewardItems = Java.type('org.gms.config.LevelRewardManager').queryRewardItems(chosen.getId());
        } catch (e) {
            rewardItems = null;
        }

        // 发放金币
        if (meso > 0) {
            cm.gainMeso(meso);
        }

        // 发放点卷（NX_CREDIT=1）
        if (nxCredit > 0) {
            cm.getPlayer().getCashShop().gainCash(1, nxCredit);
        }

        // 发放抵用券（MAPLE_POINT=2）
        if (maplePoint > 0) {
            cm.getPlayer().getCashShop().gainCash(2, maplePoint);
        }

        // 发放信用券（NX_PREPAID=4）
        if (nxPrepaid > 0) {
            cm.getPlayer().getCashShop().gainCash(4, nxPrepaid);
        }

        // 发放道具
        if (rewardItems && rewardItems.size() > 0) {
            for (var i = 0; i < rewardItems.size(); i++) {
                var item = rewardItems.get(i);
                var itemId = item.getItemId();
                var qty = item.getQuantity() || 1;

                if (isEquipment(itemId)) {
                    // 装备类物品不可堆叠，需逐件发放
                    for (var j = 0; j < qty; j++) {
                        cm.gainItem(itemId, 1);
                    }
                } else {
                    // 可堆叠物品直接批量发放
                    cm.gainItem(itemId, qty);
                }
            }
        }

        // 记录已领取
        var claimedStr2 = cm.getCharacterExtendValue("level_reward_claimed");
        var claimedList2 = [];
        if (claimedStr2 && claimedStr2 !== "null" && claimedStr2 !== "") {
            try {
                claimedList2 = JSON.parse(claimedStr2);
            } catch (e) {
                claimedList2 = [];
            }
        }
        claimedList2.push(chosen.getId());
        cm.saveOrUpdateCharacterExtendValue("level_reward_claimed", JSON.stringify(claimedList2));

        // 构造结果消息
        var resultText = "#e#b领取成功！#k#n\r\n";
        resultText += "等级：#rLv." + chosen.getLevel() + "#k\r\n\r\n";
        if (meso > 0) resultText += "· 金币 ×" + meso + "\r\n";
        if (nxCredit > 0) resultText += "· 点卷 ×" + nxCredit + "\r\n";
        if (maplePoint > 0) resultText += "· 抵用券 ×" + maplePoint + "\r\n";
        if (nxPrepaid > 0) resultText += "· 信用券 ×" + nxPrepaid + "\r\n";
        if (rewardItems && rewardItems.size() > 0) {
            for (var k = 0; k < rewardItems.size(); k++) {
                var itm = rewardItems.get(k);
                resultText += "· #i" + itm.getItemId() + "# ×" + (itm.getQuantity() || 1) + "\r\n";
            }
        }
        cm.sendOk(resultText);
        cm.dispose();
    } else {
        cm.dispose();
    }
}
