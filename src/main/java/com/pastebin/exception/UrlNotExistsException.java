package com.pastebin.exception;

public class UrlNotExistsException extends Exception{
    public UrlNotExistsException() {
    }

    public UrlNotExistsException(String message) {
        super(message);
    }

    public UrlNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UrlNotExistsException(Throwable cause) {
        super(cause);
    }

    public UrlNotExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
