/**
 * @description 家族系统
 *   1. 未加入家族时可创建家族（1000W金币）或浏览家族列表加入
 *   2. 加入后查看家族成员列表，区分在线/离线
 *   3. 选择在线同地图玩家可查看装备信息
 *   4. 族长可支付点券扩充成员上限（每次+5，最多99人）
 *
 * 入口：NPC 9900001 case 306
 */

var Guild = Java.type("org.gms.net.server.guild.Guild");
var PacketCreator = Java.type("org.gms.util.PacketCreator");
var DatabaseConnection = Java.type("org.gms.util.DatabaseConnection");
var Server = Java.type("org.gms.net.server.Server");
var I18nUtil = Java.type("org.gms.util.I18nUtil");

// ==================== 常量 ====================
var SEL_BACK = 0;
var MAX_CAPACITY = 99;
var EXPAND_SIZE = 5;
var EXPAND_COST_NX = 3000;
var CREATE_COST_MESO = 10000000;     // 1000W金币

var pendingAction = "";

// ==================== 入口 ====================
function start() {
    status = -1;
    pendingAction = "";
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0) {
        pendingAction = "";
        showMainMenu();
    } else if (status == 1) {
        handleMainSelection(selection);
    } else if (status == 2) {
        if (pendingAction == "memberList") {
            handleMemberSelection(selection);
        } else if (pendingAction == "expand") {
            handleExpandConfirm();
        } else if (pendingAction == "createName") {
            handleCreateFinal();
        } else if (pendingAction == "joinList") {
            handleJoinGuild(selection);
        } else {
            cm.dispose();
        }
    } else {
        cm.dispose();
    }
}

// ==================== 主菜单 ====================
function showMainMenu() {
    var guild = cm.getPlayer().getGuild();

    // ===== 未加入家族 =====
    if (guild == null) {
        var text = "\t\t#r#e< 家族系统 >#k#n\r\n\r\n";
        text += "#d你还未加入任何家族。#k\r\n\r\n";
        text += "━━━ 请选择操作 ━━━\r\n\r\n";
        text += "#L1##b创建家族#k（需要 #r" + (CREATE_COST_MESO / 10000) + "W金币#k）#l\r\n";
        text += "#L2##b浏览家族列表并加入#k#l\r\n";
        text += "\r\n#L" + SEL_BACK + "##g返回首页#k#l\r\n";
        cm.sendSimple(text);
        return;
    }

    // ===== 已加入家族 =====
    var leaderId = guild.getLeaderId();
    var leaderName = getMemberName(guild, leaderId);
    var memberCount = guild.getMembers().size();
    var capacity = guild.getCapacity();
    var guildName = guild.getName();
    var gp = guild.getGP();

    var text = "\t\t#r#e< 家族系统 >#k#n\r\n\r\n";
    text += "家族名称：#b" + guildName + "#k\r\n";
    text += "家族GP：#b" + gp + "#k\r\n";
    text += "族长：#b" + leaderName + "#k\r\n";
    text += "成员数量：#b" + memberCount + "#k / #r" + capacity + "#k\r\n\r\n";
    text += "━━━ 请选择操作 ━━━\r\n\r\n";
    text += "#L1##b查看成员列表#k#l\r\n";

    if (cm.getPlayer().getGuildRank() == 1) {
        text += "#L3##b扩充成员上限#k（#r" + EXPAND_COST_NX + "点券#k 增加" + EXPAND_SIZE + "人，最多" + MAX_CAPACITY + "人）#l\r\n";
    }
    text += "\r\n#L" + SEL_BACK + "##g返回首页#k#l\r\n";

    cm.sendSimple(text);
}

// ==================== 处理主菜单选择 ====================
function handleMainSelection(selection) {
    var guild = cm.getPlayer().getGuild();

    if (selection == SEL_BACK) {
        cm.dispose();
        cm.openNpc(9900001);
        return;
    }

    if (guild == null) {
        if (selection == 1) {
            handleCreateStep1();
        } else if (selection == 2) {
            showGuildList();
        } else {
            cm.dispose();
        }
        return;
    }

    if (selection == 1) {
        pendingAction = "memberList";
        showMemberList();
    } else if (selection == 3) {
        pendingAction = "expand";
        showExpandConfirm();
    } else {
        cm.dispose();
    }
}

