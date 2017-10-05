package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseReturns;

/**
 * Dao - 退货单
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Returns extends BaseReturns<Returns> {
	public static final Returns dao = new Returns();
	
	/** 退货项 */
	private List<ReturnsItem> returnsItems = new ArrayList<ReturnsItem>();
	
	
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
		String sql = "SELECT * FROM returns_item WHERE returns_id = ?";
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
	
	/**
	 * 获取数量
	 * 
	 * @return 数量
	 */
	public int getQuantity() {
		int quantity = 0;
		if (getReturnsItems() != null) {
			for (ReturnsItem returnsItem : getReturnsItems()) {
				if (returnsItem != null && returnsItem.getQuantity() != null) {
					quantity += returnsItem.getQuantity();
				}
			}
		}
		return quantity;
	}
}
