/*  
 NPC版权:游戏盒团队    
 制作人：风雨
 */
var 红印 = "#fEffect/Direction1.img/effect/aran/finishLogo1/0/3#";
var 红蓝点 = "#fEffect/CharacterEff.img/1032054/0/0#";
var 蓝星 = "#fEffect/CharacterEff.img/1052203/1/0#";
var 红星 = "#fEffect/CharacterEff.img/1052203/2/0#";
var 大蓝星 = "#fEffect/CharacterEff.img/1022223/2/0#";
var 大红星 = "#fEffect/CharacterEff.img/1022223/1/0#";
var 蓝点 = "#fEffect/CharacterEff.img/1022223/6/0#";
var 红点 = "#fEffect/CharacterEff.img/1022223/7/0#";
var 红冠 = "#fUI/GuildMark.img/Mark/Etc/00009023/14#";
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var status = -1;

// 仙级名称列表
var 仙级名称 = [
    "凡人", "筑基", "金丹", "元婴", "出窍", "分神", "合体", "渡劫", 
    "大乘", "天仙", "仙君", "玄仙", "仙帝", "神人", "神将", "神君", 
    "神帝", "神皇", "神尊", "圣人", "至尊", "主宰", "永恒", "创世", 
	"超脱"
];

// 仙级奖励
var 仙级奖励 = Array(
    Array("筑基", "筑基奖励1"),
    Array("金丹", "金丹奖励"),
    Array("元婴", "元婴奖励"),
    Array("出窍", "出窍奖励"),
    Array("分神", "分神奖励"),
    Array("合体", "合体奖励"),
    Array("渡劫", "渡劫奖励"),
    Array("大乘", "大乘奖励"),
    Array("天仙", "天仙奖励"),
    Array("仙君", "仙君奖励"),
    Array("玄仙", "玄仙奖励"),
    Array("仙帝", "仙帝奖励"),
    Array("神人", "神人奖励"),
    Array("神将", "神将奖励"),
    Array("神君", "神君奖励"),
    Array("神帝", "神帝奖励"),
    Array("神皇", "神皇奖励"),
    Array("神尊", "神尊奖励"),
    Array("圣人", "圣人奖励"),
    Array("至尊", "至尊奖励"),
    Array("主宰", "主宰奖励"),
	Array("永恒", "永恒奖励"),
	Array("创世", "创世奖励"),
	Array("超脱", "超脱奖励")
);

// 奖励物品列表
var 奖励物品列表 = Array(
//    Array(1132300, 1, "筑基"), // 轮回石碑
	Array(4322899, 1, "筑基"), // 1级宝石
	Array(3605020, 1, "筑基"), // MVP特权卡 野外狩猎3700184
	
    Array(4322899, 1, "金丹"), // 1级宝石
    Array(2022509, 10, "金丹"), // 元宝       Array(0, 12000, "玄仙"), // 给点券 12000
	
	Array(4322898, 1, "元婴"), // 2级宝石
	Array(2022509, 20, "元婴"), // 元宝
	
    Array(4322898, 1, "出窍"), // 2级宝石%
	Array(2022509, 30, "出窍"), // 元宝
	
    Array(4322897, 1, "分神"), // 3级宝石
	Array(2022509, 40, "分神"), // 元宝
	
    Array(4322897, 1, "合体"), // 3级宝石
	Array(2022509, 50, "合体"), // 元宝
	
    Array(4322885, 1, "渡劫"), // 4级宝石
	Array(2022509, 60, "渡劫"), // 元宝
	
    Array(4322885, 1, "大乘"), // 4级宝石 
	Array(2022509, 70, "大乘"), // 元宝
	
    Array(4322884, 1, "天仙"), // 5级宝石
	Array(2022509, 80, "天仙"), // 元宝
	
    Array(4322884, 1, "仙君"), // 5级宝石
	Array(2022509, 90, "仙君"), // 元宝
	
    Array(4322883, 1, "玄仙"), // 6级宝石
	Array(2022509, 100, "玄仙"), // 元宝
	
    Array(4322883, 1, "仙帝"), // 6级宝石
	Array(2022509, 200, "仙帝"), // 元宝
	
    Array(4322869, 1, "神人"), // 7级宝石
	Array(2022509, 300, "神人"), // 元宝
	
    Array(4322869, 1, "神将"), // 7级宝石
	Array(2022509, 400, "神将"), // 元宝
	
    Array(4322868, 1, "神君"), // 8级宝石
	Array(2022509, 500, "神君"), // 元宝
	
    Array(4322868, 1, "神帝"), // 8级宝石
	Array(2022509, 600, "神帝"), // 元宝
	
    Array(4322867, 1, "神皇"), // 9级宝石
	Array(2022509, 700, "神皇"), // 元宝
	
    Array(4322867, 1, "神尊"), // 9级宝石
	Array(2022509, 800, "神尊"), // 元宝
	
    Array(4322853, 1, "圣人"), // 10级宝石
	Array(2022509, 900, "圣人"), // 元宝
	
    Array(4322853, 1, "至尊"), // 10级宝石
	Array(2022509, 1000, "至尊"), // 元宝
	
	Array(4322852, 1, "主宰"), // 11级宝石
	Array(2022509, 2000, "主宰"), // 元宝
	
	Array(4322852, 1, "永恒"), // 11级宝石
	Array(2022509, 3000, "永恒"), // 元宝
	
	Array(4322851, 1, "创世"), // 12级宝石
	Array(2022509, 4000, "创世"), // 元宝
	
	Array(4322851, 1, "超脱"), // 12级宝石
	Array(2022509, 5000, "超脱") // 元宝

);

