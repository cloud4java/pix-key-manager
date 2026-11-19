package com.bancomundi.pix_key_manager.service;

public class InvalidPixKeyException extends RuntimeException {

    public InvalidPixKeyException(String message) {
        super(message);
    }
}
