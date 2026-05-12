package org.gms.exception;

/**
 * 【接口】BaseErrorInfoInterface：由 `exception` 模块实现的契约。
 */
public interface BaseErrorInfoInterface {
    Integer getResultCode();
    String getResultMsg();
}
