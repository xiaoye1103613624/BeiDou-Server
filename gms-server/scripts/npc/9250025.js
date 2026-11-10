/*
	自由市场枫叶电视 (9250025)

	背景：NPCTalkHandler 只按英文名 endsWith("Maple TV") 路由到 mapleTV.js，
	zh-CN 名「自由市场枫叶电视」不匹配 ⇒ 落到按 id 找本脚本 ⇒ 此前文件缺失，
	服务端刷 "NPC 自由市场枫叶电视 (9250025) is not coded"（logs/out.log
	2026-11-07 12:20~12:25 实测）。

	原生语义：TV 大屏是纯显示装置，画面由 SEND_TV 世界广播驱动（客户端
	Npc::set_tv_message 灌上屏文字）；点击本身只关对话框，与英文
	Maple TV 走 mapleTV.js 默认分支（cm.dispose）行为一致。
	发送侧仍走 5070005 枫叶电视道具（UseCashItemHandler itemType 507 case 5）。
*/

function start() {
    cm.dispose();
}
