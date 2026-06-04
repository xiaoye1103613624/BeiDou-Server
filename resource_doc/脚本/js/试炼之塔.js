/* ==========================
   每日试炼之塔 · 修复版
   1) 只检测当前频道、当前事件实例人数
   2) 修复频道限制、地图人数判断
   3) 逻辑结构微调，避免多重嵌套
   ========================== */

var 最小人数   = 1;
var 最大小人数 = 1;
var 星星       = "#fEffect/CharacterEff/1114000/2/0#";
var 爱心       = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头   = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形     = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头   = "#fUI/UIWindow/Quest/icon2/7#";

var minLevel   = 200;
var maxLevel   = 255;
var minPartySize = 1;
var maxPartySize = 1;
var 每日次数   = 10;
var em;   // 事件管理器
var eim;  // 事件实例

/* ---------- 入口 ---------- */
function start() {
    // ① 频道限制
//    if (cm.getPlayer().getClient().getChannel() !== 3) {
	if (cm.getPlayer().getClient().getChannel() != 3 && cm.getPlayer().getClient().getChannel() != 4) {
        cm.sendOk("只能在 3 - 4 频道进行！！！");
        cm.dispose();
        return;
    }

    // ② 单人组队提示
    if (cm.getParty() == null) {
        cm.sendOk("请开个单人组后再来。");
        cm.dispose();
        return;
    }

    status = -1;
    action(1, 0, 0);
}

/* ---------- 主循环 ---------- */
function action(mode, type, selection) {
    em = cm.getEventManager("knsy");
    if (em == null) {
        cm.sendOk("脚本错误，请联系管理员");
        cm.dispose();
        return;
    }

    if (mode === -1) {
        cm.dispose();
        return;
    }

    if (mode === 1) {
        status++;
    } else {
        status--;
    }

    /* ---------- 0  主菜单 ---------- */
    if (status === 0) {
        eim = em.getInstance("BossQuest123");

        var text = "";
        text += "        #v1142684# ---- #d#e每日试炼之塔#k#n ---- #v1142684#\r\n\r\n#b";
        text += "           本塔每关会召唤 4 只非常强大的怪物\r\n\r\n";
		text += "                今日已挑战次数：#r" + cm.getBossLog("每日试炼次数") + "#k/" + 每日次数 + " 次\r\n";

        if (cm.getBossLog("每日试炼次数") < 每日次数) {
            text += "                #L0#" + 蓝色箭头 + "#r挑战每日试炼之塔#l\r\n\r\n";
        } else {
            text += "\r\n                  #r今日挑战次数已达上限\r\n";
            cm.sendOk(text);
            cm.dispose();
            return;
        }

        // 掉线重返（可选，逻辑保留）
        // if (eim != null && eim.getProperty("掉线重返" + cm.getPlayer().getId()) === "1") {
        //     text += "   #d#L2#掉线重返#l#k\r\n";
        // }

        cm.sendSimple(text);
    }

    /* ---------- 1  选项处理 ---------- */
    else if (status === 1) {
        /* ① 掉线重返（示例） */
        if (selection === 2) {
            eim.registerPlayer(cm.getPlayer());
            cm.sendOk("#e#d已为你重返地图");
            cm.dispose();
            return;
        }

        /* ② 挑战 */
        if (selection === 0) {
            var party      = cm.getParty().getMembers();
            var inMap      = cm.partyMembersInMap();
            var levelValid = 0;

            /* 等级检测 */
            for (var i = 0; i < party.size(); i++) {
                var lv = party.get(i).getLevel();
                if (lv >= minLevel && lv <= maxLevel) levelValid++;
            }

            /* 各种条件拦截 */
            if (inMap < minPartySize || inMap > maxPartySize) {
                cm.sendOk("请确保队伍人数为 " + minPartySize + " 人，且都在当前地图！");
                cm.dispose();
                return;
            }
            if (cm.getMeso() < 1500) {
                cm.sendOk("#e#r需要 1500 金币才能开启传送门！");
                cm.dispose();
                return;
            }
            if (!cm.haveItem(2022524, 1)) {
                cm.sendOk("需要一个 #v2022524# 才能进入！");
                cm.dispose();
                return;
            }
            if (cm.getLevel() < 200) {
                cm.sendOk("需要达到 200 级才能进入！");
                cm.dispose();
                return;
            }
            if (cm.getPlayer().getDamage() < 299999) {
                cm.sendOk("需要 29 万破功才能进入！");
                cm.dispose();
                return;
            }
            if (!cm.isLeader()) {
                cm.sendOk("请让队长与我对话！");
                cm.dispose();
                return;
            }
            if (cm.getBossLog("每日试炼次数") >= 每日次数) {
                cm.sendOk("今日已挑战 " + 每日次数 + " 次，明天再来吧！");
                cm.dispose();
                return;
            }

            /* ---------- 关键：只检测当前事件实例人数 ---------- */
            eim = em.getInstance("BossQuest123");
            if (eim != null && eim.getPlayerCount() > 0) {
                cm.sendOk("当前副本已有玩家在进行中，请稍后再试！");
                cm.dispose();
                return;
            }

            /* ---------- 真正开启副本 ---------- */
        //    cm.getPlayer().setBossLog("试炼之路", 0);
            em.startInstance(cm.getParty(), cm.getPlayer().getMap());
            cm.给团队道具(2022524, -1);
            cm.setBossLog("每日试炼次数");
            cm.dispose();
        }

        /* ③ 其他选项可扩展 */
        else if (selection === 1) {
            cm.dispose();
            // cm.openNpc(9000288, 4);
        }
    }
}