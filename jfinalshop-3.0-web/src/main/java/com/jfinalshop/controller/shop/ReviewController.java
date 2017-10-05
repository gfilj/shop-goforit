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
import com.jfinalshop.common.Setting.ReviewAuthority;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Review;
import com.jfinalshop.plugin.shiro.core.SubjectKit;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.service.ReviewService;
import com.jfinalshop.utils.SettingUtils;



/**
 * Controller - 评论
 * 
 * 
 * 
 */
public class ReviewController extends BaseShopController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	private ReviewService reviewService = enhance(ReviewService.class);
	private ProductService productService = enhance(ProductService.class);
	private MemberService memberService = enhance(MemberService.class);

	/**
	 * 发表
	 */
	public void add() {
		Long id = getParaToLong("id");
		Setting setting = SettingUtils.get();
		if (!setting.getIsReviewEnabled()) {
			throw new ResourceNotFoundException();
		}
		Product product = productService.find(id);
		if (product == null) {
			throw new ResourceNotFoundException();
		}
		setAttr("product", product);
		setAttr("captchaId", UUID.randomUUID().toString());
		render("/shop/review/add.html");
	}

	/**
	 * 内容
	 */
	public void content() {
		Long id = getParaToLong("id");
		Integer pageNumber = getParaToInt("pageNumber");
		
		Setting setting = SettingUtils.get();
		if (!setting.getIsReviewEnabled()) {
			throw new ResourceNotFoundException();
		}
		Product product = productService.find(id);
		if (product == null) {
			throw new ResourceNotFoundException();
		}
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("product", product);
		setAttr("page", reviewService.findPage(null, product, null, true, pageable));
		render("/shop/review/content.html");
	}

	/**
	 * 保存
	 */
	public void save() {
		String captcha = getPara("captcha");
		Long id = getParaToLong("id");
		Integer score = getParaToInt("score");
		String content = getPara("content");
		
		if (!SubjectKit.doCaptcha("captcha", captcha)) {
			renderJson(Message.error("shop.captcha.invalid"));
			return;
		}
		Setting setting = SettingUtils.get();
		if (!setting.getIsReviewEnabled()) {
			renderJson(Message.error("shop.review.disabled"));
		}
		Product product = productService.find(id);
		if (product == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		Member member = memberService.getCurrent(getRequest());
		if (setting.getReviewAuthority() != ReviewAuthority.anyone && member == null) {
			renderJson(Message.error("shop.review.accessDenied"));
			return;
		}
		if (setting.getReviewAuthority() == ReviewAuthority.purchased) {
			if (!productService.isPurchased(member, product)) {
				renderJson(Message.error("shop.review.noPurchased"));
				return;
			}
			if (reviewService.isReviewed(member, product)) {
				renderJson(Message.error("shop.review.reviewed"));
				return;
			}
		}
		Review review = new Review();
		review.setScore(score);
		review.setContent(content);
		review.setIp(getRequest().getRemoteAddr());
		review.setMemberId(member.getId());
		review.setProductId(product.getId());
		if (setting.getIsReviewCheck()) {
			review.setIsShow(false);
			reviewService.save(review);
			renderJson(Message.success("shop.review.check"));
		} else {
			review.setIsShow(true);
			reviewService.save(review);
			renderJson(Message.success("shop.review.success"));
		}
	}

}