	package com.jfinalshop.validator;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.jfinalshop.model.DeliveryCenter;
import com.jfinalshop.service.DeliveryCenterService;

public class DeliveryCenterValidator extends Validator {

	DeliveryCenterService deliveryCenterService = Enhancer.enhance(DeliveryCenterService.class);
	@Override
	protected void validate(Controller c) {
		
	}

	@Override
	protected void handleError(Controller controller) {
		controller.keepModel(DeliveryCenter.class);
		controller.setAttr("dcs", deliveryCenterService.findAll());
		String actionKey = getActionKey();
		if (actionKey.equals("/deliveryCenter/save")) {
			controller.render("/admin/delivery_center/add.html");
		} else if (actionKey.equals("/deliveryCenter/update")) {
			controller.render("/admin/delivery_center/edit.html");
		}
	}

}
