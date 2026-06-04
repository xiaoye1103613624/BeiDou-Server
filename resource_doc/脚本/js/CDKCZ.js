//importPackage(net.sf.odinms.client);
var status = 0;
var 心 = "#fUI/GuildMark.img/Mark/Etc/00009001/14#";
var 取服务器时间 = Packages.tools.FileoutputUtil.CurrentReadable_Time();
var fee;
var chance = Math.floor(Math.random() * 1);
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.sendOk("你没有卡号？");
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
			
            cm.sendGetText("#k当前时间：#r" + 取服务器时间 + "#k\r\n\r\n请输入合法的兑换卡号:");
        } else if (status == 1) {
            fee = cm.getText();
            if (cm.判断兑换卡是否存在("" + fee + "") <= 0) {
                cm.sendOk("卡号不存在，或已经被使用过。");
                cm.dispose();
            }
            if (cm.判断兑换卡类型("" + fee + "") == 1) {
				cm.gainNX(cm.判断兑换卡数额("" + fee + ""));
				cm.sendOk("恭喜你成功兑换了 #r" + cm.判断兑换卡数额("" + fee + "") + "#k 点券。");
                cm.全服黄色喇叭("[赞助充值] : 土豪玩家 "+cm.getPlayer().getName()+" 在拍卖自助充值处,兑换了丰厚的点卷！")
            } else if (cm.判断兑换卡类型("" + fee + "") == 2) {
				
				cm.getPlayer().setmoney(cm.getPlayer().getmoney()+cm.判断兑换卡数额("" + fee + ""));
			//	cm.getPlayer().setmoneym(cm.getPlayer().getmoneym()+cm.判断兑换卡数额("" + fee + ""));
				//cm.gainD(cm.判断兑换卡数额("" + fee + ""));
				cm.sendOk("恭喜你成功兑换了 #r" + cm.判断兑换卡数额("" + fee + "") + "#k 充值。");
               cm.dispose();
            } else if (cm.判断兑换卡类型("" + fee + "") == 5) {
				var Lb = cm.判断兑换卡礼包("" + fee + "");
				cm.打开NPC(9900005,Lb);
            }
			cm.Deleteexchangecard("" + fee + "");
        }
    }
}