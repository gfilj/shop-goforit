package com.jfinalshop.controller.admin;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.model.Admin;
import com.jfinalshop.plugin.shiro.hasher.Hasher;
import com.jfinalshop.plugin.shiro.hasher.HasherInfo;
import com.jfinalshop.plugin.shiro.hasher.HasherKit;
import com.jfinalshop.security.ShiroUtil;
import com.jfinalshop.service.AdminService;

/**
 * Controller - 个人资料
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/profile")
public class ProfileController extends BaseAdminController {

	private AdminService adminService = enhance(AdminService.class);
	
	/**
	 * 验证当前密码
	 */
	public void checkCurrentPassword() {
		String currentPassword = getPara("currentPassword");
		if (StringUtils.isEmpty(currentPassword)) {
			renderJson(false);
			return;
		}
		Admin admin = ShiroUtil.getAdmin();
		if (HasherKit.match(currentPassword, admin.getPassword())) {
			renderJson(true);
			return;
		} else {
			renderJson(false);
			return;
		}
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		setAttr("admin", ShiroUtil.getAdmin());
		render("/admin/profile/edit.html");
	}
	
	/**
	 * 更新
	 */
	public void update() {
		String currentPassword = getPara("currentPassword");
		String password = getPara("password");
		String rePassword = getPara("rePassword");
		String email = getPara("email");
		Admin pAdmin = ShiroUtil.getAdmin();
		if (StringUtils.isNotEmpty(currentPassword) && StringUtils.isNotEmpty(password)) {
			if (!HasherKit.match(currentPassword, pAdmin.getPassword())) {
				renderJson(ERROR_VIEW);
				return;
			}
			if (rePassword == null || (! password.equals(rePassword))) {
				renderJson(ERROR_VIEW);
				return;
			}
	/*		HasherInfo hasherInfo = HasherKit.hash(password, Hasher.DEFAULT);
			password = hasherInfo.getHashResult();*/
			pAdmin.setPassword(password);
		}
		pAdmin.setEmail(email);
		adminService.update(pAdmin);
		addFlashMessage(SUCCESS_MESSAGE);
		edit();
	}
}
