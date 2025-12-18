package com.ecommerce.advance.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayApplication {
    public static final Logger logger = LoggerFactory.getLogger(GatewayApplication.class);

    public static void main(String [] args){
        logger.info("Inside Gateway");
        SpringApplication.run(GatewayApplication.class,args);
    }
}
