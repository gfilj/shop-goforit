package com.jfinalshop.service;

import com.jfinalshop.model.CartItem;

/**
 * Service - 购物车项
 * 
 * 
 * 
 */
public class CartItemService  extends BaseService<CartItem> {
	
	public CartItemService() {
		super(CartItem.class);
	}
	
	/**
	 * 保存购物车
	 * 
	 * @param model
	 *            购物车对象
	 */
	public boolean save(CartItem cartItem) {
		cartItem.setCreationDate(getSysDate());
		cartItem.setLastUpdatedDate(getSysDate());
		cartItem.setDeleteFlag(false);
		return cartItem.save();
	}

}
