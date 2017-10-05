package com.jfinalshop.service;

import com.jfinalshop.model.Admin;
import com.jfinalshop.model.AdminRole;
import com.jfinalshop.model.Role;

public class AdminRoleService extends BaseService<AdminRole> {

	public AdminRoleService() {
		super(AdminRole.class);
	}
	
	public static final AdminRoleService service = new AdminRoleService();
	
	/**
	 * 保存
	 * @param article
	 * @return
	 */
	public boolean save(Admin admin) {
		boolean result = false;
		if (!admin.getRoles().isEmpty()) {
			for (Role role : admin.getRoles()) {
				if (AdminRole.dao.isNull(admin.getId(), role.getId())) {
					AdminRole adminRole = new AdminRole();
					adminRole.setAdmins(admin.getId());
					adminRole.setRoles(role.getId());
					result = adminRole.save();
				}
			}
		}
		return result;
	}
	
	/**
	 * 更新
	 * @param article
	 * @return
	 */
	public boolean update(Admin admin) {
		boolean result = false;
		if (!admin.getRoles().isEmpty()) {
			AdminRole.dao.deleteRole(admin.getId());
			for (Role role : admin.getRoles()) {
				AdminRole adminRole = new AdminRole();
				adminRole.setAdmins(admin.getId());
				adminRole.setRoles(role.getId());
				result = adminRole.save();
			}
		}
		return result;
	}
}
