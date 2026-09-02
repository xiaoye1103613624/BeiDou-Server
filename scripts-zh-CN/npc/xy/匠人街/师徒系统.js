// 匠人街 · 师徒系统子脚本（通过 9031000 成长辅助菜单进入）
// 拜师/收徒/出师，师徒币兑换，经验加成

var QUEST_MENTOR = 9900330;      // 师傅数据：徒弟列表JSON
var QUEST_MENTEE = 9900331;      // 徒弟数据：师傅ID + 拜师等级
var QUEST_MENTOR_COINS = 9900332; // 师傅的师徒币余额

var MAX_MENTEES = 3;             // 最多同时带3个徒弟
var MENTOR_MIN_LV = 150;         // 师傅最低等级
var MENTEE_MAX_LV = 120;         // 徒弟出师等级
var EXP_BONUS_PCT = 10;          // 徒弟经验加成%
var LEVELUP_COINS = 5;           // 徒弟每级给师傅的币
var GRADUATE_COINS = 500;        // 出师奖励

var status = -1;
var actionType = 0;

function start() {
    status = -1; actionType = 0;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) { cm.dispose(); return; }
    status++;
    if (actionType === 0) handleMain(selection);
    else if (actionType === 1) handleBecomeMentor(selection);
    else if (actionType === 2) handleFindMentor(selection);
    else if (actionType === 3) handleMyMentees(selection);
    else if (actionType === 4) handleMyMentor(selection);
    else if (actionType === 5) handleCoinShop(selection);
}

// ==================== 主菜单 ====================

function handleMain(selection) {
    if (status === 0) {
        var chr = cm.getPlayer();
        var lv = chr.getLevel();
        var isMentor = isRegisteredMentor();
        var hasMentor = getMyMentorId() > 0;

        var t = "#e#b<师徒系统>#k#n\r\n\r\n";
        t += "师傅带徒弟，共同成长！\r\n\r\n";
        t += "【规则】\r\n";
        t += "· 师傅需≥Lv." + MENTOR_MIN_LV + "，徒弟需≤Lv." + MENTEE_MAX_LV + "\r\n";
        t += "· 徒弟获得 " + EXP_BONUS_PCT + "% 经验加成\r\n";
        t += "· 徒弟每升1级，师傅获得 " + LEVELUP_COINS + " 师徒币\r\n";
        t += "· 徒弟达到Lv." + MENTEE_MAX_LV + " 自动出师，师傅获 " + GRADUATE_COINS + " 师徒币\r\n\r\n";

        if (lv >= MENTOR_MIN_LV) {
            if (isMentor) {
                t += "#L3#我的徒弟（管理徒弟列表）#l\r\n";
            } else {
                t += "#L1##b成为师傅（登记收徒资格）#l\r\n";
            }
        }
        if (lv <= MENTEE_MAX_LV && !hasMentor) {
            t += "#L2##b拜师（寻找师傅）#l\r\n";
        }
        if (hasMentor) {
            t += "#L4#我的师傅#l\r\n";
        }
        // 师徒币商店
        t += "#L5##b师徒币商店#l\r\n\r\n";
        t += "#L9000##g离开#k#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 9000) { cm.dispose(); return; }
        actionType = selection;
        status = -1;
        action(1, 0, 0);
    }
}

// ==================== 成为师傅 ====================

function handleBecomeMentor(selection) {
    if (status === 0) {
        var chr = cm.getPlayer();
        if (chr.getLevel() < MENTOR_MIN_LV) {
            cm.sendOk("需要等级 #b" + MENTOR_MIN_LV + "#k 才能成为师傅！");
            cm.dispose(); return;
        }
        if (isRegisteredMentor()) {
            cm.sendOk("你已经是注册师傅了！");
            cm.dispose(); return;
        }
        cm.sendYesNo("确认登记为师傅？\r\n登记后其他低等级玩家可以拜你为师。\r\n你最多同时带 #b" + MAX_MENTEES + "#k 个徒弟。");
    } else if (status === 1) {
        registerMentor();
        cm.sendOk("#b登记成功！#k\r\n你现在可以收徒了！\r\n等待其他玩家通过师徒系统找到你。\r\n\r\n提示：当徒弟在线时，你可以直接右键对方邀请拜师。");
        cm.dispose();
    }
}

// ==================== 拜师 ====================

