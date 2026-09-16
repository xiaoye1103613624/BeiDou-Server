/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.gms.net.server.channel.handlers;

import org.gms.client.Client;
import org.gms.client.Job;
import org.gms.config.GameConfig;
import org.gms.constants.id.NpcId;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.scripting.npc.NPCScriptManager;
import org.gms.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 点击引导精灵。头顶入口迁侧边栏后：开关开启时打开北斗助手（与侧边栏同源）。
 */
public class ClickGuideHandler extends AbstractPacketHandler {
    private static final Logger log = LoggerFactory.getLogger(ClickGuideHandler.class);

    /** 与 {@link org.gms.server.sidebar.SidebarTools} / 北斗助手一致的聚合入口。 */
    private static final String SIDEBAR_HELPER_SCRIPT = "xy/portal/北斗助手";

    @Override
    public void handlePacket(InPacket p, Client c) {
        // 缺省视为开启（迁移头顶入口到侧边栏）
        boolean replace = GameConfig.getValueProp("server", "replace_overhead_icons") == null
                || GameConfig.getServerBoolean("replace_overhead_icons");
        if (replace) {
            log.info(I18nUtil.getLogMessage("OverheadIcons.clickGuideToHelper"),
                    c.getPlayer() != null ? c.getPlayer().getId() : -1);
            NPCScriptManager.getInstance().start(c, 9900001, SIDEBAR_HELPER_SCRIPT, c.getPlayer());
            return;
        }
        if (c.getPlayer().getJob().equals(Job.NOBLESSE)) {
            NPCScriptManager.getInstance().start(c, NpcId.MIMO, null);
        } else {
            NPCScriptManager.getInstance().start(c, NpcId.LILIN, null);
        }
    }
}
