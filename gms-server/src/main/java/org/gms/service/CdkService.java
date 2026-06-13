package org.gms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.config.CdkManager;
import org.gms.dao.entity.CdkConfigDO;
import org.gms.dao.entity.CdkItemDO;
import org.gms.dao.entity.CdkLogDO;
import org.gms.dao.mapper.CdkConfigMapper;
import org.gms.dao.mapper.CdkItemMapper;
import org.gms.dao.mapper.CdkLogMapper;
import org.gms.model.dto.*;
import org.gms.net.server.Server;
import org.gms.server.CashShop;
import org.gms.server.ItemInformationProvider;
import org.gms.util.RateLimitUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CDK兑换码服务类，负责CDK的配置管理、兑换执行、日志审计。
 * <p>
 * 提供CDK配置的增删改查、批量生成、兑换执行和日志查询功能。
 * 配置变更时同步更新 {@link CdkManager} 的内存缓存。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class CdkService {

    /** CDK配置主表Mapper */
    private final CdkConfigMapper cdkConfigMapper;

    /** CDK道具子表Mapper */
    private final CdkItemMapper cdkItemMapper;

    /** CDK兑换日志Mapper */
    private final CdkLogMapper cdkLogMapper;

    /** 赞助系统服务（CDK可附带赞助金额） */
    private final SponsorService sponsorService;

    /** JSON序列化工具 */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /** 安全随机数生成器（用于生成兑换码） */
    private static final SecureRandom RANDOM = new SecureRandom();

    /** 兑换码可用字符集（去除易混淆字符 0/O/I/1/L） */
    private static final String CODE_CHARS = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";

    /** 日期格式化 */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 初始化缓存
     */
    @PostConstruct
    public void init() {
        refreshCache();
        log.info("CDK配置加载完成");
    }

    // ==================== Web管理端 CRUD ====================

    /**
     * 获取CDK配置列表（含道具和物品名称）
     *
     * @param keyword 搜索关键词（匹配兑换码或批次号）
     * @param type    CDK类型
     * @param enabled 启用状态
     * @return CDK配置DTO列表
     */
    public List<CdkConfigDTO> listCdkConfigs(String keyword, Integer type, Integer enabled) {
        List<CdkConfigDO> configs;
        if (keyword != null && !keyword.isEmpty()) {
            configs = cdkConfigMapper.selectListByQuery(
                    QueryWrapper.create()
                            .where("code LIKE ?", "%" + keyword + "%")
                            .or("batch_no LIKE ?", "%" + keyword + "%"));
        } else {
            configs = cdkConfigMapper.selectAll();
        }

        // 内存过滤
        if (type != null) {
            configs = configs.stream().filter(c -> c.getType().equals(type)).collect(Collectors.toList());
        }
        if (enabled != null) {
            configs = configs.stream().filter(c -> c.getEnabled().equals(enabled)).collect(Collectors.toList());
        }

        // 按创建时间倒序（兼容历史数据 createTime 为 null）
        configs.sort((a, b) -> {
            Date ta = a.getCreateTime();
            Date tb = b.getCreateTime();
            if (ta == null && tb == null) return 0;
            if (ta == null) return 1;
            if (tb == null) return -1;
            return tb.compareTo(ta);
        });

        List<CdkItemDO> allItems = cdkItemMapper.selectAll();
        return configs.stream().map(c -> toDTO(c, allItems)).collect(Collectors.toList());
    }

    /**
     * 根据ID获取单个CDK配置（含道具）
     */
    public CdkConfigDTO getCdkConfigById(Long id) {
        CdkConfigDO config = cdkConfigMapper.selectOneById(id);
        if (config == null) return null;
        List<CdkItemDO> items = cdkItemMapper.selectListByQuery(
                QueryWrapper.create().where("cdk_id = ?", id));
        return toDTO(config, items);
    }

    /**
     * 保存CDK配置（新增或更新），级联保存道具子表。
     * 新增时检查code唯一性；更新时code不可修改。
     */
    @Transactional
    public CdkConfigDTO saveCdkConfig(CdkConfigDTO dto) {
        // 构建DO
        CdkConfigDO config = CdkConfigDO.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .batchNo(dto.getBatchNo())
                .type(dto.getType() != null ? dto.getType() : 1)
                .nxCredit(dto.getNxCredit() != null ? dto.getNxCredit() : 0)
                .nxPrepaid(dto.getNxPrepaid() != null ? dto.getNxPrepaid() : 0)
                .meso(dto.getMeso() != null ? dto.getMeso() : 0)
                .sponsor(dto.getSponsor() != null ? dto.getSponsor() : 0)
                .maxUseCount(dto.getMaxUseCount() != null ? dto.getMaxUseCount() : 1)
                .usedCount(dto.getUsedCount() != null ? dto.getUsedCount() : 0)
                .expireTime(parseDate(dto.getExpireTime()))
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .comment(dto.getComment())
                .build();

        if (config.getId() != null) {
            // 更新：保留原code、批次号、已使用次数和创建时间不变
            CdkConfigDO existing = cdkConfigMapper.selectOneById(config.getId());
            if (existing != null) {
                config.setCode(existing.getCode());
                config.setBatchNo(existing.getBatchNo());
                config.setUsedCount(existing.getUsedCount());
                config.setCreateTime(existing.getCreateTime());
            }
            cdkConfigMapper.update(config);
            // 删除旧道具
            deleteItemsByCdkId(config.getId());
        } else {
            // 新增：检查code唯一性
            if (config.getCode() == null || config.getCode().isEmpty()) {
                throw new IllegalArgumentException("CDK兑换码不能为空");
            }
            long count = cdkConfigMapper.selectCountByQuery(
                    QueryWrapper.create().where("code = ?", config.getCode()));
            if (count > 0) {
                throw new IllegalArgumentException("CDK兑换码已存在: " + config.getCode());
            }
            config.setCreateTime(new Date());
            cdkConfigMapper.insert(config);
        }

        // 保存道具列表
        if (dto.getItems() != null) {
            for (CdkConfigDTO.CdkItemDTO itemDTO : dto.getItems()) {
                if (itemDTO.getItemId() == null || itemDTO.getItemId() <= 0) continue;
                CdkItemDO item = CdkItemDO.builder()
                        .cdkId(config.getId())
                        .itemId(itemDTO.getItemId())
                        .quantity(itemDTO.getQuantity() != null ? itemDTO.getQuantity() : 1)
                        .build();
                cdkItemMapper.insert(item);
            }
        }

        refreshCache();
        return getCdkConfigById(config.getId());
    }

    /**
     * 删除CDK配置及其关联道具（日志保留）
     */
    @Transactional
    public void deleteCdkConfig(Long id) {
        deleteItemsByCdkId(id);
        cdkConfigMapper.deleteById(id);
        refreshCache();
    }

    /**
     * 批量生成CDK兑换码
     */
    @Transactional
    public CdkBatchGenRtnDTO batchGenerate(CdkBatchGenReqDTO req) {
        int count = req.getCount() != null ? req.getCount() : 1;
        int length = req.getLength() != null ? Math.min(Math.max(req.getLength(), 6), 16) : 10;
        String prefix = req.getPrefix() != null ? req.getPrefix().toUpperCase() : "";
        String batchNo = prefix + System.currentTimeMillis();
        List<String> codeList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String code;
            int retry = 0;
            do {
                code = generateCode(prefix, length);
                retry++;
            } while (cdkConfigMapper.selectCountByQuery(
                    QueryWrapper.create().where("code = ?", code)) > 0 && retry < 10);

            if (retry >= 10) {
                log.warn("CDK批量生成：{}次重试后仍碰撞，跳过", retry);
                continue;
            }

            CdkConfigDO config = CdkConfigDO.builder()
                    .code(code)
                    .batchNo(batchNo)
                    .type(req.getType() != null ? req.getType() : 2)
                    .nxCredit(req.getNxCredit() != null ? req.getNxCredit() : 0)
                    .nxPrepaid(req.getNxPrepaid() != null ? req.getNxPrepaid() : 0)
                    .meso(req.getMeso() != null ? req.getMeso() : 0)
                    .sponsor(req.getSponsor() != null ? req.getSponsor() : 0)
                    .maxUseCount(req.getMaxUseCount() != null ? req.getMaxUseCount() : 1)
                    .expireTime(parseDate(req.getExpireTime()))
                    .enabled(req.getEnabled() != null ? req.getEnabled() : 1)
                    .comment(req.getComment())
                    .createTime(new Date())
                    .build();
            cdkConfigMapper.insert(config);

            // 保存道具
            if (req.getItems() != null) {
                for (CdkConfigDTO.CdkItemDTO itemDTO : req.getItems()) {
                    if (itemDTO.getItemId() == null || itemDTO.getItemId() <= 0) continue;
                    CdkItemDO item = CdkItemDO.builder()
                            .cdkId(config.getId())
                            .itemId(itemDTO.getItemId())
                            .quantity(itemDTO.getQuantity() != null ? itemDTO.getQuantity() : 1)
                            .build();
                    cdkItemMapper.insert(item);
                }
            }

            codeList.add(code);
        }

        refreshCache();
        log.info("CDK批量生成完成：批次号={}，数量={}", batchNo, codeList.size());
        return CdkBatchGenRtnDTO.builder()
                .batchNo(batchNo)
                .totalCount(codeList.size())
                .codeList(codeList)
                .build();
    }

    // ==================== 兑换执行 ====================

    /**
     * CDK兑换结果常量
     */
    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_NOT_FOUND = 1;
    public static final int RESULT_EXPIRED = 2;
    public static final int RESULT_LIMIT_REACHED = 3;
    public static final int RESULT_DISABLED = 4;
    public static final int RESULT_INVENTORY_FULL = 5;
    public static final int RESULT_SYSTEM_ERROR = 6;
    public static final int RESULT_NOT_ONLINE = 7;

    /**
     * 执行CDK兑换（供REST接口和JS脚本调用）。
     * <p>
     * 校验流程：code存在 → enabled=1 → 未过期 → used_count < max_use_count → 背包空间足够
     * 全部通过后发放奖励并更新used_count，全程记录日志
     * </p>
     *
     * @param code       兑换码
     * @param playerName 玩家名称（JS脚本传入）
     * @param ip         客户端IP（用于限流和日志）
     * @return 兑换结果DTO
     */
    @Transactional
    public CdkRedeemRtnDTO redeem(String code, String playerName, String ip) {
        if (code == null || code.isEmpty()) {
            return failResult(null, code, playerName, ip, RESULT_NOT_FOUND, "兑换码不能为空");
        }
        code = code.toUpperCase().trim();

        // 限流检查
        if (ip != null && !ip.isEmpty()) {
            if (!RateLimitUtil.getInstance().check(ip)) {
                log.warn("CDK兑换限流触发：IP={}, code={}", ip, code);
                return failResult(null, code, playerName, ip, RESULT_SYSTEM_ERROR, "操作过于频繁，请稍后再试");
            }
        }

        // 查询CDK配置
        CdkConfigDO config = cdkConfigMapper.selectOneByQuery(
                QueryWrapper.create().where("code = ?", code));

        // 校验：码不存在
        if (config == null) {
            return failResult(null, code, playerName, ip, RESULT_NOT_FOUND, "CDK兑换码不存在");
        }

        // 校验：是否启用
        if (config.getEnabled() == null || config.getEnabled() != 1) {
            return failResult(config.getId(), code, playerName, ip, RESULT_DISABLED, "该CDK已被禁用");
        }

        // 校验：是否过期
        if (config.getExpireTime() != null && config.getExpireTime().before(new Date())) {
            return failResult(config.getId(), code, playerName, ip, RESULT_EXPIRED, "该CDK已过期（过期时间：" + DATE_FORMAT.format(config.getExpireTime()) + "）");
        }

        // 校验：使用次数
        int maxUse = config.getMaxUseCount() != null ? config.getMaxUseCount() : 1;
        int used = config.getUsedCount() != null ? config.getUsedCount() : 0;
        if (used >= maxUse) {
            return failResult(config.getId(), code, playerName, ip, RESULT_LIMIT_REACHED,
                    "该CDK已达使用上限（" + used + "/" + maxUse + "）");
        }

        // 查找在线角色
        Character chr = findOnlineCharacter(playerName);
        if (chr == null) {
            return failResult(config.getId(), code, playerName, ip, RESULT_NOT_ONLINE, "角色不在线或不存在，请先登录游戏");
        }

        Client c = chr.getClient();
        if (c == null) {
            return failResult(config.getId(), code, playerName, ip, RESULT_SYSTEM_ERROR, "无法获取客户端连接");
        }

        // 查询道具奖励列表
        List<CdkItemDO> items = cdkItemMapper.selectListByQuery(
                QueryWrapper.create().where("cdk_id = ?", config.getId()));

        // 校验背包空间
        if (!checkInventorySpace(c, items)) {
            return failResult(config.getId(), code, playerName, ip, RESULT_INVENTORY_FULL,
                    "背包空间不足，请清理背包后再兑换");
        }

        // 发放奖励（事务内执行）
        StringBuilder detailBuilder = new StringBuilder();
        try {
            // 发放点券
            if (config.getNxCredit() != null && config.getNxCredit() > 0) {
                chr.getCashShop().gainCash(CashShop.NX_CREDIT, config.getNxCredit());
                detailBuilder.append("点券x").append(config.getNxCredit()).append("; ");
            }

            // 发放抵用券
            if (config.getNxPrepaid() != null && config.getNxPrepaid() > 0) {
                chr.getCashShop().gainCash(CashShop.NX_PREPAID, config.getNxPrepaid());
                detailBuilder.append("抵用券x").append(config.getNxPrepaid()).append("; ");
            }

            // 发放金币
            if (config.getMeso() != null && config.getMeso() > 0) {
                chr.gainMeso(config.getMeso(), true);
                detailBuilder.append("金币x").append(config.getMeso()).append("; ");
            }

            // 发放道具
            if (items != null && !items.isEmpty()) {
                for (CdkItemDO item : items) {
                    String itemName = ItemInformationProvider.getInstance().getName(item.getItemId());
                    InventoryManipulator.addById(c, item.getItemId(), (short) item.getQuantity().intValue(), "", -1);
                    detailBuilder.append(itemName != null ? itemName : ("道具#" + item.getItemId()))
                            .append("x").append(item.getQuantity()).append("; ");
                }
            }

            // 处理赞助金额（CDK附带赞助额时，累加到角色赞助记录）
            if (config.getSponsor() != null && config.getSponsor() > 0) {
                try {
                    sponsorService.addSponsorAmount(
                            chr.getId(), chr.getName(),
                            c.getAccID(), c.getAccountName(),
                            config.getSponsor(), 1,
                            "CDK兑换：" + code);
                    detailBuilder.append("赞助额+").append(config.getSponsor()).append("; ");
                } catch (Exception e) {
                    log.error("CDK兑换添加赞助金额失败：code={}, player={}", code, chr.getName(), e);
                }
            }

            // 更新使用次数
            config.setUsedCount(used + 1);
            cdkConfigMapper.update(config);

            // 写成功日志
            String detail = detailBuilder.toString().trim();
            writeLog(config.getId(), code, chr, ip, RESULT_SUCCESS, "兑换成功", detail);
            refreshCache();

            log.info("CDK兑换成功：code={}, player={}, detail={}", code, chr.getName(), detail);
            return CdkRedeemRtnDTO.builder()
                    .success(true)
                    .message("兑换成功！获得：" + detail)
                    .detailJson(detail)
                    .build();

        } catch (Exception e) {
            log.error("CDK兑换发放奖励异常：code={}, player={}", code, playerName, e);
            return failResult(config.getId(), code, playerName, ip, RESULT_SYSTEM_ERROR,
                    "系统错误，请联系管理员（奖励已部分发放，请核实）");
        }
    }

    // ==================== 日志查询 ====================

    /**
     * 查询兑换日志（供反滥用检测）
     *
     * @param playerName 玩家名称
     * @param code       兑换码
     * @param ip         IP地址
     * @param result     兑换结果（null=全部）
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return 日志列表
     */
    public List<CdkLogDO> queryLogs(String playerName, String code, String ip,
                                     Integer result, String startTime, String endTime) {
        QueryWrapper qw = QueryWrapper.create();
        if (playerName != null && !playerName.isEmpty()) {
            qw.where("player_name LIKE ?", "%" + playerName + "%");
        }
        if (code != null && !code.isEmpty()) {
            qw.where("code LIKE ?", "%" + code.toUpperCase() + "%");
        }
        if (ip != null && !ip.isEmpty()) {
            qw.where("ip LIKE ?", "%" + ip + "%");
        }
        if (result != null) {
            qw.where("result = ?", result);
        }
        if (startTime != null && !startTime.isEmpty()) {
            qw.where("create_time >= ?", startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            qw.where("create_time <= ?", endTime);
        }
        qw.orderBy("create_time desc");
        return cdkLogMapper.selectListByQuery(qw);
    }

    // ==================== 内部辅助方法 ====================

    /**
     * 生成随机兑换码
     */
    private String generateCode(String prefix, int length) {
        StringBuilder sb = new StringBuilder(prefix);
        int randomLen = length - prefix.length();
        for (int i = 0; i < randomLen; i++) {
            sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * 查找在线角色
     */
    private Character findOnlineCharacter(String playerName) {
        if (playerName == null || playerName.isEmpty()) return null;
        for (org.gms.net.server.world.World world : Server.getInstance().getWorlds()) {
            Character chr = world.getPlayerStorage().getCharacterByName(playerName);
            if (chr != null && chr.isLoggedIn()) return chr;
        }
        return null;
    }

    /**
     * 检查背包空间是否足够容纳所有道具奖励
     */
    private boolean checkInventorySpace(Client c, List<CdkItemDO> items) {
        if (items == null || items.isEmpty()) return true;
        for (CdkItemDO item : items) {
            if (!InventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity().shortValue(), "")) {
                return false;
            }
        }
        return true;
    }

    /**
     * 构建失败结果DTO并写日志
     */
    private CdkRedeemRtnDTO failResult(Long cdkId, String code, String playerName, String ip,
                                        int result, String msg) {
        Character chr = findOnlineCharacter(playerName);
        writeLog(cdkId, code, chr, ip, result, msg, null);
        log.info("CDK兑换失败：code={}, player={}, result={}, msg={}", code, playerName, result, msg);
        return CdkRedeemRtnDTO.builder()
                .success(false)
                .message(msg)
                .detailJson(null)
                .build();
    }

    /**
     * 写入兑换日志
     */
    private void writeLog(Long cdkId, String code, Character chr, String ip, int result, String msg, String detail) {
        CdkLogDO logDO = CdkLogDO.builder()
                .cdkId(cdkId)
                .code(code)
                .playerName(chr != null ? chr.getName() : null)
                .playerId(chr != null ? chr.getId() : null)
                .accountName(chr != null && chr.getClient() != null ? chr.getClient().getAccountName() : null)
                .accountId(chr != null && chr.getClient() != null ? chr.getClient().getAccID() : null)
                .ip(ip)
                .result(result)
                .resultMsg(msg)
                .detail(detail)
                .createTime(new Date())
                .build();
        cdkLogMapper.insert(logDO);
    }

    /**
     * 删除CDK关联的所有道具
     */
    private void deleteItemsByCdkId(Long cdkId) {
        cdkItemMapper.deleteByQuery(
                QueryWrapper.create().where("cdk_id = ?", cdkId));
    }

    /**
     * 刷新CdkManager内存缓存
     */
    private void refreshCache() {
        CdkManager.load(cdkConfigMapper.selectAll(), cdkItemMapper.selectAll());
    }

    /**
     * 将DO实体转换为DTO（含道具列表和物品名称）
     */
    private CdkConfigDTO toDTO(CdkConfigDO config, List<CdkItemDO> allItems) {
        List<CdkConfigDTO.CdkItemDTO> itemDTOs = new ArrayList<>();
        for (CdkItemDO item : allItems) {
            if (item.getCdkId().equals(config.getId())) {
                String itemName = ItemInformationProvider.getInstance().getName(item.getItemId());
                itemDTOs.add(CdkConfigDTO.CdkItemDTO.builder()
                        .id(item.getId())
                        .itemId(item.getItemId())
                        .itemName(itemName != null ? itemName : ("未知物品#" + item.getItemId()))
                        .quantity(item.getQuantity())
                        .build());
            }
        }
        return CdkConfigDTO.builder()
                .id(config.getId())
                .code(config.getCode())
                .batchNo(config.getBatchNo())
                .type(config.getType())
                .nxCredit(config.getNxCredit())
                .nxPrepaid(config.getNxPrepaid())
                .meso(config.getMeso())
                .sponsor(config.getSponsor())
                .maxUseCount(config.getMaxUseCount())
                .usedCount(config.getUsedCount())
                .expireTime(config.getExpireTime() != null ? DATE_FORMAT.format(config.getExpireTime()) : null)
                .enabled(config.getEnabled())
                .comment(config.getComment())
                .createTime(config.getCreateTime() != null ? DATE_FORMAT.format(config.getCreateTime()) : null)
                .updateTime(config.getUpdateTime() != null ? DATE_FORMAT.format(config.getUpdateTime()) : null)
                .items(itemDTOs)
                .build();
    }

    /**
     * 解析日期字符串
     */
    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            return DATE_FORMAT.parse(dateStr);
        } catch (Exception e) {
            log.warn("日期解析失败：{}", dateStr);
            return null;
        }
    }
}
