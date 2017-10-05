package com.jfinalshop.controller.admin;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Message;
import com.jfinalshop.model.Navigation;
import com.jfinalshop.model.Navigation.Position;
import com.jfinalshop.service.ArticleCategoryService;
import com.jfinalshop.service.NavigationService;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.validator.NavigationValidator;

/**
 * Controller - 导航
 * 
 * 
 * 
 */ 
@ControllerBind(controllerKey = "/admin/navigation")
public class NavigationController extends BaseAdminController {

	private NavigationService navigationService = enhance(NavigationService.class);
	private ArticleCategoryService articleCategoryService = enhance(ArticleCategoryService.class);
	private ProductCategoryService productCategoryService = enhance(ProductCategoryService.class);
	private Navigation navigation;
	
	/**
	 * 列表
	 */
	public void list() {
		setAttr("topNavigations", navigationService.findList(Position.top));
		setAttr("middleNavigations", navigationService.findList(Position.middle));
		setAttr("bottomNavigations", navigationService.findList(Position.bottom));
		render("/admin/navigation/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		setAttr("positions", Position.values());
		setAttr("articleCategoryTree", articleCategoryService.findTree());
		setAttr("productCategoryTree", productCategoryService.findTree());
		render("/admin/navigation/add.html");
	}
	
	/**
	 * 保存
	 */
	@Before(NavigationValidator.class)
	public void save() {
		String position = getPara("position", "");
		navigation = getModel(Navigation.class);
		navigation.setPosition(Position.valueOf(position).ordinal());
		navigationService.save(navigation);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}

	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("positions", Position.values());
		setAttr("articleCategoryTree", articleCategoryService.findTree());
		setAttr("productCategoryTree", productCategoryService.findTree());
		setAttr("navigation", navigationService.find(id));
		render("/admin/navigation/edit.html");
	}
	
	/**
	 * 更新
	 */
	@Before(NavigationValidator.class)
	public void update() {
		String position = getPara("position", "");
		navigation = getModel(Navigation.class);
		navigation.setPosition(Position.valueOf(position).ordinal());
		navigationService.update(navigation);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			navigationService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
