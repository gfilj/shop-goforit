package com.jfinalshop.controller.admin;


import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Message;
import com.jfinalshop.model.ArticleCategory;
import com.jfinalshop.service.ArticleCategoryService;
import com.jfinalshop.validator.ArticleCategoryValidator;

/**
 * 文章分类
 * 
 */
@ControllerBind(controllerKey = "/admin/articleCategory")
public class ArticleCategoryController extends BaseAdminController {
	private ArticleCategory articleCategory;
	private ArticleCategoryService articleCategoryService = enhance(ArticleCategoryService.class);
	
	
	/**
	 * 列表
	 */
	public void list() {
		setAttr("articleCategoryTree", articleCategoryService.findTree());
		render("/admin/article_category/list.html");
	}

	/**
	 * 添加
	 */
	public void add() {
		setAttr("articleCategoryTree", articleCategoryService.findTree());
		render("/admin/article_category/add.html");
	}
	
	
	/**
	 * 保存
	 */
	@Before(ArticleCategoryValidator.class)
	public void save() {
		articleCategory = getModel(ArticleCategory.class);
		articleCategoryService.save(articleCategory);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		ArticleCategory articleCategory = articleCategoryService.find(id);
		setAttr("articleCategoryTree", articleCategoryService.findTree());
		setAttr("articleCategory", articleCategory);
		setAttr("children", articleCategoryService.findChildren(articleCategory));
		render("/admin/article_category/edit.html");
	}
	
	/**
	 * 更新
	 */
	@Before(ArticleCategoryValidator.class)
	public void update() {
		articleCategory = getModel(ArticleCategory.class);
		if (articleCategory.getParentId() != null) {
			ArticleCategory parent = articleCategoryService.find(articleCategory.getParentId());
			if (parent.equals(articleCategory)) {
				renderJson(ERROR_VIEW);
			}
			List<ArticleCategory> children = articleCategoryService.findChildren(parent);
			if (children != null && children.contains(parent)) {
				renderJson(ERROR_VIEW);
			}
		}
		articleCategoryService.update(articleCategory);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("id");
		if (ids.length > 0) {
			articleCategoryService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
