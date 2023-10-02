package com.pastebin.exception;

import java.util.Objects;

public class UserBlockedException extends RuntimeException {
    public UserBlockedException(String message) {
        super(message);
    }

    public UserBlockedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserBlockedException(Throwable cause) {
        super(cause);
    }

    public UserBlockedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackService) {
        super(message, cause, enableSuppression, writableStackService);
    }
}
