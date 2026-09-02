/* 
 * 作    者: 84266386
 * 脚本用途: 赞助中心
 * 更新时间: 2020.9.15
 */


var ca = java.util.Calendar.getInstance();
var 年 = ca.get(java.util.Calendar.YEAR); //获得年份
var 月 = ca.get(java.util.Calendar.MONTH) + 1; //获得月份
var 日 = ca.get(java.util.Calendar.DATE);//获取日
var 时 = ca.get(java.util.Calendar.HOUR_OF_DAY); //获得小时
var 分 = ca.get(java.util.Calendar.MINUTE);//获得分钟
var 秒 = ca.get(java.util.Calendar.SECOND); //获得秒
var weekday = ca.get(java.util.Calendar.DAY_OF_WEEK);

var status = -1;
var beauty = 0;
var tosend = 0;
var sl;
var mats;

var 熊1 = "#fUI/GuildMark/Mark/Animal/00002011/6#";
var 熊2 = "#fUI/GuildMark/Mark/Animal/00002011/7#";
var 熊3 = "#fUI/GuildMark/Mark/Animal/00002011/8#";
var 熊4 = "#fUI/GuildMark/Mark/Animal/00002011/9#";
var 熊5 = "#fUI/GuildMark/Mark/Animal/00002011/5#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 小蓝星 = "#fEffect/CharacterEff/1112925/0/0#"; //蓝星
var 警报灯 = "#fUI/StatusBar/BtClaim/normal/0#";
var 金币图标 = "#fUI/UIWindow.img/Item/BtCoin/normal/0#";
var 点卷图标 = "#fUI/CashShop/CashItem/0#";
var t2 = "#fEffect/CharacterEff/1112902/0/0#";
var 星星 ="#fMap/MapHelper/weather/witch/3#";

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
                cm.sendNext("如果需要点卷中介服务在来找我吧。");
                cm.dispose();
            }
            status--;
        }
		

		
		
        if (status == 0) {
			比例 = 500;
			if (日 == 52) {
				比例 = 1000;
				比例说明 = "#e#r惊喜福利当前充值可获得 1:1000 比例【限时活动中】#n\r\n";
			} else {
				比例 = 2000;
				比例说明 = "";
			}
			//赞助点 = getzanzhu();
			余额 = getmoneyb();
			元宝 = getmoneyb();
            累计充值 = getmoney();
			点卷 = cm.getChar().getCSPoints(1);
			
			获取点卷 = 余额*比例;
            var text = "";
            text += "\t\t\t       "+ 星星 +"#e#d自助中心#k#n"+ 星星 + "#l\r\n\r\n"; 
			text += "#d[本服为良心公益服,赞助均用于服务器运营]\r\n"			
			text += "#r赞助比例 1:"+比例+" 1元="+比例+"点卷 #k\r\n"	
			text += ""+点卷图标+"赞助余额："+累计充值+"              "+点卷图标+"当前赞助："+充值点()+"\r\n"
			text += ""+点卷图标+"点卷余额："+点卷+"            "+点卷图标+"元宝余额："+余额+" \r\n"
			
			if(充值点()>0){
			text += "\r\n#e"+点卷图标+"您有:"+充值点()+" 赞助可以提现成元宝哦！#n\r\n"	
			text += "#L999##e- - - <<<赞助提现元宝>>> - - -#l#k\r\n\r\n"
			}
			if (余额 > 0) {
				text += "#e#b"+点卷图标+"您有:"+元宝+" 元宝可以兑换点卷哦！#n\r\n"
				//text += "#L4##e#b- - - <<<元宝全部兑换点卷>>> - - -#l\r\n"
				text += "#L2##e#b- - - <<<元宝兑换点卷>>> - - -#l\r\n"
			} 
			//text += "#L5##e#r- - - <<<每日充值礼包>>> - - -#l\r\n"
			text += "#L3##e#r- - - <<<查看赞助礼包>>> - - -#l\r\n"
			
			text += "#L33##e#r- - - <<<赞助礼包福利>>> - - -#l\r\n"	
            //text += "#L0##r#r- - - <<<点击自助赞助>>> - - -#l\r\n"			
			
			cm.sendSimple(text);
 
        } else if (status == 1) {
			sele1 = selection;
			if(selection ==999){
				var msg = "";
				msg += "1赞助点 = 1元宝 当前赞助点："+充值点()+"\r\n"
				msg += "请输入赞助点数量兑换元宝\r\n"
				cm.sendGetNumber(msg, 1,1, 充值点());
				
				
			}else if (selection == 0) { //自助充值接口
                    cm.openWeb("http://811zf.com/Pay/5b8c39815c0f60d7d4020ea6f32e387c"); //打开网页
					cm.playerMessage(5, "已经为您打开自助充值网站,如果没有弹出网站请联系GM充值");
					cm.dispose();
            } else if (selection == 1) {
				余额 = cm.getHyPay(1);
				if (余额 >= 1) {
					cm.写入推广值(余额);
					cm.addHyPay(余额);
					//cm.gainmoney(-余额);
					cm.gain(-余额);
					cm.worldMessage(9, cm.getClient().getChannel(),"【赞助中心】" + " : " + " [" + cm.getPlayer().getName() + "] 对本服鼎力支持！ 获得了："+余额+"元宝！推广人获得10%奖励！", true);
					cm.dispose();
				} else {
					cm.sendOk("抱歉，你没有充值！");
					cm.dispose();
				
				}
			} else if (selection == 2) {
				var msg = "";
				msg += "1元宝 = "+比例+"点卷 当前元宝："+元宝+"\r\n"
				msg += "请输入元宝数量兑换点卷\r\n"
				cm.sendGetNumber(msg, 1,1, 9999);
			} else if (selection == 3) {
				cm.dispose();
			    cm.openNpc(9900004,2102);
			} 
			else if (selection == 33) {
				cm.dispose();
			    cm.openNpc(9900004,70);
			}
			else if (selection == 4) {	
                                      
				if (余额 >= 1) {
					setmoneyb(0);
					cm.gainNX(获取点卷);	
                    cm.getPlayer().setBossLog("每日充值",1,+余额*比例);			
					//cm.worldMessage(9, cm.getClient().getChannel(),"【赞助中心】" + " : " + " [" + cm.getPlayer().getName() + "] 对本服鼎力支持！ 获得了："+余额+"元宝！推广人获得10%奖励！", true);
					cm.dispose();
				} else {
					cm.sendOk("抱歉，你没有赞助！");
					cm.dispose();
				
				}
			} else if (selection == 5) {				
				 cm.dispose();
			     cm.openNpc(9900004,184816);
							 
			} else if (selection == 9) { //易宝兑换
                    cm.dispose();
			        cm.openNpc(9900004,27);
			} else {
				//cm.sendOk("未进行然后操作哟...");
				cm.dispose();
			}
				 							 
		 

        
        } else if (status == 2) {
			
			sele2 = selection;
			
            if (sele1 == 0) {
	
			} else	if (sele1 == 1) {
			} else	if (sele1 == 2) {
				if (元宝 >= sele2) {
					cm.setmoneyb(-sele2);
					cm.gainNX(sele2*比例);
					cm.sendOk("兑换成功");
                    cm.dispose();
				} else {
					cm.sendOk("元宝不足无法兑换");
                    cm.dispose();
				}
				
			

			
			}else if (sele1 == 999) {
				if (充值点() >= sele2) {
					cm.setmoneyb(sele2);
					扣除充值点(sele2);
					if(推广人()!=0){
					cm.sendOk("提现成功");	
					推广人获得点卷(sele2*500);
					}else {
					cm.sendOk("提现成功");	
					}
					//cm.gainNX(sele2*比例);
					
                    cm.dispose();
				} else {
					cm.sendOk("赞助点不足无法兑换");
                    cm.dispose();
				}
				
			

			
			} else {
				
			}	
        
			

            status = -1;
        } else {
            cm.dispose();
        }
    }
}



