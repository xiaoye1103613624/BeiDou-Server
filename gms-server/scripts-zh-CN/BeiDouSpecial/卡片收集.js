/*
 * ==================
 * 脚本类型: 卡片收集
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 击杀怪物掉落怪物卡片，双击使用后自动收集
 *   2. 同一怪物收集5张卡片 = 完成，可领取2AP
 *   3. 同一地区所有怪物完成 = 地区完成，可领取5AP
 *   4. 怪物卡片数据从数据库 card_collection_config 表读取
 *   5. 管理员通过Web后台维护数据
 * ==================
 */

// 从数据库加载地区数据
var CardCollectionManager = Java.type('org.gms.config.CardCollectionManager');
var regions = CardCollectionManager.getRegions();

// ===== NPC脚本部分 =====
var status = -1;
var currentRegionIdx = -1;
var currentMonsterIdx = 0;

function start() {
	status = -1;
	currentRegionIdx = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode === -1) {
		if (currentRegionIdx === -1) {
			cm.dispose();
			return;
		}
		currentRegionIdx = -1;
		currentMonsterIdx = 0;
		status = -1;
		showMainMenu();
		return;
	}

	if (mode === 0) {
		cm.dispose();
		return;
	}

	status++;

	if (currentRegionIdx === -1) {
		if (status === 0) {
			showMainMenu();
		} else {
			handleMainSelection(selection);
		}
	} else {
		if (status === 0) {
			showMonsterList();
		} else {
			handleMonsterAction(selection);
		}
	}
}

function showMainMenu() {
	var collection = loadCollection();
	var text = "#e#b=== 怪物卡片收集手册 ===#k#n\r\n\r\n";
	text += "#r集齐5张同一怪物卡片 = 2AP属性奖励#k\r\n";
	text += "#r完成整个地区收集 = 5AP属性奖励#k\r\n\r\n";
	text += "请选择要查看的地区：\r\n";

	for (var i = 0; i < regions.size(); i++) {
		var region = regions.get(i);
		var cards = region.monsters;
		var total = cards.size();
		var completed = 0;
		for (var j = 0; j < total; j++) {
			if (isCompleted(cards.get(j).monsterId, collection)) {
				completed++;
			}
		}
		var pct = Math.floor(completed / total * 100);
		text += "#L" + i + "##b" + region.name + "#k (" + completed + "/" + total + ", " + pct + "%)#l\r\n";
	}

	status = 0;
	cm.sendSimple(text);
}

function handleMainSelection(selection) {
	currentRegionIdx = selection;
	currentMonsterIdx = 0;
	status = 0;
	showMonsterList();
}

function showMonsterList() {
	var region = regions.get(currentRegionIdx);
	var cards = region.monsters;
	var collection = loadCollection();
	var total = cards.size();
	var completed = 0;

	var text = "#e#b" + region.name + "#k#n\r\n\r\n";

	for (var i = 0; i < total; i++) {
		var m = cards.get(i);
		var monsterId = m.monsterId;
		var cardItemId = m.cardItemId;
		var count = getCardCount(monsterId, collection);
		var done = count >= 5;
		if (done) completed++;

		var icon = done ? "#g[完]#k " : "";
		text += "#L" + i + "#" + icon + " #i" + cardItemId + "# ";

		if (count >= 5 && !isClaimed(monsterId, collection)) {
			text += "#r[可领取2AP]#k";
		} else if (isClaimed(monsterId, collection)) {
			text += "#g[已领取]#k";
		} else {
			text += "(" + count + "/5)";
		}
		text += "#l\r\n";
	}

	var regionComplete = (completed === total);
	var regionClaimed = isRegionClaimed(currentRegionIdx, collection);

	text += "\r\n";
	text += "#L" + total + "##r[返回主菜单]#l\r\n";
	if (regionComplete && !regionClaimed) {
		text += "#L" + (total + 1) + "##d[领取地区完成奖励: 5AP]#l\r\n";
	}

	status = 1;
	cm.sendSimple(text);
}

function handleMonsterAction(selection) {
	var region = regions.get(currentRegionIdx);
	var cards = region.monsters;
	var total = cards.size();
	var collection = loadCollection();

	if (selection < total) {
		var m = cards.get(selection);
		var monsterId = m.monsterId;
		var cardItemId = m.cardItemId;
		var count = getCardCount(monsterId, collection);

		if (count >= 5 && !isClaimed(monsterId, collection)) {
			claimMonsterReward(monsterId, collection);
			cm.getPlayer().gainAp(2, true);
			cm.sendOk("恭喜！完成了 #i" + cardItemId + "# 卡片收集！\r\n获得 #r2点AP#k 属性奖励！");
		} else if (isClaimed(monsterId, collection)) {
			cm.sendOk("该怪物的奖励已经领取过了。");
		} else {
			cm.sendOk("#i" + cardItemId + "#\r\n收集进度: #r" + count + "/5#k\r\n还需 #r" + (5 - count) + "#k 张卡片即可完成！\r\n完成奖励: #r2AP");
		}
	} else if (selection === total) {
		currentRegionIdx = -1;
		status = -1;
		showMainMenu();
		return;
	} else if (selection === total + 1) {
		if (!isRegionClaimed(currentRegionIdx, collection)) {
			claimRegionReward(currentRegionIdx, collection);
			cm.getPlayer().gainAp(5, true);
			cm.sendOk("恭喜！完成了 #b" + region.name + "#k 所有怪物卡片收集！\r\n获得 #r5点AP#k 属性奖励！");
		} else {
			cm.sendOk("该地区的奖励已经领取过了。");
		}
	}

	status = -1;
	currentMonsterIdx = 0;
	showMonsterList();
}

// ===== 数据持久化 =====

function loadCollection() {
	var data = cm.getCharacterExtendValue("monsterCardCollection");
	if (data == null || data === "") {
		return { monsters: {}, claimed: [], regionRewards: [] };
	}
	try {
		return JSON.parse(data);
	} catch (e) {
		return { monsters: {}, claimed: [], regionRewards: [] };
	}
}

function saveCollection(data) {
	cm.saveOrUpdateCharacterExtendValue("monsterCardCollection", JSON.stringify(data));
}

// ===== 辅助函数 =====

function getCardCount(monsterId, collection) {
	return collection.monsters[String(monsterId)] || 0;
}

function isCompleted(monsterId, collection) {
	return getCardCount(monsterId, collection) >= 5;
}

function isClaimed(monsterId, collection) {
	return collection.claimed.indexOf(String(monsterId)) >= 0;
}

function isRegionClaimed(regionIdx, collection) {
	return collection.regionRewards.indexOf(String(regionIdx)) >= 0;
}

function claimMonsterReward(monsterId, collection) {
	var key = String(monsterId);
	if (collection.claimed.indexOf(key) < 0) {
		collection.claimed.push(key);
	}
	saveCollection(collection);
}

function claimRegionReward(regionIdx, collection) {
	var key = String(regionIdx);
	if (collection.regionRewards.indexOf(key) < 0) {
		collection.regionRewards.push(key);
	}
	saveCollection(collection);
}
