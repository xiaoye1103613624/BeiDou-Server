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
package org.gms.net.server.coordinator.login;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.config.GameConfig;
import org.gms.net.server.Server;
import org.gms.net.server.coordinator.session.Hwid;
import org.gms.net.server.world.World;
import org.gms.util.Pair;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * 登录绕过协调器
 * 管理角色的登录绕过请求，用于离线迁移和重新登录场景
 */
public class LoginBypassCoordinator {
    /** 单例实例 */
    private final static LoginBypassCoordinator instance = new LoginBypassCoordinator();

    /**
     * 获取单例实例
     *
     * @return 登录绕过协调器实例
     */
    public static LoginBypassCoordinator getInstance() {
        return instance;
    }

    /** 登录绕过缓存表，key为(Hwid,账号ID)对，value为(是否PIC绕过,过期时间戳)对 */
    private final ConcurrentHashMap<Pair<Hwid, Integer>, Pair<Boolean, Long>> loginBypass = new ConcurrentHashMap<>();

    /**
     * 判断是否可以绕过登录验证
     *
     * @param hwid  硬件ID
     * @param accId 账号ID
     * @param pic   是否PIC验证
     * @return true表示可以绕过，false表示需要验证
     */
    public boolean canLoginBypass(Hwid hwid, int accId, boolean pic) {
        try {
            Pair<Hwid, Integer> entry = new Pair<>(hwid, accId);
            Boolean p = loginBypass.get(entry).getLeft();

            return !pic || p;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public void registerLoginBypassEntry(Hwid hwid, int accId, boolean pic) {
        long expireTime = (pic ? GameConfig.getServerInt("bypass_pic_expiration") : GameConfig.getServerInt("bypass_pin_expiration"));
        if (expireTime > 0) {
            Pair<Hwid, Integer> entry = new Pair<>(hwid, accId);
            expireTime = Server.getInstance().getCurrentTime() + MINUTES.toMillis(expireTime);
            try {
                pic |= loginBypass.get(entry).getLeft();
                expireTime = Math.max(loginBypass.get(entry).getRight(), expireTime);
            } catch (NullPointerException npe) {
            }

            loginBypass.put(entry, new Pair<>(pic, expireTime));
        }
    }

    public void unregisterLoginBypassEntry(Hwid hwid, int accId) {
        String hwidValue = hwid == null ? null : hwid.hwid();
        Pair<String, Integer> entry = new Pair<>(hwidValue, accId);
        loginBypass.remove(entry);
    }

    public void runUpdateLoginBypass() {
        if (!loginBypass.isEmpty()) {
            List<Pair<Hwid, Integer>> toRemove = new LinkedList<>();
            Set<Integer> onlineAccounts = new HashSet<>();
            long timeNow = Server.getInstance().getCurrentTime();

            for (World w : Server.getInstance().getWorlds()) {
                for (Character chr : w.getPlayerStorage().getAllCharacters()) {
                    Client c = chr.getClient();
                    if (c != null) {
                        onlineAccounts.add(c.getAccID());
                    }
                }
            }

            for (Entry<Pair<Hwid, Integer>, Pair<Boolean, Long>> e : loginBypass.entrySet()) {
                if (onlineAccounts.contains(e.getKey().getRight())) {
                    long expireTime = timeNow + MINUTES.toMillis(2);
                    if (expireTime > e.getValue().getRight()) {
                        loginBypass.replace(e.getKey(), new Pair<>(e.getValue().getLeft(), expireTime));
                    }
                } else if (e.getValue().getRight() < timeNow) {
                    toRemove.add(e.getKey());
                }
            }

            if (!toRemove.isEmpty()) {
                for (Pair<Hwid, Integer> p : toRemove) {
                    loginBypass.remove(p);
                }
            }
        }
    }

}