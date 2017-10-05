package com.jfinalshop.validator;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.jfinalshop.model.Admin;
import com.jfinalshop.service.AdminService;
import com.jfinalshop.service.RoleService;

public class AdminValidator extends Validator {

	RoleService roleService = Enhancer.enhance(RoleService.class);
	AdminService adminService = Enhancer.enhance(AdminService.class);
	
	@Override
	protected void validate(Controller c) {
		Long[] roleIds = c.getParaValuesToLong("roleIds");
		String actionKey = getActionKey();
		String username = c.getPara("admin.username", "");
		
		if (roleIds == null) {
			addError("errorMessage", "角色必须选择一个!");
		}
		
		if (actionKey.equals("/admin/save")) {
			validateRequiredString("admin.password", "errorMessage", "密码不允许为空!");
			validateEqualField("admin.password", "rePassword", "errorMessage", "两次密码输入不一致!");
			validateString("admin.password", 4, 20, "errorMessage", "密码长度必须在【4】到【20】之间!");
			if (adminService.usernameExists(username)) {
				addError("errorMessage", "用户名称已存在!");
			}
		} else if (actionKey.equals("/admin/update")) {
			Admin pAdmin = adminService.find(c.getParaToLong("admin.id"));
			if (!StringUtils.equalsIgnoreCase(pAdmin.getUsername(), username) && adminService.usernameExists(username)) {
				addError("errorMessage", "用户名称已存在!");
			}
		}
		validateRequiredString("admin.name", "errorMessage", "姓名不允许为空!");
		validateEmail("admin.email", "errorMessage", "E-mail格式错误!");
		validateRequiredString("admin.email", "errorMessage", "E-mail不允许为空!");
		
		validateRequiredString("admin.username", "errorMessage", "用户名不允许为空!");
		validateString("admin.username", 2, 20, "errorMessage", "用户名长度必须在【2】到【20】之间!");
		validateRegex("admin.username", "^[0-9a-z_A-Z\u4e00-\u9fa5]+$", "errorMessage", "用户名只允许包含中文、英文、数字和下划线!!!!!!!");
	}

	@Override
	protected void handleError(Controller controller) {
		controller.keepModel(Admin.class);
		controller.setAttr("roles", roleService.findAll());
		String actionKey = getActionKey();
		if (actionKey.equals("/admin/save")) {
			controller.render("/admin/admin/add.html");
		} else if (actionKey.equals("/admin/update")) {
			controller.render("/admin/admin/edit.html");
		}
	}

}
