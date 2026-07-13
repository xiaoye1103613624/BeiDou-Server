/**
 * @author: 萧曵
 * @npc: 每日活跃
 * @description:
 *   展示 xy_daily_active_task 配置的全部已启用任务及当日进度，达标后可逐项领取奖励。
 *   进度累计逻辑见 org.gms.config.DailyActiveManager(Java侧门面) + org.gms.service.DailyActiveService(懒重置)。
 *   当前已接入事件钩子的任务：装备砸卷(ScrollHandler)、使用聊天表情(FaceExpressionHandler，F2大笑)、
 *   挑战BOSS/击杀野外精英(MapleMap.killMonster)、休闲娱乐(MiniGame对局结束)、在线奖励(OnlineTimeTask，每30分钟+1)、
 *   守护城镇(event/MonsterSiege.js攻城判定成功时记在场玩家)、
 *   通关副本(pq_clear，闹钟PapulatusBattle.js/扎昆ZakumBattle.js/黑龙HorntailBattle.js/品克缤PinkBeanBattle.js/
 *   巨型蝙蝠怪BalrogBattle.js，各自monsterKilled判定通关时记在场远征队全部成员)。
 *   尚未接入钩子、需后续在对应脚本里手动调用 DailyActiveManager.addProgress(角色ID,"task_key",次数) 的任务：
 *   参加活动(join_event)、商城购买道具(cash_shop_buy)、每日跑环(daily_loop)、打豆豆机(bean_machine)。
 *   这4项暂时只能通过GM命令或脚本手动调用累计，菜单仍会展示其配置好的目标值，方便后续接入。
 */

var DailyActiveManager = Java.type("org.gms.config.DailyActiveManager");

var characterId;
var overviewCache = [];
var claimableKeys = [];

function start() {
    characterId = cm.getPlayer().getId();
    levelMain();
}

function levelMain() {
    overviewCache = DailyActiveManager.getOverview(characterId);
    claimableKeys = [];

    var text = "#e每日活跃#n\r\n#d完成下列任务可领取奖励，每日0点(自然日切换)自动重置#k\r\n\r\n";

    if (overviewCache.length === 0) {
        text += "暂无配置的每日活跃任务。";
        cm.sendOk(text);
        cm.dispose();
        return;
    }

    for (var i = 0; i < overviewCache.length; i++) {
        var t = overviewCache[i];
        var progress = t.get("progress");
        var target = t.get("targetCount");
        var completed = t.get("completed");
        var claimed = t.get("claimed");

        var shownProgress = progress > target ? target : progress;
        text += t.get("taskName") + " [" + shownProgress + "/" + target + "]";
        if (claimed) {
            text += " #g(已领取)#k";
        } else if (completed) {
            text += " #r(可领取)#k";
            claimableKeys.push(t.get("taskKey"));
        }
        text += "\r\n";
    }

    if (claimableKeys.length === 0) {
        text += "\r\n暂无可领取的任务奖励。\r\n\r\n";
    } else {
        text += "\r\n#b请选择要领取奖励的任务：#k\r\n";
        for (var j = 0; j < claimableKeys.length; j++) {
            var task = findTask(claimableKeys[j]);
            text += "#L" + j + "##b" + task.get("taskName") + "#k#l\r\n";
        }
    }
    // 菜单末尾追加"活跃度奖励"入口，跳转到独立的活跃度积分脚本(scripts-zh-CN/npc/活跃度奖励.js)
    text += "#L" + claimableKeys.length + "##b活跃度奖励(积分阶梯奖励)#k#l\r\n";
    cm.sendNextSelectLevel("HandleClaim", text);
}

function findTask(taskKey) {
    for (var i = 0; i < overviewCache.length; i++) {
        if (overviewCache[i].get("taskKey") == taskKey) {
            return overviewCache[i];
        }
    }
    return null;
}

function levelHandleClaim(selection) {
    if (selection == claimableKeys.length) {
        // 跳转到独立的"活跃度奖励"脚本(scripts-zh-CN/npc/活跃度奖励.js)，由该脚本接管后续对话
        cm.openNpc(cm.getNpc(), "活跃度奖励");
        return;
    }
    if (selection < 0 || selection >= claimableKeys.length) {
        cm.dispose();
        return;
    }
    var taskKey = claimableKeys[selection];
    var result = DailyActiveManager.claim(characterId, taskKey);
    if (!result.get("success")) {
        cm.sendOkLevel("Main", "领取失败：" + result.get("message"));
        return;
    }

    var rewardMeso = result.get("rewardMeso");
    var rewardItemId = result.get("rewardItemId");
    var rewardItemCount = result.get("rewardItemCount");

    if (rewardMeso > 0) {
        cm.gainMeso(rewardMeso);
    }
    if (rewardItemId > 0 && rewardItemCount > 0) {
        if (!cm.canHold(rewardItemId, rewardItemCount)) {
            cm.sendOkLevel("Main", "背包空间不足，无法领取物品奖励，请清理背包后重试。");
            return;
        }
        cm.gainItem(rewardItemId, rewardItemCount);
    }

    var text = "#b领取成功！#k\r\n";
    if (rewardMeso > 0) {
        text += "金币 +" + rewardMeso + "\r\n";
    }
    if (rewardItemId > 0 && rewardItemCount > 0) {
        text += "#v " + rewardItemId + "# x" + rewardItemCount + "\r\n";
    }
    cm.sendOkLevel("Main", text);
}
