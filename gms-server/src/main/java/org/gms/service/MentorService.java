package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.MentorManager;
import org.gms.constants.inventory.ItemConstants;
import org.gms.constants.string.BroadcastPrefix;
import org.gms.dao.entity.*;
import org.gms.dao.mapper.*;
import org.gms.manager.ServerManager;
import org.gms.model.dto.MentorConfigDTO;
import org.gms.model.dto.MentorGraduationRewardDTO;
import org.gms.model.dto.MentorGraduationRewardDTO.ItemDTO;
import org.gms.net.server.Server;
import org.gms.util.PacketCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 师徒系统服务
 * <p>
 * 分为两部分：
 * A. 管理端 CRUD（Spring 实例方法）—— 配置表、毕业奖励表的增删改查
 * B. 游戏逻辑（静态方法）—— 供 GraalJS 脚本通过 Java.type() 调用
 * 静态方法通过 ServerManager.getApplicationContext().getBean() 桥接到实例方法实现事务管理
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class MentorService {

    private final MentorConfigMapper configMapper;
    private final MentorMasterMapper masterMapper;
    private final MentorRelationshipMapper relationshipMapper;
    private final MentorGraduationRewardMapper rewardMapper;
    private final MentorGraduationRewardItemMapper rewardItemMapper;

    @PostConstruct
    public void init() {
        refreshCache();
        log.info("师徒系统配置加载完成");
    }

    // ==================== A. 管理端 CRUD ====================

    /** 获取所有系统配置 */
    public List<MentorConfigDTO> getAllConfigs() {
        return configMapper.selectAll().stream()
                .map(this::toConfigDTO)
                .sorted(Comparator.comparingLong(MentorConfigDTO::getId))
                .collect(Collectors.toList());
    }

    /** 根据ID获取配置 */
    public MentorConfigDTO getConfigById(Long id) {
        MentorConfigDO config = configMapper.selectOneById(id);
        return config == null ? null : toConfigDTO(config);
    }

    /** 保存配置（新增或更新） */
    @Transactional
    public MentorConfigDTO saveConfig(MentorConfigDTO dto) {
        MentorConfigDO config = MentorConfigDO.builder()
                .id(dto.getId())
                .configKey(dto.getConfigKey())
                .configValue(dto.getConfigValue())
                .description(dto.getDescription())
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (config.getId() != null) {
            configMapper.update(config);
        } else {
            configMapper.insert(config);
        }
        refreshCache();
        return getConfigById(config.getId());
    }

    /** 删除配置 */
    @Transactional
    public void deleteConfig(Long id) {
        configMapper.deleteById(id);
        refreshCache();
    }

    /** 获取所有毕业奖励配置（含道具） */
    public List<MentorGraduationRewardDTO> getAllGraduationRewards() {
        List<MentorGraduationRewardDO> rewards = rewardMapper.selectAll();
        List<MentorGraduationRewardItemDO> allItems = rewardItemMapper.selectAll();
        return rewards.stream()
                .map(r -> toRewardDTO(r, allItems))
                .collect(Collectors.toList());
    }

    /** 根据ID获取毕业奖励 */
    public MentorGraduationRewardDTO getGraduationRewardById(Long id) {
        MentorGraduationRewardDO reward = rewardMapper.selectOneById(id);
        if (reward == null) return null;
        List<MentorGraduationRewardItemDO> items = rewardItemMapper.selectListByQuery(
                QueryWrapper.create().where("reward_id = ?", id));
        return toRewardDTO(reward, items);
    }

    /** 保存毕业奖励（新增或更新） */
    @Transactional
    public MentorGraduationRewardDTO saveGraduationReward(MentorGraduationRewardDTO dto) {
        MentorGraduationRewardDO reward = MentorGraduationRewardDO.builder()
                .id(dto.getId())
                .rewardType(dto.getRewardType())
                .meso(dto.getMeso() != null ? dto.getMeso() : 0)
                .nxCredit(dto.getNxCredit() != null ? dto.getNxCredit() : 0)
                .maplePoint(dto.getMaplePoint() != null ? dto.getMaplePoint() : 0)
                .nxPrepaid(dto.getNxPrepaid() != null ? dto.getNxPrepaid() : 0)
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (reward.getId() != null) {
            rewardMapper.update(reward);
            // 删除旧道具，重新插入
            rewardItemMapper.deleteByQuery(
                    QueryWrapper.create().where("reward_id = ?", reward.getId()));
        } else {
            rewardMapper.insert(reward);
        }

        if (dto.getItems() != null) {
            for (ItemDTO item : dto.getItems()) {
                rewardItemMapper.insert(MentorGraduationRewardItemDO.builder()
                        .rewardId(reward.getId())
                        .itemId(item.getItemId())
                        .quantity(item.getQuantity() != null ? item.getQuantity() : 1)
                        .build());
            }
        }

        return getGraduationRewardById(reward.getId());
    }

    /** 删除毕业奖励 */
    @Transactional
    public void deleteGraduationReward(Long id) {
        rewardItemMapper.deleteByQuery(
                QueryWrapper.create().where("reward_id = ?", id));
        rewardMapper.deleteById(id);
    }

    /** 刷新 MentorManager 缓存 */
    private void refreshCache() {
        MentorManager.load(configMapper.selectAll());
    }

    private MentorConfigDTO toConfigDTO(MentorConfigDO c) {
        return MentorConfigDTO.builder()
                .id(c.getId())
                .configKey(c.getConfigKey())
                .configValue(c.getConfigValue())
                .description(c.getDescription())
                .enabled(c.getEnabled())
                .build();
    }

    private MentorGraduationRewardDTO toRewardDTO(MentorGraduationRewardDO r, List<MentorGraduationRewardItemDO> allItems) {
        List<ItemDTO> items = allItems.stream()
                .filter(i -> i.getRewardId().equals(r.getId()))
                .map(i -> ItemDTO.builder()
                        .id(i.getId())
                        .itemId(i.getItemId())
                        .quantity(i.getQuantity())
                        .build())
                .collect(Collectors.toList());
        return MentorGraduationRewardDTO.builder()
                .id(r.getId())
                .rewardType(r.getRewardType())
                .meso(r.getMeso())
                .nxCredit(r.getNxCredit())
                .maplePoint(r.getMaplePoint())
                .nxPrepaid(r.getNxPrepaid())
                .enabled(r.getEnabled())
                .items(items)
                .build();
    }

    // ==================== B. 游戏逻辑（实例方法，由静态方法桥接调用） ====================

    /**
     * 创建师门（成为师父）
     * 校验条件：等级 >= 配置要求，尚未创建过师门
     */
    @Transactional
    public String createMentorGroupInternal(int characterId) {
        // 检查是否已是师父
        MentorMasterDO existing = masterMapper.selectOneByQuery(
                QueryWrapper.create().where("character_id = ?", characterId));
        if (existing != null) {
            return "您已经是师父了，无需重复创建师门！";
        }
        // 检查是否可以成为徒弟（如果正在别人门下，不能创建师门）
        MentorRelationshipDO asDisciple = relationshipMapper.selectOneByQuery(
                QueryWrapper.create().where("disciple_character_id = ?", characterId)
                        .and("status = ?", 0));
        if (asDisciple != null) {
            return "您当前正在师门中修行，请先出师或退出后再创建自己的师门！";
        }
        // 查找角色名称和等级
        CharactersDO character = getCharacterById(characterId);
        if (character == null) {
            return "角色不存在！";
        }
        int level = character.getLevel();
        int requiredLevel = MentorManager.getCreateMasterLevel();
        if (level < requiredLevel) {
            return "您的等级不足！创建师门需要达到 " + requiredLevel + " 级，您当前 " + level + " 级。";
        }
        masterMapper.insert(MentorMasterDO.builder()
                .characterId(characterId)
                .createTime(LocalDateTime.now())
                .build());
        log.info("角色 {} (ID={}) 创建了师门", character.getName(), characterId);
        return "恭喜！您已成功创建师门，现在可以收徒了！";
    }

    /**
     * 拜师（添加徒弟）
     * @param masterCharId 师父角色ID
     * @param discipleName 徒弟角色名（由脚本端输入）
     */
    @Transactional
    public String addDiscipleInternal(int masterCharId, String discipleName) {
        // 校验师父存在
        MentorMasterDO master = masterMapper.selectOneByQuery(
                QueryWrapper.create().where("character_id = ?", masterCharId));
        if (master == null) {
            return "该玩家尚未创建师门，无法收徒！";
        }
        // 查找徒弟角色
        CharactersDO discipleChar = getCharacterByName(discipleName);
        if (discipleChar == null) {
            return "找不到名为 \"" + discipleName + "\" 的角色！";
        }
        int discipleCharId = discipleChar.getId();
        // 不能拜自己为师
        if (masterCharId == discipleCharId) {
            return "您不能拜自己为师！";
        }
        // 检查徒弟等级
        int maxLevel = MentorManager.getMaxBeDiscipleLevel();
        if (discipleChar.getLevel() > maxLevel) {
            return "\"" + discipleName + "\" 的等级超过了拜师上限（" + maxLevel + " 级），无法拜师！";
        }
        // 检查徒弟是否已有师父（活跃或已毕业，不包括已退出的）
        MentorRelationshipDO existingDisciple = relationshipMapper.selectOneByQuery(
                QueryWrapper.create().where("disciple_character_id = ?", discipleCharId)
                        .and("status in (0, 1)"));
        if (existingDisciple != null) {
            String statusText = existingDisciple.getStatus() == 0 ? "在师门中" : "已出师";
            return "\"" + discipleName + "\" 已有师门关系（" + statusText + "），无法重复拜师！";
        }
        // 检查师父的徒弟数量
        long currentCount = relationshipMapper.selectCountByQuery(
                QueryWrapper.create().where("master_character_id = ?", masterCharId)
                        .and("status = ?", 0));
        int maxDisciples = MentorManager.getMaxDisciples();
        if (currentCount >= maxDisciples) {
            return "您的徒弟数量已达上限（" + maxDisciples + " 个），无法再收徒！";
        }
        // 插入师徒关系
        relationshipMapper.insert(MentorRelationshipDO.builder()
                .masterCharacterId(masterCharId)
                .discipleCharacterId(discipleCharId)
                .status(0)
                .createTime(LocalDateTime.now())
                .build());
        // 查找师父名用于日志
        CharactersDO masterChar = getCharacterById(masterCharId);
        String masterName = masterChar != null ? masterChar.getName() : "未知";
        log.info("{} (ID={}) 拜 {} (ID={}) 为师", discipleName, discipleCharId, masterName, masterCharId);
        // 全服广播拜师成功通告（粉色文字，type=5）
        broadcastMentorNotice(discipleChar, discipleName + " 拜 " + masterName + " 为师，师徒一心，共闯冒险岛！");
        return "恭喜！\"" + discipleName + "\" 已成功拜入 " + masterName + " 的门下！";
    }

    /**
     * 拜师（通过两个角色ID）
     * 由脚本端 3 参数版 addDisciple 桥接调用
     */
    @Transactional
    public String addDiscipleByIdsInternal(int masterCharId, int discipleCharId) {
        // 校验师父存在
        MentorMasterDO master = masterMapper.selectOneByQuery(
                QueryWrapper.create().where("character_id = ?", masterCharId));
        if (master == null) {
            return "该玩家尚未创建师门，无法收徒！";
        }
        // 查找徒弟角色
        CharactersDO discipleChar = getCharacterById(discipleCharId);
        if (discipleChar == null) {
            return "徒弟角色不存在！";
        }
        String discipleName = discipleChar.getName();
        // 不能拜自己为师
        if (masterCharId == discipleCharId) {
            return "您不能拜自己为师！";
        }
        // 检查徒弟等级
        int maxLevel = MentorManager.getMaxBeDiscipleLevel();
        if (discipleChar.getLevel() > maxLevel) {
            return "\"" + discipleName + "\" 的等级超过了拜师上限（" + maxLevel + " 级），无法拜师！";
        }
        // 检查徒弟是否已有师父
        MentorRelationshipDO existingDisciple = relationshipMapper.selectOneByQuery(
                QueryWrapper.create().where("disciple_character_id = ?", discipleCharId)
                        .and("status in (0, 1)"));
        if (existingDisciple != null) {
            String statusText = existingDisciple.getStatus() == 0 ? "在师门中" : "已出师";
            return "\"" + discipleName + "\" 已有师门关系（" + statusText + "），无法重复拜师！";
        }
        // 检查师父的徒弟数量
        long currentCount = relationshipMapper.selectCountByQuery(
                QueryWrapper.create().where("master_character_id = ?", masterCharId)
                        .and("status = ?", 0));
        int maxDisciples = MentorManager.getMaxDisciples();
        if (currentCount >= maxDisciples) {
            CharactersDO masterChar = getCharacterById(masterCharId);
            String masterName = masterChar != null ? masterChar.getName() : "未知";
            return masterName + " 的徒弟数量已达上限（" + maxDisciples + " 个），无法再收徒！";
        }
        // 插入师徒关系
        relationshipMapper.insert(MentorRelationshipDO.builder()
                .masterCharacterId(masterCharId)
                .discipleCharacterId(discipleCharId)
                .status(0)
                .createTime(LocalDateTime.now())
                .build());
        CharactersDO masterChar = getCharacterById(masterCharId);
        String masterName = masterChar != null ? masterChar.getName() : "未知";
        log.info("{} (ID={}) 拜 {} (ID={}) 为师", discipleName, discipleCharId, masterName, masterCharId);
        // 全服广播拜师成功通告（粉色文字，type=5）
        broadcastMentorNotice(discipleChar, discipleName + " 拜 " + masterName + " 为师，师徒一心，共闯冒险岛！");
        return "恭喜！您已成功拜入 " + masterName + " 的门下！";
    }

    /**
     * 师父踢出徒弟
     */
    @Transactional
    public String removeDiscipleInternal(int masterCharId, int discipleCharId) {
        MentorRelationshipDO rel = relationshipMapper.selectOneByQuery(
                QueryWrapper.create().where("master_character_id = ?", masterCharId)
                        .and("disciple_character_id = ?", discipleCharId)
                        .and("status = ?", 0));
        if (rel == null) {
            return "该角色不是您的徒弟，或关系已结束！";
        }
        rel.setStatus(2); // 已退出
        relationshipMapper.update(rel);
        CharactersDO discipleChar = getCharacterById(discipleCharId);
        String name = discipleChar != null ? discipleChar.getName() : "未知";
        log.info("师父 {} 将徒弟 {} (ID={}) 逐出师门", masterCharId, name, discipleCharId);
        // 全服广播逐出师门通告（粉色文字，type=5）
        broadcastMentorNotice(discipleChar, name + " 被师父逐出了师门，从此各奔天涯。");
        return "已将 \"" + name + "\" 逐出师门。";
    }

    /**
     * 徒弟出师
     */
    @Transactional
    public String graduateDiscipleInternal(int discipleCharId) {
        MentorRelationshipDO rel = relationshipMapper.selectOneByQuery(
                QueryWrapper.create().where("disciple_character_id = ?", discipleCharId)
                        .and("status = ?", 0));
        if (rel == null) {
            return "您当前没有活跃的师门关系，无法出师！";
        }
        CharactersDO discipleChar = getCharacterById(discipleCharId);
        if (discipleChar == null) {
            return "角色不存在！";
        }
        int level = discipleChar.getLevel();
        int requiredLevel = MentorManager.getGraduateLevel();
        if (level < requiredLevel) {
            return "您的等级不足！出师需要达到 " + requiredLevel + " 级，您当前 " + level + " 级。";
        }
        // 更新关系为已出师
        rel.setStatus(1);
        rel.setGraduateTime(LocalDateTime.now());
        relationshipMapper.update(rel);
        // 发放出师奖励
        String rewardMsg = grantGraduationRewards(rel.getMasterCharacterId(), discipleCharId);
        CharactersDO masterChar = getCharacterById(rel.getMasterCharacterId());
        String masterName = masterChar != null ? masterChar.getName() : "未知";
        log.info("{} (ID={}) 从 {} (ID={}) 门下出师", discipleChar.getName(), discipleCharId, masterName, rel.getMasterCharacterId());
        // 全服广播出师通告（粉色文字，type=5）
        broadcastMentorNotice(discipleChar, discipleChar.getName() + " 从 " + masterName + " 门下光荣出师，学有所成！");
        return "恭喜出师！\r\n师父：" + masterName + "\r\n" + rewardMsg;
    }

    /**
     * 徒弟主动退出师门
     */
    @Transactional
    public String leaveMentorInternal(int discipleCharId) {
        MentorRelationshipDO rel = relationshipMapper.selectOneByQuery(
                QueryWrapper.create().where("disciple_character_id = ?", discipleCharId)
                        .and("status = ?", 0));
        if (rel == null) {
            return "您当前没有活跃的师门关系，无法退出！";
        }
        rel.setStatus(2); // 已退出
        relationshipMapper.update(rel);
        CharactersDO masterChar = getCharacterById(rel.getMasterCharacterId());
        String masterName = masterChar != null ? masterChar.getName() : "未知";
        CharactersDO discipleChar = getCharacterById(discipleCharId);
        String discipleName = discipleChar != null ? discipleChar.getName() : "未知";
        log.info("{} (ID={}) 退出了 {} (ID={}) 的师门", discipleName, discipleCharId, masterName, rel.getMasterCharacterId());
        // 全服广播退出师门通告（粉色文字，type=5）
        broadcastMentorNotice(discipleChar, discipleName + " 退出了 " + masterName + " 的师门，师徒缘尽。");
        return "您已退出 " + masterName + " 的师门。";
    }

    /**
     * 发放出师奖励（师父和徒弟各得一份）
     */
    private String grantGraduationRewards(int masterCharId, int discipleCharId) {
        StringBuilder sb = new StringBuilder();
        // 师父奖励
        MentorGraduationRewardDO masterReward = rewardMapper.selectOneByQuery(
                QueryWrapper.create().where("reward_type = ?", 0)
                        .and("enabled = ?", 1));
        if (masterReward != null) {
            grantSingleReward(masterCharId, masterReward, "师父", sb);
        }
        // 徒弟奖励
        MentorGraduationRewardDO discipleReward = rewardMapper.selectOneByQuery(
                QueryWrapper.create().where("reward_type = ?", 1)
                        .and("enabled = ?", 1));
        if (discipleReward != null) {
            grantSingleReward(discipleCharId, discipleReward, "徒弟", sb);
        }
        return sb.toString();
    }

    /**
     * 发放单个角色的出师奖励
     */
    private void grantSingleReward(int characterId, MentorGraduationRewardDO reward, String roleLabel, StringBuilder sb) {
        CharactersDO character = getCharacterById(characterId);
        String charName = character != null ? character.getName() : "未知";
        sb.append("【").append(roleLabel).append("奖励】").append(charName).append("：\r\n");
        boolean hasReward = false;

        if (reward.getMeso() != null && reward.getMeso() > 0) {
            sb.append("  · 金币 +").append(String.format("%,d", reward.getMeso())).append("\r\n");
            hasReward = true;
            // 通过玩家在线实例发放（如果在线），否则直接更新数据库
            grantMesoToCharacter(characterId, reward.getMeso());
        }
        if (reward.getNxCredit() != null && reward.getNxCredit() > 0) {
            sb.append("  · 点券 +").append(reward.getNxCredit()).append("\r\n");
            hasReward = true;
            grantCashToCharacter(characterId, 1, reward.getNxCredit());
        }
        if (reward.getMaplePoint() != null && reward.getMaplePoint() > 0) {
            sb.append("  · 抵用券 +").append(reward.getMaplePoint()).append("\r\n");
            hasReward = true;
            grantCashToCharacter(characterId, 2, reward.getMaplePoint());
        }
        if (reward.getNxPrepaid() != null && reward.getNxPrepaid() > 0) {
            sb.append("  · 信用券 +").append(reward.getNxPrepaid()).append("\r\n");
            hasReward = true;
            grantCashToCharacter(characterId, 4, reward.getNxPrepaid());
        }

        // 道具奖励
        List<MentorGraduationRewardItemDO> items = rewardItemMapper.selectListByQuery(
                QueryWrapper.create().where("reward_id = ?", reward.getId()));
        if (items != null && !items.isEmpty()) {
            for (MentorGraduationRewardItemDO item : items) {
                sb.append("  · 道具ID ").append(item.getItemId())
                        .append(" x").append(item.getQuantity()).append("\r\n");
                hasReward = true;
                grantItemToCharacter(characterId, item.getItemId(), item.getQuantity());
            }
        }

        if (!hasReward) {
            sb.append("  （暂无奖励配置）\r\n");
        }
    }

    // ==================== C. 静态桥接方法（供 GraalJS 脚本调用） ====================

    public static String createMentorGroup(int characterId) {
        MentorService svc = ServerManager.getApplicationContext().getBean(MentorService.class);
        return svc.createMentorGroupInternal(characterId);
    }

    public static String addDisciple(int masterCharId, String discipleName) {
        MentorService svc = ServerManager.getApplicationContext().getBean(MentorService.class);
        return svc.addDiscipleInternal(masterCharId, discipleName);
    }

    /** 脚本端拜师：通过师父名查找并建立关系（discipleCharId=当前玩家, masterName=输入的师父名） */
    public static String addDisciple(int discipleCharId, String masterName, String discipleName) {
        Integer masterCharId = getCharacterIdByName(masterName);
        if (masterCharId == null) {
            return "找不到名为 \"" + masterName + "\" 的角色，请确认师父角色名输入正确！";
        }
        MentorService svc = ServerManager.getApplicationContext().getBean(MentorService.class);
        return svc.addDiscipleByIdsInternal(masterCharId, discipleCharId);
    }

    public static String removeDisciple(int masterCharId, int discipleCharId) {
        MentorService svc = ServerManager.getApplicationContext().getBean(MentorService.class);
        return svc.removeDiscipleInternal(masterCharId, discipleCharId);
    }

    public static String graduateDisciple(int discipleCharId) {
        MentorService svc = ServerManager.getApplicationContext().getBean(MentorService.class);
        return svc.graduateDiscipleInternal(discipleCharId);
    }

    public static String leaveMentor(int discipleCharId) {
        MentorService svc = ServerManager.getApplicationContext().getBean(MentorService.class);
        return svc.leaveMentorInternal(discipleCharId);
    }

    /** 获取师父信息（创建师门的记录），返回给JS用 */
    public static MentorMasterDO getMasterInfo(int characterId) {
        MentorMasterMapper mapper = ServerManager.getApplicationContext().getBean(MentorMasterMapper.class);
        return mapper.selectOneByQuery(
                QueryWrapper.create().where("character_id = ?", characterId));
    }

    /** 获取师父的徒弟列表，返回给JS用 */
    public static List<MentorRelationshipDO> getDiscipleList(int masterCharId) {
        MentorRelationshipMapper mapper = ServerManager.getApplicationContext().getBean(MentorRelationshipMapper.class);
        return mapper.selectListByQuery(
                QueryWrapper.create().where("master_character_id = ?", masterCharId));
    }

    /** 获取徒弟当前的师父关系 */
    public static MentorRelationshipDO getMyMaster(int discipleCharId) {
        MentorRelationshipMapper mapper = ServerManager.getApplicationContext().getBean(MentorRelationshipMapper.class);
        return mapper.selectOneByQuery(
                QueryWrapper.create().where("disciple_character_id = ?", discipleCharId)
                        .and("status = ?", 0));
    }

    /** 获取角色名称 */
    public static String getCharacterName(int characterId) {
        CharactersMapper mapper = ServerManager.getApplicationContext().getBean(CharactersMapper.class);
        CharactersDO character = mapper.selectOneById(characterId);
        return character != null ? character.getName() : "未知";
    }

    /** 获取角色等级 */
    public static int getCharacterLevel(int characterId) {
        CharactersMapper mapper = ServerManager.getApplicationContext().getBean(CharactersMapper.class);
        CharactersDO character = mapper.selectOneById(characterId);
        return character != null ? character.getLevel() : 0;
    }

    /** 按名称查找角色ID */
    public static Integer getCharacterIdByName(String name) {
        CharactersMapper mapper = ServerManager.getApplicationContext().getBean(CharactersMapper.class);
        CharactersDO character = mapper.selectOneByQuery(
                QueryWrapper.create().where("name = ?", name));
        return character != null ? character.getId() : null;
    }

    // ==================== D. 内部工具方法 ====================

    /**
     * 全服广播拜师成功通告（粉色文字，聊天框显示）
     * @param discipleChar 徒弟角色记录（用于获取所在世界）
     * @param message      通告消息内容
     */
    private void broadcastMentorNotice(CharactersDO discipleChar, String message) {
        try {
            Integer worldId = discipleChar.getWorld();
            if (worldId != null) {
                Server.getInstance().broadcastMessage(worldId,
                        PacketCreator.serverNotice(BroadcastPrefix.MENTOR.getType(), BroadcastPrefix.MENTOR.msg(message)));
            }
        } catch (Exception e) {
            // 广播失败不影响核心业务
            log.warn("师徒通告广播失败", e);
        }
    }

    private CharactersDO getCharacterById(int characterId) {
        CharactersMapper mapper = ServerManager.getApplicationContext().getBean(CharactersMapper.class);
        return mapper.selectOneById(characterId);
    }

    private CharactersDO getCharacterByName(String name) {
        CharactersMapper mapper = ServerManager.getApplicationContext().getBean(CharactersMapper.class);
        return mapper.selectOneByQuery(
                QueryWrapper.create().where("name = ?", name));
    }

    /** 给角色加金币（在线通过实例，离线直接改DB） */
    private void grantMesoToCharacter(int characterId, int amount) {
        try {
            var world = ServerManager.getApplicationContext().getBean(
                    org.gms.net.server.world.World.class);
            if (world != null) {
                org.gms.client.Character onlineChar = world.getPlayerStorage().getCharacterById(characterId);
                if (onlineChar != null) {
                    onlineChar.gainMeso(amount, true, true, false);
                    return;
                }
            }
        } catch (Exception ignored) {}
        // 如果不在线，直接更新数据库
        CharactersMapper mapper = ServerManager.getApplicationContext().getBean(CharactersMapper.class);
        CharactersDO character = mapper.selectOneById(characterId);
        if (character != null) {
            character.setMeso(character.getMeso() + amount);
            mapper.update(character);
        }
    }

    /** 给角色加NX（在线通过实例，离线直接改DB） */
    private void grantCashToCharacter(int characterId, int cashType, int amount) {
        try {
            var world = ServerManager.getApplicationContext().getBean(
                    org.gms.net.server.world.World.class);
            if (world != null) {
                org.gms.client.Character onlineChar = world.getPlayerStorage().getCharacterById(characterId);
                if (onlineChar != null) {
                    onlineChar.getCashShop().gainCash(cashType, amount);
                    return;
                }
            }
        } catch (Exception ignored) {}
        // 离线时暂不处理NX（后续可扩展）
        log.warn("角色 {} 不在线，NX奖励暂未发放（type={}, amount={}）", characterId, cashType, amount);
    }

    /** 给角色发放道具（在线通过实例，离线直接写DB） */
    private void grantItemToCharacter(int characterId, int itemId, int quantity) {
        try {
            var world = ServerManager.getApplicationContext().getBean(
                    org.gms.net.server.world.World.class);
            if (world != null) {
                org.gms.client.Character onlineChar = world.getPlayerStorage().getCharacterById(characterId);
                if (onlineChar != null) {
                    var type = ItemConstants.getInventoryType(itemId);
                    if (type != null) {
                        onlineChar.getAbstractPlayerInteraction().gainItem(itemId, (short) quantity);
                        return;
                    }
                }
            }
        } catch (Exception ignored) {}
        log.warn("角色 {} 不在线，道具 {} x{} 暂未发放", characterId, itemId, quantity);
    }
}
