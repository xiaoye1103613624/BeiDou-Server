// 幽灵斯坦 - 纯氛围NPC，无任务/商店，点击仅随机说一句台词后结束对话
function start() {
	var lines = ["我担心我的儿子……", "喂，你！最好不要在这里惹是生非！"];
	cm.sendOk(lines[Math.floor(Math.random() * lines.length)]);
	cm.dispose();
}
