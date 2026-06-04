package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.CharactersDO;
import org.gms.dao.entity.MarriagesDO;
import org.gms.dao.mapper.CharactersMapper;
import org.gms.dao.mapper.MarriagesMapper;
import org.gms.model.dto.BasePageDTO;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.gms.dao.entity.table.CharactersDOTableDef.CHARACTERS_D_O;
import static org.gms.dao.entity.table.MarriagesDOTableDef.MARRIAGES_D_O;

/**
 * 【业务服务】MarriageManageService：婚姻管理服务类，负责婚姻关系的查询和解除操作。
 */
@Service
@AllArgsConstructor
@Slf4j
public class MarriageManageService {

    /** 婚姻数据访问接口 */
    private final MarriagesMapper marriagesMapper;
    /** 角色数据访问接口 */
    private final CharactersMapper charactersMapper;

    /**
     * 分页查询婚姻列表。
     * 
     * <p>查询所有婚姻记录，并关联查询角色名称。</p>
     * 
     * @param condition 分页条件
     * @return 分页后的婚姻列表（包含夫妻双方ID和名称）
     */
    public Page<Map<String, Object>> getMarriageList(BasePageDTO condition) {
        QueryWrapper qw = QueryWrapper.create().from(MARRIAGES_D_O).orderBy(MARRIAGES_D_O.MARRIAGEID, true);
        Page<MarriagesDO> page = marriagesMapper.paginate(condition.getPageNo(), condition.getPageSize(), qw);

        // 收集所有角色ID
        Set<Integer> allCharIds = new HashSet<>();
        for (MarriagesDO m : page.getRecords()) {
            if (m.getHusbandid() != null) allCharIds.add(m.getHusbandid().intValue());
            if (m.getWifeid() != null) allCharIds.add(m.getWifeid().intValue());
        }
        
        // 批量查询角色名称
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

        // 构建返回结果
        List<Map<String, Object>> records = page.getRecords().stream().map(m -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("marriageid", m.getMarriageid());
            map.put("husbandid", m.getHusbandid());
            map.put("husbandName", m.getHusbandid() != null ? nameMap.getOrDefault(m.getHusbandid().intValue(), "-") : "-");
            map.put("wifeid", m.getWifeid());
            map.put("wifeName", m.getWifeid() != null ? nameMap.getOrDefault(m.getWifeid().intValue(), "-") : "-");
            return map;
        }).collect(Collectors.toList());

        return new Page<>(records, page.getPageNumber(), page.getPageSize(), page.getTotalRow());
    }

    /**
     * 解除婚姻关系。
     * 
     * <p>删除婚姻记录，并清除夫妻双方的婚姻状态（partnerId和marriageItemId设为0）。</p>
     * 
     * @param marriageId 婚姻ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void dissolveMarriage(Long marriageId) {
        RequireUtil.requireNotNull(marriageId, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "marriageId"));
        MarriagesDO marriage = marriagesMapper.selectOneById(marriageId);
        if (marriage == null) return;

        // 清除丈夫的婚姻状态
        if (marriage.getHusbandid() != null) {
            charactersMapper.update(
                    CharactersDO.builder().id(marriage.getHusbandid().intValue()).partnerId(0).marriageItemId(0).build()
            );
        }
        // 清除妻子的婚姻状态
        if (marriage.getWifeid() != null) {
            charactersMapper.update(
                    CharactersDO.builder().id(marriage.getWifeid().intValue()).partnerId(0).marriageItemId(0).build()
            );
        }
        // 删除婚姻记录
        marriagesMapper.deleteById(marriageId);
    }
}