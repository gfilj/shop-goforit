/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop.member;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.controller.shop.BaseShopController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.ConsultationService;
import com.jfinalshop.service.MemberService;

/**
 * Controller - 会员中心 - 咨询
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/consultation")
@Before(MemberInterceptor.class)
public class ConsultationController extends BaseShopController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	private MemberService memberService = enhance(MemberService.class);
	private ConsultationService consultationService = enhance(ConsultationService.class);

	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent(getRequest());
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", consultationService.findPage(member, null, null, pageable));
		render("/shop/member/consultation/list.html");
	}

}