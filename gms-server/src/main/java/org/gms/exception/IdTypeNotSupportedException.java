package org.gms.exception;

/**
 * ID类型不支持异常
 * 当传入的ID类型不被当前操作支持时抛出
 */
public class IdTypeNotSupportedException extends Exception {
    public IdTypeNotSupportedException() {
        super("The given ID type is not supported");
    }

    public IdTypeNotSupportedException(String message) {
        super(message);
    }
}