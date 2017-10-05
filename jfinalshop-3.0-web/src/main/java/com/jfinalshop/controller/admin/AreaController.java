package com.jfinalshop.controller.admin;

import java.util.ArrayList;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Message;
import com.jfinalshop.model.Area;
import com.jfinalshop.service.AreaService;
import com.jfinalshop.validator.AreaValidator;

/**
 * Controller - 地区
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/area")
public class AreaController extends BaseAdminController {
	private Area area;
	AreaService areaService = enhance(AreaService.class);
	
	/**
	 * 列表
	 */
	public void list() {
		Long parentId = getParaToLong("parentId");
		Area parent = areaService.find(parentId);
		if (parent != null) {
			setAttr("parent", parent);
			setAttr("areas", new ArrayList<Area>(parent.getChildren()));
		} else {
			setAttr("areas", areaService.findRoots());
		}
		render("/admin/area/list.html");
	}
	

	/**
	 * 添加
	 */
	public void add() {
		Long parentId = getParaToLong("parentId");
		setAttr("parent", areaService.find(parentId));
		render("/admin/area/add.html");
	}
	
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("area", areaService.find(id));
		render("/admin/area/edit.html");
	}
	
	/**
	 * 保存
	 */
	@Before(AreaValidator.class)
	public void save() {
		area = getModel(Area.class);
		areaService.save(area);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 更新
	 */
	@Before(AreaValidator.class)
	public void update() {
		area = getModel(Area.class);
		areaService.update(area);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("id");
		if (ids.length > 0) {
			areaService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
