package org.gms.service;

import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.Stat;
import org.gms.client.inventory.*;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.constants.inventory.ItemConstants;
import org.gms.constants.string.ExtendType;
import org.gms.dao.entity.ExtendValueDO;

import org.gms.model.dto.GiveResourceReqDTO;
import org.gms.exception.BizException;


import org.gms.net.server.Server;
import org.gms.server.CashShop;
import org.gms.server.ItemInformationProvider;
import org.gms.util.I18nUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import static java.util.concurrent.TimeUnit.DAYS;


/**
 * 发放资源服务类
 * 提供向玩家发放各种资源的功能，包括：
 * - 点券（nxCredit、nxPrepaid、maplePoint）
 * - 金币（mesos）
 * - 经验值（exp）
 * - 物品（item）
 * - 装备（equip）
 * - 倍率（expRate、mesoRate、dropRate）
 * - GM权限
 * - 声望
 * - 传送
 */
@Service
@Slf4j
public class GiveService {
    /** 角色服务，用于更新角色倍率 */
    @Autowired
    CharacterService characterService;

    /**
     * 发放资源入口方法
     * 根据玩家ID判断是发放给单个玩家还是所有在线玩家
     *
     * @param submitData 发放请求数据
     */
    public void give(GiveResourceReqDTO submitData) {
        if (submitData.getPlayerId() == 0) {
            giveAllOnlineChr(submitData);
        } else {
            giveChr(submitData);
        }
    }

    /**
     * 向所有在线玩家发放资源
     * 根据资源类型分发到对应的发放方法
     *
     * @param submitData 发放请求数据
     */
    private void giveAllOnlineChr(GiveResourceReqDTO submitData) {
        // 根据类型分发：0/1/2→点券，3→金币，4→经验，5→物品，6→装备
        switch (submitData.getType()) {
            case 0:
            case 1:
            case 2:
                int cashType = switch (submitData.getType()) {
                    case 1 -> CashShop.NX_PREPAID;
                    case 2 -> CashShop.MAPLE_POINT;
                    default -> CashShop.NX_CREDIT;
                };
                giveNxAllOnlineChr(submitData.getQuantity(), cashType);
                break;
            case 3:
                giveMesosAllOnlineChr(submitData.getQuantity());
                break;
            case 4:
                giveExpAllOnlineChr(submitData.getQuantity());
                break;
            case 5:
                giveItemAllOnlineChr(submitData.getId(), Short.parseShort(submitData.getQuantity().toString()));
                break;
            case 6:
                giveEquipAllOnlineChr(submitData);
                break;
        }
    }

    /**
     * 向单个玩家发放资源
     * 先校验世界ID和角色ID，再根据资源类型分发
     *
     * @param submitData 发放请求数据
     */
    private void giveChr(GiveResourceReqDTO submitData) {
        Integer wId = submitData.getWorldId();
        Integer cId = submitData.getPlayerId();
        if (wId == null || wId < 0 || cId == null || cId < 1) {
            throw new BizException(I18nUtil.getExceptionMessage("CHR_OR_WORLD_ID_ERROR"));
        }
        Character chr = Server.getInstance()
                .getWorlds().get(wId)
                .getPlayerStorage().getCharacterById(cId);
        if (chr == null) throw new BizException(I18nUtil.getExceptionMessage("CHR_OFFLINE"));

        // 根据类型分发：0/1/2→点券，3→金币，4→经验，5→物品，6→装备，7-10→倍率，11→GM，12→声望，13→传送
        switch (submitData.getType()) {
            case 0:
            case 1:
            case 2:
                int cashType = switch (submitData.getType()) {
                    case 1 -> CashShop.NX_PREPAID;
                    case 2 -> CashShop.MAPLE_POINT;
                    default -> CashShop.NX_CREDIT;
                };
                giveNxChr(chr, submitData.getQuantity(), cashType);
                break;
            case 3:
                giveMesosChr(chr, submitData.getQuantity());
                break;
            case 4:
                giveExpChr(chr, submitData.getQuantity());
                break;
            case 5:
                giveItemChr(chr, submitData.getId(), Short.parseShort(submitData.getQuantity().toString()));
                break;
            case 6:
                giveEquipChr(chr, submitData);
                break;
            case 7:
            case 8:
            case 9:
            case 10:
                String rateType = switch (submitData.getType()) {
                    case 7 -> "expRate";
                    case 8 -> "mesoRate";
                    case 9 -> "dropRate";
                    default -> "None";
                };
                giveRateChr(chr, rateType, submitData.getRate());
                break;
            case 11:
                giveGMChr(chr, submitData.getQuantity());
                break;
            case 12:
                giveFameChr(chr, submitData.getQuantity());
                break;
            case 13:
                changeMap(chr, submitData.getQuantity());
                break;
        }
    }

