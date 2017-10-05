/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop.member;

import com.jfinal.aop.Before;
import com.jfinalshop.controller.shop.BaseShopController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.ConsultationService;
import com.jfinalshop.service.CouponCodeService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.MessageService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.ProductNotifyService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.service.ReviewService;

/**
 * Controller - 会员中心
 * 
 * 
 * 
 */
@Before(MemberInterceptor.class)
public class MemberController extends BaseShopController {

	/** 最新订单数 */
	private static final int NEW_ORDER_COUNT = 6;

	private MemberService memberService = enhance(MemberService.class);
	private OrderService orderService = enhance(OrderService.class);
	private CouponCodeService couponCodeService = enhance(CouponCodeService.class);
	private MessageService messageService = enhance(MessageService.class);
	private ProductService productService = enhance(ProductService.class);
	private ProductNotifyService productNotifyService = enhance(ProductNotifyService.class);
	private ReviewService reviewService = enhance(ReviewService.class);
	private ConsultationService consultationService = enhance(ConsultationService.class);

	/**
	 * 首页
	 */
	public void index() {
		//Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent(getRequest());
		setAttr("waitingPaymentOrderCount", orderService.waitingPaymentCount(member));
		setAttr("waitingShippingOrderCount", orderService.waitingShippingCount(member));
		setAttr("messageCount", messageService.count(member, false));
		setAttr("couponCodeCount", couponCodeService.count(null, member, null, false, false));
		setAttr("favoriteCount", productService.count(member, null, null, null, null, null, null));
		setAttr("productNotifyCount", productNotifyService.count(member, null, null, null));
		setAttr("reviewCount", reviewService.count(member, null, null, null));
		setAttr("consultationCount", consultationService.count(member, null, null));
		setAttr("newOrders", orderService.findList(member, NEW_ORDER_COUNT, null, null));
		render("/shop/member/index.html");
	}

}