/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

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
package org.gms.server.maps;

import org.gms.scripting.event.EventInstanceManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 【类型】MapManager（class），包 {@code org.gms.server.maps}。
 * 地图管理器/地图工厂，负责频道内地图实例的加载、缓存与生命周期管理。
 * 支持通过地图ID获取 {@link MapleMap} 实例，采用读写锁保证多线程安全。
 */
public class MapManager {
    /** 所属频道 */
    private final int channel;
    /** 所属世界 */
    private final int world;
    /** 关联的事件实例管理器 */
    private EventInstanceManager event;

    /** 地图缓存（地图ID -> 地图实例） */
    private final Map<Integer, MapleMap> maps = new HashMap<>();

    /** 地图缓存读锁 */
    private final Lock mapsRLock;
    /** 地图缓存写锁 */
    private final Lock mapsWLock;

    /**
     * 构造函数：创建地图管理器实例
     * 
     * @param eim 关联的事件实例管理器
     * @param world 所属世界
     * @param channel 所属频道
     */
    public MapManager(EventInstanceManager eim, int world, int channel) {
        this.world = world;
        this.channel = channel;
        this.event = eim;

        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        this.mapsRLock = readWriteLock.readLock();
        this.mapsWLock = readWriteLock.writeLock();
    }

    /**
     * 重置地图
     * 
     * <p>从缓存中移除指定地图并重新加载它。</p>
     * 
     * @param mapid 地图ID
     * @return 重置后的地图实例
     */
    public MapleMap resetMap(int mapid) {
        mapsWLock.lock();
        try {
            maps.remove(mapid);
        } finally {
            mapsWLock.unlock();
        }

        return getMap(mapid);
    }

    /**
     * 从WZ文件加载地图
     * 
     * @param mapid 地图ID
     * @param cache 是否缓存加载的地图
     * @return 加载的地图实例
     */
    private synchronized MapleMap loadMapFromWz(int mapid, boolean cache) {
        MapleMap map;

        if (cache) {
            mapsRLock.lock();
            try {
                map = maps.get(mapid);
            } finally {
                mapsRLock.unlock();
            }

            if (map != null) {
                return map;
            }
        }

        map = MapFactory.loadMapFromWz(mapid, world, channel, event);

        if (cache) {
            mapsWLock.lock();
            try {
                maps.put(mapid, map);
            } finally {
                mapsWLock.unlock();
            }
        }

        return map;
    }

    /**
     * 获取地图实例
     * 
     * <p>从缓存中获取指定ID的地图实例，如果不存在则从WZ文件加载并缓存。</p>
     * 
     * @param mapid 地图ID
     * @return 地图实例
     */
    public MapleMap getMap(int mapid) {
        MapleMap map;

        mapsRLock.lock();
        try {
            map = maps.get(mapid);
        } finally {
            mapsRLock.unlock();
        }

        return (map != null) ? map : loadMapFromWz(mapid, true);
    }

    /**
     * 通过生命体ID获取地图
     * 
     * @param lifeId 生命体ID
     * @return 对应的地图实例，如果找不到则返回null
     */
    public MapleMap getMapByLifeId(int lifeId) {
        String mapId = MapFactory.getMapIdByLifeId(lifeId);
        return mapId == null ? null : getMap(Integer.parseInt(mapId));
    }

    /**
     * 获取一次性地图
     * 
     * <p>获取不进行缓存的地图实例，通常用于临时或一次性的地图操作。</p>
     * 
     * @param mapid 地图ID
     * @return 地图实例
     */
    public MapleMap getDisposableMap(int mapid) {
        return loadMapFromWz(mapid, false);
    }

    /**
     * 检查地图是否已加载
     * 
     * @param mapId 地图ID
     * @return 如果地图已加载则返回true，否则返回false
     */
    public boolean isMapLoaded(int mapId) {
        mapsRLock.lock();
        try {
            return maps.containsKey(mapId);
        } finally {
            mapsRLock.unlock();
        }
    }

    /**
     * 获取所有地图
     * 
     * @return 地图映射副本
     */
    public Map<Integer, MapleMap> getMaps() {
        mapsRLock.lock();
        try {
            return new HashMap<>(maps);
        } finally {
            mapsRLock.unlock();
        }
    }

    /**
     * 更新所有地图
     * 
     * <p>执行地图刷新和怪物MP恢复等更新操作。</p>
     */
    public void updateMaps() {
        for (MapleMap map : getMaps().values()) {
            map.respawn();
            map.mobMpRecovery();
        }
    }

    /**
     * 销毁地图管理器
     * 
     * <p>释放所有地图资源并清理事件引用。</p>
     */
    public void dispose() {
        for (MapleMap map : getMaps().values()) {
            map.dispose();
        }

        this.event = null;
    }

}