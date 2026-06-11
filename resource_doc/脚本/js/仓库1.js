load("nashorn:mozilla_compat.js");
importPackage(Packages.tools);
importPackage(Packages.database);

//--------------------------------------------------------------
var maxCellNums = 100;//[配置]允许开通的最大格子数。不建议过大。
//[配置]禁止存储物品黑名单
var blacklist = [
	2070000,
	2070001,
	2070003,
	2070005,
	2070006,
	2070007,
	2070011,
	2070016,
	2070019,
	2070021,
	2070023,
	2070024,
	2070025,
	2070026,
	2330000,
	2330001,
	2330002,
	2330003,
	2330004,
	2330005,
	2330006,
	2330007,
	2330008,
	2330016,
	2330020,
	2060000,
	2060001,
	2060002,
	2060003,
	2060004,
	2061000,
	2061001,
	2061002,
	2061003,
	2061004,
    4031332,
	4031333,
	4031334,
	4031335,
	4031336,
	4031337,
	4031338,
	4031339,
	4031340,
	4031341,
	4031505,
	4031504,
	4031506,
	2430043,
	2430042,
	2430041
];

//装备存储专用白名单
var whiteList = [
1003242,
1052357,
1082314,
1102294,
1132092,
1072521,

1003364,
1052405,
1082391,
1102322,
1132110,
1072610,

1122174,
1032121,
1082401,
1042231,
1062148,
1072618,

1003552,
1052461,
1102441,
1082433,
1132154,
1072666,

1003561,
1052467,
1102467,
1082438,
1132161,
1072672,

1003740,
1052569,
1102506,
1082498,
1132209,
1072768,

1003946,
1052647,
1102612,
1082540,
1132242,
1072853,

1003540,
1052460,
1032142,
1072664,
1082432,
1112738,
1122197,
1132152
];

//--------------------------------------------------------------
var 粉心 = "#fEffect/CharacterEff/1042176/2/0#";
var 红心 = "#fEffect/CharacterEff/1082229/0/0#";
var 粉星 = "#fEffect/CharacterEff/1112926/0/1#";
var 黑皇冠 = "#fUI/GuildMark/Mark/Etc/00009023/14#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var status = -1;//模组状态
var chr =null;
var say = "";
var openCellNums = 0;//已经开通的仓库格子数量
var useCellNums = 0;//已经使用的格子数
var result = null;//数据库读取的物品列表
var getOutItem = null;//用户选择要取出的物品ID
var _itemPositsion = 0;//存储物品时的位置
var InventoryType = 4;//背包存储类型
var cellPay = 100; //扩充仓库价格 点券

function start(){
	status = -1;

    action(1, 0, 0);
}


