/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.plugin;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.controller.admin.BaseAdminController;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.FilePlugin;
import com.jfinalshop.service.PluginConfigService;

/**
 * Controller - 本地文件存储
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/storagePlugin/file")
public class FileController extends BaseAdminController {

	private FilePlugin filePlugin = new FilePlugin();
	private PluginConfigService pluginConfigService = new PluginConfigService();

	/**
	 * 设置
	 */
	public void setting() {
		PluginConfig pluginConfig = filePlugin.getPluginConfig();
		setAttr("pluginConfig", pluginConfig);
		render("/plugin/file/setting.html");
	}

	/**
	 * 更新
	 */
	public void update(Integer order) {
		PluginConfig pluginConfig = filePlugin.getPluginConfig();
		pluginConfig.setIsEnabled(true);
		pluginConfig.setOrders(order);
		pluginConfigService.update(pluginConfig);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/storagePlugin/list");
	}

}