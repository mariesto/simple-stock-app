package com.mariesto.simplestockapp.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String key) {
        super(key + " Not Found");
    }
}
