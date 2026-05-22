package org.gms.server.partyquest;

import java.awt.*;

/**
 * 【类型】GuardianSpawnPoint（class），包 `org.gms.server.partyquest`。
 *
 * 嘉年华守护者生成点，管理守护者的生成位置、占用状态和所属队伍，用于怪物嘉年华中的守护者召唤机制。
 *
 * @author 萧曵
 */
public class GuardianSpawnPoint {

    private Point position;
    private boolean taken;
    private int team = -1;

    public GuardianSpawnPoint(Point a) {
        this.position = a;
        this.taken = true;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }
}
