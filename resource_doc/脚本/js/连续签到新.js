var 蓝加 = "#fUI/Basic.img/BtMax/mouseOver/0#";
var jl = Array(
//累积天数， 物品ID  ，数量
Array(7,2340000,30),
Array(14,2340000,60),
Array(21,2340000,90),
Array(30,2340000,120)

//Array(30,2340000,333)
)
var maxday = 0;
var day = 0;
var qiandao = 0;
var s = 0;
var 最大补签数 = 5;
var 当前补签 = 0;


var date = new Date(); // 获取当前日期
var month = date.getMonth() + 1; // 获取当前月份，加1转换为1-12的格式
var 签到记录 =  month + "月签到记录"; //月份
var 补签次数记录 = month + "月补签次数"; // 动态生成键值
var 领取奖励 = month + "月领取奖励"; // 动态生成键值

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
			当前补签 =  cm.getPlayer().getOneTimeLogMcs(补签次数记录);
			day = cm.getDay();
			qiandao = 0;
			var txt =""
		    txt +="\t\t#k[当日待签到]#g绿色  "+蓝加+"  #k[已完成签到]#r红色\r\n\t\t#k[过期忘签到]#b蓝色  "+蓝加+"  #k[剩余签到天]黑色\r\n\r\n"
			txt +=" \t\t当月补签 [#r"+当前补签 + "/"+最大补签数+"#k] 次 [点击下方日期签到]\r\n\r\n"
			for(var i =1; i<=maxday;i++ ){
				var x = cm.getPlayer().getOneTimeLogM(签到记录+i);
				if(x > 0){
					txt += "#L"+i+"##r"+cm.getString(i,2)+"号#l#k"
					qiandao++;
				}else if(i > day){
					txt += "#L"+i+"##k"+cm.getString(i,2)+"号#l#k"
				}else if(i == day){
					txt += "#L"+i+"##g"+cm.getString(i,2)+"号#l#k"
				}else {
					txt += "#L"+i+"##b"+cm.getString(i,2)+"号#l#k"
				}
				if(i % 7 == 0){
					txt +="\r\n";
				}
			}
			txt +="\r\n\r\n";
			for(var i= 0;i<jl.length;i++){
			 var info = cm.getPlayer().getOneTimeLogM(领取奖励+jl[i][0]) > 0 ? "#r已领取#k" : "#b未领取#k"
			//txt +="#L"+(i+10000)+"#累积签到"+cm.getString(jl[i][0],2)+"日奖励 累计积分 x "+ jl[i][2] + "["+qiandao+"/"+jl[i][0]+"] "+info+"#l\r\n"
			txt +="#L"+(i+10000)+"#累积签到 [#r"+qiandao+"/"+jl[i][0]+"#k] 日奖励：#r"+ jl[i][2] + " #b累计积分  #k【"+info+"】#l\r\n"
			}
			cm.sendOk(txt);
        } else if (status == 1) {
			s = selection;
			if(s < 10000){
				if(cm.getPlayer().getOneTimeLogM(签到记录+s) > 0 ){
					cm.sendOk("你当月已经签到了")
					status = -1;
					return;
				}
				if(s > day){
					cm.sendOk("还没有到签到日期")
					status = -1;
					return;
				}
				if(s < day && 当前补签 >=最大补签数 ){
					cm.sendOk("你当月补签的次数已经达到最大了")
					status = -1;
					return;
				}
				cm.getPlayer().setOneTimeLogM(签到记录+s);
				var txt ="签到成功";
				if(s<day){
					txt ="补签成功";
					cm.getPlayer().gainOneTimeLogMcs(补签次数记录);
				}
				cm.sendOk(txt);
				status = -1;
				return;
			}else{
				 var x = s -10000;
				 if(cm.getPlayer().getOneTimeLogM(领取奖励+jl[x][0]) > 0){
					 cm.sendOk("你已经领取过奖励了")
					 status= -1;
					 return;
				 }
				 if(qiandao < jl[x][0]){
					 cm.sendOk("你的签到次数不足")
					 status = -1;
					 return;
				 }
				 if(!cm.canHold(jl[x][1])){
					 cm.sendOk("背包放不下了")
					 cm.dispose();
					 return;
				 }
				 //cm.gainItem(jl[x][1],jl[x][2]);
				 //cm.gainNX(+jl[x][2])
				 cm.getPlayer().setlpjf(cm.getPlayer().getlpjf()+jl[x][2]);
				 cm.getPlayer().setOneTimeLogM(领取奖励+jl[x][0]);
				 cm.sendOk("领取成功");
				 status = -1;
				 
			}
			
			
			
		}
	}
}