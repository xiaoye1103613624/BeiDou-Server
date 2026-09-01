package org.gms.net.server.channel.handlers;

import org.gms.client.Client;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.scripting.npc.NPCScriptManager;
import org.gms.server.sidebar.SidebarTools;

/**
 * 侧边栏工具点击处理 — ijl15 sidetoolbar 集成
 * 客户端点击侧边栏按钮 → SIDEBAR_TOOL(0xC6) → 本Handler → 打开对应脚本
 * 脚本映射来自 {@link SidebarTools}（DB 可配，热重载）。
 */
public class SidebarToolHandler extends AbstractPacketHandler {

    @Override
    public void handlePacket(InPacket p, Client c) {
        int toolIndex = p.readByte();
        String script = SidebarTools.scriptOf(toolIndex);
        if (script == null || script.isBlank()) {
            return;
        }
        NPCScriptManager.getInstance().start(c, 9900001, script, c.getPlayer());
    }
}
