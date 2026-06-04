/*
	Red Sign - 101st Floor Eos Tower (221024500)
*/

var status = 0;
var 事件名称 = "pk"   //要配套event事件里面的名称  文件名
var BOSS地图;  //event事件里定义
var 出去地图   //event事件里定义	
function start() {
    action(1, 0, 0);
}
var s

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else if (mode == 0) {
        cm.dispose();
        return;
    } else {
        cm.dispose();
        return;
    }
	var em = cm.getEventManager(事件名称);
	if (em == null) {
		cm.sendOk("当前未开通,请联系管理员。");
		cm.dispose()
		return
	}
	if(em.getProperty("state") != null &&　em.getProperty("state")== "2"){
		cm.sendOk("当前有人在挑战")
		cm.dispose()
		return
		
	}
	var eim = em.getInstance("pk");
	BOSS地图 = Math.floor(em.getProperty("进入地图ID"));
	出去地图 = Math.floor(em.getProperty("出去的地图ID"));
	if(cm.getMapId()==BOSS地图 && status ==1){
		cm.sendYesNo("你确定要出去？");
		status =2;
	}else if(status ==1){
		
		if(eim == null){
			var txt ="请选择你要下注的东西:\r\n"
			for(var i =0;i<96;i++){
				var item  = cm.getItem(4,i);
				if(item != null){
					txt +="#L"+i+"##v"+item.getItemId()+"##t"+item.getItemId()+"##l"
				}
			}
			cm.sendOk(txt);
		}else{
			var itemid =parseInt(eim.getProperty("物品ID"))
			var sl = parseInt(eim.getProperty("物品SL"))
			var txt ="当前挑战的物品#t"+itemid+"# x "+sl+"\r\n"
			var chr  = cm.getMap().getCharacterById(parseInt(eim.getProperty("挑战者ID")));
			if(chr == null){
				cm.sendOk("挑战者离开了当前地图")
				eim.dispose_NoLock();
				cm.dispose()
				return;
			}
			txt +="当前挑战者名字："+chr.getName()+""
			cm.sendYesNo("请确认是否接受挑战")
			
		}
		
	}else if(status ==2){
		s = selection;
		if(eim == null){
			var item = cm.getItem(4,s);
			cm.sendGetNumber("请问你要下注多少个呢  当前背包#c"+item.getItemId()+"#",1,1,10000)
		}else{
			var itemid =parseInt(eim.getProperty("物品ID"))
			var sl = parseInt(eim.getProperty("物品SL"))
			if(!cm.haveItem(itemid,sl)){
				cm.sendOk("你的物品不够啊")
				cm.dispose()
				return
			}
			var chr  = cm.getMap().getCharacterById(parseInt(eim.getProperty("挑战者ID")));
			if(chr == null){
				cm.sendOk("挑战者离开了当前地图")
				eim.dispose_NoLock();
				cm.dispose()
				return;
			}
			cm.gainItem(itemid,-sl);
			em.setProperty("state","2")
			eim.registerPlayer(chr)
			eim.registerPlayer(cm.getPLayer())
			cm.dispose()
		}
	}else if(status ==3){
		
		if(eim == null){
			var item = cm.getItem(4,s);
			if(item.getQuantity() < selection){
				cm.sendOk("你的物品不够啊")
				cm.dispose()
				return
			}
			try{
				eim = em.getInv().invokeFunction("setup","1")
			}catch(e){
				cm.playerMessage(e)
			}
			eim.setProperty("物品ID",item.getItemId())
			eim.setProperty("物品SL",selection);
			eim.setProperty("挑战者ID",cm.getPlayer().getId())
			cm.gainItem(item.getItemId(),-selection)
			cm.dispose()
		}
	}
	
}