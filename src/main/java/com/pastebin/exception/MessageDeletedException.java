package com.pastebin.exception;

public class MessageDeletedException extends RuntimeException {
    public MessageDeletedException() {
    }

    public MessageDeletedException(String message) {
        super(message);
    }

    public MessageDeletedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageDeletedException(Throwable cause) {
        super(cause);
    }

    public MessageDeletedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
