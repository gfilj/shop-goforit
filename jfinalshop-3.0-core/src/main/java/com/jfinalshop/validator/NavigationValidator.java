package com.jfinalshop.validator;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.jfinalshop.model.Navigation;
import com.jfinalshop.service.ArticleCategoryService;
import com.jfinalshop.service.NavigationService;
import com.jfinalshop.service.ProductCategoryService;

public class NavigationValidator extends Validator {

	NavigationService navigationService = Enhancer.enhance(NavigationService.class);
    ArticleCategoryService articleCategoryService = Enhancer.enhance(ArticleCategoryService.class);
	ProductCategoryService productCategoryService = Enhancer.enhance(ProductCategoryService.class);
	@Override
	protected void validate(Controller c) {
		
	}
	@Override
	protected void handleError(Controller controller) {
		controller.keepModel(Navigation.class);
		controller.setAttr("ns", navigationService.findAll());
		String actionKey = getActionKey();
		if (actionKey.equals("/navigation/save")) {
			controller.render("/admin/navigation/add.html");
		} 
		else if (actionKey.equals("/navigation/update")) {
			controller.render("/admin/navigation/edit.html");
		}
	}

}
