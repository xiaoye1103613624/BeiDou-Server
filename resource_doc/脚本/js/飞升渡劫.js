var mi0 = "┏----------------------┓";
var mi1 = "┃     - XiaoMiMS -     ┃";
var mi2 = "┃ 脚本仿制  　定制脚本 ┃";
var mi3 = "┃ 技术支持 　 游戏顾问 ┃";
var mi4 = "┃ ＷＺ添加　  地图制作 ┃";
var mi5 = "┃----------------------┃";
var mi6 = "┃　唯一QQ:526703257    ┃";
var mi7 = "┗----------------------┛";
var mi8 = "请不要修改版权信息，否则脚本将会报错";

var xiaomi = {
	系统:"xiaomi",//支持服务端设置(xiaomi,yxh,ty,meng,congms)
	调试模式: 0,//当调试模式改为1时 将会忽略一些判断
	
	转生等级: 200, //转生至等级150
	转生等级模式: -1,//0为正常
	
	转生条件: 250,//到达255级才可以转生
	
};


//飞升所需的组
var xmxsz = new Array(
	//第一行为占位用，请勿操作
	{ 仙级: "凡人", 层数: 0, 属性点: 0 , 飞升奖励: Array([4000017,10],[0,500],[1,100],[2,200]) , 飞升率: 100 , 飞升材料: Array([4000000,10],[0,10000],[1,500],[2,1000]) , 渡劫率 : 100 , 渡劫材料: Array([4000001,10],[4000002,10],[0,200000],[1,1000],[2,5000])   },
	
	
	//这里开始后就阔以修改参数  奖励和材料数据格式 其他ID=物品类型  0-金币  1是点卷 2是抵用
	{ 仙级: "筑基", 层数: 2, 属性点: 10 , 飞升奖励: Array([2614006,1]) ,飞升率: 85 , 飞升材料: Array([4310143,10],[1,500],[2,1000]) , 渡劫率 : 90 , 渡劫材料: Array([4001084,1],[4001083,1])   },
	{ 仙级: "金丹", 层数: 3, 属性点: 10 , 飞升奖励: Array([2614006,2]) ,飞升率: 80 , 飞升材料: Array([3605011,15],[1,1000],[2,2000]) , 渡劫率 : 85 , 渡劫材料: Array([4031952,10],[3700290,10])   },
	{ 仙级: "元婴", 层数: 15, 属性点: 10 , 飞升奖励: Array([2614006,3]) ,飞升率: 90 , 飞升材料: Array([3605011,20],[1,2000],[2,4000]) , 渡劫率 : 70 , 渡劫材料: Array([4031952,30],[3700290,20])   },
	{ 仙级: "出窍", 层数: 20, 属性点: 10 , 飞升奖励: Array([2614012,3]) ,飞升率: 70 , 飞升材料: Array([3605011,25],[1,3000],[2,6000]) , 渡劫率 : 65 , 渡劫材料: Array([4031952,50],[3700290,30])   }, 
	
	{ 仙级: "分神", 层数: 25, 属性点: 10 , 飞升奖励: Array([2614012,1]) ,飞升率: 80 , 飞升材料: Array([3605011,30],[1,4000],[2,8000]) , 渡劫率 : 75 , 渡劫材料: Array([4031952,70],[3700290,50])   },  
	{ 仙级: "合体", 层数: 30, 属性点: 10 , 飞升奖励: Array([2614012,1]) ,飞升率: 75 , 飞升材料: Array([3605011,35],[1,5000],[2,10000]) , 渡劫率 : 90 , 渡劫材料: Array([4031952,100],[3605009,10])   },
	{ 仙级: "渡劫", 层数: 35, 属性点: 15 , 飞升奖励: Array([2614012,1]) ,飞升率: 65 , 飞升材料: Array([3605011,40],[1,6000],[2,12000]) , 渡劫率 : 80 , 渡劫材料: Array([4031952,300],[3605009,20])   },
	{ 仙级: "大乘", 层数: 40, 属性点: 15 , 飞升奖励: Array([2614012,1]) ,飞升率: 70 , 飞升材料: Array([3605011,50],[1,7000],[2,14000]) , 渡劫率 : 70 , 渡劫材料: Array([4031952,500],[3605009,30])   },
	
	{ 仙级: "天仙", 层数: 45, 属性点: 15 , 飞升奖励: Array([2614012,1]) ,飞升率: 85 , 飞升材料: Array([3605011,100],[1,8000],[2,16000]) , 渡劫率 : 65 , 渡劫材料: Array([4031952,700],[3605009,50])   },
	{ 仙级: "仙君", 层数: 50, 属性点: 15 , 飞升奖励: Array([2614012,1]) ,飞升率: 80 , 飞升材料: Array([3605011,150],[1,9000],[2,18000]) , 渡劫率 : 55 , 渡劫材料: Array([4031952,1000],[3700291,10])   },
	{ 仙级: "玄仙", 层数: 55, 属性点: 15 , 飞升奖励: Array([2614012,1]) ,飞升率: 65 , 飞升材料: Array([3605011,200],[1,10000],[2,20000]) , 渡劫率 : 60 , 渡劫材料: Array([4031952,3000],[3700291,30])   },
	{ 仙级: "仙帝", 层数: 60, 属性点: 15 , 飞升奖励: Array([2614012,1]) ,飞升率: 70 , 飞升材料: Array([3605011,250],[1,11000],[2,22000]) , 渡劫率 : 65 , 渡劫材料: Array([4031952,5000],[3700291,50])   },
	
	{ 仙级: "神人", 层数: 65, 属性点: 20 , 飞升奖励: Array([2614012,1]) ,飞升率: 75 , 飞升材料: Array([3700292,5],[1,22000],[2,34000]) , 渡劫率 : 90 , 渡劫材料: Array([4031952,15000],[3700294,10])   },
	{ 仙级: "神将", 层数: 70, 属性点: 20 , 飞升奖励: Array([2614012,1]) ,飞升率: 70 , 飞升材料: Array([3700292,5],[1,23000],[2,36000]) , 渡劫率 : 70 , 渡劫材料: Array([4031952,16000],[3700294,15])   },
	{ 仙级: "神君", 层数: 75, 属性点: 20 , 飞升奖励: Array([2614012,1]) ,飞升率: 65 , 飞升材料: Array([3700292,5],[1,24000],[2,38000]) , 渡劫率 : 60 , 渡劫材料: Array([4031952,17000],[3700294,20])   }, 
	{ 仙级: "神帝", 层数: 80, 属性点: 20 , 飞升奖励: Array([2614012,1]) ,飞升率: 60 , 飞升材料: Array([3700292,10],[1,25000],[2,40000]) , 渡劫率 : 90 , 渡劫材料: Array([4031952,18000],[3700294,25])   }, 
	{ 仙级: "神皇", 层数: 85, 属性点: 20 , 飞升奖励: Array([2614012,1]) ,飞升率: 90 , 飞升材料: Array([3700292,10],[1,26000],[2,42000]) , 渡劫率 : 70 , 渡劫材料: Array([4031952,19000],[3700294,30])   }, 
	{ 仙级: "神尊", 层数: 90, 属性点: 20 , 飞升奖励: Array([2614012,1]) ,飞升率: 70 , 飞升材料: Array([3700292,10],[1,27000],[2,44000]) , 渡劫率 : 75 , 渡劫材料: Array([4031952,20000],[3700294,40])   },  
	
	{ 仙级: "圣人", 层数: 95, 属性点: 30 , 飞升奖励: Array([2614012,1]) ,飞升率: 70 , 飞升材料: Array([3700292,15],[1,28000],[2,46000]) , 渡劫率 : 65 , 渡劫材料: Array([4321026,1000],[3700293,50])   },  
	{ 仙级: "至尊", 层数: 100, 属性点: 30 , 飞升奖励: Array([2614012,1]) ,飞升率: 70 , 飞升材料: Array([3700292,20],[1,29000],[2,48000]) , 渡劫率 : 60 , 渡劫材料: Array([4321026,2000],[3700293,100])   },  
	{ 仙级: "主宰", 层数: 200, 属性点: 30 , 飞升奖励: Array([2614007,1]) ,飞升率: 70 , 飞升材料: Array([3700292,25],[3994731,25],[1,88888],[2,88888]) , 渡劫率 : 60 , 渡劫材料: Array([4321026,3000],[3700293,200])   },// 3605020 卡飞升
	
	{ 仙级: "永恒", 层数: 999 , 属性点: 0 , 飞升奖励: Array([2350014,1]) ,飞升率: 70 , 飞升材料: Array([4001126,10000],[4000313,10000],[3994731,10],[1,100000],[2,100000]) , 渡劫率 : 60 , 渡劫材料: Array([4321026,10000],[3605012,200])   },
	{ 仙级: "创世", 层数: 3999, 属性点: 0 , 飞升奖励: Array([2550003,1]) ,飞升率: 70 , 飞升材料: Array([4001126,20000],[4000313,20000],[3994731,20],[1,100000],[2,100000]) , 渡劫率 : 60 , 渡劫材料: Array([4321026,20000],[3605012,400])   },
	{ 仙级: "超脱", 层数: 9999, 属性点: 0 , 飞升奖励: Array([3602000,1]) ,飞升率: 70 , 飞升材料: Array([4001126,30000],[4000313,30000],[3994731,30],[1,100000],[2,100000]) , 渡劫率 : 60 , 渡劫材料: Array([4321026,30000],[3605012,800])   }

//总层数2+3+15+20+25+30+35+40+45+50+55+60+65+70+75+80+85+90+95+100+999+3 999 = 6 093 层
/*
| 区间 | 累计层数  | 每层 AP | 小计 AP | 区间累计 AP |
|  --  | ----- | ----- | ----- | ------- |
| 筑基 | 2     | 10    | 20    | 20      |
| 金丹 | 3     | 10    | 30    | 50      |
| 元婴 | 15    | 10    | 150   | 200     |
| 出窍 | 20    | 10    | 200   | 400     |
| 分神 | 25    | 10    | 250   | 650     |
| 合体 | 30    | 10    | 300   | 950     |
| 渡劫 | 35    | 15    | 525   | 1 475   |
| 大乘 | 40    | 15    | 600   | 2 075   |
| 天仙 | 45    | 15    | 675   | 2 750   |
| 仙君 | 50    | 15    | 750   | 3 500   |
| 玄仙 | 55    | 15    | 825   | 4 325   |
| 仙帝 | 60    | 15    | 900   | 5 225   |
| 神人 | 65    | 20    | 1 300 | 6 525   |
| 神将 | 70    | 20    | 1 400 | 7 925   |
| 神君 | 75    | 20    | 1 500 | 9 425   |
| 神帝 | 80    | 20    | 1 600 | 11 025  |
| 神皇 | 85    | 20    | 1 700 | 12 725  |
| 神尊 | 90    | 20    | 1 800 | 14 525  |
| 圣人 | 95    | 30    | 2 850 | 17 375  |
| 至尊 | 100   | 30    | 3 000 | 20 375  |
| 主宰 | 200   | 30    | 6 000 | 26 375  |
| 永恒 | 999   | 0     | 0     | 26 375  |
| 创世 | 3 999 | 0     | 0     | 26 375  |
| 超脱 | 9 999 | 0     | 0     | 26 375  |
*/

);


