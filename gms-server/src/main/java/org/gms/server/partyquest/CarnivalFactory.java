package org.gms.server.partyquest;

import org.gms.client.Disease;
import org.gms.provider.Data;
import org.gms.provider.DataProvider;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.DataTool;
import org.gms.provider.wz.WZFiles;
import org.gms.server.life.MobSkill;
import org.gms.server.life.MobSkillFactory;
import org.gms.server.life.MobSkillType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 嘉年华技能工厂（单例）
 * 从WZ文件加载怪物嘉年华技能配置，管理技能和守护者
 * 区分单目标和多目标技能
 *
 * @author Drago (Dragohe4rt)
 */
public class CarnivalFactory {

    /** 单例实例 */
    private final static CarnivalFactory instance = new CarnivalFactory();
    /** 技能映射（技能ID -> MCSkill） */
    private final Map<Integer, MCSkill> skills = new HashMap<>();
    /** 守护者映射 */
    private final Map<Integer, MCSkill> guardians = new HashMap<>();
    /** WZ技能数据提供者 */
    private final DataProvider dataRoot = DataProviderFactory.getDataProvider(WZFiles.SKILL);

    /** 单目标技能列表 */
    private final List<Integer> singleTargetedSkills = new ArrayList<>();
    /** 多目标技能列表 */
    private final List<Integer> multiTargetedSkills = new ArrayList<>();

    public CarnivalFactory() {
        //whoosh
        initialize();
    }

    /**
     * 获取单例实例
     *
     * @return CarnivalFactory 单例
     */
    public static final CarnivalFactory getInstance() {
        return instance;
    }

    /**
     * 从WZ文件加载所有嘉年华技能和守护者配置
     */
    private void initialize() {
        if (skills.size() != 0) {
            return;
        }
        for (Data z : dataRoot.getData("MCSkill.img")) {
            Integer id = Integer.parseInt(z.getName());
            int spendCp = DataTool.getInt("spendCP", z, 0);
            int mobSkillId = DataTool.getInt("mobSkillID", z, 0);
            MobSkillType mobSkillType = null;
            if (mobSkillId != 0) {
                mobSkillType = MobSkillType.from(mobSkillId).orElseThrow();
            }
            int level = DataTool.getInt("level", z, 0);
            boolean isMultiTarget = DataTool.getInt("target", z, 1) > 1;
            MCSkill ms = new MCSkill(spendCp, mobSkillType, level, isMultiTarget);

            skills.put(id, ms);
            if (ms.targetsAll) {
                multiTargetedSkills.add(id);
            } else {
                singleTargetedSkills.add(id);
            }
        }
        for (Data z : dataRoot.getData("MCGuardian.img")) {
            int spendCp = DataTool.getInt("spendCP", z, 0);
            int mobSkillId = DataTool.getInt("mobSkillID", z, 0);
            MobSkillType mobSkillType = MobSkillType.from(mobSkillId).orElseThrow();
            int level = DataTool.getInt("level", z, 0);
            guardians.put(Integer.parseInt(z.getName()), new MCSkill(spendCp, mobSkillType, level, true));
        }
    }

    /**
     * 随机获取一个技能
     *
     * @param multi true=多目标技能，false=单目标技能
     * @return 随机选中的技能
     */
    private MCSkill randomizeSkill(boolean multi) {
        if (multi) {
            return skills.get(multiTargetedSkills.get((int) (Math.random() * multiTargetedSkills.size())));
        } else {
            return skills.get(singleTargetedSkills.get((int) (Math.random() * singleTargetedSkills.size())));
        }
    }

    /**
     * 根据ID获取技能，若技能类型为空则随机获取一个同类型技能
     *
     * @param id 技能ID
     * @return 技能对象
     */
    public MCSkill getSkill(final int id) {
        MCSkill skill = skills.get(id);
        if (skill != null && skill.mobSkillType == null) {
            return randomizeSkill(skill.targetsAll);
        } else {
            return skill;
        }
    }

    /**
     * 根据ID获取守护者
     *
     * @param id 守护者ID
     * @return 守护者技能对象
     */
    public MCSkill getGuardian(final int id) {
        return guardians.get(id);
    }

    /**
     * 嘉年华技能记录
     * 包含CP消耗、怪物技能类型、等级和是否多目标
     *
     * @param cpLoss       CP消耗
     * @param mobSkillType 怪物技能类型
     * @param level        技能等级
     * @param targetsAll   是否多目标
     */
    public record MCSkill(int cpLoss, MobSkillType mobSkillType, int level, boolean targetsAll) {
        /**
         * 获取对应的MobSkill实例
         *
         * @return MobSkill
         */
        public MobSkill getSkill() {
            return MobSkillFactory.getMobSkillOrThrow(mobSkillType, level);
        }

        /**
         * 获取技能对应的异常状态
         *
         * @return 异常状态枚举
         */
        public Disease getDisease() {
            return Disease.getBySkill(mobSkillType);
        }
    }
}