var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var 师徒系统A = "#fEffect/CharacterEff1.img/QQ1408745/0/7#";
var 箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 心 = "#fUI/GuildMark.img/Mark/Etc/00009001/14#";
var levelup = "#fEffect/BasicEff.img/ItemLevelUp/20#";
var levelup2 = "#fEffect/BasicEff.img/ItemLevelUp/21#";

// 仙级等级数字到汉字的映射
var levelToChinese = {
    0: "凡人",
    1: "筑基",
    2: "金丹",
    3: "元婴",
    4: "出窍",
    5: "分神",
    6: "合体",
    7: "渡劫",
    8: "大乘",
    9: "天仙",
    10: "仙君",
    11: "玄仙",
    12: "仙帝",
    13: "神人",
    14: "神将",
    15: "神君",
    16: "神帝",
    17: "神皇",
    18: "神尊",
    19: "圣人",
    20: "至尊",
    21: "主宰",
	22: "永恒",
	23: "创世",
	24: "超脱",
};

function start() {
    status = -1;

    action(1, 0, 0)
}
function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.对话结束();
        return
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }
    var 推广码 = cm.getPlayer().id;
    var 返利 = cm.getBossRank9(cm.getPlayer().id,"返利金额",2);
    if (status <= 0) {
        var
		// selStr = "\r\n   " + 心 + " " + 心 + "  " + 心 + "  " + 心 + " #r#e < 游戏推广 > #k#n " + 心 + "  " + 心 + "  " + 心 + " " + 心 + "\r\n\r\n";
        //selStr = "\t\t\t\t\t#e#r师徒系统#n#k\r\n\r\n";
		selStr = "\t\t  "+师徒系统A+"\r\n\r\n";
            //text += ""+蓝色小喇叭+" 欢迎来到开心冒险岛抽奖中心\r\n\r\n";#L2#装备抽奖#v3992036##l  
		selStr +=""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n\r\n"
        // selStr +="#d邀请玩家可以获得赞助的#r10%#d点券返利\r\n#k\r\n\r\n";
        //显示收到的赞助返利
        // if (返利 >= 0) {
            // selStr += "\t\t\t\t邀请码返利:#r" + 返利 + "#k#n\r\n";
        // }
        //显示自己的推广码
        selStr += "\t#b你的角色码: #e#r" + 推广码 + "#k#n\r\n\r\n";
        //判断是否有推广员
        if (cm.getBossRank("推广员", 2) > 0) {
             selStr += "\t你的师傅是:#r" + cm.角色ID取名字Z(cm.getBossRank("推广员", 2)) + "#k#n\r\n";
			
			selStr += "\t#L2#" + 箭头 + "#b查看我的徒弟（徒弟出师点击即可领取奖励）#l#k\r\n\r\n";
			selStr += "\t#L5#" + 箭头 + "#b师徒奖励说明#l#k\r\n";
        }else{
			selStr += "\t#L2#" + 箭头 + "#b查看我的徒弟#l#k\r\n\r\n";{
			selStr += "\t#L1#" + 箭头 + "#r输入师傅角色码#r#l#k\r\n\r\n";
			}
			selStr += "\t#L5#" + 箭头 + "#b师徒奖励说明#l#k\r\n";
			
		}
        if (cm.getPlayer().getLevel() >= 150 && cm.getBossRank("推广员", 2) > 0 && cm.getBossRank("150徒弟奖励", 2) <= 0) {
            selStr += "\r\n\t#L4#" + 箭头 + "#r领取150级奖励#l#k\r\n";
        }
        if (cm.getPlayer().getLevel() >= 200 && cm.getBossRank("推广员", 2) > 0 && cm.getBossRank("200徒弟奖励", 2) <= 0) {
            selStr += "\r\n\t#L3#" + 箭头 + "#r领取200级奖励#l#k\r\n";
        }
        cm.sendSimple(selStr)
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.对话结束();
                //这里填写推广码二级分支
                cm.打开NPC(9900004, "角色码");
                break;
            case 0:
                cm.对话结束();
                cm.openNpc(9900004,"聚合功能");
                break;
            case 3: 
				cm.setmoneyb(+10); //元宝
				cm.gainItem(2022699, 1); 
				cm.gainItem(2049104, 1); 
                cm.setBossRankCount("200徒弟奖励", 1);
				cm.getPlayer().dropMessage(5, "成功出师：元宝+10");   //弹窗提示
                cm.说明文字("恭喜你领取 #r出师#k 奖励。");
				cm.全服黄色喇叭("师徒系统" + " : [" + cm.getPlayer().getName() + "]领取出师奖励，获得元宝+10，高级正向混沌卷轴*1，恶魔卷轴*1!");//徒弟奖励
                cm.对话结束();
                break;
            case 4:
				cm.getPlayer().setlpjf(cm.getPlayer().getlpjf()+10); //累计赞助
				cm.gainItem(2049122, 2); 
				cm.gainItem(2340000, 2); 
                cm.setBossRankCount("150徒弟奖励", 1);
				cm.getPlayer().dropMessage(5, "领取成功：累计赞助+10");   //弹窗提示
                cm.说明文字("恭喜你领取 #r师门#k 奖励。");
				cm.全服黄色喇叭("师徒系统" + " : [" + cm.getPlayer().getName() + "]领取150级师门奖励，累计赞助+10，正向混沌*2，祝福卷轴*2!");//徒弟奖励
                cm.对话结束();
                break;
            case 2:
                var text = "\t#r" + cm.getChar().getName() + "#k 的徒弟：#n\r\n\r\n";
                var rankinfo_list = cm.getBossRankCountTop("" + cm.getPlayer().id + "");
                if (rankinfo_list != null) {
                    for (var i = 0; i < rankinfo_list.size(); i++) {
                        if (i == 100) {
                            break;
                        }
                        var info = rankinfo_list.get(i);
                        var 徒弟名字 = info.getCname();
                        var 徒弟等级 = cm.角色名字取等级(徒弟名字);
                        var 仙级数字 = getPlayerXianLevel(徒弟名字);
                        var 仙级名称 = levelToChinese[仙级数字] || "凡人";

                        text += i == 0 ? "#b" : i == 1 ? "#b" : i == 2 ? "#b" : "";
                        text += "\t #r" + (i + 1) + "#k#n.[ ";
                        text += 徒弟名字 + " ]";
                        for (var j = 16 - 徒弟名字.getBytes().length; j > 0; j--) {
                            text += " ";
                        }
                        text += "\t#r" + 仙级名称 + "    #bLv." + 徒弟等级 + "#k";
                        text += "#k";
                        text += "\r\n";
						
						var 仙级 = getPlayerXianLevel(徒弟名字);
                        if (徒弟等级 >= 200 && 仙级 >= 1 && cm.getAcLog(徒弟名字 + "200奖励") == 0) {
                            cm.gainItem(2049104, 5);
                            cm.gainItem(4000487, 5);
                            cm.setAcLog(徒弟名字 + "200奖励");
                            cm.全服黄色喇叭("师徒系统" + " : [" + cm.getPlayer().getName() + "] 成功领取 徒弟 [" + 徒弟名字 + "] 的出师奖励!");
                        }
                    }
                }
                cm.sendOkS(text, 3);
                cm.对话结束();
                break;
            case 6:
				cm.gainNX(返利);
                cm.说明文字("恭喜你领取 #r" + 返利 + "#k 返利。");
				cm.setBossRank9(cm.getPlayer().id,cm.getPlayer().name,"返利金额",2,-返利);
                cm.对话结束();
                break;
            case 5:
            cm.sendOk("\t#r#e师门系统介绍：#n\r\n徒弟等级：必须大于10级小于150级\r\n师父等级：必须150级以上\r\n\r\n " + 箭头 + " #b徒弟奖励：\r\n\r\n 达到150级可获取奖励\r\n #r10 累计赞助，#z2049122##v2049122#*2，#z2340000##v2340000#*2\r\n\r\n #b达到200级可获取奖励\r\n #r10 元宝，#z2022699##v2022699#*1，#z2049104##v2049104#*1\r\n\r\n#b " + 箭头 + " 师傅出师奖励(#d要求：徒弟达到筑基#b)\r\n\r\n #r20 累计赞助(#b需联系客服发放#r)\r\n\r\n #z4000487##v4000487#*5，#z2049104##v2049104#*5\r\n\r\n #d徒弟赞助，师父获得20%返还奖励，限首次，需联系客服发放\r\n");//110级师傅【1000】点券，徒弟【1000】点券\r\n120级师傅【2000】点券，徒弟【1000】点券\r\n
            cm.dispose();
                break;
        }
    }
}

function getPlayerXianLevel(playerName) {
    var conn = cm.getConnection();
    var sql = "SELECT x.count FROM xmwnjl x INNER JOIN characters c ON x.characterid = c.id WHERE c.name = ? AND x.bossid = 'XM飞升系统_仙级'";
    var pstmt = conn.prepareStatement(sql);
    pstmt.setString(1, playerName);
    var rs = pstmt.executeQuery();
    var level = 0; // 默认为凡人
    if (rs.next()) {
        level = rs.getInt("count");
    }
    rs.close();
    pstmt.close();
    conn.close();
    return level;
}