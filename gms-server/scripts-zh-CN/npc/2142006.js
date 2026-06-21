// 亚华 - 纯氛围NPC，无任务/商店，点击仅随机说一句台词后结束对话
function start() {
	var lines = ["射手村是我长大的地方，希望这里能永远和平……", "我必须变强。"];
	cm.sendOk(lines[Math.floor(Math.random() * lines.length)]);
	cm.dispose();
}
