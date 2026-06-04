package org.gms.client.creator;

import org.gms.client.Character;
import org.gms.client.Job;
import org.gms.client.inventory.InventoryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.provider.Data;
import org.gms.provider.DataTool;

import java.util.HashSet;
import java.util.Set;

/**
 * 【类型】MakeCharInfo（class），包 `org.gms.client.creator`。
 *
 * 角色创建信息类，用于验证新角色创建时的各项参数。
 * 
 * <p>该类从WZ数据文件中加载角色创建的合法配置，包括脸型、发型、肤色、
 * 服装和武器等项目的有效ID列表，并提供验证方法确保创建的角色符合预设条件。</p>
 * 
 * <p>验证内容包括：
 * <ul>
 *   <li>脸型ID的有效性</li>
 *   <li>发型ID的有效性（考虑发型颜色变化）</li>
 *   <li>发色ID的有效性</li>
 *   <li>肤色ID的有效性</li>
 *   <li>初始装备（上衣、下装、鞋子、武器）的有效性</li>
 * </ul></p>
 *
 * @author RonanLana
 */
public class MakeCharInfo {
    private static final Logger log = LoggerFactory.getLogger(MakeCharInfo.class);
    private static final String FACE_ID = "0";           // 脸型节点ID
    private static final String HAIR_ID = "1";           // 发型节点ID
    private static final String HAIR_COLOR_ID = "2";     // 发色节点ID
    private static final String SKIN_ID = "3";           // 肤色节点ID
    private static final String TOP_ID = "4";            // 上衣节点ID
    private static final String BOTTOM_ID = "5";         // 下装节点ID
    private static final String SHOE_ID = "6";           // 鞋子节点ID
    private static final String WEAPON_ID = "7";         // 武器节点ID

    /** 合法的脸型ID集合 */
    private final Set<Integer> charFaces = new HashSet<>();
    /** 合法的发型ID集合 */
    private final Set<Integer> charHairs = new HashSet<>();
    /** 合法的发色ID集合 */
    private final Set<Integer> charHairColors = new HashSet<>();
    /** 合法的肤色ID集合 */
    private final Set<Integer> charSkins = new HashSet<>();
    /** 合法的上衣ID集合 */
    private final Set<Integer> charTops = new HashSet<>();
    /** 合法的下装ID集合 */
    private final Set<Integer> charBottoms = new HashSet<>();
    /** 合法的鞋子ID集合 */
    private final Set<Integer> charShoes = new HashSet<>();
    /** 合法的武器ID集合 */
    private final Set<Integer> charWeapons = new HashSet<>();

    /**
     * 构造函数：使用WZ数据初始化角色创建信息。
     * 
     * <p>从提供的WZ数据中解析并加载角色创建的合法配置，
     * 包括脸型、发型、肤色、服装和武器等项目的有效ID列表。</p>
     * 
     * @param charInfoData 来自WZ文件的角色创建信息数据
     */
    public MakeCharInfo(Data charInfoData) {
        for (Data data : charInfoData.getChildren()) {
            switch (data.getName()) {
                case FACE_ID -> {
                    for (Data faceData : data) {
                        charFaces.add(DataTool.getInt(faceData));
                    }
                }
                case HAIR_ID -> {
                    for (Data hairData : data) {
                        charHairs.add(DataTool.getInt(hairData));
                    }
                }
                case HAIR_COLOR_ID -> {
                    for (Data hairColorData : data) {
                        charHairColors.add(DataTool.getInt(hairColorData));
                    }
                }
                case SKIN_ID -> {
                    for (Data skinData : data) {
                        charSkins.add(DataTool.getInt(skinData));
                    }
                }
                case TOP_ID -> {
                    for (Data topData : data) {
                        charTops.add(DataTool.getInt(topData));
                    }
                }
                case BOTTOM_ID -> {
                    for (Data bottomData : data) {
                        charBottoms.add(DataTool.getInt(bottomData));
                    }
                }
                case SHOE_ID -> {
                    for (Data shoeData : data) {
                        charShoes.add(DataTool.getInt(shoeData));
                    }
                }
                case WEAPON_ID -> {
                    for (Data weaponData : data) {
                        charWeapons.add(DataTool.getInt(weaponData));
                    }
                }
                default -> log.error("Unhandled node inside MakeCharInfo.img.xml: '" + data.getName() + "'");
            }
        }
    }

