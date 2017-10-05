package com.jfinalshop.controller.admin;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Seo;
import com.jfinalshop.service.SeoService;

/**
 * Controller - SEO设置
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/seo")
public class SeoController extends BaseAdminController {

	private SeoService seoService = enhance(SeoService.class);
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<Seo> page = seoService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/seo/list.html");
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("seo", seoService.find(id));
		render("/admin/seo/edit.html");
	}
	
	/**
	 * 更新
	 */
	public void update() {
		Seo seo = getModel(Seo.class);
		seoService.update(seo);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	
}
