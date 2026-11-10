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
package org.gms.server.life;

import org.gms.client.Client;
import org.gms.provider.Data;
import org.gms.provider.DataProvider;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.DataTool;
import org.gms.provider.wz.WZFiles;
import org.gms.server.ShopFactory;
import org.gms.server.maps.MapObjectType;
import org.gms.util.PacketCreator;

public class NPC extends AbstractLoadedLife {
    private final NPCStats stats;

    public NPC(int id, NPCStats stats) {
        super(id);
        this.stats = stats;
    }

    public boolean hasShop() {
        return ShopFactory.getInstance().getShopForNPC(getId()) != null;
    }

    public void sendShop(Client c) {
        ShopFactory.getInstance().getShopForNPC(getId()).sendShop(c);
    }

    // 雇佣商店/卖场设置 NPC（WZ info/storebank=1，如 Fredrick 9030000）
    public boolean hasStoreBank() {
        DataProvider npc = DataProviderFactory.getDataProvider(WZFiles.NPC);
        Data npcData = npc.getData(getId() + ".img");
        if (npcData == null) {
            return false;
        }
        return DataTool.getIntConvert("info/storebank", npcData, 0) == 1;
    }

    // 仓库 NPC（WZ info/trunkGet 或 info/trunkPut 存在，如 9030100）
    public boolean hasTrunk() {
        DataProvider npc = DataProviderFactory.getDataProvider(WZFiles.NPC);
        Data npcData = npc.getData(getId() + ".img");
        if (npcData == null) {
            return false;
        }
        return DataTool.getIntConvert("info/trunkGet", npcData, 0) > 0
                || DataTool.getIntConvert("info/trunkPut", npcData, 0) > 0;
    }

    @Override
    public void sendSpawnData(Client client) {
        client.sendPacket(PacketCreator.spawnNPC(this));
        client.sendPacket(PacketCreator.spawnNPCRequestController(this, true));
    }

    @Override
    public void sendDestroyData(Client client) {
        client.sendPacket(PacketCreator.removeNPCController(getObjectId()));
        client.sendPacket(PacketCreator.removeNPC(getObjectId()));
    }

    @Override
    public MapObjectType getType() {
        return MapObjectType.NPC;
    }

    public String getName() {
        return stats.getName();
    }
}
