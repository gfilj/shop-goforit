package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BasePaymentShippingMethod;

/**
 * Dao - 支付物流方式
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class PaymentShippingMethod extends BasePaymentShippingMethod<PaymentShippingMethod> {
	public static final PaymentShippingMethod dao = new PaymentShippingMethod();
	
	/**
	 * 检测是否已存在
	 * @return
	 */
	public boolean isNull(Long adminId, Long roleId) {
		String sql = "SELECT COUNT(*) count FROM payment_shipping_method `psm` WHERE `psm`.payment_methods = ? AND `psm`.shipping_methods = ?";
		return Db.queryLong(sql, adminId, roleId) == 0L;
	}
	
	/**
	 * 删除
	 * @return
	 */
	public boolean deletePaymentMethods(Long payment_methods) {
		return Db.deleteById("payment_shipping_method", "payment_methods", payment_methods);
	}
}
