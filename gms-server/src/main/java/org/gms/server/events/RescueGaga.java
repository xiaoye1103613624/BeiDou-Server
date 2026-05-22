/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gms.server.events;

import org.gms.client.Character;
import org.gms.client.SkillFactory;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * 【类型】RescueGaga（class），包 `org.gms.server.events`。
 *
 * 营救加加活动实现，追踪玩家营救加加的完成次数，并根据完成度给予对应的技能奖励（20天有效期）。
 *
 * @author 萧曵
 */
public class RescueGaga extends Events {

    private int completed;

    public RescueGaga(int completed) {
        super();
        this.completed = completed;
    }

    public int getCompleted() {
        return completed;
    }

    public void complete() {
        completed++;
    }

    @Override
    public int getInfo() {
        return getCompleted();
    }

    public void giveSkill(Character chr) {
        int skillid = 0;
        switch (chr.getJobType()) {
            case 0:
                skillid = 1013;
                break;
            case 1:
            case 2:
                skillid = 10001014;
        }

        long expiration = (System.currentTimeMillis() + DAYS.toMillis(20));
        if (completed < 20) {
            chr.changeSkillLevel(SkillFactory.getSkill(skillid), (byte) 1, 1, expiration);
            chr.changeSkillLevel(SkillFactory.getSkill(skillid + 1), (byte) 1, 1, expiration);
            chr.changeSkillLevel(SkillFactory.getSkill(skillid + 2), (byte) 1, 1, expiration);
        } else {
            chr.changeSkillLevel(SkillFactory.getSkill(skillid), (byte) 2, 2, chr.getSkillExpiration(skillid));
        }
    }

}
