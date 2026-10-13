package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// コンソール入力待ちでテストが止まらないよう、Runner を無効化する
@SpringBootTest(properties = "product-order.runner.enabled=false")
class ProductOrderSystemApplicationTests {

	@Test
	void contextLoads() {
	}

}
