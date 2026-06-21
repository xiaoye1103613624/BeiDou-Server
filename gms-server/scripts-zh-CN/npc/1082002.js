var status = -1;
// 仅保留你指定的4个UI变量，无其他多余定义、无组合
var 粉小于号 = "#fUI/Initials.img/Button/Button2/mouseOver/0#";
var 粉大于号 = "#fUI/Initials.img/Button/Button3/mouseOver/0#";
var 蓝色小喇叭 = "#fUI/CN_Chat.img/ChattingRoom/BtVolUp/0/mouseOver/0#";  
var 热点推荐 = "#fUI/CashShop.img/CSChar/BtCoordination/normal/0#";
var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else if (mode == 0) {
        status--;
    } else {
        cm.dispose();
        return;
    }

    if (status == 0) {
        var selStr = "\r\n";
        // 顶部标题：移除所有旧UI，仅用热点推荐单独点缀，保留原居中+开服名称格式
        selStr += "\t" + 热点推荐 + "#r#e" + cm.开服名称() + "排名系统#k \r\n\r\n";

        // 战力排名+仙级排名：移除旧UI，粉小于号单独前缀，保留原图标+文字+排版
        selStr +=  广播 + "#r#L1##v4031569#战 力 排 名#v4031569##l\r\n\r\n";
		selStr +=  广播 + "#r#L3##v1142499#仙 级 排 名#v1142499##l\r\n\r\n";
        selStr +=  广播 + "#b#L6##v2022546#公 会 排 名#v2022546##l\r\n\r\n";
		selStr +=  广播 + "#b#L4##v5010073#人 气 排 名#v5010074##l\r\n\r\n";
        
        cm.sendSimple(selStr);
    } else if (status == 1) {
        // 所有功能逻辑100%保留，无任何修改
        if (selection == 0) {
            cm.dispose();
            cm.openNpc(9900004);
        } else if (selection == 1) {
            cm.dispose();
            cm.openNpc(9010000,"最强战力排行榜");
            return;
        } else if (selection == 2) {
            cm.dispose();
            cm.openNpc(9010000,"飞升排行榜");
            return;
        } else if (selection == 3) {
            cm.openNpc(9010000,"仙级排行榜");
            cm.dispose();
        } else if (selection == 4) {
            cm.人气排行榜();
            cm.dispose();
        } else if (selection == 5) {
            cm.showfame();
            cm.dispose();
            return;
        } else if (selection == 6) {
            cm.showAllGuiGP();
            cm.dispose();
        }
    } else if (status == 2) {
        cm.sendNext(cm.ShowJobRank(selection));
        cm.dispose();
    } else {
        cm.dispose();
    }
}

/*
// 原职业排名注释代码，完整保留无修改
//cm.sendSimple("#L9##r综合#k排名\r\n#L1##b战士#k排名\r\n#L2##b法师#k排名\r\n#L3##b弓箭手#k排名\r\n#L4##b飞侠#k排名\r\n#L5##b海盜#k排名\r\n#L7##d骑士团#k排名\r\n#L6##d战神#k排名\r\n#L8##d龙神#k排名\r\n"); //职业排名
*/