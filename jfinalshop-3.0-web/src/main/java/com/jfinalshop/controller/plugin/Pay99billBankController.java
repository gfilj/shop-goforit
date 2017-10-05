/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.plugin;

import java.math.BigDecimal;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.controller.admin.BaseAdminController;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.Pay99billBankPlugin;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.plugin.PaymentPlugin.FeeType;
import com.jfinalshop.service.PluginConfigService;

/**
 * Controller - 快钱支付(网银直连)
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/paymentPlugin/pay99billBank")
public class Pay99billBankController extends BaseAdminController {

	private Pay99billBankPlugin pay99billBankPlugin = new Pay99billBankPlugin();
	private PluginConfigService pluginConfigService = new PluginConfigService();

	/**
	 * 安装
	 */
	public void install() {
		if (!pay99billBankPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = new PluginConfig();
			pluginConfig.setPluginId(pay99billBankPlugin.getId());
			pluginConfig.setIsEnabled(false);
			pluginConfigService.save(pluginConfig);
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 卸载
	 */
	public void uninstall() {
		if (pay99billBankPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = pay99billBankPlugin.getPluginConfig();
			pluginConfigService.delete(pluginConfig);
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 设置
	 */
	public void setting() {
		PluginConfig pluginConfig = pay99billBankPlugin.getPluginConfig();
		setAttr("feeTypes", FeeType.values());
		setAttr("pluginConfig", pluginConfig);
		render("/plugin/pay99billBank/setting.html");
	}

	/**
	 * 更新
	 */
	public void update() {
		String paymentName = getPara("paymentName");
		String partner = getPara("partner");
		String key = getPara("key");
		FeeType feeType = FeeType.valueOf(getPara("feeType"));
		BigDecimal fee = new BigDecimal(getPara("fee"));
		String logo = getPara("logo");
		String description = getPara("description");
		Boolean isEnabled = getParaToBoolean("isEnabled");
		Integer order = getParaToInt("order");
		PluginConfig pluginConfig = pay99billBankPlugin.getPluginConfig();
		pluginConfig.setAttribute(PaymentPlugin.PAYMENT_NAME_ATTRIBUTE_NAME, paymentName);
		pluginConfig.setAttribute("partner", partner);
		pluginConfig.setAttribute("key", key);
		pluginConfig.setAttribute(PaymentPlugin.FEE_TYPE_ATTRIBUTE_NAME, feeType.toString());
		pluginConfig.setAttribute(PaymentPlugin.FEE_ATTRIBUTE_NAME, fee.toString());
		pluginConfig.setAttribute(PaymentPlugin.LOGO_ATTRIBUTE_NAME, logo);
		pluginConfig.setAttribute(PaymentPlugin.DESCRIPTION_ATTRIBUTE_NAME, description);
		pluginConfig.setIsEnabled(isEnabled);
		pluginConfig.setOrders(order);
		pluginConfigService.update(pluginConfig);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/paymentPlugin/list"); 
	}

}