// 可自行修改：兑换产出装备列表
var rewardList = [
	1092030,//	枫叶盾
	1092045,//	冒险岛魔法师盾牌
	1092046,//	冒险岛战士盾牌
	1092047,//	冒险岛飞侠盾牌		
    //1092089,//	战神贤者盾
   // 1092088,//	战神黑暗盾
   // 1092087,//	战神勇士盾
	1092074,//	至尊不速之客战士盾
    1092079,//	至尊不速之客法师盾
	1092084,//	至尊不速之客飞侠盾
	1092050,//	冷艳剑盾
	1092049,//	热情剑盾
];

// 可自行修改：所需材料 [物品ID, 数量]，可无限增删
var needMaterial = [
	 [4001126, 888],//枫叶
	 [4000313, 8],//黄金枫叶	
	 [4011008, 1],//锂	 
	 [4021009, 1],//星石
	 [4011007, 1],//月石
 
     // [4000407, 200],//铜心
     // [4000402, 200],//银心
     // [4000406, 200],//金心

	 // [4001083, 1],//扎昆象征
	 // [4001084, 1],//闹钟象征
	 // [4001085, 1]//皮亚努斯的象征	
];

var 返回图标 = "#fUI/UIWindow.img/itemSearch/BtBack/normal/0#";
var selectIndex;
var status = -1;

// 固定入口，服务端标准写法
function start() {
    action(1, 0, 0);
}

// 核心交互流程（分段status，和你参考代码完全一致）
function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else if (mode == 0) {
        status--;
    } else {
        cm.dispose();
        return;
    }

    // 第一页：展示材料 + 可选装备列表
    if (status == 0) {
        var msg = "";
        msg += "\r\n#d兑换所需材料:#b \r\n\r\n";
        // 遍历展示所有材料
        for (var ii = 0; ii < needMaterial.length; ii++) {
            msg += "#i" + needMaterial[ii][0] + "##z" + needMaterial[ii][0] + "#x" + needMaterial[ii][1];
            // 每3个材料换行，排版美观
            if (ii % 3 == 0) {
                msg += "\r\n";
            }
        }
        msg += "\r\n#g----------------------------------------------\r\n";
        // 遍历展示所有可兑换装备
        for (var i = 0; i < rewardList.length; i++) {
            msg += "#r#L" + i + "#";
            msg += "#i" + rewardList[i] + "##z" + rewardList[i] + "##l\r\n";
        }
        msg += "#g----------------------------------------------\r\n";
        msg += "#L999#" + 返回图标 + "#l
"
        // 发送选择界面
        cm.sendSimple("#r 装备兑换使者 #v4000110#\r\n选择你想要兑换的装备：\r\n" + msg);
    }

    // 第二页：校验背包、材料，弹出确认框
    else if (status == 1) {
        if (selection == 999) { cm.dispose(); cm.openNpc(9900001, "xy/装备系统/v000/套装制作升级"); return; }
        selectIndex = selection;
        var targetItem = rewardList[selectIndex];

        // 1. 检查背包是否有空间
        if (!cm.canHold(targetItem)) {
            cm.sendNext("#r背包空间不足，请清理后再来！固有装备只能持有一个。");
            cm.dispose();
            return;
        }

        // 2. 遍历检查所有材料是否足够
        for (var m = 0; m < needMaterial.length; m++) {
            var matId = needMaterial[m][0];
            var matNum = needMaterial[m][1];
            if (!cm.haveItem(matId, matNum)) {
                cm.sendNext("#b材料不足！缺少 #r#i" + matId + "##t" + matId + "# 共" + matNum + "个");
                cm.dispose();
                return;
            }
        }

        // 确认兑换弹窗
        cm.sendYesNo("#b确定要兑换装备 #r #i" + targetItem + "##z" + targetItem + "# 吗？");
    }

    // 第三页：扣除材料 + 发放装备，完成兑换
    else if (status == 2) {
        var targetItem = rewardList[selectIndex];
        // 扣除全部材料
        for (var m = 0; m < needMaterial.length; m++) {
            cm.gainItem(needMaterial[m][0], -needMaterial[m][1]);
        }
        // 发放装备
        cm.gainItem(targetItem, 1);
        cm.sendNext("#b兑换成功！已获得装备 #i" + targetItem + "##z" + targetItem + "#");
        cm.dispose();
    }

    // 异常处理
    else {
        //cm.sendNext("#b好的，谢谢惠顾！");
        cm.dispose();
    }
}