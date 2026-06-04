
load('nashorn:mozilla_compat.js');
var 爆物查询A = "#fUI/UIWindow/DragonBall_A/BtClose/normal/0#";

var status;
var h1=-1;
var h2=-1;

function start(){
	status = -1;
	str = "";
	select = -1;

	cm.sendSimple(""+爆物查询A+"\r\n=============#e欢迎使用物品掉落查询工具#n=============\r\n\r\n我可以帮助您#r了解游戏中各种道具#k的#r爆出概率和来源#k,您可以快速查找特定道具的爆率信息,了解它们在#r哪些怪物可以获得#k,这样，您可以有针对性地进行游戏任务，提高获取所需道具的效率。\r\n#b\t\t\t\t#L1#查询物品掉落怪物#l#k#k" + str);

}

function action(mode, type, selection) {
	if(mode<0){
	cm.dispose();	
	}
       if (status >= 0 && mode == 0) {//联动菜单
            cm.dispose();
           cm.openNpc(9900004, "快捷导航"); 	
		   return;
        }	
		
		if(mode==0){
            cm.dispose();
           cm.openNpc(9900004, "快捷导航"); 	
		   return;			
		}
		
	if (mode == 1) {
		status++;
	} else {
		status--;
		cm.dispose();
		return;
	}
	
	

	switch (status) {
	case 0:
		str = selection;
		cm.sendGetText("输入您想查询的道具名字:");
		break;
	case 1:
		
		if(isChina(cm.getText())==true){
		cm.sendOk(cm.searchData2(str, cm.getText()));
		}else{
		cm.sendOk("请输入正确的道具名称");	
		cm.dispose();
		return;			
		}

		
		
		break;
	case 2:
	    h2=selection;
		if (!cm.foundData(str, cm.getText())) {
			cm.dispose();
			return;
		}else
		cm.getMobs(h2);
		cm.dispose();
	}
}


function isChina(s){
    if (escape(s).indexOf( "%u" )<0){
        return false;
    } else {
       return true;
    }
}
