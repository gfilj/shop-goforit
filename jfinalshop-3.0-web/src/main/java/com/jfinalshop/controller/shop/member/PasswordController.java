/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop.member;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Setting;
import com.jfinalshop.controller.shop.BaseShopController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.utils.SettingUtils;

/**
 * Controller - 会员中心 - 密码
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/password")
@Before(MemberInterceptor.class)
public class PasswordController extends BaseShopController {

	private MemberService memberService = enhance(MemberService.class);

	/**
	 * 验证当前密码
	 */
	public void checkCurrentPassword() {
		String currentPassword = getPara("currentPassword");
		if (StringUtils.isEmpty(currentPassword)) {
			renderJson(false);
		}
		Member member = memberService.getCurrent(getRequest());
		if (StringUtils.equals(DigestUtils.md5Hex(currentPassword), member.getPassword())) {
			renderJson(true);
		} else {
			renderJson(false);
		}
	}

	/**
	 * 编辑
	 */
	public void edit() {
		render("/shop/member/password/edit.html");
	}

	/**
	 * 更新
	 */
	public void update() {
		String currentPassword = getPara("currentPassword");
		String password = getPara("password");
		if (StringUtils.isEmpty(password) || StringUtils.isEmpty(currentPassword)) {
			renderJson(ERROR_VIEW);
		}
		Setting setting = SettingUtils.get();
		if (password.length() < setting.getPasswordMinLength() || password.length() > setting.getPasswordMaxLength()) {
			renderJson(ERROR_VIEW);
		}
		Member member = memberService.getCurrent(getRequest());
		if (!StringUtils.equals(DigestUtils.md5Hex(currentPassword), member.getPassword())) {
			renderJson(ERROR_VIEW);
		}
		member.setPassword(DigestUtils.md5Hex(password));
		memberService.update(member);
		addFlashMessage(SUCCESS_MESSAGE);
		edit();
	}

}