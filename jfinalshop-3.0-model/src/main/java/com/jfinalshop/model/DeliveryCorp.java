package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import com.jfinalshop.model.base.BaseDeliveryCorp;

/**
 * Dao - 物流公司
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class DeliveryCorp extends BaseDeliveryCorp<DeliveryCorp> {
	
	public static final DeliveryCorp dao = new DeliveryCorp();
	
	/** 配送方式 */
	private List<ShippingMethod> shippingMethods = new ArrayList<ShippingMethod>();
	
	/**
	 * 获取配送方式
	 * 
	 * @return 配送方式
	 */
	public List<ShippingMethod> getShippingMethods() {
		String sql = "SELECT * FROM shipping_method WHERE `default_delivery_corp_id` = ?";
		if (shippingMethods.isEmpty()) {
			shippingMethods = ShippingMethod.dao.find(sql, getId());
		}
		return shippingMethods;
	}

	/**
	 * 设置配送方式
	 * 
	 * @param shippingMethods
	 *            配送方式
	 */
	public void setShippingMethods(List<ShippingMethod> shippingMethods) {
		this.shippingMethods = shippingMethods;
	}
	
}
