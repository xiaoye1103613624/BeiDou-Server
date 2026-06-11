var 转职系统 = "#fEffect/CharacterEff1.img/QQ1408745/1/5#";
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

            text += ""+dd+"\r\n\t\t\t"+转职系统+"\r\n"
		    text +=""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n"

            text += "#L1##r#e#v3605016#快 速 转 职#v3605017##l"
            //text += "\t\t\t#L2##r#e#v2180003#更 换 职 业#v2180003##l\r\n\r\n"

            text += " #L2##r#e#v2022518#一键满技能#v2022518##l\r\n\r\n"
		    text +=""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n"

            cm.sendSimple(text);
        } else if (selection == 1) {
            cm.openNpc(9900004, "快速转职");
			
        } else if (selection == 2) {
            cm.openNpc(9900004, "满技能");
			
        } else if (selection == 3) {
            cm.openNpc(9000442, 0);
			
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


