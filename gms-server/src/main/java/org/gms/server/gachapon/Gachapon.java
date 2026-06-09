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
package org.gms.server.gachapon;

import lombok.Getter;
import org.gms.client.Character;
import org.gms.constants.id.NpcId;
import org.gms.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.server.ItemInformationProvider;
import org.gms.util.Randomizer;

/**
 * 扭蛋机
 * 管理全服扭蛋系统，支持不同城镇的扭蛋机类型和奖品池
 * 使用单例模式，按城镇划分奖品池
 *
 * @author Alan (SharpAceX)
 */
public class Gachapon {
    private static final Logger log = LoggerFactory.getLogger(Gachapon.class);
    /** 扭蛋机单例 */
    private static final Gachapon instance = new Gachapon();

    /**
     * 扭蛋机类型枚举
     */
    public enum GachaponType {
        /** 全局 */
        GLOBAL(-1, -1, -1, -1, new Global()),
        /** 射手村 */
        HENESYS(NpcId.GACHAPON_HENESYS, 90, 8, 2, new Henesys()),
        /** 魔法密林 */
        ELLINIA(NpcId.GACHAPON_ELLINIA, 90, 8, 2, new Ellinia()),
        /** 勇士部落 */
        PERION(NpcId.GACHAPON_PERION, 90, 8, 2, new Perion()),
        /** 废弃都市 */
        KERNING_CITY(NpcId.GACHAPON_KERNING, 90, 8, 2, new KerningCity()),
        /** 林中之城 */
        SLEEPYWOOD(NpcId.GACHAPON_SLEEPYWOOD, 90, 8, 2, new Sleepywood()),
        /** 蘑菇神社 */
        MUSHROOM_SHRINE(NpcId.GACHAPON_MUSHROOM_SHRINE, 90, 8, 2, new MushroomShrine()),
        /** 昭和村男澡堂 */
        SHOWA_SPA_MALE(NpcId.GACHAPON_SHOWA_MALE, 90, 8, 2, new ShowaSpaMale()),
        /** 昭和村女澡堂 */
        SHOWA_SPA_FEMALE(NpcId.GACHAPON_SHOWA_FEMALE, 90, 8, 2, new ShowaSpaFemale()),
        /** 天空之城 */
        LUDIBRIUM(NpcId.GACHAPON_LUDIBRIUM, 90, 8, 2, new Ludibrium()),
        /** 新叶城 */
        NEW_LEAF_CITY(NpcId.GACHAPON_NLC, 90, 8, 2, new NewLeafCity()),
        /** 冰封雪域 */
        EL_NATH(NpcId.GACHAPON_EL_NATH, 90, 8, 2, new ElNath()),
        /** 诺特勒斯 */
        NAUTILUS_HARBOR(NpcId.GACHAPON_NAUTILUS, 90, 8, 2, new NautilusHarbor());

        /** 扭蛋物品配置 */
        private final GachaponItems gachapon;
        @Getter
        /** NPC ID */
        private final int npcId;
        /** 普通概率 */
        private final int common;
        /** 稀有概率 */
        private final int uncommon;
        /** 罕见概率 */
        private final int rare;

        GachaponType(int npcid, int c, int u, int r, GachaponItems g) {
            this.npcId = npcid;
            this.gachapon = g;
            this.common = c;
            this.uncommon = u;
            this.rare = r;
        }

        private int getTier() {
            int chance = Randomizer.nextInt(common + uncommon + rare) + 1;
            if (chance > common + uncommon) {
                return 2; //Rare
            } else if (chance > common) {
                return 1; //Uncommon
            } else {
                return 0; //Common
            }
        }

        public int[] getItems(int tier) {
            return gachapon.getItems(tier);
        }

        /**
         * 随机抽取指定品质的物品
         * 从本地奖池和全局奖池中随机选择
         *
         * @param tier 品质等级
         * @return 物品ID
         */
        public int getItem(int tier) {
            int[] gacha = getItems(tier);
            int[] global = GLOBAL.getItems(tier);
            int chance = Randomizer.nextInt(gacha.length + global.length);
            return chance < gacha.length ? gacha[chance] : global[chance - gacha.length];
        }

        /**
         * 根据NPC ID获取扭蛋类型
         *
         * @param npcId NPC ID
         * @return 扭蛋类型，未找到返回null
         */
        public static GachaponType getByNpcId(int npcId) {
            for (GachaponType gacha : GachaponType.values()) {
                if (npcId == gacha.npcId) {
                    return gacha;
                }
            }
            return null;
        }

        /**
         * 获取扭蛋种类名称列表
         *
         * @return 名称数组
         */
        public static String[] getLootNames() {
            return new String[]{
                    I18nUtil.getMessage("GachaCommand.message2"),
                    I18nUtil.getMessage("GachaCommand.message3"),
                    I18nUtil.getMessage("GachaCommand.message4"),
                    I18nUtil.getMessage("GachaCommand.message5"),
                    I18nUtil.getMessage("GachaCommand.message6"),
                    I18nUtil.getMessage("GachaCommand.message7"),
                    I18nUtil.getMessage("GachaCommand.message8"),
                    I18nUtil.getMessage("GachaCommand.message9"),
                    I18nUtil.getMessage("GachaCommand.message10"),
                    I18nUtil.getMessage("GachaCommand.message11")
            };
        }

        public static int[] getLootIds() {
            return new int[]{
                    NpcId.GACHAPON_HENESYS,
                    NpcId.GACHAPON_ELLINIA,
                    NpcId.GACHAPON_PERION,
                    NpcId.GACHAPON_KERNING,
                    NpcId.GACHAPON_SLEEPYWOOD,
                    NpcId.GACHAPON_MUSHROOM_SHRINE,
                    NpcId.GACHAPON_SHOWA_MALE,
                    NpcId.GACHAPON_SHOWA_FEMALE,
                    NpcId.GACHAPON_NLC,
                    NpcId.GACHAPON_NAUTILUS
            };
        }
    }

    /**
     * 处理扭蛋
     * 根据NPC ID查找对应扭蛋类型，随机抽取物品
     *
     * @param npcId NPC ID
     * @return 扭蛋结果（含等级和物品ID）
     */
    public GachaponItem process(int npcId) {
        GachaponType gacha = GachaponType.getByNpcId(npcId);
        int tier = gacha.getTier();
        int item = gacha.getItem(tier);
        return new GachaponItem(tier, item);
    }

    /**
     * 扭蛋物品
     * 封装扭蛋结果的等级和物品ID
     */
    public static class GachaponItem {
        /** 物品ID */
        private final int id;
        /** 品质等级 */
        private final int tier;

        /**
         * 构造扭蛋物品
         *
         * @param t 品质等级
         * @param i 物品ID
         */
        public GachaponItem(int t, int i) {
            id = i;
            tier = t;
        }

        /**
         * 获取品质等级
         *
         * @return 品质等级
         */
        public int getTier() {
            return tier;
        }

        /**
         * 获取物品ID
         *
         * @return 物品ID
         */
        public int getId() {
            return id;
        }
    }

    /**
     * 记录扭蛋日志
     *
     * @param player 玩家
     * @param itemId 物品ID
     * @param map    地图名称
     */
    public static void log(Character player, int itemId, String map) {
        String itemName = ItemInformationProvider.getInstance().getName(itemId);
        log.info(I18nUtil.getLogMessage("Gachapon.log.info"), player.getName(), itemName, itemId, map);
    }
}