//读取角色永久记录
function getxmwnjlc(log) {
	return getxmwnjljsc(log);
}

//写入角色永久记录
function gainxmwnjlc(log,cs) {
	gainxmwnjljsc(log,cs);
	return 0;
}


function xmfeishenglaba(仙级,层,总转生) {
		cm.喇叭(2," 玩家 <"+cm.getName()+">【"+仙级+" 突破 <"+层+"层> 】 目前总转生层数：<"+总转生+"层>");

	
}

//主页UI
function zhuyeUI() {
    var text = "";
    if (当前层 >= xmxsz[局仙级].层数) {
        text += "#v2022359##e#b是否需要渡劫！渡劫会让你进阶下一个仙级!\r\n"
        text += "" + jty + "#d当前仙级：【" + xmxsz[局仙级].仙级 + "】渡劫后升为 ：#r【" + xmxsz[局仙级 + 1].仙级 + "】\r\n"
        djcgl = xmxsz[局仙级 + 1].渡劫率;
        text += "" + jty + "#d渡劫成功率：#B" + djcgl + "[%]##n#b[" + djcgl + "]%\r\n"
        text += "\r\n#b请携带以下材料证明你有能力：\r\n"
        text += "#n" + getxmckdjsz(xmxsz[局仙级 + 1].渡劫材料);
        text += "\r\n"
        text += "#e#b#L1#" + hsyjt + "我已经通过试练[开始渡劫]#l\r\n"
    } else {
        text +=    "     #v2022365##e#b来吧，开始进行飞升(转生)吧！#n\r\n"
        //text += "飞升会清空之前的所能力值并重新计算！\r\n"
        xmJXJ = parseInt(100 / (xmxsz.length - 1) * 局仙级);
        text += "" + jty + "#d当前仙级：【#r" + xmxsz[局仙级].仙级 + "#d】#B" + xmJXJ + "[%]##n#b[" + xmJXJ + "]%\r\n"
        xmjcjd = parseInt(100 / xmxsz[局仙级].层数 * 当前层);
        text += "" + jty + "#d飞升阶层：【#r" + 当前层 + "/" + xmxsz[局仙级].层数 + "#d】#B" + xmjcjd + "[%]##n#b[" + xmjcjd + "]%\r\n"
        fscgl = xmxsz[局仙级].飞升率;
        text += "" + jty + "#d飞升成功率：#B" + fscgl + "[%]##n#b[" + fscgl + "]%\r\n"
        text += "" + jty + "#d本仙级飞升额外奖励属性：" + xmxsz[局仙级].属性点 + "\r\n"
        text += "" + jty + "#d飞升后将会重置能力值并且等级降至：" + xiaomi.转生等级 + " 级\r\n"
        text += " \r\n"
        text += "" + xzdj + "\r\n#b如果你要飞升，请给我带来以下试练材料\r\n"
        text += "#n" + getxmckdjsz(xmxsz[局仙级].飞升材料);
        text += "\r\n#b飞升获取的奖励：#r(请注意背包空间，丢失概不负责！)#d\r\n"
        text += "" + getxmckdjsz(xmxsz[局仙级].飞升奖励);
        text += "#e#b#L2#" + hsyjt + "我已经通过试练[开始飞升]#l\r\n"
    }
    return text;
}




