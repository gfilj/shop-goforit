package com.jfinalshop.validator;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.jfinalshop.model.FriendLink;
import com.jfinalshop.service.FriendLinkService;

public class FriendLinkValidator extends Validator {

	FriendLinkService friendLinkService = Enhancer.enhance(FriendLinkService.class);
	@Override
	protected void validate(Controller c) {
		
	}

	@Override
	protected void handleError(Controller controller) {
		controller.keepModel(FriendLink.class);
		controller.setAttr("fls", friendLinkService.findAll());
		String actionKey = getActionKey();
		if (actionKey.equals("/friendLink/save")) {
			controller.render("/admin/friend_link/add.html");
		} else if (actionKey.equals("/friendLink/update")) {
			controller.render("/admin/friend_link/edit.html");
		}
	}

}
