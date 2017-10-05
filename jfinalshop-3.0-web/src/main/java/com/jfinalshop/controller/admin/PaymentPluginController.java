package com.jfinalshop.controller.admin;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.service.PluginService;

/**
 * Controller - 支付插件
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/paymentPlugin")
public class PaymentPluginController extends BaseAdminController {

	/**
	 * 列表
	 */
	public void list() {
		setAttr("paymentPlugins", PluginService.service.getPaymentPlugins());
		render("/admin/payment_plugin/list.html");
	}
}
