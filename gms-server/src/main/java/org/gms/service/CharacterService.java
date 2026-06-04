package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.*;
import org.gms.client.Character;
import org.gms.client.keybind.KeyBinding;
import org.gms.config.GameConfig;
import org.gms.constants.id.MapId;
import org.gms.constants.string.ExtendType;
import org.gms.dao.entity.*;
import org.gms.dao.mapper.*;
import org.gms.model.dto.ChrOnlineListReqDTO;
import org.gms.model.dto.ChrOnlineListRtnDTO;
import org.gms.exception.BizException;
import org.gms.model.pojo.SkillEntry;
import org.gms.net.server.Server;
import org.gms.net.server.guild.GuildCharacter;
import org.gms.net.server.world.Messenger;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.PartyCharacter;
import org.gms.net.server.world.World;
import org.gms.server.Storage;
import org.gms.server.life.MobSkill;
import org.gms.server.life.MobSkillFactory;
import org.gms.server.life.MobSkillType;
import org.gms.server.maps.*;
import org.gms.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.*;

import static com.mybatisflex.core.query.QueryMethods.dateDiff;
import static com.mybatisflex.core.query.QueryMethods.now;
import static org.gms.dao.entity.table.AccountsDOTableDef.ACCOUNTS_D_O;
import static org.gms.dao.entity.table.AreaInfoDOTableDef.AREA_INFO_D_O;
import static org.gms.dao.entity.table.BbsRepliesDOTableDef.BBS_REPLIES_D_O;
import static org.gms.dao.entity.table.BbsThreadsDOTableDef.BBS_THREADS_D_O;
import static org.gms.dao.entity.table.BuddiesDOTableDef.BUDDIES_D_O;
import static org.gms.dao.entity.table.CharactersDOTableDef.CHARACTERS_D_O;
import static org.gms.dao.entity.table.CooldownsDOTableDef.COOLDOWNS_D_O;
import static org.gms.dao.entity.table.EventstatsDOTableDef.EVENTSTATS_D_O;
import static org.gms.dao.entity.table.ExtendValueDOTableDef.EXTEND_VALUE_D_O;
import static org.gms.dao.entity.table.FamelogDOTableDef.FAMELOG_D_O;
import static org.gms.dao.entity.table.FamilyCharacterDOTableDef.FAMILY_CHARACTER_D_O;
import static org.gms.dao.entity.table.FredstorageDOTableDef.FREDSTORAGE_D_O;
import static org.gms.dao.entity.table.KeymapDOTableDef.KEYMAP_D_O;
import static org.gms.dao.entity.table.MonsterbookDOTableDef.MONSTERBOOK_D_O;
import static org.gms.dao.entity.table.PlayerdiseasesDOTableDef.PLAYERDISEASES_D_O;
import static org.gms.dao.entity.table.SavedlocationsDOTableDef.SAVEDLOCATIONS_D_O;
import static org.gms.dao.entity.table.ServerQueueDOTableDef.SERVER_QUEUE_D_O;
import static org.gms.dao.entity.table.SkillmacrosDOTableDef.SKILLMACROS_D_O;
import static org.gms.dao.entity.table.SkillsDOTableDef.SKILLS_D_O;
import static org.gms.dao.entity.table.TrocklocationsDOTableDef.TROCKLOCATIONS_D_O;
import static org.gms.dao.entity.table.WishlistsDOTableDef.WISHLISTS_D_O;

/**
 * 【业务服务】CharacterService：封装角色相关应用逻辑与数据协作。
 * 提供角色的增删改查、排名查询、数据持久化等功能。
 */
