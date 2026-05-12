package org.gms.exception;

/**
 * 【类型】NotEnabledException（class），包 `org.gms.exception`。
 */
public class NotEnabledException extends RuntimeException {

    public NotEnabledException() {
        super("Feature not enabled, please enable the feature in ServerConstant");
    }

    public NotEnabledException(String message) {
        super(message);
    }
}