// ==================== 创建家族 ====================
function handleCreateStep1() {
    var player = cm.getPlayer();
    var meso = player.getMeso();

    if (meso < CREATE_COST_MESO) {
        cm.sendOk("金币不足 #r" + (CREATE_COST_MESO / 10000) + "W#k，无法创建家族！");
        cm.dispose();
        return;
    }

    player.gainMeso(-CREATE_COST_MESO, true);
    pendingAction = "createName";
    cm.sendGetText("请输入新家族的名称（3-12个字符，支持中文、英文、数字、下划线）：");
}

function handleCreateFinal() {
    var player = cm.getPlayer();
    var guildName = cm.getText();

    if (guildName == null || guildName.length < 3 || guildName.length > 12) {
        player.gainMeso(CREATE_COST_MESO, true);
        cm.sendOk("家族名称长度必须为 3-12 个字符！已退还 " + (CREATE_COST_MESO / 10000) + "W金币。");
        cm.dispose();
        return;
    }

    var guildId = Guild.createGuild(player.getId(), guildName);
    if (guildId == 0) {
        player.gainMeso(CREATE_COST_MESO, true);
        cm.sendOk("家族名称已被使用！已退还 " + (CREATE_COST_MESO / 10000) + "W金币。");
        cm.dispose();
        return;
    }

    // 加载公会到内存
    Server.getInstance().getGuild(guildId, player.getWorld());

    // 设置玩家公会信息并加入
    var mgc = player.getMGC();
    mgc.setGuildId(guildId);
    mgc.setGuildRank(1);
    mgc.setAllianceRank(5);
    Server.getInstance().addGuildMember(mgc, player);

    cm.sendOk("恭喜！家族创建成功！\r\n你现在是家族族长了。已扣除 " + (CREATE_COST_MESO / 10000) + "W金币。");
    cm.dispose();
}

// ==================== 浏览家族列表并加入 ====================
function showGuildList() {
    var text = "\t\t#r#e< 家族列表 >#k#n\r\n\r\n";
    var guilds = [];

    try {
        var con = DatabaseConnection.getConnection();
        var ps = con.prepareStatement(
            "SELECT g.guildid, g.name, g.capacity, c.name AS leaderName, " +
            "  (SELECT COUNT(*) FROM characters WHERE guildid = g.guildid) AS memberCount, " +
            "  g.gp " +
            "FROM guilds g " +
            "JOIN characters c ON c.id = g.leader " +
            "ORDER BY g.gp DESC"
        );
        var rs = ps.executeQuery();

        while (rs.next()) {
            guilds.push({
                id: rs.getInt("guildid"),
                name: rs.getString("name"),
                capacity: rs.getInt("capacity"),
                leader: rs.getString("leaderName"),
                memberCount: rs.getInt("memberCount"),
                gp: rs.getInt("gp"),
                full: rs.getInt("memberCount") >= rs.getInt("capacity")
            });
        }
        rs.close();
        ps.close();
        con.close();
    } catch (e) {
        cm.sendOk("查询家族列表失败，请稍后再试。");
        cm.dispose();
        return;
    }

    if (guilds.length == 0) {
        text += "#d当前服务器暂无家族。#k\r\n";
        text += "\r\n#L" + SEL_BACK + "##g返回主菜单#k#l\r\n";
        cm.sendSimple(text);
        return;
    }

    text += "共 #b" + guilds.length + "#k 个家族\r\n";
    text += "━━━━━━━━━━━━━━━━━━━━\r\n\r\n";

    for (var i = 0; i < guilds.length; i++) {
        var g = guilds[i];
        text += "#L" + g.id + "#";
        text += "#b" + (i + 1) + ". " + g.name + "#k";
        text += " 族长：" + g.leader;
        text += " 成员：" + g.memberCount + "/" + g.capacity;
        if (g.full) text += " #r[已满]#k";
        text += "\r\n";
        text += "#l\r\n";
    }

    text += "\r\n#L" + SEL_BACK + "##g返回主菜单#k#l\r\n";
    pendingAction = "joinList";
    cm.sendSimple(text);
}

