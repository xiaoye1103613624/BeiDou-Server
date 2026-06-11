var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var 师徒系统A = "#fEffect/CharacterEff1.img/QQ1408745/0/7#";
var 箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 心 = "#fUI/GuildMark.img/Mark/Etc/00009001/14#";
var levelup = "#fEffect/BasicEff.img/ItemLevelUp/20#";
var levelup2 = "#fEffect/BasicEff.img/ItemLevelUp/21#";
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
             selStr += "你的师傅是:#r" + cm.角色ID取名字Z(cm.getBossRank("推广员", 2)) + "#k#n\r\n";
			
			selStr += "\t#L2#" + 箭头 + "#b查看我的徒弟#l#k\r\n\r\n";
			selStr += "\t#L5#" + 箭头 + "#b师徒奖励说明#l#k\r\n";
        }else{
			selStr += "\t#L2#" + 箭头 + "#b查看我的徒弟#l#k\r\n\r\n";{
			selStr += "\t#L1#" + 箭头 + "#b输入师傅角色码#r#l#k\r\n\r\n";
			}
			selStr += "\t#L5#" + 箭头 + "#b师徒奖励说明#l#k\r\n";
			
		}
        if (cm.getPlayer().getLevel() >= 30 && cm.getBossRank("推广员", 2) > 0 && cm.getBossRank("30徒弟奖励", 2) <= 0) {
            selStr += "#L4#" + 箭头 + "#b徒弟30级奖励#l#k\r\n";
        }
        if (cm.getPlayer().getLevel() >= 150 && cm.getBossRank("推广员", 2) > 0 && cm.getBossRank("150徒弟奖励", 2) <= 0) {
            selStr += "#L3#" + 箭头 + "#b徒弟150级奖励#l#k\r\n";
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
				//cm.gainNX(20000);
				//cm.gainItem(2460005, 20); 
				cm.setmoneyb(+10); 
				//cm.gainItem(2049124, 4); 
				cm.gainItem(2049104, 1); 
				//cm.gainMeso(200000000);
                cm.setBossRankCount("150徒弟奖励", 1);
                cm.说明文字("恭喜你领取 #r出师#k 奖励。");
				cm.全服黄色喇叭("师徒系统" + " : [" + cm.getPlayer().getName() + "]成功出师。获得巨额奖励!");
                cm.对话结束();
                break;
            case 4:
				//cm.gainNX(20000);
				//cm.setmoneym(cm.getmoneym()+100)
				cm.getPlayer().setlpjf(cm.getPlayer().getlpjf()+10);
				//cm.gainItem(2460005, 20); 
				//cm.gainItem(2049124, 4); 
				//cm.gainItem(2049104, 1); 
				//cm.gainMeso(200000000);
                cm.setBossRankCount("30徒弟奖励", 1);
                cm.说明文字("恭喜你领取 #r出师#k 奖励。");
				cm.全服黄色喇叭("师徒系统" + " : [" + cm.getPlayer().getName() + "]成功获得30级巨额奖励!");
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
                        text += i == 0 ? "#b" : i == 1 ? "#b" : i == 2 ? "#b" : "";
                        text += "\t #r" + (i + 1) + "#k#n.[ ";
                        text += info.getCname() + " ]";
                        for (var j = 16 - info.getCname().getBytes().length; j > 0; j--) {
                            text += " ";
                        }
                        text += "\t#bLv." + cm.角色名字取等级(info.getCname()) + "";
                        text += "#k";
                        text += "\r\n";
						if(cm.角色名字取等级(info.getCname())>=150&&cm.getAcLog(info.getCname()+"150奖励")==0){
							//cm.gainNX(20000);
							cm.gainItem(2049104, 5); 
							cm.gainItem(4310174, 5); 
							cm.setAcLog(info.getCname()+"150奖励");
							cm.全服黄色喇叭("师徒系统" + " : [" + cm.getPlayer().getName() + "]的徒弟成功出师。师傅获得巨额奖励!");
						}
                    }
                }
                cm.sendOkS(text, 3);
                cm.对话结束();
                break;
            case 4:
				//cm.setBossRankCount9("长生币",返利);
                //cm.Gaincharacterz("" + 推广码 + "", 300, -返利);
				cm.gainNX(返利);
                cm.说明文字("恭喜你领取 #r" + 返利 + "#k 返利。");
				cm.setBossRank9(cm.getPlayer().id,cm.getPlayer().name,"返利金额",2,-返利);
                cm.对话结束();
                break;
            case 5:
            cm.sendOk("\t#r#e师门系统介绍：#n\r\n徒弟等级：必须大于10级小于140级\r\n师父等级：必须150级以上\r\n\r\n\r\n#b徒弟奖励：\r\n 达到30级可获取奖励\r\n #r10 累计赞助积分\r\n #b达到150级可获取奖励\r\n #r10 元宝，#z2049104##v2049104#*1\r\n\r\n\r\n#b 师傅出师奖励\r\n#r#z4310174##v4310174#*5，#z2049104##v2049104#*5\r\n徒弟赞助，师父获得20%返还奖励\r\n");//110级师傅【1000】点券，徒弟【1000】点券\r\n120级师傅【2000】点券，徒弟【1000】点券\r\n
            cm.dispose();
                break;
        }
    }
}