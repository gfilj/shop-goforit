package com.jfinalshop.validator;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.service.MemberRankService;

public class MemberRankValidator extends Validator {

	MemberRankService memberRankService = Enhancer.enhance(MemberRankService.class);
	@Override
	protected void validate(Controller c) {
		
	}

	@Override
	protected void handleError(Controller controller) {
		controller.keepModel(MemberRank.class);
		controller.setAttr("mrs", memberRankService.findAll());
		String actionKey = getActionKey();
		if (actionKey.equals("/memberRank/save")) {
			controller.render("/admin/member_rank/add.html");
		} else if (actionKey.equals("/memberRank/update")) {
			controller.render("/admin/member_rank/edit.html");
		}
	}

}
