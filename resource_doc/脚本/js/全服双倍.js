var need_startdobleexp, sj_wpsl, progress, s_sel, s_type;
var item_quan;
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.sendOk("感谢使用！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
			need_startdobleexp = cm.getqjbl(5, 1);
			sj_wpsl = cm.getqjbl(6, 1);
			progress = (sj_wpsl/need_startdobleexp)*100;
				var text = "#L1##b#i3010048##i3010048##i3010048#今晚全场赵公子买单#i3010048##i3010048##i3010048##l\r\n\r\n";
				cm.sendSimple(text);
		} else if(status == 1){
			s_type = selection;
			if(s_type == 0){
				
				cm.sendGetNumber("请输入你要捐赠的数量。\r\n\r\n进度：#B"+progress+"# #g"+sj_wpsl+"#k/#r"+need_startdobleexp+"#k - #r#e"+progress.toFixed(2)+"%#k#n\r\n目前你有 #b点卷#k × #r#e"+cm.getPotion(1)+"#n#k\r\n#i3992025#数量: #c3992025# #n#k\r\n",0,1,30000);
			} else {
				cm.sendSimple("\tHi~#b"+cm.getPlayer().getName()+"#l，这里是#b自助双倍经验系统#k，如果你有足够的点卷的话，可以给全服开启双倍经验哦！ \r\n#k#r#L0#给全服开双 3 小时 (需要30个#z2049104#)#l");
			}
		} else if(status == 2){
			if(s_type == 0){
				s_sel = selection;
				need_startdobleexp = cm.getqjbl(5, 1);
				sj_wpsl = cm.getqjbl(6, 1) + s_sel;
				progress = (sj_wpsl/need_startdobleexp)*100;
				cm.sendYesNo("#b你确定要将 #r#e"+s_sel+"#n #i3992025# #b捐赠吗？\r\n\r\n捐赠后进度:#B"+progress+"# #g"+sj_wpsl+"#k/#r"+need_startdobleexp+"#k - #r#e"+progress.toFixed(2)+"%#k#n");
			} else {
				//cm.startDoubleExp(180);
				cm.sendYesNo("#d再次确认是否手误,大佬确定要使用 #r30个 #z2049104##d 开启全服双倍经验活动三小时？");
			}
		} else if(status == 3){
			if(s_type == 0){
			} else {
				if(cm.haveItem(2049104,30)){
					
					//cm.getPlayer().setFame(cm.getPlayer().getFame() + 66);
					cm.gainItem(2049104,-30);
					//cm.gainItem(2049104,10);
					cm.startDoubleExp(180);
                    cm.全服漂浮喇叭("〖双倍经验活动〗 本次活动由["+cm.getName()+"]买单，全服开放双倍经验活动三小时 ~ 大家快感谢Ta", 5121002);
					//cm.喇叭(5390006, "双倍经验活动","本次活动由 "+cm.getPlayer().getName()+" 买单，全服开放双倍经验活动三小时！");
					cm.dispose();
				} else {
					cm.sendOk("#r 当前 #z2049104# 不足30个！");
					cm.dispose();
				}
			}
		} else {
			cm.dispose();
		}
	}
}