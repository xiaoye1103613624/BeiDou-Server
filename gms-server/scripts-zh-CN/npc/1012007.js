
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
            txt = "我是每日跑商第7环NPC哦！\r\n\r\n";

            if (cm.getBossLog('每日跑商') == 6) {
            //if (cm.getPS() == 6){// cm.getPS()  的意思是 读取跑商值如果等于1 就得出他跑商已经完成了第一环 就运行他进行第二环跑商!

                txt += "#L1##b请收集#v2001000##z2001000#50个交给我！#l\r\n\r\n";
                txt += "   需要任务物品：#v2001000# [#r#c2001000##k/50]\r\n";
               // txt += "\r\n   奖励：点券*3000 ";
               // txt += "\r\n#L2#送你到对应地图？需要200点券#l";
                cm.sendSimple(txt);
            }else{
				if (cm.getBossLog('每日跑商') < 6) {
                txt += "请完成前面的任务再来找我！\r\n";
                cm.sendOk(txt);
                cm.dispose();
				}else{
                //txt += "你已经完成过了然后你去找.黄金海岸-红螃蟹海滩Ⅱ-飞天猪!\r\n";
                txt += "你已经完成了!\r\n";
                txt += "下一环里恩-仓库管理员 普斯拉。\r\n";
                cm.sendOk(txt);
                cm.dispose();
				}
            }

        } else if (status == 2) {
			if (cm.getPlayer().getCSPoints(1) > 200){
				cm.gainNX(-200);	//加减点券
                cm.warp(100000202, 0);
                cm.dispose();
			}else{
                cm.sendOk("点券不足.");
                cm.dispose();
			}
		} else if (selection == 1) {
            if (cm.haveItem(2001000,50)){
                cm.setBossLog('每日跑商');
                cm.gainItem(2001000,-50);
			
				//cm.gainNX(3000); //点卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(11,cm.getC().getChannel(),"[每日跑商]" + " : " + " 玩家[" + cm.getPlayer().getName() + "]完成了每日跑商第七环,获得大量奖励！",true).getBytes()); //喇叭  
                cm.sendOk("跑商第7环完成!然后你去找.里恩 - 仓库管理员 普斯拉。进行下一环！");
                cm.dispose();
            }else{
                cm.sendOk("请收集#v2001000##z2001000#50个交给我！");
                cm.dispose();
            }
        }else if (selection == 2) {
                cm.sendYesNo("送你到对应地图？需要200点券");
        }
    }
}
