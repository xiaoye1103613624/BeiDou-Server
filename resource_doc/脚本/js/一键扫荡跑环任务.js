/* ========= NPC 9900002 脚本（每日 1 次，直接发奖） ========= */
var status = 0;
var logKey  = "一键扫荡跑环"; // 必须与跑环脚本一致
var dailyMax = 1;

function start() {
    status = 0;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode !== 1) {
        cm.dispose();
        return;
    }

    if (status === 0) {
        // 使用跑环脚本的函数读取今日次数
        var used = getBossLog1(logKey);
        var left = dailyMax - used;

        if (left <= 0) {
            cm.sendOk("今日「跑环30次」奖励已领取完毕（每日 1 次），请明日再来！");
            cm.dispose();
            return;
        }

        /* ===== 直接发奖 ===== */
        cm.gainItem(3994789, 50);
        cm.sendOk("领取成功！\r\n获得：#i3994789# 50 张，\r\n\r\n#b今日剩余次数： " + (left - 1) + " / " + dailyMax);
		cm.喇叭(3, "" + cm.getName() + ":一键扫荡完成全部「跑环任务」，获得大量奖励！");

        /* ===== 记录次数（使用跑环脚本的函数） ===== */
        if (noData(logKey)) {
            setBossLog1(logKey);        // 首次写入
        } else {
            updateBossLog1(logKey);     // 累加次数
        }

        cm.dispose();
    }
}

/* ========= 以下函数直接复制自跑环脚本 9900001 ========= */
function getBossLog1(log) {
    var id = cm.getPlayer().getId();
    var conn = Packages.database.DBConPool.getInstance().getDataSource().getConnection();
    var ps = conn.prepareStatement(
        "SELECT count FROM bosslog1 WHERE characterid = ? AND bossid = ? AND time = CURDATE()"
    );
    ps.setInt(1, id);
    ps.setString(2, log);
    var rs = ps.executeQuery();
    var count = 0;
    if (rs.next()) count = rs.getInt("count");
    rs.close();
    ps.close();
    conn.close();
    return count;
}

function setBossLog1(log) {
    var id = cm.getPlayer().getId();
    var con = Packages.database.DBConPool.getInstance().getDataSource().getConnection();
    var ps = con.prepareStatement(
        "INSERT INTO bosslog1 (characterid, bossid, count, time) VALUES (?,?,1,CURDATE())"
    );
    ps.setInt(1, id);
    ps.setString(2, log);
    ps.executeUpdate();
    ps.close();
}

function updateBossLog1(log) {
    var id = cm.getPlayer().getId();
    var con = Packages.database.DBConPool.getInstance().getDataSource().getConnection();
    var ps = con.prepareStatement(
        "UPDATE bosslog1 SET count = count + 1 WHERE characterid = ? AND bossid = ? AND time = CURDATE()"
    );
    ps.setInt(1, id);
    ps.setString(2, log);
    ps.executeUpdate();
    ps.close();
}

function noData(log) {
    var id = cm.getPlayer().getId();
    var con = Packages.database.DBConPool.getInstance().getDataSource().getConnection();
    var ps = con.prepareStatement(
        "SELECT * FROM bosslog1 WHERE characterid = ? AND bossid = ? AND time = CURDATE()"
    );
    ps.setInt(1, id);
    ps.setString(2, log);
    var rs = ps.executeQuery();
    var empty = !rs.next();
    rs.close();
    ps.close();
    return empty;
}