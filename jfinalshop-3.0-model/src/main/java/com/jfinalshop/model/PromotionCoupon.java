package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BasePromotionCoupon;

/**
 * Dao - 促销优惠券
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class PromotionCoupon extends BasePromotionCoupon<PromotionCoupon> {
	public static final PromotionCoupon dao = new PromotionCoupon();
	
	/**
	 * 根据促销ID删除
	 * @param adminId
	 * @return
	 */
	public boolean delete(Long promotions) {
		return Db.deleteById("promotion_coupon", "promotions", promotions);
	}
}
