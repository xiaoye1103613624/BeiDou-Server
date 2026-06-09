package org.gms.net.server.task;

import org.gms.net.server.PlayerStorage;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.server.maps.MapManager;

/**
 * 怪物重生定时任务
 * 定期更新所有频道的地图，触发怪物重生逻辑
 *
 * @author Resinate
 */
public class RespawnTask implements Runnable {

    @Override
    public void run() {
        for (Channel ch : Server.getInstance().getAllChannels()) {
            PlayerStorage ps = ch.getPlayerStorage();
            if (ps != null) {
                if (!ps.getAllCharacters().isEmpty()) {
                    MapManager mapManager = ch.getMapFactory();
                    if (mapManager != null) {
                        mapManager.updateMaps();
                    }
                }
            }
        }
    }
}