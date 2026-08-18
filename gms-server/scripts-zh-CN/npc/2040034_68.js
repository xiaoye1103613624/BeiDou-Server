var FY0 = "┏━━━━━━━━━━━┓";
var FY1 = "┃       079MAX4       ┃";
var FY2 = "┃ 脚本仿制  　定制脚本 ┃";
var FY3 = "┃ 技术支持 　 游戏顾问 ┃";
var FY4 = "┃ ＷＺ添加　  地图制作 ┃";
var FY5 = "┃ 售登陆器    售下载器 ┃";
var FY6 = "┣━━━━━━━━━━━┫";
var FY7 = "┃唯一QQ: 782772124┃";
var FY8 = "┗━━━━━━━━━━━┛";
var FY9 = "怀旧岛单机交流群免费分享";

var status = 0;

var itemList1 = [

	


[1072534, 10, 1, 1],
[1052970, 10, 1, 1],
[4000089, 10, 1, 1],
[4031095, 10, 1, 1],
[4031142, 10, 1, 1],
[4001752, 10, 1, 1]




];
var useNx = 400;
var sel0 = -1;
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
		cm.dispose();
    } else {
        status--;
		cm.dispose();
    }

    if (status == 0) {
    	var txt = "#d\t\t\t#b欢迎查看『玩具副本』奖励列表#n#k\r\n\r\n";
		txt += "\t#r通关奖励列表 \r\n";
		cm.dispose();
		
		var txt2 = "";
		for (var i = 0; i < itemList1.length;  i++){
			txt2 += "#i"+itemList1[i][0]+":#";
			cm.dispose();
		}
    	cm.sendSimple(txt + txt2);
		cm.dispose();
    }  
	cm.dispose();
}

