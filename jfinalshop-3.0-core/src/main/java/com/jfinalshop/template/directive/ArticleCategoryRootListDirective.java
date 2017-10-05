/*
 * 
 * 
 * 
 */
package com.jfinalshop.template.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.jfinalshop.model.ArticleCategory;
import com.jfinalshop.service.ArticleCategoryService;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 顶级文章分类列表
 * 
 * 
 * 
 */
public class ArticleCategoryRootListDirective extends BaseDirective {

	/** 变量名称 */
	private static final String VARIABLE_NAME = "articleCategories";

	private ArticleCategoryService articleCategoryService = new ArticleCategoryService();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		List<ArticleCategory> articleCategories;
		boolean useCache = useCache(env, params);
		String cacheRegion = getCacheRegion(env, params);
		Integer count = getCount(params);
		if (useCache) {
			articleCategories = articleCategoryService.findRoots(count, cacheRegion);
		} else {
			articleCategories = articleCategoryService.findRoots(count);
		}
		setLocalVariable(VARIABLE_NAME, articleCategories, env, body);
	}
}