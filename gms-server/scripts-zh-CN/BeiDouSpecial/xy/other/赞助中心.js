/*
    赞助中心 — 查看总赞助/可消费赞助，领取赞助档位奖励（每档限领一次）
    角色独立：总赞助只增不减（档位），可消费赞助用于商店扣减
**/
var status = 0;
var sponsorConfigs = [];
var totalSponsor = 0;
var spendableSponsor = 0;
var sponsorService = null;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) { cm.dispose(); return; }
    if (mode == 0 && status >= 0) { cm.sendOk("欢迎再来！"); cm.dispose(); return; }
    if (mode == 1) status++; else status--;

    if (status == 0) {
        try {
            var ServerManager = Java.type('org.gms.manager.ServerManager');
            var context = ServerManager.getApplicationContext();
            sponsorService = context.getBean("sponsorService");

            var record = sponsorService.getRecordByPlayerId(cm.getPlayer().getId());
            totalSponsor = record != null ? record.getTotalSponsor() : 0;
            spendableSponsor = record != null ? record.getSpendableSponsor() : 0;

            var configs = sponsorService.listConfigs();
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
            cm.sendOk("系统错误，请联系管理员。\r\n" + e);
            cm.dispose(); return;
        }

        var text = "#e#d赞助中心#n#k\r\n\r\n";
        text += "总赞助：#r" + totalSponsor + "#k（档位达标，只增不减）\r\n";
        text += "可消费赞助：#b" + spendableSponsor + "#k（商店购买扣减）\r\n";
        text += "#d充值时两者同时增加；购买只扣可消费赞助。#k\r\n\r\n";
        text += "#e可领取的赞助奖励：#n\r\n\r\n";

        if (sponsorConfigs.length == 0) {
            text += "暂无赞助奖励配置。\r\n";
            cm.sendOk(text);
            cm.dispose(); return;
        }

        for (var j = 0; j < sponsorConfigs.length; j++) {
            var sc = sponsorConfigs[j];
            var claimed = sponsorService.isClaimed(cm.getPlayer().getId(), sc.id);
            var canClaim = totalSponsor >= sc.amount;

            text += "#L" + j + "#";
            if (claimed) {
                text += "#d[已领取]#k ";
            } else if (canClaim) {
                text += "#b[可领取]#k ";
            } else {
                text += "#r[未达标]#k ";
            }
            text += "#e" + (sc.name || ("赞助满" + sc.amount)) + "#n  (满#b" + sc.amount + "#k)";
            text += "  →  " + getRewardSummary(sc.rewards);
            text += "#l\r\n";
        }

        cm.sendSimple(text);

    } else if (status == 1) {
        var idx = selection;
        if (idx < 0 || idx >= sponsorConfigs.length) { cm.dispose(); return; }
        var sc2 = sponsorConfigs[idx];

        try {
            var result = sponsorService.claimReward(cm.getPlayer().getId(), sc2.id, cm.getPlayer());
            cm.sendOk("领取成功！\r\n\r\n" + result);
        } catch (e2) {
            cm.sendOk("领取失败：" + e2);
        }
        cm.dispose();
    }
}

function getRewardSummary(rewards) {
    if (rewards == null || rewards.size() == 0) return "无奖励";
    var parts = [];
    for (var i = 0; i < rewards.size(); i++) {
        var r = rewards.get(i);
        var qty = r.getQty();
        if (r.getType() == "nx") {
            parts.push("点券×" + qty);
        } else if (r.getType() == "maple") {
            parts.push("抵用×" + qty);
        } else if (r.getType() == "meso") {
            parts.push("金币×" + qty);
        } else if (r.getType() == "item") {
            parts.push("#v" + r.getId() + "#×" + qty);
        }
    }
    return parts.join("  ");
}
