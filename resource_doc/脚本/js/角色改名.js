var status = 0;
var selected;
var name="";
//-------------------------------- 

var 改名字需要的点卷 = 666;
var 改名字需要的金币 = 1000000;
var 改名字需要的元宝 = 1000;

//--------------------------------

function start() {
	status = -1;
	action(1, 0, 0);
}
function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0) {
			cm.sendOk("名字修改服务再会");
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		name = cm.getName();
		if (status == 0) {
			cm.sendNext(" 您好~尊敬的 #b"+name+"#k, 您需要改名么？\r\n#r注意（名字不可带侮辱,违法,血腥,暴力,涉政,敏感,广告,违背游戏主旨的不良内容.一经发现封号处理，请慎重行事！#n\r\n #d改名需要：\r\n\r\n#r金币 * "+改名字需要的金币+"\r\n点卷 * "+改名字需要的点卷+"\r\n元宝 * "+改名字需要的元宝+"\r\n\r\n\r\n（点击下一步即可改名）.");
		} else if (status == 1) {
			cm.sendGetText("请在在下面的白色框内输入你想要的名字.  请注意！你的名字必须在#b2 - 6个字符之间#k. \r\n 不能有特殊字符或者改为其他玩家相同的名字 .");
		} else if (status == 2) {
			selected = cm.getText();
			if (selected.length() < 2 || selected.length() > 6) {
				cm.sendOk("你的名字必须在 2 ~ 6 个字符之间.");
				cm.dispose();
				return ;
			} else if (selected.indexOf(" ") != -1) {
				cm.sendOk("不能包含空格");
				cm.dispose();
				return ;
			} else if (checktext(selected) == false) {
				cm.sendOk("名字带有侮辱性质 或者空格名字  或者违禁字");
				cm.dispose();
				return ;
			} else if (cm.isEligibleName(selected) == true) {
				cm.sendOk("名字不通过，名字太长或者已经存在该名字!");
				cm.dispose();
				return ;	
			}
			cm.sendYesNo("你想要#b" + selected + "#k 这个名字吗?\r\n 改名成功后会掉线！");
		} else if (status == 3) {
				
				if(cm.getMeso() < 改名字需要的金币){
				cm.sendOk("金币不足"+改名字需要的金币+"!");
				cm.dispose();
				return ;	
					
				}
				
				if(cm.getmoneyb() < 改名字需要的元宝){
				cm.sendOk("元宝不足"+改名字需要的元宝+"!");
				cm.dispose();
				return ;	
					
				}				
				
				if(cm.getPlayer().getCSPoints(1) < 改名字需要的点卷){
				cm.sendOk("点卷不足"+改名字需要的点卷+"!");
				cm.dispose();
				return ;	
					
				}
				
//-----------------------------------------------------------
               // cm.gxring(selected);
				cm.getPlayer().setName(selected);
				
				cm.getPlayer().modifyCSPoints(0, -改名字需要的金币, true);
				cm.getPlayer().modifyCSPoints(1, -改名字需要的点卷, true);
				cm.setmoneyb( -改名字需要的元宝);
				cm.sendOk("好了! 你现在的名字是#b" + selected + "!#k 再见！.");
				cm.worldMessage(6, "【改名系统】玩家：[" + name + "]改名为" + selected + "！请大家以后注意这2B");
				cm.getPlayer().saveToDB(false, false);
				cm.getPlayer().getClient().getChannelServer().removePlayer(cm.getPlayer());
				cm.getPlayer().getClient().getSession().close();//端口连接
		} else if (status == 4) {
			cm.sendOk("更改成功。请切换频道或者上下线生效~");
			cm.getPlayer().getClient().getSession().close();//端口连接
			cm.dispose();
			return ;
		}
	}
}

function checktext(text) { //字符串过虑功能，自动删除关键字宠物
	var gl = new Array("傻B", "GM", "gm", "管理", "你妈", "操", "SB", "沙雕",  "杨伟", "逗比", "狗托", "阳痿", "冒险岛", "外挂", "滚 ","垃圾","狗拖", "	");
	for (i = 0; i < gl.length; i++) {
		if (text.indexOf(gl[i]) >= 0) {
			return false;
			break;
		}
	}
	return true;
}
