/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.plugin;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Message;
import com.jfinalshop.controller.admin.BaseAdminController;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.OssPlugin;
import com.jfinalshop.service.PluginConfigService;

/**
 * Controller - 阿里云存储
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/storagePlugin/oss")
public class OssController extends BaseAdminController {

	private OssPlugin ossPlugin = new OssPlugin();
	private PluginConfigService pluginConfigService = new PluginConfigService();

	/**
	 * 安装
	 */
	public void install() {
		String specificationVersion = System.getProperty("java.specification.version");
		if (StringUtils.isNotEmpty(specificationVersion)) {
			BigDecimal version = new BigDecimal(specificationVersion);
			if (version.compareTo(new BigDecimal("1.6")) < 0) {
				renderJson(Message.error("admin.plugin.oss.unsupportedJavaVersion"));
			}
		}
		if (!ossPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = new PluginConfig();
			pluginConfig.setPluginId(ossPlugin.getId());
			pluginConfig.setIsEnabled(false);
			pluginConfigService.save(pluginConfig);
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 卸载
	 */
	public void uninstall() {
		if (ossPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = ossPlugin.getPluginConfig();
			pluginConfigService.delete(pluginConfig);
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 设置
	 */
	public void setting() {
		PluginConfig pluginConfig = ossPlugin.getPluginConfig();
		setAttr("pluginConfig", pluginConfig);
		render("/plugin/oss/setting.html");
	}

	/**
	 * 更新
	 */
	public void update() {
		String accessId = getPara("accessId");
		String accessKey = getPara("accessKey");
		String bucketName = getPara("bucketName");
		String urlPrefix = getPara("urlPrefix");
		Boolean isEnabled = getParaToBoolean("isEnabled");
		Integer order = getParaToInt("order");

		PluginConfig pluginConfig = ossPlugin.getPluginConfig();
		pluginConfig.setAttribute("accessId", accessId);
		pluginConfig.setAttribute("accessKey", accessKey);
		pluginConfig.setAttribute("bucketName", bucketName);
		pluginConfig.setAttribute("urlPrefix", StringUtils.removeEnd(urlPrefix, "/"));
		pluginConfig.setIsEnabled(isEnabled);
		pluginConfig.setOrders(order);
		pluginConfigService.update(pluginConfig);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/storagePlugin/list");
	}

}