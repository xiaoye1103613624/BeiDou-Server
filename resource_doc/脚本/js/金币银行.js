

var mi0 = "┏━━━━━━━━━━━┓";
var mi1 = "┃     - kaixinMS -     ┃";
var mi2 = "┃ 脚本仿制  　定制脚本 ┃";
var mi3 = "┃ 技术支持 　 游戏顾问 ┃";
var mi4 = "┃ ＷＺ添加　  地图制作 ┃";
var mi5 = "┣━━━━━━━━━━━┫";
var mi6 = "┃　唯一QQ:526703257    ┃";
var mi7 = "┗━━━━━━━━━━━┛";

var kaixin = {        
 手续费: 2, 容量: 100
};







var 表情高兴 = "#fUI/GuildBBS/GuildBBS/Emoticon/Basic/2#";
var status = -1;
var selection;
var 彩虹 ="#fEffect/ItemEff/1071085/effect/walk1/2#";
var 积分 = new Array(1,2);
var 随机积分 = 积分[Math.floor(Math.random() * 积分.length)];
var 小金币 = "#fUI/UIWindow.img/Item/BtCoin/normal/0#";
var xmcz = 0;
var xmyhjb = 0;
var xmbbjb = 0;
function start() {
    
	status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else if (mode == 0) {
        status--;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
		//cm.getPlayer().setxmjbck(100000000000);
		xmyhjb = cm.getPlayer().getxmjbck();
		xmbbjb = cm.getMeso();
		
		
		
		
    var text = "";	
	    text += "                "+表情高兴+" #e#b"+cm.serverName()+"银行#k#n "+表情高兴+"   \r\n\r\n";
		// 测试 = 2000;
		// text += "测试 "+ (测试 / 100 * kaixin.手续费) +" \r\n"
		
		text += "保管金币收取："+kaixin.手续费+" %手续费 [容量："+kaixin.容量+" 亿]\r\n"
		text += ""+小金币+"银行存款："+splitK(xmyhjb)+"\r\n"
		text += ""+小金币+"背包金币："+splitK(xmbbjb)+"\r\n"
        
		text += "#b#L0#[存入金币]#l #L1#[取出金币]#l #L3#[返回大仓]#l \r\n"//#L2#[银行扩容]#l

		
		cm.sendSimple(text);
        
    } else if (status == 1) {
         if (selection == 0) {
			xmcz = 0;
			cm.sendGetNumber("请输入存入的金额\r\n",1000000,1000000,xmbbjb);
	     } else if (selection == 1) {
			xmcz = 1;
			jbxs = 2100000000-xmbbjb;
			cm.sendGetNumber("请问你要取出多少？",1000000,1000000,jbxs);	 
		 } else if (selection == 3) {
			cm.dispose();
			cm.openNpc(9900004,"仓库");
			//cm.sendOk("功能尚未完善..." ); 
			//status = -1;
		 }
		
    } else if (status == 2) {
		xmcza = selection;
		xmyhjb = cm.getPlayer().getxmjbck();
		
		if (xmcz == 0) {
			ckrl = kaixin.容量*100000000;
			if (xmcza > xmbbjb) {
				cm.sendOk("抱歉，你输入的金额不正确..." );
			} else if ((xmcza+xmyhjb) > kaixin.容量*100000000) {
				cm.sendOk("我勒个去，你的钱也太多了吧，我这里最高只能存"+kaixin.容量+"E" );
			} else {
			
			
				
			zzcrjb = parseInt((xmcza * (100 - kaixin.手续费) /100));
			kcsxfy = parseInt(xmcza / 100 * kaixin.手续费);
			
			cm.gainMeso(-xmcza);
			cm.getPlayer().setxmjbck(xmyhjb+zzcrjb);
			
			cm.getPlayer().saveToDB(false,false);//存档
			//cm.dispose();
			取服务器时间 = Packages.tools.FileoutputUtil.CurrentReadable_Time();
			输出记录 = "存入金币："+zzcrjb+"";
			Packages.tools.FileoutputUtil.packetLog("log\\玩家相关\\冒险银行记录.log",取服务器时间+" "+cm.getName()+" ["+输出记录+"]\r\n");
			写入记录("存入",zzcrjb);
			cm.sendOk("存款信息- - - - - \r\n输入金额："+xmcza+" 金币\r\n实际存入："+zzcrjb+" 金币\r\n手续费："+kcsxfy+" 金币" );
			}
			
			status = -1;
		} else if (xmcz == 1) {
			if (xmcza > xmyhjb) {
				cm.sendOk("你的存折里没钱了吧？" );
			} else {
			cm.gainMeso(+xmcza);
			cm.getPlayer().setxmjbck(xmyhjb-xmcza);
					
			cm.getPlayer().saveToDB(false,false);//存档
			取服务器时间 = Packages.tools.FileoutputUtil.CurrentReadable_Time();
			输出记录 = "取出金币："+xmcza+"";
			Packages.tools.FileoutputUtil.packetLog("log\\玩家相关\\冒险银行记录.log",取服务器时间+" "+cm.getName()+" ["+输出记录+"]\r\n");
			写入记录("取出",xmcza);
			cm.sendOk("取出："+xmcza+"金币" ); 	
			}
			
			
			status = -1;
		}
		
		
		

}
}

//取千位分割
function splitK(num) {
  var decimal = String(num).split('.')[1] || '';//小数部分
  var tempArr = [];
  var revNumArr = String(num).split('.')[0].split("").reverse();//倒序
  for (i in revNumArr){
    tempArr.push(revNumArr[i]);
    if((i+1)%3 === 0 && i != revNumArr.length-1){
      tempArr.push(',');
    }
  }
  var zs = tempArr.reverse().join('');//整数部分
  return decimal?zs+'.'+decimal:zs;
}

function 写入记录(type,money) {
	var conn = cm.getConnection();//  
	var sql = "insert into xmyhlog (acczh,chaname,chaid,type,money) values (?,?,?,?,?);";            
    var psu = conn.prepareStatement(sql);
	psu.setString(1,cm.getPlayer().getClient().getAccountName());
	psu.setString(2,cm.getName());
	psu.setString(3,cm.getPlayer().getId());
	psu.setString(4,type);
	psu.setString(5,money);
    psu.executeUpdate();	
	psu.close();
	conn.close();
}


