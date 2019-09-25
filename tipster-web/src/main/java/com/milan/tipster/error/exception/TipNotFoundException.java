package com.milan.tipster.error.exception;

public class TipNotFoundException extends RuntimeException {
    public TipNotFoundException(String message) {
        super(message);
    }
}