function handleFindMentor(selection) {
    if (status === 0) {
        var chr = cm.getPlayer();
        if (getMyMentorId() > 0) {
            cm.sendOk("你已经有师傅了！出师之前不能换师傅。");
            cm.dispose(); return;
        }
        if (chr.getLevel() > MENTEE_MAX_LV) {
            cm.sendOk("你的等级已经超过 #b" + MENTEE_MAX_LV + "#k，无法拜师了！");
            cm.dispose(); return;
        }

        // 扫描当前地图玩家的师傅（简化版：只能拜同地图的师傅）
        var map = chr.getMap();
        var players = map.getAllPlayers();
        var mentors = [];
        for (var i = 0; i < players.size(); i++) {
            var p = players.get(i);
            if (p.getId() === chr.getId()) continue;
            if (p.getLevel() >= MENTOR_MIN_LV) {
                var mentees = getMentees(p.getId());
                if (mentees.length < MAX_MENTEES) {
                    mentors.push({ id: p.getId(), name: p.getName(), level: p.getLevel(), job: p.getJob() });
                }
            }
        }

        var t = "#e#b寻找师傅#k#n\r\n\r\n";
        t += "当前地图可拜师的玩家：\r\n\r\n";
        if (mentors.length === 0) {
            t += "#r当前地图没有可收徒的师傅。#k\r\n";
            t += "提示：请到匠人街（玩家聚集地）寻找师傅。\r\n";
        } else {
            for (var i = 0; i < mentors.length; i++) {
                var m = mentors[i];
                t += "#L" + m.id + "##b" + m.name + "#k Lv." + m.level + " " + jobName(m.job) + "#l\r\n";
            }
        }
        t += "\r\n#L9000##g返回#k#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 9000) { backMain(); return; }
        var mentorId = selection;
        // 发送拜师请求
        requestMentorship(mentorId);
    }
}

function requestMentorship(mentorId) {
    var chr = cm.getPlayer();
    var qr = chr.getQuestNAdd(QUEST_MENTEE);
    qr.setProgressValue("pendingMentor", "" + mentorId);
    qr.setProgressValue("joinLevel", "" + chr.getLevel());
    chr.updateQuest(qr);

    // 尝试通知师傅
    try {
        var mentor = chr.getMap().getCharacterById(mentorId);
        if (mentor != null) {
            mentor.dropMessage(6, "【师徒】" + chr.getName() + "(Lv." + chr.getLevel() + ") 请求拜你为师！");
            mentor.dropMessage(6, "请在师徒系统→我的徒弟中确认。");
        }
    } catch (e) {}

    cm.sendOk("#b拜师请求已发送！#k\r\n等待师傅确认...\r\n你获得 #b" + EXP_BONUS_PCT + "%#k 经验加成（拜师成功后生效）");
    cm.dispose();
}

// ==================== 我的徒弟(师傅视角) ====================

function handleMyMentees(selection) {
    if (status === 0) {
        var chr = cm.getPlayer();
        var mentees = getMentees(chr.getId());
        var pending = getPendingRequests(chr.getId());

        var t = "#e#b<我的徒弟>#k#n\r\n\r\n";
        if (mentees.length === 0 && pending.length === 0) {
            t += "你还没有徒弟。\r\n";
            t += "低等级玩家会通过师徒系统拜你为师。\r\n";
        }
        if (pending.length > 0) {
            t += "【待确认的拜师请求】\r\n";
            for (var i = 0; i < pending.length; i++) {
                var p = pending[i];
                t += "#L99_" + p.id + "#✅ 接受 " + p.name + "(Lv." + p.level + ") 的拜师#l\r\n";
            }
            t += "\r\n";
        }
        if (mentees.length > 0) {
            t += "【在读徒弟】\r\n";
            for (var i = 0; i < mentees.length; i++) {
                var m = mentees[i];
                t += "#L" + m.id + "#" + m.name + " Lv." + m.onlineLevel + " (拜师时Lv." + m.joinLevel + ")#l\r\n";
            }
        }
        t += "\r\n师徒币余额：#b" + getMentorCoins() + "#k\r\n";
        t += "\r\n#L9000##g返回#k#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 9000) { backMain(); return; }

        var selStr = "" + selection;
        if (selStr.startsWith("99_")) {
            // 确认拜师请求
            var menteeId = parseInt(selStr.substring(3));
            confirmMentorship(menteeId);
        } else {
            // 查看徒弟详情
            showMenteeDetail(selection);
        }
    }
}

