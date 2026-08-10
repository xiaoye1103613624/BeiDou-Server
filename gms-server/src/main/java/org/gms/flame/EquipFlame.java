package org.gms.flame;

/** 火花加算结果（虚拟，不写 body short）。对齐 265 EquipFlame。 */
public final class EquipFlame {
    public short str, dex, inte, luk;
    public short pad, mad, pdd;
    public short hp, mp, speed, jump;
    public short allStatR, bossDamageR, damageR;
    public byte reduceReqLevel;

    public void reset() {
        str = dex = inte = luk = 0;
        pad = mad = pdd = 0;
        hp = mp = speed = jump = 0;
        allStatR = bossDamageR = damageR = 0;
        reduceReqLevel = 0;
    }

    public EquipFlame deepCopy() {
        EquipFlame r = new EquipFlame();
        r.str = str;
        r.dex = dex;
        r.inte = inte;
        r.luk = luk;
        r.pad = pad;
        r.mad = mad;
        r.pdd = pdd;
        r.hp = hp;
        r.mp = mp;
        r.speed = speed;
        r.jump = jump;
        r.allStatR = allStatR;
        r.bossDamageR = bossDamageR;
        r.damageR = damageR;
        r.reduceReqLevel = reduceReqLevel;
        return r;
    }

    /** tip / 成长包：STR..Jump 共 15 槽（与 tip statIdx 对齐）。 */
    public int[] toTipStats15() {
        return new int[]{
                str, dex, inte, luk, hp, mp, pad, mad, pdd, pdd,
                0, 0, speed, jump, 0
        };
    }
}
