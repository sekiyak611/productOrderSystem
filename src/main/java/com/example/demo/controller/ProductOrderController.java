package com.example.demo.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;

import com.example.demo.common.EnumUserRank;
import com.example.demo.model.ProductOrderResponce;
import com.example.demo.service.ProductOrderService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ProductOrderController {

    private final ProductOrderService productOrderService;

    /**
     * コンソールから受け取った引数をサービスに投げる
     * @param orderMap 注文データ
     * @param paymentId 支払方法
     * @param userRank 会員ランク
     * @return 注文計算結果
     */
    public ProductOrderResponce productOrderController(Map<String, Integer> orderMap, int paymentId, EnumUserRank userRank) {

        // 商品注文
        ProductOrderResponce responce = productOrderService.productOrderService(orderMap, paymentId, userRank);

        return responce;

    }

}
