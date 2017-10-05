package com.jfinalshop.controller.admin;

import java.util.Arrays;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberAttribute;
import com.jfinalshop.model.MemberAttribute.Type;
import com.jfinalshop.service.MemberAttributeService;

/**
 * Controller - 会员注册项
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/memberAttribute")
public class MemberAttributeController extends BaseAdminController {

	private MemberAttributeService memberAttributeService = enhance(MemberAttributeService.class);
	private MemberAttribute memberAttribute;
	String [] options;
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<MemberAttribute> page = memberAttributeService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/member_attribute/list.html");
	}
	
	
	/**
	 * 添加
	 */
	public void add() {
		if (memberAttributeService.count() - 8 >= Member.ATTRIBUTE_VALUE_PROPERTY_COUNT) {
			addFlashMessage(Message.warn("admin.memberAttribute.addCountNotAllowed", Member.ATTRIBUTE_VALUE_PROPERTY_COUNT));
		}
		render("/admin/member_attribute/add.html");
	}
	
	
	/**
	 * 保存
	 */
	public void save() {
		memberAttribute = getModel(MemberAttribute.class);
		Type type = getPara("type") != null ? Type.valueOf(getPara("type")) : null;
		memberAttribute.setType(type.ordinal());
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		Boolean isRequired = getParaToBoolean("isRequired", false);
		memberAttribute.setIsEnabled(isEnabled);
		memberAttribute.setIsRequired(isRequired);
		if (memberAttribute.getTypeValues() == Type.select || memberAttribute.getTypeValues() == Type.checkbox) {
			options = getParaValues("options");
			memberAttribute.setOptions(Arrays.asList(options));
		} 
		Integer propertyIndex = memberAttributeService.findUnusedPropertyIndex();
		if (propertyIndex == null) {
			renderJson(ERROR_VIEW);
			return;
		}
		memberAttribute.setPropertyIndex(propertyIndex);
		memberAttributeService.save(memberAttribute);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("memberAttribute", memberAttributeService.find(id));
		render("/admin/member_attribute/edit.html");
	}
	
	/**
	 * 更新
	 */
	public void update() {
		memberAttribute = getModel(MemberAttribute.class);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		Boolean isRequired = getParaToBoolean("isRequired", false);
		memberAttribute.setIsEnabled(isEnabled);
		memberAttribute.setIsRequired(isRequired);
		MemberAttribute pMemberAttribute = memberAttributeService.find(memberAttribute.getId());
		if (pMemberAttribute == null) {
			renderJson(ERROR_VIEW);
			return;
		}
		if (pMemberAttribute.getTypeValues() == Type.select || pMemberAttribute.getTypeValues() == Type.checkbox) {
			options = getParaValues("options");
			memberAttribute.setOptions(Arrays.asList(options));
		} 
		memberAttributeService.update(memberAttribute);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			memberAttributeService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
