var 蓝加 = "#fUI/Basic.img/BtMax/mouseOver/0#";
var jl = Array(
//累积天数， 物品ID  ，数量
Array(7,2340000,50),
Array(14,2340000,100),
Array(21,2340000,150),
Array(30,2340000,200)

//Array(30,2340000,333)
)
var maxday = 0;
var day = 0;
var qiandao = 0;
var s = 0;
var 最大补签数 = 3;
var 当前补签 = 0;
function start() {
	// cm.sendOk("");
    // cm.dispose();
    // return; player.setxmwnjlc("战力计算", player.getzhanli());

        status = -1;
        action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {

            // cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
			cm.dispose();
            return;
        }
        if (status == 0) {
			maxday = cm.getMaxDayForMonth();
			当前补签 =  cm.getPlayer().getOneTimeLogMcs("补签次数");
			day = cm.getDay();
			qiandao = 0;
			var txt ="\t\t\t\t#r#e#i4001102#白嫖签到系统#i4001102##k\r\n\r\n"
			txt +="\t\t签到说明：#n您本月可以补签[#r"+当前补签 + "#k/#r"+最大补签数+"#k]次\r\n"
		txt +="\t\t#g绿色[当日待签]  "+蓝加+"  #r红色[完成签到]\r\n\t\t#b蓝色[过期漏签]  "+蓝加+"  #k黑色[未到日期]\r\n\r\n"
			for(var i =1; i<=maxday;i++ ){
				var x = cm.getPlayer().getOneTimeLogM("签到"+i);
				if(x > 0){
					txt += "#e#L"+i+"##r"+cm.getString(i,2)+"号#l#n#k"//已签
					qiandao++;
				}else if(i > day){
					txt += "#e#L"+i+"##k"+cm.getString(i,2)+"号#l#n#k"//未到时间
				}else if(i == day){
					txt += "#e#L"+i+"##g"+cm.getString(i,2)+"号#l#n#k"//当日待签到
				}else {
					txt += "#e#L"+i+"##b"+cm.getString(i,2)+"号#l#n#k"//漏签
				}
				if(i % 7 == 0){
					txt +="\r\n\r\n";
				}
			}
			txt +="\r\n\r\n";
			for(var i= 0;i<jl.length;i++){
			 var info = cm.getPlayer().getOneTimeLogM("领取奖励"+jl[i][0]) > 0 ? "#r已领取#k" : "#g未领取#k"
			txt +="#L"+(i+10000)+"#累积签到 #r"+qiandao+"#k/#r"+jl[i][0]+"#k 日  #b奖励 #r"+ jl[i][2] + " #b累计积分 "+info+"#l\r\n\r\n"//"+cm.getString(jl[i][0],2)+"
			
			}
			cm.sendOk(txt);
        } else if (status == 1) {
			s = selection;
			if(s < 10000){
				if(cm.getPlayer().getOneTimeLogM("签到"+s) > 0 ){
					cm.sendOk("\t\t#r#e你已经完成签到")
					status = -1;
					return;
				}
				if(s > day){
					cm.sendOk("\t\t#r#e不能提前签到哦")
					status = -1;
					return;
				}
				if(s < day && 当前补签 <=最大补签数 ){
					cm.sendOk("\t\t#r#e你已用完当月补签次数")
					status = -1;
					return;
				}
				cm.getPlayer().setOneTimeLogM("签到"+s);
				var txt ="\t\t#r#e签到成功";
				if(s<day){
					cm.gainNX(+jl[x][2])
					txt ="\t\t#r#e补签成功";
					cm.getPlayer().gainOneTimeLogMcs("补签次数");
				}
				cm.sendOk(txt);
				status = -1;
				return;
			}else{
				 var x = s -10000;
				 if(cm.getPlayer().getOneTimeLogM("领取奖励"+jl[x][0]) > 0){
					 cm.sendOk("\t\t#r#e你已经领取奖励")
					 status= -1;
					 return;
				 }
				 if(qiandao < jl[x][0]){
					 cm.sendOk("\t\t#r#e你的签到次数不足")
					 status = -1;
					 return;
				 }
				 if(!cm.canHold(jl[x][1])){
					 cm.sendOk("\t\t#r#e背包放不下")
					 cm.dispose();
					 return;
				 }
				 //cm.gainItem(jl[x][1],jl[x][2]);
				 //cm.gainNX(+jl[x][2])
				 cm.getPlayer().setlpjf(cm.getPlayer().getlpjf()+jl[x][2]);
				 cm.getPlayer().setOneTimeLogM("领取奖励"+jl[x][0]);
				 cm.sendOk("\t\t#r#e领取成功");
				 status = -1;
				 
			}
			
			
			
		}
	}
}