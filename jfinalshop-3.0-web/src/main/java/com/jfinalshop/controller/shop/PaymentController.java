/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.jfinalshop.common.Setting;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.Order.PaymentStatus;
import com.jfinalshop.model.Payment;
import com.jfinalshop.model.Payment.Method;
import com.jfinalshop.model.Payment.Status;
import com.jfinalshop.model.Payment.Type;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.model.Sn;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.plugin.PaymentPlugin.NotifyMethod;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.PaymentService;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.service.SnService;
import com.jfinalshop.utils.SettingUtils;



/**
 * Controller - 支付
 * 
 * 
 * 
 */
public class PaymentController extends BaseShopController {

	private OrderService orderService = enhance(OrderService.class);
	private MemberService memberService = enhance(MemberService.class);
	private PluginService pluginService = enhance(PluginService.class);
	private PaymentService paymentService = enhance(PaymentService.class);
	private SnService snService = enhance(SnService.class);

	/**
	 * 提交
	 */
	public void submit() {
		String paymentPluginId = getPara("paymentPluginId");
		String sn = getPara("sn");
		BigDecimal amount = new BigDecimal(getPara("amount", "0"));
		Type type = getPara("type") != null ? Type.valueOf(getPara("type")) : null;
		
		Member member = memberService.getCurrent(getRequest());
		if (member == null) {
			renderJson(ERROR_VIEW);
			return;
		}
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		if (paymentPlugin == null || !paymentPlugin.getIsEnabled()) {
			renderJson(ERROR_VIEW);
			return;
		}
		Payment payment = new Payment();
		String description = null;
		if (type == Type.payment) {
			Order order = orderService.findBySn(sn);
			if (order == null || !member.equals(order.getMember()) || order.isExpired() || order.isLocked(null)) {
				renderJson(ERROR_VIEW);
				return;
			}
			if (order.getPaymentMethod() == null || order.getPaymentMethod().getMethod() != PaymentMethod.Method.online.ordinal()) {
				renderJson(ERROR_VIEW);
				return;
			}
			if (order.getPaymentStatus() != PaymentStatus.unpaid.ordinal() && order.getPaymentStatus() != PaymentStatus.partialPayment.ordinal()) {
				renderJson(ERROR_VIEW);
				return;
			}
			if (order.getAmountPayable().compareTo(new BigDecimal(0)) <= 0) {
				renderJson(ERROR_VIEW);
				return;
			}
			payment.setSn(snService.generate(Sn.Type.payment));
			payment.setType(Type.payment.ordinal());
			payment.setMethod(Method.online.ordinal());
			payment.setStatus(Status.wait.ordinal());
			payment.setPaymentMethod(order.getPaymentMethodName() + Payment.PAYMENT_METHOD_SEPARATOR + paymentPlugin.getPaymentName());
			payment.setFee(paymentPlugin.calculateFee(order.getAmountPayable()));
			payment.setAmount(paymentPlugin.calculateAmount(order.getAmountPayable()));
			payment.setPaymentPluginId(paymentPluginId);
			payment.setExpire(paymentPlugin.getTimeout() != null ? DateUtils.addMinutes(new Date(), paymentPlugin.getTimeout()) : null);
			payment.setOrderId(order.getId());
			payment.setPayer(member.getUsername());
			payment.setPaymentDate(new Date());
			payment.setCreateBy(member.getUsername());
			paymentService.save(payment);
			description = order.getName();
		} else if (type == Type.recharge) {
			Setting setting = SettingUtils.get();
			if (amount == null || amount.compareTo(new BigDecimal(0)) <= 0 || amount.precision() > 15 || amount.scale() > setting.getPriceScale()) {
				renderJson(ERROR_VIEW);
				return;
			}
			payment.setSn(snService.generate(Sn.Type.payment));
			payment.setType(Type.recharge.ordinal());
			payment.setMethod(Method.online.ordinal());
			payment.setStatus(Status.wait.ordinal());
			payment.setPaymentMethod(paymentPlugin.getPaymentName());
			payment.setFee(paymentPlugin.calculateFee(amount));
			payment.setAmount(paymentPlugin.calculateAmount(amount));
			payment.setPaymentPluginId(paymentPluginId);
			payment.setExpire(paymentPlugin.getTimeout() != null ? DateUtils.addMinutes(new Date(), paymentPlugin.getTimeout()) : null);
			payment.setMemberId(member.getId());
			paymentService.save(payment);
			description = message("shop.member.deposit.recharge");
		} else {
			renderJson(ERROR_VIEW);
			return;
		}
		setAttr("requestUrl", paymentPlugin.getRequestUrl());
		setAttr("requestMethod", paymentPlugin.getRequestMethod());
		setAttr("requestCharset", paymentPlugin.getRequestCharset());
		setAttr("parameterMap", paymentPlugin.getParameterMap(payment.getSn(), description, getRequest()));
		if (StringUtils.isNotEmpty(paymentPlugin.getRequestCharset())) {
			getResponse().setContentType("text/html; charset=" + paymentPlugin.getRequestCharset());
		}
		render("/shop/payment/submit.html");
	}

	/**
	 * 通知
	 */
	//@RequestMapping("/notify/{notifyMethod}/{sn}")
	public void paymentNotify() {
		String sn = getPara("sn");
		NotifyMethod notifyMethod = getBean(NotifyMethod.class);
		Payment payment = paymentService.findBySn(sn);
		if (payment != null) {
			PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(payment.getPaymentPluginId());
			if (paymentPlugin != null) {
				if (paymentPlugin.verifyNotify(sn, notifyMethod, getRequest())) {
					paymentService.handle(payment);
				}
				setAttr("notifyMessage", paymentPlugin.getNotifyMessage(sn, notifyMethod, getRequest()));
			}
			setAttr("payment", payment);
		}
		render("/shop/payment/notify.html");
	}

}