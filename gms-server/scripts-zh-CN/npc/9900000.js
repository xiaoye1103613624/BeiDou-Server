importPackage(java.util);
importPackage(Packages.client);
importPackage(Packages.server);
importPackage(Packages.tools);
importPackage(Packages.tools.packet);

/* ========== 配置区 ========== */
var 修炼等级上限 = 9999;

/* ========== 材料消耗配置（分区间） ========== */
var 等级消耗表 = [
    {区间上限: 999,   单次消耗: 10000},
    {区间上限: 1999,  单次消耗: 20000},
    {区间上限: 2999,  单次消耗: 30000},
    {区间上限: 3999,  单次消耗: 40000},
    {区间上限: 4999,  单次消耗: 50000},
    {区间上限: 5999,  单次消耗: 60000},
    {区间上限: 6999,  单次消耗: 70000},
    {区间上限: 7999,  单次消耗: 80000},
    {区间上限: 8999,  单次消耗: 90000},
    {区间上限: 9999,  单次消耗: 100000}  // 最后一项覆盖到9999级
];

var 材料ID = 4001126; //修炼等级提交材料
var 额外固定消耗 = 0; //可选项：每次升级额外加的固定值（设为0则取消）

var 制作材料 = [4000000, 4000001, 4000002, 4310143];
var 材料数量 = [50, 100, 20, 200];

var 可附魔盾牌 = [1092067, 1092031, 1092032, 1092033, 1092040, 1092044, 1092062, 1092053, 1092063, 1092064];   // ★可自由增删 ID

var 附魔道具ID = 3994730;          // ← 新增
var 附魔道具消耗基数 = 1;          // ← 新增
var 随机数 = Math.floor(Math.random() * 10);

/* ========== 图标 ========== */
var 分割线 = "#fMap/Back/zerek/拍卖/标题1#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 提示 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";

/* ========== 数据库读写 ========== */
function 读取修炼等级(charId) {
    var conn = cm.getConnection();
    var ps = conn.prepareStatement("SELECT count FROM xmwnjl WHERE characterid=? AND bossid='修炼等级'");
    ps.setInt(1, charId);
    var rs = ps.executeQuery();
    var lv = 0;
    if (rs.next()) lv = rs.getInt(1);
    rs.close(); ps.close(); conn.close();
    return lv;
}
function 写入修炼等级(charId, add) {
    var accid = charId;
    var bossid = "修炼等级";
    var conn = cm.getConnection();

    // 先查有没有
    var sel = "SELECT * FROM xmwnjl WHERE bossid = ? AND characterid = ?";
    var pstmt = conn.prepareStatement(sel);
    pstmt.setString(1, bossid);
    pstmt.setInt(2, accid);
    var rs = pstmt.executeQuery();

    if (rs.next()) {
        // 已有记录，直接 +add
        rs.close();
        pstmt.close();
        var up = "UPDATE xmwnjl SET count = count + ? WHERE bossid = ? AND characterid = ?";
        var ps2 = conn.prepareStatement(up);
        ps2.setInt(1, add);
        ps2.setString(2, bossid);
        ps2.setInt(3, accid);
        ps2.executeUpdate();
        ps2.close();
    } else {
        // 没有记录，插入一条
        rs.close();
        pstmt.close();
        var ins = "INSERT INTO xmwnjl (time,bossid,count,characterid) VALUES (CURRENT_TIMESTAMP(),?,?,?)";
        var ps3 = conn.prepareStatement(ins);
        ps3.setString(1, bossid);
        ps3.setInt(2, add);
        ps3.setInt(3, accid);
        ps3.executeUpdate();
        ps3.close();
    }
    conn.close();
}
function 读取盾牌附魔值(charId) {
    var conn = cm.getConnection();
    var ps = conn.prepareStatement("SELECT count FROM xmwnjl WHERE characterid=? AND bossid='盾牌附魔'");
    ps.setInt(1, charId);
    var rs = ps.executeQuery();
    var v = 0;
    if (rs.next()) v = rs.getInt(1);
    rs.close(); ps.close(); conn.close();
    return v;
}
function 写入盾牌附魔值(charId, v) {
    var conn = cm.getConnection();
    var ps = conn.prepareStatement(
        "INSERT INTO xmwnjl(characterid,bossid,count) VALUES(?,?,?) ON DUPLICATE KEY UPDATE count=?");
    ps.setInt(1, charId); ps.setString(2, "盾牌附魔");
    ps.setInt(3, v);      ps.setInt(4, v);
    ps.executeUpdate(); ps.close(); conn.close();
}

