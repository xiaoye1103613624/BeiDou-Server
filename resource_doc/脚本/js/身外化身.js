var 表情大笑 = "#fUI/GuildBBS/GuildBBS/Emoticon/Basic/2#"; // 表情大笑/1哭/0微笑 
var xx = 0;
var 道具代码 = 3604010;
var 道具代码1 = 3602107;
var status = -1;
var 系统基础克隆伤害 = 1; // 系统基础克隆伤害设置为1%
var 最大化身数量 = 3; // 设置化身数量的最大上限为 2

function kelo(cm) {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            if (cm.getPlayer().getAccYjLog("化身") == null || cm.getPlayer().getAccYjLog("化身") < 1) {
                cm.getPlayer().setAccYjLog("化身", 1); // 设置 "化身" 键的值为 1（可开启的分身数量）
                if (cm.getPlayer().getFenShen() == 0) {
                    cm.getPlayer().setFenShen(系统基础克隆伤害); // 设置默认分身伤害为1%
                }
                var textz = "#v1702169#   #e#r欢迎来到 - 身外化身修炼地  #v1702165##k#n\r\n";
                textz += "#r#e----------------------------------------------#k#n\r\n";
                cm.sendOk(textz + "#r#e[" + cm.getName() + "]#k#n欢迎来到#r#e" + cm.开服名称() + "#k#n身外化身修炼场。\r\n由于您是第一次来到本场所，在此表示感谢您的支持！\r\n#r特意赠送您[化身一个][化身伤害1%]#k\r\n点击确定，与我重新对话开启化身详细功能！");
                cm.dispose();
                return;
            }
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
            var currentCloneSize = cm.getPlayer().getCloneSize();
            var currentDamage = cm.getPlayer().getFenShen(); // 当前分身伤害
			// 检查玩家背包中是否已经有道具ID 2022506
			if (!cm.haveItem(2022506)) {
            // 如果没有该道具，则赠送一个
            cm.gainItem(2022506, 1);
			cm.getPlayer().dropMessage(5, "检测到你背包没有“召唤分身秘法”特赠送你一个，如果遗失可以在此补领！");   //显示在聊天框 的红色个人提示
			}
            text += "     #v1702169#   #e#r欢迎来到 - 身外化身修炼系统  #v1702165##k#n\r\n";
            text += "#r#e----------------------------------------------#k#n\r\n";
            text += "当前伤害：#r[" + currentDamage + "%]#k      已开启数：#r[" + currentCloneSize + "]#k      可开启数：#r[" + cm.getPlayer().getAccYjLog("化身") + "]#k#n\r\n";
            text += "#r#e----------------------------------------------#k#n\r\n";
            text += "  #L0##k" + 表情大笑 + "#r开启身外化身#l";
            text += "			#L3##k" + 表情大笑 + "#b关闭身外化身#l\r\n\r\n\r\n";
            text += "  #L2##k" + 表情大笑 + "#r提升化身伤害#l";
            text += "			#L1##k" + 表情大笑 + "#b增加化身数量#l\r\n\r\n\r\n";
            text += "		  #d#e说明：伤害提升最高[100%]本体伤害\r\n";
		//	text += "		  #d#e说明：伤害提升最高[100%]本体伤害\r\n	           化身最多可创建[" + 最大化身数量 + "]个\r\n";
            cm.sendSimple(text);
        } else if (status == 1) {
            if (selection == 0) {
                var currentCloneSize1 = cm.getPlayer().getCloneSize() + 1;
                var currentMaxCloneSize = cm.getPlayer().getAccYjLog("化身");

                if (cm.getPlayer().getCloneSize() >= cm.getPlayer().getAccYjLog("化身")) {
                    cm.sendOk("你可启动的化身上限为[" + cm.getPlayer().getAccYjLog("化身") + "]，目前已经达到上限。");
                    cm.dispose();
                    return;
                } else {
                    var currentDamage = cm.getPlayer().getFenShen(); // 当前分身伤害
                    cm.getPlayer().cloneLook(); // 创建分身时设置分身的伤害值
					
					cm.sendOk("成功开启\r\n已为你开启化身第：[" + currentCloneSize1 + "]个\r\n当前伤害为：[" + currentDamage + "%]\r\n");
                //    cm.sendOk("成功开启\r\n已为你开启化身第：[" + currentCloneSize1 + "]个\r\n当前伤害为：[" + currentDamage + "%]\r\n#r#e----------------------------------------------#k#n\r\n#b特别说明：#r换线后化身自动消失，需要重新开启！\r\n#r#e----------------------------------------------#k#n\r\n");
                    cm.dispose();
                    return;
                }
            } else if (selection == 1) {
                var current化身 = cm.getPlayer().getAccYjLog("化身");
                if (current化身 >= 最大化身数量) {
                    cm.sendOk("化身数量已达到最大值 " + 最大化身数量 + "，无法再增加！");
                    cm.dispose();
                    return;
                }
                if (cm.haveItem(道具代码1, 1) == false) {
                    cm.sendOk("你没有#r#v" + 道具代码1 + "##z" + 道具代码1 + "##k无法提升数量。");
				//	cm.sendOk("管理员暂未开放此功能，无法提升数量。");
                    cm.dispose();
                    return;
                }
                cm.gainItem(道具代码1, -1);
                cm.getPlayer().setAccYjLog("化身", cm.getPlayer().getAccYjLog("化身") + 1);
                cm.sendOk("成功增加化身数量！");
                cm.dispose();
            } else if (selection == 2) {
                var txt = "     #v1702169#   #e#r欢迎来到 - 身外化身修炼系统  #v1702165##k#n\r\n";
                txt += "#r#e----------------------------------------------#k#n\r\n";
                txt += "当前化身伤害：#r[" + cm.getPlayer().getFenShen() + "%]#k       #n\r\n";
                txt += "#r#e----------------------------------------------#k#n\r\n";
                txt += "提升伤害需要提交#v" + 道具代码 + "#最少1个，当前拥有#r[#c3604010#]#k个\r\n每一个可提升#r#e[1%]#k#n伤害\r\n";
                txt += "   \r\n";
                txt += "#b输入(1-99)提交数量：\r\n";
                cm.sendGetNumber(txt, 1, 1, 99);
                xx = 1;
            } else if (selection == 3) {
                cm.getPlayer().disposeClones();
                cm.sendOk('已关闭身外之身，如需要请重新开启');
                cm.dispose();
                return;
            }
        } else if (status == 2) {
            if (xx == 1) {
                if (cm.haveItem(道具代码, selection) == false) {
                    cm.sendOk("你没有这么多#v" + 道具代码 + "#无法提交");
                    cm.dispose();
                    return;
                } else if ((cm.getPlayer().getFenShen() + selection) >= 101) {
                    cm.sendOk("最多100%伤害,已达到伤害上限");
                    cm.dispose();
                    return;
                } else {
                    cm.gainItem(道具代码, -selection);
                    cm.getPlayer().setFenShen(cm.getPlayer().getFenShen() + selection);
                    var newDamage = cm.getPlayer().getFenShen();
                    cm.sendOk("恭喜你成功提升化身伤害#r[" + selection + "%]#k点。当前化身伤害：#r[" + newDamage + "%]#k");
                    cm.dispose();
                    return;
                }
            }
        }
    }
}