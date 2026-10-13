package com.example.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.campaign.Campaign;
import com.example.demo.common.EnumPaymentMethod;
import com.example.demo.common.EnumUserRank;
import com.example.demo.dao.Product;
import com.example.demo.model.ProductOrderResponce;
import com.example.demo.repository.ProductRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductOrderServiceImpl implements ProductOrderService {

    private final ProductRepository productRepository;

    /** 適用対象のキャンペーン一覧（Springが Campaign 実装のBeanをすべて注入する） */
    private final List<Campaign> campaigns;

    /**
     * 注文明細 商品と個数の組
     * @param product 商品
     * @param quantity 個数
     */
    private record OrderLine(Product product, int quantity) {

        /**
         * 割引前の明細金額（単価 × 個数）
         * @return 明細金額
         */
        BigDecimal amount() {
            return product.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }

    /**
     * コントローラから受け取った引数をもとにビジネスロジックをする
     * @param orderMap 注文データ（キー：商品ID、値：個数）
     * @param paymentId 支払方法
     * @param userRank 会員ランク
     * @return 計算結果
     * @throws IllegalArgumentException 注文内容が不正な場合
     */
    public ProductOrderResponce productOrderService(Map<String, Integer> orderMap, int paymentId, EnumUserRank userRank) {

        // 入力チェック
        validateOrder(orderMap, userRank);
        Optional<EnumPaymentMethod> paymentMethod = EnumPaymentMethod.getById(paymentId);
        if (paymentMethod.isEmpty()) {
            return new ProductOrderResponce("存在しない支払方法です。", false);
        }

        // 商品データ取得
        List<OrderLine> orderLines = createOrderLines(orderMap);

        // 商品種別による割引後の小計
        BigDecimal subtotal = orderLines.stream().map(this::productDiscount).reduce(BigDecimal.ZERO, BigDecimal::add);

        // 会員割引
        BigDecimal userDiscount = userDiscount(subtotal, userRank);
        BigDecimal afterUserDiscount = subtotal.subtract(userDiscount);

        StringBuilder message = new StringBuilder();
        orderLines.forEach(line -> message.append(line.product().getProductName()).append(" ")
                .append(line.product().getPrice().toPlainString()).append("円 × ").append(line.quantity()).append("\n"));
        message.append("\n");
        message.append("商品割引後小計：").append(subtotal.toPlainString()).append("円\n");
        message.append("会員割引：").append(userDiscount.toPlainString()).append("円\n");

        // キャンペーン割引（会員割引後の金額に対して順に適用する）
        BigDecimal productTotal = afterUserDiscount;
        for (Campaign campaign : campaigns) {
            BigDecimal campaignDiscount = campaign.discount(productTotal);
            if (campaignDiscount.signum() > 0) {
                message.append(campaign.getName()).append("：").append(campaignDiscount.toPlainString()).append("円\n");
                productTotal = productTotal.subtract(campaignDiscount);
            }
        }

        // 配送料計算（会員割引適用後の商品金額で判定する）
        BigDecimal shippingFee = shippingFeeCalc(afterUserDiscount);
        BigDecimal paymentAmount = productTotal.add(shippingFee);

        // 注文完了メッセージ作成
        message.append("送料：").append(shippingFee.toPlainString()).append("円\n");
        message.append("支払金額：").append(paymentAmount.toPlainString()).append("円\n");
        message.append("\n");
        message.append(paymentMethod.get().paymentMessage(paymentAmount));
        return new ProductOrderResponce(message.toString(), true);
    }

    /**
     * 注文データの入力チェック
     * @param orderMap 注文データ
     * @param userRank 会員ランク
     * @throws IllegalArgumentException 注文内容が不正な場合
     */
    private void validateOrder(Map<String, Integer> orderMap, EnumUserRank userRank) {

        if (orderMap == null || orderMap.isEmpty()) {
            throw new IllegalArgumentException("注文する商品がありません。");
        }
        if (userRank == null) {
            throw new IllegalArgumentException("会員ランクが指定されていません。");
        }
        orderMap.forEach((productId, quantity) -> {
            if (quantity == null || quantity <= 0) {
                throw new IllegalArgumentException("個数は1以上を指定してください。商品ID：" + productId);
            }
        });
    }

    /**
     * 注文データをもとに注文明細を作成する
     * @param orderMap 注文データ
     * @return 注文明細リスト（注文データの順序を保持）
     * @throws IllegalArgumentException 存在しない商品IDが含まれる場合
     */
    private List<OrderLine> createOrderLines(Map<String, Integer> orderMap) {

        List<Product> productList = productRepository.findById(orderMap.keySet().stream().toList());

        return orderMap.entrySet().stream().map(o -> {
            String productId = o.getKey();
            Product product = productList.stream().filter(p -> p.getProductId().equals(productId)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("存在しない商品IDです。商品ID：" + productId));
            return new OrderLine(product, o.getValue());
        }).toList();
    }

    /**
     * 商品種別による割引 商品種別によって割引額を変更する
     * 明細金額（単価 × 個数）に係数を掛け、1円未満を切り捨てる。
     * ELECTRONICS の 50,000円以上の判定は単価で行う。
     * @param orderLine 注文明細
     * @return 割引後の明細金額
     */
    private BigDecimal productDiscount(OrderLine orderLine) {

        Product product = orderLine.product();
        BigDecimal amount = orderLine.amount();

        return switch (product.getProductType()) {
        case FOOD -> amount.multiply(new BigDecimal("0.05")).setScale(0, RoundingMode.DOWN);
        case ELECTRONICS -> product.getPrice().compareTo(new BigDecimal(50000)) >= 0
                ? amount.multiply(new BigDecimal("0.1")).setScale(0, RoundingMode.DOWN)
                : amount;
        default -> amount;
        };
    }

    /**
     * 会員割引 会員ランクによって割引額を変更する
     * 割引額は小計に割引率を掛け、1円未満を切り捨てる。
     * @param subtotal 商品種別割引後の小計
     * @param userRank 会員ランク
     * @return 会員割引額
     */
    private BigDecimal userDiscount(BigDecimal subtotal, EnumUserRank userRank) {

        BigDecimal discountRate = switch (userRank) {
        case EnumUserRank.SILVER -> new BigDecimal("0.03");
        case EnumUserRank.GOLD -> new BigDecimal("0.05");
        default -> BigDecimal.ZERO;
        };

        return subtotal.multiply(discountRate).setScale(0, RoundingMode.DOWN);
    }

    /**
     * 配送料計算
     * @param price 会員割引適用後の商品金額
     * @return 配送料
     */
    private BigDecimal shippingFeeCalc(BigDecimal price) {
        return price.compareTo(new BigDecimal(10000)) >= 0 ? BigDecimal.ZERO : new BigDecimal(500);
    }
}
