package com.jfinalshop.service;

import com.jfinalshop.model.PluginConfig;



public class PluginConfigService extends BaseService<PluginConfig>{
	
	public PluginConfigService() {
		super(PluginConfig.class);
	}
	
	
	/**
	 * 判断插件ID是否存在
	 * 
	 * @param pluginId
	 *            插件ID
	 * @return 插件ID是否存在
	 */
	public boolean pluginIdExists(String pluginId) {
		return PluginConfig.dao.pluginIdExists(pluginId);
	}

	/**
	 * 根据插件ID查找插件配置
	 * 
	 * @param pluginId
	 *            插件ID
	 * @return 插件配置，若不存在则返回null
	 */
	public PluginConfig findByPluginId(String pluginId) {
		return PluginConfig.dao.findByPluginId(pluginId);
	}
	
}
