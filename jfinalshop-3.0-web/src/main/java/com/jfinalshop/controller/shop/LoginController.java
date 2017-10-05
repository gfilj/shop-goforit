package com.jfinalshop.controller.shop;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.aop.Clear;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Principal;
import com.jfinalshop.common.Setting;
import com.jfinalshop.common.Setting.AccountLockType;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.Member;
import com.jfinalshop.plugin.shiro.core.SubjectKit;
import com.jfinalshop.service.CartService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.RSAService;
import com.jfinalshop.utils.IpUtil;
import com.jfinalshop.utils.SettingUtils;
import com.jfinalshop.utils.WebUtils;


/**
 * Controller - 会员登录
 * 
 * 
 * 
 */
public class LoginController extends BaseShopController {

	private MemberService memberService = enhance(MemberService.class);
	private CartService cartService = enhance(CartService.class);
	private RSAService rsaService = new RSAService();
	
	/**
	 * 登录检测
	 */
	@Clear
	public void check() {
		renderJson(memberService.isAuthenticated(getRequest())); ;
	}
	
	/**
	 * 登录页面
	 */
	@Clear
	public void index() {
		String redirectUrl = getPara("redirectUrl");
		Setting setting = SettingUtils.get();
		if (redirectUrl != null && !redirectUrl.equalsIgnoreCase(setting.getSiteUrl()) && !redirectUrl.startsWith(getRequest().getContextPath() + "/") && !redirectUrl.startsWith(setting.getSiteUrl() + "/")) {
			redirectUrl = null;
		}
		setAttr("redirectUrl", redirectUrl);
		setAttr("captchaId", UUID.randomUUID().toString());
		render("/shop/login/index.html");
	}
	
	/**
	 * 登录提交
	 */
	@Clear
	public void submit() {
		String username = getPara("username", "");
		String captcha = getPara("captcha");
		
		String password = rsaService.decryptParameter("enPassword", getRequest());
		rsaService.removePrivateKey(getRequest());

		if (!SubjectKit.doCaptcha("captcha", captcha)) {
			renderJson(Message.error("shop.captcha.invalid"));
			return;
		}
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			renderJson(Message.error("shop.common.invalid"));
			return;
		}
		Member member;
		Setting setting = SettingUtils.get();
		if (setting.getIsEmailLogin() && username.contains("@")) {
			List<Member> members = memberService.findListByEmail(username);
			if (members.isEmpty()) {
				member = null;
			} else if (members.size() == 1) {
				member = members.get(0);
			} else {
				renderJson(Message.error("shop.login.unsupportedAccount"));
				return;
			}
		} else {
			member = memberService.findByUsername(username);
		}
		if (member == null) {
			renderJson(Message.error("shop.login.unknownAccount"));
			return;
		}
		if (!member.getIsEnabled()) {
			renderJson(Message.error("shop.login.disabledAccount"));
			return;
		}
		if (member.getIsLocked()) {
			if (ArrayUtils.contains(setting.getAccountLockTypes(), AccountLockType.member)) {
				int loginFailureLockTime = setting.getAccountLockTime();
				if (loginFailureLockTime == 0) {
					renderJson(Message.error("shop.login.lockedAccount"));
					return;
				}
				Date lockedDate = member.getLockedDate();
				Date unlockDate = DateUtils.addMinutes(lockedDate, loginFailureLockTime);
				if (new Date().after(unlockDate)) {
					member.setLoginFailureCount(0);
					member.setIsLocked(false);
					member.setLockedDate(null);
					member.update();
				} else {
					renderJson(Message.error("shop.login.lockedAccount"));
					return;
				}
			} else {
				member.setLoginFailureCount(0);
				member.setIsLocked(false);
				member.setLockedDate(null);
				member.update();
			}
		}

		if (!DigestUtils.md5Hex(password).equals(member.getPassword())) {
			int loginFailureCount = member.getLoginFailureCount() + 1;
			if (loginFailureCount >= setting.getAccountLockCount()) {
				member.setIsLocked(true);
				member.setLockedDate(new Date());
			}
			member.setLoginFailureCount(loginFailureCount);
			member.update();
			if (ArrayUtils.contains(setting.getAccountLockTypes(), AccountLockType.member)) {
				renderJson(Message.error("shop.login.accountLockCount", setting.getAccountLockCount()));
				return;
			} else {
				renderJson(Message.error("shop.login.incorrectCredentials"));
				return;
			}
		}
		
		member.setLoginIp(IpUtil.getIpAddr(getRequest()));
		member.setLoginDate(new Date());
		member.setLoginFailureCount(0);
		member.update();

		Cart cart = cartService.getCurrent(getRequest());
		if (cart != null) {
			if (cart.getMember() == null) {
				cartService.merge(member, cart);
				WebUtils.removeCookie(getRequest(), getResponse(), Cart.ID_COOKIE_NAME);
				WebUtils.removeCookie(getRequest(), getResponse(), Cart.KEY_COOKIE_NAME);
			}
		}

		//HttpSession session = getSession();
//		Map<String, Object> attributes = new HashMap<String, Object>();
//		Enumeration<?> keys = session.getAttributeNames();
//		while (keys.hasMoreElements()) {
//			String key = (String) keys.nextElement();
//			attributes.put(key, session.getAttribute(key));
//		}
//		session.invalidate();
//		for (Entry<String, Object> entry : attributes.entrySet()) {
//			session.setAttribute(entry.getKey(), entry.getValue());
//		}
		
		setSessionAttr(MemberService.PRINCIPAL_ATTRIBUTE_NAME,  new Principal(member.getId(), username));
		WebUtils.addCookie(getRequest(), getResponse(), Member.USERNAME_COOKIE_NAME, member.getUsername());
		
		renderJson(SUCCESS_MESSAGE);
	}
}
