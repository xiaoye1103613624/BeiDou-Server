/* 
 * 脚本类型: cm
 * 脚本用途: 积分中介
 * 脚本作者: 故事丶
 * 制作时间: 2014/12/18
 */

var status = -1;
var money1 = 0;
var tosend = 0;
var 小烟花 ="#fMap/MapHelper/weather/squib/squib4/1#";
var 星星 ="#fMap/MapHelper/weather/witch/3#";
var 彩虹 ="#fEffect/ItemEff/1071085/effect/walk1/2#";
var 中条猫 ="#fUI/ChatBalloon/37/n#";
var 闪星 = "#fEffect/CharacterEff/1114000/2/0#";
var sl;
var mats;
var dds;
function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            if (status == 0) {
                cm.sendNext("如果需要积分中介服务在来找我吧！");
                cm.dispose();
            }
            status--;
        }
        if (status == 0) {	
            var gsjb = "";
            gsjb += "#b冒险岛一条龙服务#k★#r定制各版单机+商业端#k★#d免费收徒教技术\r\n\r\n";
			gsjb += "\t\t\t"+彩虹+"#r#e赞 助 系 统\t"+彩虹+" #k#n"+小烟花 +"\r\n";
			gsjb += ""+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+"\r\n\r\n";
            //gsjb += "     #b你拥有：#r#v2022524#*#c2022524##b张        #r#v2432407#*#c2432407##b个\r\n\r\n     #b赞助积分:#r"+剩余积分(cm.getPlayer().getId())+"#b点        累计积分:#r"+累计充值积分(cm.getPlayer().getId())+"#b点\r\n\r\n\r\n";
			gsjb += "         #L1##r#e#v2022524#兑换积分  #n#b(1张：1赞助 + 1累计)#l\r\n\r\n\r\n";
			//gsjb += "         #L2##r#e#v2432407#兑换积分  #n#b(1个：1累计积分)#l\r\n\r\n\r\n";
            cm.sendSimple(gsjb);
        } else if (status == 1) {
            if (cm.getPlayer() >= 5 && cm.getPlayer() <= 5) {
                cm.sendOk("GM不能参与兑换。");
                cm.dispose();
            }
            if (selection == 1) {
                if (cm.haveItem(2022524) == 0) {
                    cm.sendNext("#r你的背包内没有足够的#v2022524#，不能进行兑换！");
                    status = -1;
                } else {
                    money1 = 1;
					cm.sendGetNumber("#r请输入兑换积分所要使用#v2022524#的数量:\r\n#b当前拥有#v2022524#的数量为：#r#c2022524##b张\r\n", 1, 1, 10000);
                }
            }
			else if (selection == 2) {
                if (cm.haveItem(2432407) == 0) {
                    cm.sendNext("#r你的背包内没有足够的#v2432407#，不能进行兑换！");
                    status = -1;
                } else {
                    money1 = 2;
					cm.sendGetNumber("#r请输入兑换积分所要使用#v2432407#的数量:\r\n#b当前拥有#v2432407#的数量为：#r#c2432407##b张\r\n", 1, 1, 10000);
                }
            }
															
        } else if (status == 2) {
           if (money1 == 1) {
                 if (cm.haveItem(2022524, selection)) {
                    cm.gainItem(2022524, -selection);
					//cm.gainNX(selection);
					增加剩余积分(cm.getPlayer().getId(),(selection));//剩余积分
					增加累计积分(cm.getPlayer().getId(),(selection));//累计积分
                    cm.sendOk("#r你已成功使用"+selection+"张#v2022524#兑换了"+selection+"赞助积分和"+selection+"累计积分！");
					Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(12, cm.getClient().getChannel(), "赞助系统" + " : " + "恭喜『" + cm.getChar().getName() + "』成功兑换了"+selection+"赞助积分和"+selection+"累计积分！！！！！『" + cm.getChar().getName() + "』的介绍人或师父别忘了去找老G拿福利哦！！"));
                } else {
                    cm.sendNext("#r你输入的数量大于你拥有的数量，请重新操作！");
                    cm.dispose();
                }
				
            }
			else if (money1 == 2) {
                 if (cm.haveItem(2432407, selection)) {
                    cm.gainItem(2432407, -selection);
					//cm.gainNX(selection);
					//增加剩余积分(cm.getPlayer().getId(),(selection));//剩余积分
					增加累计积分(cm.getPlayer().getId(),(selection));//累计积分
                    cm.sendOk("#r你已成功使用"+selection+"张#v2432407#兑换了"+selection+"累计积分！");
					Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(12, cm.getClient().getChannel(), "累计积分" + " : " + "恭喜『" + cm.getChar().getName() + "』成功兑换了"+selection+"累计积分！！！！！"));
                } else {
                    cm.sendNext("#r你输入的数量大于你拥有的数量，请重新操作！");
                    cm.dispose();
                }
            }
        } else {
            cm.dispose();
        }
    }
}


function 累计充值积分(a){
var sql ="SELECT characterid,ljjf FROM paymoney";
var rs =cm.sql_Select(sql);	
var 数值 =0;
var 判定 =true;
for(var i=0;i<rs.size();i++){
	if(rs[i].get("characterid")==a){
	   	数值 =rs[i].get("ljjf");
		判定 =false;
	}
}

if(判定 ==true){
var sql1 ="INSERT INTO paymoney(id,characterid,syjf,ljjf,kydj) VALUE(?,?,?,?,?)";
cm.sql_Insert(sql1,null,a,0,0,0);	
}
return 数值;
}
function 增加累计积分(id,number){
var sql ="UPDATE paymoney SET ljjf = ? WHERE characterid = ?";
cm.sql_Update(sql,(累计充值积分(id)+number),id);	
}

function 剩余积分(a){
var sql ="SELECT characterid,syjf FROM paymoney";
var rs =cm.sql_Select(sql);	
var 数值 =0;
var 判定 =true;
for(var i=0;i<rs.size();i++){
	if(rs[i].get("characterid")==a){
	   	数值 =rs[i].get("syjf");
		判定 =false;
	}
}
if(判定 ==true){
var sql1 ="INSERT INTO paymoney(id,characterid,syjf,ljjf,kydj) VALUE(?,?,?,?,?)";
cm.sql_Insert(sql1,null,a,0,0,0);	
}
return 数值;
}

function 增加剩余积分(id,number){
var sql ="UPDATE paymoney SET syjf = ? WHERE characterid = ?";
cm.sql_Update(sql,(剩余积分(id)+number),id);	
}

