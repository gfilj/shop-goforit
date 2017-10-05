/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.admin;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Template;
import com.jfinalshop.service.StaticService;
import com.jfinalshop.service.TemplateService;

/**
 * Controller - Sitemap
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/sitemap")
public class SitemapController extends BaseAdminController {

	/**
	 * 生成Sitemap
	 */
	public void build() {
		Template sitemapIndexTemplate = TemplateService.service.get("sitemapIndex");
		setAttr("sitemapIndexPath", sitemapIndexTemplate.getStaticPath());
		render("/admin/sitemap/build.html");
	}

	/**
	 * 生成Sitemap
	 */
	public void submit() {
		StaticService.service.buildSitemap();
		addFlashMessage(SUCCESS_MESSAGE);
		build();
	}

}