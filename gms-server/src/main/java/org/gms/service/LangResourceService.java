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
 * 国际化资源服务类
 * 管理多语言资源的增删改查操作
 */
@Service
@AllArgsConstructor
public class LangResourceService {
    /** 服务配置属性 */
    private final ServiceProperty serviceProperty;
    /** 多语言资源数据访问对象 */
    private final LangResourcesMapper langResourcesMapper;

    /**
     * 获取国际化资源的值
     * 根据条件查询，要求结果唯一
     *
     * @param langResourcesDO 查询条件
     * @return 国际化值
     */
    public String getI18n(LangResourcesDO langResourcesDO) {
        List<LangResourcesDO> langResourcesDOList = langResourcesMapper.selectListByQuery(QueryWrapper.create(langResourcesDO));
        RequireUtil.requireTrue(langResourcesDOList.size() == 1, I18nUtil.getExceptionMessage("LangResourceService.getI18n.exception1"));
        return langResourcesDOList.getFirst().getLangValue();
    }

    /**
     * 插入或更新国际化资源
     * 存在则更新，不存在则新增
     *
     * @param langResourcesDO 国际化资源
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdateI18n(LangResourcesDO langResourcesDO) {
        LangResourcesDO queryCondition = new LangResourcesDO();
        if (langResourcesDO.getId() == null) {
            queryCondition.setLangCode(langResourcesDO.getLangCode());
            queryCondition.setLangBase(langResourcesDO.getLangBase());
            queryCondition.setLangType(serviceProperty.getLanguage());
        } else {
            queryCondition.setId(langResourcesDO.getId());
        }
        List<LangResourcesDO> langResourcesDOList = langResourcesMapper.selectListByQuery(QueryWrapper.create(queryCondition));
        if (langResourcesDOList.size() == 1) {
            langResourcesDO.setId(langResourcesDOList.getFirst().getId());
            langResourcesMapper.update(langResourcesDO);
            return;
        }
        if (langResourcesDOList.size() > 1) {
            langResourcesMapper.deleteBatchByIds(langResourcesDOList.stream().map(LangResourcesDO::getId).collect(Collectors.toList()));
        }
        // 为空或大于1时新增
        langResourcesDO.setId(null);
        langResourcesMapper.insert(langResourcesDO);
    }

    /**
     * 删除国际化资源
     *
     * @param langResourcesDO 删除条件
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteI18n(LangResourcesDO langResourcesDO) {
        langResourcesMapper.deleteByQuery(QueryWrapper.create(langResourcesDO));
    }
}