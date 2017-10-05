package com.jfinalshop.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.common.Setting;
import com.jfinalshop.model.base.BaseShippingMethod;
import com.jfinalshop.utils.SettingUtils;

/**
 * Dao - 配送方式
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class ShippingMethod extends BaseShippingMethod<ShippingMethod> {
	public static final ShippingMethod dao = new ShippingMethod();

	/** 支付方式 */
	private List<PaymentMethod> paymentMethods = new ArrayList<PaymentMethod>();

	/** 订单 */
	private List<Order> orders = new ArrayList<Order>();
	
	/**
	 * 获取支付方式
	 * 
	 * @return 支付方式
	 */
	public List<PaymentMethod> getPaymentMethods() {
		String sql = "SELECT pm.* FROM `payment_shipping_method` psm LEFT JOIN `payment_method` pm ON psm.`payment_methods` = pm.`id` WHERE psm.`shipping_methods` = ?";
		if (CollectionUtils.isEmpty(paymentMethods)) {
			paymentMethods = PaymentMethod.dao.find(sql, getId());
		}
		return paymentMethods;
	}

	/**
	 * 设置支付方式
	 * 
	 * @param paymentMethods
	 *            支付方式
	 */
	public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public List<Order> getOrder() {
		String sql = "SELECT * FROM `order` WHERE shipping_method_id = ?";
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

	/**
	 * 计算运费
	 * 
	 * @param weight
	 *            重量
	 * @return 运费
	 */
	public BigDecimal calculateFreight(Integer weight) {
		Setting setting = SettingUtils.get();
		BigDecimal freight = new BigDecimal(0);
		if (weight != null) {
			if (weight <= getFirstWeight() || getContinuePrice().compareTo(new BigDecimal(0)) == 0) {
				freight = getFirstPrice();
			} else {
				double contiuneWeightCount = Math.ceil((weight - getFirstWeight()) / (double) getContinueWeight());
				freight = getFirstPrice().add(getContinuePrice().multiply(new BigDecimal(contiuneWeightCount)));
			}
		}
		return setting.setScale(freight);
	}
	

	public boolean exists(String shippingMethodName) {
		String sql = "SELECT COUNT(*) FROM shipping_method WHERE LOWER(name) = ?";
		if (StrKit.notBlank(shippingMethodName)) {
			return Db.queryLong(sql, shippingMethodName) > 0;
		}
		return false;
	}
	
}