var 仙级 = -1;
var 标识 = null;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status >= 0) {
            cm.dispose();
            return;
        }
        status--;
    }
    if (status == 0) {
        var selStr = "#e#d\t\t\t\t" + 红冠 + "【仙级奖励】" + 红冠 + "#l#n\r\n";
        var 当前仙级 = getxmwnjlc("XM飞升系统_仙级");
        var 当前仙级名称 = 仙级名称[当前仙级];
        var 仙帝索引 = 仙级名称.indexOf("仙帝"); // 获取“仙帝”的索引位置
        for (var i = 0; i < 仙级奖励.length; i++) {
			if (i >= 仙帝索引 && 当前仙级 < 仙帝索引) {
                continue; // 如果当前仙级未达到“仙帝”，跳过“仙帝”之前的奖励
            }
            var 状态 = "";
            var 仙级标识 = 仙级奖励[i][1];
            if (cm.getPlayer().getOneTimeLog(仙级标识) == 1) {
                状态 = "#b已领取#r"; // 如果已经领取过，直接显示“已领取”
            } else if (仙级名称.indexOf(仙级奖励[i][0]) <= 当前仙级) {
                状态 = "#g可领取#r"; // 如果仙级达到但未领取，显示“可领取”
            } else {
                状态 = "#k未达成#r"; // 如果仙级未达到，显示“未领取”
            }
            selStr += "\t\t\t  #r#L" + i + "# " + 蓝色角点 + " " + 仙级奖励[i][0] + "奖励 (" + 状态 + ") #l\r\n";
        }
        cm.sendSimple(selStr);
		} else if (status == 1) {
			仙级 = selection;
			标识 = 仙级奖励[仙级][1];
		var 当前仙级 = getxmwnjlc("XM飞升系统_仙级");
		var 当前仙级名称 = 仙级名称[当前仙级];
		var 奖励仙级索引 = 仙级名称.indexOf(仙级奖励[仙级][0]);

		if (cm.getInventory(1).isFull(2) || cm.getInventory(2).isFull(2) || cm.getInventory(3).isFull(2) || cm.getInventory(4).isFull(2) || cm.getInventory(5).isFull(2)) {
			cm.sendOk("防止领取失败，请保证背包所有栏位至少保留3个空格！");
			cm.dispose();
			return;
		}
        var txt = "#e#d\t\t\t\t" + 红冠 + "【仙级奖励】" + 红冠 + "#l\r\n\r\n";
        txt += "#e#d恭喜你达到 #r" + 仙级奖励[仙级][0] + "#d 可领取仙级奖励：\r\n";
        txt += "#k" + 红星 + "   " + 大红星 + "   " + 红点 + "   " + 红蓝点 + "   " + 红蓝点 + "   " + 蓝点 + "   " + 大蓝星 + "   " + 蓝星 + "#k  \r\n";
        for (var j = 0; j < 奖励物品列表.length; j++) {
            if (奖励物品列表[j][2] == 仙级奖励[仙级][0]) { // 判断物品标识
                if (奖励物品列表[j][0] == 0) { // 是金币
                    txt += "#r         点券 × " + 奖励物品列表[j][1] + "\r\n";
                } else { // 不是金币
                    txt += "#r         #v" + 奖励物品列表[j][0] + ":##z" + 奖励物品列表[j][0] + "# × " + 奖励物品列表[j][1] + "\r\n";
                }
            }
        }
        txt += "#k" + 红星 + "   " + 大红星 + "   " + 红点 + "   " + 红蓝点 + "   " + 红蓝点 + "   " + 蓝点 + "   " + 大蓝星 + "   " + 蓝星 + "#k  \r\n";
        cm.sendYesNo(txt + "\r\n确认领取吗？");
    } else if (status == 2) {
		var 当前仙级 = getxmwnjlc("XM飞升系统_仙级");
		var 奖励仙级索引 = 仙级名称.indexOf(仙级奖励[仙级][0]);
		if (cm.getPlayer().getOneTimeLog("仙级奖励1") < 1) {
			cm.sendOk("你还没有购买仙级奖励,无法领取哦。");
			cm.dispose();
			return; // 确保在发送消息后立即退出函数
		}

		if (奖励仙级索引 > 当前仙级) {
			cm.sendOk("你的仙级尚未达到，无法领取该奖励！");
			cm.dispose();
			return; // 确保在发送消息后立即退出函数
		}

		if (cm.getPlayer().getOneTimeLog(标识) == 1) { // 判断是否领过
			cm.sendOk("#b你已经领过了！");
			cm.dispose();
			return; // 确保在发送消息后立即退出函数
		}
            var 奖励描述 = ""; // 用于存储奖励描述
            for (var j = 0; j < 奖励物品列表.length; j++) { // 遍历奖励物品列表
                if (奖励物品列表[j][2] == 仙级奖励[仙级][0]) { // 判断物品标识
                    if (奖励物品列表[j][0] == 0) { // 如果是金币
                        奖励描述 += "点券 x" + 奖励物品列表[j][1] + " ";
                        cm.gainNX(+奖励物品列表[j][1]); // 给金币
                    } else { // 如果是物品
						var 物品名称 = cm.getItemName(奖励物品列表[j][0]) || "轮回碑石"; // 如果获取不到名称，使用默认描述
                        奖励描述 += 物品名称 + " x" + 奖励物品列表[j][1] + " ";
                        cm.gainItem(奖励物品列表[j][0], 奖励物品列表[j][1], true); // gainItem
                    }
                }
            }
        cm.getPlayer().setOneTimeLog(标识); // 给永久记录
        var playerName = cm.getPlayer().getName(); // 获取玩家名称
        cm.全服漂浮喇叭("[成长基金] " + playerName + " 领取了 " + 仙级奖励[仙级][0] + " 飞升奖励： " + 奖励描述, 5121000); // 发送全服喇叭，包含奖励详情
		cm.getPlayer().dropMessage(5, "领取成长基金：" + 奖励描述 +"");   //红字私聊提示
    //    cm.sendOk("#b成功领取！");
    //    cm.dispose();
		status = -1;      // 重新显示奖励列表页面--如果不需要放开上面连拍注射下面两排
		start(); // 重新调用 start 函数，从头开始整个对话流程
	}
}

function getxmwnjlc(log) {
    return getxmwnjljsc(log);
}

function getxmwnjljsc(jiluid) {
    var xmsjfh = 0;
    zhjsid = cm.getPlayer().getId();
    var conn = cm.getConnection();
    var sql = "SELECT * FROM xmwnjl WHERE characterid = " + zhjsid + " AND bossid = '" + jiluid + "' ;";
    var pstmt = conn.prepareStatement(sql);
    var result = pstmt.executeQuery();		
    if (result.next()) {
        xmsjfh = result.getInt("count");
    } 
    result.close();
    pstmt.close();
    conn.close();
    return xmsjfh;
}