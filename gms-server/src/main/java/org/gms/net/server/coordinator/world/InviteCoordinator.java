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
package org.gms.net.server.coordinator.world;

import org.gms.client.Character;
import org.gms.util.Pair;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 邀请协调器
 * 管理组队和公会邀请的协调，防止重复邀请请求
 */
public class InviteCoordinator {

    /**
     * 邀请结果类型枚举
     */
    public enum InviteResultType {
        /** 接受邀请 */
        ACCEPTED,
        /** 拒绝邀请 */
        DENIED,
        /** 未找到邀请 */
        NOT_FOUND
    }

    /**
     * 邀请类型枚举
     * 每种邀请类型维护独立的邀请表
     */
    public enum InviteType {
        /** 家族邀请 */
        FAMILY,
        /** 家族召唤邀请 */
        FAMILY_SUMMON,
        /**  messenger聊天邀请 */
        MESSENGER,
        /** 交易邀请 */
        TRADE,
        /** 组队邀请 */
        PARTY,
        /** 公会邀请 */
        GUILD,
        /** 联盟邀请 */
        ALLIANCE;

        /** 邀请表，key为被邀请人角色ID，value为引用对象 */
        final ConcurrentHashMap<Integer, Object> invites;
        /** 邀请人映射，key为被邀请人角色ID，value为邀请人 */
        final ConcurrentHashMap<Integer, Character> inviteFrom;
        /** 超时计数器，key为被邀请人角色ID，value为超时计数 */
        final ConcurrentHashMap<Integer, Integer> inviteTimeouts;
        /** 邀请参数，key为被邀请人角色ID，value为参数数组 */
        final ConcurrentHashMap<Integer, Object[]> inviteParams;

        InviteType() {
            invites = new ConcurrentHashMap<>();
            inviteTimeouts = new ConcurrentHashMap<>();
            inviteFrom = new ConcurrentHashMap<>();
            inviteParams = new ConcurrentHashMap<>();
        }

        private Map<Integer, Object> getRequestsTable() {
            return invites;
        }

        private Map<Integer, Integer> getRequestsTimeoutTable() {
            return inviteTimeouts;
        }

        private Pair<Character, Object[]> removeRequest(Integer target) {
            invites.remove(target);
            Character from = inviteFrom.remove(target);
            inviteTimeouts.remove(target);

            return new Pair<>(from, inviteParams.remove(target));
        }

        private boolean addRequest(Character from, Object referenceFrom, int targetCid, Object[] params) {
            Object v = invites.putIfAbsent(targetCid, referenceFrom);
            if (v != null) {    // there was already an entry
                return false;
            }

            inviteFrom.put(targetCid, from);
            inviteTimeouts.put(targetCid, 0);
            inviteParams.put(targetCid, params);
            return true;
        }

        private boolean hasRequest(int targetCid) {
            return invites.containsKey(targetCid);
        }
    }

    // note: referenceFrom is a specific value that represents the "common association" created between the sender/recver parties
    public static boolean createInvite(InviteType type, Character from, Object referenceFrom, int targetCid, Object... params) {
        return type.addRequest(from, referenceFrom, targetCid, params);
    }

    public static boolean hasInvite(InviteType type, int targetCid) {
        return type.hasRequest(targetCid);
    }

    public static InviteResult answerInvite(InviteType type, int targetCid, Object referenceFrom, boolean answer) {
        Map<Integer, Object> table = type.getRequestsTable();

        Character from = null;
        InviteResultType result = InviteResultType.NOT_FOUND;
        Pair<Character, Object[]> inviteInfo = null;

        Object reference = table.get(targetCid);
        if (referenceFrom.equals(reference)) {
            inviteInfo = type.removeRequest(targetCid);
            from = inviteInfo.getLeft();
            if (from != null && !from.isLoggedInWorld()) {
                from = null;
            }

            result = answer ? InviteResultType.ACCEPTED : InviteResultType.DENIED;
        }

        return new InviteResult(result, from, inviteInfo != null ? inviteInfo.getRight() : new Object[0]);
    }

    public static void removeInvite(InviteType type, int targetCid) {
        type.removeRequest(targetCid);
    }

    public static void removePlayerIncomingInvites(int cid) {
        for (InviteType it : InviteType.values()) {
            it.removeRequest(cid);
        }
    }

    public static void runTimeoutSchedule() {
        for (InviteType it : InviteType.values()) {
            Map<Integer, Integer> timeoutTable = it.getRequestsTimeoutTable();

            if (!timeoutTable.isEmpty()) {
                Set<Entry<Integer, Integer>> entrySet = new HashSet<>(timeoutTable.entrySet());
                for (Entry<Integer, Integer> e : entrySet) {
                    int eVal = e.getValue();

                    if (eVal > 5) { // 3min to expire
                        it.removeRequest(e.getKey());
                    } else {
                        timeoutTable.put(e.getKey(), eVal + 1);
                    }
                }
            }
        }
    }

    public static class InviteResult {

        public final InviteResultType result;
        public final Character from;
        public final Object[] params;

        private InviteResult(InviteResultType result, Character from, Object[] params) {
            this.result = result;
            this.from = from;
            this.params = params;
        }
    }
}