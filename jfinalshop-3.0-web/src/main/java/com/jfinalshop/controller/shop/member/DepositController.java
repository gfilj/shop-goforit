/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop.member;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.controller.shop.BaseShopController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.service.DepositService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.PluginService;

/**
 * Controller - 会员中心 - 预存款
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/deposit")
@Before(MemberInterceptor.class)
public class DepositController extends BaseShopController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	private MemberService memberService = enhance(MemberService.class);
	private DepositService depositService = enhance(DepositService.class);
	private PluginService pluginService = new PluginService();

	/**
	 * 计算支付手续费
	 */
	public void calculateFee() {
		String paymentPluginId = getPara("paymentPluginId");
		BigDecimal amount = new BigDecimal(getParaToLong("amount"));
		Map<String, Object> data = new HashMap<String, Object>();
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		if (paymentPlugin == null || !paymentPlugin.getIsEnabled() || amount == null || amount.compareTo(new BigDecimal(0)) < 0) {
			data.put("message", ERROR_MESSAGE);
			renderJson(data);
			return;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("fee", paymentPlugin.calculateFee(amount));
		renderJson(data);
	}

	/**
	 * 检查余额
	 */
	public void checkBalance() {
		Map<String, Object> data = new HashMap<String, Object>();
		Member member = memberService.getCurrent(getRequest());
		data.put("balance", member.getBalance());
		renderJson(data);
	}

	/**
	 * 充值
	 */
	public void recharge() {
		List<PaymentPlugin> paymentPlugins = pluginService.getPaymentPlugins(true);
		if (!paymentPlugins.isEmpty()) {
			setAttr("defaultPaymentPlugin", paymentPlugins.get(0));
			setAttr("paymentPlugins", paymentPlugins);
		}
		render("/shop/member/deposit/recharge.html");
	}

	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent(getRequest());
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", depositService.findPage(member, pageable));
		render("/shop/member/deposit/list.html");
	}

}