var HSXXA = "#fUI/UIWindow.img/MonsterCarnival/icon0#";//红色星星
var LSXXB = "#fUI/UIWindow.img/MonsterCarnival/icon1#";//红色星星
var 首页标题 = "#b┣━━━━━━━ " + HSXXA + " #e修仙◇#r飞升#n " + LSXXB + " ━━━━━━━┫#k\r\n\r\n";

var jty ="#fUI/UIWindow.img/MonsterBook/arrowRight/normal/0#";//魔法箭头右
var jtz ="#fUI/UIWindow.img/MonsterBook/arrowLeft/normal/0#";//魔法箭头左
var qbtb = "#fUI/UIWindow.img/PvP/btWrite/mouseOver/0#";//铅笔图标
var 蓝加 = "#fUI/Basic.img/BtMax/mouseOver/0#";	

var add = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var hsyjt = "#fUI/UIWindow.img/Quest/icon9/0#";//红色右箭头
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";//蓝色右箭头
var xzdj = "#fUI/UIWindow.img/QuestIcon/3/0#";//选择道具


var 表情高兴 = "#fUI/GuildBBS/GuildBBS/Emoticon/Basic/2#";
var status = -1;
var selection;
var 彩虹 ="#fEffect/ItemEff/1071085/effect/walk1/2#";
var 积分 = new Array();
var 随机积分 = 积分[Math.floor(Math.random() * 积分.length)];
var xmml1 = 0;
var xmml2 = 0;
var xmml3 = 0;
var xmml4 = 0;

