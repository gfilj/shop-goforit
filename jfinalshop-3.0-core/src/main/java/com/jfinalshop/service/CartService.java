package com.jfinalshop.service;

import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.common.Principal;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.CartItem;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.utils.WebUtils;

/**
 * Service - 购物车
 * 
 * 
 * 
 */
public class CartService extends BaseService<Cart> {
	
	public CartService() {
		super(Cart.class);
	}
	
	/**
	 * 获取当前购物车
	 * 
	 * @return 当前购物车,若不存在则返回null
	 */
	public Cart getCurrent(HttpServletRequest request) {
		Principal principal = (Principal) request.getSession().getAttribute(MemberService.PRINCIPAL_ATTRIBUTE_NAME);
		Member member = principal != null ? Member.dao.findById(principal.getId()) : null;
		if (member != null) {
			Cart cart = member.getCart();
			if (cart != null) {
				if (!cart.hasExpired()) {
					if (!DateUtils.isSameDay(cart.getLastUpdatedDate(), new Date())) {
						cart.setLastUpdatedDate(new Date());
						cart.update();
					}
					return cart;
				} else {
					clear(cart.getId());
				}
			}
		} else {
			String id = WebUtils.getCookie(request, Cart.ID_COOKIE_NAME);
			String key = WebUtils.getCookie(request, Cart.KEY_COOKIE_NAME);
			if (StringUtils.isNotEmpty(id) && StringUtils.isNumeric(id) && StringUtils.isNotEmpty(key)) {
				Cart cart = Cart.dao.findById(Long.valueOf(id));
				if (cart != null && cart.getMember() == null && StringUtils.equals(cart.getCartKey(), key)) {
					if (!cart.hasExpired()) {
						if (!DateUtils.isSameDay(cart.getLastUpdatedDate(), new Date())) {
							cart.setLastUpdatedDate(new Date());
							cart.update();
						}
						return cart;
					} else {
						clear(cart.getId());
					}
				}
			}
		}
		return null;
	}

	/**
	 * 合并临时购物车至会员
	 * 
	 * @param member
	 *            会员
	 * @param cart
	 *            临时购物车
	 */
	public void merge(Member member, Cart cart) {
		if (member != null && cart != null && cart.getMember() == null) {
			Cart memberCart = member.getCart();
			if (memberCart != null) {
				for (Iterator<CartItem> iterator = cart.getCartItems().iterator(); iterator.hasNext();) {
					CartItem cartItem = iterator.next();
					Product product = cartItem.getProduct();
					if (memberCart.contains(product)) {
						if (Cart.MAX_PRODUCT_COUNT != null && memberCart.getCartItems().size() > Cart.MAX_PRODUCT_COUNT) {
							continue;
						}
						CartItem item = memberCart.getCartItem(product);
						item.add(cartItem.getQuantity());
						item.update();
					} else {
						if (Cart.MAX_PRODUCT_COUNT != null && memberCart.getCartItems().size() >= Cart.MAX_PRODUCT_COUNT) {
							continue;
						}
						iterator.remove();
						cartItem.setCartId(memberCart.getId());
						memberCart.getCartItems().add(cartItem);
						cartItem.update();
					}
				}
				Cart.dao.deleteById(cart.getId());
			} else {
				//member.setCart(cart);
				cart.setMemberId(member.getId());
				cart.update();
			}
		}
	}
	
	/**
	 * 保存购物车对象
	 * 
	 * @param model
	 *            购物车对象
	 */
	public boolean save(Cart cart) {
		cart.setCreationDate(getSysDate());
		cart.setLastUpdatedDate(getSysDate());
		cart.setDeleteFlag(false);
		return cart.save();
	}

	/**
	 * 清除过期购物车
	 */
	public void evictExpired() {
		Cart.dao.evictExpired();
	}
	
	/**
	 * 清空
	 */
	@Before(Tx.class)
	public void clear(Long cartId) {
		CartItem.dao.delete(cartId);
		Cart.dao.deleteById(cartId);
	}
}
