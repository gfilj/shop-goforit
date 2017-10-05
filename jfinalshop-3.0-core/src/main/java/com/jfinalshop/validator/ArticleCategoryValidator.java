package com.jfinalshop.validator;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.jfinalshop.model.ArticleCategory;
import com.jfinalshop.service.ArticleCategoryService;

public class ArticleCategoryValidator extends Validator {

	ArticleCategoryService articleCategoryService = Enhancer.enhance(ArticleCategoryService.class);
	
	@Override
	protected void validate(Controller c) {
		
	}

	@Override
	protected void handleError(Controller controller) {
		controller.keepModel(ArticleCategory.class);
		controller.setAttr("acs", articleCategoryService.findAll());
		String actionKey = getActionKey();
		if (actionKey.equals("/articleCategory/save")) {
			controller.render("/admin/article_category/add.html");
		} else if (actionKey.equals("/articleCategory/update")) {
			controller.render("/admin/article_category/edit.html");
		}
	}

}
