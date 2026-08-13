/*
	自助开双爆脚本 —— 全服双倍掉落
	玩家可通过消耗点券为当前大区开启双倍掉落
	全服生效，不可叠加，不可重复开启
**/
// ==================== 配置变量 ====================
var 扣除点券数量 = 100000;        // 开启双倍掉落需要扣除的点券数量（nxCredit）
var 开双时限小时 = 2;             // 双倍掉落持续时间（小时）
var 事件名称 = "2xDropEvent";     // 服务端事件脚本名称（scripts-zh-CN/event/2xDropEvent.js）

// ==================== UI变量 ====================
var 心2 = "#fUI/GuildMark.img/Mark/Etc/00009001/15#";
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }

        if (status == 0) {
            // 先检查是否已有双倍掉落活动在运行（不可重复开启，提前拦截）
            var em = cm.getEventManager(事件名称);
            if (em != null) {
                var prop = em.getProperty("state");
                if (prop != null && prop.equals("1")) {
                    cm.sendOk("目前正在全服双倍掉落活动进行中，不可重复开启！\r\n\r\n请等活动结束后再来。");
                    cm.dispose();
                    return;
                }
            }

            var txt = "";
            txt += "   \t\t  " + 心2 + "   " + 心2 + "  #d#e < 自助双倍掉落 > #k#n  " + 心2 + "   " + 心2 + "\r\n\r\n";
            txt += "\tHi~#b#h ##k，这里是#b自助双倍掉落系统#k，如果你有足够的点券，可以给全服开启双倍掉落哦！\r\n";
            txt += "\t当前点券余额：#r" + cm.getPlayer().getCashShop().getCash(1) + "#k 点券\r\n\r\n";
            txt += "#L1##e#d给全服开双倍掉落 " + 开双时限小时 + " 小时#n#r（消耗" + 扣除点券数量 + "点券）#l\r\n\r\n";
            txt += "#k 全服开启，所有线路生效，不可叠加重复开启。#k\r\n";
            cm.sendSimple(txt); // sendOk不支持#L选择项，改用sendSimple
        }

        if (status == 1) {
            if (selection == 1) {
                // 检查点券是否足够
                if (cm.getPlayer().getCashShop().getCash(1) < 扣除点券数量) {
                    cm.sendOk("你的点券不足，需要 #r" + 扣除点券数量 + "#k 点券。");
                    cm.dispose();
                    return;
                }

                // 获取事件管理器
                var em = cm.getEventManager(事件名称);
                if (em == null) {
                    cm.sendOk("发生未知错误，事件管理器未找到，请稍后再试...");
                    cm.dispose();
                    return;
                }

                // 二次确认：检查是否已有双倍掉落活动在运行（防止并发）
                var prop = em.getProperty("state");
                if (prop != null && prop.equals("1")) {
                    cm.sendOk("目前正在全服双倍掉落活动进行中，不可重复开启！");
                    cm.dispose();
                    return;
                }

                // 扣除点券
                cm.getPlayer().getCashShop().gainCash(1, -扣除点券数量);
                // 发送点券更新包给客户端，刷新点券显示
                var PacketCreator = Java.type('org.gms.util.PacketCreator');
                cm.getClient().sendPacket(PacketCreator.showCash(cm.getPlayer()));
                // 提示扣除信息
                cm.getPlayer().dropMessage(5, "已扣除 #r" + 扣除点券数量 + "#k 点券，剩余： #b" + cm.getPlayer().getCashShop().getCash(1) + "#k 点券");

                // 保存原始掉落倍率，以便结束后恢复
                var originalDropRate = em.getWorldServer().getDropRate();
                em.setProperty("state", "1");
                em.setProperty("originalDropRate", String(originalDropRate));

                // 立即开启双倍掉落
                em.schedule("start", 0);

                // 在指定时长后自动关闭双倍掉落
                var durationMs = 开双时限小时 * 60 * 60 * 1000;
                em.schedule("stop", durationMs);

                // 全服公告
                em.getWorldServer().broadcastPacket(
                    PacketCreator.serverNotice(5, "【双倍爆率】恭喜 [" + cm.getName() + "] 为全服开启了双倍爆率活动，持续" + 开双时限小时 + "小时，要刷装备的赶紧了！")
                );

                cm.sendOk("双倍掉落活动已开启！全服所有线路生效，持续 #r" + 开双时限小时 + "#k 小时。\r\n\r\n已扣除 #r" + 扣除点券数量 + "#k 点券，剩余： #b" + cm.getPlayer().getCashShop().getCash(1) + "#k 点券");
                cm.dispose();
            }
        }
    }
}
