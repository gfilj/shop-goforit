package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BasePaymentMethod;

/**
 * Dao - 支付方式
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class PaymentMethod extends BasePaymentMethod<PaymentMethod> {
	public static final PaymentMethod dao = new PaymentMethod();
	
	/**
	 * 方式
	 */
	public enum Method {

		/** 在线支付 */
		online,

		/** 线下支付 */
		offline
	};
	
	/** 支持配送方式 */
	private List<ShippingMethod> shippingMethods = new ArrayList<ShippingMethod>();

	/** 订单 */
	private List<Order> orders = new ArrayList<Order>();
	
	/**
	 * 支付方式
	 * 
	 */
	public Method getMethodValues() {
		return Method.values()[getMethod()];
	}
	
	/**
	 * 获取支持配送方式
	 * 
	 * @return 支持配送方式
	 */
	public List<ShippingMethod> getShippingMethods() {
		String sql = "SELECT sm.* FROM `payment_shipping_method` psm LEFT JOIN `shipping_method` sm ON psm.`shipping_methods` = sm.`id` WHERE psm.`payment_methods` = ?";
		if (CollectionUtils.isEmpty(shippingMethods)) {
			shippingMethods = ShippingMethod.dao.find(sql, getId());
		}
		return shippingMethods;
	}
	
	/**
	 * 设置支持配送方式
	 * 
	 * @param shippingMethods
	 *            支持配送方式
	 */
	public void setShippingMethods(List<ShippingMethod> shippingMethods) {
		this.shippingMethods = shippingMethods;
	}
	
	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public List<Order> getOrder() {
		String sql = "SELECT * FROM `order` WHERE `payment_method_id` = ?";
		if (CollectionUtils.isEmpty(orders)) {
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
	public void setOrder(List<Order> orders) {
		this.orders = orders;
	}
	
}
