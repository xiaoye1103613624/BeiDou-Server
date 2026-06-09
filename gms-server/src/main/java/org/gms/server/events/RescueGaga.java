/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gms.server.events;

import org.gms.client.Character;
import org.gms.client.SkillFactory;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * 拯救Gaga事件
 * 玩家完成20次拯救后可获得特殊技能，完成次数越多技能等级越高
 * 技能有效期为20天，累计20次后解锁最高等级
 *
 * @author kevintjuh93
 */
public class RescueGaga extends Events {

    /** 已完成次数 */
    private int completed;

    /**
     * 构造拯救Gaga事件
     *
     * @param completed 已完成次数
     */
    public RescueGaga(int completed) {
        super();
        this.completed = completed;
    }

    /**
     * 获取已完成次数
     *
     * @return 已完成次数
     */
    public int getCompleted() {
        return completed;
    }

    /**
     * 完成一次拯救，计数加1
     */
    public void complete() {
        completed++;
    }

    @Override
    public int getInfo() {
        return getCompleted();
    }

    /**
     * 发放拯救奖励技能
     * 根据玩家职业类型赋予对应技能，完成次数小于20获得1级技能（20天有效期）
     * 完成次数达到20获得2级技能
     *
     * @param chr 玩家
     */
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