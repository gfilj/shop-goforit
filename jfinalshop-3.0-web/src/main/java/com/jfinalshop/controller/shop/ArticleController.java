/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop;

import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.common.Pageable;
import com.jfinalshop.common.ResourceNotFoundException;
import com.jfinalshop.model.ArticleCategory;
import com.jfinalshop.service.ArticleCategoryService;
import com.jfinalshop.service.ArticleService;
import com.jfinalshop.service.SearchService;

/**
 * Controller - 文章
 * 
 * 
 * 
 */
public class ArticleController extends BaseShopController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 20;

	private ArticleService articleService = enhance(ArticleService.class);
	private ArticleCategoryService articleCategoryService = enhance(ArticleCategoryService.class);
	private SearchService searchService = new SearchService();

	/**
	 * 列表
	 */
	public void list() {
		Long id = getParaToLong(0);
		Integer pageNumber = getParaToInt("pageNumber");
		ArticleCategory articleCategory = articleCategoryService.find(id);
		if (articleCategory == null) {
			throw new ResourceNotFoundException();
		}
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("articleCategory", articleCategory);
		setAttr("page", articleService.findPage(articleCategory, null, pageable));
		render("/shop/article/list.html");
	}

	/**
	 * 搜索
	 */
	public void search() {
		String keyword = getPara("keyword");
		Integer pageNumber = getParaToInt("pageNumber");
		if (StringUtils.isEmpty(keyword)) {
			renderJson(ERROR_VIEW);
			return;
		}
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("articleKeyword", keyword);
		setAttr("page", searchService.search(keyword, pageable));
		render("/shop/article/search.html");
	}

	/**
	 * 点击数
	 */
	public void hits() {
		Long id = getParaToLong("id");
		renderJson(articleService.viewHits(id));
	}

}