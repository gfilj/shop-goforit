package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseShipping;

/**
 * Dao - 发货单
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Shipping extends BaseShipping<Shipping> {
	public static final Shipping dao = new Shipping();
	
	/** 发货项 */
	private List<ShippingItem> shippingItems = new ArrayList<ShippingItem>();
	
	/**
	 * 根据编号查找发货单
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 若不存在则返回null
	 */
	public Shipping findBySn(String sn) {
		if (sn == null) {
			return null;
		}
		String sql = "SELECT * FROM shipping WHERE LOWER(sn) = LOWER(?)";
		try {
			return findFirst(sql, sn);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public Order getOrder() {
		return Order.dao.findById(getOrderId());
	}
	
	
	/**
	 * 获取发货项
	 * 
	 * @return 发货项
	 */
	public List<ShippingItem> getShippingItems() {
		String sql = "SELECT * FROM shipping_item WHERE shipping_id = ?";
		if (CollectionUtils.isEmpty(shippingItems)) {
			shippingItems = ShippingItem.dao.find(sql, getId());
		}
		return shippingItems;
	}
	
	/**
	 * 设置发货项
	 * 
	 * @param shippingItems
	 *            发货项
	 */
	public void setShippingItems(List<ShippingItem> shippingItems) {
		this.shippingItems = shippingItems;
	}

	
	/**
	 * 获取数量
	 * 
	 * @return 数量
	 */
	public int getQuantity() {
		int quantity = 0;
		if (getShippingItems() != null) {
			for (ShippingItem shippingItem : getShippingItems()) {
				if (shippingItem != null && shippingItem.getQuantity() != null) {
					quantity += shippingItem.getQuantity();
				}
			}
		}
		return quantity;
	}
}
