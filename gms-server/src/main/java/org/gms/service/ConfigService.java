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
 * 【业务服务】ConfigService：游戏配置服务类，负责游戏配置的管理与维护。
 * 
 * <p>提供游戏配置的增删改查、导入导出功能，支持配置类型筛选、分页查询，
 * 并在配置变更时同步更新内存缓存。配置支持国际化描述，通过语言资源服务管理多语言。</p>
 * 
 * <p>主要功能：
 * <ul>
 *   <li>配置列表分页查询，支持按类型、子类型、关键词筛选</li>
 *   <li>新增配置时自动创建国际化描述记录</li>
 *   <li>更新配置时同步更新内存缓存和国际化描述</li>
 *   <li>支持YAML格式配置文件的导入导出</li>
 * </ul></p>
 */
@Service
@AllArgsConstructor
@Slf4j
public class ConfigService {
    /** 游戏配置数据访问接口 */
    private final GameConfigMapper gameConfigMapper;
    /** 服务配置属性（包含语言设置等） */
    private final ServiceProperty serviceProperty;
    /** 语言资源服务，用于国际化描述管理 */
    private final LangResourceService langResourceService;

    /**
     * 加载所有游戏配置。
     * 
     * @return 游戏配置实体列表
     */
    public List<GameConfigDO> loadGameConfigs() {
        return gameConfigMapper.selectAll();
    }

    /**
     * 获取配置类型和子类型列表。
     * 
     * <p>用于前端下拉框选项，提供配置类型的去重列表。</p>
     * 
     * @return 包含类型和子类型列表的DTO
     */
    public ConfigTypeDTO getConfigTypeList() {
        List<GameConfigDO> typeDOList = gameConfigMapper.selectListByQuery(
                QueryWrapper.create().select(distinct(GAME_CONFIG_D_O.CONFIG_TYPE)));
        List<GameConfigDO> subTypeDOList = gameConfigMapper.selectListByQuery(
                QueryWrapper.create().select(distinct(GAME_CONFIG_D_O.CONFIG_SUB_TYPE)));
        return ConfigTypeDTO.builder()
                .types(typeDOList.stream().map(GameConfigDO::getConfigType).toList())
                .subTypes(subTypeDOList.stream().map(GameConfigDO::getConfigSubType).toList())
                .build();
    }

    /**
     * 分页查询游戏配置列表。
     * 
     * <p>支持按类型、子类型筛选，支持关键词模糊搜索。
     * 查询结果会关联语言资源表获取配置描述的国际化内容。</p>
     * 
     * @param condition 查询条件（包含分页信息、类型、子类型、筛选关键词）
     * @return 分页后的配置列表
     */
    public Page<GameConfigDO> getConfigList(GameConfigReqDTO condition) {
        // 关联语言资源表查询配置描述
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(GAME_CONFIG_D_O.ID, GAME_CONFIG_D_O.CONFIG_CODE, GAME_CONFIG_D_O.CONFIG_CLAZZ,
                        GAME_CONFIG_D_O.CONFIG_TYPE, GAME_CONFIG_D_O.CONFIG_SUB_TYPE, GAME_CONFIG_D_O.CONFIG_VALUE,
                        LANG_RESOURCES_D_O.LANG_VALUE.as("config_desc"))
                .from(GAME_CONFIG_D_O)
                .leftJoin(LANG_RESOURCES_D_O).on(LANG_RESOURCES_D_O.LANG_CODE.eq(GAME_CONFIG_D_O.CONFIG_DESC)
                        .and(LANG_RESOURCES_D_O.LANG_TYPE.eq(serviceProperty.getLanguage()))
                        .and(LANG_RESOURCES_D_O.LANG_BASE.eq("game_config")));
        
        // 按类型筛选
        if (!RequireUtil.isEmpty(condition.getType()))
            queryWrapper.and(GAME_CONFIG_D_O.CONFIG_TYPE.eq(condition.getType()));
        // 按子类型筛选
        if (!RequireUtil.isEmpty(condition.getSubType()))
            queryWrapper.and(GAME_CONFIG_D_O.CONFIG_SUB_TYPE.eq(condition.getSubType()));
        // 关键词模糊搜索（匹配配置编码或描述）
        if (!RequireUtil.isEmpty(condition.getFilter())) {
            queryWrapper.and(GAME_CONFIG_D_O.CONFIG_CODE.like(condition.getFilter())
                    .or(LANG_RESOURCES_D_O.LANG_VALUE.like(condition.getFilter())));
        }

        return gameConfigMapper.paginate(condition.getPageNo(), condition.getPageSize(), queryWrapper);
    }

