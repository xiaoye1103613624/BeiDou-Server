/*
	家族信息查询 - 查看家族成员在线/离线状态、职业、等级、所在线路、地图
*/

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode === -1) {
		cm.dispose();
		return;
	}

	var guild = cm.getGuild();
	if (guild == null) {
		cm.sendOk("您还没有加入家族哦！");
		cm.dispose();
		return;
	}

	var members = guild.getMembers();
	var totalMembers = members.size();
	var onlineList = [];
	var offlineList = [];
	var leaderName = "";

	for (var i = 0; i < totalMembers; i++) {
		var mgc = members.get(i);
		if (mgc.getId() === guild.getLeaderId()) {
			leaderName = mgc.getName();
		}
		if (mgc.isOnline()) {
			onlineList.push(mgc);
		} else {
			offlineList.push(mgc);
		}
	}

	var text = "#b========== 家族信息 ==========#k\r\n";
	text += "家族名称：#r" + guild.getName() + "#k\r\n";
	text += "家族ID：" + guild.getId();
	text += "\t族长：" + leaderName + "\r\n";
	text += "家族点数：" + guild.getGP();
	text += "\t容量：" + totalMembers + "/" + guild.getCapacity() + "\r\n";

	if (guild.getNotice() != null && guild.getNotice() !== "" && guild.getNotice() !== "null") {
		text += "家族公告：" + guild.getNotice() + "\r\n";
	}

	text += "\r\n#b========== 在线成员（" + onlineList.length + "）==========#k\r\n";
	if (onlineList.length > 0) {
		for (var j = 0; j < onlineList.length; j++) {
			var om = onlineList[j];
			var rankName = guild.getRankTitle(om.getGuildRank());
			var jobName = cm.getJobName(om.getJobId());

			text += "#g" + (j + 1) + ".#k " + rankName + " ";
			text += om.getName();
			text += "  #bLv." + om.getLevel() + "#k";
			text += "  #d" + jobName + "#k";
			text += "  #e线路" + om.getChannel() + "#k";

			var chr = om.getCharacter();
			if (chr != null) {
				try {
					text += "  #r[" + chr.getMap().getMapName() + "]#k";
				} catch (e) {
					text += "  #r[未知]#k";
				}
			}

			text += "\r\n";
		}
	} else {
		text += "暂无在线成员\r\n";
	}

	text += "\r\n#b========== 离线成员（" + offlineList.length + "）==========#k\r\n";
	if (offlineList.length > 0) {
		for (var k = 0; k < offlineList.length; k++) {
			var om2 = offlineList[k];
			var rankName2 = guild.getRankTitle(om2.getGuildRank());
			var jobName2 = cm.getJobName(om2.getJobId());

			text += "#d" + (k + 1) + ".#k " + rankName2 + " ";
			text += om2.getName();
			text += "  #bLv." + om2.getLevel() + "#k";
			text += "  #d" + jobName2 + "#k";
			text += "\r\n";
		}
	} else {
		text += "暂无离线成员\r\n";
	}

	cm.sendOk(text);
	cm.dispose();
}