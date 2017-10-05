package com.jfinalshop.service;

import com.jfinalshop.model.PermissionRole;
import com.jfinalshop.model.Role;

/**
 * Service - 角色
 * 
 * 
 * 
 */
public class RoleService extends BaseService<Role> {
	
	public RoleService() {
		super(Role.class);
	}
	
	/**
	 * 保存
	 * 
	 */
	public boolean save(Role role) {
		boolean result = false;
		result = super.save(role);
		PermissionRoleService.service.save(role);
		return result;
	}
	
	
	/**
	 * 更新
	 * 
	 */
	public boolean update(Role role) {
		boolean result = false;
		result = super.update(role);
		PermissionRoleService.service.update(role);
		return result;
	}
	
	/**
	 * 删除
	 * 
	 */
	public boolean delete(Long[] ids) {
		boolean result = false;
		for (Long id : ids) {
			PermissionRole.dao.deletePermissionRole(id);
			result = Role.dao.deleteById(id);
		}
		return result;
	}
	
	
}
