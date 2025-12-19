package com.ecommerce.advance.notification.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super("Business Exception: " + message);

    }
}
