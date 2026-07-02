/*


var JT = "#fUI/Basic/BtHide3/mouseOver/0#";
var 心 = "#fUI/GuildMark.img/Mark/Etc/00009001/14#";
var 小烟花 ="#fMap/MapHelper/weather/squib/squib4/1#";
var 感叹号 = "#fEffect/UIWindow/Quest/icon0#";
var juanzs =Array(
Array(100,200000),
Array(105,200000),
Array(110,200000),
Array(115,200000),
Array(120,200000),
Array(125,200000),
Array(130,200000),
Array(135,200000),
Array(140,200000),
Array(145,200000),
Array(150,200000),
Array(155,250000),
Array(160,300000),
Array(165,350000),
Array(170,400000),
Array(175,450000),
Array(180,500000),
Array(185,550000),
Array(190,5000000),
Array(195,5000000),
Array(200,5000000),
Array(205,5000000),
Array(210,20000000),
Array(215,20000000),
Array(220,20000000),
Array(225,20000000),
Array(230,20000000),
Array(235,20000000),
Array(240,20000000),
Array(245,20000000),
Array(250,20000000),
Array(255,99999999999999999999)

);
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
    } else {
        status--;
    }
    if (status == 0) {
			for(var i=0;i<juanzs.length;i++){
				var shuliang = 0;
				if((cm.getMaxLevel()+5) == juanzs[i][0]){
					shuliang = juanzs[i][1];
	var 解除封印进度 = cm.GetPiot("解除封印进度", "1");
	var 解除封印进度条 = 解除封印进度/shuliang*100;
        var selStr = "	Hi~#b#h ##k嘤嘤嘤嘤嘤嘤，你有 "+cm.显示物品(4001128)+" 吗？在财神爷冒险岛公测中...在勇士们征战的途中遇到黑暗龙王的攻击~封印了等级勇士的力量！但是其他勇士们不畏惧黑暗龙王的力量，在冒险过程中收集强大的火药冲破结界，解除对等级的封印！[#e下次解封等级"+(cm.getMaxLevel()+(cm.getMaxLevel()>=250?0:5))+"级#n]\r\n";
		 selStr += "     解除封印进度: #B"+解除封印进度条+"#["+解除封印进度+"/"+shuliang+"] \r\n\r\n";
		 selStr += "  "+感叹号+"提示：捐献火药会获取相应抵用券！\r\n";
         selStr += "#L3##b提交火药解除封印#k#l";//#L4##b提交火药排行榜#k#l
				}
			}
         cm.sendSimple(selStr,2);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.gainItem(1472063, 1, 1);
                cm.dispose();
                break;
            case 2:
                cm.gainItem(2060006, 800);
                cm.dispose();
                break;
			case 3:
				cm.dispose();
                cm.打开NPC(2007,9220004);
                break;	
			case 4:
			var text = "   ────────< #e#r捐献火药榜#k#n >──────── #n\r\n\r\n";
				text += "    排名        \t玩家         \t\t\t捐献数量\r\n";
                var rankinfo_list = cm.getBossRankCountTop("捐献数量");
                if (rankinfo_list != null) {
                    for (var i = 0; i < rankinfo_list.size(); i++) {
                        if (i == 20) {
                            break;
                        }
                        var info = rankinfo_list.get(i);

                        text += i == 0 ? "#r" : i == 1 ? "#b" : i == 2 ? "#b" : "";
                        text += "\t" + (i + 1) + "\t\t\t\t";
                        text += info.getCname();
                        for (var j = 16 - info.getCname().getBytes().length; j > 0; j--) {
                            text += " ";
                        }
                        text += "\t\t#k#n#r" + info.getCount();
                        text += "#k#n \t\t#k";
                        text += "";
                    }
                }
				text += "\r\n\r\n";
                cm.sendOkS(text, 2);
                cm.dispose();
				 break;	
				
        }
    }
} */
var sss
var 初始化世界世界等级 =120;
var 初始化捐献数量 =100000;//刚开始需要突破第一次等级的数量
var 突破一次加多少级 =5;
var 突破一次加捐献数量 =100000;