function handleJoinGuild(selection) {
    if (selection == SEL_BACK) {
        showMainMenu();
        return;
    }

    var player = cm.getPlayer();
    var guildId = selection;

    var guild = Server.getInstance().getGuild(guildId, player.getWorld());
    if (guild == null) {
        cm.sendOk("该家族不存在或已解散。");
        cm.dispose();
        return;
    }
    if (guild.getMembers().size() >= guild.getCapacity()) {
        cm.sendOk("该家族人数已满，无法加入。");
        cm.dispose();
        return;
    }

    var mgc = player.getMGC();
    mgc.setGuildId(guildId);
    mgc.setGuildRank(5);
    mgc.setAllianceRank(5);

    if (Server.getInstance().addGuildMember(mgc, player) == 0) {
        mgc.setGuildId(0);
        cm.sendOk("加入家族失败！可能人数已满。");
    } else {
        cm.sendOk("恭喜！你已成功加入家族 " + guild.getName() + "！");
    }
    cm.dispose();
}

// ==================== 成员列表 ====================
function showMemberList() {
    var guild = cm.getPlayer().getGuild();
    var members = guild.getMembers();

    var onlineMembers = [];
    var offlineMembers = [];
    for (var i = 0; i < members.size(); i++) {
        var m = members.get(i);
        if (m.isOnline()) {
            onlineMembers.push(m);
        } else {
            offlineMembers.push(m);
        }
    }

    var text = "\t\t#r#e< 家族成员列表 >#k#n\r\n\r\n";
    text += "总成员：#b" + members.size() + "#k  在线：#g" + onlineMembers.length + "#k  离线：#d" + offlineMembers.length + "#k\r\n";
    text += "━━━━━━━━━━━━━━━━━━━━\r\n\r\n";

    if (onlineMembers.length > 0) {
        text += "#g-- 在线成员 --#k\r\n\r\n";
        for (var i = 0; i < onlineMembers.length; i++) {
            var m = onlineMembers[i];
            var rankTag = getRankTag(m.getGuildRank());
            var jobName = getJobNameCN(m.getJobId());
            var mapName = "";
            var chr = m.getCharacter();
            if (chr != null) {
                mapName = chr.getMap().getMapName();
            }

            text += "#L" + m.getId() + "#";
            text += "#b" + (i + 1) + ". " + rankTag + " " + m.getName() + "#k";
            text += " Lv." + m.getLevel();
            text += " " + jobName;
            text += " [频道" + m.getChannel() + "] - 地图：" + mapName + "\r\n\r\n";
            text += "#l\r\n";
        }
    }

    if (offlineMembers.length > 0) {
        text += "#d-- 离线成员 --#k\r\n\r\n";
        for (var i = 0; i < offlineMembers.length; i++) {
            var m = offlineMembers[i];
            var rankTag = getRankTag(m.getGuildRank());
            var jobName = getJobNameCN(m.getJobId());

            text += "#L" + m.getId() + "#";
            text += "#d" + (i + 1) + ". " + rankTag + " " + m.getName() + "#k";
            text += " Lv." + m.getLevel();
            text += " " + jobName;
            text += " [离线]\r\n\r\n";
            text += "#l\r\n";
        }
    }

    text += "\r\n#L" + SEL_BACK + "##g返回主菜单#k#l\r\n";
    pendingAction = "memberList";
    cm.sendSimple(text);
}

