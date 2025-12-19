package com.ecommerce.advance.product.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super("Business Exception: " + message);

    }
}
