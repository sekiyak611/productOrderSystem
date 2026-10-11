package com.example.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.example.demo.common.EnumPaymentMethod;
import com.example.demo.controller.ProductOrderController;
import com.example.demo.model.ProductOrderResponce;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
public class ProductOrderSystemApplication implements CommandLineRunner {

    @Autowired
    private ApplicationContext context;
    private final ProductOrderController controller;

    public static void main(String[] args) {
        SpringApplication.run(ProductOrderSystemApplication.class, args);
    }

    public void run(String... args) throws Exception {

        Map<String, Integer> orderDataMap = new HashMap<>();
        orderDataMap.put("A001", 1);
        orderDataMap.put("B002", 2);
        orderDataMap.put("C003", 5);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            // 支払方法取得
            EnumPaymentMethod.getPaymentMethod();
            int paymentId;

            while (true) {
                try {
                    // 支払方法入力
                    paymentId = Integer.parseInt(br.readLine());
                } catch (NumberFormatException e) {
                    System.out.println("数字を入力してください。");
                    continue;
                } catch (NullPointerException e) {
                    System.out.println("支払方法を入力してください。");
                    continue;
                }
                break;
            }

            // 注文情報投入
            ProductOrderResponce response = controller.productOrderController(orderDataMap, paymentId);

            System.out.println(response);

            if (response.isComplete()) {
                break;
            }
        }

        // アプリケーションを終了させる
        int exitCode = SpringApplication.exit(context, () -> 0);
        System.exit(exitCode);
    }

}
