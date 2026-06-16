/*
梦之岛冒险到079 QQ279934747游戏服务端
脚本：推广系统二级分支（拜师）
*/

importPackage(Packages.client);

var 点券 = "#fUI/CashShop.img/CashItem/0#";
var status = 0;
var fee;
var chance = Math.floor(Math.random() * 1);

function start() {
    status = -1;
    action(1, 0, 0);
}

/* ===== 工具：读玩家仙级 ===== */
function getPlayerXianLevel(playerName) {
    var conn = cm.getConnection();
    var sql = "SELECT x.count FROM xmwnjl x INNER JOIN characters c ON x.characterid = c.id WHERE c.name = ? AND x.bossid = 'XM飞升系统_仙级'";
    var pstmt = conn.prepareStatement(sql);
    pstmt.setString(1, playerName);
    var rs = pstmt.executeQuery();
    var level = 0;
    if (rs.next()) level = rs.getInt("count");
    rs.close(); pstmt.close(); conn.close();
    return level;
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.sendOk("新人可以找老玩家要角色码。");
            cm.dispose();
            return;
        }
        if (mode == 1) status++;
        else status--;

        if (status == 0) {
		/* ******** 双向门槛 ******** */
			var playerLevel = cm.getPlayer().getLevel();
			var playerXian  = getPlayerXianLevel(cm.getPlayer().getName());

			// ① 徒弟自身条件
			if (playerLevel <= 10 || playerXian >= 1) {
				cm.sendOk("拜师失败！\r\n必须同时满足：\r\n1. 等级＞10级\r\n2. 尚未筑基（仙级0级）");
				cm.dispose();
				return;
			}

			// ② 师父必须已筑基
			var masterId = cm.getBossRank("推广员", 2);   // 读取当前准备拜的师父ID
			if (masterId > 0) {                         // 已有师父记录，说明准备拜他
				var masterXian = getPlayerXianLevel(cm.角色ID取名字Z(masterId));
				if (masterXian < 1) {
					cm.sendOk("拜师失败！\r\n您准备拜的师父尚未筑基，还是去找个强点的师傅吧。");
					cm.dispose();
					return;
				}
			}
            cm.sendGetText("#b#e\t每个玩家只能拜师一次#k#n(请慎重选择师父)\r\n\r\n#b#e\t请在群里找个靠谱的老玩家要 #r角色码#n\r\n\r\n\r\n#k请输入你要拜师玩家的角色码:");
        } else if (status == 1) {
            fee = cm.getText();
            var masterName = cm.getCharacterNameById(fee);
            if (masterName == null) {
                cm.sendOk("输入有误！！！");
                cm.dispose();
                return;
            }
			    /* 实时检测师父仙级 */
		var masterXian = getPlayerXianLevel(masterName);
		if (masterXian < 1) {
			cm.sendOk("拜师失败！\r\n您输入的师父尚未筑基，还是去找个强点的师傅吧。");
			cm.dispose();
			return;
		}
            cm.sendYesNo("确认你的角色码准确无误 #r" + fee + " #k?\r\n确认是否拜：#r" + masterName + "#k为师。");
        } else if (fee == cm.getPlayer().id) {
            cm.sendOk("你不能使用自己的角色码。");
            cm.dispose();
        } else if (cm.getQuestStatus(9941301) == 2) {
            cm.sendOk("很抱歉，你已经使用过角色码了！");
            cm.dispose();
        } else {
            if (chance <= 1) {
                cm.setBossRankCount("" + fee + "", 1);
                cm.setBossRankCount("推广员", 0);
                cm.setBossRankCount("推广员", fee);
                cm.completeQuest(9941301);
                var masterName = cm.getCharacterNameById(fee);
                cm.sendOk("输入角色码成功！你的师傅是：#r" + masterName + "#k!");
                cm.全服黄色喇叭("师徒系统 : 恭喜 [" + cm.getPlayer().getName() + "] 成功拜入 【" + masterName + "】 的师门!");
				cm.全服黄色喇叭("师徒系统 : 恭喜 [" + cm.getPlayer().getName() + "] 成功拜入 【" + masterName + "】 的师门!");
				cm.全服黄色喇叭("师徒系统 : 恭喜 [" + cm.getPlayer().getName() + "] 成功拜入 【" + masterName + "】 的师门!");
                cm.dispose();
            } else {
                cm.sendOk("未知错误，请联系管理员");
                cm.dispose();
            }
        }
    }
}