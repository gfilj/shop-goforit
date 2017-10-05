package com.jfinalshop.validator;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.jfinalshop.model.Brand;
import com.jfinalshop.service.BrandService;

public class BrandValidator extends Validator {

	BrandService brandService = Enhancer.enhance(BrandService.class);
	@Override
	protected void validate(Controller c) {
		
	}

	@Override
	protected void handleError(Controller controller) {
		controller.keepModel(Brand.class);
		controller.setAttr("bs", brandService.findAll());
		String actionKey = getActionKey();
		if (actionKey.equals("/brand/save")) {
			controller.render("/admin/brand/add.html");
		} else if (actionKey.equals("/brand/update")) {
			controller.render("/admin/brand/edit.html");
		}
	}

}
