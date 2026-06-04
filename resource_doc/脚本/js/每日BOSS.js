var \u56fe\u6807="#fEffect/CharacterEff/1112905/0/1#",JT="#fUI/Basic/BtHide3/mouseOver/0#",\u5fc3="#fUI/GuildMark.img/Mark/Etc/00009001/14#",\u95f9\u949f\u56fe\u6807="#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/0/0#",\u5b8c\u6210="#fUI/UIWindow/Quest/Tab/enabled/2#",\u6b63\u5728\u8fdb\u884c\u4e2d="#fUI/UIWindow/Quest/Tab/enabled/1#",ca=java.util.Calendar.getInstance(),year=ca.get(java.util.Calendar.YEAR),month=ca.get(java.util.Calendar.MONTH)+1,day=ca.get(java.util.Calendar.DATE),hour=ca.get(java.util.Calendar.HOUR_OF_DAY),
minute=ca.get(java.util.Calendar.MINUTE),second=ca.get(java.util.Calendar.SECOND),weekday=ca.get(java.util.Calendar.DAY_OF_WEEK),itemSet,itemSetQty,hasQty=!1,prizeIdEtc=[4310088,4000313,2049100,2340000,4310098,4000038,4001126],prizeIdEtc1=[4310174,2049124,4310098,4310097,4310156,2049122,2531000,2049104];
var items = [1,2,3,4,5,6,7,8,9,10]
var 任务物品 = Array(
Array(1,4000296,200),
Array(2,4000011,200),
Array(3,4000433,200),
Array(4,4000110,100),
Array(5,4000027,100),
Array(6,4000054,20),
Array(7,4000053,20),
Array(8,4000068,10),
Array(9,4000067,10),
Array(10,4000151,10)
)
var s;
var myDate = new Date();
var year = myDate.getFullYear();
var month = myDate.getMonth() + 1;
var days = myDate.getDate();
var DatabaseConnection = Java.type('database.DatabaseConnection');
function start() {
   status = -1;
   action(1, 0, 0);
}

function action(mode, type, selection) {
	
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    } 
	else if (mode == 0 && selection == -1) {
		cm.dispose();
        return;
	}
    if (mode == 1) {
        status++;
    } 
	else {
        status--;
		cm.dispose();
        return;
    }
    if (status == 0) {
		var text = "\r\n";
		text += "每一次完成后随机奖励:\r\n#v4310088##v4000313##v2049100##v2340000##v4310098##v4000038##v4001126##l\r\n#k";
		text += "完成十次全部收集后奖励:#v4310174#*2#v4031138#500万\r\n额外随机奖励:#v4310174##v2049124##v4310097##v4310098##v4310156##v2049122##v2531000##v2049104##l\r\n#k";
		var ss =true;
		for(var i=0;i<items.length;i++){
			var dj = items[i];
			if(dj >= items[i]){
				var c = getBossLog1("收集任务A"+items[i]);
				if(c <1 && ss){
					text +="\t\t#L"+i+"#"+图标+"#b收集任务(#rLv."+items[i]+"#b)"+正在进行中+"#k#l\r\n\r\n"
					ss = false;
				}else if(c >0){
					text +="\t\t   "+图标+"#b收集任务(#rLv."+items[i]+"#b)"+完成+"#k\r\n"
				}
			}
			else{
				text +="\t\t   "+图标+"#b收集任务(#rLv."+items[i]+"可开始#b)"+图标+"#k\r\n"
			}
		}
		cm.sendOk(text);
		if(ss){
			cm.dispose()
			return
		}
    } 
	else if (status == 1) {
		s = selection
        var dj =cm.getPlayer().getLevel()
		var text = "\r\n";
		text +="#r任务说明:\r\n\r\n"
		for(var i=0;i<任务物品.length;i++){
			if(任务物品[i][0] == items[s]){
				text +="#k需要收集#b#v"+任务物品[i][1]+"##t"+任务物品[i][1]+"# * #r"+任务物品[i][2]+" #b个  #k当前收集: #r#c"+任务物品[i][1]+"# #b个#k\r\n"
			}
		}
		cm.sendNext(text)
    }
	else if(status ==2){
		var next = false;
		for(var i=0;i<任务物品.length;i++){
			var iii = 任务物品[i]
			if(iii[0] == items[s]){
				if(!cm.haveItem(iii[1],iii[2])){
					next = true;
					break;
				}
			}
		}
		if(next){
			cm.sendOk("你的任务收集点材料准备的不充分,请检查")
			cm.dispose()
			return
		}
		for(var i=0;i<任务物品.length;i++){
			var iii = 任务物品[i]
			if(iii[0] == items[s]){
				cm.gainItem(iii[1],-iii[2])
			}
		}
		if(getBossLog1("收集任务A9")!=1){
				itemSet=prizeIdEtc;var sel=Math.floor(Math.random()*itemSet.length),qty=1;cm.gainItem(itemSet[sel],qty);
		}
		else{
			itemSet=prizeIdEtc1;var sel=Math.floor(Math.random()*itemSet.length),qty=1;cm.gainItem(itemSet[sel],qty);
			cm.gainMeso(5000000);
			cm.gainItem(4310174, 2);
		}
		setBossLog1("收集任务A"+items[s])
		cm.getPlayer().指定喇叭("高质地喇叭", "系统公告", "恭喜[" + cm.getPlayer().getName() + "]完成了收集任务第"+items[s]+"个获得了丰厚的奖励!");
		cm.sendOk("恭喜你完成了收集任务第"+items[s]+"个获得了丰厚的奖励")
		status = -1;
	}
}

function setBossLog1(log) {
	var id = cm.getPlayer().getId();
    var con1 = DatabaseConnection.getConnection();
	var day = ""+year+"-"+month+"-"+days+"";
    var ps = con1.prepareStatement("insert into bosslog1 (characterid, bossid, count, time) values (?,?,?,?)");
    ps.setInt(1, id);
    ps.setString(2, log);
	ps.setInt(3, 1);
	ps.setString(4, day);
    ps.executeUpdate();
    ps.close();
}

function getBossLog1(log) {
		var id = cm.getPlayer().getId();
        var con = DatabaseConnection.getConnection();
        var count = 0;
        var ps;
        ps = con.prepareStatement("SELECT * FROM bosslog1 WHERE characterid = ? and bossid = ? and time = CURDATE()");
        ps.setInt(1, id);
		ps.setString(2, log);
        var rs = ps.executeQuery();
        if (rs.next()) {
            count = rs.getInt("count");
        } else {
            count = 0;
        }
        rs.close();
        ps.close();
        return count;
}