function action(mode, type, selection) {
	
    openCellNums = _depository_cellCount();
    useCellNums = _depositoryItem_cellCount();
    if(mode == -1){cm.dispose();return;}
    if(status == 0 && mode == 0) {cm.dispose();return;}
    if(mode == 1) {status++;} else {status--;}

    if(status == 0){
        say = "\r\n";
		result = storeQuery();
        say += "            #e#r╭#k#n 仓库格数:[ #r"+extend(result.length,2)+"#k / "+extend(openCellNums,2)+" ]格#e #r╮#n\r\n";
        say += "          #r#e╭～#n"+粉星+""+粉星+"#e～～～～～～～～～～╮#n\r\n";
        say += "          #r#r#L5#"+正方箭头+"[存放消耗]#l#L2#"+正方箭头+"[存放其他]#l\r\n\r\n";
		say += "          #r#r#L6#"+正方箭头+"[存放装备]#l\r\n\r\n";// 保留原L6菜单显示
        say += "          #b#L3#"+正方箭头+"[取出物品]#l#L1#"+正方箭头+"[增加容量]#l#k\r\n\r\n";
        say += "         #r#e╰ ～～～～～～～～～～～～～ ╯#n#l\r\n";
        cm.sendSimple(say);
    }else if(status == 1){
        if(selection == 1){
            //增加仓库格子。
            if(openCellNums>=maxCellNums){
                cm.sendOk("#b仓库容量最大"+maxCellNums+"，不可以在增加格子了！");
                cm.dispose();
            }else{
                status = 99;
                if(openCellNums == 0){_depository_cellInit();}//默认开启仓库
                say =""+正方箭头+"开启一个位置需要：[ #r"+cellPay+" #k] 点券";
                cm.sendYesNo(say);
            }
        }else if(selection == 6){// 新增L6单独判断：点击直接跳转存储界面
            cm.sendStorage();
            cm.dispose();
        }else if(selection ==2 || selection ==5){// 移除原selection==6的条件，保留原有存放消耗/其他逻辑
			if(selection == 2){
				InventoryType = 4;
			}else if(selection == 5){
				InventoryType = 2;
			}
            status = 199;
            //存放物品
            say = "#r当前背包物品支持存储的有：#k\r\n";
            var othersItems = cm.getInventory(InventoryType).list().iterator();
			while (othersItems.hasNext()) {
                var it = othersItems.next();
                if (it == null) {continue;}
                var itemid = it.getItemId();
                //黑名单过滤     
				var check=true;				
				for(var j=0;j<blacklist.length;j++){
					if(itemid ==blacklist[j]){
						check=false;
						break;
					}
				}
				//装备白名单存储（原逻辑保留，仅不再触发L6）
				if(InventoryType == 1){
					var isEquip = false;
					for(var k = 0;k<whiteList.length;k++){if(whiteList[k] == itemid){isEquip = true;break;}}
					if(!isEquip){continue;}//过滤
				}
				if(InventoryType == 1){
					say += "#L"+it.getPosition()+"#[#v" + itemid + "#]#b#z" + itemid + "##k#l\r\n";
				}else{	
					if(check){
						say += "#L"+it.getPosition()+"#[#v" + itemid + "#]#b#z" + itemid + "##k 数量：#n#r"+ it.getQuantity() +"#k 个。#l\r\n";
					}
				}					
            }
			cm.sendSimple(say);
        }else if(selection == 3){
            status = 299;
            //取物品
            say = "#r当前仓库存储的物品有：#k\r\n";
            result = storeQuery();
            if(result != null){
                say += "物品数量："+result.length+"\r\n";
                for(var i =0;i< result.length;i++){
                    say +="#L"+i+"##b#v"+result[i].itemId+"##z"+result[i].itemId+"# 数量："+result[i].count +" #r[取出]\r\n";
                }
                cm.sendSimple(say);
            }else{
                cm.sendOk("貌似还没有存储物品哟！");
                cm.dispose();
            }
			
        }else{
            cm.dispose();
        }
    }else if(status == 100){//仓库加格子
        //点券扣费代码。
        
        //if(cm.getMoney() < (cellPay+价格)){
        //    cm.sendOk("点券余额不足，当前点券："+cm.getMoney()+",开通一个格子需要："+(cellPay+价格)+"点券。");
        //    cm.dispose();
        //    return;
        //}

        //点券扣费代码
        if(cm.getPlayer().getCSPoints(1) < cellPay){
            cm.sendOk("点券余额不足，当前点券："+cm.getPlayer().getCSPoints(1)+",开通一个格子需要："+cellPay+"点券。");
            cm.dispose();
            return;
        }
		//黑卡扣除
		/*if(cm.getPlayer().getBossLog("黑卡",1) < cellPay){
            cm.sendOk("黑卡余额不足，当前点券："+cm.getPlayer().getBossLog("黑卡",1)+",开通一个格子需要："+cellPay+" 黑卡。");
            cm.dispose();
            return;
        }*/
        //开通格子
        if(_depository_cellAdd()){
            cm.gainNX(-cellPay);//点券扣费
           // cm.gainzb(-(cellPay+价格));//点券扣费
			cm.getPlayer().setBossLog("背包",1,1);
			//cm.getPlayer().setBossLog("黑卡", 1 ,-cellPay);
            cm.sendOk("#b仓库扩充成功,当前格数：#r"+_depository_cellCount());
            cm.dispose();
        }else{
            cm.sendOk("扩充仓库格子是发生未知错误，请联系管理员！");
            cm.dispose();
        }
        
        
    }else if(status == 200){//存储指定物品
        _itemPositsion = selection;
        var it = cm.getInventory(InventoryType).getItem(selection);
        if(it == null){
            cm.sendOk("请重新选择物品！");
            cm.dispose();
            return;
        }
        var maxCount = it.getQuantity()>32767?32767:it.getQuantity();
        say = "\r\n#b请输入你要存储的数量：(最大输入数量" +maxCount + ")";
        cm.sendGetNumber(say, it.getQuantity(), 1, maxCount);
    }else if(status == 201){
		if(selection <= 0){cm.sendOk("不存在BUG的谢谢");cm.dispose();return;}//BUG
        var it = cm.getInventory(InventoryType).getItem(_itemPositsion);
        if(it == null){cm.sendOk("请重新选择物品！");cm.dispose();return;}
        if(!cm.haveItem(it.getItemId(),selection)){cm.sendOk("你没有这么多物品啊！");cm.dispose();return;}

        var msg = store(it.getItemId(),selection);
        if(msg == ""){//删除物品
            cm.gainItem(it.getItemId(),-selection);
            cm.sendOk("存储成功");
            status =-1;
        }else{cm.sendOk(msg);cm.dispose();}
    }else if(status == 300){//取出物品
        if(result!=null){
            getOutItem = result[selection];
            if(getOutItem == null){
                cm.sendOk("选择要取出的物品出错，请联系管理员！");cm.dispose();return;    
            }else{
                var maxCount = getOutItem.count>32767?32767:getOutItem.count;
                say = "#r若单次提取过多，请务必检查背包空位是否足够#k\r\n#b请输入你要提取的数量：(最大输入数量" +maxCount + ")";
                cm.sendGetNumber(say, getOutItem.count, 1, maxCount);
            }
        }else{
            cm.sendOk("貌似没有可取出的物品哟！");cm.dispose();
        }
    }else if(status == 301){//提取
        if(getOutItem != null){
			var isEquip = false;
			for(var k = 0;k<whiteList.length;k++){if(whiteList[k] == getOutItem.itemId){isEquip = true;break;}}
			if(isEquip){
				if (cm.getInventory(1).isFull(selection)) {
					cm.sendOk("您的背包空间不足。\r\n\r\n请#r装备#k栏最少留出 #r"+selection+"#k 个空位。");
					cm.dispose();
					return;
				}
			}else{
				if (!cm.canHold(getOutItem.itemId,selection)){cm.sendOk("#b背包空间不足，为防止提取过多无法获取，请清理“其他栏”背包空位。");cm.dispose();return;}
			}
            //物品提取
            store_getOut(getOutItem.itemId,selection);
            if(selection == getOutItem.count){//清除完成提取的格子
                store_clear(getOutItem.itemId);
            }
			
			
			
			if(isEquip){
				for(var i =0;i<selection;i++){
					cm.gainItem(getOutItem.itemId,1);
				}
			}else{
				cm.gainItem(getOutItem.itemId,selection);
			}
            cm.sendOk("提取成功！");
            status =-1;
        }else{
            cm.sendOk("提取失败，请联系管理员。");
        }
    }else{
        cm.dispose();
    }

}
function extend(say,num){
	var curLength = say.toString().length;
	if(curLength < num){
		for(var i =0;i<num-curLength;i++){
			say += " ";
		}
	}
	return say;
}

