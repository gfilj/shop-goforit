package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BasePromotionProduct;

/**
 * Dao - 促销产品
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class PromotionProduct extends BasePromotionProduct<PromotionProduct> {
	public static final PromotionProduct dao = new PromotionProduct();
	

	/**
	 * 根据促销ID删除
	 * @param adminId
	 * @return
	 */
	public boolean delete(Long promotions) {
		return Db.deleteById("promotion_product", "promotions", promotions);
	}
}
