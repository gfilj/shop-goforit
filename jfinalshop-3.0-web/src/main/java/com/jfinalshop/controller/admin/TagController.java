package com.jfinalshop.controller.admin;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Tag;
import com.jfinalshop.model.Tag.Type;
import com.jfinalshop.service.TagService;

/**
 * Controller - 标签
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/tag")
public class TagController extends BaseAdminController {
	
	private TagService tagService = enhance(TagService.class);
	private Tag tag;
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<Tag> page = tagService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/tag/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		setAttr("types", Type.values());
		render("/admin/tag/add.html");
	}
	
	/**
	 * 保存
	 */
	public void save() {
		tag = getModel(Tag.class);
		String type = getPara("type", "");
		tag.setType(Type.valueOf(type).ordinal());
		tagService.save(tag);
		//addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("types", Type.values());
		setAttr("tag", tagService.find(id));
		render("/admin/tag/edit.html");
	}
	
	/**
	 * 更新
	 */
	public void update() {
		tag = getModel(Tag.class);
		tagService.update(tag);
		//addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		list();
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			tagService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
