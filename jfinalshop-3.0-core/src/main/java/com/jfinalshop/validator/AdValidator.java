package com.jfinalshop.validator;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.jfinalshop.model.Ad;
import com.jfinalshop.service.AdService;

public class AdValidator extends Validator {

	AdService adService = Enhancer.enhance(AdService.class);
	
	@Override
	protected void validate(Controller c) {
		
	}

	@Override
	protected void handleError(Controller controller) {
		controller.keepModel(Ad.class);
		controller.setAttr("ads", adService.findAll());
		String actionKey = getActionKey();
		if (actionKey.equals("/ad/save")) {
			controller.render("/admin/ad/add.html");
		} else if (actionKey.equals("/ad/update")) {
			controller.render("/admin/ad/edit.html");
		}
	}

}
