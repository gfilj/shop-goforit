package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BasePromotionProductCategory;

/**
 * Dao - 促销产品分类
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class PromotionProductCategory extends BasePromotionProductCategory<PromotionProductCategory> {
	public static final PromotionProductCategory dao = new PromotionProductCategory();
	
	/**
	 * 根据促销ID删除
	 * @param adminId
	 * @return
	 */
	public boolean delete(Long promotions) {
		return Db.deleteById("promotion_product_category", "promotions", promotions);
	}
}
