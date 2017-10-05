package com.jfinalshop.service;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.Permission;
import com.jfinalshop.model.PermissionRole;
import com.jfinalshop.model.Role;

public class PermissionRoleService extends BaseService<PermissionRole> {
	public static final PermissionRoleService service = new PermissionRoleService();
	
	public PermissionRoleService() {
		super(PermissionRole.class);
	}
	
	/**
	 * 保存
	 * 
	 * 
	 */
	public boolean save(Role role) {
		boolean result = false;
		if (CollectionUtils.isNotEmpty(role.getPermissions())) {
			for (Permission permission : role.getPermissions()) {
				if (PermissionRole.dao.isNull(permission.getId(), role.getId())) {
					PermissionRole permissionRole = new PermissionRole();
					permissionRole.setPermissions(permission.getId());
					permissionRole.setRoles(role.getId());
					result = permissionRole.save();
				}
			}
		}
		return result;
	}
	
	/**
	 * 更新
	 * 
	 * 
	 */
	public boolean update(Role role) {
		boolean result = false;
		if (CollectionUtils.isNotEmpty(role.getPermissions())) {
			PermissionRole.dao.deletePermissionRole(role.getId());
			for (Permission permission : role.getPermissions()) {
				PermissionRole permissionRole = new PermissionRole();
				permissionRole.setPermissions(permission.getId());
				permissionRole.setRoles(role.getId());
				result = permissionRole.save();
			}
		}
		return result;
	}
	
}
