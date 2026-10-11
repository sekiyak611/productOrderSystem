package com.example.demo.common;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EnumPaymentMethod {

    CREDIT_CARD(1, "クレジットカード"), BANK_TRANSFER(2, "銀行振込");

    private int paymentId;
    private String paymentMethod;

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
     * 支払方法が存在するか確認
     * @param paymentId 支払方法を識別するID
     * @return 支払方法
     */
    public static String getById(int paymentId) {

        for (EnumPaymentMethod e : EnumPaymentMethod.values()) {
            if (e.paymentId == paymentId) {
                System.out.println(e.paymentMethod + "が選択されました。");
                return e.paymentMethod;
            }
        }
        System.out.println("存在しない支払方法です。");
        return null;
    }
}
