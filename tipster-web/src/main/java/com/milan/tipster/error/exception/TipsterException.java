package com.milan.tipster.error.exception;

public class TipsterException extends RuntimeException {

    public TipsterException(String message) {
        super(message);
    }

    public TipsterException(String message, Throwable cause) {
        super(message, cause);
    }
}
