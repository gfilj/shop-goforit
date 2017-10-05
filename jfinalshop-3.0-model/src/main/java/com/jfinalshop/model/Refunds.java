package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseRefunds;

/**
 * Dao - 退款单
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Refunds extends BaseRefunds<Refunds> {
	public static final Refunds dao = new Refunds();
	
	/**
	 * 方式
	 */
	public enum Method {

		/** 在线支付 */
		online,

		/** 线下支付 */
		offline,

		/** 预存款支付 */
		deposit
	}
	
	/** 退货项 */
	private List<ReturnsItem> returnsItems = new ArrayList<ReturnsItem>();
	
	/**
	 * 获取方式
	 * 
	 * @return 方式
	 */
	public Method getMethodValues() {
		return Method.values()[getMethod()];
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
	 * 获取退货项
	 * 
	 * @return 退货项
	 */
	public List<ReturnsItem> getReturnsItems() {
		String sql = "SELECT * FROM `returns_item` WHERE returns_id = ?";
		if (CollectionUtils.isEmpty(returnsItems)) {
			returnsItems = ReturnsItem.dao.find(sql, getId());
		}
		return returnsItems;
	}

	/**
	 * 设置退货项
	 * 
	 * @param returnsItems
	 *            退货项
	 */
	public void setReturnsItems(List<ReturnsItem> returnsItems) {
		this.returnsItems = returnsItems;
	}
}
