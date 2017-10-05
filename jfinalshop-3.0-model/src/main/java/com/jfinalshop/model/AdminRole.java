package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseAdminRole;

/**
 * Dao - 用户角色
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class AdminRole extends BaseAdminRole<AdminRole> {
	public static final AdminRole dao = new AdminRole();
	
	/**
	 * 检测Role是否已存在
	 * @return
	 */
	public boolean isNull(Long adminId, Long roleId) {
		String sql = "SELECT COUNT(*) count FROM admin_role `adminRole` WHERE `adminRole`.admins = ? AND `adminRole`.roles = ?";
		return Db.queryLong(sql, adminId, roleId) == 0L;
	}
	
	/**
	 * 根据ID查找角色
	 * @param adminId
	 * @return
	 */
	public boolean deleteRole(Long admins) {
		return Db.deleteById("admin_role", "admins", admins);
	}
}