@Service
@AllArgsConstructor
@Slf4j
public class CharacterService {
    /** 扩展值数据访问接口 */
    private final ExtendValueMapper extendValueMapper;
    /** 角色数据访问接口 */
    private final CharactersMapper charactersMapper;
    /** 技能数据访问接口 */
    private final SkillsMapper skillsMapper;
    /** 技能宏数据访问接口 */
    private final SkillmacrosMapper skillmacrosMapper;
    /** 公会数据访问接口 */
    private final GuildsMapper guildsMapper;
    /** 好友数据访问接口 */
    private final BuddiesMapper buddiesMapper;
    /** BBS帖子数据访问接口 */
    private final BbsThreadsMapper bbsThreadsMapper;
    /** BBS回复数据访问接口 */
    private final BbsRepliesMapper bbsRepliesMapper;
    /** 愿望清单数据访问接口 */
    private final WishlistsMapper wishlistsMapper;
    /** 冷却数据访问接口 */
    private final CooldownsMapper cooldownsMapper;
    /** 玩家疾病数据访问接口 */
    private final PlayerdiseasesMapper playerdiseasesMapper;
    /** 区域信息数据访问接口 */
    private final AreaInfoMapper areaInfoMapper;
    /** 怪物图鉴数据访问接口 */
    private final MonsterbookMapper monsterbookMapper;
    /** 家族角色数据访问接口 */
    private final FamilyCharacterMapper familyCharacterMapper;
    /** 声望日志数据访问接口 */
    private final FamelogMapper famelogMapper;
    /** 背包服务 */
    private final InventoryService inventoryService;
    /** 任务服务 */
    private final QuestService questService;
    /** 仓库数据访问接口 */
    private final FredstorageMapper fredstorageMapper;
    /** 拍卖行服务 */
    private final MtsService mtsService;
    /** 按键映射数据访问接口 */
    private final KeymapMapper keymapMapper;
    /** 保存位置数据访问接口 */
    private final SavedlocationsMapper savedlocationsMapper;
    /** 特洛克位置数据访问接口 */
    private final TrocklocationsMapper trocklocationsMapper;
    /** 事件统计数据访问接口 */
    private final EventstatsMapper eventstatsMapper;
    /** 服务器队列数据访问接口 */
    private final ServerQueueMapper serverQueueMapper;
    /** 改名服务 */
    private final NameChangeService nameChangeService;
    /** 跨服服务 */
    private final WorldTransferService worldTransferService;

    /**
     * 根据ID查询角色
     * @param id 角色ID
     * @return 角色实体
     */
    public CharactersDO findById(int id) {
        return charactersMapper.selectOneById(id);
    }

    /**
     * 更新角色信息
     * @param condition 角色实体
     */
    public void update(CharactersDO condition) {
        charactersMapper.update(condition);
    }

    /**
     * 获取在线角色列表（分页）
     * @param request 查询条件
     * @return 分页结果
     */
    public Page<ChrOnlineListRtnDTO> getChrOnlineList(ChrOnlineListReqDTO request) {
        Collection<Character> chrList = Server.getInstance().getWorld(request.getWorld()).getPlayerStorage().getAllCharacters();
        return BasePageUtil.create(chrList, request)
                .filter(chr -> (Objects.isNull(request.getId()) || Objects.equals(chr.getId(), request.getId()))
                        && (RequireUtil.isEmpty(request.getName()) || chr.getName().contains(request.getName()))
                        && (Objects.isNull(request.getMap()) || Objects.equals(chr.getMap().getId(), request.getMap())))
                .page(chr -> ChrOnlineListRtnDTO.builder()
                        .id(chr.getId())
                        .name(chr.getName())
                        .map(chr.getMap().getId())
                        .job(chr.getJob().getId())
                        .jobName(chr.getJob().getName())
                        .level(chr.getLevel())
                        .gm(chr.gmLevel())
                        .build());
    }

    /**
     * 更新角色倍率（经验/掉落/金币）
     * @param data 扩展值数据
     */
    public void updateRate(ExtendValueDO data) {
        checkName(data);
        data.setExtendType(ExtendType.CHARACTER_EXTEND.getType());
        ExtendValueDO extendValueDO = ExtendUtil.getExtendValue(data.getExtendId(), data.getExtendType(), data.getExtendName());
        if (extendValueDO == null) {
            extendValueMapper.insertSelective(data);
        } else {
            data.setCreateTime(null);
            data.setUpdateTime(new Date(System.currentTimeMillis()));
            extendValueMapper.update(data);
        }

        Character character = getCharacter(data);
        character.resetPlayerRates();
        character.setWorldRates();
        character.setCouponRates();
    }

