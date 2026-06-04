var 技能中心 = "#fEffect/CharacterEff1.img/QQ279934747/2/2#";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var 黑桃1 = "#fUI/GuildMark/Mark/Pattern/00004018/1#";
var 塔罗牌1 ="#fEffect/CharacterEff/1051296/1/0#";//蓝圆星
function start() {
    status = -1;

    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {

            //cm.sendOk("感谢你的光临！");
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
            var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }


            text += "                  "+黑桃1+"#k#e成长戒指强化中心"+黑桃1+"\r\n\r\n"
		    text +=""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n"

            text += "\t#L1##r#e"+塔罗牌1+"戒指强化"+塔罗牌1+"#l"
            text += "\t\t#L2##d#e"+塔罗牌1+"储存经验"+塔罗牌1+"#l\r\n\r\n"

		    text +=""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n"

            cm.sendSimple(text);
       } else if  (selection == 1) {
            cm.openNpc(9900004, "经验戒指");

			
        } else if (selection == 2) {
            cm.openNpc(9900004, "储存经验");




		}
    }
}