/**
 * 查询仓库格子数量
 */
function _depository_cellCount(){

    var db = DBConPool.getInstance().getDataSource().getConnection();
	var sql = "SELECT count FROM wy_depository WHERE chrId = ?";
	var pstmt = db.prepareStatement(sql);
	pstmt.setInt(1, cm.getPlayer().getId());
	var res = pstmt.executeQuery();
	var num=0;
	if (res.next()) {
		num = res.getInt("count");
	} else {
		return 0;
	}
	res.close();
	pstmt.close();
	return num;
}
/**
 * 查询已经使用的仓库格子数
 */
function _depositoryItem_cellCount(){
    var db = DBConPool.getInstance().getDataSource().getConnection();
	var sql = "SELECT COUNT(1) as num FROM wy_depositoryItems WHERE chrId = ?";
	var pstmt = db.prepareStatement(sql);
	pstmt.setInt(1, cm.getPlayer().getId());
	var res = pstmt.executeQuery();
	var num=0;
	if (res.next()) {
		num = res.getInt("num");
	} else {
		return 0;
	}
	res.close();
	pstmt.close();
	return num;
}

/**
 * 增加格子数量
 */
function _depository_cellAdd(){
    var db = DBConPool.getInstance().getDataSource().getConnection();
    var sql = "UPDATE wy_depository SET count=count+1 WHERE chrId = ?";
    var pstmt = db.prepareStatement(sql);
	pstmt.setInt(1, cm.getPlayer().getId());
	var flag = pstmt.executeUpdate();
	pstmt.close();
	return flag;
}

/**
 * 初始化仓库
 */
function _depository_cellInit(){
    var db = DBConPool.getInstance().getDataSource().getConnection();
    var sql = "INSERT INTO wy_depository VALUES(?,0)";
    var pstmt = db.prepareStatement(sql);
	pstmt.setInt(1, cm.getPlayer().getId());
	var flag = pstmt.executeUpdate();
	pstmt.close();
	return flag;
}

