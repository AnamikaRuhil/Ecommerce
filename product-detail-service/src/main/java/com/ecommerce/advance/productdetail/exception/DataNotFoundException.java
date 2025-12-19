package com.ecommerce.advance.productdetail.exception;

public class DataNotFoundException extends RuntimeException{

    public DataNotFoundException(String message) {
        super("Data not found: " + message);

    }
}
