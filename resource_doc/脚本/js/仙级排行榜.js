var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = "" + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + "\r\n";
// 数据库表名（需要根据实际情况调整）
var RANKING_TABLE = "xmwnjl"; // 存储玩家仙级信息的表名
var CHARACTERS_TABLE = "characters"; // 存储玩家信息的表名

// 仙级等级数字到汉字的映射
var levelToChinese = {
    1: "筑基",
    2: "金丹",
    3: "元婴",
    4: "出窍",
    5: "分神",
    6: "合体",
    7: "渡劫",
    8: "大乘",
    9: "天仙",
    10: "仙君",
    11: "玄仙",
    12: "仙帝",
    13: "神人",
    14: "神将",
    15: "神君",
    16: "神帝",
    17: "神皇",
    18: "神尊",
    19: "圣人",
    20: "至尊",
    21: "主宰",
	22: "永恒",
	23: "创世",
	24: "超脱"
	
};

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
            var rankingText = "" + 群粉心 + "#e#r\t\t\t#i1802533#全服仙级排行榜#i1802533##n\r\n";
            rankingText += "#k------------------------------------------------------\r\n";
            rankingText += "#r#e" + 粉心 + "排名" + 粉心 + "   " + 粉心 + "仙级" + 粉心 + "   " + 粉心 + "当前层数" + 粉心 + "  " + 粉心 + "玩家名称" + 粉心 + " #k#n\r\n";
            rankingText += "#k------------------------------------------------------\r\n";
            for (var i = 0; i < filteredRankingData.length; i++) {
                var chineseLevel = levelToChinese[filteredRankingData[i].level] || "未知仙级";
                var playerName = pad(filteredRankingData[i].name, 7); // 格式化玩家名称，固定宽度为7
                var currentLevel = pad(filteredRankingData[i].currentLevel.toString(), 5); // 格式化当前层数，固定宽度为5
                var formattedLevel = pad(chineseLevel, 4); // 格式化仙级，固定宽度为4
                var rankNumber = pad((i + 1).toString(), 2); // 格式化排名，固定宽度为2
                var rankColor = i < 3 ? "#r" : "#k"; // 前三名用红色字体，其他用默认字体
                rankingText += rankColor + "   " + rankNumber + "        " + formattedLevel + "         " + currentLevel + "     " + playerName + "  \r\n";
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

// 从数据库获取全服仙级排行榜数据
function getRankingData() {
    var conn = getConnection();
    var sql = "SELECT c.name, x.count AS level, (SELECT SUM(y.count) FROM xmwnjl y WHERE y.characterid = x.characterid AND y.bossid = 'XM飞升系统_当前层') AS currentLevel FROM " + RANKING_TABLE + " x INNER JOIN " + CHARACTERS_TABLE + " c ON x.characterid = c.id WHERE x.bossid = 'XM飞升系统_仙级' ORDER BY level DESC, currentLevel DESC LIMIT 11;";
    var pstmt = conn.prepareStatement(sql);
    var result = pstmt.executeQuery();

    var rankingData = [];
    while (result.next()) {
        var playerData = {
            name: result.getString("name"),
            level: result.getInt("level"),
            currentLevel: result.getInt("currentLevel")
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