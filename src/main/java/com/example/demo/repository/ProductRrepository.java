package com.example.demo.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.demo.common.EnumProductType;
import com.example.demo.dao.Product;

/**
 * 商品レポジトリ 疑似データ
 */
@Repository
public class ProductRrepository {

    public static final List<Product> productList = List.of(
            new Product("A001", "ノートPC", new BigDecimal(100000), EnumProductType.ELECTRONIC),
            new Product("B002", "マウス", new BigDecimal(3000), EnumProductType.ELECTRONIC),
            new Product("C003", "水", new BigDecimal(100), EnumProductType.FOOD));

    /**
     * 商品IDをもとに商品データを取得
     * @param productId キーとなる商品ID
     * @return 取得してきた商品データ
     */
    public List<Product> findById(List<String> productId) {
        return productList.stream().filter(p -> productId.contains(p.getProductId())).toList();
    }
}
