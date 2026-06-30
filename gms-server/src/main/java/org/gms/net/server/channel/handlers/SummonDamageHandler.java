/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation version 3 as published by
the Free Software Foundation. You may not use, modify or distribute
this program under any other version of the GNU Affero General Public
License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.client.autoban.AutobanFactory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.WeaponType;
import org.gms.client.status.MonsterStatusEffect;
import org.gms.config.EquipDamageBonusManager;
import org.gms.config.ExtraDamageConfigManager;
import org.gms.config.SetDamageBonusManager;
import org.gms.constants.skills.Outlaw;
import org.gms.net.packet.InPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.server.ItemInformationProvider;
import org.gms.server.StatEffect;
import org.gms.server.life.Monster;
import org.gms.server.life.MonsterInformationProvider;
import org.gms.server.maps.Summon;
import org.gms.util.PacketCreator;

import java.util.ArrayList;
import java.util.List;

public final class SummonDamageHandler extends AbstractDealDamageHandler {
    private static final Logger log = LoggerFactory.getLogger(SummonDamageHandler.class);

    public final class SummonAttackEntry {

        private final int monsterOid;
        private int damage;

        public SummonAttackEntry(int monsterOid, int damage) {
            this.monsterOid = monsterOid;
            this.damage = damage;
        }

        public int getMonsterOid() {
            return monsterOid;
        }

        public int getDamage() {
            return damage;
        }

        public void setDamage(int damage) {
            this.damage = damage;
        }

    }

    @Override
    public void handlePacket(InPacket p, Client c) {
        int oid = p.readInt();
        Character player = c.getPlayer();
        if (!player.isAlive()) {
            return;
        }
        Summon summon = null;
        for (Summon sum : player.getSummonsValues()) {
            if (sum.getObjectId() == oid) {
                summon = sum;
            }
        }
        if (summon == null) {
            return;
        }
        Skill summonSkill = SkillFactory.getSkill(summon.getSkill());
        StatEffect summonEffect = summonSkill.getEffect(summon.getSkillLevel());
        p.skip(4);
        List<SummonAttackEntry> allDamage = new ArrayList<>();
        byte direction = p.readByte();
        int numAttacked = p.readByte();
        p.skip(8); // I failed lol (mob x,y and summon x,y), Thanks Gerald
        for (int x = 0; x < numAttacked; x++) {
            int monsterOid = p.readInt(); // attacked oid
            p.skip(18);
            int damage = p.readInt();
            allDamage.add(new SummonAttackEntry(monsterOid, damage));
        }
        // 装备/套装伤害加成：预应用到召唤兽伤害值，使得广播包中其他玩家看到加成后的伤害
        EquipDamageBonusManager.Bonus equipBonus = player.getEquipDamageBonus();
        SetDamageBonusManager.Bonus setBonus = player.getSetDamageBonus();
        int equipPct = 0;
        long extraSegs = 0L;
        if (equipBonus != EquipDamageBonusManager.Bonus.EMPTY || setBonus != SetDamageBonusManager.Bonus.EMPTY) {
            equipPct = equipBonus.damagePct() + setBonus.damagePct();
            // 检查第一个目标是否为Boss
            if (!allDamage.isEmpty()) {
                Monster firstTarget = player.getMap().getMonsterByOid(allDamage.get(0).getMonsterOid());
                if (firstTarget != null && firstTarget.isBoss()) {
                    equipPct += equipBonus.bossDamagePct() + setBonus.bossDamagePct();
                }
            }
            extraSegs = equipBonus.extraSegments();
            if (equipPct != 0) {
                for (SummonAttackEntry entry : allDamage) {
                    int boosted = (int) ((long) entry.getDamage() + (long) entry.getDamage() * equipPct / 100);
                    entry.setDamage(boosted);
                }
            }
        }

        player.getMap().broadcastMessage(player, PacketCreator.summonAttack(player.getId(), summon.getObjectId(), direction, allDamage), summon.getPosition());

        if (player.getMap().isOwnershipRestricted(player)) {
            return;
        }

        boolean magic = summonEffect.getWatk() == 0;
        int maxDmg = calcMaxDamage(summonEffect, player, magic);    // thanks Darter (YungMoozi) for reporting unchecked max dmg
        for (SummonAttackEntry attackEntry : allDamage) {
            int damage = attackEntry.getDamage();
            Monster target = player.getMap().getMonsterByOid(attackEntry.getMonsterOid());
            if (target != null) {
                if (damage > maxDmg) {
                    AutobanFactory.DAMAGE_HACK.alert(c.getPlayer(), "Possible packet editing summon damage exploit.");
                    final String mobName = MonsterInformationProvider.getInstance().getMobNameFromId(target.getId());
                    log.info("Possible exploit - chr {} used a summon of skillId {} to attack {} with damage {} (max: {})",
                            c.getPlayer().getName(), summon.getSkill(), mobName, damage, maxDmg);
                    damage = maxDmg;
                }

                if (damage > 0 && summonEffect.getMonsterStati().size() > 0) {
                    if (summonEffect.makeChanceResult()) {
                        target.applyStatus(player, new MonsterStatusEffect(summonEffect.getMonsterStati(), summonSkill, null, false), summonEffect.isPoison(), 4000);
                    }
                }
                player.getMap().damageMonster(player, target, damage);

                // 装备加成额外段数：每段独立造成伤害
                if (extraSegs > 0 && target.isAlive()) {
                    for (long i = 0; i < extraSegs; i++) {
                        player.getMap().damageMonster(player, target, damage);
                    }
                }

                // 额外伤害（可配置，基于角色属性）
                long extraDamage = ExtraDamageConfigManager.calcExtraDamage(player, damage);
                if (extraDamage > 0 && target.isAlive()) {
                    player.getMap().damageMonster(player, target, extraDamage);
                }
            }
        }

        if (summon.getSkill() == Outlaw.GAVIOTA) {  // thanks Periwinks for noticing Gaviota not cancelling after grenade toss
            player.cancelEffect(summonEffect, false, -1);
        }
    }

    private static int calcMaxDamage(StatEffect summonEffect, Character player, boolean magic) {
        double maxDamage;

        if (magic) {
            int matk = Math.max(player.getTotalMagic(), 14);
            maxDamage = player.calculateMaxBaseMagicDamage(matk) * (0.05 * summonEffect.getMatk());
        } else {
            int watk = Math.max(player.getTotalWatk(), 14);
            Item weapon_item = player.getInventory(InventoryType.EQUIPPED).getItem((short) -11);

            int maxBaseDmg;  // thanks Conrad, Atoot for detecting some summons legitimately hitting over the calculated limit
            if (weapon_item != null) {
                maxBaseDmg = player.calculateMaxBaseDamage(watk, ItemInformationProvider.getInstance().getWeaponType(weapon_item.getItemId()));
            } else {
                maxBaseDmg = player.calculateMaxBaseDamage(watk, WeaponType.SWORD1H);
            }

            float summonDmgMod = (maxBaseDmg >= 438) ? 0.054f : 0.077f;
            maxDamage = maxBaseDmg * (summonDmgMod * summonEffect.getWatk());
        }

        return (int) maxDamage;
    }
}
