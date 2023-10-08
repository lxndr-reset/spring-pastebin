package com.pastebin.exception;

public class MessageDeletedException extends RuntimeException {
    public MessageDeletedException(String message) {
        super(message);
    }

    public MessageDeletedException(String message, Throwable cause) {
        super(message, cause);
    }
}