// ==================== 查看成员装备信息 ====================
function handleMemberSelection(selection) {
    if (selection == SEL_BACK) {
        showMainMenu();
        return;
    }

    var guild = cm.getPlayer().getGuild();
    var members = guild.getMembers();
    var targetMember = null;

    for (var i = 0; i < members.size(); i++) {
        if (members.get(i).getId() == selection) {
            targetMember = members.get(i);
            break;
        }
    }

    if (targetMember == null) {
        cm.sendOk("成员信息异常，请重新选择。");
        cm.dispose();
        return;
    }

    if (targetMember.isOnline() && targetMember.getCharacter() != null) {
        var targetChr = targetMember.getCharacter();
        if (targetChr.getMapId() == cm.getPlayer().getMapId()) {
            cm.getClient().sendPacket(PacketCreator.charInfo(targetChr));
            cm.sendOk("正在查看 " + targetMember.getName() + " 的装备信息...");
            cm.dispose();
            return;
        }
    }

    var rankTag = getRankTag(targetMember.getGuildRank());
    var jobName = getJobNameCN(targetMember.getJobId());
    var statusStr = targetMember.isOnline() ? "在线" : "离线";

    var text = "\t\t#r#e< 成员信息 >#k#n\r\n\r\n";
    text += "角色名：" + targetMember.getName() + "\r\n";
    text += "角色ID：" + targetMember.getId() + "\r\n";
    text += "等级：Lv." + targetMember.getLevel() + "\r\n";
    text += "职业：" + jobName + "\r\n";
    text += "职位：" + rankTag + "\r\n";
    text += "状态：" + statusStr + "\r\n\r\n";

    if (!targetMember.isOnline()) {
        text += "该成员当前离线，无法查看装备详情。";
    } else {
        text += "该成员在其他地图，需在同一地图才能查看装备。";
    }

    cm.sendOk(text);
    cm.dispose();
}

// ==================== 扩充成员上限 ====================
function showExpandConfirm() {
    if (cm.getPlayer().getGuildRank() != 1) {
        cm.sendOk("只有族长才能扩充家族成员上限！");
        cm.dispose();
        return;
    }

    var guild = cm.getPlayer().getGuild();
    var currentCapacity = guild.getCapacity();

    if (currentCapacity >= MAX_CAPACITY) {
        cm.sendOk("家族成员上限已达最大值 " + MAX_CAPACITY + " 人！");
        cm.dispose();
        return;
    }

    var newCapacity = currentCapacity + EXPAND_SIZE;
    if (newCapacity > MAX_CAPACITY) newCapacity = MAX_CAPACITY;

    var cashShop = cm.getPlayer().getCashShop();
    var currentNX = cashShop.getCash(1);

    var text = "\t\t#r#e< 扩充家族成员上限 >#k#n\r\n\r\n";
    text += "当前上限：" + currentCapacity + "人\r\n";
    text += "扩充后上限：" + newCapacity + "人\r\n";
    text += "消耗点券：" + EXPAND_COST_NX + "点\r\n";
    text += "当前点券：" + currentNX + "点\r\n\r\n";

    if (currentNX < EXPAND_COST_NX) {
        text += "点券不足，无法扩充！";
        cm.sendOk(text);
        cm.dispose();
        return;
    }

    text += "确定要支付 " + EXPAND_COST_NX + "点券 扩充家族人数吗？";
    pendingAction = "expand";
    cm.sendYesNo(text);
}

function handleExpandConfirm() {
    var guild = cm.getPlayer().getGuild();

    if (guild.getCapacity() >= MAX_CAPACITY) {
        cm.sendOk("家族成员上限已达最大值！");
        cm.dispose();
        return;
    }

    var cashShop = cm.getPlayer().getCashShop();
    if (cashShop.getCash(1) < EXPAND_COST_NX) {
        cm.sendOk("点券不足！");
        cm.dispose();
        return;
    }

    if (!guild.increaseCapacity()) {
        cm.sendOk("扩充失败！家族人数已达系统上限。");
        cm.dispose();
        return;
    }

    cashShop.gainCash(1, -EXPAND_COST_NX);
    cm.sendOk("扩充成功！家族成员上限已提升至 " + guild.getCapacity() + "人。已扣除 " + EXPAND_COST_NX + "点券。");
    cm.dispose();
}

// ==================== 工具函数 ====================
function getMemberName(guild, memberId) {
    var members = guild.getMembers();
    for (var i = 0; i < members.size(); i++) {
        if (members.get(i).getId() == memberId) {
            return members.get(i).getName();
        }
    }
    return "未知";
}

function getRankTag(rank) {
    if (rank == 1) return "[族长]";
    if (rank == 2) return "[副族长]";
    return "[成员]";
}

function getJobNameCN(jobId) {
    return I18nUtil.getMessage("job.name." + jobId);
}
