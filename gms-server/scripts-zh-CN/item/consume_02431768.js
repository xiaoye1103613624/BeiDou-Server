/*
	物品:	2431768
	描述:	妖精学院艾利涅 - 纸条（任务32110随机线索物品）
	任务:	32110
	适配:	北斗GMS083 物品脚本
*/

var chat = [
	"#e#b我们的秘密物品已经安全地藏在书桌下了。这可千万不能让副校长发现。",
	"困死了……何时才下课啊？",
	"好耀眼……副校长的头发好耀眼啊……",
	"你更傻！彩虹反射",
	"一决胜负吧，帕伊尼！我要让你知道，我比你更快。下午来一场对决吧。"
];

function start() {
	var player = im.getPlayer();

	// 任务32110未进行中：消耗纸条并提示无价值
	if (player.getQuestStatus(32110) != 1) {
		im.gainItem(2431768, -1);
		player.dropMessage(5, "（上面写的都是一些闲聊的内容……没什么有价值的东西）");
		im.dispose();
		return;
	}

	// 消耗1个纸条，随机显示一条聊天内容
	im.gainItem(2431768, -1);
	var randChat = randomNum(0, chat.length - 1);
	player.dropMessage(5, chat[randChat]);

	if (randChat == 0) {
		// 抽中"秘密物品"线索 → 给经验 + 完成任务
		player.dropMessage(5, "书桌下,秘密物品……？这个应该能成为线索。和 魔法师库迪 商议一下吧。");
		im.forceCompleteQuest(32110);
		try {
			im.gainExp(4800);
		} catch (e) {
			player.dropMessage(5, "经验获取异常，请联系管理员。");
		}
	} else {
		// 其他闲聊内容
		player.dropMessage(5, "……总之，好像并不是十分重要的样子。看看别的纸条吧。");
	}

	im.dispose();
}

function randomNum(min, max) {
	return parseInt(Math.random() * (max - min + 1) + min, 10);
}