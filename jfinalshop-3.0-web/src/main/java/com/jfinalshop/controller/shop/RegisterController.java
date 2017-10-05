/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.core.Controller;
import com.jfinalshop.common.CommonAttributes;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Principal;
import com.jfinalshop.common.Setting;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Member.Gender;
import com.jfinalshop.model.MemberAttribute;
import com.jfinalshop.model.MemberAttribute.Type;
import com.jfinalshop.plugin.shiro.core.SubjectKit;
import com.jfinalshop.service.AreaService;
import com.jfinalshop.service.CartService;
import com.jfinalshop.service.MemberAttributeService;
import com.jfinalshop.service.MemberRankService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.RSAService;
import com.jfinalshop.utils.SettingUtils;
import com.jfinalshop.utils.WebUtils;

/**
 * Controller - 会员注册
 * 
 * 
 * 
 */
public class RegisterController extends Controller {

	private RSAService rsaService = new RSAService();
	private MemberService memberService = enhance(MemberService.class);
	private MemberRankService memberRankService = enhance(MemberRankService.class);
	private MemberAttributeService memberAttributeService = enhance(MemberAttributeService.class);
	private AreaService areaService = enhance(AreaService.class);
	private CartService cartService = enhance(CartService.class);

	/**
	 * 检查用户名是否被禁用或已存在
	 */
	public void checkUsername() {
		String username = getPara("username");
		if (StringUtils.isEmpty(username)) {
			renderJson(false);
		}
		if (memberService.usernameDisabled(username) || memberService.usernameExists(username)) {
			renderJson(false);
		} else {
			renderJson(true);
		}
	}

	/**
	 * 检查E-mail是否存在
	 */
	public void checkEmail() {
		String email = getPara("email");
		if (StringUtils.isEmpty(email)) {
			renderJson(false);
		}
		if (memberService.emailExists(email)) {
			renderJson(false);
		} else {
			renderJson(true);
		}
	}

	/**
	 * 注册页面
	 */
	public void index() {
		setAttr("genders", Gender.values());
		setAttr("captchaId", UUID.randomUUID().toString());
		render("/shop/register/index.html");
	}

	 
	
	/**
	 * 注册提交
	 */
	public void submit() {
		String captcha = getPara("captcha");
		String username = getPara("username");
		String email = getPara("email");
		HttpServletRequest request = getRequest();
		HttpServletResponse response = getResponse();
		//检查验证码是否有效
		/**
		 * 
		 */
		
		String password = rsaService.decryptParameter("enPassword", request);
		rsaService.removePrivateKey(request);

		if (!SubjectKit.doCaptcha("captcha", captcha)) {
			renderJson(Message.error("shop.captcha.invalid"));
			return;
		}
		Setting setting = SettingUtils.get();
		if (!setting.getIsRegisterEnabled()) {
			renderJson(Message.error("shop.register.disabled"));
			return;
		}
		if (username.length() < setting.getUsernameMinLength() || username.length() > setting.getUsernameMaxLength()) {
			renderJson(Message.error("shop.common.invalid"));
			return;
		}
		if (password.length() < setting.getPasswordMinLength() || password.length() > setting.getPasswordMaxLength()) {
			renderJson(Message.error("shop.common.invalid"));
			return;
		}
		if (memberService.usernameDisabled(username) || memberService.usernameExists(username)) {
			renderJson(Message.error("shop.register.disabledExist"));
			return;
		}
		if (!setting.getIsDuplicateEmail() && memberService.emailExists(email)) {
			renderJson(Message.error("shop.register.emailExist"));
			return;
		}

		Member pMember = new Member();
		List<MemberAttribute> memberAttributes = memberAttributeService.findList();
		for (MemberAttribute memberAttribute : memberAttributes) {
			String parameter = request.getParameter("memberAttribute_" + memberAttribute.getId());
			if (memberAttribute.getType() == Type.name.ordinal() || memberAttribute.getType() == Type.address.ordinal() || memberAttribute.getType() == Type.zipCode.ordinal() || memberAttribute.getType() == Type.phone.ordinal() || memberAttribute.getType() == Type.mobile.ordinal() || memberAttribute.getType() == Type.text.ordinal() || memberAttribute.getType() == Type.select.ordinal()) {
				if (memberAttribute.getIsRequired() && StringUtils.isEmpty(parameter)) {
					renderJson(Message.error("shop.common.invalid"));
					return;
				}
				pMember.setAttributeValue(memberAttribute, parameter);
			} else if (memberAttribute.getType() == Type.gender.ordinal()) {
				Gender gender = StringUtils.isNotEmpty(parameter) ? Gender.valueOf(parameter) : null;
				if (memberAttribute.getIsRequired() && gender == null) {
					renderJson(Message.error("shop.common.invalid"));
					return;
				}
				pMember.setGender(gender.ordinal());
			} else if (memberAttribute.getType() == Type.birth.ordinal()) {
				try {
					Date birth = StringUtils.isNotEmpty(parameter) ? DateUtils.parseDate(parameter, CommonAttributes.DATE_PATTERNS) : null;
					if (memberAttribute.getIsRequired() && birth == null) {
						renderJson(Message.error("shop.common.invalid"));
						return;
					}
					pMember.setBirth(birth);
				} catch (ParseException e) {
					renderJson(Message.error("shop.common.invalid"));
					return;
				}
			} else if (memberAttribute.getType() == Type.area.ordinal()) {
				Area area = StringUtils.isNotEmpty(parameter) ? areaService.find(Long.valueOf(parameter)) : null;
				if (area != null) {
					pMember.setAreaId(area.getId());
				} else if (memberAttribute.getIsRequired()) {
					renderJson(Message.error("shop.common.invalid"));
					return;
				}
			} else if (memberAttribute.getType() == Type.checkbox.ordinal()) {
				String[] parameterValues = request.getParameterValues("memberAttribute_" + memberAttribute.getId());
				List<String> options = parameterValues != null ? Arrays.asList(parameterValues) : null;
				if (memberAttribute.getIsRequired() && (options == null || options.isEmpty())) {
					renderJson(Message.error("shop.common.invalid"));
				}
				pMember.setAttributeValue(memberAttribute, options);
			}
		}
		pMember.setUsername(username.toLowerCase());
		pMember.setPassword(DigestUtils.md5Hex(password));
		pMember.setEmail(email);
		pMember.setPoint(setting.getRegisterPoint());
		pMember.setAmount(new BigDecimal(0));
		pMember.setBalance(new BigDecimal(0));
		pMember.setIsEnabled(true);
		pMember.setIsLocked(false);
		pMember.setLoginFailureCount(0);
		pMember.setLockedDate(null);
		pMember.setRegisterIp(request.getRemoteAddr());
		pMember.setLoginIp(request.getRemoteAddr());
		pMember.setLoginDate(new Date());
		pMember.setMemberRankId(memberRankService.findDefault().getId());
		pMember.setCreationDate(new Date());
		pMember.save();
		//memberService.save(pMember);
		
		Cart cart = cartService.getCurrent(request);
		if (cart != null && cart.getMember() == null) {
			cartService.merge(pMember, cart);
			WebUtils.removeCookie(request, response, Cart.ID_COOKIE_NAME);
			WebUtils.removeCookie(request, response, Cart.KEY_COOKIE_NAME);
		}

		setSessionAttr(MemberService.PRINCIPAL_ATTRIBUTE_NAME, new Principal(pMember.getId(), pMember.getUsername()));
		WebUtils.addCookie(request, response, Member.USERNAME_COOKIE_NAME, pMember.getUsername());
		renderJson(Message.success("shop.register.success"));
	}

}