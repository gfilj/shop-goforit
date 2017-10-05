package com.jfinalshop.controller.admin;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Permission;
import com.jfinalshop.service.PermissionService;

@ControllerBind(controllerKey = "/admin/permission")
public class PermissionController extends BaseAdminController {
	
	private PermissionService permissionService = enhance(PermissionService.class);
	private Permission permission;
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("page", permissionService.findPage(pageable));
		setAttr("pageable", pageable);
		render("/admin/permission/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		render("/admin/permission/add.html");
	}
	
	/**
	 * 保存
	 */
	public void save() {
		permission = getModel(Permission.class);
		permissionService.save(permission);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/permission/list");
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("permission", permissionService.find(id));
		render("/admin/permission/edit.html");
	}
	
	/**
	 * 更新
	 */
	public void update() {
		permission = getModel(Permission.class);
		permissionService.update(permission);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/permission/list");
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			for (Long id : ids) {
				Permission permission = permissionService.find(id);
				if (permission != null && (permission.getRoles() != null && !permission.getRoles().isEmpty())) {
					renderJson(Message.error("admin.permission.deleteExistNotAllowed", permission.getName()));
					return;
				}
			}
			permissionService.delete(ids);
		}
		renderJson(SUCCESS_MESSAGE);
	}

}
