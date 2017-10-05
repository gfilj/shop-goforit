package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BasePermissionRole;

/**
 * Dao - 权限角色
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class PermissionRole extends BasePermissionRole<PermissionRole> {
	public static final PermissionRole dao = new PermissionRole();
	
	/**
	 * 检测是否已存在
	 * @return
	 */
	public boolean isNull(Long permissions, Long roles) {
		String sql = "SELECT COUNT(*) FROM permission_role WHERE permissions = ? AND roles = ?";
		return Db.queryLong(sql, permissions, roles) == 0L;
	}
	
	/**
	 * 根据文章删除
	 * @param articles
	 * @return
	 */
	public boolean deletePermissionRole(Long roles) {
		return Db.deleteById("permission_role", "roles", roles);
	}
}
