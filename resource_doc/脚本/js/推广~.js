
var status = -1;
var beauty = 0;
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
                cm.sendOk("如果需要冒险岛推广服务在来找我吧。");
                cm.dispose();
            }
            status--;
        }
        if (status == 0) {
			
			
            var gsjb = "";
            //gsjb ="  #e欢迎来到冒险岛推广系统，当前共推荐了"+cm.showtgr()+"人\r\n\r\n";//统计多少个账号
            gsjb +="  #e#d您的推广ID号是："+cm.getPlayer().getAccountID()+"#k#d\r\n\r\n\r\n";
			gsjb +="  #e#d玩家输入推广ID后,被推荐的人凡以后充值对方均可获得10%的额外充值#k\r\n#d";
			gsjb +="#L2##b填写您的推荐人ID号#l\r\n\r\n";
			gsjb +="#L3##b【当前尚未领取-充值#k "+cm.getPlayer().getzb() +"#r充值尚未领取请领取】#l\r\n\r\n\r\n";
			gsjb +="#L4##b【未领推广奖励-当前#k "+cm.getPlayer().getcz() +"#r推广尚未领取请领取】#l\r\n\r\n\r\n";
            cm.sendSimple(gsjb);
        } else if (status == 1) {
        if (selection == 2) {
        beauty = 1;
        cm.sendGetNumber("请您输入 推广人id （不可以填写自己id）。#k", 1, 1, 90000000);
        } else if (selection == 3) {
        if(cm.getPlayer().getzb()==0){//判断表吧应该是
		cm.sendOk("您当前未兑现金额为"+ cm.getPlayer().getzb() +"积分 !");
		cm.dispose();	
		}else{
		var  j = cm.getPlayer().getzb()
		cm.setzb(-j);
		cm.settgjl(j*0.1);
		cm.getPlayer().set喇叭(cm.getPlayer().get喇叭()+j*1);
		cm.getPlayer().modifyCSPoints(1, +j*1000,true);
        cm.sendOk("领取成功！获得"+ j + "点累计充值.");
		cm.dispose();
        }
		
		  } else if (selection == 4) {
        if(cm.getPlayer().getcz()==0){//判断表吧应该是
		cm.sendOk("您当前未兑现金额为"+ cm.getPlayer().getcz() +"积分 !");
		cm.dispose();	
		}else{
		var l = cm.getPlayer().getcz()
		cm.setcz(-l);
		cm.getPlayer().set喇叭(cm.getPlayer().get喇叭()+l*1);
		cm.getPlayer().modifyCSPoints(1, +l*1000,true);
        cm.sendOk("领取成功！.");
		cm.dispose();
        }
		}
        } else if (status == 2) {
            if (beauty == 1) {
                if(cm.getPlayer().getAccountID()== selection){
					cm.sendOk("别逗了，自己怎么能推荐自己呢？");
                    status = -1;
				} else if(cm.getPlayer().getPrizeLog('推广登记')> 1 ){
					cm.sendOk("您的其他角色是否已经有登记！");
                    status = -1;
                } else {					 
					cm.getPlayer().settuiguang(+selection);
					cm.getPlayer().setPrizeLog('推广登记');
                    cm.sendOk("成功登记了推广人，谢谢您的参与。你登记了推广ID是：#r"+selection+"");
                    cm.dispose();
                }
                }
        }
    }
}
