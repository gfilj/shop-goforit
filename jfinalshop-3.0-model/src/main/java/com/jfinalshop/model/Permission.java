package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BasePermission;

/**
 * Dao - 权限
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Permission extends BasePermission<Permission> {
	public static final Permission dao = new Permission();
	
	/** 角色 */
	private List<Role> roles = new ArrayList<Role>();
	
	public List<Permission> getPermissionByRoleId(Long roleId) {
		String sql = "SELECT * FROM permission WHERE id IN (SELECT permissions FROM permission_role WHERE roles = ?)";
		return find(sql,roleId);
	}

	/**
	 * 获取角色
	 * 
	 * @return 角色
	 */
	public List<Role> getRoles() {
		if (CollectionUtils.isEmpty(roles)) {
			String sql = "SELECT r.* FROM permission_role pr INNER JOIN role r ON pr.`roles` = r.`id` WHERE pr.`permissions` = ?";
			roles = Role.dao.find(sql, getId());
		}
		return roles;
	}

	/**
	 * 获取模块分组
	 * 
	 * @return 模块分组
	 */
	public List<String> getModules() {
		String sql = "SELECT DISTINCT module FROM permission";
		return Db.query(sql);
	}
	
	/**
	 * 设置角色
	 * 
	 * @param roles
	 *            角色
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
}