/* ========== 业务函数 ========== */
function 获取等级消耗(当前等级) {
    for (var i = 0; i < 等级消耗表.length; i++) {
        if (当前等级 <= 等级消耗表[i].区间上限) {
            return 等级消耗表[i].单次消耗;
        }
    }
    // 如果超出所有配置（如>9999），返回最后一档
    return 等级消耗表[等级消耗表.length - 1].单次消耗;
}
function 是附魔盾牌(id) {
    for (var i = 0; i < 可附魔盾牌.length; i++) if (可附魔盾牌[i] == id) return true;
    return false;
}
function 判断材料是否满足() {
    for (var i = 0; i < 制作材料.length; i++)
        if (!cm.haveItem(制作材料[i], 材料数量[i])) return false;
    return true;
}

/* ========== 主流程 ========== */
var status = -1;
function start() {
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) { cm.dispose(); return; }
    if (mode == 0 && status >= 0) { cm.sendOk("好的，下次再来！"); cm.dispose(); return; }
    mode == 1 ? status++ : status--;

    if (status == 0) {
        if (cm.getPlayer().getMapId() == 180000001) { cm.dispose(); cm.openNpc(9900005); return; }
        var 当前修炼等级 = 读取修炼等级(cm.getPlayer().getId());
		var 盾牌图标列表 = "";
		for (var i = 0; i < 可附魔盾牌.length; i++) {
			盾牌图标列表 += "#v" + 可附魔盾牌[i] + "##z" + 可附魔盾牌[i] + "# ";
		}
        var text = "   #v1092067##r#e最强盾牌#b修炼中心#k - #n[无止境的突破自我] \r\n"
                 + 分割线 + "\r\n"
				 + "   " + 提示 + " 只有点装盾牌可以附魔。\r\n"
                 + "   " + 提示 + " 当前修炼等级：#r" + 当前修炼等级 + "级\r\n"
                 + "#L1#" + 正方箭头 + "#b 提高修炼等级#l\r\n"
                 + "#L2#" + 正方箭头 + "#b 制作修炼盾牌#l\r\n"
                 + "#L3#" + 正方箭头 + "#b 为盾牌附魔 #d[1级=1点全属性]#l\r\n\r\n"
			//	 + 盾牌图标列表 + "\r\n"  // ← 动态插入图标
                 + 分割线 + "\r\n";
        cm.sendSimple(text);
    } else if (status == 1) {
        var 当前修炼等级 = 读取修炼等级(cm.getPlayer().getId());
        if (selection == 1) {
            bh = 1;
                    // 【核心修改】根据等级区间获取消耗
			var 单次消耗 = 获取等级消耗(当前修炼等级);
			var 总消耗 = 单次消耗 + 额外固定消耗;  // 如需固定消耗就加上
            cm.sendYesNo("当前修炼等级：#b" + 当前修炼等级 + "级#k\r\n"
                       + "本次升级需要：#v" + 材料ID + "# x " + 总消耗 + "\r\n"
                       + "是否升级？");
        } else if (selection == 2) {
            bh = 2;
            if (!判断材料是否满足()) {
                var eadd = "#e#r材料不足！#n#k\r\n";
                for (var i = 0; i < 制作材料.length; i++)
                    if (!cm.haveItem(制作材料[i], 材料数量[i]))
                        eadd += "#v" + 制作材料[i] + "##z" + 制作材料[i] + "#  需求：" + 材料数量[i] + " 实际：#c" + 制作材料[i] + "#\r\n";
                cm.sendOk(eadd);
                cm.dispose();
            } else {
                for (var i = 0; i < 制作材料.length; i++) cm.gainItem(制作材料[i], -材料数量[i]);
                cm.gainItem(可附魔盾牌[0], 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0);
                cm.sendOk("制作成功！");
                cm.喇叭(1, "玩家[" + cm.getPlayer().getName() + "] 合成了修炼盾牌，迈出变强第一步！");
                cm.dispose();
            }
        } else if (selection == 3) {
            bh = 3;
            var 当前修炼等级 = 读取修炼等级(cm.getPlayer().getId());
            var 道具消耗量 = 当前修炼等级 * 附魔道具消耗基数;   // ← 改名
            var eqpOld = cm.getInventory(1).getItem(1);
            if (eqpOld == null || !是附魔盾牌(eqpOld.getItemId())) {
                cm.sendOk("请把可附魔盾牌放在装备栏第一格！");
                cm.dispose();
                return;
            }
            cm.sendYesNo("当前修炼等级：#b" + 当前修炼等级 + " 级#k\r\n"
                       + "盾牌将获得：#b" + 当前修炼等级 + "点全属性#k\r\n"
					   + "消耗道具：#b#v" + 附魔道具ID + "##z" + 附魔道具ID + "# x " + 道具消耗量 + "#k\r\n"
                       + "#r#e是否继续？");
        }
    } else if (status == 2) {
        var 当前修炼等级 = 读取修炼等级(cm.getPlayer().getId());
        if (bh == 1) {
                    // 【核心修改】根据等级区间获取消耗
			var 单次消耗 = 获取等级消耗(当前修炼等级);
			var 总消耗 = 单次消耗 + 额外固定消耗;  // 如需固定消耗就加上
			
            if (!cm.haveItem(材料ID, 总消耗)) {
                cm.sendOk("#v" + 材料ID + "#不足！需要 " + 总消耗 + " 个");
                cm.dispose();
                return;
            }
            cm.gainItem(材料ID, -总消耗);
            写入修炼等级(cm.getPlayer().getId(), 1);
            var 新等级 = 读取修炼等级(cm.getPlayer().getId());
            cm.sendOk("修炼等级提升！当前：#r" + 新等级 + "级#k\r\n");
			cm.喇叭(2, "玩家 [" + cm.getPlayer().getName() + "] 将盾牌附魔修炼等级提升至 " + 新等级 + " 级！");
            cm.dispose();
        } else if (bh == 3) {
    var 当前修炼等级 = 读取修炼等级(cm.getPlayer().getId());
    var 道具消耗量 = 当前修炼等级 * 附魔道具消耗基数;

    if (!cm.haveItem(附魔道具ID, 道具消耗量)) {
        cm.sendOk("#v" + 附魔道具ID + "#不足！需要 " + 道具消耗量 + " 个");
        cm.dispose();
        return;
    }
    var eqpOld = cm.getInventory(1).getItem(1);
    if (eqpOld == null || !是附魔盾牌(eqpOld.getItemId())) {
        cm.sendOk("请把可附魔盾牌放在装备栏第一格！");
        cm.dispose();
        return;
    }

    /* 1. 直接复制新盾牌 */
    var ii = Packages.server.MapleItemInformationProvider.getInstance();
    var eqpNew = ii.getEquipById(eqpOld.getItemId()).copy();

    /* 2. 属性直接等于修炼等级（完全覆盖） */
    var 附魔值 = 当前修炼等级;
    eqpNew.setStr (附魔值);
    eqpNew.setDex (附魔值);
    eqpNew.setInt (附魔值);
    eqpNew.setLuk (附魔值);
//    eqpNew.setHp  (附魔值);
//    eqpNew.setMp  (附魔值);
    eqpNew.setWatk(附魔值);
    eqpNew.setMatk(附魔值);
//    eqpNew.setWdef(附魔值);
//    eqpNew.setMdef(附魔值);
//    eqpNew.setAcc (附魔值);
//    eqpNew.setAvoid(附魔值);

    /* 3. 继承强化数据 */
    eqpNew.setHands (eqpOld.getHands());
    eqpNew.setSpeed (eqpOld.getSpeed());
    eqpNew.setJump  (eqpOld.getJump());
    eqpNew.setUpgradeSlots(eqpOld.getUpgradeSlots());
    eqpNew.setLevel(eqpOld.getLevel());
//    eqpNew.setFlag(1); // 上锁

    /* 4. 换装备 */
    cm.gainItem(附魔道具ID, -道具消耗量);
    cm.gainItem(eqpOld.getItemId(), -1);
    cm.addFromDrop(eqpNew);

    cm.sendOk("附魔成功！盾牌全属性为 " + 附魔值 + " 点");
    cm.喇叭(2, "玩家[" + cm.getPlayer().getName() + "] 将修炼盾牌附魔了 " + 附魔值 + " 点全属性！");
    cm.dispose();
        }
    }
}