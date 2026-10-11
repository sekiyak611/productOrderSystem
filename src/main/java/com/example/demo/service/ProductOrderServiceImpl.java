package com.example.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.common.EnumPaymentMethod;
import com.example.demo.common.EnumUserRank;
import com.example.demo.dao.Product;
import com.example.demo.model.ProductOrderResponce;
import com.example.demo.repository.ProductRrepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductOrderServiceImpl implements ProductOrderService {

    private final ProductRrepository productRrepository;

    public ProductOrderResponce productOrderService(Map<String, Integer> orderMap, int paymentId) {
        
        //　商品データ取得
        List<String> productIdList = orderMap.keySet().stream().toList();
        List<Product> productList = productRrepository.findById(productIdList);
        EnumPaymentMethod.getById(paymentId);
        System.out.println(productList);
        
        // 個数に応じて注文データを複製
        List<Product> expandedProductList = orderMap.entrySet().stream()
                .flatMap(o -> {
                    String productId = o.getKey();
                    int quantity = o.getValue();

                    Product product = productList.stream()
                            .filter(p -> p.getProductId().equals(productId))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("商品が見つかりません: " + productId));

                    // 個数分複製してストリームにする
                    return Collections.nCopies(quantity, product).stream();
                })
                .collect(Collectors.toList());
        
        // 商品種別による割引
        
        // 会員割引
        
        // 配送料計算
        
        // 注文完了メッセージ作成

        String message = "";
        ProductOrderResponce responce = new ProductOrderResponce(message, true);
        return responce;
    }
    
    /**
     * 商品種別による割引
     * 商品種別によって割引額を変更する
     * @param products 注文データリスト
     * @return 割引後の商品データリスト
     */
    private List<Product> productDiscount(List<Product> products) {

        return products.stream().map(product -> {

            BigDecimal discountedPrice = switch (product.getProductType()) {
            case FOOD -> product.getPrice().multiply(new BigDecimal("0.05")).setScale(0, RoundingMode.DOWN);
            case ELECTRONIC -> product.getPrice().compareTo(new BigDecimal(50000)) >= 0
                    ? product.getPrice().multiply(new BigDecimal("0.1")).setScale(0, RoundingMode.DOWN)
                    : product.getPrice();
            default -> product.getPrice();
            };

            product.setPrice(discountedPrice);

            return product;
        }).toList();
    }
    
    /**
     * 会員割引
     * 会員ランクによって割引額を変更する
     * @param totalPrice 割引前の金額
     * @param userRank 会員ランク
     * @return 割引後の金額
     */
    private BigDecimal userDiscount(BigDecimal subtotal, EnumUserRank userRank) {

        return switch (userRank) {
        case EnumUserRank.SILVER -> subtotal.multiply(new BigDecimal("0.03").setScale(0, RoundingMode.DOWN));
        case EnumUserRank.GOLD -> subtotal.multiply(new BigDecimal("0.05").setScale(0, RoundingMode.DOWN));
        default -> subtotal;
        };
    }
    
    /**
     * 配送料計算
     * @param price 配送料計算前の金額
     * @return 配送料計算後の金額
     */
    private BigDecimal shippingFeeCalc(BigDecimal price) {
        return price.compareTo(new BigDecimal(10000)) <= 0 ? price : price.add(new BigDecimal(500));
    }
}
