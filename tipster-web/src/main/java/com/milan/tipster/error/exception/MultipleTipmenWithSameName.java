package com.milan.tipster.error.exception;

public class MultipleTipmenWithSameName extends RuntimeException {
    public MultipleTipmenWithSameName(String message) {
        super(message);
    }
}
