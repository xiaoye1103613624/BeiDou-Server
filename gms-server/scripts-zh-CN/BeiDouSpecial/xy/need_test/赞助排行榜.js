var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = "" + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + 粉心 + "\r\n";
// 数据库表名（需要根据实际情况调整）
var CHARACTERS_TABLE = "characters"; // 存储玩家信息的表名
var ACCOUNTS_TABLE = "accounts"; // 存储账号信息的表名

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
        var rankingText = "" + 群粉心 + "#e#r\t\t\t#i1802533#全服赞助排行榜#i1802533##n\r\n";
        rankingText += "#k------------------------------------------------------\r\n";
        rankingText += "#r#e" + 粉心 + "排名" + 粉心 + "  " + 粉心 + "账号ID" + 粉心 + "   " + 粉心 + "赞助数量" + 粉心 + "  " + 粉心 + "玩家名称" + 粉心 + "#k#n\r\n";
        rankingText += "#k------------------------------------------------------\r\n";
        for (var i = 0; i < rankingData.length; i++) {
            var playerName = pad(rankingData[i].name, 7); // 格式化玩家名称，固定宽度为7
            var money = pad(rankingData[i].money.toString(), 6); // 格式化赞助数量，固定宽度为10
            var accountID = pad(rankingData[i].account, 3); // 格式化账号ID，固定宽度为10
            var rankNumber = pad((i + 1).toString(), 2); // 格式化排名，固定宽度为2
            var rankColor = i < 3 ? "#r" : "#k"; // 前三名用红色字体，其他用默认字体
            rankingText += rankColor + "  " + rankNumber + "         " + accountID + "           " + money + "       " + playerName + "\r\n";
			
        }
        cm.sendOk(rankingText);
    } else {
        cm.sendOk("排行榜为空，暂无玩家数据。");
    }
    cm.dispose();
}

// 从数据库获取全服赞助排行榜数据
function getRankingData() {
    var conn = getConnection();
	var sql = "SELECT a.id AS account, c.name, a.money AS money FROM accounts a LEFT JOIN characters c ON a.id = c.accountid WHERE c.gm = 0 GROUP BY a.id ORDER BY money DESC LIMIT 20";  // 显示前20名
    var pstmt = conn.prepareStatement(sql);
    var result = pstmt.executeQuery();

    var rankingData = [];
    while (result.next()) {
        var playerData = {
            account: result.getString("account"), // 添加账号ID字段
            name: result.getString("name"),
            money: result.getInt("money")
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