/*function 充值点(){
	var 角色id = cm.getPlayer().getId();
	var 数值 =0;
	var 数值1 =1;
	var conn = Packages.database.DatabaseConnection.getConnection();
	var sql = "SELECT ownid,jf FROM chongzhidian WHERE ownid = "+角色id+"   ;";
	var pstmt = conn.prepareStatement(sql);
	var result = pstmt.executeQuery();
	if (result.next()) {
	数值 = result.getInt("jf");
	数值1=0;
	}
	result.close();
	pstmt.close();
	if(数值1==1){
	var conn = Packages.database.DatabaseConnection.getConnection();
	var sql = "INSERT INTO chongzhidian(id,ownid,jf) value(null,"+角色id+",0)";
	var pstmt = conn.prepareStatement(sql);
	pstmt.executeUpdate();
	pstmt.close();
	}
	return 数值;	
	
}*/

function 推广人(){
	var 角色id = cm.getPlayer().getId();
	var 数值 =0;
	var 数值1 =1;
	var conn = Packages.database.DatabaseConnection.getConnection();
	var sql = "SELECT ownid,objecid FROM invsystem WHERE ownid = "+角色id+"";
	var pstmt = conn.prepareStatement(sql);
	var result = pstmt.executeQuery();
	if (result.next()) {
	数值 = result.getInt("objecid");
	数值1= 0;
	}
	result.close();
	pstmt.close();
	if(数值1==1){
	var conn = Packages.database.DatabaseConnection.getConnection();
	var sql = "INSERT INTO invsystem(id,ownid,objecid) value(null,"+角色id+",0)";
	var pstmt = conn.prepareStatement(sql);
	pstmt.executeUpdate();
	pstmt.close();
	}
	return 数值;	
	
}


