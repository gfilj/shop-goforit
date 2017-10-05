package com.jfinalshop.controller.admin;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.render.FreeMarkerRender;
import com.jfinalshop.common.Template.Type;
import com.jfinalshop.service.TemplateService;

import freemarker.template.Configuration;

/**
 * Controller - 模板
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/template")
public class TemplateController extends BaseAdminController {

	private TemplateService templateService = enhance(TemplateService.class);
	private Configuration configuration = FreeMarkerRender.getConfiguration();
	
	/**
	 * 列表
	 */
	public void list() {
		String str = getPara("type");
		Type type = null;
		if (StrKit.notBlank(str)) {
			setAttr("type", Type.valueOf(str));
		}
		setAttr("types", Type.values());
		setAttr("templates", templateService.getList(type));
		render("/admin/template/list.html");
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		String id = getPara("id");
		if (StringUtils.isEmpty(id)) {
			renderJson(ERROR_VIEW);
			return;
		}
		setAttr("template", templateService.get(id));
		setAttr("content", templateService.read(id));
		render("/admin/template/edit.html");
	}
	
	/**
	 * 更新
	 */
	public void update() {
		String id = getPara("id");
		String content = getPara("content");
		if (StringUtils.isEmpty(id) || content == null) {
			renderJson(ERROR_VIEW);
			return;
		}
		templateService.write(id, content);
		configuration.clearTemplateCache();
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
}
