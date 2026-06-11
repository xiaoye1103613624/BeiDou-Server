/*
 ZEVMS冒险岛(079)游戏服务端
 脚本：清理背包
 */
var 图标1 = "#fUI/UIWindow.img/FadeYesNo/icon7#";
var 图标2 = "#fUI/StatusBar.img/BtClaim/mouseOver/0#";
var 关闭 = "#fUI/UIWindow.img/CashGachapon/BtOpen/mouseOver/0#";
var 打开 = "#fUI/UIWindow.img/CashGachapon/BtOpen/disabled/0#";
var JD = "#fUI/Basic/BtHide3/mouseOver/0#";
var 心 = "#fUI/GuildMark.img/Mark/Etc/00009001/14#";
var 装备2 = "#fUI/CashShop.img/Base/Tab2/Enable/0#";
var 消耗2 = "#fUI/CashShop.img/Base/Tab2/Enable/1#";  
var 设置2 = "#fUI/CashShop.img/Base/Tab2/Enable/2#"; 
var 其他2 = "#fUI/CashShop.img/Base/Tab2/Enable/3#";   
var 特殊2 = "#fUI/CashShop.img/Base/Tab2/Enable/4#"; 
//var 存入的啦我giao = "#fUI/UIWindow.img/Trunk/BtPut/pressed/0#"; 
//var 取出的啦我giao = "#fUI/UIWindow.img/Trunk/BtGetAll/pressed/0#"; 
var 存入的啦我giao = "#fUI/UIWindow.img/Trunk/BtPut/pressed/0#"; 
var 取出的啦我giao = "#fUI/UIWindow.img/Trunk/BtGetAll/pressed/0#"; 
var 金币 = "#fUI/UIWindow.img/Item/BtCoin/normal/0#";
var a = "#fEffect/CharacterEff.img/1112926/0/1#";
var meso =0;
var s1;
var s2;
var slot;
var num;
function start() {
    status = 0;
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode < 0) {
        cm.dispose();
        return
    }
	if (status > 0 && mode == 0) {
        cm.dispose()
		cm.openNpc(9900004,"仓库")
		return
    }
	if(status ==2){
		if(s1 ==10000){
			if(selection ==0)
				status =12
			else
				status =11;
		}else if(s1 ==20000){
			if(selection ==0){
				status =22;
				//selection = -meso;
			}else{
				status =21;
			}
		}
		s2 = selection;
	}else 
	if(status == 1){
		if(selection >=10000)
			s1 =selection;
		
	}
	//cm.playerMessage(status)
	if(selection ==100){
		cm.dispose()
		cm.openNpc(9900004,"仓库")
		return
	}
	if (status == 0) {
		//meso = cm.getWarehouseMeso();
        meso = cm.getPlayer().getxmjbck();
        var selStr = "\t\t\t\t\t#r#e【随 身 仓 库】#k#n \r\n\r\n";
		//selStr +=" \t\t当前仓库金币：#d"+meso+"#k\r\n\r\n";
		selStr +=" \t\t#L10000#"+存入的啦我giao+"#l\t\t#L20000#"+取出的啦我giao+"#l\r\n"
        cm.sendSimpleS(selStr,2)
		status =1;
    } else if (status == 1) {
        //s1 = selection;
		var selStr = "";//\t\t\t" + 心 + "  " + 心 + " #r#e < 随身仓库 > #k#n " + 心 + "  " + 心 + "\r\n\r\n
		//selStr +=" \t\t当前仓库金币：#d"+meso+"#k\r\n\r\n";
		selStr +="\r\n\r\n\t\t#r#e#L100#"+JD+"返回主页"+JD+"#l\t#L0#"+金币+"金币银行"+金币+"#l#n#k\r\n\r\n\r\n"
		selStr +="\t请选择您要存/取的物品分类：\r\n"
    //    selStr += "#L1#"+装备2+"#l\t#L2#"+消耗2+"#l\t#L3#"+设置2+"#l\t#L4#"+其他2+"#l\t#L5#"+特殊2+"#l";
		selStr += "#L1#"+装备2+"#l\t#L2#"+消耗2+"#l\t#L3#"+设置2+"#l\t#L4#"+其他2+"#l";
	//	selStr += "#L1#"+装备2+"#l\t#L3#"+设置2+"#l\t#L4#"+其他2+"#l";
		cm.sendOkS(selStr,2)
		status =2;
    } else if(status ==11){
		//s2 =selection;
		status =12
		
		var txt ="请选择你要存入的装备\r\n"// #L100#返回主页#l
		var j=1;
		var ne = true;
		for(var i=0;i<96;i++){
			var item = cm.getInventory(s2).getItem(i);
			if(item !=null){
				if((item.getItemId() >=2070000 && item.getItemId() <=2079999) || (item.getItemId() >=2330000 && item.getItemId() <=2330050) || (item.getItemId() >=1113129 && item.getItemId() <=1113131) )
					continue
				
				txt +="#L"+i+"#"+i+".#v"+item.getItemId()+"#"//"+i+"
				ne = false;
				if(j %4 ==0)
					txt +="\r\n"
				j++;
			}
		}
		if(ne){
			cm.sendOkS("您当前背包没有装备",2)
			status =1;
			return
		}
		cm.sendOkS(txt,2);
	} else if(status ==12){
		slot = selection;
		if(s2 ==0){
        cm.dispose()
		cm.openNpc(9900004,"金币银行")
        // cm.sendGetNumber("请问你要存多少个呢,当前金币："+cm.getMeso(),1,1,cm.getMeso())
		}else{
			var items = cm.getWarehouseItems(s2)
			if(items.size() >=128){
				cm.sendOk("当前位置不够了，只能存入128个")
				cm.dispose()
				return
			}
			
			var item = cm.getInventory(s2).getItem(slot);
			if(s2 ==1){
				if(item.getSocket1()>0  ||item.getSocket2()>0  ||item.getSocket3()>0 ){
					cm.sendOk("附魔装备暂时无法存入.\r\n")
					status =11;
					return;
				}
			}
			if (item.getExpiration() >= 0 && item.getExpiration() < 4700000000000) {
				cm.sendOk("无法存入时限物品.");
				status = 11;
				return;
            }
			if( (item.getItemId() >=2070000 && item.getItemId() <=2079999) || 
				(item.getItemId() >=2330000 && item.getItemId() <=2330050) || 
				(item.getItemId() >=2022701 && item.getItemId() <=2022728) || 
				(item.getItemId() >=1902000 && item.getItemId() <=1902999) || 
				(item.getItemId() >=1912000 && item.getItemId() <=1912999) || 
				(item.getItemId() >=1140000 && item.getItemId() <=1143999)){
				cm.sendOk("附魔装备暂时无法存入.\r\n标跟子弹不能放入.\r\nBOSS箱子无法存入.\r\n勋章无法存入.\r\n坐骑与鞍子无法存入.")
				status =11;
				return;
			}
			cm.sendGetNumber("请问你要存多少个呢,当前数量："+item.getQuantity(),item.getQuantity(),1,item.getQuantity())
       // cm.dispose()
		//cm.openNpc(9900004,"金币银行")
		}
		status =13;
		
	}else if(status ==13){
		
		if(s2 ==0){
			if((meso +selection ) >2147483647 ){
				cm.sendOk("当前太多了，超过2147483647")
				cm.dispose()
				return
				
			}
			cm.sendYesNo("存入成功")
			status =0;
			//return
		}else{
			//cm.sendYesNo("存入成功,是否继续存放")
cm.sendOkS("存入成功,点击确定为您返回上一层",2)
			status = 11;
		}
		cm.putWarehouseItem(s2,slot,selection)
		
	}else if(status ==21){
		//s2 = selection;
		var items = cm.getWarehouseItems(s2)
    /* ---------- 先统计有效数量 ---------- */
    var validCount = 0;
    var its0 = items.entrySet().iterator();
    while (its0.hasNext()) {
        var id = its0.next().getValue().getItemId();
        if ((id >= 2070000 && id <= 2079999) ||
            (id >= 2330000 && id <= 2330050) ||
            (id >= 2022701 && id <= 2022728)) {
            continue;          // 被过滤掉
        }
        validCount++;          // 真正会显示的
    }

    if (validCount == 0) {
        cm.sendOkS("里面没有存入的装备", 2);
        status = 1;
        return;
    }
		var txt ="请选择你要取出的装备,背包记得留位置\r\n"
		var its = items.entrySet().iterator();
		while(its.hasNext()){
			var itemc = its.next();
			var ccc = itemc.getKey();
			var item = itemc.getValue();
			var id    = item.getItemId();
    // 不显示这些范围的道具
    if ((id >= 2070000 && id <= 2079999) ||
        (id >= 2330000 && id <= 2330050) ||
        (id >= 2022701 && id <= 2022728)) {
        continue;   // 直接跳过，不生成 #L# 选项
    }
			
			txt +="#L"+ccc+"##v"+item.getItemId()+"##t"+item.getItemId()+"#  x  "+item.getQuantity()+"#l\r\n"
		}

		cm.sendOkS(txt,2);
		status =22;
	}else if(status ==22){
		slot = selection;
		if(s2 ==0){
			if(meso <=0){

        cm.dispose()
		cm.openNpc(9900004,"金币银行")
				return
			}
			/*if((meso +cm.getMeso()) >2147483647){
				cm.sendOk("背包钱太多了，装不下了")
				cm.dispose()
				return
			}*/
			//cm.putWarehouseItem(0,0,-meso);
		    //cm.sendYesNo("取出成功")
			cm.dispose()
		    cm.openNpc(9900004,"金币银行")
			//status =0;
			return
		}
    if (cm.getInventory(1).isFull()) {
        cm.sendOk("背包所有栏需要留空位");
        cm.dispose();
        return;
    } else if (cm.getInventory(2).isFull()) {
        cm.sendOk("背包所有栏需要留空位");
         cm.dispose();
        return;
    } else if (cm.getInventory(3).isFull()) {
        cm.sendOk("背包所有栏需要留空位");
         cm.dispose();
        return;
    } else if (cm.getInventory(4).isFull()) {
        cm.sendOk("背包所有栏需要留空位");
         cm.dispose();
        return;
    } else if (cm.getInventory(5).isFull()) {
        cm.sendOk("背包所有栏需要留空位");
         cm.dispose();
        return;
   // } else if (cm.getPlayer().getName()==0) {//黑名单名字
    //    cm.sendOk("由于你被判断为交易行黑名单无法使用交易行");
    //     cm.dispose();
    //    return;
   // }
}else{
		cm.addWarehouseItem(slot);
		cm.sendOkS("取出成功,点击确定为您返回上一层",2)
		//cm.sendYesNo("取出成功，是否继续取出该物品栏的物品");
		status = 21;
	}
	}
}