package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductOrderSystemApplication {

    public static void main(String[] args) {

        System.exit(SpringApplication.exit(SpringApplication.run(ProductOrderSystemApplication.class, args)));
    }

}
