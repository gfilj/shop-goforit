package com.jfinalshop.controller.admin;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.Role;
import com.jfinalshop.service.AdminService;
import com.jfinalshop.service.RoleService;
import com.jfinalshop.validator.AdminValidator;

/**
 * Controller - 管理员
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/admin")
public class AdminController extends BaseAdminController {
	
	private AdminService adminService = enhance(AdminService.class);
	private RoleService roleService = enhance(RoleService.class);
	private Admin admin;
		
	/**
	 * 列表分页
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<Admin> page = adminService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/admin/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		setAttr("roles", roleService.findAll());
		render("/admin/admin/add.html");
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("roles", roleService.findAll());
		setAttr("admin", adminService.find(id));
		render("/admin/admin/edit.html"); 
	}
	
	/**
	 * 检查用户名是否存在
	 */
	public void checkUsername() {
		String username = getPara("admin.username", "");
		if (StrKit.isBlank(username) || adminService.usernameExists(username)) {
			renderJson(false); ;
		} else {
			renderJson(true);
		}
	}
	
	/**
	 * 保存
	 */
	@Before(AdminValidator.class)
	public void save() {
		admin = getModel(Admin.class);
		Long[] roleIds = getParaValuesToLong("roleIds");
		List<Role> roles = roleService.findList(roleIds);
		admin.setRoles(roles);
		if (!adminService.save(admin)) {
			redirect(ERROR_VIEW);
			return;
		}
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/admin/list");
	}
	
	/**
	 * 更新
	 */
	@Before(AdminValidator.class)
	public void update() {
		admin = getModel(Admin.class);
		Long[] roleIds = getParaValuesToLong("roleIds");
		List<Role> roles = roleService.findList(roleIds);
		admin.setRoles(roles);
		if(!adminService.update(admin)) {
			redirect(ERROR_VIEW);
			return;
		}
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/admin/list");
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			adminService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