/*function 扣除充值点(uit) {
	var uid = cm.getPlayer().getId();
    var conn = Packages.database.DatabaseConnection.getConnection();
	var sql = "UPDATE chongzhidian SET jf = jf-"+uit+"  WHERE ownid = "+uid+"";
    var pstmt = conn.prepareStatement(sql);
	pstmt.executeUpdate();
	pstmt.close();
}*/

function 推广人获得点卷(uit) {
	var uid = 推广人();
    var conn = Packages.database.DatabaseConnection.getConnection();
	var sql = "UPDATE tuiguangjf SET dj = dj+"+uit+"  WHERE ownid = "+uid+"";
    var pstmt = conn.prepareStatement(sql);
	pstmt.executeUpdate();
	pstmt.close();
}















//V95端的充值读取
function getmoney() {
	accid = cm.getPlayer().getAccountID();
	xmfhz = 0;
	var conn = Packages.database.DatabaseConnection.getConnection();
	var sql = "SELECT * FROM accounts WHERE id = "+accid+"   ;";
	var pstmt = conn.prepareStatement(sql);
	var result = pstmt.executeQuery();
	if (result.next()) {
	xmfhz = result.getString("money");
	}
	result.close();
	pstmt.close();	
	return xmfhz;
}

function setmoney(xiezhi) {
	accid = cm.getPlayer().getAccountID();
    var conn = Packages.database.DatabaseConnection.getConnection();
	var sql = "UPDATE accounts SET money = "+xiezhi+"  WHERE id = "+accid+"  ;";
    var pstmt = conn.prepareStatement(sql);
	pstmt.executeUpdate();
	pstmt.close();
}

function gainmoney(xiezhi) {
	accid = cm.getPlayer().getAccountID();
    var conn = Packages.database.DatabaseConnection.getConnection();
	var sql = "UPDATE accounts SET money = money+"+xiezhi+"  WHERE id = "+accid+"  ;";
    var pstmt = conn.prepareStatement(sql);
	pstmt.executeUpdate();
	pstmt.close();
}


function getmoneyb() {
	accid = cm.getPlayer().getAccountID();
	xmfhz = 0;
	var conn = Packages.database.DatabaseConnection.getConnection();
	var sql = "SELECT * FROM accounts WHERE id = "+accid+"   ;";
	var pstmt = conn.prepareStatement(sql);
	var result = pstmt.executeQuery();
	if (result.next()) {
	xmfhz = result.getString("moneyb");
	}
	result.close();
	pstmt.close();	
	return xmfhz;
}
function setmoneyb(xiezhi) {
	accid = cm.getPlayer().getAccountID();
    var conn = Packages.database.DatabaseConnection.getConnection();
	var sql = "UPDATE accounts SET moneyb = "+xiezhi+"  WHERE id = "+accid+"  ;";
    var pstmt = conn.prepareStatement(sql);
	pstmt.executeUpdate();
	pstmt.close();
}

function 充值点() {
	accid = cm.getPlayer().getAccountID();
	xmfhz = 0;
	var conn = Packages.database.DatabaseConnection.getConnection();
	var sql = "SELECT * FROM accounts WHERE id = "+accid+"   ;";
	var pstmt = conn.prepareStatement(sql);
	var result = pstmt.executeQuery();
	if (result.next()) {
	xmfhz = result.getString("moneyg");
	}
	result.close();
	pstmt.close();	
	return xmfhz;
}
function 扣除充值点(xiezhi) {
	accid = cm.getPlayer().getAccountID();
    var conn = Packages.database.DatabaseConnection.getConnection();
	var sql = "UPDATE accounts SET moneyg = moneyg-"+xiezhi+"  WHERE id = "+accid+"  ;";
    var pstmt = conn.prepareStatement(sql);
	pstmt.executeUpdate();
	pstmt.close();
}





function getmoneyc() {
	accid = cm.getPlayer().getAccountID();
	xmfhz = 0;
	var conn = Packages.database.DatabaseConnection.getConnection();
	var sql = "SELECT * FROM accounts WHERE id = "+accid+"   ;";
	var pstmt = conn.prepareStatement(sql);
	var result = pstmt.executeQuery();
	if (result.next()) {
	xmfhz = result.getString("moneyc");
	}
	result.close();
	pstmt.close();	
	return xmfhz;
}

function gainmoneyc(xiezhi) {
	accid = cm.getPlayer().getAccountID();
    var conn = Packages.database.DatabaseConnection.getConnection();
	var sql = "UPDATE accounts SET moneyc = moneyc+"+xiezhi+"  WHERE id = "+accid+"  ;";
    var pstmt = conn.prepareStatement(sql);
	pstmt.executeUpdate();
	pstmt.close();
}