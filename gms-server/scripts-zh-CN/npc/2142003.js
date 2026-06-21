// 爱德华 - 纯氛围NPC，无任务/商店，点击仅随机说一句台词后结束对话
function start() {
	var lines = ["大头国到底怎么样了呢？", "就算情况再困难，脑袋都必须打理好。"];
	cm.sendOk(lines[Math.floor(Math.random() * lines.length)]);
	cm.dispose();
}
