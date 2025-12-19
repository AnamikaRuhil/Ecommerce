package com.ecommerce.advance.order.exception;

public class DataNotFoundException extends RuntimeException{

    public DataNotFoundException(String message) {
        super("Data not found: " + message);

    }
}
