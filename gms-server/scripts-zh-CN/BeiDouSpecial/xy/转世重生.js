/**
 * @description 转世重生系统
 * 功能：角色达到最高等级后可转生，回到1级重新升级，累积转生次数可获得属性加成
 * 入口：9900001.js case 507
 */

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }

    if (status === 0) {
        showMainMenu();
    } else if (status === 1) {
        handleMainSelection(selection);
    } else if (status === 2) {
        // YesNo 确认
        if (type === 1) {
            doRebirth();
        } else {
            cm.sendOk("已取消转生。");
            cm.dispose();
        }
    }
}

function showMainMenu() {
    var player = cm.getPlayer();
    var reborns = player.getReborns();
    var maxLevel = player.getMaxClassLevel();
    var currentLevel = player.getLevel();

    var text = "#e转世重生系统#n\r\n\r\n";
    text += "你已转生：#r" + reborns + "#k 次\r\n";
    text += "当前等级：#b" + currentLevel + "#k / 最高等级：#b" + maxLevel + "#k\r\n\r\n";

    // 转生奖励说明
    text += "#d转生奖励（每次转生永久叠加）：#k\r\n";
    text += "  #b每次转生：#k 全属性 +5、命中 +3、HP +100\r\n";
    text += "  #b特殊里程碑：#k\r\n";
    text += "     第 5 次：获得 #r专属称号#k\r\n";
    text += "    第 10 次：获得 #r传说武器皮肤#k\r\n";
    text += "    第 20 次：获得 #rVIP 资格（7天）#k\r\n\r\n";

    var bonus = getRebirthBonus(reborns);
    text += "当前已有加成：\r\n";
    text += "  全属性 +#b" + bonus.allStat + "#k  命中 +#b" + bonus.acc + "#k  HP +#b" + bonus.hp + "#k\r\n\r\n";

    if (currentLevel < maxLevel) {
        text += "#r你还未达到最高等级 " + maxLevel + "，无法转生！#k\r\n";
        text += "#L0#关闭#l\r\n";
    } else {
        text += "#L1##r我要转生（回到1级）#k#l\r\n";
        text += "#L0#取消#l\r\n";
    }

    cm.sendSimple(text);
}

function handleMainSelection(selection) {
    if (selection === 0) {
        cm.dispose();
        return;
    }

    var player = cm.getPlayer();
    var currentLevel = player.getLevel();
    var maxLevel = player.getMaxClassLevel();

    if (currentLevel < maxLevel) {
        cm.sendOk("#r还未达到最高等级 " + maxLevel + "，无法转生！#k");
        cm.dispose();
        return;
    }

    var reborns = player.getReborns();
    var text = "#e确认转生？#n\r\n\r\n";
    text += "#r警告：#k 转生后将回到1级，所有技能重置！\r\n\r\n";
    text += "转生后你将是第 #r" + (reborns + 1) + "#k 次转生角色\r\n";
    text += "并永久获得全属性 #b+5#k、命中 #b+3#k、HP #b+100#k\r\n";
    cm.sendYesNo(text);
}

function doRebirth() {
    var player = cm.getPlayer();

    // 执行转生
    player.executeRebornAsId(0);  // 0 = 冒险家

    var reborns = player.getReborns();
    var text = "转生成功！\r\n\r\n";
    text += "你现在是第 #r" + reborns + "#k 次转生的勇者！\r\n";
    text += "全属性 +#r" + (reborns * 5) + "#k（累计加成）\r\n\r\n";

    // 里程碑奖励
    if (reborns === 5) {
        cm.gainItem(1142006, 1);  // 称号道具示例
        text += "达成第5次转生！获得 #r专属称号#k！\r\n";
    } else if (reborns === 10) {
        text += "达成第10次转生！#r传说奖励已解锁，请联系管理员领取！#k\r\n";
    } else if (reborns >= 20 && reborns % 20 === 0) {
        text += "达成里程碑！#r奖励已解锁，请联系管理员领取！#k\r\n";
    }

    cm.sendOk(text);
    cm.dispose();
}

/** 计算当前转生加成 */
function getRebirthBonus(reborns) {
    return {
        allStat: reborns * 5,
        acc: reborns * 3,
        hp: reborns * 100
    };
}
