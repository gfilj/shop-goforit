package com.jfinalshop.service;

import com.jfinalshop.model.ArticleTag;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.model.PaymentShippingMethod;
import com.jfinalshop.model.ShippingMethod;

public class PaymentShippingMethodService extends BaseService<ArticleTag> {
	public static final PaymentShippingMethodService service = new PaymentShippingMethodService();
	
	public PaymentShippingMethodService() {
		super(ArticleTag.class);
	}
	
	/**
	 * 保存
	 * 
	 * 
	 */
	public boolean save(PaymentMethod paymentMethod) {
		boolean result = false;
		if (!paymentMethod.getShippingMethods().isEmpty()) {
			for (ShippingMethod shippingMethod : paymentMethod.getShippingMethods()) {
				if (PaymentShippingMethod.dao.isNull(paymentMethod.getId(), shippingMethod.getId())) {
					PaymentShippingMethod paymentShippingMethod = new PaymentShippingMethod();
					paymentShippingMethod.setPaymentMethods(paymentMethod.getId());
					paymentShippingMethod.setShippingMethods(shippingMethod.getId());
					paymentShippingMethod.save();
				}
			}
		}
		return result;
	}
	
	/**
	 * 更新
	 * 
	 * 
	 */
	public boolean update(PaymentMethod paymentMethod) {
		boolean result = false;
		if (!paymentMethod.getShippingMethods().isEmpty()) {
			PaymentShippingMethod.dao.deletePaymentMethods(paymentMethod.getId());
			for (ShippingMethod shippingMethod : paymentMethod.getShippingMethods()) {
				if (PaymentShippingMethod.dao.isNull(paymentMethod.getId(), shippingMethod.getId())) {
					PaymentShippingMethod paymentShippingMethod = new PaymentShippingMethod();
					paymentShippingMethod.setPaymentMethods(paymentMethod.getId());
					paymentShippingMethod.setShippingMethods(shippingMethod.getId());
					paymentShippingMethod.save();
				}
			}
		}
		return result;
	}
}
