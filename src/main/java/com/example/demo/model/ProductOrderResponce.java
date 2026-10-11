package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductOrderResponce {

    private String message;
    private boolean isComplete = false;

}
