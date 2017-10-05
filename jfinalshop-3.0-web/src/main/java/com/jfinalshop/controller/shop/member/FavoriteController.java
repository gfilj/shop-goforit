package com.jfinalshop.controller.shop.member;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.controller.shop.BaseShopController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberFavoriteProduct;
import com.jfinalshop.model.Product;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ProductService;

/**
 * Controller - 会员中心 - 商品收藏
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/favorite")
@Before(MemberInterceptor.class)
public class FavoriteController extends BaseShopController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	private MemberService memberService = enhance(MemberService.class);
	private ProductService productService = enhance(ProductService.class);

	/**
	 * 添加
	 */
	public void add() {
		Long id = getParaToLong("id");
		Product product = productService.find(id);
		if (product == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		Member member = memberService.getCurrent(getRequest());
		if (member.getFavoriteProducts().contains(product)) {
			renderJson(Message.warn("shop.member.favorite.exist"));
			return;
		}
		if (Member.MAX_FAVORITE_COUNT != null && member.getFavoriteProducts().size() >= Member.MAX_FAVORITE_COUNT) {
			renderJson(Message.warn("shop.member.favorite.addCountNotAllowed", Member.MAX_FAVORITE_COUNT));
			return;
		}
		//member.getFavoriteProducts().add(product);
		MemberFavoriteProduct memberFavoriteProduct = new MemberFavoriteProduct();
		memberFavoriteProduct.setFavoriteMembers(member.getId());
		memberFavoriteProduct.setFavoriteProducts(product.getId());
		memberFavoriteProduct.save();
		memberService.update(member);
		renderJson(Message.success("shop.member.favorite.success"));
	}

	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent(getRequest());
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", productService.findPage(member, pageable));
		render("/shop/member/favorite/list.html");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long id = getParaToLong("id");
		Product product = productService.find(id);
		if (product == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		Member member = memberService.getCurrent(getRequest());
		if (!member.getFavoriteProducts().contains(product)) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		//member.getFavoriteProducts().remove(product);
		MemberFavoriteProduct.dao.delete(id);
		memberService.update(member);
		renderJson(SUCCESS_MESSAGE);
	}

}