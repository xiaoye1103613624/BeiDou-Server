// 视觉装饰变量（完整保留用户提供的所有标识，新增变量均保留）
var 星星 = "#fEffect/CharacterEff/1112903/0/0#";
var 爱心 = "#fEffect/CharacterEff/1032063/0/0#";
var 红色箭头 = "#fEffect/UIWindow/Quest/icon6/7#";
var 正方形 = "#fEffect/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fEffect/UIWindow/Quest/icon2/7#";
var ttt ="#fUI/UIWindow.img/Quest/icon9/0#";
var xxx ="#fUI/UIWindow.img/Quest/icon8/0#";
var sss ="#fUI/UIWindow.img/QuestIcon/3/0#";
var 表情 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/0#";
var 表情1 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/1#";
var 表情2 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/2#";
var 蓝色小兔子 = "#fEffect/CharacterEff.img/1112960/3/1#";
var 小红星 = "#fEffect/CharacterEff.img/1112926/0/0#";
var 小蓝星 = "#fEffect/CharacterEff.img/1112925/0/0#";
var 音符3 = "#fEffect/CharacterEff.img/1112949/2/0#";
var 蓝色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/0/0#"; 
var 红色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/4/0#"; 
var 任务简介 = "#fUI/UIWindow.img/Quest/summary#"; 
var 任务提示 = "#fUI/UIWindow.img/Quest/BtAlert/mouseOver/0#"; 
var 成功了 = "#fEffect/BasicEff.img/Fishing/6#"; 
var 蓝色小喇叭 = "#fUI/CN_Chat.img/ChattingRoom/BtVolUp/0/mouseOver/0#";  
var 热点推荐 = "#fUI/CashShop.img/CSChar/BtCoordination/normal/0#";
var 铅笔 = "#fUI/GuildBBS.img/GuildBBS/BtReply/mouseOver/0#";

// 初始化函数（完全保留原逻辑）
function start() {
    status = -1;
    action(1, 0, 0);
}

// 核心交互函数（功能逻辑1:1保留，仅美化UI文本）
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        // 取消操作提示（保留原逻辑，美化提示文本）
        if (status >= 0 && mode == 0) {
            cm.sendOk(爱心 + 小红星 + " #k感谢你的光临！" + 小蓝星 + 爱心 + "\r\n" + 蓝色箭头 + " 期待你的再次来访 ~ " + 表情1);
            cm.dispose();
            return;
        }
        // 状态切换逻辑（完全保留原逻辑）
        if (mode == 1) {
            status++;
        } else {
            status--;
        }

        // 初始界面 - 5转技能必成中心主页面（核心UI美化）
        if (status == 0) {
            var text = "";
            // 顶部对称装饰边框
            text += "    " + 小红星 + 星星 + 蓝色小兔子 + 爱心 + 蓝色小兔子 + 星星 + 小蓝星 + "    \r\n\r\n";
            // 标题区 - 醒目特效+装饰图标
            text += "            #e#b#r【5转技能必成中心】#k#n#b " + 表情2 + "            \r\n\r\n";
            // 欢迎语 - 专属图标点缀
            text += "    " + 热点推荐 + " " + 蓝色小喇叭 + " #k欢迎来到专属5转技能学习中心 \r\n\r\n";
            // 分隔线 - 装饰图标组合
            text += "    " + 正方形 + 小红星 + 正方形 + 小蓝星 + 正方形 + 小红星 + 正方形 + "    \r\n\r\n";
            // 功能菜单 - 统一视觉格式，专属前缀
            text += "#L0#" + 红色箭头 + " " + 铅笔 + " #e#b学习5转技能#n#l\r\n\r\n";
            // 注释功能保留原注释状态，不显示
            //text += "#L1#"+铅笔+"豆豆抽奖\r\n";
            // GM专属功能（保留原权限判断，美化UI）
            if(cm.getChar().isGM()){
                //text += "#L2#"+红色箭头 + " " + 铅笔 + " 加100万豆豆[GM可见,正式上线可删除]\r\n";
            }
            // 底部装饰收尾
            text += "\r\n    " + 爱心 + 星星 + 小红星 + 小蓝星 + 星星 + 爱心 + "    ";
            // 发送美化后的界面
            cm.sendSimple(text);
        } 
        // 功能处理逻辑（完全1:1保留原所有跳转/发放逻辑）
        else if (status == 1){
            if(selection == 0){
                cm.openNpc(9310022,1); // 学习5转技能NPC跳转
            }else if(selection == 1){
                cm.openNpc(9310022,2); // 豆豆抽奖NPC跳转（原注释，保留逻辑）
            }else if(selection == 2){
                cm.gainBeans(1000000); // GM专属加豆豆（原注释，保留逻辑）
            }
        } else if (status == 2){       
            // 预留二级菜单（完全保留原空逻辑，便于后续扩展）
        }
    }
}