package com.jfinalshop.validator;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.jfinalshop.model.Message;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.MessageService;

public class MessageValidator extends Validator {

	MessageService messageService = Enhancer.enhance(MessageService.class);
 	MemberService memberService = Enhancer.enhance(MemberService.class);
	@Override
	protected void validate(Controller c) {
		
	}

	@Override
	protected void handleError(Controller controller) {
		controller.keepModel(Message.class);
		controller.setAttr("ms", messageService.findAll());
		String actionKey = getActionKey();
		if (actionKey.equals("/message/submit")) {
			controller.render("/admin/message/send.html");
		} 
	}

}
