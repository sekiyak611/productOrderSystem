package com.example.demo.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;

import com.example.demo.model.ProductOrderResponce;
import com.example.demo.service.ProductOrderService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ProductOrderController {

    private final ProductOrderService productOrderService;

    public ProductOrderResponce productOrderController(Map<String, Integer> orderMap, int paymentId) {

        // 商品注文
        ProductOrderResponce responce = productOrderService.productOrderService(orderMap, paymentId);
        
        return responce;

    }

}
