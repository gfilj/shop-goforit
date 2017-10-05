package com.jfinalshop.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.base.BaseCoupon;
import com.jfinalshop.utils.DateUtil;
import com.jfinalshop.utils.FreemarkerUtils;

import freemarker.template.TemplateException;

/**
 * Dao - 优惠券
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Coupon extends BaseCoupon<Coupon> {
	public static final Coupon dao = new Coupon();
	
	/** 优惠码 */
	private List<CouponCode> couponCodes = new ArrayList<CouponCode>();

	/** 促销 */
	private List<Promotion> promotions = new ArrayList<Promotion>();

	/** 订单 */
	private List<Order> orders = new ArrayList<Order>();
	
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
		String select = " SELECT * ";
		String sqlExceptSelect = " FROM coupon WHERE 1 = 1 ";
		if (isEnabled != null) {
			sqlExceptSelect += " AND is_enabled = " + isEnabled;
		}
		if (isExchange != null) {
			sqlExceptSelect += " AND is_exchange = " + isExchange;
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sqlExceptSelect += " AND end_date IS NOT NULL AND end_date < '" + DateUtil.getNowTime() + "'";
			} else {
				sqlExceptSelect += " AND (end_date IS  NULL OR end_date >= '" + DateUtil.getNowTime() + "')";
			}
		}
		Page<Coupon> coupons = paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect);
		return coupons;
	}
	
	
	
	/**
	 * 获取优惠码
	 * 
	 * @return 优惠码
	 */
	public List<CouponCode> getCouponCodes() {
		String sql = "SELECT * FROM coupon_code c WHERE c.`coupon_id` = ?";
		if (couponCodes.isEmpty()) {
			couponCodes = CouponCode.dao.find(sql, getId());
		}
		return couponCodes;
	}
	
	/**
	 * 设置优惠码
	 * 
	 * @param couponCodes
	 *            优惠码
	 */
	public void setCouponCodes(List<CouponCode> couponCodes) {
		this.couponCodes = couponCodes;
	}
	
	/**
	 * 获取促销
	 * 
	 * @return 促销
	 */
	public List<Promotion> getPromotions() {
		String sql = "SELECT p.* FROM promotion_coupon pc INNER JOIN promotion p ON pc.`promotions` = p.`id` WHERE pc.`coupons` = ?";
		if (promotions.isEmpty()) {
			promotions = Promotion.dao.find(sql, getId());
		}
		return promotions;
	}
	
	/**
	 * 设置促销
	 * 
	 * @param promotions
	 *            促销
	 */
	public void setPromotions(List<Promotion> promotions) {
		this.promotions = promotions;
	}
	
	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public List<Order> getOrders() {
		String sql = "SELECT o.*  FROM order_coupon oc  INNER JOIN `order` o ON oc.`orders` = o.`id` WHERE oc.`coupons` = ?";
		if (orders.isEmpty()) {
			orders = Order.dao.find(sql, getId());
		}
		return orders;
	}
	
	/**
	 * 设置订单
	 * 
	 * @param orders
	 *            订单
	 */
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
	
	/**
	 * 判断是否已开始
	 * 
	 * @return 是否已开始
	 */
	public boolean hasBegun() {
		return getBeginDate() == null || new Date().after(getBeginDate());
	}

	/**
	 * 判断是否已过期
	 * 
	 * @return 是否已过期
	 */
	public boolean hasExpired() {
		return getEndDate() != null && new Date().after(getEndDate());
	}
	
	/**
	 * 计算优惠价格
	 * 
	 * @param quantity
	 *            商品数量
	 * @param price
	 *            商品价格
	 * @return 优惠价格
	 */
	public BigDecimal calculatePrice(Integer quantity, BigDecimal price) {
		if (price == null || StringUtils.isEmpty(getPriceExpression())) {
			return price;
		}
		BigDecimal result = new BigDecimal(0);
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("quantity", quantity);
			model.put("price", price);
			result = new BigDecimal(FreemarkerUtils.process("#{(" + getPriceExpression() + ");M50}", model));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
		if (result.compareTo(price) > 0) {
			return price;
		}
		return result.compareTo(new BigDecimal(0)) > 0 ? result : new BigDecimal(0);
	}
}
