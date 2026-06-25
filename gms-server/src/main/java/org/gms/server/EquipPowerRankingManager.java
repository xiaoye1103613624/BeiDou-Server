package org.gms.server;

import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.HOURS;

/**
 * 装备战力排行榜
 * 后台每小时重新统计一次全服(含离线)角色当前穿戴装备的"装备战力"，取前10名写入 equip_power_ranking 表。
 * 战力计算口径(按职业区分)：
 *   物理职业：四维(str+dex+int+luk)*1 + 装备攻击力(watk)*10 = 战力值
 *   法系职业：四维(str+dex+int+luk)*1 + 装备魔法力(matk)*4  = 战力值
 * 职业分支(niche)取 characters.job 的 (job/100)%10(1=战士 2=法师 3=弓手 4=飞侍 5=海盗)，
 * niche=2(法师系，含暗夜法师/逆叶/夜光等)按法系公式计算，其余职业按物理公式计算。
 * 仅统计 inventoryitems.inventorytype=1(装备) 且 position&lt;0(穿戴中) 的物品。
 */
public class EquipPowerRankingManager {

    private static final Logger log = LoggerFactory.getLogger(EquipPowerRankingManager.class);

    /** 排行榜重新计算的时间间隔(小时) */
    private static final int RANKING_REFRESH_INTERVAL_HOURS = 1;

    /** 排行榜保留名次数量 */
    private static final int RANKING_TOP_N = 10;

    private static final ScheduledExecutorService schdExctr = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r);
        t.setPriority(Thread.MIN_PRIORITY);
        t.setName("EquipPowerRankingManager");
        return t;
    });

    /** 单个角色的装备战力统计结果(含截图所需的10个展示槽位装备ID) */
    private record EquipPowerRow(int characterId, String characterName, long power,
                                  int hat, int face, int eye, int earring, int top,
                                  int pants, int shoes, int gloves, int cape, int pendant) {
    }

    private static final Runnable refreshRankingRunnable = () -> {
        try {
            List<EquipPowerRow> rows = queryTopRanking();
            saveRanking(rows);
        } catch (Exception e) {
            log.error("装备战力排行榜刷新失败", e);
        }
    };

    /**
     * 汇总所有角色当前穿戴装备的战力，取前 RANKING_TOP_N 名
     * 聚合查询带条件MAX(CASE)，通过 MyBatis-Flex 的 Db 工具类执行原生SQL(走统一数据源，非手动JDBC连接)
     */
    private static List<EquipPowerRow> queryTopRanking() {
        // 槽位编号参见 org.gms.constants.inventory.EquipSlot：
        // 帽子-1 脸饰-2 眼饰-3 耳饰-4 上衣-5 裤裙-6 鞋子-7 手套-8 披风-9 项链-17
        String sql = "SELECT ii.characterid AS charid, c.name AS name, " +
                "SUM(ie.str + ie.dex + ie.`int` + ie.luk) " +
                "  + (CASE WHEN (c.job DIV 100) % 10 = 2 THEN SUM(ie.matk) * 4 ELSE SUM(ie.watk) * 10 END) AS power, " +
                "MAX(CASE WHEN ii.position = -1 THEN ii.itemid ELSE 0 END) AS hat_item, " +
                "MAX(CASE WHEN ii.position = -2 THEN ii.itemid ELSE 0 END) AS face_item, " +
                "MAX(CASE WHEN ii.position = -3 THEN ii.itemid ELSE 0 END) AS eye_item, " +
                "MAX(CASE WHEN ii.position = -4 THEN ii.itemid ELSE 0 END) AS earring_item, " +
                "MAX(CASE WHEN ii.position = -5 THEN ii.itemid ELSE 0 END) AS top_item, " +
                "MAX(CASE WHEN ii.position = -6 THEN ii.itemid ELSE 0 END) AS pants_item, " +
                "MAX(CASE WHEN ii.position = -7 THEN ii.itemid ELSE 0 END) AS shoes_item, " +
                "MAX(CASE WHEN ii.position = -8 THEN ii.itemid ELSE 0 END) AS gloves_item, " +
                "MAX(CASE WHEN ii.position = -9 THEN ii.itemid ELSE 0 END) AS cape_item, " +
                "MAX(CASE WHEN ii.position = -17 THEN ii.itemid ELSE 0 END) AS pendant_item " +
                "FROM inventoryitems ii " +
                "JOIN inventoryequipment ie ON ie.inventoryitemid = ii.inventoryitemid " +
                "JOIN characters c ON c.id = ii.characterid " +
                "WHERE ii.inventorytype = 1 AND ii.position < 0 " +
                "GROUP BY ii.characterid, c.name, c.job " +
                "ORDER BY power DESC " +
                "LIMIT " + RANKING_TOP_N;

        List<Row> rs = Db.selectListBySql(sql);
        List<EquipPowerRow> rows = new ArrayList<>(rs.size());
        for (Row r : rs) {
            rows.add(new EquipPowerRow(
                    r.getInt("charid"), r.getString("name"), r.getLong("power"),
                    r.getInt("hat_item"), r.getInt("face_item"), r.getInt("eye_item"), r.getInt("earring_item"),
                    r.getInt("top_item"), r.getInt("pants_item"), r.getInt("shoes_item"),
                    r.getInt("gloves_item"), r.getInt("cape_item"), r.getInt("pendant_item")
            ));
        }
        return rows;
    }

    /**
     * 用本次计算结果整体覆盖 equip_power_ranking 表
     */
    private static void saveRanking(List<EquipPowerRow> rows) {
        Db.deleteBySql("DELETE FROM equip_power_ranking");
        String insertSql = "INSERT INTO equip_power_ranking " +
                "(rank_no, character_id, character_name, power, hat_item, face_item, eye_item, earring_item, " +
                " top_item, pants_item, shoes_item, gloves_item, cape_item, pendant_item, update_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        int rank = 1;
        for (EquipPowerRow row : rows) {
            Db.insertBySql(insertSql, rank++, row.characterId(), row.characterName(), row.power(),
                    row.hat(), row.face(), row.eye(), row.earring(),
                    row.top(), row.pants(), row.shoes(), row.gloves(), row.cape(), row.pendant());
        }
    }

    /**
     * 启动装备战力排行榜的定时刷新任务(服务器启动时调用一次)
     */
    public static void startRankingRefresh() {
        // 启动时先立即跑一次，之后每隔1小时刷新
        schdExctr.scheduleAtFixedRate(refreshRankingRunnable, 0, RANKING_REFRESH_INTERVAL_HOURS, HOURS);
    }
}
