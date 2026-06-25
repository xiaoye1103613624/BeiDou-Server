/**
 * @author: 萧曵
 * @npc: 装备战力排行榜
 * @description:
 *   由 9031003.js 通过 cm.openNpc(9031003, "装备战力排行榜") 跳转进入。
 *   展示 equip_power_ranking 表中的前10名(每小时由后台定时任务 org.gms.server.EquipPowerRankingManager
 *   重新统计全服角色当前穿戴装备的"装备战力"并覆盖写入该表，脚本本身只负责读取展示，不做计算)。
 *   展示顺序与截图一致：上衣/裤裙/鞋子/帽子/脸饰/眼饰/耳饰/项链/披风/手套。
 */

var DatabaseConnection = Java.type('database.DatabaseConnection');

function start() {
    var add = "#e#b装备战力排行榜#k#n\r\n更新战斗力会自动更新装备排行榜哦~\r\n\r\n";
    add += 查询排行榜文本();
    cm.sendSimple(add);
}

function action(mode, type, selection) {
    cm.dispose();
}

function 查询排行榜文本() {
    var text = "";
    var con;
    var ps;
    var rs;
    try {
        con = DatabaseConnection.getConnection();
        ps = con.prepareStatement("SELECT rank_no, character_name, power, hat_item, face_item, eye_item, earring_item, " +
            "top_item, pants_item, shoes_item, gloves_item, cape_item, pendant_item " +
            "FROM equip_power_ranking ORDER BY rank_no ASC LIMIT 10");
        rs = ps.executeQuery();
        var hasRow = false;
        while (rs.next()) {
            hasRow = true;
            text += "#b第" + rs.getInt("rank_no") + "名#k " + rs.getString("character_name");
            text += "  装备战力：#r" + rs.getLong("power") + "#k\r\n";
            text += 拼装备图标(rs.getInt("top_item"));
            text += 拼装备图标(rs.getInt("pants_item"));
            text += 拼装备图标(rs.getInt("shoes_item"));
            text += 拼装备图标(rs.getInt("hat_item"));
            text += 拼装备图标(rs.getInt("face_item"));
            text += 拼装备图标(rs.getInt("eye_item"));
            text += 拼装备图标(rs.getInt("earring_item"));
            text += 拼装备图标(rs.getInt("pendant_item"));
            text += 拼装备图标(rs.getInt("cape_item"));
            text += 拼装备图标(rs.getInt("gloves_item"));
            text += "\r\n\r\n";
        }
        if (!hasRow) {
            text = "暂无排行数据，请等待下一次刷新(每小时刷新一次)。";
        }
    } catch (e) {
        text = "排行榜数据读取失败：" + e;
    } finally {
        if (rs != null) { try { rs.close(); } catch (e2) {} }
        if (ps != null) { try { ps.close(); } catch (e2) {} }
    }
    return text;
}

function 拼装备图标(itemId) {
    if (itemId == null || itemId <= 0) {
        return "";
    }
    return "#i" + itemId + "#";
}
