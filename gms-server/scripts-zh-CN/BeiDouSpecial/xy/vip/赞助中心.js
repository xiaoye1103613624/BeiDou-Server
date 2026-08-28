/*
    赞助中心 — 查看总赞助/可消费赞助，领取赞助档位奖励（每档限领一次）
    角色独立：总赞助只增不减（档位），可消费赞助用于商店扣减
    领取成功后服务端会全服广播；档位配置由 Web 后台管理，实时读库生效
    装备奖励会展示实际属性（WZ 模板或自定义）
    技能组：领取前按 ONE/MULTI 选技，写入 skills + 默认快捷键；ALL 自动全发
**/
var status = 0;
var sponsorConfigs = [];
var totalSponsor = 0;
var spendableSponsor = 0;
var sponsorService = null;
var selectedIdx = -1;

/** 待选技能组队列：{ rewardId, pickMode, needCount, options:[view], picked:[] } */
var skillPickQueue = [];
var skillPickIdx = 0;
/** Java Map&lt;Integer, List&lt;Integer&gt;&gt; */
var skillSelections = null;
/** true=正在技能选择，下一次 selection 交给 handleSkillSelection */
var pickPhase = false;

function start() {
    status = -1;
    pickPhase = false;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) { cm.dispose(); return; }

    if (pickPhase) {
        if (mode == 0) {
            pickPhase = false;
            cm.sendOk("已取消领取。");
            cm.dispose();
            return;
        }
        handleSkillSelection(selection);
        return;
    }

    if (mode == 0) {
        if (status == 1) {
            status = -1;
            action(1, 0, 0);
            return;
        }
        cm.sendOk("欢迎再来！");
        cm.dispose();
        return;
    }
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
        text += "#d充值时两者同时增加；购买只扣可消费赞助。#k\r\n";
        text += "#d每个角色每档限领一次；领取后全服公告。#k\r\n\r\n";
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
            text += "  →  " + getRewardSummary(sc.rewards, false);
            text += "#l\r\n";
        }

        cm.sendSimple(text);

    } else if (status == 1) {
        selectedIdx = selection;
        if (selectedIdx < 0 || selectedIdx >= sponsorConfigs.length) { cm.dispose(); return; }
        var sc2 = sponsorConfigs[selectedIdx];
        var claimed2 = sponsorService.isClaimed(cm.getPlayer().getId(), sc2.id);
        var canClaim2 = totalSponsor >= sc2.amount;

        var detail = "#e" + (sc2.name || ("赞助满" + sc2.amount)) + "#n\r\n\r\n";
        detail += "达标金额：#b" + sc2.amount + "#k\r\n";
        detail += "当前总赞助：#r" + totalSponsor + "#k\r\n\r\n";
        detail += "#e奖励内容：#n\r\n" + getRewardSummary(sc2.rewards, true) + "\r\n\r\n";

        if (claimed2) {
            detail += "#d该档奖励已领取过。#k";
            cm.sendOk(detail);
            cm.dispose();
            return;
        }
        if (!canClaim2) {
            detail += "#r尚未达标，无法领取。#k";
            cm.sendOk(detail);
            cm.dispose();
            return;
        }

        skillPickQueue = buildSkillPickQueue(sc2.rewards);
        if (skillPickQueue.length > 0) {
            detail += "确认后将进入#b技能选择#k，选完后发放全部奖励并#r全服广播#k。";
        } else {
            detail += "确认领取吗？领取后将#r全服广播#k。";
        }
        cm.sendYesNo(detail);

    } else if (status == 2) {
        if (selectedIdx < 0 || selectedIdx >= sponsorConfigs.length) { cm.dispose(); return; }
        skillPickIdx = 0;
        skillSelections = new (Java.type('java.util.HashMap'))();
        if (skillPickQueue.length > 0) {
            pickPhase = true;
            showSkillPickPrompt();
            return;
        }
        doClaim();
    }
}

function handleSkillSelection(selection) {
    if (skillPickIdx < 0 || skillPickIdx >= skillPickQueue.length) {
        pickPhase = false;
        doClaim();
        return;
    }
    var group = skillPickQueue[skillPickIdx];
    var remaining = [];
    for (var i = 0; i < group.options.length; i++) {
        var opt = group.options[i];
        if (group.picked.indexOf(opt.getSkillId()) < 0) {
            remaining.push(opt);
        }
    }
    if (selection < 0 || selection >= remaining.length) {
        pickPhase = false;
        cm.sendOk("选择无效。");
        cm.dispose();
        return;
    }
    group.picked.push(remaining[selection].getSkillId());

    if (group.picked.length < group.needCount) {
        showSkillPickPrompt();
        return;
    }

    var ArrayList = Java.type('java.util.ArrayList');
    var list = new ArrayList();
    for (var p = 0; p < group.picked.length; p++) {
        list.add(java.lang.Integer.valueOf(group.picked[p]));
    }
    skillSelections.put(java.lang.Integer.valueOf(group.rewardId), list);

    skillPickIdx++;
    if (skillPickIdx < skillPickQueue.length) {
        showSkillPickPrompt();
        return;
    }
    pickPhase = false;
    doClaim();
}

