package org.gms.model.dto;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.gms.exception.BaseErrorInfoInterface;
import org.gms.exception.BizExceptionEnum;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.BufferedReader;
import java.util.Optional;
import java.util.UUID;

/**
 * 统一响应体
 * 封装API返回结果，包含状态码、消息、响应ID和数据
 *
 * @param <T> 数据类型
 */
@Data
@Slf4j
public class ResultBody<T> {
    /** 状态码 */
    private Integer code;
    /** 提示消息 */
    private String message;
    /** 响应ID，用于追踪请求 */
    private String responseId;
    /** 响应数据 */
    private T data;

    public ResultBody() {
    }

    public ResultBody(BaseErrorInfoInterface errorInfo) {
        this.code = errorInfo.getResultCode();
        this.message = errorInfo.getResultMsg();
    }

    /**
     * 创建成功响应（无数据）
     *
     * @param <T> 数据类型
     * @return 成功响应体
     */
    public static <T> ResultBody<T> success() {
        return success(null);
    }

    /**
     * 创建成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功响应体
     */
    public static <T> ResultBody<T> success(T data) {
        ResultBody<T> rb = new ResultBody<>();
        rb.setResponseId(UUID.randomUUID().toString());
        rb.setCode(BizExceptionEnum.SUCCESS.getResultCode());
        rb.setMessage(BizExceptionEnum.SUCCESS.getResultMsg());
        rb.setData(data);
        return rb;
    }

    /**
     * 创建成功响应，复用请求ID
     *
     * @param request 请求体
     * @param data    响应数据
     * @param <T>     数据类型
     * @return 成功响应体
     */
    public static <T> ResultBody<T> success(SubmitBody<?> request, T data) {
        ResultBody<T> rb = new ResultBody<>();
        rb.setResponseId(request.getRequestId());
        rb.setCode(BizExceptionEnum.SUCCESS.getResultCode());
        rb.setMessage(BizExceptionEnum.SUCCESS.getResultMsg());
        rb.setData(data);
        return rb;
    }

    /**
     * 创建错误响应，使用错误信息接口
     *
     * @param req      HTTP请求
     * @param errorInfo 错误信息接口
     * @param <T>      数据类型
     * @return 错误响应体
     */
    public static <T> ResultBody<T> error(HttpServletRequest req, BaseErrorInfoInterface errorInfo) {
        return error(req, errorInfo.getResultCode(), errorInfo.getResultMsg());
    }

    /**
     * 创建错误响应，自定义错误消息
     *
     * @param req     HTTP请求
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 错误响应体
     */
    public static <T> ResultBody<T> error(HttpServletRequest req, String message) {
        return error(req, -1, message);
    }

    /**
     * 创建错误响应，自定义状态码和错误消息
     *
     * @param req     HTTP请求
     * @param code    状态码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 错误响应体
     */
    public static <T> ResultBody<T> error(HttpServletRequest req, Integer code, String message) {
        String method = req.getMethod();
        String contentType = req.getContentType();
        ResultBody<T> rb = new ResultBody<>();
        if (RequestMethod.POST.name().equals(method) && contentType.contains("application/json")) {
            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = req.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            } catch (Exception e) {
                log.error("Error reading request body: {}", e.getMessage(), e);
            }
            String requestId = null;
            try {
                SubmitBody<?> request = JSONObject.parseObject(body.toString(), SubmitBody.class);
                requestId = request == null ? null : request.getRequestId();
            } catch (Exception ignore) {
            }
            rb.setResponseId(Optional.ofNullable(requestId).orElse(UUID.randomUUID().toString()));
        } else {
            rb.setResponseId(UUID.randomUUID().toString());
        }
        rb.setCode(code);
        rb.setMessage(message);
        rb.setData(null);
        return rb;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}