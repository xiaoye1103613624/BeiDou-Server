/**
 * 艾琳森林入口 NPC脚本
 * 功能: 介绍副本规则, 组队检查, 扣费进入
 * NPC: 可在魔法密林放置 (建议NPC ID: 1032001或自定义)
 *
 * @author 萧曵
 * @date 2026-07-30
 */

var ENTRY_FEE = 200000;
var MIN_LEVEL = 70;
var MAX_PLAYERS = 6;
var MIN_PLAYERS = 1;

var status = 0;

function start() {
    cm.sendNext(
        "#b🌲 艾琳森林 - 主题副本#k\r\n\r\n" +
        "魔法密林深处传来精灵的呼唤...\r\n" +
        "森林被神秘力量侵蚀, 需要勇者相助!\r\n\r\n" +
        "#r副本流程:#k\r\n" +
        "第1关: 森林入口 - 清除入侵怪物\r\n" +
        "第2关: 毒藤区 - 消灭变异植物\r\n" +
        "第3关: 精灵废墟 - 清理废墟\r\n" +
        "Boss战: 击败森林守护者\r\n\r\n" +
        "#b入场费: " + ENTRY_FEE.toLocaleString() + " 金币#k\r\n" +
        "#b等级要求: Lv" + MIN_LEVEL + "+#k"
    );
}

function action(mode, type, selection) {
    if (mode == -1) { cm.dispose(); return; }
    if (mode == 1) { status++; } else { cm.dispose(); return; }

    if (status == 1) {
        if (cm.getLevel() < MIN_LEVEL) {
            cm.sendOk("等级不足! 需要Lv" + MIN_LEVEL + "+");
            cm.dispose();
            return;
        }
        if (cm.getMeso() < ENTRY_FEE) {
            cm.sendOk("金币不足! 需要" + ENTRY_FEE.toLocaleString() + "金币");
            cm.dispose();
            return;
        }
        if (cm.getEventInstance() != null) {
            cm.sendOk("你已经在一个副本中了!");
            cm.dispose();
            return;
        }

        cm.sendYesNo("确认进入 #b艾琳森林#k?\r\n将扣除 #r" + ENTRY_FEE.toLocaleString() + "金币#k。");
    } else if (status == 2) {
        cm.gainMeso(-ENTRY_FEE);
        var em = cm.getClient().getChannelServer().getEventSM().getEventManager("EllinForest");
        if (em != null) {
            em.startInstance("solo", cm.getPlayer());
        } else {
            cm.sendOk("副本系统暂时不可用, 请联系管理员。");
        }
        cm.dispose();
    }
}
