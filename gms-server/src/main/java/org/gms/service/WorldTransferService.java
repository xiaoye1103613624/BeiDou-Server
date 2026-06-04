package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.client.DefaultDates;
import org.gms.config.GameConfig;
import org.gms.dao.entity.AccountsDO;
import org.gms.dao.entity.BuddiesDO;
import org.gms.dao.entity.CharactersDO;
import org.gms.dao.entity.WorldtransfersDO;
import org.gms.dao.mapper.BuddiesMapper;
import org.gms.dao.mapper.CharactersMapper;
import org.gms.dao.mapper.WorldtransfersMapper;
import org.gms.manager.ServerManager;
import org.gms.net.server.Server;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import static org.gms.dao.entity.table.CharactersDOTableDef.CHARACTERS_D_O;
import static org.gms.dao.entity.table.WorldtransfersDOTableDef.WORLDTRANSFERS_D_O;

/**
 * 【业务服务】WorldTransferService：角色转区服务类，负责角色跨大区转移的申请、校验和执行。
 * 
 * <p>提供转区申请注册、转区资格校验、转区执行等功能。转区后角色金币上限为100万，
 * 会自动退出公会并清除好友关系。</p>
 */
@Service
@AllArgsConstructor
@Slf4j
public class WorldTransferService {
    
    /** 转区记录数据访问接口 */
    private final WorldtransfersMapper worldtransfersMapper;
    /** 角色数据访问接口 */
    private final CharactersMapper charactersMapper;
    /** 账号服务 */
    private final AccountService accountService;
    /** 好友数据访问接口 */
    private final BuddiesMapper buddiesMapper;

    /**
     * 应用所有待处理的转区请求。
     * 
     * <p>服务启动时调用，处理所有未完成的转区记录。</p>
     */
    public void applyAllWorldTransfer() {
        List<WorldtransfersDO> worldtransfersDOList = worldtransfersMapper.selectListByQuery(QueryWrapper.create()
                .where(WORLDTRANSFERS_D_O.COMPLETION_TIME.isNull()));
        worldtransfersDOList.forEach(worldtransfersDO -> {
            try {
                if (checkWorldTransferEligibility(worldtransfersDO)) {
                    ServerManager.getApplicationContext().getBean(WorldTransferService.class).doWorldTransfer(worldtransfersDO);
                }
            } catch (Exception e) {
                log.error(I18nUtil.getLogMessage("Server.init.error5"), e);
            }
        });
    }

    /**
     * 校验角色是否具备转区资格。
     * 
     * <p>校验条件：
     * <ul>
     *   <li>服务器配置允许转区（allow_cash_shop_world_transfer=true）</li>
     *   <li>角色存在</li>
     *   <li>角色未结婚（partnerId为空）</li>
     *   <li>账号未被封禁</li>
     *   <li>目标大区角色名未被占用</li>
     *   <li>目标大区存在</li>
     * </ul></p>
     * 
     * @param data 转区记录
     * @return 是否具备转区资格
     */
    public boolean checkWorldTransferEligibility(WorldtransfersDO data) {
        // 检查服务器配置是否允许转区
        if (!GameConfig.getServerBoolean("allow_cash_shop_world_transfer")) {
            return false;
        }
        
        // 获取角色信息
        CharactersDO charactersDO = charactersMapper.selectOneById(data.getCharacterid());
        if (charactersDO == null) {
            return false;
        }
        
        // 判断是否结婚
        if (charactersDO.getPartnerId() != null) {
            return false;
        }
        
        // 判断账号是否被封禁
        AccountsDO accountsDO = accountService.findById(charactersDO.getAccountid());
        if (accountsDO == null) {
            return false;
        }
        if (accountsDO.getBanned() != null && accountsDO.getBanned()) {
            return false;
        }
        if (accountsDO.getTempban() != null && !Objects.equals(accountsDO.getTempban().toLocalDateTime(), DefaultDates.getTempban())) {
            return false;
        }
        
        // 判断目标大区角色名是否被占用
        long count = charactersMapper.selectCountByQuery(QueryWrapper.create()
                .where(CHARACTERS_D_O.NAME.eq(charactersDO.getName()))
                .and(CHARACTERS_D_O.WORLD.eq(data.getTo())));
        if (count > 0) {
            return false;
        }
        
        // 判断目标大区是否存在
        return Server.getInstance().getWorld(data.getTo()) != null;
    }

