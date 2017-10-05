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
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.plugin.PaymentPlugin.FeeType;
import com.jfinalshop.plugin.TenpayPartnerPlugin;
import com.jfinalshop.service.PluginConfigService;

/**
 * Controller - 财付通(担保交易)
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/paymentPlugin/tenpayPartner")
public class TenpayPartnerController extends BaseAdminController {

	private TenpayPartnerPlugin tenpayPartnerPlugin;
	private PluginConfigService pluginConfigService;

	/**
	 * 安装
	 */
	public void install() {
		if (!tenpayPartnerPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = new PluginConfig();
			pluginConfig.setPluginId(tenpayPartnerPlugin.getId());
			pluginConfig.setIsEnabled(false);
			pluginConfigService.save(pluginConfig);
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 卸载
	 */
	public void uninstall() {
		if (tenpayPartnerPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = tenpayPartnerPlugin.getPluginConfig();
			pluginConfigService.delete(pluginConfig);
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 设置
	 */
	public void setting() {
		PluginConfig pluginConfig = tenpayPartnerPlugin.getPluginConfig();
		setAttr("feeTypes", FeeType.values());
		setAttr("pluginConfig", pluginConfig);
		renderJson("/plugin/tenpayPartner/setting.html");
	}

	/**
	 * 更新
	 */
	public void update() {
		String paymentName = getPara("paymentName");
		String partner = getPara("partner");
		String key = getPara("key");
		FeeType feeType = FeeType.valueOf(getPara("feeType"));
		BigDecimal fee = new BigDecimal(getPara("fee", "0"));
		String logo = getPara("logo");
		String description = getPara("description");
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		Integer order = getParaToInt("order");
		PluginConfig pluginConfig = tenpayPartnerPlugin.getPluginConfig();
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