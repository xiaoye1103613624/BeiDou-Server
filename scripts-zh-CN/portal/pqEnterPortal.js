var 现金次数 = 100;
var 普通次数 = 2;

function enter(pi) {
			if(pi.getPlayer().getBossLog("xjcishu") == 现金次数){//判断现金次数
				pi.playerMessage("挑战每天进入次数："+现金次数+"次\t你已经进入"+现金次数+"次 !");//对话提示
				return;
			} else if(pi.getPlayer().getBossLog("ptcishu") == 普通次数){//判断非现金次数
				pi.playerMessage("挑战每天进入次数："+普通次数+"次\t你已经进入"+普通次数+"次 !");//对话提示
				return;
			} else if (pi.haveItem(5252006)) {//现金念力钥匙
				pi.getPlayer().setBossLog('xjcishu');//给现金次数
				pi.gainItem(5252006, -1);//扣除现金念力钥匙
				pi.warp(803000505, 0);//传送地图
				return;
			} else if (pi.haveItem(5252006)) {//念力钥匙
				pi.getPlayer().setBossLog('ptcishu');
				pi.gainItem(5252006, -1);//扣除念力钥匙
				pi.warp(803000505, 0);//传送地图
				return;
			} else {
				pi.playerMessage("你没有念力钥匙. ");
				return;
			}
		}
