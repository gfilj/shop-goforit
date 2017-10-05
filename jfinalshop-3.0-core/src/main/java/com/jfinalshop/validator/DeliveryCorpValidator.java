package com.jfinalshop.validator;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.jfinalshop.model.DeliveryCorp;
import com.jfinalshop.service.DeliveryCorpService;

public class DeliveryCorpValidator extends Validator {

	DeliveryCorpService deliveryCorpService = Enhancer.enhance(DeliveryCorpService.class);
	
	@Override
	protected void validate(Controller c) {
		
	}

	@Override
	protected void handleError(Controller controller) {
		controller.keepModel(DeliveryCorp.class);
		controller.setAttr("dc", deliveryCorpService.findAll());
		String actionKey = getActionKey();
		if (actionKey.equals("/deliveryCorp/save")) {
			controller.render("/admin/delivery_corp/add.html");
		} else if (actionKey.equals("/deliveryCorp/update")) {
			controller.render("/admin/delivery_corp/edit.html");
		}
	}

}
