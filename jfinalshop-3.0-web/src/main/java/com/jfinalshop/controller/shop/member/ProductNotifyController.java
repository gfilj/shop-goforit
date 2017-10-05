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
import com.jfinalshop.model.ProductNotify;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ProductNotifyService;



/**
 * Controller - 会员中心 - 到货通知
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/productNotify")
@Before(MemberInterceptor.class)
public class ProductNotifyController extends BaseShopController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	ProductNotifyService productNotifyService = enhance(ProductNotifyService.class);
	MemberService memberService = enhance(MemberService.class);

	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent(getRequest());
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", productNotifyService.findPage(member, null, null, null, pageable));
		render("/shop/member/product_notify/list.html");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long id = getParaToLong("id");
		ProductNotify productNotify = productNotifyService.find(id);
		if (productNotify == null) {
			 renderJson(ERROR_MESSAGE);
			 return;
		}
		Member member = memberService.getCurrent(getRequest());
		if (!member.getProductNotifies().contains(productNotify)) {
			 renderJson(ERROR_MESSAGE);
			 return;
		}
		productNotifyService.delete(productNotify);
		renderJson(SUCCESS_MESSAGE);
	}

}