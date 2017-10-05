/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop;

import java.util.UUID;

import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.common.ResourceNotFoundException;
import com.jfinalshop.common.Setting;
import com.jfinalshop.common.Setting.ConsultationAuthority;
import com.jfinalshop.model.Consultation;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.plugin.shiro.core.SubjectKit;
import com.jfinalshop.service.ConsultationService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.utils.SettingUtils;

/**
 * Controller - 咨询
 * 
 * 
 * 
 */
public class ConsultationController extends BaseShopController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	private ConsultationService consultationService = enhance(ConsultationService.class);
	private ProductService productService = enhance(ProductService.class);
	private MemberService memberService = enhance(MemberService.class);

	/**
	 * 发表
	 */
	public void add() {
		Long id = getParaToLong("id");
		Setting setting = SettingUtils.get();
		if (!setting.getIsConsultationEnabled()) {
			throw new ResourceNotFoundException();
		}
		Product product = productService.find(id);
		if (product == null) {
			throw new ResourceNotFoundException();
		}
		setAttr("product", product);
		setAttr("captchaId", UUID.randomUUID().toString());
		render("/shop/consultation/add.html");
	}

	/**
	 * 内容
	 */
	public void content() {
		Long id = getParaToLong("id");
		Integer pageNumber = getParaToInt("pageNumber");
		
		Setting setting = SettingUtils.get();
		if (!setting.getIsConsultationEnabled()) {
			throw new ResourceNotFoundException();
		}
		Product product = productService.find(id);
		if (product == null) {
			throw new ResourceNotFoundException();
		}
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("product", product);
		setAttr("page", consultationService.findPage(null, product, true, pageable));
		render("/shop/consultation/content.html");
	}

	/**
	 * 保存
	 */
	public void save() {
		String captcha = getPara("captcha");
		Long id = getParaToLong("id");
		String content = getPara("content");
		
		if (!SubjectKit.doCaptcha("captcha", captcha)) {
			renderJson(Message.error("shop.captcha.invalid"));
			return;
		}
		Setting setting = SettingUtils.get();
		if (!setting.getIsConsultationEnabled()) {
			renderJson(Message.error("shop.consultation.disabled"));
			return;
		}
		Member member = memberService.getCurrent(getRequest());
		if (setting.getConsultationAuthority() != ConsultationAuthority.anyone && member == null) {
			renderJson(Message.error("shop.consultation.accessDenied"));
			return;
		}
		Product product = productService.find(id);
		if (product == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		Consultation consultation = new Consultation();
		consultation.setContent(content);
		consultation.setIp(getRequest().getRemoteAddr());
		consultation.setMemberId(member.getId());
		consultation.setProductId(product.getId());
		if (setting.getIsConsultationCheck()) {
			consultation.setIsShow(false);
			consultationService.save(consultation);
			renderJson(Message.success("shop.consultation.check"));
		} else {
			consultation.setIsShow(true);
			consultationService.save(consultation);
			renderJson(Message.success("shop.consultation.success"));
		}
	}

}