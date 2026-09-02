/*
	物品:	2431769
	描述:	妖精学院艾利涅 - 纸条（任务32113随机线索物品）
	任务:	32113
	适配:	北斗GMS083 物品脚本
*/

var chat = [
	"#e#b终于完成了！真是的，光制作就花了一个多星期。真是迫不及待想试穿了。现在先放在画像后面，待会用上时好来拿。",
	"下课后去吃桑蘑吧？我知道一个没人知道的地方。",
	"看上去好像有男朋友了吧？其实没有。",
	"那些毕业生姐姐真漂亮啊。不过我比她们更漂亮，嘿嘿~",
	"隔壁班的帕伊尼好像以为我喜欢她。",
	"最近好像长胖了……我的翅膀都快承受不住了。"
];

function start() {
	var player = im.getPlayer();

	// 任务32113未进行中：消耗纸条并提示无价值
	if (player.getQuestStatus(32113) != 1) {
		im.gainItem(2431769, -1);
		player.dropMessage(5, "（上面写的都是一些闲聊的内容……没什么有价值的东西）");
		im.dispose();
		return;
	}

	// 消耗1个纸条，随机显示一条聊天内容
	im.gainItem(2431769, -1);
	var randChat = randomNum(0, chat.length - 1);
	player.dropMessage(5, chat[randChat]);

	if (randChat == 0) {
		// 抽中"制作物品"线索 → 给经验 + 完成任务
		player.dropMessage(5, "正在制作什么东西，而且就位于自画像的后面？这个应该能够成为线索。和 魔法师库迪 商议一下吧。");
		im.forceCompleteQuest(32113);
		try {
			im.gainExp(4800);
		} catch (e) {
			player.dropMessage(5, "经验获取异常，请联系管理员。");
		}
	} else {
		// 其他闲聊内容
		player.dropMessage(5, "……总之，好像并不是十分有用的情报。看看别的纸条吧。");
	}

	im.dispose();
}

function randomNum(min, max) {
	return parseInt(Math.random() * (max - min + 1) + min, 10);
}
