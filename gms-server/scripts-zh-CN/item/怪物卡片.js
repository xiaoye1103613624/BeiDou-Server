/*
 * ==================
 * 脚本类型: 怪物卡片使用脚本
 * 脚本作者：北斗项目组
 * 功能说明：
 *   双击使用怪物卡片后，自动收集到卡片图鉴中
 *   需要在WZ中配置每个卡片物品：
 *     物品spec/npc = 对应怪物ID
 *     物品spec/script = "怪物卡片"
 *   如果WZ未配置spec/npc，使用数据库映射表（card_collection_config）
 * ==================
 */

var status = -1;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode === -1) {
		im.dispose();
		return;
	}
	if (mode === 1) {
		status++;
	} else {
		im.dispose();
		return;
	}

	if (status === 0) {
		// 优先从WZ的spec/npc获取怪物ID
		var monsterId = im.getNpc();
		if (monsterId <= 0) {
			// WZ未配置spec/npc，从物品ID反查（遍历角色背包中带脚本的卡片物品）
			monsterId = 0;
		}
		if (monsterId <= 0) {
			im.sendOk("卡片数据异常，请联系管理员配置卡片物品的WZ数据。\r\n(spec/npc需设置为对应怪物ID)");
			im.dispose();
			return;
		}

		// 加载现有收集数据
		var data = im.getCharacterExtendValue("monsterCardCollection");
		var collection;
		if (data == null || data === "") {
			collection = { monsters: {}, claimed: [], regionRewards: [] };
		} else {
			try {
				collection = JSON.parse(data);
			} catch (e) {
				collection = { monsters: {}, claimed: [], regionRewards: [] };
			}
		}

		// 增加卡片计数
		var key = String(monsterId);
		if (!collection.monsters[key]) {
			collection.monsters[key] = 0;
		}
		collection.monsters[key]++;
		var newCount = collection.monsters[key];

		// 保存收集数据
		im.saveOrUpdateCharacterExtendValue("monsterCardCollection", JSON.stringify(collection));

		// 提示信息
		if (newCount >= 5 && newCount < 10) {
			im.sendOk("恭喜！已收集 #r" + newCount + "#k 张卡片！\r\n收集已满 #b5张#k，可以去找NPC领取 #r2AP#k 奖励了！");
		} else if (newCount >= 10) {
			im.sendOk("卡片已收集！当前: #r" + newCount + "#k 张\r\n(已超过5张上限，可到NPC处领取奖励)");
		} else {
			im.sendOk("卡片已收集！\r\n当前进度: #b" + newCount + "/5#k\r\n还需 #r" + (5 - newCount) + "#k 张即可完成！");
		}
		im.dispose();
	}
}
