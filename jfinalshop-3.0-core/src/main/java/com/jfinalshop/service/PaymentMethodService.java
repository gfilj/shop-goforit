package com.jfinalshop.service;

import java.util.Date;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.model.PaymentShippingMethod;
import com.jfinalshop.security.ShiroUtil;

/**
 * Service - 支付方式
 * 
 * 
 * 
 */
public class PaymentMethodService extends BaseService<PaymentMethod> {

	public PaymentMethodService() {
		super(PaymentMethod.class);
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public boolean save(PaymentMethod paymentMethod) {
		boolean result = false;
		paymentMethod.setCreateBy(ShiroUtil.getName());
		paymentMethod.setCreationDate(new Date());
		paymentMethod.setDeleteFlag(false);
		result = paymentMethod.save();
		PaymentShippingMethodService.service.save(paymentMethod);
		return result;
	}
	
	/**
	 * 更新
	 */
	@Before(Tx.class)
	public boolean update(PaymentMethod paymentMethod) {
		boolean result = false;
		paymentMethod.setLastUpdatedBy(ShiroUtil.getName());
		paymentMethod.setLastUpdatedDate(new Date());
		paymentMethod.setDeleteFlag(false);
		result = paymentMethod.update();
		PaymentShippingMethodService.service.update(paymentMethod);
		return result;
	}
	
	/**
	 * 删除
	 * 
	 * @param ids
	 * @return
	 */
	@Before(Tx.class)
	public boolean delete(Long[] ids) {
		boolean result = false;
		for (Long id : ids) {
			PaymentShippingMethod.dao.deletePaymentMethods(id);
			result = PaymentMethod.dao.deleteById(id);
		}
		return result;
	}
	
}
