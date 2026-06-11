/*
SnailMS脚本生成器
*/
var 美化1 = "#fUI/ChatBalloon.img/pet/120/nw#";//选择道具
var 美化3 = "#fUI/ChatBalloon.img/pet/120/ne#";//选择道具
var 美化2 = "#fUI/ChatBalloon.img/pet/120/n#";//选择道具
var 美化4 = "#fUI/ChatBalloon.img/pet/120/sw#";//选择道具
var 美化5 = "#fUI/ChatBalloon.img/pet/120/se#";//选择道具
var 美化6 = "#fUI/ChatBalloon.img/pet/120/s#";//选择道具
var 美化7 = "#fUI/ChatBalloon.img/156/arrow#";//选择道具
var 箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 道具栏位1 = "#fUI/UIWindow/Item/activeIcon#";
var 道具栏位2 = "#fUI/UIWindow/Item/activeExpChairIcon#";
var 道具栏位3 = "#fUI/UIWindow/Item/bossPetIcon#";
var 道具栏位4 = "#fUI/UIWindow/Item/disabled#";
var line2 = "#fUI/UIWindow/AdminClaim/default/2#";

var 武器描述 = "T2武器";
var 可转换列表 = [//武器
1302285,
1402204,
1432176,
1442232,
1382220,
1452214,
1462202,
1472223,
1332235,
1482177,
1492188

];
var 转换需要元宝数量 = 100;

var isCash = false;
var mark = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.sendOk("对话结束语");
            cm.dispose();
            return;
        }
        status--;
    }
    if (status == 0) {
        var text = "#d" + 美化1 + "" + 美化2 + "" + 美化2 + "" + 美化2 + "" + 美化2 + "" + 美化2 + "" + 美化2 + "『" + 武器描述 + "转换』" + 美化2 + "" + 美化2 + "" + 美化2 + "" + 美化2 + "" + 美化2 + "" + 美化2 + "" + 美化3 + "\r\n\r\n";
        text += "#k我这里可以转换" + 武器描述 + "，#r请选择你要转换的武器：\r\n";
        for (var i = 0; i < 可转换列表.length; i++) {
            text += "#L" + i + "#" + 箭头 + "#i" + 可转换列表[i] + "##t"+ 可转换列表[i] +"##l\r\n"
        }
        text += "\r\n\r\n";
     //   text += "#r#e注：请将要转换的武器放在背包装备栏第一格#b装备栏第一格#r，每次转换需要 #b" + 转换需要元宝数量 + " 元宝#r，转换后的武器继承原有武器的所有属性！"
		text += "#r#e注意：\r\n";
		text += "1. 将上面类型的装备放在背包#b装备栏第一格#k\r\n";
		text += "2. 需要消耗 #b" + 转换需要元宝数量 + " 元宝，会员一折#k\r\n";
		text += "3. 转换后继承原装备所有属性";
        cm.sendSimple(text);
    } else if (status == 1) {
        mark = selection;
        var item = cm.getInventory(1).getItem(1);
        if (item == null) {
            cm.sendOk("你把需要转换的装备放置在背包装备栏第一格");
            cm.dispose();
            return;
        }
        if (!containsId(item.getItemId(), 可转换列表)) {
            var text = "只有以下装备可以进行转换：\r\n";
            for (var i = 0; i < 可转换列表.length; i++) {
                text += "#i" + 可转换列表[i] + "#"
            }
            text += "\r\n\r\n#r#e请将要转换的装备放置在背包装备栏第一格！"
            cm.sendOk(text);
            cm.dispose();
            return;
        }
       // cm.sendYesNo("您选择了#i" + 可转换列表[mark] + "#，#r请确认是否要将背包里的#i" + item.getItemId() + "#转换为这件装备？")
		    cm.sendYesNo("确定要将#r #i" + item.getItemId() + "##t" + item.getItemId() + "##k\r\n" +
               "转换为#b #i" + 可转换列表[mark] + "##t" + 可转换列表[mark] + "##k 吗？\r\n\r\n" +
               "#r消耗 " + 转换需要元宝数量 + " 元宝，会员一折");
    } else if (status == 2) {
		var discountItem = 5010019; // 折扣道具ID
		var discount = cm.haveItem(discountItem) ? 0.1 : 1; // 如果有折扣道具，折扣为0.1，否则为1
		
        if (cm.getChar().getmoneyb() < 转换需要元宝数量) {
            cm.sendOk("你的元宝不够#r" + 转换需要元宝数量 + "");
            cm.dispose();
            return;
        }
        var item = cm.getInventory(1).getItem(1);
        if (item == null) {
            cm.sendOk("你把需要转换的装备放置在背包装备栏第一格");
            cm.dispose();
            return;
        }
        if (!containsId(item.getItemId(), 可转换列表)) {
            var text = "只有以下装备可以进行转换：\r\n";
            for (var i = 0; i < 可转换列表.length; i++) {
                text += "#L" + i + "#" + 箭头 + "#i" + 可转换列表[i] + "#"
            }
            text += "\r\n\r\n#r#e请将要转换的装备放置在背包装备栏第一格！"
            cm.sendOk(text);
            cm.dispose();
            return;
        }
		cm.setmoneyb(-转换需要元宝数量 * discount); // 根据折扣扣除元宝

        // 获取新装备的模板并复制
        var newEquip = Packages.server.MapleItemInformationProvider.getInstance().getEquipById(可转换列表[mark]).copy();

        // 继承原装备的属性
        newEquip.setStr(item.getStr());
        newEquip.setDex(item.getDex());
        newEquip.setInt(item.getInt());
        newEquip.setLuk(item.getLuk());
        newEquip.setHp(item.getHp());
        newEquip.setMp(item.getMp());
        newEquip.setWatk(item.getWatk());
        newEquip.setMatk(item.getMatk());
        newEquip.setWdef(item.getWdef());
        newEquip.setMdef(item.getMdef());
        newEquip.setAcc(item.getAcc());
        newEquip.setAvoid(item.getAvoid());
        newEquip.setHands(item.getHands());
        newEquip.setSpeed(item.getSpeed());
        newEquip.setJump(item.getJump());
        newEquip.setOwner(item.getOwner());
        newEquip.setUpgradeSlots(item.getUpgradeSlots());
        newEquip.setLevel(item.getLevel()); // 继承砸卷次数
        newEquip.setViciousHammer(item.getViciousHammer()); // 继承金锤子次数
		// 上锁
        newEquip.setFlag(1);

        // 删除原装备
        Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);

        // 添加新装备
        Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), newEquip, false);
		
		// 提示信息
    if (discount == 0.1) {
        cm.getPlayer().dropMessage(5, "会员一折转换装备：-" + (转换需要元宝数量 * discount) + "元宝");
    } else {
        cm.getPlayer().dropMessage(5, "转换装备：-" + 转换需要元宝数量 + "元宝");
    }
        cm.sendOk("恭喜你转换成功！去背包里查看你的新装备吧！");
        cm.dispose();
    } else {
        cm.dispose();
        return;
    }
}

function containsId(id, list) {
    for (var i in list) {
        if (list[i] == id) {
            return true;
        }
    }
    return false;
}