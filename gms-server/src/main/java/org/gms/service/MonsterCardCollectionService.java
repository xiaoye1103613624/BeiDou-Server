package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.dao.entity.MonstercarddataDO;
import org.gms.dao.entity.MonsterbookDO;
import org.gms.dao.mapper.MonstercarddataMapper;
import org.gms.dao.mapper.MonsterbookMapper;
import org.gms.manager.ServerManager;
import org.gms.server.maps.MapFactory;

import org.gms.client.Character;

import java.util.*;

import static org.gms.dao.entity.table.MonsterbookDOTableDef.MONSTERBOOK_D_O;

/**
 * 怪物卡片收集服务
 * 提供按地区分组的卡片查询，供 GraalJS 脚本调用
 * 所有方法均为 static，通过 Java.type() 访问
 */
public class MonsterCardCollectionService {

    /**
     * 卡片区域DTO —— 一个区域包含多张卡片
     */
    public static class CardRegion {
        private final int mapid;
        private final String regionName;
        private final List<CardInfo> cards;

        public CardRegion(int mapid, String regionName, List<CardInfo> cards) {
            this.mapid = mapid;
            this.regionName = regionName;
            this.cards = cards;
        }

        public int getMapid() { return mapid; }
        public String getRegionName() { return regionName; }
        public List<CardInfo> getCards() { return cards; }
    }

    /**
     * 卡片信息DTO —— 单张卡片的收集信息
     */
    public static class CardInfo {
        private final int cardid;
        private final int mobid;
        private final int level;
        private final int mapid;

        public CardInfo(int cardid, int mobid, int level, int mapid) {
            this.cardid = cardid;
            this.mobid = mobid;
            this.level = level;
            this.mapid = mapid;
        }

        public int getCardid() { return cardid; }
        public int getMobid() { return mobid; }
        public int getLevel() { return level; }
        public int getMapid() { return mapid; }
    }

    // ==================== 数据库查询 ====================

    /**
     * 查询所有卡片数据（从 monstercarddata 表）
     */
    public static List<MonstercarddataDO> queryAllCards() {
        MonstercarddataMapper mapper = ServerManager.getApplicationContext()
                .getBean(MonstercarddataMapper.class);
        return mapper.selectAll();
    }

    /**
     * 查询角色的怪物图鉴等级（从 monsterbook 表）
     * @return Map<cardid, level>
     */
    public static Map<Integer, Integer> queryPlayerCardLevels(int charid) {
        MonsterbookMapper mapper = ServerManager.getApplicationContext()
                .getBean(MonsterbookMapper.class);
        List<MonsterbookDO> list = mapper.selectListByQuery(
                QueryWrapper.create().where(MONSTERBOOK_D_O.CHARID.eq(charid)));
        Map<Integer, Integer> result = new HashMap<>();
        for (MonsterbookDO mb : list) {
            result.put(mb.getCardid(), mb.getLevel());
        }
        return result;
    }

    // ==================== 核心方法 ====================

    /**
     * 获取按区域分组的卡片列表（含玩家收集进度）
     * 使用角色内存中的 MonsterBook 数据（实时），而非查数据库（有延迟）
     * 这是脚本调用的主力方法
     *
     * @param playerCards 角色内存中的卡片数据 Map<cardid, level>（来自 MonsterBook.getCards()）
     * @return 区域列表，每个区域包含该区域的卡片及玩家收集等级
     */
    public static List<CardRegion> getRegionsWithCards(Map<Integer, Integer> playerCards) {
        List<MonstercarddataDO> allCards = queryAllCards();

        // 按 mapid 分组（使用 LinkedHashMap 保持插入顺序）
        Map<Integer, List<CardInfo>> regionGroups = new LinkedHashMap<>();

        for (MonstercarddataDO card : allCards) {
            int cardid = card.getCardid();
            Integer levelObj = playerCards.get(cardid);
            int level = (levelObj != null) ? levelObj : 0;
            // mapid 可能为 null（迁移尚未执行时），兜底根据 cardid 区间推断
            Integer mapidObj = card.getMapid();
            int mapid = (mapidObj != null) ? mapidObj : inferMapidFromCardid(cardid);

            CardInfo info = new CardInfo(cardid, card.getMobid(), level, mapid);
            regionGroups.computeIfAbsent(mapid, k -> new ArrayList<>()).add(info);
        }

        // 构建区域列表，按 mapid 排序保证显示顺序一致
        List<Integer> sortedMapIds = new ArrayList<>(regionGroups.keySet());
        Collections.sort(sortedMapIds);

        List<CardRegion> regions = new ArrayList<>();
        for (int mapid : sortedMapIds) {
            String regionName = resolveRegionName(mapid);
            regions.add(new CardRegion(mapid, regionName, regionGroups.get(mapid)));
        }
        return regions;
    }

