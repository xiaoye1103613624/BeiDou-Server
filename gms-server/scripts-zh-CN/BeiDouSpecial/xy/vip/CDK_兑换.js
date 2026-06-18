/*
	CDK兑换脚本 — 通用兑换码系统
	玩家输入CDK兑换码，服务端校验后发放奖励（点券/抵用券/金币/道具）
	所有兑换尝试均记录日志，用于审计和反滥用检测
**/
var 完成 = "#fUI/UIWindow/Quest/Tab/enabled/2#";
var status = 0;
var inputCode = "";
var redeemResult = null;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        cm.sendOk("你取消了兑换。");
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0) {
        cm.sendGetText("请输入CDK兑换码：\r\n\r\n#b提示：#kCDK不区分大小写，兑换后无法退回。");
    } else if (status == 1) {
        inputCode = cm.getText();
        if (inputCode == null || inputCode.length == 0) {
            cm.sendOk("兑换码不能为空。");
            cm.dispose();
            return;
        }

        // 通过 Spring 上下文获取 CdkService 执行兑换
        try {
            var ServerManager = Java.type('org.gms.manager.ServerManager');
            var context = ServerManager.getApplicationContext();
            if (context == null) {
                cm.sendOk("系统未就绪，请稍后再试。");
                cm.dispose();
                return;
            }

            // 使用字符串 bean 名称获取，避免 GraalJS HostClass → getBean(Class) 类型匹配问题
            var service = context.getBean("cdkService");
            // 获取玩家IP（用于限流和日志）
            var ip = "";
            try {
                ip = cm.getPlayer().getClient().getRemoteAddress();
                if (ip == null) {
                    ip = "unknown";
                }
            } catch (e) {
                ip = "unknown";
            }

            redeemResult = service.redeem(inputCode, cm.getPlayer().getName(), ip);
        } catch (e) {
            cm.sendOk("系统错误，请联系管理员。\r\n" + e.getMessage());
            cm.dispose();
            return;
        }

        if (redeemResult != null && redeemResult.getSuccess()) {
            // 发送全服公告
            try {
                var PacketCreator = Java.type('org.gms.util.PacketCreator');
                var world = cm.getPlayer().getWorldServer();
                world.broadcastPacket(PacketCreator.serverNotice(6,
                    "【CDK兑换】恭喜 " + cm.getPlayer().getName() + " 使用CDK兑换码获得了 " + redeemResult.getMessage()));
            } catch (e) {
                // 公告发送失败不影响兑换流程
            }

            cm.sendOk(`${完成} #e#d 兑换成功！#k#n\r\n\r\n` +
                      "兑换码：#b" + inputCode + "#k\r\n\r\n" +
                      "获得奖励：\r\n#r" + (redeemResult.getMessage() || "查看背包") + "#k\r\n\r\n" +
                      "请检查背包、点券和金币变动。");
        } else {
            var msg = redeemResult != null ? redeemResult.getMessage() : "未知错误";
            cm.sendOk("#e#rX   兑换失败#k#n\r\n\r\n" +
                      "兑换码：#b" + inputCode + "#k\r\n\r\n" +
                      "原因：#r" + msg + "#k\r\n\r\n" +
                      "如有疑问请联系管理员。");
        }
        cm.dispose();
    }
}
