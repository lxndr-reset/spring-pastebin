package com.pastebin.exception;

public class NoAvailableShortURLException extends Exception{
    public NoAvailableShortURLException(String message) {
        super(message);
    }

    public NoAvailableShortURLException(String message, Throwable cause) {
        super(message, cause);
    }
}
