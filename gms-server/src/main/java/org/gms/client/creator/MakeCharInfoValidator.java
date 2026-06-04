package org.gms.client.creator;

import org.gms.client.Character;
import org.gms.provider.Data;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.wz.WZFiles;

/**
 * 【类型】MakeCharInfoValidator（class），包 `org.gms.client.creator`。
 *
 * 角色创建信息验证器，用于验证新角色创建时的各项参数是否符合要求。
 * 
 * <p>该类从WZ数据文件中加载不同职业和性别的角色创建配置，并提供验证功能，
 * 确保新创建的角色满足预设的条件（如起始地图、可用装备、技能等）。</p>
 * 
 * <p>支持的角色类型包括：
 * <ul>
 *   <li>基本职业（新手、战士、法师、弓箭手、飞侠、海盗）</li>
 *   <li>贵族系列（NOBLESSE）</li>
 *   <li>传说系列（LEGEND）</li>
 * </ul></p>
 *
 * @author RonanLana
 */
public class MakeCharInfoValidator {
    /** 女性基本角色创建配置 */
    private static final MakeCharInfo charFemale;
    /** 男性基本角色创建配置 */
    private static final MakeCharInfo charMale;
    /** 女性东方角色创建配置 */
    private static final MakeCharInfo orientCharFemale;
    /** 男性东方角色创建配置 */
    private static final MakeCharInfo orientCharMale;
    /** 女性高级角色创建配置 */
    private static final MakeCharInfo premiumCharFemale;
    /** 男性高级角色创建配置 */
    private static final MakeCharInfo premiumCharMale;

    static {
        // 从WZ数据文件中加载角色创建配置
        Data data = DataProviderFactory.getDataProvider(WZFiles.ETC).getData("MakeCharInfo.img");
        charFemale = new MakeCharInfo(data.getChildByPath("Info/CharFemale"));
        charMale = new MakeCharInfo(data.getChildByPath("Info/CharMale"));
        orientCharFemale = new MakeCharInfo(data.getChildByPath("OrientCharFemale"));
        orientCharMale = new MakeCharInfo(data.getChildByPath("OrientCharMale"));
        premiumCharFemale = new MakeCharInfo(data.getChildByPath("PremiumCharFemale"));
        premiumCharMale = new MakeCharInfo(data.getChildByPath("PremiumCharMale"));
    }

    /**
     * 根据角色的职业和性别获取对应的创建配置。
     * 
     * <p>根据角色的职业类型选择相应的配置：
     * <ul>
     *   <li>BEGINNER, WARRIOR, MAGICIAN, BOWMAN, THIEF, PIRATE: 使用基本配置</li>
     *   <li>NOBLESSE: 使用高级配置</li>
     *   <li>LEGEND: 使用东方配置</li>
     * </ul></p>
     * 
     * @param character 角色对象
     * @return 对应的创建配置，如果职业不支持则返回null
     */
    private static MakeCharInfo getMakeCharInfo(Character character) {
        return switch (character.getJob()) {
            case BEGINNER, WARRIOR, MAGICIAN, BOWMAN, THIEF, PIRATE -> character.isMale() ? charMale : charFemale;
            case NOBLESSE -> character.isMale() ? premiumCharMale : premiumCharFemale;
            case LEGEND -> character.isMale() ? orientCharMale : orientCharFemale;
            default -> null;
        };
    }

    /**
     * 验证新角色创建信息是否有效。
     * 
     * <p>通过获取对应的职业和性别配置，验证角色的创建参数是否符合预设条件，
     * 包括起始地图、可用装备、技能等。如果职业不受支持或验证失败，则返回false。</p>
     * 
     * @param character 待验证的角色对象
     * @return 如果角色创建信息有效则返回true，否则返回false
     */
    public static boolean isNewCharacterValid(Character character) {
        MakeCharInfo makeCharInfo = getMakeCharInfo(character);
        if (makeCharInfo == null) return false;

        return makeCharInfo.verifyCharacter(character);
    }
}