    /**
     * 验证脸型ID是否合法。
     * 
     * @param id 待验证的脸型ID
     * @return 如果脸型ID合法则返回true，否则返回false
     */
    public boolean verifyFaceId(int id) {
        return this.charFaces.contains(id);
    }

    /**
     * 验证发型ID是否合法。
     * 
     * <p>对于带颜色的发型ID，会去除颜色部分后验证基础发型ID。</p>
     * 
     * @param id 待验证的发型ID
     * @return 如果发型ID合法则返回true，否则返回false
     */
    public boolean verifyHairId(int id) {
        if (id % 10 != 0) {
            return this.charHairs.contains(id - (id % 10));
        }
        return this.charHairs.contains(id);
    }

    /**
     * 验证发色ID是否合法。
     * 
     * <p>提取ID的个位数作为发色部分进行验证。</p>
     * 
     * @param id 待验证的发型ID（从中提取发色部分）
     * @return 如果发色ID合法则返回true，否则返回false
     */
    public boolean verifyHairColorId(int id) {
        return this.charHairColors.contains(id % 10);
    }

    /**
     * 验证肤色ID是否合法。
     * 
     * @param id 待验证的肤色ID
     * @return 如果肤色ID合法则返回true，否则返回false
     */
    public boolean verifySkinId(int id) {
        return this.charSkins.contains(id);
    }

    /**
     * 验证上衣ID是否合法。
     * 
     * @param id 待验证的上衣ID
     * @return 如果上衣ID合法则返回true，否则返回false
     */
    public boolean verifyTopId(int id) {
        return this.charTops.contains(id);
    }

    /**
     * 验证下装ID是否合法。
     * 
     * @param id 待验证的下装ID
     * @return 如果下装ID合法则返回true，否则返回false
     */
    public boolean verifyBottomId(int id) {
        return this.charBottoms.contains(id);
    }

    /**
     * 验证鞋子ID是否合法。
     * 
     * @param id 待验证的鞋子ID
     * @return 如果鞋子ID合法则返回true，否则返回false
     */
    public boolean verifyShoeId(int id) {
        return this.charShoes.contains(id);
    }

    /**
     * 验证武器ID是否合法。
     * 
     * @param id 待验证的武器ID
     * @return 如果武器ID合法则返回true，否则返回false
     */
    public boolean verifyWeaponId(int id) {
        return this.charWeapons.contains(id);
    }

    /**
     * 验证角色对象的所有创建参数是否合法。
     * 
     * <p>验证角色的脸型、发型、发色、肤色以及初始装备（仅限特定职业）。
     * 对于BEGINNER、NOBLESSE和LEGEND职业，还会验证初始装备的有效性，
     * 包括上衣、下装、鞋子和武器。</p>
     * 
     * <p>注意：只有特定职业（新手、贵族、传说）需要验证初始装备，
     * 因为当使用Maple Life A或B道具创建角色时，客户端不会发送装备数据，
     * 装备由服务器端处理。</p>
     * 
     * @param character 待验证的角色对象
     * @return 如果所有参数都合法则返回true，否则返回false
     */
    public boolean verifyCharacter(Character character) {
        if (!verifyFaceId(character.getFace())) return false;
        if (!verifyHairId(character.getHair())) return false;
        if (!verifyHairColorId(character.getHair())) return false;
        if (!verifySkinId(character.getSkinColor().getId())) return false;

        // Here we only verify the equipment if the character that's being created is of type 'Beginner'
        // This is because when the Maple Life A or Maple Life B items are used, the client does not send any data
        // regarding what equipment the character should be wearing (as it's all handled server-side)
        Job characterJob = character.getJob();
        if (characterJob == Job.BEGINNER || characterJob == Job.NOBLESSE || characterJob == Job.LEGEND) {
            if (!verifyTopId(character.getInventory(InventoryType.EQUIPPED).getItem((short) -5).getItemId()))
                return false;
            if (!verifyBottomId(character.getInventory(InventoryType.EQUIPPED).getItem((short) -6).getItemId()))
                return false;
            if (!verifyShoeId(character.getInventory(InventoryType.EQUIPPED).getItem((short) -7).getItemId()))
                return false;
            if (!verifyWeaponId(character.getInventory(InventoryType.EQUIPPED).getItem((short) -11).getItemId()))
                return false;
        }

        return true;
    }
}