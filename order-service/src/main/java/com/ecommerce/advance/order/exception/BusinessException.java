package com.ecommerce.advance.order.exception;

public class BusinessException extends RuntimeException{

    public BusinessException(String message) {
        super("Business Exception: " + message);

    }
}
