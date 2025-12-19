package com.ecommerce.advance.productdetail.exception;

public class BusinessException extends RuntimeException{

    public BusinessException(String message) {
        super("Business Exception: " + message);

    }
}
