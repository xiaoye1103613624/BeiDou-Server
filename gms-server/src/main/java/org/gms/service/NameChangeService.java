package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.config.GameConfig;
import org.gms.dao.entity.CharactersDO;
import org.gms.dao.entity.NamechangesDO;
import org.gms.dao.entity.RingsDO;
import org.gms.dao.mapper.CharactersMapper;
import org.gms.dao.mapper.InventoryitemsMapper;
import org.gms.dao.mapper.NamechangesMapper;
import org.gms.dao.mapper.RingsMapper;
import org.gms.manager.ServerManager;
import org.gms.util.I18nUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.gms.constants.id.ItemId;

import java.sql.Timestamp;
import java.util.List;

import static org.gms.dao.entity.table.InventoryitemsDOTableDef.INVENTORYITEMS_D_O;
import static org.gms.dao.entity.table.NamechangesDOTableDef.NAMECHANGES_D_O;
import static org.gms.dao.entity.table.RingsDOTableDef.RINGS_D_O;

/**
 * 改名服务类，提供角色改名的申请、应用和撤销功能。
 */
@Service
@AllArgsConstructor
@Slf4j
public class NameChangeService {

    /**
     * 改名记录数据访问对象
     */
    private final NamechangesMapper namechangesMapper;

    /**
     * 角色数据访问对象
     */
    private final CharactersMapper charactersMapper;

    /**
     * 戒指数据访问对象
     */
    private final RingsMapper ringsMapper;

    /**
     * 背包物品数据访问对象
     */
    private final InventoryitemsMapper inventoryitemsmapper;

    /**
     * 应用所有待处理的改名请求。
     * 服务器启动时调用，逐条处理所有未完成的改名记录。
     */
    public void applyAllNameChange() {
        List<NamechangesDO> namechangesDOList = getAllNameChanges();
        namechangesDOList.forEach(namechangesDO -> {
            try {
                // 通过Spring获取代理对象确保事务隔离
                ServerManager.getApplicationContext().getBean(NameChangeService.class).doNameChange(namechangesDO);
            } catch (Exception e) {
                log.error(I18nUtil.getLogMessage("Server.init.error4"), e);
            }
        });
    }

    /**
     * 应用单个角色的改名请求。
     * 角色下线时调用，检测是否有待处理的改名并执行。
     *
     * @param characterId   角色ID
     * @param characterName 当前角色名
     */
    public void applyNameChange(int characterId, String characterName) {
        List<NamechangesDO> namechangesDOList = namechangesMapper.selectListByQuery(QueryWrapper.create()
                .where(NAMECHANGES_D_O.COMPLETION_TIME.isNull()).and(NAMECHANGES_D_O.CHARACTERID.eq(characterId)));
        if (!namechangesDOList.isEmpty()) {
            NamechangesDO namechangesDO = namechangesDOList.getFirst();
            try {
                // 通过Spring获取代理对象确保事务隔离
                ServerManager.getApplicationContext().getBean(NameChangeService.class).doNameChange(NamechangesDO.builder()
                        .id(namechangesDO.getId())
                        .characterid(characterId)
                        .older(characterName)
                        .newer(namechangesDO.getNewer())
                        .build());
            } catch (Exception e) {
                log.error(I18nUtil.getLogMessage("Server.init.error4"), e);
            }
        }
    }

    /**
     * 获取所有待处理的改名记录（completion_time为null）。
     *
     * @return 改名记录列表
     */
    public List<NamechangesDO> getAllNameChanges() {
        return namechangesMapper.selectListByQuery(QueryWrapper.create().where(NAMECHANGES_D_O.COMPLETION_TIME.isNull()));
    }

    /**
     * 执行改名操作（事务性）。
     * 更新角色名称、戒指伴侣名称，标记改名完成时间，并删除改名卡道具。
     *
     * @param data 改名记录DO
     */
    @Transactional(rollbackFor = Exception.class)
    public void doNameChange(NamechangesDO data) {
        int accountid = charactersMapper.selectOneById(data.getCharacterid()).getAccountid();
        // 更新角色名称
        charactersMapper.update(CharactersDO.builder().id(data.getCharacterid()).name(data.getNewer()).build());
        // 更新戒指中的伴侣名称
        ringsMapper.updateByQuery(RingsDO.builder().partnername(data.getNewer()).build(), QueryWrapper.create().where(RINGS_D_O.PARTNERNAME.eq(data.getOlder())));
        // 标记改名完成时间
        namechangesMapper.update(NamechangesDO.builder().id(data.getId()).completionTime(new Timestamp(System.currentTimeMillis())).build());
        // 删除角色背包和账户商城中所有改名卡（解决撤销改名客户端闪退38错误）
        inventoryitemsmapper.deleteByQuery(QueryWrapper.create().where(INVENTORYITEMS_D_O.ITEMID.eq(ItemId.NAME_CHANGE)).and(INVENTORYITEMS_D_O.CHARACTERID.eq(data.getCharacterid()).or(INVENTORYITEMS_D_O.ACCOUNTID.eq(accountid))));
        log.info(I18nUtil.getLogMessage("CharacterService.doNameChange.info1"), data.getOlder(), data.getNewer());
    }

    /**
     * 注册改名请求。
     * 检查是否有未完成的改名或改名冷却时间未到。
     *
     * @param chr     角色对象
     * @param newName 新名称
     * @return 注册成功返回true，存在未完成改名或冷却未到返回false
     */
    public boolean registerNameChange(Character chr, String newName) {
        List<NamechangesDO> namechangesDOList = namechangesMapper.selectListByQuery(QueryWrapper.create()
                .where(NAMECHANGES_D_O.CHARACTERID.eq(chr.getId())));
        // 已有改名未生效或改名冷却未到
        if (!namechangesDOList.isEmpty() && namechangesDOList.stream().anyMatch(namechangesDO ->
                namechangesDO.getCompletionTime() == null || namechangesDO.getCompletionTime().getTime() + GameConfig.getServerLong("name_change_cooldown") > System.currentTimeMillis())) {
            return false;
        }
        namechangesMapper.insertSelective(NamechangesDO.builder().characterid(chr.getId()).older(chr.getName()).newer(newName).build());
        return true;
    }

    /**
     * 取消待处理的改名请求。
     *
     * @param chr        角色对象
     * @param needFinish 为true时只取消未完成的改名（completion_time为null）
     */
    public void cancelPendingNameChange(Character chr, boolean needFinish) {
        QueryWrapper queryWrapper = QueryWrapper.create().where(NAMECHANGES_D_O.CHARACTERID.eq(chr.getId()));
        if (needFinish) queryWrapper.and(NAMECHANGES_D_O.COMPLETION_TIME.isNull());
        namechangesMapper.deleteByQuery(queryWrapper);
    }
}