package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.dao.entity.LangResourcesDO;
import org.gms.dao.mapper.LangResourcesMapper;
import org.gms.property.ServiceProperty;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 【业务服务】LangResourceService：语言资源服务类，负责国际化资源的管理。
 * 
 * <p>提供多语言资源的查询、新增、更新和删除操作，支持应用程序的国际化功能。
 * 语言资源由三部分组成：
 * <ul>
 *   <li>langBase：资源基础类型（如 game_config、item_name 等）</li>
 *   <li>langCode：资源编码（唯一标识）</li>
 *   <li>langType：语言类型（如 zh-CN、en-US 等）</li>
 * </ul></p>
 */
@Service
@AllArgsConstructor
public class LangResourceService {
    /** 服务配置属性（包含当前语言设置） */
    private final ServiceProperty serviceProperty;
    /** 语言资源数据访问接口 */
    private final LangResourcesMapper langResourcesMapper;

    /**
     * 根据条件获取国际化资源。
     * 
     * @param langResourcesDO 查询条件
     * @return 国际化资源值
     * @throws BizException 当查询结果不为1条时抛出
     */
    public String getI18n(LangResourcesDO langResourcesDO) {
        List<LangResourcesDO> langResourcesDOList = langResourcesMapper.selectListByQuery(
                QueryWrapper.create(langResourcesDO));
        RequireUtil.requireTrue(langResourcesDOList.size() == 1, 
                I18nUtil.getExceptionMessage("LangResourceService.getI18n.exception1"));
        return langResourcesDOList.getFirst().getLangValue();
    }

    /**
     * 插入或更新国际化资源。
     * 
     * <p>根据查询条件判断：
     * <ul>
     *   <li>查询到1条记录：执行更新操作</li>
     *   <li>查询到多条记录：删除所有重复记录后插入新记录</li>
     *   <li>未查询到记录：执行插入操作</li>
     * </ul></p>
     * 
     * @param langResourcesDO 语言资源实体
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdateI18n(LangResourcesDO langResourcesDO) {
        LangResourcesDO queryCondition = new LangResourcesDO();
        
        // 根据是否有ID确定查询条件
        if (langResourcesDO.getId() == null) {
            // 无ID时，按 langCode + langBase + langType 查询
            queryCondition.setLangCode(langResourcesDO.getLangCode());
            queryCondition.setLangBase(langResourcesDO.getLangBase());
            queryCondition.setLangType(serviceProperty.getLanguage());
        } else {
            // 有ID时，按ID查询
            queryCondition.setId(langResourcesDO.getId());
        }
        
        List<LangResourcesDO> langResourcesDOList = langResourcesMapper.selectListByQuery(
                QueryWrapper.create(queryCondition));
        
        if (langResourcesDOList.size() == 1) {
            // 更新操作：设置ID并更新
            langResourcesDO.setId(langResourcesDOList.getFirst().getId());
            langResourcesMapper.update(langResourcesDO);
            return;
        }
        
        if (langResourcesDOList.size() > 1) {
            // 存在重复记录，先删除所有重复记录
            langResourcesMapper.deleteBatchByIds(langResourcesDOList.stream()
                    .map(LangResourcesDO::getId).collect(Collectors.toList()));
        }
        
        // 为空或大于1时执行插入
        langResourcesDO.setId(null);
        langResourcesMapper.insert(langResourcesDO);
    }

    /**
     * 删除国际化资源。
     * 
     * <p>根据条件删除匹配的所有语言资源记录。</p>
     * 
     * @param langResourcesDO 删除条件
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteI18n(LangResourcesDO langResourcesDO) {
        langResourcesMapper.deleteByQuery(QueryWrapper.create(langResourcesDO));
    }
}