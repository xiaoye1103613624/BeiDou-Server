package org.gms.server.partyquest;

import java.awt.*;

/**
 * 守护者生成点
 * 管理嘉年华中守护者的生成位置和占用状态
 *
 * @author David
 */
public class GuardianSpawnPoint {

    /** 生成位置坐标 */
    private Point position;
    /** 是否已被占用 */
    private boolean taken;
    /** 所属队伍，-1表示未分配 */
    private int team = -1;

    public GuardianSpawnPoint(Point a) {
        this.position = a;
        this.taken = true;
    }

    /**
     * 获取生成位置
     *
     * @return 位置坐标
     */
    public Point getPosition() {
        return position;
    }

    /**
     * 设置生成位置
     *
     * @param position 位置坐标
     */
    public void setPosition(Point position) {
        this.position = position;
    }

    /**
     * 判断是否已被占用
     *
     * @return true表示已占用
     */
    public boolean isTaken() {
        return taken;
    }

    /**
     * 设置占用状态
     *
     * @param taken 是否占用
     */
    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    /**
     * 获取所属队伍编号
     *
     * @return 队伍编号，-1表示未分配
     */
    public int getTeam() {
        return team;
    }

    /**
     * 设置所属队伍编号
     *
     * @param team 队伍编号
     */
    public void setTeam(int team) {
        this.team = team;
    }
}