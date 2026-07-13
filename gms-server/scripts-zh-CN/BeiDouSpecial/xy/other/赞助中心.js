/*
    赞助中心 — 查看赞助金额、领取赞助档位奖励（每档限领一次）
    同账号不同角色赞助额互相隔离
**/
var status = 0;
var sponsorConfigs = [];   // [{id, amount, rewards}]
var totalSponsor = 0;      // 当前角色累计赞助金额

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) { cm.dispose(); return; }
    if (mode == 0 && status >= 0) { cm.sendOk("欢迎再来！"); cm.dispose(); return; }
    if (mode == 1) status++; else status--;

    if (status == 0) {
        // ===== 加载赞助数据 =====
        try {
            var ServerManager = Java.type('org.gms.manager.ServerManager');
            var SponsorService = Java.type('org.gms.service.SponsorService');
            var context = ServerManager.getApplicationContext();
            var service = context.getBean("sponsorService");

            // 获取角色赞助记录
            var record = service.getRecordByPlayerId(cm.getPlayer().getId());
            totalSponsor = record != null ? record.getTotalSponsor() : 0;

            // 获取所有启用配置
            var configs = service.listConfigs();
            sponsorConfigs = [];
            for (var i = 0; i < configs.size(); i++) {
                var c = configs.get(i);
                sponsorConfigs.push({
                    id: c.getId(),
                    name: c.getName(),
                    amount: c.getAmount(),
                    rewards: c.getRewards()
                });
            }
        } catch (e) {
            cm.sendOk("系统错误，请联系管理员。\r\n" + e.getMessage());
            cm.dispose(); return;
        }

        // ===== 显示赞助面板 =====
        var text = "#e#d赞助中心#n#k\r\n\r\n";
        text += "当前累计赞助额：#r" + totalSponsor + "元#k\r\n";
        text += "#d赞助额通过CDK兑换累积，角色独立计算。#k\r\n\r\n";
        text += "#e可领取的赞助奖励：#n\r\n\r\n";

        if (sponsorConfigs.length == 0) {
            text += "暂无赞助奖励配置。\r\n";
            cm.sendOk(text);
            cm.dispose(); return;
        }

        for (var i = 0; i < sponsorConfigs.length; i++) {
            var sc = sponsorConfigs[i];
            var claimed = isClaimed(sc.id);
            var canClaim = totalSponsor >= sc.amount;

            text += "#L" + i + "#";
            if (claimed) {
                text += "#d[已领取]#k ";
            } else if (canClaim) {
                text += "#b[可领取]#k ";
            } else {
                text += "#r[未达标]#k ";
            }
            text += "#e" + (sc.name || ("赞助满" + sc.amount + "元")) + "#n  (满#b" + sc.amount + "元#k)";
            text += "  →  ";
            // 显示奖励概要
            var rewardText = getRewardSummary(sc.rewards);
            text += rewardText;
            text += "#l\r\n";
        }

        text += "\r\n#e#r当前金币：#b" + cm.getPlayer().getMeso() + "#k";
        cm.sendSimple(text);

    } else if (status == 1) {
        // ===== 领取所选奖励 =====
        var idx = selection;
        if (idx < 0 || idx >= sponsorConfigs.length) { cm.dispose(); return; }
        var sc = sponsorConfigs[idx];

        // 检查是否已领取
        if (isClaimed(sc.id)) {
            cm.sendOk("该档奖励已经领取过了！");
            cm.dispose(); return;
        }

        // 检查赞助额
        if (totalSponsor < sc.amount) {
            cm.sendOk("赞助金额不足！\r\n当前累计：#r" + totalSponsor + "元#k\r\n需要：#r" + sc.amount + "元#k");
            cm.dispose(); return;
        }

        // 检查背包空间（仅检查道具类型奖励）
        if (!checkInventorySpace(sc.rewards)) {
            cm.sendOk("背包空间不足，请清理背包后再来领取。");
            cm.dispose(); return;
        }

        // 发放奖励
        try {
            var ServerManager = Java.type('org.gms.manager.ServerManager');
            var SponsorService = Java.type('org.gms.service.SponsorService');
            var service = ServerManager.getApplicationContext().getBean("sponsorService");
            var result = service.claimReward(cm.getPlayer().getId(), sc.id, cm.getPlayer());

            // 标记已领取
            markClaimed(sc.id);

            cm.sendOk("领取成功！\r\n\r\n" + result);
            cm.dispose();
        } catch (e) {
            cm.sendOk("领取失败：" + e.getMessage());
            cm.dispose();
        }
    }
}

// ==================== 辅助函数 ====================

/** 检查是否已领取（通过CharacterExtendValue持久化） */
function isClaimed(configId) {
    var val = cm.getCharacterExtendValue("sponsor_config_" + configId);
    return val != null && val == "1";
}

/** 标记已领取 */
function markClaimed(configId) {
    cm.saveOrUpdateCharacterExtendValue("sponsor_config_" + configId, "1");
}

/** 奖励概要 */
function getRewardSummary(rewards) {
    if (rewards == null || rewards.size() == 0) return "无奖励";
    var parts = [];
    for (var i = 0; i < rewards.size(); i++) {
        var r = rewards.get(i);
        var qty = r.getQty();
        if (r.getType() == "nx") {
            parts.push("点券×" + qty);
        } else if (r.getType() == "meso") {
            parts.push("金币×" + qty);
        } else if (r.getType() == "item") {
            parts.push("#v" + r.getId() + "#×" + qty);
        }
    }
    return parts.join("  ");
}

/** 检查背包空间（仅道具类型） */
function checkInventorySpace(rewards) {
    if (rewards == null) return true;
    for (var i = 0; i < rewards.size(); i++) {
        var r = rewards.get(i);
        if (r.getType() == "item" && r.getId() >= 1000000) {
            if (!cm.canHold(r.getId(), r.getQty())) {
                return false;
            }
        }
    }
    return true;
}