    /**
     * 从玩家内存 MonsterBook 中获取单张卡片的收集等级
     * 供 JS 脚本调用，避免 GraalJS Map.get() 类型匹配问题
     *
     * @param playerCards 角色内存中的卡片数据 Map<cardid, level>
     * @param cardid 卡片ID
     * @return 收集等级（0-5），未收集返回0
     */
    public static int getPlayerCardLevel(Map<Integer, Integer> playerCards, int cardid) {
        Integer level = playerCards.get(cardid);
        return level != null ? level : 0;
    }

    /**
     * 领取卡片收集AP奖励（在Java层完成验证+发放，避免GraalJS类型转换问题）
     *
     * @param player 角色对象
     * @param playerCards 角色内存中的卡片数据
     * @param cardid 卡片ID
     * @return true=领取成功 false=卡片等级不足
     */
    public static boolean claimCardAp(Character player, Map<Integer, Integer> playerCards, int cardid) {
        Integer levelObj = playerCards.get(cardid);
        int level = (levelObj != null) ? levelObj : 0;
        if (level < 5) {
            return false;
        }
        player.gainAp(1, false);
        return true;
    }

    // ==================== 区域名称解析 ====================

    /**
     * 解析地图ID对应的中文区域名称
     * 优先从 WZ 数据读取，失败则使用硬编码兜底
     */
    private static String resolveRegionName(int mapid) {
        String name = MapFactory.loadPlaceName(mapid);
        if (name != null && !name.isEmpty()) {
            return name;
        }
        return fallbackRegionName(mapid);
    }

    /**
     * 根据卡片ID区间推断所属地图ID（迁移未执行时的兜底逻辑）
     * 与 V1.11.12 迁移中的 CASE 逻辑保持一致
     */
    private static int inferMapidFromCardid(int cardid) {
        if (cardid >= 2388000 && cardid < 2389000) return 200000000;  // 特殊/Boss → 天空之城
        if (cardid >= 2387000 && cardid < 2388000) return 270000100;  // 时间神殿
        if (cardid >= 2386000 && cardid < 2387000) return 260000000;  // 阿里安特
        if (cardid >= 2385000 && cardid < 2386000) return 250000000;  // 武陵
        if (cardid >= 2384000 && cardid < 2385000) return 240000000;  // 神木村
        if (cardid >= 2383000 && cardid < 2384000) return 220000000;  // 玩具城
        if (cardid >= 2382000 && cardid < 2383000) return 211000000;  // 冰峰雪域
        if (cardid >= 2381000 && cardid < 2382000) return 100000000;  // 射手村/金银岛
        if (cardid >= 2380000 && cardid < 2381000) return 1000000;    // 明珠港/新手区
        return 100000000;  // 兜底
    }

    /**
     * 硬编码区域名称兜底（防止 WZ 数据缺失）
     */
    private static String fallbackRegionName(int mapid) {
        switch (mapid) {
            case 1000000:   return "明珠港";
            case 100000000: return "射手村";
            case 211000000: return "冰峰雪域";
            case 220000000: return "玩具城";
            case 240000000: return "神木村";
            case 250000000: return "武陵";
            case 260000000: return "阿里安特";
            case 270000100: return "时间神殿";
            case 200000000: return "天空之城";
            default:        return "未知区域(" + mapid + ")";
        }
    }
}
