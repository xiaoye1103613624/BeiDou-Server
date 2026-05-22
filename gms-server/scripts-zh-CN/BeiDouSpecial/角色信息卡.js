/*
 * ==================
 * 脚本类型: 角色信息卡/统计显示
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 显示角色详细统计信息
 *   2. 包括等级、职业、经验、金币、人气等
 * ==================
 */

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) {
        cm.dispose();
        return;
    }

    if (status === 0) {
        var player = cm.getPlayer();
        var job = Java.type('org.gms.client.Job');
        var jobName = job.getNameByJobId(player.getJob().getId());

        var text = "#e#b=== 角色信息卡 ===#k#n\r\n\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n";
        text += "角色名：#b" + player.getName() + "#k\r\n";
        text += "等级：  #b" + player.getLevel() + "#k\r\n";
        text += "职业：  #b" + jobName + "#k (ID:" + player.getJob().getId() + ")\r\n";
        text += "经验：  #b" + player.getExp().toLocaleString() + "#k / #b" + player.getExpToNextLevel().toLocaleString() + "#k\r\n";
        text += "金币：  #b" + player.getMeso().toLocaleString() + "#k\r\n";
        text += "人气：  #b" + player.getFame() + "#k\r\n";
        text += "\r\n";
        text += "HP：    #b" + player.getHp() + "#k / #b" + player.getCurrentMaxHp() + "#k\r\n";
        text += "MP：    #b" + player.getMp() + "#k / #b" + player.getCurrentMaxMp() + "#k\r\n";
        text += "\r\n";
        text += "力量：#b" + player.getStr() + "#k  ";
        text += "敏捷：#b" + player.getDex() + "#k  ";
        text += "智力：#b" + player.getInt() + "#k  ";
        text += "运气：#b" + player.getLuk() + "#k\r\n";
        text += "\r\n";
        text += "AP：#b" + player.getRemainingAp() + "#k  ";
        text += "SP：#b" + player.getRemainingSp() + "#k\r\n";
        text += "\r\n";
        text += "点券：    #b" + player.getCashShop().getCash(1) + "#k\r\n";
        text += "抵用券：#b" + player.getCashShop().getCash(2) + "#k\r\n";
        text += "信用券：#b" + player.getCashShop().getCash(4) + "#k\r\n";

        cm.sendOk(text);
        cm.dispose();
    }
}
