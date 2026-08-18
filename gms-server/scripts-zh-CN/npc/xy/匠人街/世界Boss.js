// 匠人街 · 世界Boss子脚本（通过 9031000 副本讨伐中心菜单进入）
// 全服定时Boss：扎昆之王，每6小时刷新，贡献排名奖励

var BOSS_MAP = 280030001;      // 世界Boss专用地图（需在Map.wz创建或复用）
var BOSS_MOB_ID = 8800102;     // 混沌扎昆（高HP版本）
var BOSS_NAME = "扎昆之王";
var RESPAWN_HOURS = 6;         // 每6小时刷新

// 用quest记录Boss状态（世界级状态通过serverProperty或BossLog）
var STATUS_QUEST = 9900320;
var KEY_ALIVE = "bossAlive";
var KEY_NEXT_SPAWN = "nextSpawnTime";
var KEY_KILL_COUNT = "totalKills";

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) { cm.dispose(); return; }
    status++;

    if (status === 0) {
        // 检查Boss状态
        var bossStatus = getBossStatus();

        var t = "#e#b<世界Boss · " + BOSS_NAME + ">#k#n\r\n\r\n";
        t += "全服玩家共同讨伐的终极Boss！\r\n\r\n";

        if (bossStatus.alive) {
            t += "#b🟢 Boss存活中！#k\r\n";
            t += "HP：???,???,??? (动态调整)\r\n\r\n";
            t += "#L0##r⚔ 进入讨伐#k#l\r\n";
        } else {
            t += "#r🔴 Boss已被击败#k\r\n";
            t += "下次刷新：#b" + bossStatus.nextSpawnStr + "#k\r\n";
            t += "历史击杀次数：#b" + bossStatus.totalKills + "#k\r\n\r\n";
            t += "#L1#查看击杀排行榜#l\r\n";
        }

        t += "\r\n\r\n#e#b掉落预览#k#n\r\n";
        t += "SS级宝石 · 神秘之影散件 · 创世武器碎片\r\n";
        t += "灵韵结晶 · 圣者之石×10 · 稀有称号\r\n\r\n";
        t += "#L2##b排行榜#k#l\r\n";
        t += "#L9000##g离开#k#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 9000) { cm.dispose(); return; }

        if (selection === 0) {
            enterBoss();
        } else if (selection === 1) {
            showRanking();
        } else if (selection === 2) {
            showRanking();
        }
        cm.dispose();
    }
}

// ==================== Boss状态管理 ====================

function getBossStatus() {
    var chr = cm.getPlayer();
    var qr = chr.getQuestNAdd(STATUS_QUEST);

    var aliveStr = qr.getProgressValue(KEY_ALIVE) || "1";
    var nextStr = qr.getProgressValue(KEY_NEXT_SPAWN) || "0";
    var killsStr = qr.getProgressValue(KEY_KILL_COUNT) || "0";

    var alive = aliveStr === "1";
    var nextSpawn = parseInt(nextStr);
    var totalKills = parseInt(killsStr);

    // 检查是否应该刷新
    var now = java.lang.System.currentTimeMillis();
    if (!alive && now >= nextSpawn && nextSpawn > 0) {
        // Boss应该刷新了
        alive = true;
        qr.setProgressValue(KEY_ALIVE, "1");
        chr.updateQuest(qr);

        // 全服公告
        try {
            var cserv = chr.getClient().getChannelServer();
            cserv.broadcastMessage(
                Java.type("org.gms.net.packet.PacketCreator").serverNotice(
                    6, "[世界Boss] " + BOSS_NAME + " 已刷新！前往匠人街世界Boss入口参与讨伐！"
                )
            );
        } catch (e) {}
    }

    // 格式化下次刷新时间
    var nextSpawnStr = "";
    if (nextSpawn > 0) {
        var diff = nextSpawn - now;
        if (diff > 0) {
            var hours = Math.floor(diff / 3600000);
            var mins = Math.floor((diff % 3600000) / 60000);
            nextSpawnStr = hours + "小时" + mins + "分钟后";
        } else {
            nextSpawnStr = "即将刷新";
        }
    } else {
        nextSpawnStr = "未知";
    }

    return { alive: alive, nextSpawn: nextSpawn, nextSpawnStr: nextSpawnStr, totalKills: totalKills };
}

function parseInt(v) {
    try {
        return java.lang.Integer.parseInt(v || "0");
    } catch (e) {
        return 0;
    }
}

// ==================== 进入讨伐 ====================

function enterBoss() {
    var chr = cm.getPlayer();
    var bossStatus = getBossStatus();

    if (!bossStatus.alive) {
        cm.sendOk("Boss已被击败！\r\n下次刷新：" + bossStatus.nextSpawnStr);
        cm.dispose();
        return;
    }

    // 检查等级
    if (chr.getLevel() < 120) {
        cm.sendOk("需要等级 #b120#k 以上才能进入世界Boss讨伐！");
        cm.dispose();
        return;
    }

    // 检查是否已在讨伐中
    if (chr.getEventInstance() != null) {
        cm.sendOk("你已在其他副本中，请先退出。");
        cm.dispose();
        return;
    }

    // 传送玩家到Boss地图
    cm.warp(BOSS_MAP, 0);
    chr.dropMessage(6, "已进入" + BOSS_NAME + "讨伐！击败Boss后根据贡献排名发放奖励。");
}

// ==================== 排行榜 ====================

function showRanking() {
    var t = "#e#b<世界Boss击杀排行榜>#k#n\r\n\r\n";
    // 读取BossLog（最近10次击杀记录）
    try {
        var BossLogMapper = Java.type("org.gms.dao.mapper.BossLogMapper");
        var QueryWrapper = Java.type("com.mybatisflex.core.query.QueryWrapper");
        var ctx = Java.type("org.gms.GMSApplication").getApplicationContext();
        if (ctx != null) {
            var mapper = ctx.getBean(BossLogMapper.class);
            var logs = mapper.selectListByQuery(
                QueryWrapper.create()
                    .where("bossid = " + BOSS_MOB_ID)
                    .orderBy("bossendtime", false)
                    .limit(10)
            );
            if (logs.isEmpty()) {
                t += "暂无击杀记录。\r\n";
            } else {
                t += "最近击杀记录：\r\n\r\n";
                for (var i = 0; i < logs.size(); i++) {
                    var log = logs.get(i);
                    var date = new java.util.Date(log.getBossendtime());
                    t += "#" + (i + 1) + " " + log.getCharacterid() + " - " + date.toString() + "\r\n";
                }
            }
        }
    } catch (e) {
        t += "排行榜暂不可用。\r\n";
    }
    t += "\r\n全服累计击杀：#b" + getBossStatus().totalKills + "#k 次";
    cm.sendOk(t);
}

// ==================== Boss击败回调（由事件脚本触发） ====================

function onBossKilled() {
    var chr = cm.getPlayer();
    var qr = chr.getQuestNAdd(STATUS_QUEST);

    // 标记Boss已死亡
    var respawnTime = java.lang.System.currentTimeMillis() + (RESPAWN_HOURS * 3600000);
    qr.setProgressValue(KEY_ALIVE, "0");
    qr.setProgressValue(KEY_NEXT_SPAWN, "" + respawnTime);

    // 增加击杀计数
    var kills = parseInt(qr.getProgressValue(KEY_KILL_COUNT) || "0");
    qr.setProgressValue(KEY_KILL_COUNT, "" + (kills + 1));
    chr.updateQuest(qr);

    // 发放参与奖励（根据贡献排名）
    // 在事件脚本实际处理
}
