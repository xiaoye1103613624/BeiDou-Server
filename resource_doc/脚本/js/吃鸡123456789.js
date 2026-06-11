//【配置区】-----------------------------------------------------
var config_EventName = "WY_ChiJi"; //事件脚本名
var config_ReadyMapId = 450004450; //吃鸡战场准备地图ID
var config_Channel = 1; //限制频道
var config_MaxNP = 15; //战场最大玩家数量
var config_MaxJoinCount = 10; //单人最大参与次数
//-----------------------------------------------------

var 粉心 = "#fEffect/CharacterEff/1042176/2/0#";
var 红心 = "#fEffect/CharacterEff/1082229/0/0#";
var 粉星 = "#fEffect/CharacterEff/1112926/0/1#";
var 黑皇冠 = "#fUI/GuildMark/Mark/Etc/00009023/14#";
var status = -1; //模组状态
var chr = null;
var say = "";
var em = null;

function start() {
    chr = cm.getPlayer();
    if (chr.getClient().getChannel() != config_Channel) {
        cm.sendOk("抱歉，吃鸡战场仅可在1线进行！");
        cm.dispose();
        return;
    }
    if (chr.getClient().getChannel() != config_Channel) {
        cm.sendOk("抱歉，吃鸡战场仅可在1线进行！");
        cm.dispose();
        return;
    }
    if (!cm.isLeader()) {  // 没有组队if (cm.isLeader()) { 
        cm.sendOk("本活动只允许单人组队,请自行开组,不可组其他玩家");
        cm.dispose();
        return;
    }
 var inMap = cm.partyMembersInMap();
     if (inMap < 1 || inMap > 1) {//判断初始地图 队伍的人数，是否匹配限定人数
        cm.sendOk("本活动只允许单人组队,请自行开组单人组");
        cm.dispose();
        return;
 }
    em = cm.getEventManager(config_EventName);
    if (em == null) {
        cm.sendOk("配置文件不存在，请联系管理员：");
        cm.dispose();
        return;
    }
    action(1, 0, 0);

}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0) {
        say = _getTitle("吃鸡战场");

        if (em.getProperty("state") == "0") {
            say += "      #r#e╭～#n" + 粉星 + "" + 粉星 + "#e～～～～～～～～～～～～～～～╮#n\r\n";
            say += "			#b 吃鸡战场活动尚未开启,请耐心等待\r\n";
            say += "     #r#e╰ ～～～～～～～～～～～～～～～～～～～ ╯#n#l\r\n";
        } else if (em.getProperty("state") == "1") {
            say += "      #e#r╭～～～～～～～～～～～～～～～～～～╮#n\r\n";
            say += "#b\t\t    吃鸡战场全面开启,谁能坚持到最后\r\n";
            say += "#n\t\t\t    #r#L1#加入战场[ " + em.newInstance(config_EventName).getMapInstance(config_ReadyMapId).getCharacters().size() + " / 50 ]#l\r\n\r\n";
            say += "     #r#e╰ ～～～～～～～～～～～～～～～～～～ ╯#n#l\r\n";
        } else {
            say += "      #d#e╭～#n" + 粉星 + "" + 粉星 + "#e～～～～～～～～～～～～～～～╮#n\r\n";
            say += "\t\t#d		 战场已经开启，无法加入！\r\n";
            say += "     #d#e╰ ～～～～～～～～～～～～～～～～～～ ╯#n#l\r\n";
        }

        if (chr.isGM()) {
            say += "      #b#e╭～～～～～～ #nＧ Ｍ 功 能#e ～～～～～～╮#k#n\r\n";
            say += "#d\t\t       ----以下内容管理员可见----\r\n";
            say += "\t\t\t\t     #L666##r开启吃鸡#l\r\n\r\n";
            say += "     #b#e╰ ～～～～～～～～～～～～～～～～～～ ╯#n#l\r\n";
        }

        say += "";
        cm.sendSimpleS(say, 2);
    } else if (status == 1) {
        if (selection == 1) {

            if (em.getProperty("state") == 1) {

                if (em.newInstance(config_EventName).getMapInstance(config_ReadyMapId).getCharacters().size() >= config_MaxNP) {
                    cm.sendOk("#e#b战场最多同时容纳#r" + config_MaxNP + "#b人，你已经无法加入了");
                    cm.dispose();
                    return;
                }

                if (chr.getBossLog("吃鸡战场") >= config_MaxJoinCount) {
                    cm.sendOk("你今天已经参加过吃鸡战场了，无法再次参与。每天可以参与" + config_MaxJoinCount + "次");
                    cm.dispose();
                    return;
                }

                //em.startInstance(chr);
                em.newInstance(config_EventName).registerPlayer(chr);
                //em.setProperty("team","1");
                chr.changeMap(config_ReadyMapId);
                cm.setBossLog1("吃鸡Rank",-cm.getBossLog1("吃鸡Rank"));         
                //cm.warp(config_ReadyMapId, 0);
                cm.getPlayer().dropMessage(1, "活动马上开始，请等候其他玩家入场\r\n本活动不可组其他玩家,避免进不去");
                cm.getPlayer().dispelBuff(5121003);
                cm.dispose();
            } else {
                cm.sendOk("活动尚未开启！或正在进行中！");
                cm.dispose();
            }

        } else if (selection == 666) {
            cm.sendYesNo("#b尊敬的管理员，您想开放 #r<吃鸡战场>#b 活动的入口吗？");
        } else {
            cm.dispose();
        }
    } else if (status == 2) {
        //开放活动
        em.setProperty("state", "1");
        say = "『吃鸡战场』吃鸡战场已经全面开启,请各位前往1线 点击拍卖=>限时活动 <吃鸡活动> 进入";
        cm.全服漂浮喇叭(say, 5122000);
        cm.worldMessage(6, say); //蓝色字公告
        cm.worldMessage(5, say); //红色字公告
        cm.worldMessage(6, say); //蓝色字公告
        cm.worldMessage(5, say); //红色字公告
        cm.worldMessage(6, say); //蓝色字公告
        cm.worldMessage(5, say); //红色字公告
        cm.dispose();
    } else {
        cm.dispose();
    }
}

var ul_cloud = "#fItem/Etc/0403/04031309/info/iconRaw#"; //
function _getTitle(t) {
    return " " + ul_cloud + ul_cloud + ul_cloud + ul_cloud + "#r#e『" + t + "』#k#n" + ul_cloud + ul_cloud + ul_cloud + ul_cloud + "\r\n\r\n";
}


//╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