var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) { 
        var 序号=0;
		//sss = cm.getServernum(0);
		var 最大值=cm.getServernum(0,0);
		var 自己捐献数量 =cm.getServernum(0,1);
		var 世界等级= cm.getServerLevel();
	if (mode == -1) {
        cm.dispose();
    } else {
		if (mode == 1) {
			status++;
		} else {
			cm.dispose();
			return;
		}
    if (status == 0) {
		//cm.setServernum(0,0,1500000);	 //设置物品最大值
		//cm.addServernum(0,0,1500000);//增加需求值
		//cm.addServernum(0,1,1000000);//增加完成值

			if(世界等级==250){
		    cm.sendOk("魔王的封印已经解开！世界等级已到达极限200，你们拥有极限的力量可以打败魔王去了！");
			cm.dispose();
			return;	
		}else{
		cm.sendNext("等级上限被封印！\r\n收集#v4001128#炸开封印，突破等级上限！\r\n当前等级上限：#r"+世界等级);
		}
		
    } else if (status == 1) {
		var textx = "#v4001128#收集现状 \n\r #B" + Math.floor(自己捐献数量/最大值*100) + "# "+Math.floor(自己捐献数量/最大值*100)+"%【"+自己捐献数量+"/"+最大值+"】\n\r 如果我们把它们集中起来，世界等级封印就解除了一分……\r\n";
		if(cm.getPlayer().isGM()){
			cm.sendSimple(textx+"#r(只有GM才会显示)#k\n\r #b#L0#为世界捐献#v4001128##l#k  \n\r #L2#清理进度(只有GM才会显示)#l\n\r #L3#初始化世界等级进度(只有GM才会显示)#l#k");
			
		cm.sendSimple("解锁该地图需要用到拼图的力量-#b枫叶#k \n\r #b#L0# 我把枫叶带来了。#l#k \n\r #b#L1# 请告诉我现在的收集进度。#l#k \n\r #b#L2# 清理记忆拼图(只有GM才会显示)。 #l#k \n\r #b#L3# 初始化世界等级(只有GM才会显示)。 #l#k\n\r\n\r #b< 在库存的记忆拼图数 : "+自己捐献数量+" 最大值 "+最大值+"个 >#k");
		}else{
			cm.sendSimple(textx+"\n\r #b#L0#为突破等级捐献#v4001128##l#k");
		}
    } else if (status == 2) {
        if (selection == 1) {
            cm.sendNext("枫叶收集现状 \n\r #B" + Math.floor(自己捐献数量/最大值) + "# "+Math.floor(自己捐献数量/最大值)+"%\n\r 如果我们把它们集中起来，该封印就解除了……");
        cm.dispose();
        } else if (selection == 2) {
		
		cm.setServernum(0,1,0)
        cm.sendNext("清理成功.");
        cm.dispose();
		} else if (selection == 3) {
			cm.setServernum(0,0,初始化捐献数量);	 //设置物品最大值
			cm.setServernum(0,1,0)
			cm.setServerLevel(初始化世界世界等级)
			cm.sendNext("世界等级为"+初始化世界世界等级);
			cm.dispose();
        } else if (selection == 0) {
             cm.sendGetNumber("你把#z4001128#带来了吗？很好写上你捐献的个数 最大32767 #k", cm.itemQuantity(4001128), 1,32767);
        }
    } else if (status == 3) {
        var num = selection;
        if (num < 10) {
            cm.sendOk("#v4001128##z4001128#最低10个起捐！");
			cm.dispose();
			return;
		}else if(最大值 < 自己捐献数量 + num){
            cm.sendOk("物品数量超过了"+ (自己捐献数量 + num - 最大值) +"个！");
			cm.dispose();
			return;
        } else if (cm.haveItem(4001128, num)) {
            cm.gainItem(4001128, -num);
			cm.gainCS(2,num);
			//cm.gainNX(num);
			cm.addServernum(0,1,num);
			cm.worldMessage(0x06,"玩家 "+cm.getPlayer().getName()+" 为突破等级捐献【火药桶】"+num+"个,封印力量削减一分，并获得" + num + "抵用卷 ");
			if(Math.floor((自己捐献数量+num)/最大值*100)>=100){
				cm.worldMapEffect("封印削弱！等级上限提升至"+(世界等级+突破一次加多少级),5120015);	
				cm.worldMessage(0x06,"玩家 "+cm.getPlayer().getName()+" 捐献是发现有异动！世界等级提升至"+(世界等级+突破一次加多少级));
				cm.setServerLevel(突破一次加多少级+世界等级);//增加世界等级
				cm.setServernum(0,1,0)
				cm.addServernum(0,0,突破一次加捐献数量)
			}
        }else{
            cm.sendOk("小子，你包里真的有火药吗？\r\n 别拿这种事开玩笑！");
		}
        cm.dispose();
		}
	}
}			
