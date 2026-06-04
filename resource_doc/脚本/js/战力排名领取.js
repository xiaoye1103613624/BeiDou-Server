var status;



function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
     if (mode == -1) {
        cm.dispose();
    } else {
		if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
			var name = JobType(cm.getPlayer().getJob())
			if(name ==""){
				cm.sendOk("你没有职业无法打开")
				cm.dispose()
				return
			}
			var x = cm.getPlayer().getWeekBossLog(name+"战力排名");
			if(x <0){
				cm.sendOk("你上周没有排名")
				cm.dispose()
				return
			}
			if(cm.getPlayer().getWeekBossLog(name+"战力排名领取") >0){
				cm.sendOk("你已经领取过了")
				cm.dispose()
				return
			}
			switch(x){
				case 1:
				cm.gainItem(1122277,10,10,10,10,0,0,10,20, 0,0,0,0,0,0,7*24);
				break
				case 2:
				cm.gainItem(1122278,5,5,5,5,0,0,5,10, 0,0,0,0,0,0,7*24);   
				break
				case 3:
				 cm.gainItem(1112279,3,3,3,3,0,0,3,6, 0,0,0,0,0,0,7*24); 
				break
			}
			cm.getPlayer().setWeekBossLog(name+"战力排名领取",1);
			cm.sendOk("恭喜你领取了排名第"+x+"奖励")
			cm.dispose()
			return
		}
	}
   
    
}
function JobType(job)
{
	 if ( job >= 100 &&  job <= 132) {
           return "战士"
        } else if ( job >= 200 &&  job <= 232) {
            return "魔法师"
        } else if ( job >= 300 &&  job <= 322) {
            return "射手"
        } else if ( job >= 400 &&  job <= 422) {
            return "飞侠"
        } else if ( job >= 500 &&  job <= 522) {
            return "海盗"
        }
		return "";
}