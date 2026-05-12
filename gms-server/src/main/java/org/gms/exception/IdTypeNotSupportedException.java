package org.gms.exception;

/**
 * 【类型】IdTypeNotSupportedException（class），包 `org.gms.exception`。
 */
public class IdTypeNotSupportedException extends Exception {
    public IdTypeNotSupportedException() {
        super("The given ID type is not supported");
    }

    public IdTypeNotSupportedException(String message) {
        super(message);
    }
}
