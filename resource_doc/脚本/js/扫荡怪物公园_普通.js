/* ========= NPC 9900001 扫荡脚本（对齐怪物公园次数） ========= */
var status = 0;
var logKey = "怪物公园_普通";   // 与副本脚本完全一致
var dailyMax = 2;

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
        /* 1. 查今日已用次数（使用副本脚本的读取函数） */
        var used = getacanwnjzd(logKey, 1, cm.getPlayer().getId());
        var left = dailyMax - used;
        if (left <= 0) {
            cm.sendOk("今日「怪物公园_普通」奖励已领取完毕（每日 2 次），请明日再来！");
            cm.dispose();
            return;
        }

        /* 2. 计算奖励（保持不变） */
        var fame = cm.getPlayer().getFame();
        fame = Math.min(fame, 2000);
        var rewardCount  = Math.floor(fame / 3);
        var rewardCount1 = Math.floor(fame / 5);

        var msg = "";
        if (rewardCount > 0) {
            cm.gainItem(4310020, rewardCount);
            cm.gainItem(3605006, rewardCount1);
            msg += "#r扫荡奖励：" + rewardCount + "个怪物公园纪念币、"
                 + rewardCount1 + "个女神的赐福！\r\n\r\n"
                 + "#b每3人气值增加1个怪物公园纪念币；\r\n每5人气值增加1个女神的赐福！";
        } else {
            cm.gainItem(4310020, 1);
            msg = "你的人气值不足3点，给予1个怪物公园纪念币作为安慰奖。";
        }
		cm.喇叭(3, "恭喜[" + cm.getName() + "]一键扫荡完成了【" + logKey + "】，直接获得全部奖励！");
        /* 3. 记录一次扫荡（使用副本脚本的写入函数） */
        gainxmwnjlzd(logKey, 1, cm.getPlayer().getId());

        cm.sendNext(msg + "\r\n\r\n#b今日剩余次数： " + (left - 1) + " / " + dailyMax);
        status = 1;
    } else if (status === 1) {
        cm.dispose();
    }
}

/* ========= 以下函数从副本脚本中复制过来，保持一致 ========= */
function getacanwnjzd(jiluid, type, accid) {
    var xmsjfh = 0;
    var conn = Packages.database.DBConPool.getInstance().getDataSource().getConnection();
    var sql = "SELECT * FROM xmwnjl WHERE characterid = " + accid + " AND bossid = '" + jiluid + "';";
    var pstmt = conn.prepareStatement(sql);
    var result = pstmt.executeQuery();
    if (result.next()) {
        xmsjfh = result.getInt("count");
        var kzhq_time = result.getTimestamp("time");
        var kzhq_month = kzhq_time.getMonth() + 1;
        var kzhq_date = kzhq_time.getDate();
        if (type == 1) {
            var now = new Date();
            var month = now.getMonth() + 1;
            var date = now.getDate();
            if (month != kzhq_month || date >= (kzhq_date + 1) || date < kzhq_date) {
                xmsjfh = 0;
                var sql2 = "UPDATE xmwnjl SET count = 0 WHERE characterid = " + accid + " AND bossid = '" + jiluid + "';";
                var pstmt2 = conn.prepareStatement(sql2);
                pstmt2.executeUpdate();
                pstmt2.close();
            }
        }
    }
    result.close();
    pstmt.close();
    conn.close();
    return xmsjfh;
}

function gainxmwnjlzd(wnjllog, cs, accid) {
    var conn = Packages.database.DBConPool.getInstance().getDataSource().getConnection();
    var sql = "SELECT * FROM xmwnjl WHERE bossid = '" + wnjllog + "' AND characterid = " + accid + ";";
    var pstmt = conn.prepareStatement(sql);
    var result = pstmt.executeQuery();
    if (result.next()) {
        result.close();
        var sql2 = "UPDATE xmwnjl SET count = count + " + cs + " WHERE bossid = '" + wnjllog + "' AND characterid = " + accid + ";";
        var pstmt2 = conn.prepareStatement(sql2);
        pstmt2.executeUpdate();
        pstmt2.close();
    } else {
        var sql3 = "INSERT INTO xmwnjl (time, bossid, count, characterid) VALUES (CURRENT_TIMESTAMP(), ?, ?, ?);";
        var psu = conn.prepareStatement(sql3);
        psu.setString(1, wnjllog);
        psu.setInt(2, cs);
        psu.setInt(3, accid);
        psu.executeUpdate();
        psu.close();
    }
    conn.close();
}