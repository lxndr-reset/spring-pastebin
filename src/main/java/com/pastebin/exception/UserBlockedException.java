package com.pastebin.exception;

public class UserBlockedException extends RuntimeException {
    public UserBlockedException() {
    }

    public UserBlockedException(String message) {
        super(message);
    }

    public UserBlockedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserBlockedException(Throwable cause) {
        super(cause);
    }

    public UserBlockedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