    /**
     * 向所有在线玩家发放点券
     *
     * @param quantity 点券数量
     * @param type     点券类型（NX_CREDIT / NX_PREPAID / MAPLE_POINT）
     */
    private void giveNxAllOnlineChr(int quantity, int type) {
        Server.getInstance().getWorlds().forEach(world -> world.getPlayerStorage().getAllCharacters().forEach(chr -> {
            doGainCash(chr, type, quantity);
            chr.message(I18nUtil.getMessage("Give.Nx.All", quantity, getCashTypeName(type)));
        }));
        log.info(I18nUtil.getLogMessage("Give.Nx.All.info1", quantity, getCashTypeName(type)));
    }

    /**
     * 向单个玩家发放点券
     *
     * @param chr      目标角色
     * @param quantity 点券数量
     * @param type     点券类型
     */
    private void giveNxChr(Character chr, int quantity, int type) {
        doGainCash(chr, type, quantity);
        chr.message(I18nUtil.getMessage("Give.Nx.Chr", quantity, getCashTypeName(type)));
        log.info(I18nUtil.getLogMessage("Give.Nx.Chr.info1", chr.getId(), chr.getName(), quantity, getCashTypeName(type)));
    }

    /**
     * 获取点券类型的国际化名称
     *
     * @param type 点券类型
     * @return 点券类型名称
     */
    private String getCashTypeName(int type) {
        return switch (type) {
            case 1 -> I18nUtil.getMessage("Give.Nx.Type.1");
            case 2 -> I18nUtil.getMessage("Give.Nx.Type.2");
            default -> I18nUtil.getMessage("Give.Nx.Type.default");
        };
    }

    /**
     * 向所有在线玩家发放金币
     *
     * @param quantity 金币数量
     */
    private void giveMesosAllOnlineChr(int quantity) {
        Server.getInstance().getWorlds().forEach(world -> world.getPlayerStorage().getAllCharacters().forEach(chr -> {
            doGainMeso(chr, quantity);
            chr.message(I18nUtil.getMessage("Give.Mesos.All", quantity));
        }));
        log.info(I18nUtil.getLogMessage("Give.Mesos.All.info1", quantity));
    }

    /**
     * 向单个玩家发放金币
     *
     * @param chr      目标角色
     * @param quantity 金币数量
     */
    private void giveMesosChr(Character chr, int quantity) {
        doGainMeso(chr, quantity);
        chr.message(I18nUtil.getMessage("Give.Mesos.Chr", quantity));
        log.info(I18nUtil.getLogMessage("Give.Mesos.Chr.info1", chr.getId(), chr.getName(), quantity));
    }

    /**
     * 向所有在线玩家发放经验值
     *
     * @param quantity 经验值数量
     */
    private void giveExpAllOnlineChr(int quantity) {
        Server.getInstance().getWorlds().forEach(world -> world.getPlayerStorage().getAllCharacters().forEach(chr -> {
            doGainExp(chr, quantity);
            chr.message(I18nUtil.getMessage("Give.Exp.All", quantity));
        }));
        log.info(I18nUtil.getLogMessage("Give.Exp.All.info1", quantity));
    }

    /**
     * 向单个玩家发放经验值
     *
     * @param chr      目标角色
     * @param quantity 经验值数量
     */
    private void giveExpChr(Character chr, int quantity) {
        doGainExp(chr, quantity);
        chr.message(I18nUtil.getMessage("Give.Exp.Chr", quantity));
        log.info(I18nUtil.getLogMessage("Give.Exp.Chr.info1", chr.getId(), chr.getName(), quantity));
    }

