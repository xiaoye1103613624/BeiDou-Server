package org.gms.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.gms.model.dto.ResultBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 全局异常处理器
 * 统一处理业务异常、数据库冲突、运行时异常和Servlet异常，返回标准化的ResultBody
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 匹配 MySQL 唯一键冲突错误信息：Duplicate entry '值' for key '键名' */
    private static final Pattern DUPLICATE_KEY_PATTERN =
            Pattern.compile("Duplicate entry '([^']*)' for key '([^']*)'");

    /**
     * 处理自定义的业务异常
     */
    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public ResultBody<Object> bizExceptionHandler(HttpServletRequest req, BizException e) {
        logger.error("发生业务异常！原因是：{}", e.getErrorMsg());
        return ResultBody.error(req, e.getErrorCode(), e.getErrorMsg());
    }

    /**
     * 处理数据库唯一键冲突异常，提取冲突信息返回中文提示
     */
    @ExceptionHandler(value = DataIntegrityViolationException.class)
    @ResponseBody
    public ResultBody<Object> dataIntegrityViolationHandler(HttpServletRequest req, DataIntegrityViolationException e) {
        String detailInfo = extractDuplicateKeyInfo(e);
        logger.error("数据完整性问题！原因是: {}", detailInfo);
        String msg = MessageFormat.format(BizExceptionEnum.DUPLICATE_KEY.getResultMsg(), detailInfo);
        return ResultBody.error(req, BizExceptionEnum.DUPLICATE_KEY.getResultCode(), msg);
    }

    /**
     * IllegalArgumentException NullPointerException UnsupportedOperationException都是RuntimeException
     * 这里直接捕获RuntimeException来代替一个一个去捕获
     */
    @ExceptionHandler(value = RuntimeException.class)
    @ResponseBody
    public ResultBody<Object> exceptionHandler(HttpServletRequest req, RuntimeException e) {
        logger.error("发生运行时异常！原因是:", e);
        return ResultBody.error(req, BizExceptionEnum.BODY_NOT_MATCH);
    }

    /**
     * 处理请求方法不支持的异常
     */
    @ExceptionHandler(value = ServletException.class)
    @ResponseBody
    public ResultBody<Object> exceptionHandler(HttpServletRequest req, ServletException e) {
        logger.error("发生请求时异常！原因是:", e);
        return ResultBody.error(req, BizExceptionEnum.REQUEST_METHOD_SUPPORT);
    }
    /**
     * 处理其他异常
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultBody<Object> exceptionHandler(HttpServletRequest req, Exception e) {
        logger.error("未知异常！原因是:", e);
        return ResultBody.error(req, BizExceptionEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 从 DataIntegrityViolationException 异常链中提取重复键冲突信息。
     * 递归遍历 cause 链，匹配 MySQL 的 "Duplicate entry 'X' for key 'Y'" 格式。
     *
     * @param e 数据完整性异常
     * @return 中文可读的冲突描述，如 "装备ID[1302030] 已存在强化配置"
     */
    private String extractDuplicateKeyInfo(Throwable e) {
        if (e == null) return "未知";
        String msg = e.getMessage();
        if (msg != null) {
            Matcher m = DUPLICATE_KEY_PATTERN.matcher(msg);
            if (m.find()) {
                String value = m.group(1);
                String key = m.group(2);
                return formatDuplicateKeyMessage(value, key);
            }
        }
        return extractDuplicateKeyInfo(e.getCause());
    }

    /**
     * 根据唯一键名称和冲突值生成可读的中文提示。
     */
    private String formatDuplicateKeyMessage(String value, String key) {
        // 装备强化配置 - 同一装备ID只能有一条配置
        if (key != null && key.contains("uk_item_id") && key.contains("equip_enhance_config")) {
            return "该装备的强化配置已存在，装备ID: " + value;
        }
        // 装备强化等级 - 同一配置下等级不能重复
        if (key != null && key.contains("uk_config_level")) {
            return "该强化等级已存在，等级: " + value;
        }
        // 装备进阶路线 - 同一职业群只能有一条路线
        if (key != null && key.contains("uk_job_group")) {
            return "该职业群的进阶路线已存在，职业群: " + value;
        }
        // 装备进阶阶段 - 同路线下阶段不能重复
        if (key != null && key.contains("uk_route_stage")) {
            return "该进阶阶段已存在，阶段: " + value;
        }
        // 通用兜底
        return "键[" + key + "] 的值[" + value + "] 已存在，不能重复添加";
    }
}