    /**
     * 新增游戏配置。
     * 
     * <p>校验规则：
     * <ul>
     *   <li>configType、configSubType、configCode、configValue不能为空</li>
     *   <li>同类型下的配置编码不能重复（world类型还需检查子类型）</li>
     * </ul>
     * 新增后自动创建国际化描述记录并更新内存缓存。</p>
     * 
     * @param condition 配置实体
     * @throws BizException 当参数为空或配置已存在时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public void addConfig(GameConfigDO condition) {
        // 参数校验
        RequireUtil.requireNotEmpty(condition.getConfigType(), 
                I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "configType"));
        RequireUtil.requireNotEmpty(condition.getConfigSubType(), 
                I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "configSubType"));
        RequireUtil.requireNotEmpty(condition.getConfigCode(), 
                I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "configCode"));
        RequireUtil.requireNotEmpty(condition.getConfigValue(), 
                I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "configValue"));
        
        // 检查配置是否已存在
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(GAME_CONFIG_D_O.CONFIG_TYPE.eq(condition.getConfigType()))
                .and(GAME_CONFIG_D_O.CONFIG_CODE.eq(condition.getConfigCode()));
        if ("world".equals(condition.getConfigType())) {
            queryWrapper.and(GAME_CONFIG_D_O.CONFIG_SUB_TYPE.eq(condition.getConfigSubType()));
        }
        List<GameConfigDO> gameConfigDOList = gameConfigMapper.selectListByQuery(queryWrapper);
        RequireUtil.requireTrue(gameConfigDOList.isEmpty(), 
                I18nUtil.getExceptionMessage("ConfigService.addConfig.exception1"));
        
        // 保存国际化描述
        langResourceService.insertOrUpdateI18n(LangResourcesDO.builder()
                .langBase("game_config")
                .langCode(condition.getConfigCode())
                .langType(serviceProperty.getLanguage())
                .langValue(condition.getConfigDesc())
                .build());
        
        // 保存配置实体
        condition.setId(null);
        condition.setConfigDesc(condition.getConfigCode());
        condition.setUpdateTime(new Date());
        gameConfigMapper.insertSelective(condition);
        
        // 更新内存缓存
        GameConfig.add(condition);
    }

    /**
     * 更新游戏配置。
     * 
     * <p>更新配置值和国际化描述，并同步更新内存缓存。</p>
     * 
     * @param condition 配置实体（包含ID和新的配置值）
     * @throws BizException 当ID为空或配置值为空时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(GameConfigDO condition) {
        RequireUtil.requireNotNull(condition.getId(), 
                I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        RequireUtil.requireNotEmpty(condition.getConfigValue(), 
                I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "configValue"));
        
        GameConfigDO gameConfigDO = gameConfigMapper.selectOneById(condition.getId());
        
        // 更新国际化描述
        langResourceService.insertOrUpdateI18n(LangResourcesDO.builder()
                .langBase("game_config")
                .langCode(gameConfigDO.getConfigCode())
                .langType(serviceProperty.getLanguage())
                .langValue(condition.getConfigDesc())
                .build());
        
        // 更新配置值
        gameConfigMapper.update(GameConfigDO.builder()
                .id(condition.getId())
                .configValue(condition.getConfigValue())
                .updateTime(new Date())
                .build());
        
        // 更新内存缓存
        gameConfigDO.setConfigValue(condition.getConfigValue());
        GameConfig.update(gameConfigDO);
    }

    /**
     * 删除游戏配置。
     * 
     * <p>删除配置及其关联的所有国际化描述记录，并更新内存缓存。</p>
     * 
     * @param id 配置ID
     * @throws BizException 当ID为空时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long id) {
        RequireUtil.requireNotNull(id, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        
        GameConfigDO gameConfigDO = gameConfigMapper.selectOneById(id);
        
        // 删除关联的国际化描述（所有语言版本）
        langResourceService.deleteI18n(LangResourcesDO.builder()
                .langBase("game_config")
                .langCode(gameConfigDO.getConfigCode())
                .build());
        
        // 删除配置实体
        gameConfigMapper.deleteById(id);
        
        // 更新内存缓存
        GameConfig.remove(gameConfigDO);
    }

    /**
     * 批量删除游戏配置。
     * 
     * @param ids 配置ID列表
     * @throws BizException 当ID列表为空时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfigList(List<Long> ids) {
        RequireUtil.requireNotEmpty(ids, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "ids"));
        ids.forEach(this::deleteConfig);
    }

    /**
     * 从YAML文件导入配置。
     * 
     * <p>解析YAML文件中的world和server配置，更新到数据库。
     * 导入完成后会异步重启服务器使配置生效。</p>
     * 
     * <p>支持的配置格式转换：
     * <ul>
     *   <li>下划线命名转换（如 charslot -> chr_slot）</li>
     *   <li>特殊字段映射（如 host -> wan_host）</li>
     * </ul></p>
     * 
     * @param file YAML配置文件
     * @return 导入结果（固定返回1表示成功）
     * @throws BizException 当文件格式错误或解析失败时抛出
     */
    public int importYml(MultipartFile file) {
        String filename = file.getOriginalFilename();
        RequireUtil.requireNotEmpty(filename, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "file"));
        RequireUtil.requireTrue(filename.endsWith(".yml") || filename.endsWith(".yaml"), 
                I18nUtil.getExceptionMessage("UNSUPPORTED_TYPE") + ": " + filename);
        
