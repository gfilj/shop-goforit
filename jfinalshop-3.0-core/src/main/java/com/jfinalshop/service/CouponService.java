package com.jfinalshop.service;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Coupon;

/**
 * Service - 优惠券
 * 
 * 
 * 
 */
public class CouponService extends BaseService<Coupon>{

	public CouponService() {
		super(Coupon.class);
	}
	
	/**
	 * 查找优惠券分页
	 * 
	 * @param isEnabled
	 *            是否启用
	 * @param isExchange
	 *            是否允许积分兑换
	 * @param hasExpired
	 *            是否已过期
	 * @param pageable
	 *            分页信息
	 * @return 优惠券分页
	 */
	public Page<Coupon> findPage(Boolean isEnabled, Boolean isExchange, Boolean hasExpired, Pageable pageable) {
		return Coupon.dao.findPage(isEnabled, isExchange, hasExpired, pageable);
	}
	
}
