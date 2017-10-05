/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinalshop.common.Message;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.CartItem;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.service.CartItemService;
import com.jfinalshop.service.CartService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.utils.WebUtils;

/**
 * Controller - 购物车
 * 
 * 
 * 
 */
@Before(MemberInterceptor.class)
public class CartController extends BaseShopController {

	private MemberService memberService = enhance(MemberService.class);
	private ProductService productService = enhance(ProductService.class);
	private CartService cartService = enhance(CartService.class);
	private CartItemService cartItemService = enhance(CartItemService.class);

	/**
	 * 添加
	 */
	public void add() {
		Long id = getParaToLong("id");
		Integer quantity = getParaToInt("quantity");
		
		if (quantity == null || quantity < 1) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		Product product = productService.find(id);
		if (product == null) {
			renderJson(Message.warn("shop.cart.productNotExsit"));
			return;
		}
		if (!product.getIsMarketable()) {
			renderJson(Message.warn("shop.cart.productNotMarketable"));
			return;
		}
		if (product.getIsGift()) {
			renderJson(Message.warn("shop.cart.notForSale"));
			return;
		}

		Cart cart = cartService.getCurrent(getRequest());
		Member member = memberService.getCurrent(getRequest());

		if (cart == null) {
			cart = new Cart();
			cart.setCartKey(UUID.randomUUID().toString() + DigestUtils.md5Hex(RandomStringUtils.randomAlphabetic(30)));
			cart.setMemberId(member.getId());
			cart.setCreateBy(memberService.getCurrentUsername(getRequest()));
			cartService.save(cart);
		}

		if (Cart.MAX_PRODUCT_COUNT != null && cart.getCartItems().size() >= Cart.MAX_PRODUCT_COUNT) {
			renderJson(Message.warn("shop.cart.addCountNotAllowed", Cart.MAX_PRODUCT_COUNT));
			return;
		}

		if (cart.contains(product)) {
			CartItem cartItem = cart.getCartItem(product);
			if (CartItem.MAX_QUANTITY != null && cartItem.getQuantity() + quantity > CartItem.MAX_QUANTITY) {
				renderJson(Message.warn("shop.cart.maxCartItemQuantity", CartItem.MAX_QUANTITY));
				return;
			}
			if (product.getStock() != null && cartItem.getQuantity() + quantity > product.getAvailableStock()) {
				renderJson(Message.warn("shop.cart.productLowStock"));
				return;
			}
			cartItem.add(quantity);
			cartItemService.update(cartItem);
		} else {
			if (CartItem.MAX_QUANTITY != null && quantity > CartItem.MAX_QUANTITY) {
				renderJson(Message.warn("shop.cart.maxCartItemQuantity", CartItem.MAX_QUANTITY));
				return;
			}
			if (product.getStock() != null && quantity > product.getAvailableStock()) {
				renderJson(Message.warn("shop.cart.productLowStock"));
				return;
			}
			CartItem cartItem = new CartItem();
			cartItem.setQuantity(quantity);
			cartItem.setProductId(product.getId());
			cartItem.setCartId(cart.getId());
			cartItem.setCreateBy(memberService.getCurrentUsername(getRequest()));
			cartItemService.save(cartItem);
			cart.getCartItems().add(cartItem);
		}

		if (member == null) {
			WebUtils.addCookie(getRequest(), getResponse(), Cart.ID_COOKIE_NAME, cart.getId().toString(), Cart.TIMEOUT);
			WebUtils.addCookie(getRequest(), getResponse(), Cart.KEY_COOKIE_NAME, cart.getCartKey(), Cart.TIMEOUT);
		}
		renderJson(Message.success("shop.cart.addSuccess", cart.getQuantity(), currency(cart.getEffectivePrice(), true, false)));
	}

	/**
	 * 列表
	 */
	@Clear
	public void list() {
		setAttr("cart", cartService.getCurrent(getRequest()));
		render("/shop/cart/list.html");
	}

	/**
	 * 编辑
	 */
	@Clear
	public void edit() {
		Long id = getParaToLong("id");
		Integer quantity = getParaToInt("quantity");
		Map<String, Object> data = new HashMap<String, Object>();
		if (quantity == null || quantity < 1) {
			data.put("message", ERROR_MESSAGE);
			renderJson(data);
			return;
		}
		Cart cart = cartService.getCurrent(getRequest());
		if (cart == null || cart.isEmpty()) {
			data.put("message", Message.error("shop.cart.notEmpty"));
			renderJson(data);
			return;
		}
		CartItem cartItem = cartItemService.find(id);
		List<CartItem> cartItems = cart.getCartItems();
		if (cartItem == null || cartItems == null || !cartItems.contains(cartItem)) {
			data.put("message", Message.error("shop.cart.cartItemNotExsit"));
			renderJson(data);
			return;
		}
		if (CartItem.MAX_QUANTITY != null && quantity > CartItem.MAX_QUANTITY) {
			data.put("message", Message.warn("shop.cart.maxCartItemQuantity", CartItem.MAX_QUANTITY));
			renderJson(data);
			return;
		}
		Product product = cartItem.getProduct();
		if (product.getStock() != null && quantity > product.getAvailableStock()) {
			data.put("message", Message.warn("shop.cart.productLowStock"));
			renderJson(data);
			return;
		}
		cartItem.setQuantity(quantity);
		cartItem.setLastUpdatedBy(memberService.getCurrentUsername(getRequest()));
		cartItemService.update(cartItem);

		data.put("message", SUCCESS_MESSAGE);
		data.put("subtotal", cartItem.getSubtotal());
		data.put("isLowStock", cartItem.getIsLowStock());
		data.put("quantity", cart.getQuantity());
		data.put("effectivePoint", cart.getEffectivePoint());
		data.put("effectivePrice", cart.getEffectivePrice());
		data.put("promotions", cart.getPromotions());
		data.put("giftItems", cart.getGiftItems());
		renderJson(data);
	}

	/**
	 * 删除
	 */
	@Clear
	public void delete() {
		Long id = getParaToLong("id");
		Map<String, Object> data = new HashMap<String, Object>();
		Cart cart = cartService.getCurrent(getRequest());
		if (cart == null || cart.isEmpty()) {
			data.put("message", Message.error("shop.cart.notEmpty"));
			renderJson(data);
			return;
		}
		CartItem cartItem = cartItemService.find(id);
		List<CartItem> cartItems = cart.getCartItems();
		if (cartItem == null || cartItems == null || !cartItems.contains(cartItem)) {
			data.put("message", Message.error("shop.cart.cartItemNotExsit"));
			renderJson(data);
			return;
		}
		cartItems.remove(cartItem);
		cartItemService.delete(cartItem);

		data.put("message", SUCCESS_MESSAGE);
		data.put("quantity", cart.getQuantity());
		data.put("effectivePoint", cart.getEffectivePoint());
		data.put("effectivePrice", cart.getEffectivePrice());
		data.put("promotions", cart.getPromotions());
		data.put("isLowStock", cart.getIsLowStock());
		renderJson(data);
	}

	/**
	 * 清空
	 */
	@Clear
	public void clear() {
		Cart cart = cartService.getCurrent(getRequest());
		cartService.clear(cart.getId());
		renderJson(SUCCESS_MESSAGE);
	}

}