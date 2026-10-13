package com.example.demo.campaign;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

/**
 * 高額注文キャンペーン 注文金額が100,000円以上の場合に2%割引する
 */
@Component
public class LargeOrderCampaign implements Campaign {

    /** 適用条件となる注文金額 */
    private static final BigDecimal THRESHOLD = new BigDecimal(100000);

    /** 割引率 */
    private static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.02");

    @Override
    public String getName() {
        return "キャンペーン割引（注文金額100,000円以上で2%割引）";
    }

    /**
     * 割引額は注文金額に割引率を掛け、1円未満を切り捨てる。
     */
    @Override
    public BigDecimal discount(BigDecimal amount) {
        if (amount.compareTo(THRESHOLD) < 0) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(DISCOUNT_RATE).setScale(0, RoundingMode.DOWN);
    }
}
