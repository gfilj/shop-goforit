package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BasePromotionBrand;

/**
 * Dao - 促销品牌
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class PromotionBrand extends BasePromotionBrand<PromotionBrand> {
	public static final PromotionBrand dao = new PromotionBrand();
	
	/**
	 * 根据促销ID删除
	 * @param adminId
	 * @return
	 */
	public boolean delete(Long promotions) {
		return Db.deleteById("promotion_brand", "promotions", promotions);
	}
}