    /**
     * 向所有在线玩家发放道具
     * 支持普通道具和宠物道具，宠物道具按天数计算有效期
     *
     * @param itemId   物品ID
     * @param quantity 发放数量
     */
    private void giveItemAllOnlineChr(int itemId, short quantity) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        String itemName = ii.getName(itemId);
        if (itemName == null) {
            throw new BizException(I18nUtil.getExceptionMessage("ITEM_NOT_FOUND"));
        }
        if (ItemConstants.getInventoryType(itemId).equals(InventoryType.EQUIP)) {
            throw new BizException(I18nUtil.getExceptionMessage("ONLY_SUPPORT_GIVE_ITEM"));
        }

        boolean isPet = ItemConstants.isPet(itemId);

        long expiration;
        int petId;
        if (isPet) {
            long days = Math.max(1, quantity);
            expiration = System.currentTimeMillis() + DAYS.toMillis(days);
            petId = Pet.createPet(itemId);
        } else {
            expiration = 0;
            petId = 0;
        }

        Server.getInstance().getWorlds().forEach(world -> world.getPlayerStorage().getAllCharacters().forEach(chr -> {
            if (isPet) {
                InventoryManipulator.addById(chr.getClient(), itemId, quantity, "WAdmin", petId, expiration);
                chr.message(I18nUtil.getMessage("Give.Pet.All", quantity, itemName));
            } else {
                InventoryManipulator.addById(chr.getClient(), itemId, quantity, "WAdmin", -1, (short) 0, -1);
                chr.message(I18nUtil.getMessage("Give.Item.All", quantity, itemName));
            }
        }));

