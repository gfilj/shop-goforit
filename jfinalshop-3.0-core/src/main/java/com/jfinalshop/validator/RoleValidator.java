package com.jfinalshop.validator;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.jfinalshop.model.Role;
import com.jfinalshop.service.RoleService;

public class RoleValidator extends Validator {

	RoleService roleService = Enhancer.enhance(RoleService.class);
	@Override
	protected void validate(Controller c) {
		
	}

	@Override
	protected void handleError(Controller controller) {
		controller.keepModel(Role.class);
		controller.setAttr("rs", roleService.findAll());
		String actionKey = getActionKey();
		if (actionKey.equals("/role/save")) {
			controller.render("/admin/role/add.html");
		} else if (actionKey.equals("/role/update")) {
			controller.render("/admin/role/edit.html");
		}
	}

}