var pdfhz1 = false;
var pdfhz2 = true;

function start() {
	if (xiaomi.调试模式 == 0) {
    var xmshuxin = 0;
    for (var i = 1; i < xmxsz.length; i++) {
        xmshuxin += (xmxsz[i].属性点 * xmxsz[i].层数);
    }
	
	
	if (xmshuxin > 32767){//32767  当属性超越临界时
		cm.sendOk("紧急！紧急！紧急！紧急！已经强制结束本脚本！\r\n"
		+"由于GM不小心设置错误，数字超过安全范围内，冒险岛不支持\r\n"
		+"计算数值：#b"+xmshuxin+"#k\r\n"
		+"所有属性计算下来已经超越 #b32767#k\r\n"
		+"请修改参数 #b(层数)#k 和 #b(属性点)#k 把属性总值降下来\r\n"
		+"这样将会导致游戏错误，请通知GM进行整改此NPC\r\n"
		+"如不知道解决办法，请联系咨询GM\r\n"
		
		
		);
		cm.dispose();
        return;
	}
	// cm.getPlayer().dropMessage(5, "获取属性："+xmshuxin);
	}
	
	



    status = -1;
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
		局仙级 = getxmwnjlc("XM飞升系统_仙级");
		总转生 = getxmwnjlc("XM飞升系统_总转生");
		当前层 = getxmwnjlc("XM飞升系统_当前层");
		渡劫状态 = getxmwnjlc("XM飞升系统_渡劫");
		
		// cm.getPlayer().dropMessage(5, "获取等级 "+xmxsz.length);
		if (局仙级 >= (xmxsz.length-1)) {
			if (当前层 >= xmxsz[局仙级].层数) {
				cm.sendOk("恭喜你已经大圆满无需继续操作！\r\n");
				cm.dispose();
				return;
			}
			
		}
		
		
		
		
		
    
	    
		
        

		
		cm.sendSimple(zhuyeUI());
        
    } else if (status == 1) {
		
        if (selection == 0) {
			        												
			cm.sendOk("成功！");
			cm.dispose();
	    } else if (selection == 1) {
			if (cm.getPlayer().getLevel() < xiaomi.转生条件){
				cm.sendOk("对不起，等级没有达到："+xiaomi.转生条件+"级无法渡劫！！\r\n");
				cm.dispose();
				return;
			}
			pdcl = getxmszpdsfycl(xmxsz[局仙级+1].渡劫材料);
			if (pdcl != ""){
				cm.sendOk("对不起，缺少渡劫试练的材料！\r\n"+pdcl);
				cm.dispose();
				return;
			}
			gainItemxiaomikouchu(xmxsz[局仙级+1].渡劫材料);
			
			if ((Math.floor(Math.random() * 100) <= xmxsz[局仙级+1].渡劫率) == false ) {
				cm.sendOk("很遗憾！本次渡劫突遇黑魔法师扰乱~~~渡劫失败了\r\n");
				cm.dispose();
				return;
			}
			
			
			gainxmwnjlc("XM飞升系统_当前层",-当前层);
			gainxmwnjlc("XM飞升系统_仙级",1);
			// cm.getPlayer().dropMessage(5, "获取等级 "+xmxsz[局仙级].仙级);
			cm.sendOk("#e#r太棒了！成功渡劫至：【"+xmxsz[局仙级+1].仙级+"】\r\n");
		    cm.喇叭(2," 玩家 <"+cm.getName()+"> 成功渡劫至：【"+xmxsz[局仙级+1].仙级+"】，可喜可贺！！！");
			cm.喇叭(2," 玩家 <"+cm.getName()+"> 成功渡劫至：【"+xmxsz[局仙级+1].仙级+"】，可喜可贺！！！");
			cm.喇叭(2," 玩家 <"+cm.getName()+"> 成功渡劫至：【"+xmxsz[局仙级+1].仙级+"】，可喜可贺！！！");
			cm.喇叭(2," 玩家 <"+cm.getName()+"> 成功渡劫至：【"+xmxsz[局仙级+1].仙级+"】，可喜可贺！！！");
			cm.喇叭(2," 玩家 <"+cm.getName()+"> 成功渡劫至：【"+xmxsz[局仙级+1].仙级+"】，可喜可贺！！！");
			cm.喇叭(2," 玩家 <"+cm.getName()+"> 成功渡劫至：【"+xmxsz[局仙级+1].仙级+"】，可喜可贺！！！");
			cm.dispose();
		} else if (selection == 2) {//飞升
			
			if (cm.getPlayer().getLevel() < xiaomi.转生条件){
				cm.sendOk("对不起，等级没有达到："+xiaomi.转生条件+"级无法飞升！\r\n");
				cm.dispose();
				return;
			}
			
			
			
			pdcl = getxmszpdsfycl(xmxsz[局仙级].飞升材料);
			if (pdcl != ""){
				cm.sendOk("对不起，缺少飞升试练的材料！\r\n"+pdcl);
				cm.dispose();
				return;
			}
			gainItemxiaomikouchu(xmxsz[局仙级].飞升材料);
			if ((Math.floor(Math.random() * 100) <= xmxsz[局仙级].飞升率) == false ) {
				cm.sendOk("飞升过程中遭遇不明势力袭击，导致中断！请再接再厉！\r\n");
				cm.dispose();
				return;
			}
			
			gainxmwnjlc("XM飞升系统_当前层",+1);
			gainxmwnjlc("XM飞升系统_总转生",+1);
			// 当前层 += 1;
			局仙级 = getxmwnjlc("XM飞升系统_仙级");
			var xmshuxin = 0;
			for (var i = 1; i < xmxsz.length; i++) {
				// if (xmxsz[局仙级].仙级 == xmxsz[i].仙级) {
					// xmshuxin += (xmxsz[i].属性点*(当前层+1));
					// break;
				// }
				if (i >= 局仙级) {
					xmshuxin += (xmxsz[i].属性点*(当前层+1));
					break;

				}
				
				// if (局仙级 == 1) {
					// break;
				// }
				xmshuxin += (xmxsz[i].属性点*xmxsz[i].层数);
				
			}
			xmshuxin += ((xiaomi.转生等级-1)*5);
			gainItemxiaomizuhe(xmxsz[局仙级].飞升奖励);
			
			
			// cm.getPlayer().dropMessage(5, "自动计算属性 "+xmshuxin);
			//删除所有未使用的能力
			
			
			if (1==2){
			//黑屏刷新
			cm.getPlayer().setLevel(xiaomi.转生等级);
			cm.getChar().resetStats(4,4,4,4);
			cm.getPlayer().resetAPSP();
			cm.gainAp(xmshuxin);
			cm.getChar().fakeRelog();
			cm.gainExp(500000);
			} else {
			
			//无需刷新直接转生
			
			cm.getPlayer().setLevel(xiaomi.转生等级+xiaomi.转生等级模式);
			cm.getPlayer().levelUp();
			cm.getChar().resetStats(4,4,4,4);
			cm.getPlayer().resetAPSP();
			cm.gainAp(xmshuxin);
			cm.gainExp(1);
			}
			// 检测特定记录并增加AP点
			checkAndGainAp();
			xmfeishenglaba(xmxsz[局仙级].仙级,(当前层+1),(总转生+1));
			cm.sendOk("#e#r恭喜你成功飞升至："+(当前层+1)+" 层 总转生为："+(总转生+1)+"次\r\n");
			cm.dispose();
		}			 
		
    } else if (status == 2) {
		

	}
}