    /**
     * 重置单个倍率
     * @param data 扩展值数据
     */
    public void resetRate(ExtendValueDO data) {
        checkName(data);
        extendValueMapper.deleteByQuery(QueryWrapper.create()
                .where(EXTEND_VALUE_D_O.EXTEND_ID.eq(data.getExtendId()))
                .and(EXTEND_VALUE_D_O.EXTEND_TYPE.eq(ExtendType.CHARACTER_EXTEND.getType()))
                .and(EXTEND_VALUE_D_O.EXTEND_NAME.eq(data.getExtendName())));
        Character character = getCharacter(data);
        character.resetPlayerRates();
        character.setWorldRates();
        character.setCouponRates();
    }

    /**
     * 重置所有倍率（经验/掉落/金币）
     * @param data 扩展值数据
     */
    public void resetRates(ExtendValueDO data) {
        check(data);
        extendValueMapper.deleteByQuery(QueryWrapper.create()
                .where(EXTEND_VALUE_D_O.EXTEND_ID.eq(data.getExtendId()))
                .and(EXTEND_VALUE_D_O.EXTEND_TYPE.eq(ExtendType.CHARACTER_EXTEND.getType()))
                .and(EXTEND_VALUE_D_O.EXTEND_NAME.in("expRate", "dropRate", "mesoRate")));
        Character character = getCharacter(data);
        character.resetPlayerRates();
        character.setWorldRates();
        character.setCouponRates();
    }

    /**
     * 重置所有玩家的商人状态
     */
    public void resetMerchant() {
        charactersMapper.updateAllHasMerchant(0);
    }

    /**
     * 获取各服排名玩家列表
     * @param worldSize 服务器数量
     * @return 排名列表
     */
    public List<List<CharactersDO>> getWorldsRankPlayers(int worldSize) {
        boolean wholeServerRanking = GameConfig.getServerBoolean("use_whole_server_ranking");
        List<List<CharactersDO>> worldsRankingList = new ArrayList<>();
        if (wholeServerRanking) {
            // 全服前50
            QueryWrapper queryWrapper = QueryWrapper.create()
                    .select(CHARACTERS_D_O.NAME, CHARACTERS_D_O.LEVEL, CHARACTERS_D_O.WORLD)
                    .from(CHARACTERS_D_O)
                    .leftJoin(ACCOUNTS_D_O).on(CHARACTERS_D_O.ACCOUNTID.eq(ACCOUNTS_D_O.ID))
                    .where(CHARACTERS_D_O.GM.lt(2))
                    .and(ACCOUNTS_D_O.BANNED.eq(0).or(ACCOUNTS_D_O.TEMPBAN.isNull()))
                    .and(CHARACTERS_D_O.WORLD.between(0, worldSize - 1))
                    .orderBy(CHARACTERS_D_O.WORLD.asc(), CHARACTERS_D_O.LEVEL.desc(), CHARACTERS_D_O.EXP.desc(), CHARACTERS_D_O.LAST_EXP_GAIN_TIME.asc())
                    .limit(50);
            List<CharactersDO> charactersDOList = charactersMapper.selectListByQuery(queryWrapper);
            worldsRankingList.add(charactersDOList);
        } else {
            for (int i = 0; i < worldSize; i++) {
                // 每个区前50
                List<CharactersDO> charactersDOList = getWorldRankPlayers(i);
                worldsRankingList.add(charactersDOList);
            }
        }
        return worldsRankingList;
    }