        try {
            Yaml yaml = new Yaml();
            LinkedHashMap<String, Object> property = yaml.load(file.getInputStream());
            JSONObject gmsProperty = JSONObject.parse(JSONObject.toJSONString(property.get("gms")));
            JSONArray worlds = gmsProperty.getJSONObject("world").getJSONArray("worlds");
            JSONObject server = gmsProperty.getJSONObject("server");

            StringBuilder updateSql = new StringBuilder();
            
            // 处理world配置
            for (int i = 0; i < worlds.size(); i++) {
                JSONObject world = worlds.getJSONObject(i);
                if (world.getFloat("exp_rate") == null) {
                    continue;
                }
                for (Map.Entry<String, Object> entry : world.entrySet()) {
                    String configCode = entry.getKey().toLowerCase();
                    // 字段名映射转换
                    configCode = replaceWithEquals(configCode, 
                            new String[]{"why_am_i_recommended", "recommend_message"},
                            new String[]{"channels", "channel_size"});
                    updateSql.append("update game_config set config_value = '")
                            .append(parseObject(entry.getValue()))
                            .append("' where config_type = 'world' and config_sub_type = '").append(i)
                            .append("' and config_code = '").append(configCode).append("';\n");
                }
            }
            
            // 处理server配置
            for (Map.Entry<String, Object> entry : server.entrySet()) {
                String configCode = entry.getKey().toLowerCase();
                // 字段名精确匹配转换
                configCode = replaceWithEquals(configCode, 
                        new String[]{"wldlist_size", "max_world_size"},
                        new String[]{"channel_size", "max_channel_size"}, 
                        new String[]{"channel_load", "channel_capacity"},
                        new String[]{"host", "wan_host"}, 
                        new String[]{"lanhost", "lan_host"},
                        new String[]{"use_debug_show_rcvd_mvlife", "use_debug_show_life_move"});
                // 字段名包含匹配转换
                configCode = replaceWithContains(configCode, 
                        new String[]{"use_maxrange", "use_max_range"},
                        new String[]{"charslot", "chr_slot"}, 
                        new String[]{"multiclient", "multi_client"},
                        new String[]{"keyset", "key_set"}, 
                        new String[]{"eqpexp", "eqp_exp"},
                        new String[]{"autoassign", "auto_assign"}, 
                        new String[]{"autoban", "auto_ban"},
                        new String[]{"openshop", "open_shop"}, 
                        new String[]{"shopitemsold", "shop_item_sold"},
                        new String[]{"cashshop", "cash_shop"}, 
                        new String[]{"atkup", "atk_up"},
                        new String[]{"unitprice", "unit_price"}, 
                        new String[]{"buffstat", "buff_stat"},
                        new String[]{"autoaggro", "auto_aggro"}, 
                        new String[]{"chscroll", "chaos_scroll"},
                        new String[]{"skillset", "skill_set"}, 
                        new String[]{"equipmnt", "equipment"},
                        new String[]{"lvlup", "level_up"}, 
                        new String[]{"levelup", "level_up"},
                        new String[]{"extraheal", "extra_heal"}, 
                        new String[]{"autopot", "auto_pot"},
                        new String[]{"autohp", "auto_hp"}, 
                        new String[]{"automp", "auto_mp"});

                Object configValue = parseObject(entry.getValue());
                // npcs_scriptable字段需要特殊处理为JSON字符串
                if ("npcs_scriptable".equalsIgnoreCase(entry.getKey())) {
                    configValue = JSONObject.toJSONString(entry.getValue());
                }
                updateSql.append("update game_config set config_value = '").append(configValue)
                        .append("' where config_type = 'server' and config_code = '").append(configCode).append("';\n");
            }
            
            // 执行SQL更新
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
        
        // 异步重启服务器使配置生效
        // 注意：不能使用ThreadManager，因为停止服务会注销所有线程
        Thread.startVirtualThread(Server.getInstance().shutdown(true));
        
        return 1;
    }

