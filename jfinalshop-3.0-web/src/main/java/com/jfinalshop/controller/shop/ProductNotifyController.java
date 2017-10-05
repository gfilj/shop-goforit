/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop;

import java.util.HashMap;
import java.util.Map;

import com.jfinalshop.common.Message;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductNotify;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ProductNotifyService;
import com.jfinalshop.service.ProductService;

/**
 * Controller - 到货通知
 * 
 * 
 * 
 */
public class ProductNotifyController extends BaseShopController {

	private ProductNotifyService productNotifyService = enhance(ProductNotifyService.class);
	private MemberService memberService = enhance(MemberService.class);
	private ProductService productService = enhance(ProductService.class);

	/**
	 * 获取当前会员E-mail
	 */
	public void email() {
		Member member = memberService.getCurrent(getRequest());
		String email = member != null ? member.getEmail() : null;
		Map<String, String> data = new HashMap<String, String>();
		data.put("email", email);
		renderJson(data);
	}

	/**
	 * 保存
	 */
	public void save() {
		String email = getPara("email");
		Long productId = getParaToLong("productId");
		Map<String, Object> data = new HashMap<String, Object>();
		Product product = productService.find(productId);
		if (product == null) {
			data.put("message", Message.warn("shop.productNotify.productNotExist"));
			renderJson(data); 
			return;
		}
		if (!product.getIsMarketable()) {
			data.put("message", Message.warn("shop.productNotify.productNotMarketable"));
			renderJson(data); 
			return;
		}
		if (!product.getIsOutOfStock()) {
			data.put("message", Message.warn("shop.productNotify.productInStock"));
		}
		if (productNotifyService.exists(product, email)) {
			data.put("message", Message.warn("shop.productNotify.exist"));
		} else {
			ProductNotify productNotify = new ProductNotify();
			productNotify.setEmail(email);
			productNotify.setHasSent(false);
			productNotify.setMemberId(memberService.getCurrent(getRequest()).getId());
			productNotify.setProductId(product.getId());
			productNotifyService.save(productNotify);
			data.put("message", SUCCESS_MESSAGE);
		}
		renderJson(data); 
	}

}