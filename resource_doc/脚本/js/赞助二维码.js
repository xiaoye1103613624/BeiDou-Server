var 小烟花 ="#fMap/MapHelper/weather/squib/squib4/1#";
var 桃花 ="#fMap/MapHelper/weather/rose/4#";
var 小烟花 ="#fMap/MapHelper/weather/squib/squib4/1#";
var 二维码 = "#fEffect/CharacterEff1.img/QQ1408745/22/0#";
var 赞助中心 = "#fEffect/CharacterEff1.img/QQ1408745/3/0#";
function start() {
    status = -1;

    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }
    else {
        if (status >= 0 && mode == 0) {

            cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        }
        else {
            status--;
        }
        if (status == 0) {
            var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
			//text += "\t\t\t\t\t  "+赞助中心+"\r\n"//3
			text += "\t\t\t   "+二维码+"\r\n\r\n"//3
			text += "#r需要赞助请联系(GM唯一QQ):1408745.然后扫上方二维码\r\n"
	        text += "此码支持微信+支付宝（花呗/信用卡）并语音提示GM及时处理\r\n\r\n"
			text += "#b若有事找老G,恰好GM有事不在.也可扫码(1-10)任意额度\r\n"
	        text += "#bGM听到提示音后也会立刻QQ回复,并送上等额的福利礼包！\r\n\r\n"

			
            cm.sendSimple(text);
        }
    }
}
