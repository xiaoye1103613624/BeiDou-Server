var status = -1;
var keys = Array(7, 8, 9, 10, 11, 12, 13);
var keynames = Array("#fUI/UIWindow/KeyConfig/key/7#", "#fUI/UIWindow/KeyConfig/key/8#", "#fUI/UIWindow/KeyConfig/key/9#", "#fUI/UIWindow/KeyConfig/key/10#", "#fUI/UIWindow/KeyConfig/key/11#", "#fUI/UIWindow/KeyConfig/key/12#", "#fUI/UIWindow/KeyConfig/key/13#"); //just as reference
var skills = Array(
      Array(12101006, 1, 2022505, 1,"超级冥火焚烧")
); 
function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    status++;
    if (status == 0) {
        sel = selection;
        var text = "";
		text += "#e#r学习以后切记不要在键盘上面弄丢了哦！\r\n";
		text += "弄丢了就要重新学习了！#k\r\n";
        for (var i = 0; i < skills.length; i++) {
            text += "#L" + i + "#【#s" + skills[i][0] + "#】 需要 #r【#v" + skills[i][2] + "#】 x " + skills[i][3] + "#k#l\r\n";
 }
        cm.sendSimple(text);
    } else if (status == 1) {
        itt = selection;
        var text = "";
		text += "请选择所放置的技能位置：#b\r\n";
        for (var i = 0; i < keys.length; i++) {
            text += "#L" + i + "#" + keynames[i] + "#l\r\n";
 }
        cm.sendSimple(text + "#k");
    } else if (status == 2) {
        
            
            cm.dispose();
       
            cm.teachSkill(skills[itt][0], skills[itt][1], skills[itt][1]);
            
            cm.getPlayer().changeKeybinding(keys[selection], 1, skills[itt][0]);
			cm.刷新状态();
            cm.sendOk("学习成功，查看键盘即可。");
            cm.dispose();

    }
}