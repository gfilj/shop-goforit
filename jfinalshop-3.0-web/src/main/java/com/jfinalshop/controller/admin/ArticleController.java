package com.jfinalshop.controller.admin;

import java.util.List;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.Tag;
import com.jfinalshop.model.Tag.Type;
import com.jfinalshop.service.ArticleCategoryService;
import com.jfinalshop.service.ArticleService;
import com.jfinalshop.service.TagService;

/**
 * Controller - 文章
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/article")
public class ArticleController extends BaseAdminController {

	private ArticleService articleService = enhance(ArticleService.class);
	private TagService tagService = enhance(TagService.class);
	private ArticleCategoryService articleCategoryService = enhance(ArticleCategoryService.class);
	private Article article;
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<Article> page = articleService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/article/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		setAttr("articleCategoryTree", articleCategoryService.findTree());
		setAttr("tags", tagService.findList(Type.article));
		render("/admin/article/add.html");
	}
	
	/**
	 * 保存
	 */
	public void save() {
		Long[] tagIds = getParaValuesToLong("tagIds");
		article = getModel(Article.class);
		article.setHits(0L);
		List<Tag> tags = tagService.findList(tagIds);
		article.setTags(tags);
		articleService.save(article);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("articleCategoryTree", articleCategoryService.findTree());
		setAttr("tags", tagService.findList(Type.article));
		setAttr("article", articleService.find(id));
		render("/admin/article/edit.html");
	}
	
	/**
	 * 更新
	 */
	public void update() {
		article = getModel(Article.class);
		Long[] tagIds = getParaValuesToLong("tagIds");
		List<Tag> tags = tagService.findList(tagIds);
		article.setTags(tags);
		article.remove("hits");
		article.remove("pageNumber");
		articleService.update(article);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			articleService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
