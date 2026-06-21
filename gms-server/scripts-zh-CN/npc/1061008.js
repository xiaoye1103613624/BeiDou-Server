
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
            txt = "我是每日跑商任务NPC！第六环: #r建议地图-鬼怪山脊#k \r\n\r\n";

            if (cm.getBossLog('每日跑商') == 5) {
           // if (cm.getPS() == 5){// cm.getPS()  的意思是 读取跑商值如果等于1 就得出他跑商已经完成了第一环 就运行他进行第二环跑商!

                txt += "#L1##b请收集#v4000173##z4000173#50个交给我！#l\r\n\r\n";
                txt += "   需要任务物品：#v4000173# [#r#c4000173##k/50]\r\n";
                //txt += "\r\n   奖励：点券*2000 ";
              //  txt += "\r\n#L2#送你到对应地图？需要10W金币#l";
                cm.sendSimple(txt);
            }else{
				if (cm.getBossLog('每日跑商') < 5) {
                txt += "请完成前面的任务再来找我！\r\n";
                cm.sendOk(txt);
                cm.dispose();
				}else{
                //txt += "你已经完成过了然后你去找.黄金海岸-红螃蟹海滩Ⅱ-飞天猪!\r\n";
                txt += "你已经完成了!\r\n";
                txt += "下一环射手公园 -仓库管理员 李先生。\r\n";
                cm.sendOk(txt);
                cm.dispose();
				}
            }

        }else if (status == 2) {
			if (cm.getPlayer().getMeso() > 100000){
				cm.gainMeso(-100000);	//加减点券
                cm.warp(222010400, 0);
                cm.dispose();
			}else{
                cm.sendOk("金币不足.");
                cm.dispose();
			}
		}  else if (selection == 1) {
            if (cm.haveItem(4000173,50)){
                cm.setBossLog('每日跑商');
                cm.gainItem(4000173, -50);
                cm.gainMeso(+500000); //加减金币
	            cm.喇叭(3,"玩家：["+cm.getName()+"]完成跑商第6环！奖励：金币：50W");
                cm.sendOk("跑商第6环完成!然后你去找.下一环射手公园-仓库管理员李先生。进行下一环！");
                cm.dispose();
            }else{
                cm.sendOk("请收集#v4000173##z4000173#50个交给我！");
                cm.dispose();
            }
        }else if (selection == 2) {
                cm.sendYesNo("送你到对应地图？需要10W金币");
        }
    }
}
