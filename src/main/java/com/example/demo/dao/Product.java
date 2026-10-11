package com.example.demo.dao;

import java.math.BigDecimal;

import com.example.demo.common.EnumProductType;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 商品
 **/
@AllArgsConstructor
@Data
public class Product {

    /** 商品ID */
    private String productId;

    /** 商品名 */
    private String productName;

    /** 価格 */
    private BigDecimal price;

    /** 商品種別 */
    private EnumProductType productType;

}
