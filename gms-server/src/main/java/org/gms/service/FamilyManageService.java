package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.CharactersDO;
import org.gms.dao.entity.FamilyCharacterDO;
import org.gms.dao.mapper.CharactersMapper;
import org.gms.dao.mapper.FamilyCharacterMapper;
import org.gms.model.dto.BasePageDTO;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.gms.dao.entity.table.CharactersDOTableDef.CHARACTERS_D_O;
import static org.gms.dao.entity.table.FamilyCharacterDOTableDef.FAMILY_CHARACTER_D_O;

/**
 * 【业务服务】FamilyManageService：师徒家族管理服务类，负责师徒家族的查询和成员管理操作。
 */
@Service
@AllArgsConstructor
@Slf4j
public class FamilyManageService {

    /** 师徒家族角色数据访问接口 */
    private final FamilyCharacterMapper familyCharacterMapper;
    /** 角色数据访问接口 */
    private final CharactersMapper charactersMapper;

    /**
     * 分页查询师徒家族列表。
     * 
     * <p>查询所有师徒家族，按成员数量降序排列，包含家族ID、成员数、总声望、族长信息等。</p>
     * 
     * @param condition 分页条件
     * @return 分页后的家族列表
     */
    public Page<Map<String, Object>> getFamilyList(BasePageDTO condition) {
        // 查询所有家族成员
        List<FamilyCharacterDO> allMembers = familyCharacterMapper.selectAll();
        // 按家族ID分组
        Map<Integer, List<FamilyCharacterDO>> grouped = allMembers.stream()
                .filter(f -> f.getFamilyid() != null && f.getFamilyid() > 0)
                .collect(Collectors.groupingBy(FamilyCharacterDO::getFamilyid));

        List<Map<String, Object>> allFamilies = new ArrayList<>();
        // 收集所有角色ID用于查询名称
        Set<Integer> allCharIds = allMembers.stream().map(FamilyCharacterDO::getCid).collect(Collectors.toSet());
        Map<Integer, String> nameMap = new HashMap<>();
        
        if (!allCharIds.isEmpty()) {
            List<CharactersDO> chars = charactersMapper.selectListByQuery(
                    QueryWrapper.create().select(CHARACTERS_D_O.ID, CHARACTERS_D_O.NAME).from(CHARACTERS_D_O)
                            .where(CHARACTERS_D_O.ID.in(allCharIds))
            );
            for (CharactersDO c : chars) {
                nameMap.put(c.getId(), c.getName());
            }
        }

        // 构建每个家族的信息
        for (Map.Entry<Integer, List<FamilyCharacterDO>> entry : grouped.entrySet()) {
            int familyId = entry.getKey();
            List<FamilyCharacterDO> members = entry.getValue();
            // 计算家族总声望
            int totalRep = members.stream().mapToInt(m -> m.getTotalreputation() != null ? m.getTotalreputation() : 0).sum();
            // 找出族长（seniorid=0或seniorid不在本家族中的成员）
            Set<Integer> memberIds = members.stream().map(FamilyCharacterDO::getCid).collect(Collectors.toSet());
            FamilyCharacterDO leader = members.stream()
                    .filter(m -> m.getSeniorid() == null || m.getSeniorid() == 0 || !memberIds.contains(m.getSeniorid()))
                    .findFirst().orElse(members.get(0));

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("familyId", familyId);
            map.put("memberCount", members.size());
            map.put("totalReputation", totalRep);
            map.put("leaderId", leader.getCid());
            map.put("leaderName", nameMap.getOrDefault(leader.getCid(), "-"));
            map.put("precepts", leader.getPrecepts() != null ? leader.getPrecepts() : "");
            allFamilies.add(map);
        }

        // 按成员数量降序排列
        allFamilies.sort((a, b) -> Integer.compare((int) b.get("memberCount"), (int) a.get("memberCount")));

        // 手动分页
        int total = allFamilies.size();
        int pageNo = condition.getPageNo() != null ? condition.getPageNo() : 1;
        int pageSize = condition.getPageSize() != null ? condition.getPageSize() : 20;
        int from = (pageNo - 1) * pageSize;
        int to = Math.min(from + pageSize, total);
        List<Map<String, Object>> pageRecords = from < total ? allFamilies.subList(from, to) : Collections.emptyList();

        return new Page<>(pageRecords, pageNo, pageSize, total);
    }

    /**
     * 查询家族成员列表。
     * 
     * <p>查询指定家族的所有成员，包含成员的角色信息和声望数据。</p>
     * 
     * @param familyId 家族ID
     * @return 家族成员列表
     */
    public List<Map<String, Object>> getFamilyMembers(Integer familyId) {
        List<FamilyCharacterDO> members = familyCharacterMapper.selectListByQuery(
                QueryWrapper.create().from(FAMILY_CHARACTER_D_O).where(FAMILY_CHARACTER_D_O.FAMILYID.eq(familyId))
        );
        if (members.isEmpty()) return Collections.emptyList();

        // 查询角色信息
        Set<Integer> charIds = members.stream().map(FamilyCharacterDO::getCid).collect(Collectors.toSet());
        Map<Integer, CharactersDO> charMap = new HashMap<>();
        if (!charIds.isEmpty()) {
            List<CharactersDO> chars = charactersMapper.selectListByQuery(
                    QueryWrapper.create().from(CHARACTERS_D_O).where(CHARACTERS_D_O.ID.in(charIds))
            );
            for (CharactersDO c : chars) {
                charMap.put(c.getId(), c);
            }
        }

        // 构建成员信息
        return members.stream().map(m -> {
            CharactersDO chr = charMap.get(m.getCid());
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("cid", m.getCid());
            map.put("name", chr != null ? chr.getName() : "-");
            map.put("level", chr != null ? chr.getLevel() : 0);
            map.put("seniorid", m.getSeniorid());
            map.put("reputation", m.getReputation());
            map.put("totalreputation", m.getTotalreputation());
            map.put("todaysrep", m.getTodaysrep());
            map.put("reptosenior", m.getReptosenior());
            map.put("precepts", m.getPrecepts());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 移除家族成员。
     * 
     * @param cid 角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeFamilyMember(Integer cid) {
        RequireUtil.requireNotNull(cid, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "cid"));
        familyCharacterMapper.deleteById(cid);
    }

    /**
     * 更新家族成员声望。
     * 
     * @param cid 角色ID
     * @param reputation 声望值
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateReputation(Integer cid, Integer reputation) {
        RequireUtil.requireNotNull(cid, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "cid"));
        RequireUtil.requireNotNull(reputation, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "reputation"));
        familyCharacterMapper.update(FamilyCharacterDO.builder().cid(cid).reputation(reputation).build());
    }
}