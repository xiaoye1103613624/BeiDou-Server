/*
	物品:	2431796
	描述:	列娜海峡 - 钓鱼工具（任务32179物品）
	任务:	32179
	适配:	北斗GMS083 物品脚本
*/

function start() {
	var player = im.getPlayer();

	// 检查地图：只能在列娜海峡使用
	if (player.getMap().getId() != 141060000) {
		player.dropMessage(5, "（还是别在列娜海峡以外的地方使用比较好，免得弄坏了。）");
		im.dispose();
		return;
	}

	// 检查任务32179是否进行中
	if (player.getQuestStatus(32179) != 1) {
		player.dropMessage(5, "（这是别人的钓鱼工具，我还是不要乱用的好。）");
		im.dispose();
		return;
	}

	// 检查位置：必须在钓鱼点附近（坐标1230,382 ±150）
	var pos = player.getPosition();
	if (Math.abs(pos.getX() - 1230) > 150 || Math.abs(pos.getY() - 382) > 150) {
		player.dropMessage(5, "航海士，看样子这地方是钓不到鱼了。去其他地方看看吧。应该有鱼群多得肉眼可见的地方。");
		im.dispose();
		return;
	}

	// 75%概率钓到大鱼（列娜野生巨鱼 4030028）
	var isBigFish = randomNum(0, 100) < 75;

	// 钓鱼过程动画
	player.dropMessage(5, "……");
	player.dropMessage(5, "………………（噗通噗通）");
	player.dropMessage(5, "………………嗯？有信号传来。");

	if (isBigFish) {
		// 钓到大鱼：给物品 + 庆祝特效
		player.dropMessage(5, "列娜野生巨鱼！航海士，是条大鱼！");
		im.gainItem(4030028, 1);
		player.dropMessage(5, "太好了！有了这个，就不愁饿肚子啦。");
		try {
			player.showEffect("Yut/goal");
		} catch (e) {}
	} else {
		// 小鱼：放生
		player.dropMessage(5, "列娜野生米诺鱼...航海士，这点儿鱼都不够塞牙缝的啊！你再试一次吧。");
		player.dropMessage(5, "放生了列娜野生米诺鱼。");
	}

	im.dispose();
}

function randomNum(min, max) {
	return parseInt(Math.random() * (max - min + 1) + min, 10);
}
