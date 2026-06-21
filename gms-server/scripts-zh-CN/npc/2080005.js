
var status = 0;
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        if (status == 0) {
            var txt = "";
            txt ="我是每日跑商任务NPC！第十环: #r建议地图-蟠桃果林3#k \r\n\r\n";

            if (cm.getBossLog('每日跑商') == 9) {
           // if (cm.getPS() == 9){// cm.getPS()  的意思是 读取跑商值如果等于1 就得出他跑商已经完成了第一环 就运行他进行第二环跑商!

                txt += "#L1##b收集#v2022116##z2022116#20个交给我！#l\r\n\r\n";
                txt += "   需要任务物品：#v2022116# [#r#c2022116##k/20]\r\n";
                txt += "\r\n   奖励：#r#v4001126#*100#v4031138#*200W#v4310108#*1#v4310088#*1#v4000038#*10#v2049100#*1#v2340000#*1#v4170007#*1";
				txt += "\r\n#L2#送你到对应地图？需要2W金币#l";
                cm.sendSimple(txt);

            }else{
				if (cm.getBossLog('每日跑商') < 9) {
                txt += "请完成前面的任务再来找我！\r\n";
                cm.sendOk(txt);
                cm.dispose();
				}else{

                txt += "你已经完成了!\r\n";
                txt += "你已经完成过了所有跑商任务。\r\n";
                cm.sendOk(txt);
                cm.dispose();
				}
            }
       } else if (status == 2) {
			if (cm.getPlayer().getMeso() > 100000){
				cm.gainMeso(-100000);	//加减点券
                cm.warp(250010700, 0);
                cm.dispose();
			}else{
                cm.sendOk("金币不足.");
                cm.dispose();
			}

        } else if (selection == 1) {
            if (cm.haveItem(2022116,20) ){
                cm.setBossLog('每日跑商');
                cm.gainItem(2022116, -20);
                cm.gainItem(4001126,100);//枫叶
				cm.gainMeso(2000000);
				cm.gainItem(4310108,1);//点券
				cm.gainItem(4310088,1);//RED
				cm.gainItem(4000038,10);//金杯
				cm.gainItem(2049100,1);//混沌
                cm.gainItem(2340000,1);//祝福
				cm.gainItem(4170007,1);//时装蛋
				//cm.openNpc(2080005,1);
				cm.喇叭(3,"玩家：["+cm.getName()+"]完成了每日跑商第十环，获得了大量奖励！");
                cm.sendOk("你已经完成了所有跑商任务！");
                cm.dispose();
            }else{
                cm.sendOk("收集#v2022116##z2022116#20个交给我！");
                cm.dispose();
            }
		}else if (selection == 2) {
                cm.sendYesNo("送你到对应地图？需要10W金币");
		
        }
    }
}
