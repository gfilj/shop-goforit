package com.jfinalshop.service;

import java.util.List;

import com.jfinalshop.model.Permission;

public class PermissionService extends BaseService<Permission>{
	
	public PermissionService() {
		super(Permission.class);
	}

	
	/**
	 * 获取模块分组
	 * 
	 * @return 模块分组
	 */
	public List<String> getModules() {
		return Permission.dao.getModules();
	}
}
