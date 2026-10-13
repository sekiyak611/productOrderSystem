package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.example.demo.campaign.LargeOrderCampaign;
import com.example.demo.common.EnumUserRank;
import com.example.demo.model.ProductOrderResponce;
import com.example.demo.repository.ProductRepository;

class ProductOrderServiceImplTest {

	private static final int CREDIT_CARD = 1;
	private static final int BANK_TRANSFER = 2;

	private final ProductOrderService service = new ProductOrderServiceImpl(new ProductRepository(),
			List.of(new LargeOrderCampaign()));

	private static Map<String, Integer> order(Object... idAndQuantity) {
		Map<String, Integer> map = new LinkedHashMap<>();
		for (int i = 0; i < idAndQuantity.length; i += 2) {
			map.put((String) idAndQuantity[i], (Integer) idAndQuantity[i + 1]);
		}
		return map;
	}

	@Test
	void 商品割引と会員割引が正しく計算される() {
		// ノートPC 100000×0.1=10000、マウス 3000×2=6000（対象外）、水 100×5=500×0.05=25
		ProductOrderResponce response = service.productOrderService(order("A001", 1, "B002", 2, "C003", 5),
				CREDIT_CARD, EnumUserRank.SILVER);

		assertTrue(response.isComplete());
		assertEquals("""
				ノートPC 100000円 × 1
				マウス 3000円 × 2
				水 100円 × 5

				商品割引後小計：16025円
				会員割引：480円
				送料：0円
				支払金額：15545円

				クレジットカードで15545円を決済しました。""", response.getMessage());
	}

	@Test
	void 同じ注文を繰り返しても結果が変わらない() {
		Map<String, Integer> order = order("A001", 1, "C003", 5);
		String first = service.productOrderService(order, CREDIT_CARD, EnumUserRank.GOLD).getMessage();
		String second = service.productOrderService(order, CREDIT_CARD, EnumUserRank.GOLD).getMessage();

		assertEquals(first, second);
		// 10000 + 25 = 10025、GOLD 5% = 501（切り捨て）→ 9524 + 送料500
		assertTrue(first.contains("商品割引後小計：10025円"));
		assertTrue(first.contains("会員割引：501円"));
		assertTrue(first.contains("支払金額：10024円"));
	}

	@Test
	void 会員割引後10000円未満なら送料500円が加算される() {
		ProductOrderResponce response = service.productOrderService(order("C003", 5), BANK_TRANSFER,
				EnumUserRank.NORMAL);

		assertTrue(response.getMessage().contains("会員割引：0円"));
		assertTrue(response.getMessage().contains("送料：500円"));
		assertTrue(response.getMessage().endsWith("525円の銀行振込を受け付けました。"));
	}

	@Test
	void 注文金額100000円以上でキャンペーン割引が適用される() {
		// マウス 3000×40=120000（対象外）、GOLD 6000 → 114000、キャンペーン 2% 2280 → 111720
		ProductOrderResponce response = service.productOrderService(order("B002", 40), CREDIT_CARD,
				EnumUserRank.GOLD);

		assertTrue(response.getMessage().contains("キャンペーン割引（注文金額100,000円以上で2%割引）：2280円"));
		assertTrue(response.getMessage().contains("支払金額：111720円"));
	}

	@Test
	void 存在しない支払方法は未完了で返る() {
		ProductOrderResponce response = service.productOrderService(order("A001", 1), 9, EnumUserRank.NORMAL);

		assertFalse(response.isComplete());
		assertEquals("存在しない支払方法です。", response.getMessage());
	}

	@Test
	void 不正な注文内容は例外になる() {
		assertThrows(IllegalArgumentException.class,
				() -> service.productOrderService(order("Z999", 1), CREDIT_CARD, EnumUserRank.NORMAL));
		assertThrows(IllegalArgumentException.class,
				() -> service.productOrderService(order("A001", 0), CREDIT_CARD, EnumUserRank.NORMAL));
		assertThrows(IllegalArgumentException.class,
				() -> service.productOrderService(order(), CREDIT_CARD, EnumUserRank.NORMAL));
	}
}
