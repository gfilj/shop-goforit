package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseRole;

/**
 * Dao - 角色
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Role extends BaseRole<Role> {
	public static final Role dao = new Role();
	
	/** 权限 */
	private List<Permission> permissions = new ArrayList<Permission>();

	/** 管理员 */
	private List<Admin> admins = new ArrayList<Admin>();
	
	/**
	 * 获取所有
	 * @return
	 */
	public List<Role> getAll() {
		String sql = "SELECT * FROM role";
		return find(sql);
	}
	
	/**
	 * 登录ID所属角色
	 * @param adminId
	 * @return
	 */
	public List<Role> getRoleByAdminId(Long adminId) {
		String sql = "SELECT * FROM role WHERE id IN (SELECT roles FROM admin_role WHERE admins = ?)";
		return find(sql, adminId);
	}
	
	/**
	 * 获取权限
	 * 
	 * @return 权限
	 */
	public List<Permission> getPermissions() {
		if (CollectionUtils.isEmpty(permissions)) {
			String sql = "SELECT p.* FROM permission_role pr INNER JOIN permission p ON pr.`permissions` = p.`id` WHERE pr.`roles` = ?";
			permissions = Permission.dao.find(sql, getId());
		}
		return permissions;
	}

	/**
	 * 设置权限
	 * 
	 * @param authorities
	 *            权限
	 */
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	/**
	 * 获取管理员
	 * 
	 * @return 管理员
	 */
	public List<Admin> getAdmins() {
		if (CollectionUtils.isEmpty(admins)) {
			String sql = "SELECT a.* FROM admin_role ar INNER JOIN admin a ON ar.`admins` = a.`id` WHERE ar.`roles` = ?";
			admins = Admin.dao.find(sql, getId());
		}
		return admins;
	}

	/**
	 * 设置管理员
	 * 
	 * @param admins
	 *            管理员
	 */
	public void setAdmins(List<Admin> admins) {
		this.admins = admins;
	}
	
}