function showSkillPickPrompt() {
    var group = skillPickQueue[skillPickIdx];
    var modeLabel = group.pickMode == "ONE"
        ? "多选一"
        : ("多选多 " + group.picked.length + "/" + group.needCount);
    var text = "#e选择技能奖励#n（" + modeLabel + "）\r\n\r\n";
    text += "请选择要领取的技能：\r\n\r\n";

    var remaining = [];
    for (var i = 0; i < group.options.length; i++) {
        var opt = group.options[i];
        if (group.picked.indexOf(opt.getSkillId()) < 0) {
            remaining.push(opt);
        }
    }
    for (var j = 0; j < remaining.length; j++) {
        var o = remaining[j];
        var name = o.getName();
        if (name == null || ("" + name).length == 0) name = "" + o.getSkillId();
        var lv = o.getSkillLevel() <= 0 ? ("最大Lv" + o.getMaxLevel()) : ("Lv" + o.getSkillLevel());
        text += "#L" + j + "##b" + name + "#k (" + o.getSkillId() + ")  " + lv;
        if (o.getDefaultKey() > 0) {
            text += "  键#" + o.getDefaultKey();
        }
        text += "#l\r\n";
    }
    cm.sendSimple(text);
}

function doClaim() {
    if (selectedIdx < 0 || selectedIdx >= sponsorConfigs.length) { cm.dispose(); return; }
    var sc3 = sponsorConfigs[selectedIdx];
    try {
        var result = sponsorService.claimReward(
            cm.getPlayer().getId(), sc3.id, cm.getPlayer(), skillSelections);
        cm.sendOk("领取成功！\r\n\r\n" + result + "\r\n\r\n#d已向全服发送公告。#k");
    } catch (e2) {
        cm.sendOk("领取失败：" + e2);
    }
    cm.dispose();
}

function buildSkillPickQueue(rewards) {
    var queue = [];
    if (rewards == null) return queue;
    for (var i = 0; i < rewards.size(); i++) {
        var r = rewards.get(i);
        if (r.getType() != "skill_group") continue;
        var mode = r.getPickMode();
        if (mode == null) mode = "ALL";
        mode = ("" + mode).toUpperCase();
        if (mode == "ALL") continue;
        var opts = r.getSkillOptions();
        if (opts == null || opts.size() == 0) continue;
        var need = 1;
        if (mode == "MULTI") {
            need = r.getQty();
            if (need < 1) need = 1;
            if (need > opts.size()) need = opts.size();
        }
        var optionArr = [];
        for (var k = 0; k < opts.size(); k++) {
            optionArr.push(opts.get(k));
        }
        queue.push({
            rewardId: r.getRewardId(),
            pickMode: mode,
            needCount: need,
            options: optionArr,
            picked: []
        });
    }
    return queue;
}

function getRewardSummary(rewards, detailed) {
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
            var line = "#v" + r.getId() + "#×" + qty;
            if (r.isEquip()) {
                var emode = r.getStatMode();
                if (emode == "custom") {
                    line += " #b[自定义属性]#k";
                } else {
                    line += " #d[模板属性]#k";
                }
                if (detailed) {
                    var st = r.getStatsText();
                    if (st != null && st.length() > 0) {
                        line += "\r\n　　#d" + st + "#k";
                    }
                }
            }
            parts.push(line);
        } else if (r.getType() == "skill_group") {
            parts.push(formatSkillGroupSummary(r, detailed));
        }
    }
    return parts.join(detailed ? "\r\n" : "  ");
}

function formatSkillGroupSummary(r, detailed) {
    var mode = r.getPickMode();
    if (mode == null) mode = "ALL";
    mode = ("" + mode).toUpperCase();
    var modeZh = mode == "ONE" ? "多选一" : (mode == "MULTI" ? ("多选" + r.getQty()) : "全发");
    var opts = r.getSkillOptions();
    var count = opts != null ? opts.size() : 0;
    var head = "#b技能组#k[" + modeZh + "]×" + count;
    if (!detailed || opts == null || opts.size() == 0) {
        return head;
    }
    var lines = [head];
    for (var i = 0; i < opts.size(); i++) {
        var o = opts.get(i);
        var name = o.getName();
        if (name == null || ("" + name).length == 0) name = "" + o.getSkillId();
        var lv = o.getSkillLevel() <= 0 ? ("最大Lv" + o.getMaxLevel()) : ("Lv" + o.getSkillLevel());
        lines.push("　　· " + name + " (" + o.getSkillId() + ") " + lv);
    }
    return lines.join("\r\n");
}