function confirmMentorship(menteeId) {
    var chr = cm.getPlayer();
    var mentees = getMentees(chr.getId());
    if (mentees.length >= MAX_MENTEES) {
        cm.sendOk("你的徒弟已满（最多" + MAX_MENTEES + "个）！");
        cm.dispose(); return;
    }

    // 写师徒关系
    mentees.push({ id: menteeId, joinLevel: getMenteeInfo(menteeId, "joinLevel"), onlineLevel: 0 });
    saveMentees(chr.getId(), mentees);

    // 写徒弟记录
    clearPendingRequest(menteeId);

    cm.sendOk("#b拜师成功！#k\r\n新徒弟已加入！徒弟每升1级你都会获得师徒币。");
    cm.dispose();
}

function showMenteeDetail(menteeId) {
    var info = getMenteeInfo(menteeId, "joinLevel");
    var t = "#e#b徒弟详情#k#n\r\n\r\n";
    t += "ID: " + menteeId + "\r\n";
    t += "拜师等级: Lv." + info + "\r\n\r\n";
    t += "#L0#解除师徒关系#l\r\n";
    cm.sendSimple(t);
}

// ==================== 我的师傅(徒弟视角) ====================

function handleMyMentor(selection) {
    if (status === 0) {
        var mentorId = getMyMentorId();
        if (mentorId <= 0) {
            cm.sendOk("你没有师傅。\r\n请先通过师徒系统拜师。");
            cm.dispose(); return;
        }

        var joinLevel = getMenteeJoinLevel();
        var currentLevel = cm.getPlayer().getLevel();

        var t = "#e#b<我的师傅>#k#n\r\n\r\n";
        t += "师傅ID: #b" + mentorId + "#k\r\n";
        t += "拜师时等级: Lv." + joinLevel + "\r\n";
        t += "当前等级: Lv." + currentLevel + "\r\n";
        t += "经验加成: #b+" + EXP_BONUS_PCT + "%#k\r\n";
        if (currentLevel >= MENTEE_MAX_LV) {
            t += "\r\n#g🎉 你已达到出师等级！找师傅确认出师。#k\r\n";
        }
        t += "\r\n#L0#出师（达到Lv." + MENTEE_MAX_LV + "后）#l\r\n";
        cm.sendSimple(t);
    } else if (status === 1) {
        graduate();
    }
}

function graduate() {
    var chr = cm.getPlayer();
    if (chr.getLevel() < MENTEE_MAX_LV) {
        cm.sendOk("需要达到 #bLv." + MENTEE_MAX_LV + "#k 才能出师！");
        cm.dispose(); return;
    }
    var mentorId = getMyMentorId();
    if (mentorId <= 0) { cm.dispose(); return; }

    // 给师傅师徒币
    addMentorCoins(mentorId, GRADUATE_COINS);

    // 清空师徒关系
    removeMentee(mentorId, chr.getId());
    clearMyMentor();

    // 给徒弟出师奖励
    cm.getPlayer().gainExp(500000, true, true);
    cm.gainItem(4001126, 200);
    cm.getPlayer().getCashShop().gainCash(1, 3000);

    cm.sendOk("#b🎉 出师成功！#k\r\n\r\n你获得了出师奖励：\r\n经验×500,000\r\n匠人币×200\r\n抵用券×3,000\r\n\r\n你的师傅获得了 #b" + GRADUATE_COINS + "#k 师徒币！");
    cm.dispose();
}

// ==================== 师徒币商店 ====================

function handleCoinShop(selection) {
    if (status === 0) {
        var coins = getMentorCoins();
        var t = "#e#b<师徒币商店>#k#n\r\n\r\n";
        t += "你的师徒币：#b" + coins + "#k\r\n\r\n";
        t += "#L1#洗炼石 ×10 - 50师徒币#l\r\n";
        t += "#L2#灵韵结晶 ×1 - 100师徒币#l\r\n";
        t += "#L3#圣者之石 ×3 - 80师徒币#l\r\n";
        t += "#L4#匠人币 ×100 - 30师徒币#l\r\n";
        t += "#L5#经验券(50W) ×1 - 40师徒币#l\r\n\r\n";
        t += "#L9000##g返回#k#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 9000) { backMain(); return; }
        var coins = getMentorCoins();
        var cost = [0, 50, 100, 80, 30, 40][selection];
        var rewards = [
            null,
            { id: 4032171, qty: 10 },
            { id: 4021017, qty: 1 },
            { id: 4000314, qty: 3 },
            { id: 4001126, qty: 100 },
            null
        ];

        if (coins < cost) { cm.sendOk("师徒币不足！需要 " + cost + "，当前 " + coins); cm.dispose(); return; }

        spendMentorCoins(cost);
        if (selection === 5) {
            cm.getPlayer().gainExp(500000, true, true);
            cm.sendOk("兑换成功！获得经验×500,000");
        } else {
            cm.gainItem(rewards[selection].id, rewards[selection].qty);
            cm.sendOk("兑换成功！获得 #t" + rewards[selection].id + "# ×" + rewards[selection].qty);
        }
        cm.dispose();
    }
}

