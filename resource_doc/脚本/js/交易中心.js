var 交易中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/14#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var status = -1;
var itemInfo;
var sele = false;
var sel;
var sel1 =0;
var JT = "#fUI/Basic/BtHide3/mouseOver/0#";//小箭头
var 心 = "#fUI/GuildMark.img/Mark/Etc/00009001/14#";//大红心
var 装备2 = "#fUI/CashShop.img/Base/Tab2/Enable/0#";
var 消耗2 = "#fUI/CashShop.img/Base/Tab2/Enable/1#";  
var 设置2 = "#fUI/CashShop.img/Base/Tab2/Enable/2#"; 
var 其他2 = "#fUI/CashShop.img/Base/Tab2/Enable/3#";   
var 特殊2 = "#fUI/CashShop.img/Base/Tab2/Enable/4#"; 
var traid ;
var nx  = false;

function start() {
	status = -1;
	//cm.RemoveSelSuo(16);
	var text = ""+dd+"\r\n\t\t\t"+交易中心+"\r\n"+群粉心+"\r\n"
	//text +="------------------------------------------------------\r\n"
	text += "#b选择需购物品分类：\r\n"
	//text +="------------------------------------------------------\r\n"
	//text +="   #L8##b"+JT+"搜索金币#k#l         \r\n\r\n"
//	text+= "#L1#"+装备2+"#l\t#L2#"+消耗2+"#l\t#L3#"+设置2+"#l\t#L4#"+其他2+"#l\t#L5#"+特殊2+"#l\r\n\r\n";   //防止前开，只开放装备交易
	text+= "#L1#"+装备2+"#l\t#L3#"+设置2+"#l\r\n\r\n";
	text +="------------------------------------------------------\r\n"
	text += "#L0##e#k"+粉心+"商人服务"+粉心+"#l  #L6##b"+粉心+"搜索关键字"+粉心+"#l  #L7##r"+粉心+"重置条件"+粉心+"#l#n\r\n\r\n"
	//text +=cm.getItemListInfo(cm.getItemList());
	cm.sendOk(text)
}

function action(mode, type, selection) {
	if (mode <= 0 ) {
		//var traid = cm.getPlayer().getTradingId();
		if(traid >0){
			cm.RemoveSelSuo(traid);
			
		}
        cm.dispose();
        return;
    } else {
        status++;
    }
	if(nx){
		start();
		nx = false;
	}
    if (status == 0) {
        sel = selection;
		
        if (sel == 0) {
			cm.dispose()
			cm.openNpc(9900004,"商品处理")
			return
        }
        if (sel == 6) {
            cm.sendGetText("请输入检索物品名称。");
        }else if(sel ==7){
			cm.dispose();
			cm.openNpc(9900004,"交易中心")
			return
		}else if(sel ==8){
			cm.getPlayer().setTrading(false)
			cm.getMesoList();
			status = 1;
			return;
		} else{
			var txt ="请点击你要查询的物品\r\n"
			//cm.playerMessage(selection)
			var itemids =  cm.getItemIds(selection);
			var next = true;
			for(var i =0;i<itemids.size();i++){
				var itemid = itemids.get(i);
				txt +="#L"+itemid+"##v"+itemid+"##t"+itemid+"##l\r\n"
				next = false;
			}
			if(next){
				cm.sendOk("没有商品上架中")
				nx = true;
				return
			}
			cm.sendOk(txt)
			return
		}

    }
	//cm.playerMessage(status)
    if (status == 1) {
		if(sel ==6){
			var s = cm.getText();
			
			var text ="#d当前搜索关键字：#k#r"+s+"#k\r\n"
			var itemids = cm.getItemList(s);
			if(itemids.size() ==0){
				cm.sendOk(text + "交易行没有你搜索的商品")
				nx = true;
				return
			}
			for(var i=0;i<itemids.size();i++){
				var itemid = itemids.get(i);
				text +="#L"+itemid+"##v"+itemid+"##t"+itemid+"##l\r\n"
				
			}
			cm.sendOk(text)
			status =0
			sel =0;
			return
		}
		//cm.playerMessage(selection)
		cm.getPlayer().setTrading(true)
		cm.getItemListByItemId(selection);
				
				
			
		

    }
	if(status ==2){
		cm.setSelSuo(selection,cm.getPlayer().getId());
		traid = selection;
		//cm.playerMessage(traid)
		cm.sendYesNo("你确定要购买么")
	}
    if (status == 3) {
		//sel = selection;
		
		//cm.playerMessage(traid)
		//cm.playerMessage(traid)
		if(traid >0){
			itemInfo = cm.getItemInfo(traid);
		}
		if(itemInfo == null){
			cm.sendOk("信息已经过期")
			cm.dispose();
			return;
		}
		if(cm.getPlayer().getCSPoints(itemInfo.type) < itemInfo.price){
					cm.RemoveSelSuo(traid);
					cm.sendOk("你的货币不够，请检查后购买")
					nx = true;
					return
				}
				if(cm.isFull(1)||cm.isFull(2)||cm.isFull(3)||cm.isFull(4)||cm.isFull(5)){
					cm.RemoveSelSuo(traid);
					cm.sendOk("你的背包空间不够，请各位置留出一个空格")
					nx = true;
					return
				}
				if(cm.buyItemTrading(traid,itemInfo)){
					cm.RemoveSelSuo(traid);
					cm.getItemLog("交易行购买",cm.getName()+"购买了"+itemInfo.toString())
					cm.getPlayer().modifyCSPoints(itemInfo.type,-itemInfo.price,true);
					cm.sendOk("购买成功")
				}else{
					cm.sendOk("出现错误，请重新打开交易行")
					
				}
				cm.dispose()
		return
    }



}
