var 技能中心 = "#fEffect/CharacterEff1.img/QQ279934747/2/2#";
var 蘑菇 = "#fUI/UIWindow.img/Minigame/Common/mark#";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var 美化new = "#fUI/UIWindow/Quest/icon5/1#";
var 蓝心 = "#fEffect/CharacterEff/1022223/4/0#";
function start() {
    status = -1;

    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }
    else {
        if (status >= 0 && mode == 0) {
            cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        }
        else {
            status--;
        }
        if (status == 0) {
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }

            text += ""+蓝心+""+蓝心+""+蓝心+""+蓝心+""+蓝心+""+蓝心+""+蓝心+""+蓝心+""+蓝心+""+蓝心+""+蓝心+""+蓝心+""+蓝心+""+蓝心+""+蓝心+""+蓝心+""+蓝心+""+蓝心+""+蓝心+"\r\n"

            text += "\t\t\t#L1#  #d#e战士技能突破#l\r\n\r\n"
            text += "\t\t\t#L2##k#e法师技能突破#l\r\n\r\n"
            text += "\t\t\t#L3##r#e弓箭技能突破#l\r\n\r\n"
			text += "\t\t\t#L4##r#e飞侠技能突破#l\r\n\r\n"
			text += "\t\t\t#L5##r#e海盗技能突破#l\r\n\r\n"
	

            cm.sendSimple(text);
        } else if (selection == 1) {
          cm.openNpc(9900004, "技能1");

			
        } else if (selection == 2) {
          cm.openNpc(9900004, "技能2");

			
        } else if (selection == 3) {
          cm.openNpc(9900004, "技能3");

			
        } else if (selection == 4) {
           cm.openNpc(9900004, "技能4");

			
        } else if (selection == 5) {
           cm.openNpc(9900004, "技能5");


		}
    }
}


