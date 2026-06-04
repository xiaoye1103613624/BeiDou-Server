var 小雪花 = "#fEffect/CharacterEff/1003393/0/0#";
var  a1 = "#fUI/ChatBalloon.img/28/w#";//右上
var  a2 = "#fUI/ChatBalloon.img/28/e#";//上中
var  a8 = "#fUI/ChatBalloon.img/19/nw#";//右上
var  a9 = "#fUI/ChatBalloon.img/19/ne#";//右上
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
           // cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        }else {
            status--;
        }
        if (status == 0) {
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
				//cm.getPlayer().getGuild() == null  //判断没有家族
				//cm.getPlayer().getGuild().getLevel() //判断家族等级
				//cm.getPlayer().getGuild().getGP() //判断家族积分
				//cm.getPlayer().gainGuiExp(50);  
				//cm.playerMessage("家族贡献值 +" + 50)
				//cm.playerMessage("贡献值 +" + 50)
				//cm.setBossRankCount("贡献值",50);
				//cm.gainGP(50);
			text = "#r#e┌\t\t      ─  家 族 建 设  ─  \t\t\t┐#n\r\n\r\n";	
          //  text += "    成员的贡献越多，家族等级越高\r\n            "
		  //  text += "            就能享受更好的家族福利。\r\n#n"
			text += " #L1##r[ 家族建设任务 ] 每周五环大量建设和奖励#l\r\n\r\n";
			text += " #L2##d[ 查看成员贡献 ] 查看家族成员贡献排名#l\r\n\r\n";
			text += " #L3##d[ 查看建设说明 ] 查看贡献说明#l\r\n\r\n";
			text += " #L4##b[ 家族福利商店 ] 家族商店#l\r\n\r\n";
			text += " #L5##d[ 家族总排名榜 ] 查看冒险岛家族总排行#l\r\n\r\n";
			text += " #L6##d[ 创 建 家 族 ]  家 族 传 送#l\r\n";
			text += "#r#e\r\n└\t\t\t\t\t\t\t\t\t\t\t┘#n";			
            cm.sendSimple(text);
        } else if (status == 1) {
            if (selection == 1) {
       if (cm.getPlayer().getGuild() == null) {
		    cm.sendOk("没有家族无法查看！");
            cm.dispose();
           } else {
			cm.dispose()
			cm.openNpc(9900004,12345222);
}
         }else if (selection == 6) {

			cm.dispose()
			cm.warp(200000301, 0);

           
		   }else if (selection == 2) {
       if (cm.getPlayer().getGuild() == null) {
		    cm.sendOk("没有家族无法查看！");
            cm.dispose();
           } else {
           
			cm.dispose()
			cm.showGuiExps();
			cm.playerMessage(1, "当前查看家族 ["+cm.getPlayer().getGuild().getName()+" 贡献排行]\r\n");
			cm.dispose()
}
            }else if (selection == 3) {
            cm.sendOk("- 建设说明：成员的贡献越多，家族等级越高，就能享受更好的家族福利。\r\n");
            cm.dispose();
            }else if (selection == 4) {
       if (cm.getPlayer().getGuild() == null) {
		    cm.sendOk("没有家族无法查看！");
            cm.dispose();
           } else {
            cm.dispose();
			cm.openNpc(9900004, "家族商店");
}
            }else if (selection == 5) {
			cm.dispose()
			cm.showAllGuiGP();
            }
			
       }
    }
}
