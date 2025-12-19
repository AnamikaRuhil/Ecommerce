package com.ecommerce.advance.cart.exception;

public class BusinessException extends RuntimeException{
    public BusinessException(String message) {
        super("Business exception : " + message);

    }
}
