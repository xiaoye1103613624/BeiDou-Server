var 技能中心 = "#fEffect/CharacterEff1.img/QQ1408745/2/2#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
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

            text += ""+dd+"\r\n\t\t\t"+技能中心+"\r\n"
		    text +=""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n"

            text += "\t\t\t  #L1##r#e#v2022507#赞 助 技 能#v2022507##l\r\n\r\n"
            text += "\t\t\t#L2##r#e#v5680126#技 能 突 破#v5680126##l\r\n\r\n"

            text += "\t\t\t#L3##r#e#v2616303#五 转 神 技#v2616303##l\r\n\r\n"
		    text +=""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n"

            cm.sendSimple(text);
        } else if (selection == 1) {
            cm.openNpc(9900004, "累计技能");
			
        } else if (selection == 2) {
            cm.openNpc(9900004, "更换职业");
			
        } else if (selection == 3) {
            cm.openNpc(9900004, "五转神技");
			
        } else if (selection == 4) {
            cm.openNpc(9000431, 14);
			
        } else if (selection == 5) {
            cm.openNpc(9000431, "回收系统");
			
        } else if (selection == 6) {
            cm.openNpc(9000431, "查询物品");
			
        } else if (selection == 7) {
            cm.openNpc(9000431, "删除物品");
			
        } else if (selection == 8) {
            cm.openNpc(9000431, "删除24格");
			
        } else if (selection == 9) {
            cm.openNpc(9300003, 0);

        } else if (selection == 10) {
            cm.openNpc(9000431, 20);
		}
    }
}


