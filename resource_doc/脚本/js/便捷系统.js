var 服务中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/3#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var 黑桃1 = "#fUI/GuildMark/Mark/Pattern/00004018/1#";
var 黑桃2 = "#fUI/GuildMark/Mark/Pattern/00004018/2#";
var 黑桃3 = "#fUI/GuildMark/Mark/Pattern/00004018/3#";
var 黑桃4 = "#fUI/GuildMark/Mark/Pattern/00004018/4#";
var 黑桃5 = "#fUI/GuildMark/Mark/Pattern/00004018/5#";
var 黑桃6 = "#fUI/GuildMark/Mark/Pattern/00004018/6#";
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

            text += ""+dd+"\r\n\t\t\t"+服务中心+"\r\n"
		    text +=""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n"

            text += "\t #L1##r#e"+黑桃1+"随 身 仓 库"+黑桃1+"#l"
            text += "\t#L2##r#e"+黑桃2+"增 加 血 蓝"+黑桃2+"#l\r\n\r\n"

            text += "\t#L3##r#e"+黑桃3+"删 除 物 品"+黑桃3+"#l"
            text += "\t#L5##r#e"+黑桃4+"伤 害 皮 肤"+黑桃4+"#l\r\n\r\n"
            //text += "\t#L4##r#e"+黑桃4+"批 量 删 除"+黑桃4+"#l\r\n\r\n"
            //text += "\t\t\t#L5##r#e"+黑桃4+"伤 害 皮 肤"+黑桃4+"#l\r\n\r\n"
		    text +=""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n"

            cm.sendSimple(text);
        } else if (selection == 1) {
            cm.openNpc(9900004, "仓库");
			
        } else if (selection == 2) {
            cm.openNpc(9900004, "洗血洗蓝");
			
        } else if (selection == 3) {
            cm.openNpc(9900004, "删除物品");
			
        } else if (selection == 4) {
            cm.openNpc(9900004, "删除24格");
			
        } else if (selection == 5) {
            cm.openNpc(9000431, "分身的导航");
			
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