        if (isPet) {
            log.info(I18nUtil.getLogMessage("Give.Pet.All.info1", quantity, itemId, itemName));
        } else {
            log.info(I18nUtil.getLogMessage("Give.Item.All.info1", quantity, itemId, itemName));
        }

    }

    /**
     * 向单个玩家发放道具
     * 支持普通道具和宠物道具，宠物道具按天数计算有效期
     *
     * @param chr      目标角色
     * @param itemId   物品ID
     * @param quantity 发放数量
     */
    private void giveItemChr(Character chr, int itemId, short quantity) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        String itemName = ii.getName(itemId);
        if (itemName == null) {
            throw new BizException(I18nUtil.getExceptionMessage("ITEM_NOT_FOUND"));
        }
        if (ItemConstants.getInventoryType(itemId).equals(InventoryType.EQUIP)) {
            throw new BizException(I18nUtil.getExceptionMessage("ONLY_SUPPORT_GIVE_ITEM"));
        }

        boolean isPet = ItemConstants.isPet(itemId);

        long expiration = 0;
        int petId = 0;
        if (isPet) {
            long days = Math.max(1, quantity);
            expiration = System.currentTimeMillis() + DAYS.toMillis(days);
            petId = Pet.createPet(itemId);
        }

        if (isPet) {
            InventoryManipulator.addById(chr.getClient(), itemId, quantity, "WAdmin", petId, expiration);
            chr.message(I18nUtil.getMessage("Give.Pet.Chr", quantity, itemName));
        } else {
            InventoryManipulator.addById(chr.getClient(), itemId, quantity, "WAdmin", -1, (short) 0, -1);
            chr.message(I18nUtil.getMessage("Give.Item.Chr", quantity, itemName));
        }

        if (isPet) {
            log.info(I18nUtil.getLogMessage("Give.Pet.Chr.info1", chr.getId(), chr.getName(), quantity, itemId, itemName));
        } else {
            log.info(I18nUtil.getLogMessage("Give.Item.Chr.info1", chr.getId(), chr.getName(), quantity, itemId, itemName));
        }
    }

    /**
     * 向所有在线玩家发放装备
     *
     * @param submitData 发放请求数据，包含装备ID及各项属性值
     */
    private void giveEquipAllOnlineChr(GiveResourceReqDTO submitData) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        String itemName = ii.getName(submitData.getId());
        if (ii.getEquipById(submitData.getId()) == null || itemName == null) {
            throw new BizException(I18nUtil.getExceptionMessage("EQUIP_NOT_FOUND"));
        }
        if (!ItemConstants.getInventoryType(submitData.getId()).equals(InventoryType.EQUIP)) {
            throw new BizException(I18nUtil.getExceptionMessage("ONLY_SUPPORT_GIVE_EQUIP"));
        }
        Server.getInstance().getWorlds().forEach(world -> world.getPlayerStorage().getAllCharacters().forEach(chr -> {
            chr.gainEquip(
                    submitData.getId(),
                    submitData.getStr(),
                    submitData.getDex(),
                    submitData.get_int(),
                    submitData.getLuk(),
                    submitData.getHp(),
                    submitData.getMp(),
                    submitData.getPAtk(),
                    submitData.getMAtk(),
                    submitData.getPDef(),
                    submitData.getMDef(),
                    submitData.getAcc(),
                    submitData.getAvoid(),
                    submitData.getHands(),
                    submitData.getSpeed(),
                    submitData.getJump(),
                    submitData.getUpgradeSlot(),
                    submitData.getExpire()
            );
            chr.message(I18nUtil.getMessage("Give.Equip.All", submitData.getId().toString(), itemName));
        }));
        log.info(I18nUtil.getLogMessage("Give.Equip.All.info1",
                submitData.getId(),
                itemName,
                submitData.getStr(),
                submitData.getDex(),
                submitData.get_int(),
                submitData.getLuk(),
                submitData.getHp(),
                submitData.getMp(),
                submitData.getPAtk(),
                submitData.getMAtk(),
                submitData.getPDef(),
                submitData.getMDef(),
                submitData.getAcc(),
                submitData.getAvoid(),
                submitData.getHands(),
                submitData.getSpeed(),
                submitData.getJump(),
                submitData.getUpgradeSlot(),
                submitData.getExpire()
        ));
    }

    /**
     * 向单个玩家发放装备
     *
     * @param chr        目标角色
     * @param submitData 发放请求数据，包含装备ID及各项属性值
     */
    private void giveEquipChr(Character chr, GiveResourceReqDTO submitData) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        String itemName = ii.getName(submitData.getId());
        if (itemName == null) {
            throw new BizException(I18nUtil.getExceptionMessage("EQUIP_NOT_FOUND"));
        }

        if (!ItemConstants.getInventoryType(submitData.getId()).equals(InventoryType.EQUIP)) {
            throw new BizException(I18nUtil.getExceptionMessage("ONLY_SUPPORT_GIVE_EQUIP"));
        }
        chr.gainEquip(
                submitData.getId(),
                submitData.getStr(),
                submitData.getDex(),
                submitData.get_int(),
                submitData.getLuk(),
                submitData.getHp(),
                submitData.getMp(),
                submitData.getPAtk(),
                submitData.getMAtk(),
                submitData.getPDef(),
                submitData.getMDef(),
                submitData.getAcc(),
                submitData.getAvoid(),
                submitData.getHands(),
                submitData.getSpeed(),
                submitData.getJump(),
                submitData.getUpgradeSlot(),
                submitData.getExpire()
        );
        chr.message(I18nUtil.getMessage("Give.Equip.Chr", submitData.getId().toString(), itemName));
        log.info(I18nUtil.getLogMessage("Give.Equip.Chr.info1",
                submitData.getId(),
                itemName,
                submitData.getStr(),
                submitData.getDex(),
                submitData.get_int(),
                submitData.getLuk(),
                submitData.getHp(),
                submitData.getMp(),
                submitData.getPAtk(),
                submitData.getMAtk(),
                submitData.getPDef(),
                submitData.getMDef(),
                submitData.getAcc(),
                submitData.getAvoid(),
                submitData.getHands(),
                submitData.getSpeed(),
                submitData.getJump(),
                submitData.getUpgradeSlot(),
                submitData.getExpire(),
                chr.getId(),
                chr.getName()
        ));
    }

    /**
     * 向单个玩家设置倍率
     * 倍率必须大于0，保存到扩展值表
     *
     * @param chr  目标角色
     * @param type 倍率类型（expRate / mesoRate / dropRate）
     * @param rate 倍率值
     */
    private void giveRateChr(Character chr, String type, float rate) {
        if (rate <= 0) {
            throw new BizException(I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_ZERO", "rate"));
        }
        ExtendValueDO data = ExtendValueDO.builder()
                .extendId(String.valueOf(chr.getId()))
                .extendType(ExtendType.CHARACTER_EXTEND.getType())
                .extendName(type)
                .extendValue(String.valueOf(rate))
                .build();
        characterService.updateRate(data);

        chr.message(I18nUtil.getMessage("Give.Rate.Chr", type, rate));
        log.info(I18nUtil.getLogMessage("Give.Rate.Chr.info1", chr.getId(), chr.getName(), type, rate));
    }

    /**
     * 设置角色的GM等级
     * GM等级低于3先取消隐藏再设置权限；等级>=3先设置权限再隐藏，否则无权限执行hide
     *
     * @param chr   目标角色
     * @param level GM等级（0-127）
     */
    private void giveGMChr(Character chr, Integer level) {
        if (level < 0  || level > 127) {
            throw new BizException(I18nUtil.getExceptionMessage("ILLEGAL_PARAMETERS",level));
        }
        // 按以下顺序 hide，否则因无 GM 权限无法执行 hide/unhide
        // GM 等级 < 3 先解除隐藏再设置；>= 3 先设置权限再隐藏
        if (level < 3) {
            chr.hide(false);
            chr.setGMLevel(level);
        } else {
            chr.setGMLevel(level);
            chr.hide(true);
        }
        chr.message(I18nUtil.getMessage("Give.GM.Chr", level));
        log.info(I18nUtil.getLogMessage("Give.GM.Chr.info1", chr.getId(), chr.getName(), level));
    }

    /**
     * 设置角色的声望值
     *
     * @param chr  目标角色
     * @param fame 声望值
     */
    private void giveFameChr(Character chr, Integer fame) {
        chr.setFame(fame);
        chr.updateSingleStat(Stat.FAME, fame);
        chr.message(I18nUtil.getMessage("Give.Fame.Chr", fame));
        log.info(I18nUtil.getLogMessage("Give.Fame.Chr.info1", chr.getId(), chr.getName(), fame));
    }

    /**
     * 传送角色到指定地图
     * 自由市场（910000000）需要保存位置再传送
     *
     * @param chr   目标角色
     * @param mapId 目标地图ID
     */
    private void changeMap(Character chr, Integer mapId) {
        if (910000000 == mapId) {
            chr.saveLocation("FREE_MARKET");
            chr.changeMap(mapId, "out00");
        } else {
            chr.changeMap(mapId);
        }
        chr.message(I18nUtil.getMessage("Give.Map.Chr", mapId));
        log.info(I18nUtil.getLogMessage("Give.Map.Chr.info1", chr.getId(), chr.getName(), mapId));
    }

    /**
     * 增加点券（带边界保护）
     * 不允许小于0，不允许超过Integer.MAX_VALUE
     *
     * @param chr      目标角色
     * @param type     点券类型
     * @param quantity 点券数量
     */
    private void doGainCash(Character chr, int type, int quantity) {
        int cash = chr.getCashShop().getCash(type);
        long sum = (long) cash + (long) quantity;
        // 点券边界保护：不允许小于 0（否则商城会出错），不允许超过 Integer.MAX_VALUE
        if (sum < 0) {
            quantity = -cash;
        }
        if (sum > Integer.MAX_VALUE) {
            quantity = Integer.MAX_VALUE - cash;
        }
        chr.getCashShop().gainCash(type, quantity);
    }

    /**
     * 增加经验值（带边界保护）
     * 最低只能将经验清零，不允许溢出
     *
     * @param chr      目标角色
     * @param quantity 经验值数量
     */
    private void doGainExp(Character chr, int quantity) {
        int exp = chr.getExp();
        long sum = (long) exp + (long) quantity;
        // 经验边界保护：最低只能将经验清零，不允许溢出
        if (sum < 0) {
            sum = -exp;
        } else {
            sum = quantity;
        }
        chr.gainExp((int) sum);
    }

    /**
     * 增加金币（带边界保护）
     * 不允许小于0，不允许超过Integer.MAX_VALUE
     *
     * @param chr      目标角色
     * @param quantity 金币数量
     */
    private void doGainMeso(Character chr, int quantity) {
        int meso = chr.getMeso();
        long sum = (long) meso + (long) quantity;
        // 金币边界保护：不允许小于 0，不允许超过 Integer.MAX_VALUE
        if (sum < 0) {
            quantity = -meso;
        }
        if (sum > Integer.MAX_VALUE) {
            quantity = Integer.MAX_VALUE - meso;
        }
        chr.gainMeso(quantity);
    }
}