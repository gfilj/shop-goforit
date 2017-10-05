package com.jfinalshop.service;

import java.util.Date;

import com.jfinal.aop.Enhancer;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.Payment;
import com.jfinalshop.model.Payment.Status;
import com.jfinalshop.model.Payment.Type;

/**
 * Service - 收款单
 * 
 * 
 * 
 */
public class PaymentService extends BaseService<Payment> {
	
	public PaymentService() {
		super(Payment.class);
	}

	private OrderService orderService = Enhancer.enhance(OrderService.class);
	private MemberService memberService = Enhancer.enhance(MemberService.class);

	/**
	 * 根据编号查找收款单
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 收款单，若不存在则返回null
	 */
	public Payment findBySn(String sn) {
		return Payment.dao.findBySn(sn);
	}

	/**
	 * 支付处理
	 * 
	 * @param payment
	 *            收款单
	 */
	public void handle(Payment payment) {
		if (payment != null && payment.getStatus() == Status.wait.ordinal()) {
			if (payment.getType() == Type.payment.ordinal()) {
				Order order = payment.getOrder();
				if (order != null) {
					orderService.payment(order, payment, null);
				}
			} else if (payment.getType() == Type.recharge.ordinal()) {
				Member member = payment.getMember();
				if (member != null) {
					memberService.update(member, null, payment.getEffectiveAmount(), null, null);
				}
			}
			payment.setStatus(Status.success.ordinal());
			payment.setPaymentDate(new Date());
			payment.update();
		}
	}
	
	/**
	 * 保存
	 * 
	 */
	public boolean save(Payment payment) {
		payment.setCreationDate(getSysDate());
		payment.setDeleteFlag(false);
		return payment.save();
	}

}
