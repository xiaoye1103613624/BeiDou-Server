//[配置区]--------------------------------------------------------------
var chance = Math.random()*188;
var cfHours = [
	0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24
];
var taskReward = [
    //任务需求物品ID，任务需求物品数量
	[4000444,chance],//"绿色衣襟"    
	[4000446,chance],//"笑脸面具"    
	[4000445,chance],//"绿色高帽"    
	[4000447,chance],//"绿色头盔"    
	[4000448,chance],//"绿色心脏"    
	[4000449,chance],//"蓝色衣襟"    
	[4000451,chance],//"无表情面具"  
	[4000450,chance],//"蓝色高帽"    
	[4000452,chance],//"蓝色头盔"    
	[4000453,chance],//"蓝色心脏"    
	[4000454,chance],//"红色衣襟"    
	[4000456,chance],//"哭脸面具"    
	[4000455,chance],//"红色高帽"    
	[4000457,chance],//"红色头盔"    
	[4000458,chance],//"红色心脏"    
	[4000407,chance],//"铜人心"      
	[4000401,chance],//"铜人念珠"    
	[4000402,chance],//"银人心"      
	[4000405,chance],//"银人钝器"    
	[4000406,chance],//"金人心"      
	[4000404,chance],//"金人钝器"    
	[4000274,chance],//"断裂的角"    
	[4000273,chance],//"陈年老骨头"  
	[4000271,chance],//"破损的巢穴"  
	[4000272,chance],//"蛋壳碎片"    
	[4000429,chance],//"玻璃汗珠"    
	[4000430,chance],//"怪虫迪波之角"
	[4000431,chance],//"蚯蚓之尾"    
	[4000433,chance],//"油罐"        
	[4000432,chance],//"青苔岩石"    
	[4000434,chance],//"大花草" 
	[4000152,chance],//"黑甲凶灵的袖标"   
	[4000151,chance],//"时间门神的袖标"   
	[4000128,chance],//"黄小丑的帽子"     
	[4000129,chance],//"红小丑的小珠"     
	[4000130,chance],//"时之鬼兵的挂钟"   
	[4000131,chance],//"时之鬼将的石片"   
	[4000132,chance],//"蓝帽海贼的舵"     
	[4000133,chance],//"绿帽海贼的发动机" 
	[4000134,chance],//"大海贼的帆"       
	[4000135,chance],//"大海贼王的帽子"   
	[4000143,chance],//"僵尸娃娃"         
	[4000144,chance],//"自由灵魂"         
	[4000145,chance],//"被封印的挂钟"     
	[4000146,chance],//"死恶的灵魂"       
	[4000147,chance],//"被封印的僵尸娃娃" 
	[4000148,chance],//"束缚之手"         
	[4000149,chance],//"被封印的缸子"     
	[4000150,chance],//"时间鬼王的冰块"   
	[4000234,chance],//"半人马的骨头"     
	[4000232,chance],//"半人马的火花"     
	[4000233,chance],//"半人马的净水"     
	[4000268,chance],//"飞龙的翅膀"       
	[4000269,chance],//"飞龙的腮"         
	[4000270,chance],//"飞龙的指甲"       
	[4000051,chance],//"野狼之尾"         
	[4000053,chance],//"狼人脚趾甲"       
	[4000054,chance],//"白狼人脚趾甲"  
	[4000035,chance],//"桌布"              
	[4000036,chance],//"奇妙的药"          
	[4000026,chance],//"猴子娃娃"          
	[4000031,chance],//"诅咒娃娃"          
	[4000043,chance],//"红螃蟹钳"          
	[4000044,chance],//"青螃蟹钳"          
	[4000007,chance],//"火独眼兽之尾"      
	[4000024,chance],//"火野猪尖牙"        
	[4000178,chance],//"钢甲猪盔甲"        
	[4000039,chance],//"铁甲猪蹄"          
	[4000013,chance],//"风独眼兽之尾"      
	[4000023,chance],//"冰独眼兽之尾"      
	[4000059,chance],//"星光精灵的星块"    
	[4000060,chance],//"月光精灵的月块"    
	[4000061,chance],//"日光精灵的日块"    
	[4000073,chance],//"独角狮硬角"        
	[4000021,chance],//"动物皮"            
	[4003004,chance],//"粗羽毛"            
	[4000070,chance],//"红独角狮尾"        
	[4000071,chance],//"黄独角狮尾"        
	[4000072,chance],//"蓝独角狮尾"        
	[4000058,chance],//"食人花的种子"      
	[4000062,chance],//"黑食人花的种子"    
	[4000048,chance],//"小白雪人皮"        
	[4000086,chance],//"小白雪鬼的毛团"    
	[4000087,chance],//"小黑雪鬼的毛团"    
	[4000088,chance],//"小企鹅王的鱼"      
	[4000106,chance],//"玩具熊猫的棉花团"  
	[4000107,chance],//"玩具熊猫的黄色丝带"
	[4000108,chance],//"熊猫娃娃"          
	[4000109,chance],//"玩具小鸭"          
	[4000110,chance],//"木马骑兵的剑"      
	[4003005,chance],//"柔软羽毛" 
	[4000019,chance],//"绿色蜗牛壳"  
	[4000000,chance],//"蓝色蜗牛壳"  
	[4000016,chance],//"红色蜗牛壳"  
	[4000004,chance],//"绿液球"      
	[4000010,chance],//"绿水灵珠"    
	[4000011,chance],//"蘑菇芽孢"    
	[4000001,chance],//"花蘑菇盖"    
	[4000015,chance],//"刺蘑菇盖"    
	[4000012,chance],//"绿蘑菇盖"    
	[4000009,chance],//"蓝蘑菇盖"    
	[4000003,chance],//"树枝"        
	[4000005,chance],//"叶子"        
	[4000018,chance],//"木块"        
	[4000002,chance],//"蝴蝶结"      
	[4000017,chance],//"猪头"        
	[4000006,chance],//"三眼章鱼触角"
	[4000020,chance],//"野猪尖牙"    
	[4000188,chance],//"鸭蛋"        
	[4000252,chance],//"鸡肉"        
	[4000253,chance],//"白色鸡蛋"    
	[4000187,chance],//"鸡爪"        
	[4000189,chance],//"羊毛"        
	[4000190,chance],//"山羊角"      
	[4000191,chance],//"黑山羊角"    
	[4000042,chance] //"蝙蝠翅膀"    
	// [4000051,chance],//"野狼之尾"//0-40级活动材料         
	// [4000053,chance],//"狼人脚趾甲"       
	// [4000035,chance],//"桌布"                   
	// [4000026,chance],//"猴子娃娃"          
	// [4000031,chance],//"诅咒娃娃"          
	// [4000043,chance],//"红螃蟹钳"          
	// [4000044,chance],//"青螃蟹钳"          
	// [4000007,chance],//"火独眼兽之尾"      
	// [4000024,chance],//"火野猪尖牙"          
	// [4000013,chance],//"风独眼兽之尾"      
	// [4000023,chance],//"冰独眼兽之尾"      
	// [4000059,chance],//"星光精灵的星块"    
	// [4000060,chance],//"月光精灵的月块"    
	// [4000073,chance],//"独角狮硬角"        
	// [4000021,chance],//"动物皮"            
	// [4003004,chance],//"粗羽毛"            
	// [4000070,chance],//"红独角狮尾"        
	// [4000071,chance],//"黄独角狮尾"        
	// [4000072,chance],//"蓝独角狮尾"         
	// [4000048,chance],//"小白雪人皮"        
	// [4000086,chance],//"小白雪鬼的毛团"    
	// [4000087,chance],//"小黑雪鬼的毛团"    
	// [4000088,chance],//"小企鹅王的鱼"      
	// [4000106,chance],//"玩具熊猫的棉花团"  
	// [4000107,chance],//"玩具熊猫的黄色丝带"
	// [4000108,chance],//"熊猫娃娃"          
	// [4000109,chance],//"玩具小鸭"          
	// [4000110,chance],//"木马骑兵的剑"      
	// [4003005,chance],//"柔软羽毛" 
	// [4000019,chance],//"绿色蜗牛壳"  
	// [4000000,chance],//"蓝色蜗牛壳"  
	// [4000016,chance],//"红色蜗牛壳"  
	// [4000004,chance],//"绿液球"      
	// [4000010,chance],//"绿水灵珠"    
	// [4000011,chance],//"蘑菇芽孢"    
	// [4000001,chance],//"花蘑菇盖"    
	// [4000015,chance],//"刺蘑菇盖"    
	// [4000012,chance],//"绿蘑菇盖"    
	// [4000009,chance],//"蓝蘑菇盖"    
	// [4000003,chance],//"树枝"        
	// [4000005,chance],//"叶子"        
	// [4000018,chance],//"木块"        
	// [4000002,chance],//"蝴蝶结"          
	// [4000006,chance],//"三眼章鱼触角"
	// [4000020,chance],//"野猪尖牙"    
	// [4000188,chance],//"鸭蛋"        
	// [4000252,chance],//"鸡肉"         
	// [4000189,chance],//"羊毛"        
	// [4000190,chance],//"山羊角"      
	// [4000191,chance],//"黑山羊角"    
	// [4000042,chance] //"蝙蝠翅膀" 	
];
//--------------------------------------------------------------

