package com.jfinalshop.model;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BasePluginConfig;

/**
 * Dao - 插件配置
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class PluginConfig extends BasePluginConfig<PluginConfig> {
	public static final PluginConfig dao = new PluginConfig();
	
	/** 属性 */
	private Map<String, String> attributes = new HashMap<String, String>();
	
	/**
	 * 判断插件ID是否存在
	 * 
	 * @param pluginId
	 *            插件ID
	 * @return 插件ID是否存在
	 */
	public boolean pluginIdExists(String pluginId) {
		if (pluginId == null) {
			return false;
		}
		String sql = "SELECT COUNT(*) FROM plugin_config WHERE plugin_id = ?";
		Long count = Db.queryLong(sql, pluginId);
		return count > 0;
	}

	/**
	 * 根据插件ID查找插件配置
	 * 
	 * @param pluginId
	 *            插件ID
	 * @return 插件配置，若不存在则返回null
	 */
	public PluginConfig findByPluginId(String pluginId) {
		if (pluginId == null) {
			return null;
		}
		String sql = "SELECT * FROM plugin_config WHERE plugin_id = ?";
		return findFirst(sql, pluginId);
	}
	
	
	/**
	 * 获取属性
	 * 
	 * @return 属性
	 */
	public Map<String, String> getAttributes() {
		if (attributes == null || attributes.isEmpty()) {
			String sql = "SELECT * FROM `plugin_config_attribute` WHERE `plugin_config` = ?";
			List<PluginConfigAttribute> pluginConfigAttributes = PluginConfigAttribute.dao.find(sql, getId());
			if (CollectionUtils.isNotEmpty(pluginConfigAttributes)) {
				for (PluginConfigAttribute pluginConfigAttribute : pluginConfigAttributes) {
					attributes.put(pluginConfigAttribute.getName(), pluginConfigAttribute.getAttributes());
				}
			}
		}
		return attributes;
	}

	/**
	 * 设置属性
	 * 
	 * @param attributes
	 *            属性
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	/**
	 * 获取属性值
	 * 
	 * @param name
	 *            属性名称
	 * @return 属性值
	 */
	public String getAttribute(String name) {
		if (getAttributes() != null && name != null) {
			return getAttributes().get(name);
		} else {
			return null;
		}
	}

	/**
	 * 设置属性值
	 * 
	 * @param name
	 *            属性名称
	 * @param value
	 *            属性值
	 */
	public void setAttribute(String name, String value) {
		if (getAttributes() != null && name != null) {
			getAttributes().put(name, value);
		}
	}
	
}
