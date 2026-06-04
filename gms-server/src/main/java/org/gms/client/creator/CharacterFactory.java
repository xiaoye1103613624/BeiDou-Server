/*
    This file is part of the HeavenMS MapleStory Server
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
package org.gms.client.creator;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.SkinColor;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.config.GameConfig;
import org.gms.net.server.Server;
import org.gms.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.server.ItemInformationProvider;
import org.gms.util.PacketCreator;

/**
 * 【类型】CharacterFactory（abstract class），包 {@code org.gms.client.creator}。
 *
 * 角色创建工厂基类，提供创建新角色的统一入口（createNewCharacter），
 * 处理基础外观（发型/脸型/肤色/性别）、初始装备穿戴、属性校验与数据库持久化等共性逻辑。
 *
 * @author RonanLana
 */
public abstract class CharacterFactory {
    /** 日志记录器：用于记录角色创建过程中的重要事件和警告 */
    private static final Logger log = LoggerFactory.getLogger(CharacterFactory.class);

    /**
     * 创建新角色的核心方法。
     * 
     * <p>该方法处理新角色创建的完整流程，包括槽位检查、名称验证、
     * 角色基本信息设置、初始装备分配、数据有效性验证和数据库持久化。</p>
     * 
     * <p>返回值含义：
     * <ul>
     *   <li>0: 成功创建角色</li>
     *   <li>-1: 角色名称无效（已被占用或包含非法字符）</li>
     *   <li>-2: 数据验证失败或数据库插入失败</li>
     *   <li>-3: 角色槽位不足</li>
     * </ul></p>
     * 
     * @param c 客户端连接对象，包含用户会话信息
     * @param name 新角色的名称
     * @param face 角色的脸型ID
     * @param hair 角色的发型ID
     * @param skin 角色的肤色ID
     * @param gender 角色的性别（0-男性，1-女性）
     * @param recipe 角色创建配方，包含职业、等级、地图、装备等信息
     * @return 创建结果状态码（0为成功，负数为各种失败原因）
     */
    protected synchronized static int createNewCharacter(Client c, String name, int face, int hair, int skin, int gender, CharacterFactoryRecipe recipe) {
        // 检查角色槽位是否充足
        if (GameConfig.getServerBoolean("collective_chr_slot") ? c.getAvailableCharacterSlots() <= 0 : c.getAvailableCharacterWorldSlots() <= 0) {
            return -3; // 槽位不足
        }

        // 验证角色名称是否合法
        if (!Character.canCreateChar(name)) {
            return -1; // 名称无效
        }

        // 初始化新角色的基本信息
        Character newCharacter = Character.getDefault(c);
        newCharacter.setWorld(c.getWorld());
        newCharacter.setSkinColor(SkinColor.getById(skin));
        newCharacter.setGender(gender);
        newCharacter.setName(name);
        newCharacter.setHair(hair);
        newCharacter.setFace(face);

        // 设置角色职业、等级和起始地图
        newCharacter.setLevel(recipe.getLevel());
        newCharacter.setJob(recipe.getJob());
        newCharacter.setMapId(recipe.getMap());

        // 为新角色分配初始装备
        Inventory equipped = newCharacter.getInventory(InventoryType.EQUIPPED);
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        int top = recipe.getTop(), bottom = recipe.getBottom(), shoes = recipe.getShoes(), weapon = recipe.getWeapon();

        // 添加上衣装备（位置-5）
        if (top > 0) {
            Item eq_top = ii.getEquipById(top);
            eq_top.setPosition((byte) -5);
            equipped.addItemFromDB(eq_top);
        }

        // 添加裤子装备（位置-6）
        if (bottom > 0) {
            Item eq_bottom = ii.getEquipById(bottom);
            eq_bottom.setPosition((byte) -6);
            equipped.addItemFromDB(eq_bottom);
        }

        // 添加鞋子装备（位置-7）
        if (shoes > 0) {
            Item eq_shoes = ii.getEquipById(shoes);
            eq_shoes.setPosition((byte) -7);
            equipped.addItemFromDB(eq_shoes);
        }

        // 添加武器装备（位置-11）
        if (weapon > 0) {
            Item eq_weapon = ii.getEquipById(weapon);
            eq_weapon.setPosition((byte) -11);
            equipped.addItemFromDB(eq_weapon.copy());
        }

        // 验证角色数据的有效性，防止数据包篡改
        if (!MakeCharInfoValidator.isNewCharacterValid(newCharacter)) {
            log.warn("Owner from account {} tried to packet edit in character creation", c.getAccountName());
            return -2; // 数据验证失败
        }

        // 将新角色数据插入数据库
        if (!newCharacter.insertNewChar(recipe)) {
            return -2; // 数据库插入失败
        }
        
        // 向客户端发送新角色创建成功的通知
        c.sendPacket(PacketCreator.addNewCharEntry(newCharacter));

        // 在服务器中注册新角色
        Server.getInstance().createCharacterEntry(newCharacter);
        // 向GM广播新角色创建消息
        Server.getInstance().broadcastGMMessage(c.getWorld(), PacketCreator.sendYellowTip("[New Char]: " + c.getAccountName() + I18nUtil.getMessage("CharacterFactory.message1") + name));
        log.info("账号 {} 创建了角色 {}", c.getAccountName(), name);

        return 0; // 成功创建角色
    }
}