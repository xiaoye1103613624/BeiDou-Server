package org.gms.net.server.channel.handlers;

import org.gms.client.Client;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.scripting.npc.NPCScriptManager;

/**
 * 侧边栏工具点击处理 — ijl15 sidetoolbar 集成
 * 客户端点击侧边栏按钮 → SIDEBAR_TOOL(0xC6) → 本Handler → 打开对应脚本
 */
public class SidebarToolHandler extends AbstractPacketHandler {

    private static final String[] SCRIPTS = {
            "xy/portal/便民工具",   // 0: 便民工具
            "xy/portal/装备中心",   // 1: 装备中心
            "xy/portal/兑换中心",   // 2: 兑换中心
            "xy/portal/VIP会员",    // 3: VIP会员
            "xy/portal/成长系统",   // 4: 成长系统
            "xy/portal/每日任务",   // 5: 每日任务
            "xy/portal/社交系统",   // 6: 社交系统
            "xy/portal/收集系统",   // 7: 收集系统
            "xy/portal/GM工具",     // 8: GM工具
            "在线奖励_nextlevel",   // 9: 在线奖励（侧边栏直达）
    };

    @Override
    public void handlePacket(InPacket p, Client c) {
        int toolIndex = p.readByte();
        if (toolIndex < 0 || toolIndex >= SCRIPTS.length) {
            return;
        }
        NPCScriptManager.getInstance().start(c, 9900001, SCRIPTS[toolIndex], c.getPlayer());
    }
}
