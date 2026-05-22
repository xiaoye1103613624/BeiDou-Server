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
 * 【业务服务】NameChangeService：封装角色改名（注册申请、应用改名、撤销改名）相关的业务逻辑。
 *
 * <p>改名流程：</p>
 * <ol>
 *   <li>玩家使用改名卡 → {@link #registerNameChange(Character, String)} 注册改名申请</li>
 *   <li>服务器启动或玩家下线 → {@link #applyAllNameChange()} / {@link #applyNameChange(int, String)}
 *       调用 {@link #doNameChange(NamechangesDO)} 执行实际改名</li>
 *   <li>改名时同步更新角色表、戒指表（伴侣名称），并清理背包中的改名卡道具</li>
 * </ol>
 */
@Service
@AllArgsConstructor
@Slf4j
public class NameChangeService {
    private final NamechangesMapper namechangesMapper;
    private final CharactersMapper charactersMapper;
    private final RingsMapper ringsMapper;
    private final InventoryitemsMapper inventoryitemsmapper;

    /**
     * 批量应用所有待处理的改名申请（completion_time 为 null 的记录）。
     *
     * <p>通常在服务器启动时调用，通过 Spring 代理确保每条改名操作拥有独立事务。</p>
     */
    public void applyAllNameChange() {
        List<NamechangesDO> namechangesDOList = getAllNameChanges();
        namechangesDOList.forEach(namechangesDO -> {
            try {
                // 事务隔离：每条改名在独立事务中执行
                ServerManager.getApplicationContext().getBean(NameChangeService.class).doNameChange(namechangesDO);
            } catch (Exception e) {
                log.error(I18nUtil.getLogMessage("Server.init.error4"), e);
            }
        });
    }

    /**
     * 检测指定角色是否有待处理的改名申请，若有则立即执行。
     *
     * <p>通常在玩家下线时调用，确保离线期间完成改名。</p>
     *
     * @param characterId   角色 ID
     * @param characterName 角色当前名称
     */
    public void applyNameChange(int characterId, String characterName) {
        // 查询该角色未完成（completion_time IS NULL）的改名记录
        List<NamechangesDO> namechangesDOList = namechangesMapper.selectListByQuery(QueryWrapper.create()
                .where(NAMECHANGES_D_O.COMPLETION_TIME.isNull()).and(NAMECHANGES_D_O.CHARACTERID.eq(characterId)));
        if (!namechangesDOList.isEmpty()) {
            NamechangesDO namechangesDO = namechangesDOList.getFirst();
            try {
                // 通过Spring代理确保事务生效
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
     * 获取所有待处理的改名记录（completion_time 为 null）。
     *
     * @return 待处理的改名记录列表
     */
    public List<NamechangesDO> getAllNameChanges() {
        return namechangesMapper.selectListByQuery(QueryWrapper.create().where(NAMECHANGES_D_O.COMPLETION_TIME.isNull()));
    }

    /**
     * 执行实际改名操作（事务方法）。
     *
     * <p>在一个事务中完成以下操作：</p>
     * <ol>
     *   <li>更新 {@code characters} 表的 name 字段为新名称</li>
     *   <li>更新 {@code rings} 表中所有 partnerName 为旧名称的记录</li>
     *   <li>标记改名记录为已完成（写入 completion_time）</li>
     *   <li>删除角色背包和账户商城中所有改名卡道具（防止重复改名撤销导致客户端崩溃）</li>
     * </ol>
     *
     * @param data 改名数据（characterid、older、newer 必填）
     */
    @Transactional(rollbackFor = Exception.class)
    public void doNameChange(NamechangesDO data) {
        int accountid = charactersMapper.selectOneById(data.getCharacterid()).getAccountid();
        // 1. 更新角色名
        charactersMapper.update(CharactersDO.builder().id(data.getCharacterid()).name(data.getNewer()).build());
        // 2. 更新戒指表中的伴侣名称
        ringsMapper.updateByQuery(RingsDO.builder().partnername(data.getNewer()).build(), QueryWrapper.create().where(RINGS_D_O.PARTNERNAME.eq(data.getOlder())));
        // 3. 标记改名完成
        namechangesMapper.update(NamechangesDO.builder().id(data.getId()).completionTime(new Timestamp(System.currentTimeMillis())).build());
        // 4. 删除背包和商城中所有改名卡（避免撤销改名导致客户端38错误闪退）
        inventoryitemsmapper.deleteByQuery(QueryWrapper.create().where(INVENTORYITEMS_D_O.ITEMID.eq(ItemId.NAME_CHANGE)).and(INVENTORYITEMS_D_O.CHARACTERID.eq(data.getCharacterid()).or(INVENTORYITEMS_D_O.ACCOUNTID.eq(accountid))));
        log.info(I18nUtil.getLogMessage("CharacterService.doNameChange.info1"), data.getOlder(), data.getNewer());
    }

    /**
     * 注册改名申请。
     *
     * <p>校验是否满足改名条件：</p>
     * <ul>
     *   <li>无未完成的改名申请</li>
     *   <li>距上次改名已完成冷却时间（配置项 {@code name_change_cooldown}）</li>
     * </ul>
     *
     * @param chr     发起改名的角色
     * @param newName 目标新名称
     * @return true 表示注册成功，false 表示不满足改名条件
     */
    public boolean registerNameChange(Character chr, String newName) {
        List<NamechangesDO> namechangesDOList = namechangesMapper.selectListByQuery(QueryWrapper.create()
                .where(NAMECHANGES_D_O.CHARACTERID.eq(chr.getId())));
        // 已有改名未生效或改名未冷却
        if (!namechangesDOList.isEmpty() && namechangesDOList.stream().anyMatch(namechangesDO ->
                namechangesDO.getCompletionTime() == null || namechangesDO.getCompletionTime().getTime() + GameConfig.getServerLong("name_change_cooldown") > System.currentTimeMillis())) {
            return false;
        }
        namechangesMapper.insertSelective(NamechangesDO.builder().characterid(chr.getId()).older(chr.getName()).newer(newName).build());
        return true;
    }

    /**
     * 取消待处理的改名申请。
     *
     * <p>根据 {@code needFinish} 参数决定删除范围：true 时仅删除未完成的记录，false 时删除该角色所有改名记录。</p>
     *
     * @param chr        目标角色
     * @param needFinish true 仅取消未完成的；false 清除所有改名记录
     */
    public void cancelPendingNameChange(Character chr, boolean needFinish) {
        QueryWrapper queryWrapper = QueryWrapper.create().where(NAMECHANGES_D_O.CHARACTERID.eq(chr.getId()));
        if (needFinish) queryWrapper.and(NAMECHANGES_D_O.COMPLETION_TIME.isNull());
        namechangesMapper.deleteByQuery(queryWrapper);
    }
}
