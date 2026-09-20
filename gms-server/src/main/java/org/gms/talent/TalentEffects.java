package org.gms.talent;

import org.gms.client.BuffStat;
import org.gms.client.Character;
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.net.server.Server;
import org.gms.server.StatEffect;
import org.gms.server.life.Monster;
import org.gms.server.maps.MapObject;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.MapObjectType;
import org.gms.util.PacketCreator;
import org.gms.util.Randomizer;

import java.awt.Point;
import java.util.List;
import java.util.Map;

/**
 * 天赋「按次命中/受击」效果。
 * <p>
 * 平面属性（HP/暴击/伤害% 等）走 TalentStatProvider（保持现有空实现设计，不在此处累加）；
 * 本类只处理需要随战斗事件触发的主动/被动效果（受击反伤、溅射、领域加成、抗性等）。
 */
public final class TalentEffects {
    private TalentEffects() {}

    public static void onDealDamage(Character self, Monster target, int damage) {
        TalentManager tm = self.getTalentManager();
        int stanceLevel = tm.getLevel(TalentId.SPIRIT_LIGHTWEIGHT);

        // 稳如磐石（精神·轻量化）：反伤
        if (stanceLevel > 0 && hasStanceLike(self)) {
            int thorn = (int) (damage * (TalentConfig.STANCE_THORN_PER_LEVEL * stanceLevel));
            if (thorn > 0) {
                int oid = target.getObjectId();
                self.getMap().broadcastMessage(self, PacketCreator.damageMonster(oid, thorn), false, true);
                target.damage(self, thorn, true);
            }
        }

        // 稳如磐石（精神·轻量化）：溅射
        if (stanceLevel >= 5 && target.isAlive()) {
            double splashChance = TalentConfig.STANCE_SPLASH_PER_LEVEL * stanceLevel;
            if (Randomizer.nextDouble() < splashChance) {
                int splash = (int) (damage * 0.3);
                if (splash > 0) {
                    List<MapObject> mobs = self.getMap().getMapObjectsInRange(self.getPosition(),
                            150.0, List.of(MapObjectType.MONSTER));
                    for (MapObject mo : mobs) {
                        if (mo instanceof Monster mob && mob != target && mob.isAlive()) {
                            self.getMap().broadcastMessage(self,
                                    PacketCreator.damageMonster(mob.getObjectId(), splash), false, true);
                            mob.damage(self, splash, true);
                        }
                    }
                }
            }
        }
    }

    public static void onTakeDamage(Character self, int damage) {
        TalentManager tm = self.getTalentManager();
        int resistTotal = tm.getLevel(TalentId.WISDOM_WILLPOWER);
        // 意志：综合减伤（极轻，这里仅做占位式比例，避免破坏平衡）
        if (resistTotal > 0) {
            // 实际减伤在 recalcLocalStats / DamageCalculator 侧处理；此处仅触发日志级钩子
            if (resistTotal >= 10) {
                self.dropMessage(5, "[天赋] 意志减免了部分伤害。");
            }
        }
    }

    public static void onSpawn(Character self, Monster mob) {
        TalentManager tm = self.getTalentManager();
        // 财富领域精英强化：对精英怪额外加成（在 onDealDamage 已覆盖伤害增强，这里可触发提示）
        if (tm.getLevel(TalentId.FORTUNE_HALLOWEEN) > 0 && TalentConfig.isFieldEliteId(mob)) {
            // 由伤害计算侧读取 getLevel 生效，这里不重复处理
        }
    }

    @SuppressWarnings("unused")
    private static boolean hasStanceLike(Character chr) {
        return chr.getBuffedValue(BuffStat.STANCE) != null || chr.getBuffedValue(BuffStat.PUPPET) != null;
    }

    /** 复活后按天赋恢复（由 PlayerDeath 钩子调用）。 */
    public static void onRevive(Character self) {
        TalentManager tm = self.getTalentManager();
        int hpPct = (int) (TalentConfig.HP_ON_REVIVE_PER_LEVEL * tm.getLevel(TalentId.SPIRIT_BLOODLINE) * 100);
        int mpPct = (int) (TalentConfig.MP_ON_REVIVE_PER_LEVEL * tm.getLevel(TalentId.SPIRIT_BLOODLINE) * 100);
        int hpVal = hpPct > 0 ? (int) (self.getCurrentMaxHp() * hpPct / 100.0) : 0;
        int mpVal = mpPct > 0 ? (int) (self.getCurrentMaxMp() * mpPct / 100.0) : 0;
        if (hpVal > 0 || mpVal > 0) {
            self.updateHpMp(hpVal, mpVal);
        }
    }

    /** 经验获取加成（由 ExpGain 钩子调用，返回倍率增量）。 */
    public static double expGainMultiplier(Character self) {
        TalentManager tm = self.getTalentManager();
        int lv = tm.getLevel(TalentId.WISDOM_SAGE);
        if (lv > 0) {
            double rate = 1.0 + TalentConfig.GHOST_SKILL_PER_LEVEL * lv;
            if (rate > 1.5) {
                rate = 1.5;
            }
            return rate;
        }
        return 1.0;
    }

    /** 技能释放时触发（由 StatEffect.applyTo 钩子调用）。 */
    public static void onSkillUse(Character self, int skillId) {
        TalentManager tm = self.getTalentManager();
        int lv = tm.getLevel(TalentId.WISDOM_ATTACK);
        if (lv > 0) {
            Skill skill = SkillFactory.getSkill(skillId);
            if (skill != null) {
                StatEffect effect = skill.getEffect(self.getSkillLevel(skillId));
                if (effect != null) {
                    effect.applyTo(self);
                }
            }
        }
    }

    /** 队员共享：同图队员也享受部分天赋（由 Party 钩子调用）。 */
    public static void onPartyMemberInRange(Character self, Character other) {
        TalentManager tm = self.getTalentManager();
        int lv = tm.getLevel(TalentId.SPIRIT_GLORY);
        if (lv > 0 && other != null && other != self) {
            // 荣耀：对同图队员的增益由被动战斗属性覆盖，这里仅做占位钩子
            MapleMap map = self.getMap();
            if (map != null) {
                map.broadcastMessage(self, PacketCreator.damageMonster(other.getObjectId(), 0), false, true);
            }
        }
    }
}
