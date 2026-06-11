// 装饰符号定义（贴合冒险岛视觉风格，可按需替换）
var 充值图标 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/4/0#";
var 星星 = "#fEffect/CharacterEff/1112903/0/0#";
var 分割线 = 星星 + 星星 + 星星 + 星星 + 星星 + 星星 + 星星 + 星星 + 星星;

// 全局交互状态码
var status = -1;
// 道具-赞助配置（核心：ID/兑换金额/扣除数量，可直接扩展）
var itemConfig = [
    [5220003, 200, -1], // 道具5220003 → 200赞助，扣除1个五倍经验
    [5220004, 500, -1], // 道具5220004 → 500赞助，扣除1个十倍经验
    [3700071, 200, -1], // 道具3700071 → 200赞助，扣除1个白银VIP
    [3700070, 200, -1], // 道具3700070 → 200赞助，扣除1个黄金VIP
    [3700069, 300, -1], // 道具3700069 → 300赞助，扣除1个钻石VIP
    [1112541, 700, -1], // 道具1112541 → 800赞助，扣除1个青铜
    [1116038, 800, -1], // 道具1116038 → 900赞助，扣除1个风流
    [1114215, 900, -1]// 道具1114215 → 1000赞助，扣除1个国庆
];

function start() {
    // 初始化状态，触发首次交互
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    // mode=-1：玩家关闭对话框，直接结束交互
    if (mode == -1) {
        cm.dispose();
        return;
    }

    // mode=0且状态≥0：玩家点击「取消/返回」，友好提示并结束
    if (status >= 0 && mode == 0) {
        cm.sendOk("感谢使用CDK道具兑换中心，欢迎下次再来！");
        cm.dispose();
        return;
    }

    // 状态更新：点击确定/选择则+1，点击返回则-1
    status = mode == 1 ? status + 1 : status - 1;

    // 一级导航栏（status=0）：循环生成所有道具选择项，自动适配itemConfig扩展
    if (status == 0) {
        var txt = "";
        txt += "\t\t" + 充值图标 + " 道具兑换余额中心#n#k\r\n";
        txt += "\t\t" + 分割线 + "\r\n\r\n";
        // 循环遍历itemConfig，自动生成L0-L7所有选择项，无需手动添加
        for (var i = 0; i < itemConfig.length; i++) {
            txt += "\t\t\t#L" + i + "##b使用#v" + itemConfig[i][0] + "#兑换" + itemConfig[i][1] + "余额#l\r\n\r\n";
        }
        txt += "\t\t" + 分割线 + "\r\n";
        cm.sendSimple(txt); // 发送带所有道具选择项的导航对话框
    }

    // 二级执行逻辑（status=1）：根据玩家选择执行对应兑换，原有逻辑完全不变
    else if (status == 1) {
        // 根据选择项获取对应配置：道具ID/兑换金额/扣除数量
        var curItemId = itemConfig[selection][0];
        var rechargeAmount = itemConfig[selection][1];
        var deductNum = itemConfig[selection][2];

        // 第一步：判定玩家是否持有当前选择的道具
        if (cm.haveItem(curItemId)) {
            // 核心操作：扣除道具 + 发放赞助
            cm.gainItem(curItemId, deductNum); // 扣除对应道具（负数为扣除）
            cm.gainmoney(rechargeAmount);     // 发放对应金额赞助

            // 记录每日赞助BossLog
            cm.getPlayer().setBossLog("每日赞助", 0, rechargeAmount);

            // 全服漂浮喇叭通知（显示兑换道具图标）
            cm.全服漂浮喇叭("【赞助中心】恭喜土豪玩家 " + cm.getPlayer().getName() + " 使用#v" + curItemId + "#兑换" + rechargeAmount + "元余额！", 5120015);
            
            // 普通喇叭三连发（简化循环写法，易维护）
            for (var i = 0; i < 3; i++) {
                cm.喇叭(2, "恭喜土豪玩家：[" + cm.getPlayer().getName() + "]使用经验卡回收，获得 " + rechargeAmount + " 余额！！！");
            }

            // 玩家本地弹窗提示（含道具扣除、赞助到账、当前余额，信息完整）
            var successMsg = "";
            successMsg += "\t\t" + 充值图标 + " 兑换成功！#n\r\n\r\n";
            successMsg += "#b扣除道具：#r#v" + curItemId + "# ×" + Math.abs(deductNum) + "#k\r\n";
            successMsg += "#b获得余额：#r" + rechargeAmount + " 元#k\r\n";
            successMsg += "#b当前余额：#r" + cm.getPlayer().getmoney() + " 元#k\r\n\r\n";
            successMsg += "赞助已实时到账，可直接使用！";
            cm.sendOk(successMsg);

            // 后台日志记录（含道具信息，方便管理员核查）
            Packages.tools.FileoutputUtil.log(
                "log\\玩家相关\\CDK卡密自助充值.log",
                "[" + cm.getName() + "]使用道具#v" + curItemId + "#×" + Math.abs(deductNum) + "兑换【" + rechargeAmount + " 余额】，当前余额：" + cm.getPlayer().getmoney() + " 元"
            );
        } else {
            // 未持有对应道具：精准提示，不执行任何充值操作
            cm.sendOk("? 兑换失败！\r\n\r\n你未持有道具#v" + curItemId + "#\r\n请确认道具是否在背包中、数量是否充足后重试！");
        }
        // 兑换流程结束，关闭所有交互
        cm.dispose();
    }
}