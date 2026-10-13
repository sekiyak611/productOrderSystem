package com.example.demo.common;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EnumPaymentMethod {

    CREDIT_CARD(1, "クレジットカード", "クレジットカードで%s円を決済しました。"),
    BANK_TRANSFER(2, "銀行振込", "%s円の銀行振込を受け付けました。");

    private final int paymentId;
    private final String paymentMethod;

    /** 支払完了メッセージの書式（%s に支払金額が入る） */
    private final String messageFormat;

    /**
     * 支払方法出力機能 （Enumクラスに実装しないほうがいいかなぁ。責務分離の観点で別クラスに実装すると責務の過剰分離だと判断した。）
     */
    public static void getPaymentMethod() {

        System.out.println("支払方法を数字で選択してください。");
        for (EnumPaymentMethod e : EnumPaymentMethod.values()) {
            System.out.println(e.paymentId + "：" + e.paymentMethod);
        }
    }

    /**
     * 支払方法IDから支払方法を取得する
     * @param paymentId 支払方法を識別するID
     * @return 支払方法（存在しない場合は空）
     */
    public static Optional<EnumPaymentMethod> getById(int paymentId) {
        return Arrays.stream(EnumPaymentMethod.values()).filter(e -> e.paymentId == paymentId).findFirst();
    }

    /**
     * 支払処理（メッセージ作成のみ）
     * @param amount 支払金額
     * @return 支払完了メッセージ
     */
    public String paymentMessage(BigDecimal amount) {
        return String.format(messageFormat, amount.toPlainString());
    }
}