    /**
     * 解析对象值，处理浮点数避免科学计数法。
     * 
     * <p>针对浮点数类型进行格式化输出，以double为标准最多精确到16位，
     * 防止数值在存储和显示过程中出现科学计数法表示。</p>
     * 
     * @param obj 待解析对象
     * @return 解析后的对象值
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
     * 根据精确匹配替换字符串。
     * 
     * <p>当源字符串与替换对中的原值完全相等时进行替换，
     * 用于处理配置项中特定字段名的映射转换。</p>
     * 
     * @param src 源字符串
     * @param fts 替换对数组（每个替换对包含原值和新值）
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
     * 根据包含匹配替换字符串。
     * 
     * <p>当源字符串包含替换对中的子串时进行替换，
     * 使用replace方法将所有匹配的子串替换为新值。</p>
     * 
     * @param src 源字符串
     * @param fts 替换对数组（每个替换对包含子串和替换值）
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
     * 导出配置为YAML文件。
     * 
     * <p>将数据库中的world和server配置导出为YAML格式，支持下载。</p>
     * 
     * @return YAML文件资源响应
     * @throws BizException 当文件创建失败时抛出
     */
    public ResponseEntity<Resource> exportYml() {
        List<GameConfigDO> gameConfigDOS = loadGameConfigs();
        
        // 按world类型分组并转换
        Map<String, List<GameConfigDO>> worldCollect = gameConfigDOS.stream()
                .filter(config -> "world".equals(config.getConfigType()))
                .collect(Collectors.groupingBy(GameConfigDO::getConfigSubType));
        List<Map<String, Object>> worldList = new ArrayList<>();
        for (Map.Entry<String, List<GameConfigDO>> entry : worldCollect.entrySet()) {
            worldList.add(entry.getValue().stream().collect(toMap()));
        }
        Map<String, Object> worlds = new HashMap<>();
        worlds.put("worlds", worldList);

        // 转换server配置
        Map<String, Object> serverCollect = gameConfigDOS.stream()
                .filter(config -> "server".equals(config.getConfigType()))
                .collect(toMap());

        // 构建YAML结构
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
            // 需要将CONTENT_DISPOSITION暴露给前端，否则前端识别不到头信息
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
     * 将GameConfigDO转换为Map的收集器。
     * 
     * <p>根据配置的类型进行相应的转换：
     * <ul>
     *   <li>Map类型：解析为JSON对象</li>
     *   <li>Float/Double类型：转换为BigDecimal避免科学计数</li>
     *   <li>其他类型：尝试反射解析为对应类的实例</li>
     * </ul></p>
     * 
     * @return 收集器实例
     */
    public Collector<GameConfigDO, ?, Map<String, Object>> toMap() {
        return Collectors.toMap(GameConfigDO::getConfigCode, config -> {
            if ("java.util.Map".equals(config.getConfigClazz())) {
                return JSONObject.parseObject(config.getConfigValue(), 
                        new TypeReference<Map<Integer, Object>>() {});
            } else if ("java.lang.Float".equals(config.getConfigClazz()) 
                    || "java.lang.Double".equals(config.getConfigClazz())) {
                // 为避免科学计数，用BigDecimal
                return new BigDecimal(config.getConfigValue());
            } else {
                try {
                    return JSONObject.parseObject(config.getConfigValue(), 
                            Class.forName(config.getConfigClazz()));
                } catch (Exception e) {
                    return config.getConfigValue();
                }
            }
        }, (v1, v2) -> v1);
    }
}