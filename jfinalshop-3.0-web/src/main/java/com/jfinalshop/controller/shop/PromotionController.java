/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop;

import com.jfinalshop.common.ResourceNotFoundException;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.service.PromotionService;

/**
 * Controller - 促销
 * 
 * 
 * 
 */
public class PromotionController extends BaseShopController {

	private PromotionService promotionService = enhance(PromotionService.class);

	/**
	 * 内容
	 */
	public void content() {
		Long id = getParaToLong("id");
		Promotion promotion = promotionService.find(id);
		if (promotion == null) {
			throw new ResourceNotFoundException();
		}
		setAttr("promotion", promotion);
		render("/shop/promotion/content.html");
	}

}