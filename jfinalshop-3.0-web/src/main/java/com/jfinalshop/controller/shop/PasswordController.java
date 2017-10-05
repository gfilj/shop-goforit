/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.jfinalshop.common.Message;
import com.jfinalshop.common.Setting;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.SafeKey;
import com.jfinalshop.plugin.shiro.core.SubjectKit;
import com.jfinalshop.service.MailService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.utils.SettingUtils;


/**
 * Controller - 密码
 * 
 * 
 * 
 */
public class PasswordController extends BaseShopController {

	private MemberService memberService = enhance(MemberService.class);
	private MailService mailService = new MailService();

	/**
	 * 找回密码
	 */
	public void find() {
		setAttr("captchaId", UUID.randomUUID().toString());
		render("/shop/password/find.html");
	}

	/**
	 * 找回密码提交
	 */
	public void submit() {
		String captcha = getPara("captcha");
		String username = getPara("username");
		String email = getPara("email");
		
		if (!SubjectKit.doCaptcha("captcha", captcha)) {
			renderJson(Message.error("shop.captcha.invalid"));
			return;
		}
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(email)) {
			renderJson(Message.error("shop.common.invalid"));
			return;
		}
		Member member = memberService.findByUsername(username);
		if (member == null) {
			renderJson(Message.error("shop.password.memberNotExist"));
			return;
		}
		if (!member.getEmail().equalsIgnoreCase(email)) {
			renderJson(Message.error("shop.password.invalidEmail"));
			return;
		}
		Setting setting = SettingUtils.get();
		SafeKey safeKey = new SafeKey();
		safeKey.setValue(UUID.randomUUID().toString() + DigestUtils.md5Hex(RandomStringUtils.randomAlphabetic(30)));
		safeKey.setExpire(setting.getSafeKeyExpiryTime() != 0 ? DateUtils.addMinutes(new Date(), setting.getSafeKeyExpiryTime()) : null);
		member.setSafeKeyValue(safeKey.getValue());
		memberService.update(member);
		mailService.sendFindPasswordMail(member.getEmail(), member.getUsername(), safeKey);
		renderJson(Message.success("shop.password.mailSuccess"));
	}

	/**
	 * 重置密码
	 */
	public void resetSubmit() {
		String username = getPara("username");
		String key = getPara("key");
		
		Member member = memberService.findByUsername(username);
		if (member == null) {
			renderJson(ERROR_VIEW);
			return;
		}
		SafeKey safeKey = member.getSafeKey();
		if (safeKey == null || safeKey.getValue() == null || !safeKey.getValue().equals(key)) {
			renderJson(ERROR_VIEW);
			return;
		}
		if (safeKey.hasExpired()) {
			setAttr("erroInfo", Message.warn("shop.password.hasExpired"));
			renderJson(ERROR_VIEW);
			return;
		}
		setAttr("captchaId", UUID.randomUUID().toString());
		setAttr("member", member);
		setAttr("key", key);
		render("/shop/password/reset.html");
	}

	/**
	 * 重置密码提交
	 */
	public void reset() {
		String captcha = getPara("captcha");
		String username = getPara("captcha");
		String newPassword = getPara("captcha");
		String key = getPara("key");
		if (!SubjectKit.doCaptcha("captcha", captcha)) {
			renderJson(Message.error("shop.captcha.invalid"));
			return;
		}
		Member member = memberService.findByUsername(username);
		if (member == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		Setting setting = SettingUtils.get();
		if (newPassword.length() < setting.getPasswordMinLength() || newPassword.length() > setting.getPasswordMaxLength()) {
			renderJson(Message.warn("shop.password.invalidPassword"));
			return;
		}
		SafeKey safeKey = member.getSafeKey();
		if (safeKey == null || safeKey.getValue() == null || !safeKey.getValue().equals(key)) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		if (safeKey.hasExpired()) {
			renderJson(Message.error("shop.password.hasExpired"));
			return;
		}
		member.setPassword(DigestUtils.md5Hex(newPassword));
		safeKey.setExpire(new Date());
		safeKey.setValue(null);
		memberService.update(member);
		renderJson(Message.success("shop.password.resetSuccess"));
	}

}