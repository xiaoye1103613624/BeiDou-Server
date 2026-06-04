var 服务中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/3#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var status = -1;
var beauty = 0;
var tosend = 0;
var sl;
var mats;
function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) { 
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            if (status == 0) {
                cm.sendOk("如果需要中介服务在来找我吧。");
                cm.dispose();
            }
            status--;
        }
        if (status == 0) {
            var gsjb = "";
            gsjb =""+dd+"\r\n\t\t\t"+服务中心+"\r\n"+群粉心+""
            gsjb +="\r\n#b洗血洗蓝说明：\r\n#k1.消耗#v4001126##r*1000#k个+#v4000313##r*10#k个，每次增加#r100点#k血或蓝上限\r\n\r\n"
            gsjb +="#k2.消耗 #r10元宝#k+#r1亿金币#k，每次增加#r1000点#k血或蓝上限\r\n\r\n"
            gsjb +="#k3.血量上限和蓝量上限为 #r30000点\r\n\r\n"
            gsjb +="\t#k当前HP上限:#r" + cm.getPlayerStat("MAXHP") + "#k点\t当前MP上限:#r" + cm.getPlayerStat("MAXMP") + "#k点\r\n\r\n";    
			gsjb +="\t #r#e#L1#元宝加1000点血#l\t";
			gsjb +="#r#b#L2#元宝加1000点蓝#l\r\n\r\n";         	
			gsjb +="\t#r#e#L3#材料加100点血#l\t";
			gsjb +=" #r#b#L4#材料加100点蓝#l\r\n\r\n";
            cm.sendSimple(gsjb);
        } else if (status == 1) {
            if (selection == 1) {
                if (cm.getPlayerStat("MAXHP") >= 30000) {
                    cm.sendOk("你要增加的数值已经达到3万无法增加！");
                    cm.dispose();
					return;
                } else if(cm.getmoneyb() < 10){
					cm.sendOk("你没有#r10#k元宝！");
                    cm.dispose();
					return;
				} else if(cm.getPlayer().getMeso() < 100000000 ){ 
	                cm.sendOk("您的金币不足");
                    cm.dispose();
                    return;
                } else {
					cm.gainMeso(-100000000);
					cm.setmoneyb(-10);
                    cm.增加角色最大生命值(1000);
                    cm.sendOk("您成功增加HP：#r1000#k！");
					cm.getPlayer().指定喇叭("高质地喇叭", "洗血公告", "恭喜玩家[" + cm.getPlayer().getName() + "]通过洗血提升1000点HP上限!");
                    cm.dispose(); 
                }
				} else if(selection == 2) {
                if (cm.getPlayerStat("MAXMP") >= 30000) {
                    cm.sendOk("你要增加的数值已经达到3万无法增加！");
                    cm.dispose();
					return;
                } else if(cm.getmoneyb() < 10){
					cm.sendOk("你没有#r10#k元宝！");
                    cm.dispose();
					return;
				} else if(cm.getPlayer().getMeso() < 100000000 ){ 
	                cm.sendOk("您的金币不足");
                    cm.dispose();
                    return;
                } else {
					cm.gainMeso(-100000000);
					cm.setmoneyb(-10);
                    cm.增加角色最大法力值(1000);
                    cm.sendOk("您成功增加MP：#r1000#k！");
					cm.getPlayer().指定喇叭("高质地喇叭", "洗蓝公告", "恭喜玩家[" + cm.getPlayer().getName() + "]通过洗蓝提升1000点MP上限!");
                    cm.dispose(); 
                }
				} else if(selection == 3) {
                if (cm.getPlayerStat("MAXHP") >= 30000) {
                    cm.sendOk("你要增加的数值已经达到3万无法增加！");
                    cm.dispose();
					return;
                }else if(!cm.haveItem(4001126,1000)){
					cm.sendOk("你没有#r1000#k#v4001126#！");
                    cm.dispose();
					return;
                }else if(!cm.haveItem(4000313,10)){
					cm.sendOk("你没有#r10#k#v4000313#！");
                    cm.dispose();
					return;
                } else {
					cm.gainItem(4001126,-1000);
					cm.gainItem(4000313,-10);
                    cm.增加角色最大生命值(100);
                    cm.sendOk("您成功增加HP：#r100#k！");
					cm.getPlayer().指定喇叭("高质地喇叭", "洗血公告", "恭喜玩家[" + cm.getPlayer().getName() + "]通过洗血提升100点HP上限!");
                    cm.dispose(); 
                }
				} else if(selection == 4) {
                if (cm.getPlayerStat("MAXMP") >= 30000) {
                    cm.sendOk("你要增加的数值已经达到3万无法增加！");
                    cm.dispose();
					return;
                }else if(!cm.haveItem(4001126,1000)){
					cm.sendOk("你没有#r1000#k#v4001126#！");
                    cm.dispose();
					return;
                }else if(!cm.haveItem(4000313,10)){
					cm.sendOk("你没有#r10#k#v4000313#！");
                    cm.dispose();
					return;
                } else {
					cm.gainItem(4001126,-1000);
					cm.gainItem(4000313,-10);
                    cm.增加角色最大法力值(100);
                    cm.sendOk("您成功增加MP：#r100#k！");
					cm.getPlayer().指定喇叭("高质地喇叭", "洗蓝公告", "恭喜玩家[" + cm.getPlayer().getName() + "]通过洗蓝提升100点MP上限!");
                    cm.dispose(); 
                }
			}

        }
    }
}
