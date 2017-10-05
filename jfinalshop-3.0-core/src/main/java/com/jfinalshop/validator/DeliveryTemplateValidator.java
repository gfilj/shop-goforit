package com.jfinalshop.validator;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.service.DeliveryTemplateService;

public class DeliveryTemplateValidator extends Validator {

	DeliveryTemplateService deliveryTemplateService = Enhancer.enhance(DeliveryTemplateService.class);
	@Override
	protected void validate(Controller c) {
		
	}

	@Override
	protected void handleError(Controller controller) {
		controller.keepModel(PaymentMethod.class);
		controller.setAttr("dts", deliveryTemplateService.findAll());
		String actionKey = getActionKey();
		if (actionKey.equals("/deliveryTemplate/save")) {
			controller.render("/admin/delivery_template/add.html");
		} else if (actionKey.equals("/deliveryTemplate/update")) {
			controller.render("/admin/delivery_template/edit.html");
		}
	}

}
