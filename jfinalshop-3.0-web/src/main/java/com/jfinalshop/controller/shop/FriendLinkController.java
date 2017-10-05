/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop;

import com.jfinalshop.model.FriendLink.Type;
import com.jfinalshop.service.FriendLinkService;


/**
 * Controller - 友情链接
 * 
 * 
 * 
 */
public class FriendLinkController extends BaseShopController {

	private FriendLinkService friendLinkService = enhance(FriendLinkService.class);

	/**
	 * 首页
	 */
	public void index() {
		setAttr("textFriendLinks", friendLinkService.findList(Type.text));
		setAttr("imageFriendLinks", friendLinkService.findList(Type.image));
		render("/shop/friend_link/index.html");
	}

}