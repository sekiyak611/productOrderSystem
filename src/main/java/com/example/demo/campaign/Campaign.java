package com.example.demo.campaign;

import java.math.BigDecimal;

/**
 * キャンペーン割引
 * キャンペーンを追加する場合は、本インタフェースの実装クラスを @Component として作成する。
 */
public interface Campaign {

    /**
     * キャンペーン名（明細出力に使用する）
     * @return キャンペーン名
     */
    String getName();

    /**
     * キャンペーン割引額を計算する
     * @param amount 割引対象の注文金額
     * @return 割引額（対象外の場合は 0）
     */
    BigDecimal discount(BigDecimal amount);
}
