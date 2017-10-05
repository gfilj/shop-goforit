package com.jfinalshop.validator;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.service.PaymentMethodService;
import com.jfinalshop.service.ShippingMethodService;

public class PaymentMethodValidator extends Validator {

	PaymentMethodService paymentMethodService = Enhancer.enhance(PaymentMethodService.class);
	ShippingMethodService shippingMethodService = Enhancer.enhance(ShippingMethodService.class);
	@Override
	protected void validate(Controller c) {
		
	}

	@Override
	protected void handleError(Controller controller) {
		controller.keepModel(PaymentMethod.class);
		controller.setAttr("ships", shippingMethodService.findAll());
		String actionKey = getActionKey();
		if (actionKey.equals("/paymentMethod/save")) {
			controller.render("/admin/payment_method/add.html");
		} else if (actionKey.equals("/paymentMethod/update")) {
			controller.render("/admin/payment_method/edit.html");
		}
	}

}