    /**
     * 获取指定服务器的排名玩家
     * @param worldId 服务器ID
     * @return 排名玩家列表
     */
    public List<CharactersDO> getWorldRankPlayers(int worldId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(CHARACTERS_D_O.NAME, CHARACTERS_D_O.LEVEL, CHARACTERS_D_O.WORLD)
                .from(CHARACTERS_D_O)
                .leftJoin(ACCOUNTS_D_O).on(CHARACTERS_D_O.ACCOUNTID.eq(ACCOUNTS_D_O.ID))
                .where(CHARACTERS_D_O.GM.lt(2))
                .and(ACCOUNTS_D_O.BANNED.eq(0).or(ACCOUNTS_D_O.TEMPBAN.isNull()))
                .and(CHARACTERS_D_O.WORLD.eq(worldId))
                .orderBy(CHARACTERS_D_O.LEVEL.desc(), CHARACTERS_D_O.EXP.desc(), CHARACTERS_D_O.LAST_EXP_GAIN_TIME.asc())
                .limit(50);
        return charactersMapper.selectListByQuery(queryWrapper);
    }

    /**
     * 根据名称查询角色
     * @param name 角色名称
     * @return 角色实体，不存在返回null
     */
    public CharactersDO findByName(String name) {
        List<CharactersDO> charactersDOS = charactersMapper.selectListByQuery(QueryWrapper.create().where(CHARACTERS_D_O.NAME.eq(name)));
        return charactersDOS.isEmpty() ? null : charactersDOS.getFirst();
    }

    /**
     * 删除角色技能
     * @param skillsDO 技能实体
     */
    public void removeSkill(SkillsDO skillsDO) {
        skillsMapper.deleteByQuery(QueryWrapper.create(skillsDO));
    }

