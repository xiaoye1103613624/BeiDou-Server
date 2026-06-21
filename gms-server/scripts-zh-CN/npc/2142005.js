// 尤塔 - 纯氛围NPC，无任务/商店，点击仅随机说一句台词后结束对话
function start() {
	var lines = ["唉……发生什么不好的事了吗……", "漂漂猪难道没办法驯养吗？"];
	cm.sendOk(lines[Math.floor(Math.random() * lines.length)]);
	cm.dispose();
}