/*

 *@param{参数类型}itemid 参数说明
 *@return {返回值类型} 返回值说明
*/
function 循环() {
	//循环的使用
	for (var i = 0; i < xmxsz.length; i++) {
		break;//跳出循环
	}
	
	for (x in person) {
	
	}
	
	xmxsz.push(1);

}



//查看物品信息奖励数组型
function getxmckdjsz(itemshuzu) {
	var 金币 = "#fItem/Special/0900.img/09000001/iconRaw/1#";
	var 点卷图标 = "#fUI/CashShop/CashItem/0#";
    var xmxszss = itemshuzu;
    var msg = "";
	// msg += "#r" + 奖励 + "\r\n"
    for (var i = 0; i < xmxszss.length; i++) {
        if (xmxszss[i][0] == 0) { //金币
            msg += "" + 金币 + "#r 金币 X " + xmxszss[i][1] + "\r\n";
        } else if (xmxszss[i][0] == 1) { //点卷
            msg += "" + 点卷图标 + "#r 点卷 X " + xmxszss[i][1] + "\r\n";
        } else if (xmxszss[i][0] == 2) { //抵用券
            msg += "" + 点卷图标 + "#r 抵用卷 X " + xmxszss[i][1] + "\r\n";
        } else {
			msg += "#d#i" + xmxszss[i][0] + ":##z" + xmxszss[i][0] + "# × " + xmxszss[i][1] + "\r\n";
            
        }

    }
	return msg;

}


