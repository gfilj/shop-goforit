/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop;

import com.jfinalshop.model.Member;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.utils.WebUtils;

/**
 * Controller - 会员注销
 * 
 * 
 * 
 */
public class LogoutController extends BaseShopController {

	/**
	 * 注销
	 */
	public void index() {
		removeSessionAttr(MemberService.PRINCIPAL_ATTRIBUTE_NAME);
		WebUtils.removeCookie(getRequest(), getResponse(), Member.USERNAME_COOKIE_NAME);
		redirect("/");
	}

}