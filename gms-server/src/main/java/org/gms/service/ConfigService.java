package org.gms.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.GameConfig;
import org.gms.dao.entity.GameConfigDO;
import org.gms.dao.entity.LangResourcesDO;
import org.gms.dao.mapper.GameConfigMapper;
import org.gms.exception.BizException;
import org.gms.model.dto.ConfigTypeDTO;
import org.gms.model.dto.GameConfigReqDTO;
import org.gms.net.server.Server;
import org.gms.property.ServiceProperty;
import org.gms.util.DatabaseConnection;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.mybatisflex.core.query.QueryMethods.distinct;
import static org.gms.dao.entity.table.GameConfigDOTableDef.GAME_CONFIG_D_O;
import static org.gms.dao.entity.table.LangResourcesDOTableDef.LANG_RESOURCES_D_O;

/**
 * 游戏配置服务类
 * 提供游戏配置的查询、增删改、导入导出功能
 */
@Service
@AllArgsConstructor
@Slf4j
public class ConfigService {
    /** 游戏配置数据访问对象 */
    private final GameConfigMapper gameConfigMapper;
    /** 服务配置属性 */
    private final ServiceProperty serviceProperty;
    /** 多语言资源服务 */
    private final LangResourceService langResourceService;

    /**
     * 加载所有游戏配置
     *
     * @return 游戏配置列表
     */
    public List<GameConfigDO> loadGameConfigs() {
        return gameConfigMapper.selectAll();
    }

    /**
     * 获取配置类型和子类型列表
     * 查询数据库中所有不重复的配置类型和子类型
     *
     * @return 配置类型DTO，包含类型和子类型列表
     */
    public ConfigTypeDTO getConfigTypeList() {
        List<GameConfigDO> typeDOList = gameConfigMapper.selectListByQuery(QueryWrapper.create().select(distinct(GAME_CONFIG_D_O.CONFIG_TYPE)));
        List<GameConfigDO> subTypeDOList = gameConfigMapper.selectListByQuery(QueryWrapper.create().select(distinct(GAME_CONFIG_D_O.CONFIG_SUB_TYPE)));
        return ConfigTypeDTO.builder()
                .types(typeDOList.stream().map(GameConfigDO::getConfigType).toList())
                .subTypes(subTypeDOList.stream().map(GameConfigDO::getConfigSubType).toList())
                .build();
    }

    /**
     * 分页查询配置列表
     * 联合多语言表查询，支持按类型、子类型和关键词筛选
     *
     * @param condition 查询条件
     * @return 分页的配置列表
     */
    public Page<GameConfigDO> getConfigList(GameConfigReqDTO condition) {
        // join i18n表
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(GAME_CONFIG_D_O.ID, GAME_CONFIG_D_O.CONFIG_CODE, GAME_CONFIG_D_O.CONFIG_CLAZZ,
                        GAME_CONFIG_D_O.CONFIG_TYPE, GAME_CONFIG_D_O.CONFIG_SUB_TYPE, GAME_CONFIG_D_O.CONFIG_VALUE,
                        LANG_RESOURCES_D_O.LANG_VALUE.as("config_desc"))
                .from(GAME_CONFIG_D_O)
                .leftJoin(LANG_RESOURCES_D_O).on(LANG_RESOURCES_D_O.LANG_CODE.eq(GAME_CONFIG_D_O.CONFIG_DESC)
                        .and(LANG_RESOURCES_D_O.LANG_TYPE.eq(serviceProperty.getLanguage()))
                        .and(LANG_RESOURCES_D_O.LANG_BASE.eq("game_config")));
        if (!RequireUtil.isEmpty(condition.getType()))
            queryWrapper.and(GAME_CONFIG_D_O.CONFIG_TYPE.eq(condition.getType()));
        if (!RequireUtil.isEmpty(condition.getSubType()))
            queryWrapper.and(GAME_CONFIG_D_O.CONFIG_SUB_TYPE.eq(condition.getSubType()));
        if (!RequireUtil.isEmpty(condition.getFilter())) {
            queryWrapper.and(GAME_CONFIG_D_O.CONFIG_CODE.like(condition.getFilter()).or(LANG_RESOURCES_D_O.LANG_VALUE.like(condition.getFilter())));
        }

        return gameConfigMapper.paginate(condition.getPageNo(), condition.getPageSize(), queryWrapper);
    }

