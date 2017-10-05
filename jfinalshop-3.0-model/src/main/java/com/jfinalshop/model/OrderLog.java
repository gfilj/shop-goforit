package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseOrderLog;

/**
 * Dao - 订单日志
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class OrderLog extends BaseOrderLog<OrderLog> {
	public static final OrderLog dao = new OrderLog();
	
	/**
	 * 类型
	 */
	public enum Type {

		/** 订单创建 */
		create,

		/** 订单修改 */
		modify,

		/** 订单确认 */
		confirm,

		/** 订单支付 */
		payment,

		/** 订单退款 */
		refunds,

		/** 订单发货 */
		shipping,

		/** 订单退货 */
		returns,

		/** 订单完成 */
		complete,

		/** 订单取消 */
		cancel,

		/** 其它 */
		other
	};
	
	/**
	 * 类型
	 */
	public Type getTypeValues() {
		return Type.values()[getType()];
	}
	
	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public Order getOrder() {
		return Order.dao.findById(getOrderId());
	}
}
