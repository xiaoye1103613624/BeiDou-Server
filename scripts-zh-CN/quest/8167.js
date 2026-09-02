/* 黎明之幕
[主题副本] 埃利涅尔仙子学院
黑暗领主
作者：Daenerys
*/
var status = -1;

function start(mode, type, selection) {
if (mode == 1)
status++;
else
status--;
if (status == 0) {
qm.sendAcceptDecline("唉…！　那邊的那位，可以稍微聽一下我的請託嗎？");
} else if (status == 1) {
qm.sendNext("謝謝你…我的請託，是關於家父的事。家父經營一間叫做「楓屋」的和服店…不過在街坊間的風評不太好就是了。");
} else if (status == 2) {
qm.sendNextPrev("總之，就是家父…據街坊傳說，他好像是幫楓葉古城的城主，暗地裡進行毒藥的買賣…！");
} else if (status == 3) {
	qm.sendNextPrev("在城裡的#o9400401#所使用的毒…收集到那個就能切斷背後的管道…把那賣掉的錢部分獻給城主大人…。這樣一來就可以雙方獲利。");
} else if (status == 4) {
	qm.sendNextPrev("不過…多半家父是受城主大人矇騙！　否則，以往那麼溫和的父親，怎麼會做出這樣的事情…。城主大人也是，以前非常穩重的…總是傾聽民眾的心聲，近來也整個人都變了…。城主大人本身，一定有什麼不對勁！");
} else if (status == 5) {
		qm.sendNextPrev("總之拜託你了！　這件事你可以幫我問問其他人，在城裡調查一下內情嗎？　雖然我也拜託了街坊…但是楓葉古城裡到處都是忍者，誰也不敢接近。");
} else if (status == 6) {
qm.sendYesNo("雖然我自己進行也行…不過，以前曾為了這事和守門者爭吵過，現在不讓我通過城門了。所以，…請你代替我…！　麻煩你了！");
//qm.forceCompleteQuest(8166);
//qm.warp(101020000, 0);
qm.forceStartQuest();
qm.dispose();
}
}

function end(mode, type, selection) {
if (mode == 0 && type == 0) {
status--;
} else if (mode == -1) {
qm.dispose();
return;
} else {
status++;
}
if (status == 0) {
qm.sendNext("啊…真是感謝你！　外頭傳說，不知誰在調查著城裡的內情…但不知是什麼人就是了。不好意思情報這麼少…麻煩你了。");
} else if (status == 1) {
//qm.warp(101070000, 0);
qm.forceCompleteQuest();
qm.dispose();
}
}翻译成简体