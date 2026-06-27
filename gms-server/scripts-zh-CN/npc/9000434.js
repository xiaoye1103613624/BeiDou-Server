var 礼包物品 = "#v1302000#";
var x1 = "1302000,+1";// 物品ID,数量
var x2;
var x3;
var x4;
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 蓝色小兔子 = "#fUI/UIWindow/Quest/icon6/7#";
var 蓝色小兔子 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 礼包物品 = "#v1302000#";
var x1 = "1302000,+1";// 物品ID,数量
var x2;
var x3;
var x4;
var add = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";//红色右箭头
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";//蓝色右箭头
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";//选择道具
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 美化new = "#fUI/UIWindow/Quest/icon5/1#";
var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var 挑战中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/10#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = "" + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + "\r\n";
var 红色箭头 = "#fEffect/CharacterEff/1112908/0/1#";  //彩光3
var ttt1 = "#fEffect/CharacterEff/1062114/1/0#";  //爱心
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 剑1 = "#fUI/GuildMark.img/Mark/Etc/00009003/1#";
var 剑4 = "#fUI/GuildMark.img/Mark/Etc/00009003/4#";
var 剑7 = "#fUI/GuildMark.img/Mark/Etc/00009003/7#";
var 剑11 = "#fUI/GuildMark.img/Mark/Etc/00009003/11#";
var 剑14 = "#fUI/GuildMark.img/Mark/Etc/00009003/14#";
var 剑16 = "#fUI/GuildMark.img/Mark/Etc/00009003/16#";
var 表情1 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/1#";
var 表情2 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/2#";
var 蓝色小兔子 = "#fEffect/CharacterEff.img/1112960/3/1#";
var 小红星 = "#fEffect/CharacterEff.img/1112926/0/0#";
var 小蓝星 = "#fEffect/CharacterEff.img/1112925/0/0#";
var 音符3 = "#fEffect/CharacterEff.img/1112949/2/0#";
var 蓝色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/0/0#";
var 红色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/4/0#";
var 成功了 = "#fEffect/BasicEff.img/Fishing/6#";

function start() {
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
            /*if(cm.getJob() >= 0 && cm.getJob()<= 522 && cm.hasSkill(1017) == false){
            cm.teachSkill(1017,1,1);
            }else if(cm.getJob() >=1000 || cm.getJob() <= 2112 && cm.hasSkill(20001019) == false){
            cm.teachSkill(20001019,1,1);
            }*/
            var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
            text += "" + dd + "\r\n\t\t\t" + 挑战中心 + "\r\n" + 群粉心 + "";

            //text += ""+小红星+小蓝星+小红星+小蓝星+小红星+小蓝星+小红星+小蓝星+小红星+小蓝星+小红星+小蓝星+小红星+小蓝星+小红星+小蓝星+小红星+小蓝星+小红星+"\r\n"
            if (cm.getPlayer().getOneTimeLog("总福利BOSS") < 50 && cm.getPlayer().getBossLog("福利BOSS") < 10) {
                text += "\t\t\t\t#d#L1##r#e" + 剑14 + "福 利 BOSS" + 剑14 + "#l\r\n\r\n";
            } else {
            }
            text += "\r\n\t#L0##r#e" + 剑4 + "练 功 房" + 剑4 + "#l\t\t#L6##r#e" + 剑1 + "限时BOSS" + 剑1 + "#l\r\n\r\n\r\n"//3

            text += "\t#L2##d" + 剑11 + "高级BOSS" + 剑11 + "#l\t\t#L4##d" + 剑7 + "女神BOSS" + 剑7 + "#l\r\n\r\n\r\n"//3
            //text += "\t#L3##d" + 剑14 + "终级BOSS" + 剑14 + "#l\t\t#L5##d" + 剑16 + "神级BOSS" + 剑16 + "#l\r\n"//3
            text += "\t#L8##b" + 剑1 + "BOSS传送" + 剑1 + "#l\r\n\r\n\r\n"//新增:跨频道查看BOSS存活状态并传送

            cm.sendSimple(text);

        } else if (selection == 0) {//签到系统[完成]

            cm.openNpc(9000434, "VIP练功房");
        } else if (selection == 1) {//每日跑商[完成]
            cm.openNpc(9000434, "福利BOSS1");

        } else if (selection == 2) {//在线奖励[完成]
            cm.openNpc(9000434, "高级BOSS");

        } else if (selection == 3) {//在线奖励[完成]
            cm.openNpc(9000434, "终级BOSS");

        } else if (selection == 4) {//每日副本[完成]
            cm.openNpc(9000434, "女神BOSS");

        } else if (selection == 5) {//每日BOSS[完成]
            cm.openNpc(9000434, "神级BOSS");

        } else if (selection == 6) {//每日通缉[完成]
            cm.openNpc(9000434, "限时BOSS");

        } else if (selection == 9) {//每日收集[完成]
            cm.openNpc(9000434, "每日狩猎");

        } else if (selection == 8) {//新增：BOSS传送系统-跨频道查看BOSS存活状态并传送
            cm.openNpc(9000434, "BOSS传送系统");

        } else if (selection == 7) {//
            cm.warp(109040001, 0);
            cm.dispose();
        } else if (selection == 1111999) {//
            cm.dispose();
            cm.openNpc(9310071, 0);
            cm.dispose();
        }
    }
}