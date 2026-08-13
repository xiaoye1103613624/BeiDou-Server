/*
    This file is part of the HeavenMS MapleStory Server, commands OdinMS-based
    Copyleft (L) 2016 - 2019 RonanLana

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
package org.gms.client.command.commands.gm2;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.client.command.Command;
import org.gms.client.keybind.KeyBinding;
import org.gms.constants.game.GameConstants;

/**
 * Teach / set a skill level: {@code !skill <skillId> [level]}
 * Example (V-skill pilot): {@code !skill 1121015 1}
 * <p>
 * v083 skillbook only lists skills from {@code Data/Skill/{job}.img} on the matching job tab.
 * A 112xxxx skill will not appear unless the character is 四转英雄 (job 112).
 */
public class SkillCommand extends Command {
    /** Prefer Insert, then F10, F12, End — avoid F11 (轮回 default). */
    private static final int[] PREFERRED_KEYS = {42, 86, 88, 35, 36, 37, 38, 39, 40, 41, 43, 44, 45};

    {
        setDescription("Teach a skill: !skill <id> [level]");
    }

    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("用法: !skill <技能ID> [等级]  例: !skill 1121015 1");
            player.yellowMessage("技能窗只显示本职技能。112xxxx 需 !job 112（四转英雄），再打开技能窗点「英雄」页。");
            return;
        }
        int skillId;
        try {
            skillId = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            player.yellowMessage("技能ID必须是数字。");
            return;
        }
        Skill skill = SkillFactory.getSkill(skillId);
        if (skill == null) {
            player.yellowMessage("未知技能: " + skillId + "（服务端 SkillFactory 未加载，检查 wz-zh-CN/Skill.wz）");
            return;
        }
        int max = skill.getMaxLevel();
        if (max <= 0) {
            max = 1;
        }
        int level = max;
        if (params.length >= 2) {
            try {
                level = Integer.parseInt(params[1]);
            } catch (NumberFormatException e) {
                player.yellowMessage("等级必须是数字。");
                return;
            }
        }
        if (level < 0) {
            level = 0;
        } else if (level > max) {
            level = max;
        }

        player.changeSkillLevel(skill, (byte) level, max, -1);

        final int skillJob = skillId / 10000;
        final int playerJob = player.getJob().getId();
        final boolean inTree = GameConstants.isInJobTree(skillId, playerJob);

        player.yellowMessage("已设置技能 " + skillId + " = " + level + "/" + max
                + "（内存+发包；换线/重登前会随角色存档）");

        if (!inTree) {
            player.dropMessage(5, "【技能窗不会显示】当前职业 " + playerJob
                    + "，技能属于 " + skillJob + "。"
                    + " v083 技能窗只列本职页；112xxxx 必须是四转英雄。");
            player.dropMessage(5, "请先: !job " + skillJob + "  → 关开技能窗(K) → 点最右边「英雄」页找图标。"
                    + " 改过客户端 Skill.img 后需完全重启客户端。");
        } else if (skillJob == 112) {
            player.dropMessage(5, "请在技能窗(K) →「英雄」四转页查看「灵气武器(试点)」（通常在最后一格）。双击或按快捷键即可施放增益+V特效。");
        } else {
            player.dropMessage(5, "请打开技能窗(K)对应职业页查看。若刚更新过 Data/Skill，请完全重启客户端。");
        }

        Integer bound = bindSkillHotkey(player, skillId);
        // Always ensure Insert (42) maps to this skill as a reliable fallback test key
        if (skillId == 1121015) {
            player.changeKeybinding(42, new KeyBinding(1, skillId));
            bound = 42;
        }
        if (bound != null) {
            player.sendKeymap();
            player.dropMessage(5, "已绑定快捷键 key=" + bound + "（常见: 42=Insert, 86=F10）。直接按键或技能窗双击测试。"
                    + " 也可用 !buff " + skillId + " 强行上增益。");
        } else {
            player.dropMessage(5, "未找到空闲快捷键，请手动把技能拖到键盘设置。"
                    + " 也可用 !buff " + skillId + " 试数值。");
        }
    }

    private static Integer bindSkillHotkey(Character player, int skillId) {
        for (var entry : player.getKeymap().entrySet()) {
            KeyBinding kb = entry.getValue();
            if (kb != null && kb.getType() == 1 && kb.getAction() == skillId) {
                return entry.getKey();
            }
        }
        // Prefer empty preferred keys first
        for (int key : PREFERRED_KEYS) {
            KeyBinding cur = player.getKeymap().get(key);
            if (cur == null || cur.getType() == 0) {
                player.changeKeybinding(key, new KeyBinding(1, skillId));
                return key;
            }
        }
        for (int key = 0; key < 90; key++) {
            KeyBinding cur = player.getKeymap().get(key);
            if (cur == null || cur.getType() == 0) {
                player.changeKeybinding(key, new KeyBinding(1, skillId));
                return key;
            }
        }
        // Fallback: force Insert (42) even if occupied — needed when keymap full
        final int INSERT = 42;
        player.changeKeybinding(INSERT, new KeyBinding(1, skillId));
        return INSERT;
    }
}
