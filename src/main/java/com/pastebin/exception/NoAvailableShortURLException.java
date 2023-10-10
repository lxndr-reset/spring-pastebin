package com.pastebin.exception;

public class NoAvailableShortURLException extends Exception{
    public NoAvailableShortURLException() {
    }

    public NoAvailableShortURLException(String message) {
        super(message);
    }

    public NoAvailableShortURLException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAvailableShortURLException(Throwable cause) {
        super(cause);
    }

    public NoAvailableShortURLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