//判断是否有需要的材料
function getxmszpdsfycl(itemshuzu) {
	var 金币 = "#fItem/Special/0900.img/09000001/iconRaw/1#";
	var 点卷图标 = "#fUI/CashShop/CashItem/0#";
    var xmxszss = itemshuzu;
    var msg = "";
	// msg += "#r" + 奖励 + "\r\n"
    for (var i = 0; i < xmxszss.length; i++) {
        if (xmxszss[i][0] == 0) { //金币
			if(cm.getMeso () < xmxszss[i][1]) {
            msg += "" + 金币 + "#r 金币 X " + xmxszss[i][1] + "\r\n";
			}
        } else if (xmxszss[i][0] == 1) { //点卷
			if(cm.getPlayer().getCSPoints(1) < xmxszss[i][1]) {
            msg += "" + 点卷图标 + "#r 点卷 X " + xmxszss[i][1] + "\r\n";
			}
        } else if (xmxszss[i][0] == 2) { //抵用券
			if(cm.getPlayer().getCSPoints(2) < xmxszss[i][1]) {
            msg += "" + 点卷图标 + "#r 抵用卷 X " + xmxszss[i][1] + "\r\n";
			}
        } else {
			if (!cm.haveItem(xmxszss[i][0],xmxszss[i][1])) {
			msg += "#d#i" + xmxszss[i][0] + ":##z" + xmxszss[i][0] + "# × " + xmxszss[i][1] + "\r\n";
            }
        }

    }
	return msg;

}


