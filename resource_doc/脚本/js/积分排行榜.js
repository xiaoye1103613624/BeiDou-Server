var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = "" + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + "\r\n";
// 数据库表名（需要根据实际情况调整）
var CHARACTERS_TABLE = "characters"; // 存储玩家信息的表名

// 格式化字符串以对齐列
function pad(text, length) {
    var formattedText = text;
    while (formattedText.length < length) {
        formattedText += " ";
    }
    return formattedText;
}

function start() {
    status = -1;
    action(1, 0, 0); // 直接调用 action 函数进入排行榜显示
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else if (mode == 0) {
        cm.sendOk("操作已取消。");
        cm.dispose();
        return;
    } else {
        cm.dispose();
        return;
    }

    if (status == 0) {
        // 直接显示排行榜
        showRanking();
    }
}

function showRanking() {
    var rankingData = getRankingData();
    if (rankingData.length > 0) {
        // 过滤掉GM角色
        var filteredRankingData = rankingData.filter(function(player) {
            return !isGM(player.name); // 假设有一个isGM函数来判断是否为GM角色
        });

        if (filteredRankingData.length > 0) {
            var rankingText = "" + 群粉心 + "#e#r\t\t\t#i1802533#全服积分排行榜#i1802533##n\r\n";
            rankingText += "#k------------------------------------------------------\r\n";
            rankingText += "#r#e" + 粉心 + "排名" + 粉心 + "       " + 粉心 + "积分点数" + 粉心 + "      " + 粉心 + "玩家名称" + 粉心 + " #k#n\r\n";
            rankingText += "#k------------------------------------------------------\r\n";
            for (var i = 0; i < filteredRankingData.length; i++) {
                var playerName = pad(filteredRankingData[i].name, 7); // 格式化玩家名称，固定宽度为7
                var ps = pad(filteredRankingData[i].ps.toString(), 10); // 格式化积分点数，固定宽度为10
                var rankNumber = pad((i + 1).toString(), 2); // 格式化排名，固定宽度为2
                var rankColor = i < 3 ? "#r" : "#k"; // 前三名用红色字体，其他用默认字体
                rankingText += rankColor + "   " + rankNumber + "             " + ps + "         " + playerName + "  \r\n";
            }
            cm.sendOk(rankingText);
        } else {
            cm.sendOk("排行榜为空，暂无玩家数据。");
        }
    } else {
        cm.sendOk("排行榜为空，暂无玩家数据。");
    }
    cm.dispose();
}

// 假设的isGM函数，用于判断是否为GM角色
function isGM(playerName) {
    var conn = cm.getConnection();
    var sql = "SELECT gm FROM characters WHERE name = ?";
    var ps = conn.prepareStatement(sql);
    ps.setString(1, playerName);
    var rs = ps.executeQuery();
    var isGM = false;
    if (rs.next()) {
        isGM = rs.getInt("gm") > 0;
    }
    rs.close();
    ps.close();
    conn.close();
    return isGM;
}

// 从数据库获取全服积分排行榜数据
function getRankingData() {
    var conn = getConnection();
    var sql = "SELECT name, ps FROM " + CHARACTERS_TABLE + " WHERE gm = 0 ORDER BY ps DESC LIMIT 20;";
    var pstmt = conn.prepareStatement(sql);
    var result = pstmt.executeQuery();

    var rankingData = [];
    while (result.next()) {
        var playerData = {
            name: result.getString("name"),
            ps: result.getInt("ps")
        };
        rankingData.push(playerData);
    }

    result.close();
    pstmt.close();
    conn.close();

    return rankingData;
}

// 获取数据库连接
function getConnection() {
    return cm.getConnection();
}