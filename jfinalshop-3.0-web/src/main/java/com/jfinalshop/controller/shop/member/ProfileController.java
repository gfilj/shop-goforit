/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop.member;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.CommonAttributes;
import com.jfinalshop.common.Setting;
import com.jfinalshop.controller.shop.BaseShopController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Member.Gender;
import com.jfinalshop.model.MemberAttribute;
import com.jfinalshop.model.MemberAttribute.Type;
import com.jfinalshop.service.AreaService;
import com.jfinalshop.service.MemberAttributeService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.utils.SettingUtils;

/**
 * Controller - 会员中心 - 个人资料
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/profile")
@Before(MemberInterceptor.class)
public class ProfileController extends BaseShopController {

	private MemberService memberService = enhance(MemberService.class);
	private MemberAttributeService memberAttributeService = enhance(MemberAttributeService.class);
	private AreaService areaService = enhance(AreaService.class);

	/**
	 * 检查E-mail是否唯一
	 */
	public void checkEmail() {
		String email = getPara("email");
		if (StringUtils.isEmpty(email)) {
			renderJson(false);
			return;
		}
		Member member = memberService.getCurrent(getRequest());
		if (memberService.emailUnique(member.getEmail(), email)) {
			renderJson(true);
		} else {
			renderJson(false);
		}
	}

	/**
	 * 编辑
	 */
	public void edit() {
		setAttr("genders", Gender.values());
		setAttr("memberAttributes", memberAttributeService.findList());
		render("/shop/member/profile/edit.html");
	}

	/**
	 * 更新
	 */
	public void update() {
		String email = getPara("email");
		Setting setting = SettingUtils.get();
		Member member = memberService.getCurrent(getRequest());
		if (!setting.getIsDuplicateEmail() && !memberService.emailUnique(member.getEmail(), email)) {
			renderJson(ERROR_VIEW);
			return;
		}
		member.setEmail(email);
		List<MemberAttribute> memberAttributes = memberAttributeService.findList();
		for (MemberAttribute memberAttribute : memberAttributes) {
			String parameter = getPara("memberAttribute_" + memberAttribute.getId());
			if (memberAttribute.getType() == Type.name.ordinal() || memberAttribute.getType() == Type.address.ordinal() || memberAttribute.getType() == Type.zipCode.ordinal() || memberAttribute.getType() == Type.phone.ordinal() || memberAttribute.getType() == Type.mobile.ordinal() || memberAttribute.getType() == Type.text.ordinal() || memberAttribute.getType() == Type.select.ordinal()) {
				if (memberAttribute.getIsRequired() && StringUtils.isEmpty(parameter)) {
					renderJson(ERROR_VIEW);
					return;
				}
				member.setAttributeValue(memberAttribute, parameter);
			} else if (memberAttribute.getType() == Type.gender.ordinal()) {
				Gender gender = StringUtils.isNotEmpty(parameter) ? Gender.valueOf(parameter) : null;
				if (memberAttribute.getIsRequired() && gender == null) {
					renderJson(ERROR_VIEW);
					return;
				}
				member.setGender(Gender.male.ordinal());
			} else if (memberAttribute.getType() == Type.birth.ordinal()) {
				try {
					Date birth = StringUtils.isNotEmpty(parameter) ? DateUtils.parseDate(parameter, CommonAttributes.DATE_PATTERNS) : null;
					if (memberAttribute.getIsRequired() && birth == null) {
						renderJson(ERROR_VIEW);
						return;
					}
					member.setBirth(birth);
				} catch (ParseException e) {
					renderJson(ERROR_VIEW);
					return;
				}
			} else if (memberAttribute.getType() == Type.area.ordinal()) {
				Area area = StringUtils.isNotEmpty(parameter) ? areaService.find(Long.valueOf(parameter)) : null;
				if (area != null) {
					member.setAreaId(area.getId());
				} else if (memberAttribute.getIsRequired()) {
					renderJson(ERROR_VIEW);
					return;
				}
			} else if (memberAttribute.getType() == Type.checkbox.ordinal()) {
				String[] parameterValues = getParaValues("memberAttribute_" + memberAttribute.getId());
				List<String> options = parameterValues != null ? Arrays.asList(parameterValues) : null;
				if (memberAttribute.getIsRequired() && (options == null || options.isEmpty())) {
					renderJson(ERROR_VIEW);
					return;
				}
				member.setAttributeValue(memberAttribute, options);
			}
		}
		memberService.update(member);
		addFlashMessage(SUCCESS_MESSAGE);
		edit();
	}

}