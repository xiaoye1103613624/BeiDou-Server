package org.gms.exception;

/**
 * 功能未启用异常
 * 当尝试使用未在ServerConstant中启用的功能时抛出
 */
public class NotEnabledException extends RuntimeException {

    public NotEnabledException() {
        super("Feature not enabled, please enable the feature in ServerConstant");
    }

    public NotEnabledException(String message) {
        super(message);
    }
}