package com.jfinalshop.validator;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.jfinalshop.model.AdPosition;
import com.jfinalshop.service.AdPositionService;

public class AdPositionValidator extends Validator {

	AdPositionService adPositionService = Enhancer.enhance(AdPositionService.class);
	@Override
	protected void validate(Controller c) {
		
	}

	@Override
	protected void handleError(Controller controller) {
		controller.keepModel(AdPosition.class);
		controller.setAttr("ad", adPositionService.findAll());
		String actionKey = getActionKey();
		if (actionKey.equals("/adPosition/save")) {
			controller.render("/admin/ad_position/add.html");
		} else if (actionKey.equals("/adPosition/update")) {
			controller.render("/admin/ad_position/edit.html");
		}
	}

}
