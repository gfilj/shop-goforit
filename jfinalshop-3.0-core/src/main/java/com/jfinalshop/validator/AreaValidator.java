package com.jfinalshop.validator;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.jfinalshop.model.Area;
import com.jfinalshop.service.AreaService;

public class AreaValidator extends Validator {

	AreaService areaService = Enhancer.enhance(AreaService.class);
	@Override
	protected void validate(Controller c) {
		
	}

	@Override
	protected void handleError(Controller controller) {
		controller.keepModel(Area.class);
		controller.setAttr("areas", areaService.findAll());
		String actionKey = getActionKey();
		if (actionKey.equals("/area/save")) {
			controller.render("/admin/area/add.html");
		} else if (actionKey.equals("/area/update")) {
			controller.render("/admin/area/edit.html");
		}
	}

}