    /**
     * 执行转区操作。
     * 
     * <p>转区流程：
     * <ol>
     *   <li>更新角色的world字段为目标大区</li>
     *   <li>限制金币上限为100万</li>
     *   <li>清除公会信息（guildid=0, guildrank=0）</li>
     *   <li>删除好友关系（双向）</li>
     *   <li>更新转区记录的完成时间</li>
     * </ol></p>
     * 
     * @param data 转区记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void doWorldTransfer(WorldtransfersDO data) {
        // 获取角色信息
        CharactersDO charactersDO = charactersMapper.selectOneById(data.getCharacterid());
        RequireUtil.requireNotNull(charactersDO, I18nUtil.getLogMessage("UNKNOWN_CHARACTER"));
        
        // 更新角色信息：大区、金币上限、公会信息
        charactersMapper.update(CharactersDO.builder()
                .id(charactersDO.getId())
                .world(data.getTo())
                .meso(Math.min(charactersDO.getMeso(), 1000000))
                .guildid(0)
                .guildrank(0)
                .build());
        
        // 删除好友关系（作为主动方和被动方）
        buddiesMapper.delete(BuddiesDO.builder().characterid(charactersDO.getId()).build());
        buddiesMapper.delete(BuddiesDO.builder().buddyid(charactersDO.getId()).build());
        
        // 更新转区记录为已完成
        worldtransfersMapper.update(WorldtransfersDO.builder().id(data.getId()).completionTime(new Timestamp(System.currentTimeMillis())).build());
        
        log.info(I18nUtil.getLogMessage("CharacterService.doWorldTransfer.info1"), data.getFrom(), data.getTo());
    }

    /**
     * 注册转区申请。
     * 
     * <p>检查转区冷却时间，若冷却未结束则拒绝申请。</p>
     * 
     * @param chr 角色对象
     * @param newWorld 目标大区ID
     * @return 是否注册成功
     */
    public boolean registerWorldTransfer(Character chr, int newWorld) {
        List<WorldtransfersDO> worldTransfersDOList = worldtransfersMapper.selectListByQuery(QueryWrapper.create()
                .where(WORLDTRANSFERS_D_O.CHARACTERID.eq(chr.getId())));
        
        // 检查是否已有未完成的转区或转区冷却未结束
        if (!worldTransfersDOList.isEmpty() && worldTransfersDOList.stream().anyMatch(worldtransfersDO ->
                worldtransfersDO.getCompletionTime() == null || 
                worldtransfersDO.getCompletionTime().getTime() + GameConfig.getServerLong("world_transfer_cooldown") > System.currentTimeMillis())) {
            return false;
        }
        
        // 插入转区申请记录
        worldtransfersMapper.insert(WorldtransfersDO.builder()
                .characterid(chr.getId())
                .from(chr.getWorld())
                .to(newWorld)
                .build());
        return true;
    }

    /**
     * 取消待处理的转区申请。
     * 
     * @param chr 角色对象
     * @param needFinish 是否只取消未完成的申请
     */
    public void cancelPendingWorldTransfer(Character chr, boolean needFinish) {
        QueryWrapper queryWrapper = QueryWrapper.create().where(WORLDTRANSFERS_D_O.CHARACTERID.eq(chr.getId()));
        if (needFinish) {
            queryWrapper.and(WORLDTRANSFERS_D_O.COMPLETION_TIME.isNull());
        }
        worldtransfersMapper.deleteByQuery(queryWrapper);
    }
}