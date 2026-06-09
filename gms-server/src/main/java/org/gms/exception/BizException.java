package org.gms.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 业务异常类
 * 用于封装业务逻辑中的异常情况
 */
@Setter
@Getter
public class BizException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 错误码 */
    protected Integer errorCode;

    /** 错误消息 */
    protected String errorMsg;

    /**
     * 空构造函数
     */
    public BizException() {
        super();
    }

    /**
     * 根据错误信息接口构造
     *
     * @param errorInfoInterface 错误信息接口
     */
    public BizException(BaseErrorInfoInterface errorInfoInterface) {
        super(String.valueOf(errorInfoInterface.getResultCode()));
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = errorInfoInterface.getResultMsg();
    }

    /**
     * 根据错误信息接口和原因构造
     *
     * @param errorInfoInterface 错误信息接口
     * @param cause              异常原因
     */
    public BizException(BaseErrorInfoInterface errorInfoInterface, Throwable cause) {
        super(String.valueOf(errorInfoInterface.getResultCode()), cause);
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = errorInfoInterface.getResultMsg();
    }

    /**
     * 根据错误消息构造
     *
     * @param errorMsg 错误消息
     */
    public BizException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    /**
     * 根据错误码和错误消息构造
     *
     * @param errorCode 错误码
     * @param errorMsg  错误消息
     */
    public BizException(Integer errorCode, String errorMsg) {
        super(String.valueOf(errorCode));
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * 根据错误码、错误消息和原因构造
     *
     * @param errorCode 错误码
     * @param errorMsg  错误消息
     * @param cause     异常原因
     */
    public BizException(Integer errorCode, String errorMsg, Throwable cause) {
        super(String.valueOf(errorCode), cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * 创建非法参数异常
     *
     * @return 非法参数异常
     */
    public static BizException illegalArgument() {
        return new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS);
    }

    /**
     * 创建带自定义消息的非法参数异常
     *
     * @param errorMsg 错误消息
     * @return 非法参数异常
     */
    public static BizException illegalArgument(String errorMsg) {
        return new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), errorMsg);
    }

    /**
     * 抛出非法参数异常
     */
    public static void throwIllegalArgument() {
        throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS);
    }

    /**
     * 抛出带自定义消息的非法参数异常
     *
     * @param errorMsg 错误消息
     */
    public static void throwIllegalArgument(String errorMsg) {
        throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), errorMsg);
    }

    /**
     * 获取错误消息
     *
     * @return 错误消息
     */
    public String getMessage() {
        return errorMsg;
    }

    /**
     * 重写fillInStackTrace方法，不填充堆栈以提高性能
     *
     * @return 异常对象本身
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}