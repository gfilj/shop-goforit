package com.jfinalshop.controller.admin;

import java.util.List;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Permission;
import com.jfinalshop.model.Role;
import com.jfinalshop.service.PermissionService;
import com.jfinalshop.service.RoleService;

/**
 * Controller - 角色
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/role")
public class RoleController extends BaseAdminController {
	
	private RoleService roleService = enhance(RoleService.class);
	private PermissionService permissionService = enhance(PermissionService.class);
	
	/**
	 * 添加
	 */
	public void add() {
		setAttr("modules", permissionService.getModules());
		setAttr("permissions", permissionService.findAll());
		render("/admin/role/add.html");
	}
	
	/**
	 * 保存
	 */
	public void save() {
		Role role = getModel(Role.class);
		Long[] ids = getParaValuesToLong("permissions");
		List<Permission> permissions = permissionService.findList(ids);
		role.setPermissions(permissions);
		role.setIsSystem(false);
		role.setAdmins(null);
		roleService.save(role);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/role/list");
	}
	
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("role", roleService.find(id));
		setAttr("modules", permissionService.getModules());
		setAttr("permissions", permissionService.findAll());
		render("/admin/role/edit.html");
	}
	
	/**
	 * 更新
	 */
	public void update() {
		Role role = getModel(Role.class);
		Boolean isSystem = getParaToBoolean("isSystem", false);
		Long[] ids = getParaValuesToLong("permissions");
		List<Permission> permissions = permissionService.findList(ids);
		Role pRole = roleService.find(role.getId());
		if (pRole == null || pRole.getIsSystem()) {
			renderJson(ERROR_VIEW);
			return;
		}
		role.setIsSystem(isSystem);
		role.setPermissions(permissions);
		roleService.update(role);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/role/list");
	}
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<Role> page = roleService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/role/list.html");
	}
	
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			for (Long id : ids) {
				Role role = roleService.find(id);
				if (role != null && (role.getIsSystem() || (role.getAdmins() != null && !role.getAdmins().isEmpty()))) {
					renderJson(Message.error("admin.role.deleteExistNotAllowed", role.getName()));
					return;
				}
			}
			roleService.delete(ids);
		}
		renderJson(SUCCESS_MESSAGE);
	}
	
	
}
