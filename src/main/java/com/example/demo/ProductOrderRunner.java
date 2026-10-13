package com.example.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalInt;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.example.demo.common.EnumPaymentMethod;
import com.example.demo.common.EnumUserRank;
import com.example.demo.controller.ProductOrderController;
import com.example.demo.model.ProductOrderResponce;

import lombok.RequiredArgsConstructor;

/**
 * コンソールから注文処理を実行する
 * テスト時など、product-order.runner.enabled=false で無効化できる。
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "product-order.runner.enabled", havingValue = "true", matchIfMissing = true)
public class ProductOrderRunner implements CommandLineRunner {

    private final ProductOrderController controller;

    @Override
    public void run(String... args) throws IOException {

        // 注文データ（入力順を保持するため LinkedHashMap を使用）
        Map<String, Integer> orderDataMap = new LinkedHashMap<>();
        orderDataMap.put("A001", 1);
        orderDataMap.put("B002", 2);
        orderDataMap.put("C003", 5);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            // 支払方法取得
            EnumPaymentMethod.getPaymentMethod();

            // 支払方法入力
            OptionalInt paymentId = readPaymentId(br);
            if (paymentId.isEmpty()) {
                System.out.println("入力が終了したため、注文を中止します。");
                return;
            }

            // 注文情報投入
            ProductOrderResponce response;
            try {
                response = controller.productOrderController(orderDataMap, paymentId.getAsInt(), EnumUserRank.SILVER);
            } catch (IllegalArgumentException e) {
                System.out.println("注文内容に誤りがあります。" + e.getMessage());
                return;
            }

            // 注文結果出力（未完了の場合は支払方法を再入力させる）
            System.out.println(response.getMessage());
            if (response.isComplete()) {
                return;
            }
        }
    }

    /**
     * 支払方法IDを入力させる 数字が入力されるまで繰り返す
     * @param br 入力元
     * @return 支払方法ID（入力が終了した場合は空）
     * @throws IOException 入力エラー
     */
    private OptionalInt readPaymentId(BufferedReader br) throws IOException {

        while (true) {
            String line = br.readLine();
            if (line == null) {
                return OptionalInt.empty();
            }
            if (line.isBlank()) {
                System.out.println("支払方法を入力してください。");
                continue;
            }
            try {
                return OptionalInt.of(Integer.parseInt(line.trim()));
            } catch (NumberFormatException e) {
                System.out.println("数字を入力してください。");
            }
        }
    }
}
