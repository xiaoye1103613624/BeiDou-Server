package org.gms.model.dto;

import com.mybatisflex.annotation.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.Item;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventorySearchRtnDTO {
    /**
     * 自增id，对应inventoryitemid
     */
    private Long id;
    /**
     * 角色id
     */
    private Integer characterId;
    /**
     * 物品id，对应itemid
     */
    private Integer itemId;
    /**
     * 物品类型，对应type
     * @see org.gms.client.inventory.ItemFactory
     */
    private Integer itemType;
    /**
     * 背包栏类型，对应inventorytype
     * @see org.gms.client.inventory.InventoryType
     */
    private Byte inventoryType;
    /**
     * 物品位置，对应position
     */
    private Short position;
    /**
     * 物品数量，对应quantity
     */
    private Short quantity;
    /**
     * 制作者，对应owner
     */
    private String owner;
    /**
     * 宠物id，对应petid
     */
    private Integer petId;
    /**
     * 物品标记，对应flag
     */
    private Short flag;
    /**
     * 物品有效期，对应expiration
     */
    private Long expiration;
    /**
     * 送礼人，对应giftFrom
     */
    private String giftFrom;
    /**
     * 是否在线
     */
    private boolean online;
    /**
     * 是否装备，判断子表inventoryequipment是否有数据
     */
    private boolean equipment;
    /**
     * 装备信息，equipment为true时有值
     */
    private InventoryEquipRtnDTO inventoryEquipment;

    /**
     *
     * 物品名称，根据itemID返回。
     */
    private String itemName;

    public Item toItem() {
        Item item;
        if (isEquipment()) {
            InventoryEquipRtnDTO eq = getInventoryEquipment();
            Equip equip = new Equip(getItemId(), getPosition());
            equip.setUpgradeSlots(Optional.ofNullable(eq.getUpgradeSlots()).orElse((byte) 0));
            equip.setLevel(Optional.ofNullable(eq.getLevel()).orElse((byte) 0));
            equip.setStr(Optional.ofNullable(eq.getAttStr()).orElse((short) 0));
            equip.setDex(Optional.ofNullable(eq.getAttDex()).orElse((short) 0));
            equip.setInt(Optional.ofNullable(eq.getAttInt()).orElse((short) 0));
            equip.setLuk(Optional.ofNullable(eq.getAttLuk()).orElse((short) 0));
            equip.setHp(Optional.ofNullable(eq.getHp()).orElse((short) 0));
            equip.setMp(Optional.ofNullable(eq.getMp()).orElse((short) 0));
            equip.setWatk(Optional.ofNullable(eq.getPAtk()).orElse((short) 0));
            equip.setMatk(Optional.ofNullable(eq.getMAtk()).orElse((short) 0));
            equip.setWdef(Optional.ofNullable(eq.getPDef()).orElse((short) 0));
            equip.setMdef(Optional.ofNullable(eq.getMDef()).orElse((short) 0));
            equip.setAcc(Optional.ofNullable(eq.getAcc()).orElse((short) 0));
            equip.setAvoid(Optional.ofNullable(eq.getAvoid()).orElse((short) 0));
            equip.setHands(Optional.ofNullable(eq.getHands()).orElse((short) 0));
            equip.setSpeed(Optional.ofNullable(eq.getSpeed()).orElse((short) 0));
            equip.setJump(Optional.ofNullable(eq.getJump()).orElse((short) 0));
            equip.setVicious(Optional.ofNullable(eq.getVicious()).orElse((short) 0));
            equip.setItemLevel(Optional.ofNullable(eq.getItemLevel()).orElse((byte) 0));
            equip.setItemExp(Optional.ofNullable(eq.getItemExp()).orElse(0));
            equip.setRingId(Optional.ofNullable(eq.getRingId()).orElse(0));
            equip.setAnvilItemId(Optional.ofNullable(eq.getAnvilItemId()).orElse(0));
            equip.setEquipSkillId(Optional.ofNullable(eq.getEquipSkillId()).orElse(0));
            equip.setEquipSkillLevel(Optional.ofNullable(eq.getEquipSkillLevel()).orElse(0));
            equip.setEquipSkillExpire(Optional.ofNullable(eq.getEquipSkillExpire()).orElse(0L));
            equip.setPotential1(Optional.ofNullable(eq.getPotential1()).orElse(0));
            equip.setPotential2(Optional.ofNullable(eq.getPotential2()).orElse(0));
            equip.setPotential3(Optional.ofNullable(eq.getPotential3()).orElse(0));
            equip.setPotentialGrade(Optional.ofNullable(eq.getPotentialGrade()).orElse(0).byteValue());
            equip.setEnhance(Optional.ofNullable(eq.getEnhance()).orElse(0).byteValue());
            equip.setBonusPotential1(Optional.ofNullable(eq.getBonusPotential1()).orElse(0));
            equip.setBonusPotential2(Optional.ofNullable(eq.getBonusPotential2()).orElse(0));
            equip.setBonusPotential3(Optional.ofNullable(eq.getBonusPotential3()).orElse(0));
            equip.setBonusPotentialGrade(Optional.ofNullable(eq.getBonusPotentialGrade()).orElse(0).byteValue());
            equip.setSoulId(Optional.ofNullable(eq.getSoulId()).orElse(0));
            equip.setSoulOption(Optional.ofNullable(eq.getSoulOption()).orElse(0));
            equip.setSocket1(Optional.ofNullable(eq.getSocket1()).orElse(0));
            equip.setSocket2(Optional.ofNullable(eq.getSocket2()).orElse(0));
            equip.setSocket3(Optional.ofNullable(eq.getSocket3()).orElse(0));
            equip.setPlatinum(Optional.ofNullable(eq.getPlatinum()).orElse(0));
            equip.setReforge1(Optional.ofNullable(eq.getReforge1()).orElse(0));
            equip.setReforge2(Optional.ofNullable(eq.getReforge2()).orElse(0));
            equip.setReforge3(Optional.ofNullable(eq.getReforge3()).orElse(0));
            equip.setReforgeLock(Optional.ofNullable(eq.getReforgeLock()).orElse(0).byteValue());
            equip.setInfusion(Optional.ofNullable(eq.getInfusion()).orElse(0).byteValue());
            equip.setChaosStr(Optional.ofNullable(eq.getChaosStr()).orElse(0).shortValue());
            equip.setChaosDex(Optional.ofNullable(eq.getChaosDex()).orElse(0).shortValue());
            equip.setChaosInt(Optional.ofNullable(eq.getChaosInt()).orElse(0).shortValue());
            equip.setChaosLuk(Optional.ofNullable(eq.getChaosLuk()).orElse(0).shortValue());
            equip.setChaosHp(Optional.ofNullable(eq.getChaosHp()).orElse(0).shortValue());
            equip.setChaosMp(Optional.ofNullable(eq.getChaosMp()).orElse(0).shortValue());
            equip.setChaosWatk(Optional.ofNullable(eq.getChaosWatk()).orElse(0).shortValue());
            equip.setChaosMatk(Optional.ofNullable(eq.getChaosMatk()).orElse(0).shortValue());
            equip.setChaosWdef(Optional.ofNullable(eq.getChaosWdef()).orElse(0).shortValue());
            equip.setChaosMdef(Optional.ofNullable(eq.getChaosMdef()).orElse(0).shortValue());
            equip.setChaosAcc(Optional.ofNullable(eq.getChaosAcc()).orElse(0).shortValue());
            equip.setChaosAvoid(Optional.ofNullable(eq.getChaosAvoid()).orElse(0).shortValue());
            equip.setChaosSpeed(Optional.ofNullable(eq.getChaosSpeed()).orElse(0).shortValue());
            equip.setChaosJump(Optional.ofNullable(eq.getChaosJump()).orElse(0).shortValue());
            item = equip;
        } else {
            item = new Item(getItemId(), getPosition(), getQuantity(), getPetId());
        }
        item.setOwner(getOwner());
        item.setExpiration(getExpiration());
        item.setGiftFrom(getGiftFrom());
        item.setFlag(getFlag());
        return item;
    }
}
