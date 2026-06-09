package org.gms.exception;

import org.gms.util.I18nUtil;

/**
 * 业务异常枚举类
 * 定义系统所有业务异常的状态码和对应的国际化错误消息
 */
public enum BizExceptionEnum implements BaseErrorInfoInterface {

    /** 请求成功 */
    SUCCESS(20000, I18nUtil.getExceptionMessage("SUCCESS")),
    /** 请求体格式错误 */
    BODY_NOT_MATCH(40000, I18nUtil.getExceptionMessage("BODY_NOT_MATCH")),
    /** 请求方式不支持 */
    REQUEST_METHOD_SUPPORT(40001, I18nUtil.getExceptionMessage("REQUEST_METHOD_SUPPORT")),
    /** 非法参数 */
    ILLEGAL_PARAMETERS(40002, I18nUtil.getExceptionMessage("ILLEGAL_PARAMETERS")),
    /** 资源不存在 */
    NOT_FOUND(40004, I18nUtil.getExceptionMessage("NOT_FOUND")),
    /** 重复键冲突 */
    DUPLICATE_KEY(40005, I18nUtil.getExceptionMessage("DUPLICATE_KEY")),
    /** 内部服务器错误 */
    INTERNAL_SERVER_ERROR(50000, I18nUtil.getExceptionMessage("INTERNAL_SERVER_ERROR")),
    /** 服务器繁忙 */
    SERVER_BUSY(50003, I18nUtil.getExceptionMessage("SERVER_BUSY"));

    /** 结果状态码 */
    private final Integer resultCode;
    /** 结果消息 */
    private final String resultMsg;

    BizExceptionEnum(Integer resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    @Override
    public Integer getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultMsg() {
        return resultMsg;
    }
}