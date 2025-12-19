package com.ecommerce.advance.price.exception;

public class BusinessException extends RuntimeException{

    public BusinessException(String message) {
        super("Business Exception: " + message);

    }
}
