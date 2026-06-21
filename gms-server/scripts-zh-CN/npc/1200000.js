
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
            txt =  "我是每日跑商任务NPC！第八环\r\n\r\n";

            if (cm.getBossLog('每日跑商') == 7) {
           // if (cm.getPS() == 7){// cm.getPS()  的意思是 读取跑商值如果等于1 就得出他跑商已经完成了第一环 就运行他进行第二环跑商!

                txt += "#L1##b请收集#v2020015##z2020015# 50个交给我！#l\r\n\r\n";
                txt += "   需要任务物品：#v2020015# [#r#c2020015##k/50] \r\n";
         //     txt += "\r\n   奖励：#v2340000#x8";
                //txt += "\r\n#L2#送你到对应地图？需要200点券#l";
                cm.sendSimple(txt);
            }else{
				if (cm.getBossLog('每日跑商') < 7) {
                txt += "请完成前面的任务再来找我！\r\n";
                cm.sendOk(txt);
                cm.dispose();
				}else{
                //txt += "你已经完成过了然后你去找.黄金海岸-红螃蟹海滩Ⅱ-飞天猪!\r\n";
                txt += "你已经完成了!\r\n";
                txt += "下一环玩具城 - 仓库管理员舍琵。\r\n";
                cm.sendOk(txt);
                cm.dispose();
				}
            }

        } else if (status == 2) {
			if (cm.haveItem(4000135,20)){
				selection=0
			}
			if (selection == -1) {
					if (cm.getPlayer().getCSPoints(1) > 200){
				cm.gainNX(-200);	//加减点券
                cm.warp(103000000, 0);
                cm.dispose();
				}else{
                cm.sendOk("点券不足.");
                cm.dispose();
				}
			}else{
				if (cm.getPlayer().getCSPoints(1) > 200){
				cm.gainNX(-200);	//加减点券
                cm.warp(220070301, 0);
                cm.dispose();
				}else{
                cm.sendOk("点券不足.");
                cm.dispose();
				}
			}
		} else if (selection == 1) {
            if (cm.haveItem(2020015,50)){
                cm.setBossLog('每日跑商');
                cm.gainItem(2020015, -50);
	         	cm.gainItem(2340000,8);
				cm.喇叭(3,"玩家：["+cm.getName()+"]完成跑商第8环！奖励：祝福卷轴 *8 ");
                cm.sendOk("跑商第8环完成!然后你去找.玩具城 -仓库管理员 舍琵。进行下一环！");
                cm.dispose();
            }else{
                cm.sendOk("请收集#v2020015##z2020015#50个交给我！");
                cm.dispose();
            }
        }else if (selection == 2) {	
                cm.sendYesNo("送你到对应地图？需要200点券");
        }else if (selection == 3) {	
                cm.sendYesNo("送你到对应地图？需要200点券");
        }
    }
}