    /**
     * 删除公会
     * @param guildsDO 公会实体
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteGuild(GuildsDO guildsDO) {
        charactersMapper.updateByQuery(CharactersDO.builder().guildid(0).guildrank(5).build(), QueryWrapper.create().where(CHARACTERS_D_O.GUILDID.eq(guildsDO.getGuildid())));
        guildsMapper.deleteById(guildsDO.getGuildid());
    }

    /**
     * 从数据库删除角色（级联删除所有关联数据）
     * @param player 角色对象
     * @param senderAccId 发送者账号ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCharFromDB(Character player, int senderAccId) {
        int cid = player.getId();
        // 验证角色归属，防止越权删除
        if (!Server.getInstance().haveCharacterEntry(senderAccId, cid)) {
            throw new BizException(I18nUtil.getExceptionMessage("UNKNOWN_CHARACTER"));
        }
        int world;
        CharactersDO charactersDO = findById(cid);
        if (charactersDO != null) {
            world = charactersDO.getWorld();
            // 删除公会成员关系
            if (charactersDO.getGuildid() > 0 && Objects.equals(senderAccId, charactersDO.getAccountid())) {
                Server.getInstance().deleteGuildCharacter(new GuildCharacter(player, cid, 0, charactersDO.getName(),
                        (byte) -1, (byte) -1, 0, Optional.ofNullable(charactersDO.getGuildrank()).orElse(0),
                        Optional.ofNullable(charactersDO.getGuildid()).orElse(0), false,
                        Optional.ofNullable(charactersDO.getAllianceRank()).orElse(0)));
            }
        } else {
            world = 0;
        }
        // 删除好友关系（通知在线好友）
        QueryWrapper buddiesQueryWrapper = QueryWrapper.create().where(BUDDIES_D_O.CHARACTERID.eq(cid));
        List<BuddiesDO> buddiesDOS = buddiesMapper.selectListByQuery(buddiesQueryWrapper);
        buddiesDOS.forEach(buddiesDO -> {
            Character buddy = Server.getInstance().getWorld(world).getPlayerStorage().getCharacterById(buddiesDO.getBuddyid());
            if (buddy != null) {
                buddy.deleteBuddy(cid);
            }
        });
        buddiesMapper.deleteByQuery(buddiesQueryWrapper);
        // 删除BBS帖子和回复
        QueryWrapper bbsThreadsQueryWrapper = QueryWrapper.create().where(BBS_THREADS_D_O.POSTERCID.eq(cid));
        List<BbsThreadsDO> bbsThreadsDOS = bbsThreadsMapper.selectListByQuery(bbsThreadsQueryWrapper);
        List<Long> threadIds = bbsThreadsDOS.stream().map(BbsThreadsDO::getThreadid).toList();
        if (!threadIds.isEmpty()) {
            bbsRepliesMapper.deleteByQuery(QueryWrapper.create().where(BBS_REPLIES_D_O.THREADID.in(threadIds)));
            bbsThreadsMapper.deleteByQuery(bbsThreadsQueryWrapper);
        }
        // 删除愿望清单
        wishlistsMapper.deleteByQuery(QueryWrapper.create().where(WISHLISTS_D_O.CHARID.eq(cid)));
        // 删除冷却数据
        cooldownsMapper.deleteByQuery(QueryWrapper.create().where(COOLDOWNS_D_O.CHARID.eq(cid)));
        // 删除玩家疾病数据
        playerdiseasesMapper.deleteByQuery(QueryWrapper.create().where(PLAYERDISEASES_D_O.CHARID.eq(cid)));
        // 删除区域信息
        areaInfoMapper.deleteByQuery(QueryWrapper.create().where(AREA_INFO_D_O.CHARID.eq(cid)));
        // 删除怪物图鉴
        monsterbookMapper.deleteByQuery(QueryWrapper.create().where(MONSTERBOOK_D_O.CHARID.eq(cid)));
        // 删除角色主表
        charactersMapper.deleteById(cid);
        // 删除家族角色关系
        familyCharacterMapper.deleteByQuery(QueryWrapper.create().where(FAMILY_CHARACTER_D_O.CID.eq(cid)));
        // 删除声望日志
        famelogMapper.deleteByQuery(QueryWrapper.create().where(FAMELOG_D_O.CHARACTERID_TO.eq(cid).or(FAMELOG_D_O.CHARACTERID.eq(cid))));
        // 删除背包库存
        inventoryService.deleteInventoryByCharacterId(cid);
        // 删除任务进度
        questService.deleteQuestProgressByCharacter(cid);
        // 删除仓库
        fredstorageMapper.deleteByQuery(QueryWrapper.create().where(FREDSTORAGE_D_O.CID.eq(cid)));
        // 删除拍卖行数据
        mtsService.deleteMtsByCharacterId(cid);
        // 删除按键映射
        keymapMapper.deleteByQuery(QueryWrapper.create().where(KEYMAP_D_O.CHARACTERID.eq(cid)));
        // 删除保存位置
        savedlocationsMapper.deleteByQuery(QueryWrapper.create().where(SAVEDLOCATIONS_D_O.CHARACTERID.eq(cid)));
        // 删除特洛克位置
        trocklocationsMapper.deleteByQuery(QueryWrapper.create().where(TROCKLOCATIONS_D_O.CHARACTERID.eq(cid)));
        // 删除技能和技能宏
        skillsMapper.deleteByQuery(QueryWrapper.create().where(SKILLS_D_O.CHARACTERID.eq(cid)));
        skillmacrosMapper.deleteByQuery(QueryWrapper.create().where(SKILLMACROS_D_O.CHARACTERID.eq(cid)));
        // 删除事件统计
        eventstatsMapper.deleteByQuery(QueryWrapper.create().where(EVENTSTATS_D_O.CHARACTERID.eq(cid)));
        // 删除服务器队列
        serverQueueMapper.deleteByQuery(QueryWrapper.create().where(SERVER_QUEUE_D_O.CHARACTERID.eq(cid)));
        // 取消待处理的改名和跨服请求
        nameChangeService.cancelPendingNameChange(player, false);
        worldTransferService.cancelPendingWorldTransfer(player, false);
    }

    /**
     * 保存角色到数据库（新角色创建时）
     * @param player 角色对象
     * @param notAutosave 是否为手动保存（非自动保存）
     */
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_UNCOMMITTED)
    public void saveCharToDB(Character player, boolean notAutosave) {
        if (!player.isLoggedIn()) {
            return;
        }
        log.info(I18nUtil.getLogMessage(notAutosave ? "Character.saveCharToDB.info1" : "Character.saveCharToDB.info2"), player.getName());
        Server.getInstance().updateCharacterEntry(player);

        CharactersDO cdo = Character.toCharactersDO(player);
        charactersMapper.insertSelective(cdo);
    }

    /**
     * 从数据库加载角色（完整加载所有关联数据）
     * @param cid 角色ID
     * @param client 客户端连接
     * @param channelServer 是否为频道服务器加载
     * @return 角色对象
     */
    public Character loadCharFromDB(int cid, Client client, boolean channelServer) {
        CharactersDO charactersDO = findById(cid);
        RequireUtil.requireNotNull(charactersDO, I18nUtil.getExceptionMessage("UNKNOWN_CHARACTER"));
        Character chr = Character.fromCharactersDO(charactersDO, client);
        if (!channelServer) {
            return chr;
        }
        // 加载地图信息
        MapManager mapManager = client.getChannelServer().getMapFactory();
        MapleMap mapleMap = mapManager.getMap(chr.getMapId());
        if (mapleMap == null) {
            mapleMap = mapManager.getMap(MapId.HENESYS);
        }
        chr.setMap(mapleMap);
        Portal portal = mapleMap.getPortal(chr.getInitialSpawnPoint());
        if (portal == null) {
            portal = mapleMap.getPortal(0);
            chr.setInitialSpawnPoint(0);
        }
        chr.setPosition(portal.getPosition());

        // 加载组队信息
        World world = Server.getInstance().getWorld(charactersDO.getWorld());
        int partyId = charactersDO.getParty();
        Party party = world.getParty(partyId);
        if (party != null) {
            PartyCharacter partyCharacter = party.getMemberById(cid);
            if (partyCharacter != null) {
                chr.setMPC(new PartyCharacter(chr));
                chr.setParty(party);
            }
        }

        // 加载信使信息
        int messengerId = charactersDO.getMessengerid();
        int messengerPosition = charactersDO.getMessengerposition();
        if (messengerId > 0 && messengerPosition < 4 && messengerPosition > -1) {
            Messenger messenger = world.getMessenger(messengerId);
            if (messenger != null) {
                chr.setMessenger(messenger);
                chr.setMessengerPosition(messengerPosition);
            }
        }
        chr.setLoggedIn(true);

        // 加载任务状态
        List<QuestStatus> questStatusList = questService.getQuestStatusByCharacter(cid);
        questStatusList.forEach(questStatus -> chr.getQuests().put(questStatus.getQuestID(), questStatus));

        // 加载技能
        List<SkillsDO> skillsDOList = skillsMapper.selectListByQuery(QueryWrapper.create().where(SKILLS_D_O.CHARACTERID.eq(cid)));
        skillsDOList.forEach(skillsDO -> {
            Skill skill = SkillFactory.getSkill(skillsDO.getSkillid());
            if (skill != null) {
                chr.getEditableSkills().put(skill, new SkillEntry(Optional.ofNullable(skillsDO.getSkilllevel()).map(Integer::byteValue).orElse((byte) 0),
                        skillsDO.getMasterlevel(), skillsDO.getExpiration()));
            }
        });

        // 加载冷却数据（过期的不加载）
        QueryWrapper cdQueryWrapper = QueryWrapper.create().where(COOLDOWNS_D_O.CHARID.eq(cid));
        List<CooldownsDO> cooldownsDOList = cooldownsMapper.selectListByQuery(cdQueryWrapper);
        cooldownsDOList.forEach(cooldownsDO -> {
            if (cooldownsDO.getSkillid() != 5221999 && cooldownsDO.getLength() + cooldownsDO.getStarttime() < System.currentTimeMillis()) {
                return;
            }
            chr.giveCoolDowns(cooldownsDO.getSkillid(), cooldownsDO.getStarttime(), cooldownsDO.getLength());
        });
        cooldownsMapper.deleteByQuery(cdQueryWrapper);

        // 加载疾病状态
        QueryWrapper pdWrapper = QueryWrapper.create().where(PLAYERDISEASES_D_O.CHARID.eq(cid));
        List<PlayerdiseasesDO> playerdiseasesDOList = playerdiseasesMapper.selectListByQuery(pdWrapper);
        Map<Disease, Pair<Long, MobSkill>> loadedDiseases = new LinkedHashMap<>();
        playerdiseasesDOList.forEach(playerdiseasesDO -> {
            Disease ordinal = Disease.ordinal(playerdiseasesDO.getDisease());
            if (Disease.NULL.equals(ordinal)) {
                return;
            }
            MobSkillType mobSkillType = MobSkillType.from(playerdiseasesDO.getMobskillid()).orElseThrow();
            MobSkill mobSkill = MobSkillFactory.getMobSkillOrThrow(mobSkillType, playerdiseasesDO.getMobskilllv());
            loadedDiseases.put(ordinal, new Pair<>(playerdiseasesDO.getLength(), mobSkill));
        });
        playerdiseasesMapper.deleteByQuery(pdWrapper);
        if (!loadedDiseases.isEmpty()) {
            Server.getInstance().getPlayerBuffStorage().addDiseasesToStorage(cid, loadedDiseases);
        }

        // 加载技能宏
        List<SkillmacrosDO> skillmacrosDOList = skillmacrosMapper.selectListByQuery(QueryWrapper.create().where(SKILLMACROS_D_O.CHARACTERID.eq(cid)));
        skillmacrosDOList.forEach(skillmacrosDO -> chr.getSkillMacros()[skillmacrosDO.getPosition()] = new SkillMacro(
                skillmacrosDO.getSkill1(), skillmacrosDO.getSkill2(), skillmacrosDO.getSkill3(), skillmacrosDO.getName(),
                skillmacrosDO.getShout(), skillmacrosDO.getPosition()
        ));

        // 加载按键映射
        List<KeymapDO> keymapDOList = keymapMapper.selectListByQuery(QueryWrapper.create().where(KEYMAP_D_O.CHARACTERID.eq(cid)));
        keymapDOList.forEach(keymapDO -> chr.getKeymap().put(keymapDO.getKey(), new KeyBinding(keymapDO.getType(), keymapDO.getAction())));

        // 加载保存位置
        List<SavedlocationsDO> savedlocationsDOList = savedlocationsMapper.selectListByQuery(QueryWrapper.create().where(SAVEDLOCATIONS_D_O.CHARACTERID.eq(cid)));
        savedlocationsDOList.forEach(savedlocationsDO -> chr.getSavedLocations()[SavedLocationType.valueOf(savedlocationsDO.getLocationtype()).ordinal()]
                = new SavedLocation(savedlocationsDO.getMap(), savedlocationsDO.getPortal()));

        // 加载声望记录（最近30天）
        List<FamelogDO> famelogDOList = famelogMapper.selectListByQuery(QueryWrapper.create()
                .where(FAMELOG_D_O.CHARACTERID.eq(cid)).and(dateDiff(now(), FAMELOG_D_O.WHEN).lt(30)));
        long lastFameTime = 0;
        List<Integer> lastMonthFameIds = new ArrayList<>(31);
        for (FamelogDO famelogDO : famelogDOList) {
            lastFameTime = Math.max(lastFameTime, famelogDO.getWhen().getTime());
            lastMonthFameIds.add(famelogDO.getCharacteridTo());
        }
        chr.setLastfametime(lastFameTime);
        chr.setLastmonthfameids(lastMonthFameIds);

        // 加载好友列表
        chr.getBuddylist().loadFromDb(cid);
        // 加载账号仓库
        Storage accountStorage = world.getAccountStorage(charactersDO.getAccountid());
        if (accountStorage == null) {
            world.loadAccountStorage(charactersDO.getAccountid());
            accountStorage = world.getAccountStorage(charactersDO.getAccountid());
        }
        chr.setStorage(accountStorage);
        chr.reapplyLocalStats();
        chr.changeHpMp(charactersDO.getHp(), charactersDO.getMp(), true);
        return chr;
    }

    /**
     * 获取角色的特洛克位置列表
     * @param cid 角色ID
     * @return 特洛克位置列表
     */
    public List<TrocklocationsDO> getTrockLocationByCharacter(Integer cid) {
        return trocklocationsMapper.selectListByQuery(QueryWrapper.create().where(TROCKLOCATIONS_D_O.CHARACTERID.eq(cid)));
    }

    /**
     * 获取角色的区域信息列表
     * @param cid 角色ID
     * @return 区域信息列表
     */
    public List<AreaInfoDO> getAreaInfoByCharacter(Integer cid) {
        return areaInfoMapper.selectListByQuery(QueryWrapper.create().where(AREA_INFO_D_O.CHARID.eq(cid)));
    }

    /**
     * 获取角色的事件统计列表
     * @param cid 角色ID
     * @return 事件统计列表
     */
    public List<EventstatsDO> getEventStatsByCharacter(Integer cid) {
        return eventstatsMapper.selectListByQuery(QueryWrapper.create().where(EVENTSTATS_D_O.CHARACTERID.eq(cid)));
    }

    /**
     * 获取角色的愿望清单列表
     * @param cid 角色ID
     * @return 愿望清单列表
     */
    public List<WishlistsDO> getWishlistsByCharacter(Integer cid) {
        return wishlistsMapper.selectListByQuery(QueryWrapper.create().where(WISHLISTS_D_O.CHARID.eq(cid)));
    }

    /**
     * 根据账号ID获取角色列表
     * @param accountId 账号ID
     * @return 角色列表
     */
    public List<CharactersDO> getCharacterByAccountId(int accountId) {
        return charactersMapper.selectListByQuery(QueryWrapper.create().where(CHARACTERS_D_O.ACCOUNTID.eq(accountId)));
    }

    /**
     * 检查倍率名称是否合法（仅允许expRate/dropRate/mesoRate）
     * @param data 扩展值数据
     */
    private void checkName(ExtendValueDO data) {
        check(data);
        // 非法请求篡改其他字段
        if ("expRate".equals(data.getExtendName()) || "dropRate".equals(data.getExtendName()) || "mesoRate".equals(data.getExtendName())) {
            return;
        }
        throw BizException.illegalArgument();
    }

    /**
     * 检查扩展值数据参数是否完整
     * @param data 扩展值数据
     */
    private void check(ExtendValueDO data) {
        RequireUtil.requireNotEmpty(data.getExtendId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "extendId"));
        RequireUtil.requireNotEmpty(data.getExtendType(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "extendType"));
        RequireUtil.requireNotEmpty(data.getExtendName(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "extendName"));
    }

    /**
     * 根据扩展值数据查找在线角色
     * @param data 扩展值数据
     * @return 角色对象
     */
    private Character getCharacter(ExtendValueDO data) {
        for (World world : Server.getInstance().getWorlds()) {
            for (Character character : world.getPlayerStorage().getAllCharacters()) {
                if (ExtendType.isAccount(data.getExtendType()) && Objects.equals(String.valueOf(character.getAccountId()), data.getExtendId())) {
                    return character;
                }

                if (ExtendType.isCharacter(data.getExtendType()) && Objects.equals(String.valueOf(character.getId()), data.getExtendId())) {
                    return character;
                }
            }
        }
        throw BizException.illegalArgument(I18nUtil.getExceptionMessage("CharacterService.getCharacter.exception1"));
    }
}