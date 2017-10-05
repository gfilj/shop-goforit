package com.jfinalshop.model;

import java.math.BigDecimal;
import java.util.Date;

import com.jfinalshop.model.base.BaseOrderItem;

/**
 * Dao - 订单项
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class OrderItem extends BaseOrderItem<OrderItem> {
	public static final OrderItem dao = new OrderItem();
	
	/**
	 * 保存
	 */
	public boolean save(OrderItem orderItem) {
		orderItem.setCreationDate(new Date());
		orderItem.setDeleteFlag(false);
		return orderItem.save();
	}
	
	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public Product getProduct() {
		return Product.dao.findById(getProductId());
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
	 * 获取商品总重量
	 * 
	 * @return 商品总重量
	 */
	public int getTotalWeight() {
		if (getWeight() != null && getQuantity() != null) {
			return getWeight() * getQuantity();
		} else {
			return 0;
		}
	}

	/**
	 * 获取小计
	 * 
	 * @return 小计
	 */
	public BigDecimal getSubtotal() {
		if (getPrice() != null && getQuantity() != null) {
			return getPrice().multiply(new BigDecimal(getQuantity()));
		} else {
			return new BigDecimal(0);
		}
	}
}