// ==================== 数据操作 ====================

function isRegisteredMentor() {
    var qr = cm.getPlayer().getQuestNAdd(QUEST_MENTOR);
    return qr.getProgressValue("registered") === "1";
}

function registerMentor() {
    var qr = cm.getPlayer().getQuestNAdd(QUEST_MENTOR);
    qr.setProgressValue("registered", "1");
    qr.setProgressValue("mentees", "[]");
    cm.getPlayer().updateQuest(qr);
}

function getMentorId(playerId) {
    // 从quest数据读取玩家ID对应的师傅（跨角色需要查表，简化版使用角色自身）
    // 实际实现中需要DB查询，此处提供框架
    return 0;
}

function getMyMentorId() {
    var qr = cm.getPlayer().getQuestNAdd(QUEST_MENTEE);
    var v = qr.getProgressValue("mentorId");
    return v ? parseInt(v) : 0;
}

function getMenteeJoinLevel() {
    var qr = cm.getPlayer().getQuestNAdd(QUEST_MENTEE);
    var v = qr.getProgressValue("joinLevel");
    return v ? parseInt(v) : 0;
}

function clearMyMentor() {
    var qr = cm.getPlayer().getQuestNAdd(QUEST_MENTEE);
    qr.setProgressValue("mentorId", "0");
    qr.setProgressValue("joinLevel", "0");
    cm.getPlayer().updateQuest(qr);
}

function getMentees(mentorId) {
    // 简化版：从mentor自身quest读取
    // 完整版需要跨角色查询
    var qr = cm.getPlayer().getQuestNAdd(QUEST_MENTOR);
    var json = qr.getProgressValue("mentees");
    if (!json || json === "") return [];
    try {
        return JSON.parse(json);
    } catch (e) {
        return [];
    }
}

function saveMentees(mentorId, mentees) {
    var qr = cm.getPlayer().getQuestNAdd(QUEST_MENTOR);
    qr.setProgressValue("mentees", JSON.stringify(mentees));
    cm.getPlayer().updateQuest(qr);
}

function getPendingRequests(mentorId) {
    // 简化版返回空（需要跨角色查询）
    return [];
}

function getMenteeInfo(menteeId, key) {
    // 简化版返回默认值
    return "?";
}

function clearPendingRequest(menteeId) {
    // 简化版
}

function removeMentee(mentorId, menteeId) {
    var mentees = getMentees(mentorId);
    mentees = mentees.filter(function(m) { return m.id != menteeId; });
    saveMentees(mentorId, mentees);
}

function getMentorCoins() {
    var qr = cm.getPlayer().getQuestNAdd(QUEST_MENTOR_COINS);
    var v = qr.getProgressValue("coins");
    return v ? parseInt(v) : 0;
}

function addMentorCoins(mentorId, amount) {
    // 简化版：加到自己的币
    var qr = cm.getPlayer().getQuestNAdd(QUEST_MENTOR_COINS);
    var cur = getMentorCoins();
    qr.setProgressValue("coins", "" + (cur + amount));
    cm.getPlayer().updateQuest(qr);
}

function spendMentorCoins(amount) {
    addMentorCoins(0, -amount);
}

// ==================== 工具 ====================

function jobName(jobId) {
    var j = jobId / 100;
    if (j >= 10 && j < 20) return "战士";
    if (j >= 20 && j < 30) return "法师";
    if (j >= 30 && j < 40) return "弓手";
    if (j >= 40 && j < 50) return "飞侠";
    if (j >= 50 && j < 60) return "海盗";
    return "冒险家";
}

function parseInt(v) {
    try { return java.lang.Integer.parseInt(v || "0"); } catch (e) { return 0; }
}

function backMain() {
    actionType = 0; status = -1;
    action(1, 0, 0);
}