var setupTask;
var mDate;

function init() {
    
    em.setProperty("itemId","0");
    em.setProperty("itemNum", "0");
    em.setProperty("state", "0");
	scheduleNew();
}

function scheduleNew() {
    var cal = java.util.Calendar.getInstance();
    cal.set(java.util.Calendar.SECOND, 5);
	var nextTime = cal.getTimeInMillis();
	while (nextTime <= java.lang.System.currentTimeMillis()) {
		nextTime += 1000;
	}
	setupTask = em.scheduleAtTimestamp("start", nextTime);
}

function cancelSchedule() {
    if(setupTask != null){
        setupTask.cancel(true);
        setupTask = null;
    }
}

function start() {
    mDate = new Date();
	
    // if(mDate.getHours()>=20 && mDate.getHours()<=22){
    // if(mDate.getHours()==20){
		if(_checkedHours(mDate.getHours())){
		//em.broadcastServerMsg(5,"时间执行成功",false);
        if (mDate.getMinutes() == 0  && mDate.getSeconds() == 0) {//mDate.getMinutes() == 0  && mDate.getSeconds() == 0 
			//em.broadcastServerMsg(5,"秒数执行成功",false);
            if(taskReward.length>0){
				em.broadcastServerMsg(2,"“赏金任务”开始了点击赏金任务查看",false);
                var randomNum =  Math.floor(Math.random()*taskReward.length);
                var _tampTask = taskReward[randomNum];
                if(_tampTask!=null){                    
					em.broadcastServerMsg(2,"“赏金任务”第一完成的冒险家有丰富奖励！",false);
                    em.setProperty("state", "1");
					var chance = Math.random()*100+_tampTask[1].toString();
                    em.setProperty("itemId", _tampTask[0].toString());
                    em.setProperty("itemNum", chance);
                }
            }
        }
    }else{
        em.setProperty("state", "0");
    }
	scheduleNew();
}

function _checkedHours(timeHour){
	var isOk = false;
	for(var i =0;i<cfHours.length;i++){
		if(timeHour == cfHours[i]){
			isOk = true;
			break;
		}
	}
	return isOk;
}
