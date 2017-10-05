/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop.member;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.controller.shop.BaseShopController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Coupon;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.CouponCodeService;
import com.jfinalshop.service.CouponService;
import com.jfinalshop.service.MemberService;

/**
 * Controller - 会员中心 - 优惠码
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/couponCode")
@Before(MemberInterceptor.class)
public class CouponCodeController extends BaseShopController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	private MemberService memberService = enhance(MemberService.class);
	private CouponService couponService = enhance(CouponService.class);
	private CouponCodeService couponCodeService = enhance(CouponCodeService.class);

	/**
	 * 兑换
	 */
	public void exchange() {
		Integer pageNumber = getParaToInt("pageNumber");
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", couponService.findPage(true, true, false, pageable));
		render("/shop/member/coupon_code/exchange.html");
	}

	/**
	 * 兑换提交
	 */
	public void submit() {
		Long id = getParaToLong("id");
		Coupon coupon = couponService.find(id);
		if (coupon == null || !coupon.getIsEnabled() || !coupon.getIsExchange() || coupon.hasExpired()) {
			 renderJson(ERROR_MESSAGE);
			 return;
		}
		Member member = memberService.getCurrent(getRequest());
		if (member.getPoint() < coupon.getPoint()) {
			renderJson(Message.warn("shop.member.couponCode.point"));
			 return;
		}
		couponCodeService.exchange(coupon, member);
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent(getRequest());
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", couponCodeService.findPage(member, pageable));
		render("/shop/member/coupon_code/list.html");
	}

}