    /**
     * 新增配置
     * 同时插入配置记录和多语言描述，并同步到内存缓存
     *
     * @param condition 配置数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void addConfig(GameConfigDO condition) {
        RequireUtil.requireNotEmpty(condition.getConfigType(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "configType"));
        RequireUtil.requireNotEmpty(condition.getConfigSubType(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "configSubType"));
        RequireUtil.requireNotEmpty(condition.getConfigCode(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "configCode"));
        RequireUtil.requireNotEmpty(condition.getConfigValue(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "configValue"));
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(GAME_CONFIG_D_O.CONFIG_TYPE.eq(condition.getConfigType()))
                .and(GAME_CONFIG_D_O.CONFIG_CODE.eq(condition.getConfigCode()));
        if ("world".equals(condition.getConfigType())) {
            queryWrapper.and(GAME_CONFIG_D_O.CONFIG_SUB_TYPE.eq(condition.getConfigSubType()));
        }
        List<GameConfigDO> gameConfigDOList = gameConfigMapper.selectListByQuery(queryWrapper);
        RequireUtil.requireTrue(gameConfigDOList.isEmpty(), I18nUtil.getExceptionMessage("ConfigService.addConfig.exception1"));
        langResourceService.insertOrUpdateI18n(LangResourcesDO.builder()
                .langBase("game_config")
                .langCode(condition.getConfigCode())
                .langType(serviceProperty.getLanguage())
                .langValue(condition.getConfigDesc())
                .build());
        condition.setId(null);
        condition.setConfigDesc(condition.getConfigCode());
        condition.setUpdateTime(new Date());
        gameConfigMapper.insertSelective(condition);
        GameConfig.add(condition);
    }

    /**
     * 更新配置
     * 更新配置值和对应的多语言描述
     *
     * @param condition 配置数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(GameConfigDO condition) {
        RequireUtil.requireNotNull(condition.getId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        RequireUtil.requireNotEmpty(condition.getConfigValue(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "configValue"));
        GameConfigDO gameConfigDO = gameConfigMapper.selectOneById(condition.getId());
        langResourceService.insertOrUpdateI18n(LangResourcesDO.builder()
                .langBase("game_config")
                .langCode(gameConfigDO.getConfigCode())
                .langType(serviceProperty.getLanguage())
                .langValue(condition.getConfigDesc())
                .build());
        gameConfigMapper.update(GameConfigDO.builder()
                .id(condition.getId())
                .configValue(condition.getConfigValue())
                .updateTime(new Date())
                .build());
        gameConfigDO.setConfigValue(condition.getConfigValue());
        GameConfig.update(gameConfigDO);
    }

    /**
     * 删除配置
     * 删除配置记录及所有关联的多语言描述
     *
     * @param id 配置ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long id) {
        RequireUtil.requireNotNull(id, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        GameConfigDO gameConfigDO = gameConfigMapper.selectOneById(id);
        langResourceService.deleteI18n(LangResourcesDO.builder()
                .langBase("game_config")
                .langCode(gameConfigDO.getConfigCode())
                // 所有i18n都要删掉
//                .langType(serviceProperty.getLanguage())
                .build());
        gameConfigMapper.deleteById(id);
        GameConfig.remove(gameConfigDO);
    }

    /**
     * 批量删除配置
     * 逐个调用deleteConfig删除每个配置
     *
     * @param ids 配置ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfigList(List<Long> ids) {
        RequireUtil.requireNotEmpty(ids, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "ids"));
        ids.forEach(this::deleteConfig);
    }

    /**
     * 导入YML配置文件
     * 解析YML文件中的world和server配置，生成SQL批量更新数据库
     *
     * @param file 上传的YML文件
     * @return 成功数量（固定返回1）
     */
    public int importYml(MultipartFile file) {
        String filename = file.getOriginalFilename();
        RequireUtil.requireNotEmpty(filename, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "file"));
        RequireUtil.requireTrue(filename.endsWith(".yml") || filename.endsWith(".yaml"), I18nUtil.getExceptionMessage("UNSUPPORTED_TYPE") + ": " + filename);
        try {
            Yaml yaml = new Yaml();
            LinkedHashMap<String, Object> property = yaml.load(file.getInputStream());
            JSONObject gmsProperty = JSONObject.parse(JSONObject.toJSONString(property.get("gms")));
            JSONArray worlds = gmsProperty.getJSONObject("world").getJSONArray("worlds");
            JSONObject server = gmsProperty.getJSONObject("server");

            StringBuilder updateSql = new StringBuilder();
            for (int i = 0; i < worlds.size(); i++) {
                JSONObject world = worlds.getJSONObject(i);
                if (world.getFloat("exp_rate") == null) {
                    continue;
                }
                for (Map.Entry<String, Object> entry : world.entrySet()) {
                    String configCode = entry.getKey().toLowerCase();
                    configCode = replaceWithEquals(configCode, new String[]{"why_am_i_recommended", "recommend_message"},
                            new String[]{"channels", "channel_size"});
                    updateSql.append("update game_config set config_value = '").append(parseObject(entry.getValue()))
                            .append("' where config_type = 'world' and config_sub_type = '").append(i)
                            .append("' and config_code = '").append(configCode).append("';\n");
                }
            }
            for (Map.Entry<String, Object> entry : server.entrySet()) {
                String configCode = entry.getKey().toLowerCase();
                configCode = replaceWithEquals(configCode, new String[]{"wldlist_size", "max_world_size"},
                        new String[]{"channel_size", "max_channel_size"}, new String[]{"channel_load", "channel_capacity"},
                        new String[]{"host", "wan_host"}, new String[]{"lanhost", "lan_host"},
                        new String[]{"use_debug_show_rcvd_mvlife", "use_debug_show_life_move"});
                configCode = replaceWithContains(configCode, new String[]{"use_maxrange", "use_max_range"},
                        new String[]{"charslot", "chr_slot"}, new String[]{"multiclient", "multi_client"},
                        new String[]{"keyset", "key_set"}, new String[]{"eqpexp", "eqp_exp"},
                        new String[]{"autoassign", "auto_assign"}, new String[]{"autoban", "auto_ban"},
                        new String[]{"openshop", "open_shop"}, new String[]{"shopitemsold", "shop_item_sold"},
                        new String[]{"cashshop", "cash_shop"}, new String[]{"atkup", "atk_up"},
                        new String[]{"unitprice", "unit_price"}, new String[]{"buffstat", "buff_stat"},
                        new String[]{"autoaggro", "auto_aggro"}, new String[]{"chscroll", "chaos_scroll"},
                        new String[]{"skillset", "skill_set"}, new String[]{"equipmnt", "equipment"},
                        new String[]{"lvlup", "level_up"}, new String[]{"levelup", "level_up"},
                        new String[]{"extraheal", "extra_heal"}, new String[]{"autopot", "auto_pot"},
                        new String[]{"autohp", "auto_hp"}, new String[]{"automp", "auto_mp"});

                Object configValue = parseObject(entry.getValue());
                if ("npcs_scriptable".equalsIgnoreCase(entry.getKey())) {
                    configValue = JSONObject.toJSONString(entry.getValue());
                }
                updateSql.append("update game_config set config_value = '").append(configValue)
                        .append("' where config_type = 'server' and config_code = '").append(configCode).append("';\n");
            }
            String[] updateArr = updateSql.toString().split("\n");
            for (String str : updateArr) {
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement statement = connection.prepareStatement(str)) {
                    statement.executeUpdate();
                }
            }

        } catch (Exception e) {
            String msg = I18nUtil.getExceptionMessage("FILE_PARSE_ERROR");
            log.error(msg, e);
            throw new BizException(msg);
        }
        // 异步重启，这里千万不要用ThreadManager，因为停止服务会注销所有线程
        Thread.startVirtualThread(Server.getInstance().shutdown(true));
        // 返回成功的数量
        return 1;
    }

    /**
     * 解析对象值
     * 对Float/Double类型格式化为无科学计数法的字符串
     *
     * @param obj 原始对象
     * @return 解析后的值
     */
    private Object parseObject(Object obj) {
        String typeName = obj.getClass().getTypeName();
        // 为避免科学计数，进行格式化输出，以double为标准最多精确到16位
        if ("java.lang.Float".equals(typeName) || "java.lang.Double".equals(typeName)) {
            return new DecimalFormat("#.################").format(obj);
        }
        return obj;
    }

    /**
     * 精确匹配替换字符串
     * 如果源字符串与ft[0]相等则替换为ft[1]
     *
     * @param src 源字符串
     * @param fts 替换对数组（[from, to]）
     * @return 替换后的字符串
     */
    private String replaceWithEquals(String src, String[]... fts) {
        for (String[] ft : fts) {
            if (ft[0].equals(src)) {
                src = ft[1];
                break;
            }
        }
        return src;
    }

    /**
     * 包含匹配替换字符串
     * 如果源字符串包含ft[0]则替换其中的ft[0]为ft[1]
     *
     * @param src 源字符串
     * @param fts 替换对数组（[from, to]）
     * @return 替换后的字符串
     */
    private String replaceWithContains(String src, String[]... fts) {
        for (String[] ft : fts) {
            if (src.contains(ft[0])) {
                src = src.replace(ft[0], ft[1]);
            }
        }
        return src;
    }

    /**
     * 导出YML配置文件
     * 将所有配置按world和server分组导出为YML格式的文件下载
     *
     * @return 包含文件流和头信息的ResponseEntity
     */
    public ResponseEntity<Resource> exportYml() {
        List<GameConfigDO> gameConfigDOS = loadGameConfigs();
        // 转成yml格式
        Map<String, List<GameConfigDO>> worldCollect = gameConfigDOS.stream().filter(config -> "world".equals(config.getConfigType()))
                .collect(Collectors.groupingBy(GameConfigDO::getConfigSubType));
        List<Map<String, Object>> worldList = new ArrayList<>();
        for (Map.Entry<String, List<GameConfigDO>> entry : worldCollect.entrySet()) {
            worldList.add(entry.getValue().stream().collect(toMap()));
        }
        Map<String, Object> worlds = new HashMap<>();
        worlds.put("worlds", worldList);

        Map<String, Object> serverCollect = gameConfigDOS.stream().filter(config -> "server".equals(config.getConfigType()))
                .collect(toMap());

        Map<String, Object> gms = new HashMap<>();
        gms.put("world", worlds);
        gms.put("server", serverCollect);

        Map<String, Object> ymlCollect = new HashMap<>();
        ymlCollect.put("gms", gms);

        try {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
            Yaml yaml = new Yaml(options);
            StringWriter writer = new StringWriter();
            yaml.dump(ymlCollect, writer);
            byte[] bytes = writer.toString().getBytes(StandardCharsets.UTF_8);
            Resource resource = new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return "export.yml";
                }
            };
            HttpHeaders headers = new HttpHeaders();
            // 需要将CONTENT_DISPOSITION暴露给前端，否则前端识别不到头信息的CONTENT_DISPOSITION
            headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (Exception e) {
            String msg = I18nUtil.getExceptionMessage("FILE_CREATE_ERROR");
            log.error(msg, e);
            throw new BizException(msg);
        }

    }

    /**
     * 配置到Map的收集器
     * 将配置列表转换为configCode -> configValue的Map，根据配置类型做不同转换
     *
     * @return 配置Map收集器
     */
    public Collector<GameConfigDO, ?, Map<String, Object>> toMap() {
        return Collectors.toMap(GameConfigDO::getConfigCode, config -> {
            if ("java.util.Map".equals(config.getConfigClazz())) {
                return JSONObject.parseObject(config.getConfigValue(), new TypeReference<Map<Integer, Object>>() {
                });
            } else if ("java.lang.Float".equals(config.getConfigClazz()) || "java.lang.Double".equals(config.getConfigClazz())) {
                // 为避免科学计数，用BigDecimal
                return new BigDecimal(config.getConfigValue());
            } else {
                try {
                    return JSONObject.parseObject(config.getConfigValue(), Class.forName(config.getConfigClazz()));
                } catch (Exception e) {
                    return config.getConfigValue();
                }
            }
        }, (v1, v2) -> v1);
    }
}