//扣除自己的物品循环-台端通用-数组
function gainItemxiaomikouchu(itemshuzu) {
	var xmxszss = itemshuzu;
    for (var i = 0; i < xmxszss.length; i++) {
        if (xmxszss[i][0] == 0) { //金币
            cm.gainMeso( - xmxszss[i][1]); //扣除多少金币····
        } else if (xmxszss[i][0] == 1) { //点卷
			cm.getPlayer().dropMessage(5, "-点卷 x "+xmxszss[i][1]);
            cm.getPlayer().modifyCSPoints(1,  -xmxszss[i][1],true); //点券
        } else if (xmxszss[i][0] == 2) { //抵用券
			cm.getPlayer().dropMessage(5, "-抵用券 x "+xmxszss[i][1]);
            cm.getPlayer().modifyCSPoints(2, -xmxszss[i][1],true); //抵用券
        } else {
            cm.gainItem(xmxszss[i][0], -xmxszss[i][1]);
            
        }
    }	
}


//给自己的物品循环-台端通用-数组
function gainItemxiaomizuhe(itemshuzu) {
	var xmxszss = itemshuzu;
    for (var i = 0; i < xmxszss.length; i++) {
        if (xmxszss[i][0] == 0) { //金币
            cm.gainMeso( + xmxszss[i][1]); //扣除多少金币····
        } else if (xmxszss[i][0] == 1) { //点卷
			cm.getPlayer().dropMessage(5, "获得：点卷 x "+xmxszss[i][1]);
            cm.getPlayer().modifyCSPoints(1, +xmxszss[i][1]); //点券
        } else if (xmxszss[i][0] == 2) { //抵用券
			cm.getPlayer().dropMessage(5, "获得：抵用券 x "+xmxszss[i][1]);
            cm.getPlayer().modifyCSPoints(2, +xmxszss[i][1]); //抵用券
        } else {
            
            cm.gainItem(xmxszss[i][0], xmxszss[i][1]);
            
        }
    }	
	
}




function getConnection(){
	return cm.getConnection();
}



function getxmwnjljsc(jiluid) {
	var xmsjfh = 0;
	zhjsid = cm.getPlayer().getId();
	var conn = getConnection();
	var sql = "SELECT * FROM xmwnjl WHERE characterid = "+zhjsid+" AND bossid = '"+jiluid+"' ;";
	var pstmt = conn.prepareStatement(sql);
	var result = pstmt.executeQuery();		
	if (result.next()) {
	xmsjfh = result.getInt("count");
	} 
	result.close();
	pstmt.close();
	
	conn.close();
	
	return xmsjfh;
}





function gainxmwnjljsc(wnjllog,jilu) {
	var accid = cm.getPlayer().getId();
	var conn = getConnection();
	var sql = "SELECT * FROM xmwnjl WHERE bossid = '"+wnjllog+"' AND characterid = "+accid+" ;";
	var pstmt = conn.prepareStatement(sql);
	var result = pstmt.executeQuery();	
	
	if (result.next()) {
		result.close();
		pstmt.close();
	    //var conn = getConnection();
	    var sql = "UPDATE xmwnjl SET count = count+"+jilu+"  WHERE bossid = '"+wnjllog+"' AND characterid = "+accid+" ;";
	    var pstmt = conn.prepareStatement(sql);
	    pstmt.executeUpdate();
		pstmt.close();		
	} else {
	//var conn = getConnection();
		result.close();
		pstmt.close();
		var sql = "insert into xmwnjl (time,bossid,count,characterid) values (CURRENT_TIMESTAMP(),?,?,?);";          
		var psu = conn.prepareStatement(sql);
		psu.setString(1,wnjllog);
		psu.setInt(2,jilu);
		psu.setInt(3,accid);
		psu.executeUpdate();	
		psu.close();
	}	
	conn.close();
}


