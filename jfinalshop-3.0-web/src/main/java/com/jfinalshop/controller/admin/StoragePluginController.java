package com.jfinalshop.controller.admin;


import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.service.PluginService;

/**
 * Controller - 存储插件
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/storagePlugin")
public class StoragePluginController extends BaseAdminController {

	private PluginService pluginService = enhance(PluginService.class);
	
	/**
	 * 列表
	 */
	public void list() {
		setAttr("storagePlugins", pluginService.getStoragePlugins());
		render("/admin/storage_plugin/list.html");
	}
}
