package com.jfinalshop.model;

import java.math.BigDecimal;
import java.util.Date;

import com.jfinalshop.model.base.BasePayment;

/**
 * Dao - 收款单
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Payment extends BasePayment<Payment> {
	public static final Payment dao = new Payment();
	
	/** 支付方式分隔符 */
	public static final String PAYMENT_METHOD_SEPARATOR = " - ";

	/**
	 * 类型
	 */
	public enum Type {

		/** 订单支付 */
		payment,

		/** 预存款充值 */
		recharge
	}

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

	/**
	 * 状态
	 */
	public enum Status {

		/** 等待支付 */
		wait,

		/** 支付成功 */
		success,

		/** 支付失败 */
		failure
	}
	
	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public Type getTypeValues() {
		return Type.values()[getType()];
	}
	
	/**
	 * 获取方式
	 * 
	 * @return 方式
	 */
	public Method getMethodValues() {
		return Method.values()[getMethod()];
	}
	
	/**
	 * 获取状态
	 * 
	 * @return 状态
	 */
	public Status getStatusValues() {
		return Status.values()[getStatus()];
	}
	
	
	/**
	 * 根据编号查找收款单
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 收款单，若不存在则返回null
	 */
	public Payment findBySn(String sn) {
		if (sn == null) {
			return null;
		}
		String sql = "select * from payment payment where lower(payment.sn) = lower(?)";
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
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public Member getMember() {
		return Member.dao.findById(getMemberId());
	}
	
	/**
	 * 获取有效金额
	 * 
	 * @return 有效金额
	 */
	public BigDecimal getEffectiveAmount() {
		BigDecimal effectiveAmount = getAmount().subtract(getFee());
		return effectiveAmount.compareTo(new BigDecimal(0)) > 0 ? effectiveAmount : new BigDecimal(0);
	}
	
	/**
	 * 判断是否已过期
	 * 
	 * @return 是否已过期
	 */
	public boolean hasExpired() {
		return getExpire() != null && new Date().after(getExpire());
	}
}
