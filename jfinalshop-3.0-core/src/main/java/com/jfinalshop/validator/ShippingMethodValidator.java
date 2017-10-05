package com.jfinalshop.validator;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.jfinalshop.model.ShippingMethod;
import com.jfinalshop.service.DeliveryCorpService;
import com.jfinalshop.service.ShippingMethodService;

public class ShippingMethodValidator extends Validator {

	 ShippingMethodService shippingMethodService = Enhancer.enhance(ShippingMethodService.class);
	 DeliveryCorpService deliveryCorpService =Enhancer.enhance(DeliveryCorpService.class);
	@Override
	protected void validate(Controller c) {
		
	}

	@Override
	protected void handleError(Controller controller) {
		controller.keepModel(ShippingMethod.class);
		controller.setAttr("ships", shippingMethodService.findAll());
		String actionKey = getActionKey();
		if (actionKey.equals("/admin/shippingMethod/save")) {
			controller.render("/admin/shipping_method/add.html");
		} else if (actionKey.equals("/admin/shippingMethod/update")) {
			controller.render("/admin/shipping_method/edit.html");
		}
	}

}