// 检测特定记录并增加AP点
function checkAndGainAp() {
    // 检测是否有特定记录
	if (cm.getPlayer().getOneTimeLog("怪怪卡片-金银岛区域总奖励") >= 1){//判断永久记录
        // 如果有记录，额外增加80点AP
        cm.getPlayer().gainAp(80);
    }
	if (cm.getPlayer().getOneTimeLog("怪怪卡片-神秘岛区域总奖励") >= 1){//判断永久记录
        // 如果有记录，额外增加45点AP
        cm.getPlayer().gainAp(45);
    }
	if (cm.getPlayer().getOneTimeLog("怪怪卡片-射手村") >= 1){//判断永久记录
        // 如果有记录，额外增加20点AP
        cm.getPlayer().gainAp(20);
    }
	if (cm.getPlayer().getOneTimeLog("怪怪卡片-魔法密林") >= 1){//判断永久记录
        // 如果有记录，额外增加20点AP
        cm.getPlayer().gainAp(20);
    }
	if (cm.getPlayer().getOneTimeLog("怪怪卡片-废弃都市") >= 1){//判断永久记录
        // 如果有记录，额外增加20点AP
        cm.getPlayer().gainAp(20);
    }
	if (cm.getPlayer().getOneTimeLog("怪怪卡片-上海外滩") >= 1){//判断永久记录
        // 如果有记录，额外增加20点AP
        cm.getPlayer().gainAp(20);
    }
	if (cm.getPlayer().getOneTimeLog("怪怪卡片-勇士部落") >= 1){//判断永久记录
        // 如果有记录，额外增加20点AP
        cm.getPlayer().gainAp(20);
    }
	if (cm.getPlayer().getOneTimeLog("怪怪卡片-林中之城") >= 1){//判断永久记录
        // 如果有记录，额外增加20点AP
        cm.getPlayer().gainAp(20);
    }
	if (cm.getPlayer().getOneTimeLog("怪怪卡片-天空之城") >= 1){//判断永久记录
        // 如果有记录，额外增加20点AP
        cm.getPlayer().gainAp(35);
    }
	if (cm.getPlayer().getOneTimeLog("怪怪卡片-冰峰雪域") >= 1){//判断永久记录
        // 如果有记录，额外增加20点AP
        cm.getPlayer().gainAp(40);
    }
	if (cm.getPlayer().getOneTimeLog("怪怪卡片-武陵") >= 1){//判断永久记录
        // 如果有记录，额外增加20点AP
        cm.getPlayer().gainAp(30);
    }
	if (cm.getPlayer().getOneTimeLog("怪怪卡片-百草堂") >= 1){//判断永久记录
        // 如果有记录，额外增加20点AP
        cm.getPlayer().gainAp(15);
    }
	if (cm.getPlayer().getOneTimeLog("怪怪卡片-玩具城") >= 1){//判断永久记录
        // 如果有记录，额外增加20点AP
        cm.getPlayer().gainAp(50);
    }
	if (cm.getPlayer().getOneTimeLog("怪怪卡片-海底世界") >= 1){//判断永久记录
        // 如果有记录，额外增加20点AP
        cm.getPlayer().gainAp(35);
    }
	if (cm.getPlayer().getOneTimeLog("怪怪卡片-童话村") >= 1){//判断永久记录
        // 如果有记录，额外增加20点AP
        cm.getPlayer().gainAp(15);
    }
	if (cm.getPlayer().getOneTimeLog("怪怪卡片-地球本部") >= 1){//判断永久记录
        // 如果有记录，额外增加20点AP
        cm.getPlayer().gainAp(15);
    }
	if (cm.getPlayer().getOneTimeLog("怪怪卡片-神木村") >= 1){//判断永久记录
        // 如果有记录，额外增加20点AP
        cm.getPlayer().gainAp(40);
    }
	
	
}








