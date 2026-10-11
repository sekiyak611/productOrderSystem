package com.example.demo.service;

import java.util.Map;

import com.example.demo.model.ProductOrderResponce;

public interface ProductOrderService {

    public ProductOrderResponce productOrderService(Map<String, Integer> orderMap, int paymentId);

}