function store(itemId,count){
    var checkNum = store_Check(itemId);
    if(checkNum > 1){return "数据库存在多条相同物品记录数，无法继续存储！请联系管理员";}
    if(checkNum== 1){//修改数量
        if(!store_Update(itemId,count)){return "修改存储失败！请联系管理员";}
    }else{//新增数量
        if(useCellNums<openCellNums){
            if(!store_Insert(itemId,count)){
                return "新增存储失败！请联系管理员";
            }
        }else{
            return "格子数量不够，请开通更多格子!";
        }
        
    }
    return "";
}

/**
 * 存储相同类型物品检查，存在返回记录数
 * @param {物品ID} itemId 
 */
function store_Check(itemId){
    //var db = DatabaseConnection.getConnection();
	var db = DBConPool.getInstance().getDataSource().getConnection();
    var sql = "SELECT COUNT(1) as num FROM wy_depositoryItems WHERE chrId = ? AND itemId = ?";
	var pstmt = db.prepareStatement(sql);
    pstmt.setInt(1, cm.getPlayer().getId());
    pstmt.setInt(2, itemId);
	var res = pstmt.executeQuery();
	var num=0;
	if (res.next()) {
		num = res.getInt("num");
	} else {
		return 0;
	}
	res.close();
	pstmt.close();
	return num;
}

/**
 * 物品数量存储修改
 * @param {物品ID} itemId 
 * @param {物品数量} count 
 */
function store_Update(itemId,count){
   // var db = DatabaseConnection.getConnection();
   var db = DBConPool.getInstance().getDataSource().getConnection();
    var sql = "UPDATE wy_depositoryItems SET count=count+? WHERE chrId = ? AND itemId = ?";
    var pstmt = db.prepareStatement(sql);
    pstmt.setInt(1, count);
    pstmt.setInt(2, cm.getPlayer().getId());
    pstmt.setInt(3, itemId);
	var flag = pstmt.executeUpdate();
	pstmt.close();
	return flag;
}

/**
 * 存储新增
 * @param {物品ID} itemId 
 * @param {数量} count 
 */
function store_Insert(itemId,count){
   // var db = DatabaseConnection.getConnection();
   var db = DBConPool.getInstance().getDataSource().getConnection();
    var sql = "INSERT INTO wy_depositoryItems VALUES(?,?,?)";
    var pstmt = db.prepareStatement(sql);
    pstmt.setInt(1, cm.getPlayer().getId());
    pstmt.setInt(2, itemId);
    pstmt.setInt(3, count);
	var flag = pstmt.executeUpdate();
	pstmt.close();
	return flag;
}

/**
 * 物品数量取出修改
 * @param {物品ID} itemId 
 * @param {物品数量} count 
 */
function store_getOut(itemId,count){
    //var db = DatabaseConnection.getConnection();
	var db = DBConPool.getInstance().getDataSource().getConnection();
    var sql = "UPDATE wy_depositoryItems SET count=count-? WHERE chrId = ? AND itemId = ?";
    var pstmt = db.prepareStatement(sql);
    pstmt.setInt(1, count);
    pstmt.setInt(2, cm.getPlayer().getId());
    pstmt.setInt(3, itemId);
	var flag = pstmt.executeUpdate();
	pstmt.close();
	return flag;
}

/**
 * 清除一个完成提取的格子
 * @param {} itemId 
 */
function store_clear(itemId){
    //var db = DatabaseConnection.getConnection();
	var db = DBConPool.getInstance().getDataSource().getConnection();
    var sql = "delete FROM  wy_depositoryItems WHERE chrId = ? AND itemId = ?";
    var pstmt = db.prepareStatement(sql);
    pstmt.setInt(1, cm.getPlayer().getId());
    pstmt.setInt(2, itemId);
	var flag = pstmt.executeUpdate();
	pstmt.close();
	return flag;
}


/**
 * 仓库存储数据查询
 */
function storeQuery(){
    //var db = DatabaseConnection.getConnection();
	var db = DBConPool.getInstance().getDataSource().getConnection();
	var sql = "SELECT * FROM  wy_depositoryItems WHERE chrId = ?";
	var pstmt = db.prepareStatement(sql);
	pstmt.setInt(1, cm.getPlayer().getId());
	var res = pstmt.executeQuery();
    var _result = [];
    while(res.next()){
        _result.push({chrId:res.getInt("chrId"),itemId:res.getInt("itemId"),count:res.getInt("count")});
    }
	res.close();
	pstmt.close();
    return _result;
}

/*
wy_depository//扩展仓库表
chrId int   //角色编号
count int   //已经开通的仓库格子数量

wy_depositoryItems//扩展仓库存储表              游戏窗口
chrId int
itemId int
count int
*/