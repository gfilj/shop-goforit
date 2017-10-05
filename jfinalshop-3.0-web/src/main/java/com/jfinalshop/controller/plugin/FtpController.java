/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.plugin;


import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.controller.admin.BaseAdminController;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.FtpPlugin;
import com.jfinalshop.service.PluginConfigService;

/**
 * Controller - FTP
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/storagePlugin/ftp")
public class FtpController extends BaseAdminController {

	private FtpPlugin ftpPlugin = new FtpPlugin();
	private PluginConfigService pluginConfigService = new PluginConfigService();

	/**s
	 * 安装
	 */
	public void install() {
		if (!ftpPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = new PluginConfig();
			pluginConfig.setPluginId(ftpPlugin.getId());
			pluginConfig.setIsEnabled(false);
			pluginConfigService.save(pluginConfig);
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 卸载
	 */
	public void uninstall() {
		if (ftpPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = ftpPlugin.getPluginConfig();
			pluginConfigService.delete(pluginConfig);
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 设置
	 */
	public void setting() {
		PluginConfig pluginConfig = ftpPlugin.getPluginConfig();
		setAttr("pluginConfig", pluginConfig);
		render("/plugin/ftp/setting.html");
	}

	/**
	 * 更新
	 */
	public void update() {
		String host = getPara("host");
		Integer port = getParaToInt("port");
		String username = getPara("username");
		String password = getPara("password");
		String urlPrefix = getPara("urlPrefix");
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		Integer order = getParaToInt("order", 0);
		PluginConfig pluginConfig = ftpPlugin.getPluginConfig();
		pluginConfig.setAttribute("host", host);
		pluginConfig.setAttribute("port", port.toString());
		pluginConfig.setAttribute("username", username);
		pluginConfig.setAttribute("password", password);
		pluginConfig.setAttribute("urlPrefix", StringUtils.removeEnd(urlPrefix, "/"));
		pluginConfig.setIsEnabled(isEnabled);
		pluginConfig.setOrders(order);
		pluginConfigService.update(pluginConfig);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/storagePlugin/list");
	}

}