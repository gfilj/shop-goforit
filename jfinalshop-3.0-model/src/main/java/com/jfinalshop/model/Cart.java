package com.jfinalshop.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.common.Setting;
import com.jfinalshop.model.base.BaseCart;
import com.jfinalshop.utils.SettingUtils;

/**
 * Dao - 购物车
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Cart extends BaseCart<Cart> {
	public static final Cart dao = new Cart();
	
	/** 超时时间 */
	public static final int TIMEOUT = 604800;

	/** 最大商品数 */
	public static final Integer MAX_PRODUCT_COUNT = 100;

	/** "ID"Cookie名称 */
	public static final String ID_COOKIE_NAME = "cartId";

	/** "密钥"Cookie名称 */
	public static final String KEY_COOKIE_NAME = "cartKey";
	
	/** 购物车项 */
	private List<CartItem> cartItems = new ArrayList<CartItem>();

	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public Member getMember() {
		return Member.dao.findById(getMemberId());
	}

	
	/**
	 * 获取购物车项
	 * 
	 * @return 购物车项
	 */
	public List<CartItem> getCartItems() {
		String sql = "SELECT * FROM cart_item  WHERE `cart_id` = ?";
		if (cartItems.isEmpty()) {
			cartItems = CartItem.dao.find(sql, getId());
		}
		return cartItems;
	}
	
	/**
	 * 设置购物车项
	 * 
	 * @param cartItems
	 *            购物车项
	 */
	public void setCartItems(List<CartItem> cartItems) {
		this.cartItems = cartItems;
	}
	
	/**
	 * 获取商品重量
	 * 
	 * @return 商品重量
	 */
	
	public int getWeight() {
		int weight = 0;
		if (getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				if (cartItem != null) {
					weight += cartItem.getWeight();
				}
			}
		}
		return weight;
	}

	/**
	 * 获取商品数量
	 * 
	 * @return 商品数量
	 */
	
	public int getQuantity() {
		int quantity = 0;
		if (getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				if (cartItem != null && cartItem.getQuantity() != null) {
					quantity += cartItem.getQuantity();
				}
			}
		}
		return quantity;
	}

	/**
	 * 获取赠送积分
	 * 
	 * @return 赠送积分
	 */
	
	public long getPoint() {
		long point = 0L;
		if (getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				if (cartItem != null) {
					point += cartItem.getPoint();
				}
			}
		}
		return point;
	}

	/**
	 * 获取赠送积分增加值
	 * 
	 * @return 赠送积分增加值
	 */
	
	public long getAddedPoint() {
		long originalPoint = 0L;
		long currentPoint = 0L;
		for (Promotion promotion : getPromotions()) {
			if (promotion != null) {
				int promotionQuantity = getQuantity(promotion);
				long originalPromotionPoint = getTempPoint(promotion);
				long currentPromotionPoint = promotion.calculatePoint(promotionQuantity, originalPromotionPoint);
				originalPoint += originalPromotionPoint;
				currentPoint += currentPromotionPoint;
				Set<CartItem> cartItems = getCartItems(promotion);
				for (CartItem cartItem : cartItems) {
					if (cartItem != null && cartItem.getTempPoint() != null) {
						long tempPoint;
						if (originalPromotionPoint > 0) {
							tempPoint = (long) (currentPromotionPoint / (double) originalPromotionPoint * cartItem.getTempPoint());
						} else {
							tempPoint = (currentPromotionPoint - originalPromotionPoint) / promotionQuantity;
						}
						cartItem.setTempPoint(tempPoint > 0 ? tempPoint : 0L);
					}
				}
			}
		}
		if (getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				if (cartItem != null) {
					cartItem.setTempPoint(null);
				}
			}
		}
		long addedPoint = currentPoint - originalPoint;
		return addedPoint > 0 ? addedPoint : 0L;
	}

	/**
	 * 获取有效赠送积分
	 * 
	 * @return 有效赠送积分
	 */
	
	public long getEffectivePoint() {
		long effectivePoint = getPoint() + getAddedPoint();
		return effectivePoint > 0L ? effectivePoint : 0L;
	}

	/**
	 * 获取商品价格
	 * 
	 * @return 商品价格
	 */
	
	public BigDecimal getPrice() {
		BigDecimal price = new BigDecimal(0);
		if (getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				if (cartItem != null && cartItem.getSubtotal() != null) {
					price = price.add(cartItem.getSubtotal());
				}
			}
		}
		return price;
	}

	/**
	 * 获取折扣
	 * 
	 * @return 折扣
	 */
	
	public BigDecimal getDiscount() {
		BigDecimal originalPrice = new BigDecimal(0);
		BigDecimal currentPrice = new BigDecimal(0);
		for (Promotion promotion : getPromotions()) {
			if (promotion != null) {
				int promotionQuantity = getQuantity(promotion);
				BigDecimal originalPromotionPrice = getTempPrice(promotion);
				BigDecimal currentPromotionPrice = promotion.calculatePrice(promotionQuantity, originalPromotionPrice);
				originalPrice = originalPrice.add(originalPromotionPrice);
				currentPrice = currentPrice.add(currentPromotionPrice);
				Set<CartItem> cartItems = getCartItems(promotion);
				for (CartItem cartItem : cartItems) {
					if (cartItem != null && cartItem.getTempPrice() != null) {
						BigDecimal tempPrice;
						if (originalPromotionPrice.compareTo(new BigDecimal(0)) > 0) {
							tempPrice = currentPromotionPrice.divide(originalPromotionPrice, 50, RoundingMode.DOWN).multiply(cartItem.getTempPrice());
						} else {
							tempPrice = new BigDecimal(0);
						}
						cartItem.setTempPrice(tempPrice.compareTo(new BigDecimal(0)) > 0 ? tempPrice : new BigDecimal(0));
					}
				}
			}
		}
		if (getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				if (cartItem != null) {
					cartItem.setTempPrice(null);
				}
			}
		}
		Setting setting = SettingUtils.get();
		BigDecimal discount = setting.setScale(originalPrice.subtract(currentPrice));
		return discount.compareTo(new BigDecimal(0)) > 0 ? discount : new BigDecimal(0);
	}

	/**
	 * 获取有效商品价格
	 * 
	 * @return 有效商品价格
	 */
	
	public BigDecimal getEffectivePrice() {
		BigDecimal effectivePrice = getPrice().subtract(getDiscount());
		return effectivePrice.compareTo(new BigDecimal(0)) > 0 ? effectivePrice : new BigDecimal(0);
	}

	/**
	 * 获取赠品项
	 * 
	 * @return 赠品项
	 */
	
	public Set<GiftItem> getGiftItems() {
		Set<GiftItem> giftItems = new HashSet<GiftItem>();
		for (Promotion promotion : getPromotions()) {
			if (promotion.getGiftItems() != null) {
				for (final GiftItem giftItem : promotion.getGiftItems()) {
					GiftItem target = (GiftItem) CollectionUtils.find(giftItems, new Predicate() {
						public boolean evaluate(Object object) {
							GiftItem other = (GiftItem) object;
							return other != null && other.getGift().equals(giftItem.getGift());
						}
					});
					if (target != null) {
						target.setQuantity(target.getQuantity() + giftItem.getQuantity());
					} else {
						giftItems.add(giftItem);
					}
				}
			}
		}
		return giftItems;
	}

	/**
	 * 获取促销
	 * 
	 * @return 促销
	 */
	
	public Set<Promotion> getPromotions() {
		Set<Promotion> allPromotions = new HashSet<Promotion>();
		if (getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				if (cartItem != null && cartItem.getProduct() != null) {
					allPromotions.addAll(cartItem.getProduct().getValidPromotions());
				}
			}
		}
		Set<Promotion> promotions = new TreeSet<Promotion>();
		for (Promotion promotion : allPromotions) {
			if (isValid(promotion)) {
				promotions.add(promotion);
			}
		}
		return promotions;
	}

	/**
	 * 获取促销购物车项
	 * 
	 * @param promotion
	 *            促销
	 * @return 促销购物车项
	 */
	
	private Set<CartItem> getCartItems(Promotion promotion) {
		Set<CartItem> cartItems = new HashSet<CartItem>();
		if (promotion != null && getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				if (cartItem != null && cartItem.getProduct() != null && cartItem.getProduct().isValid(promotion)) {
					cartItems.add(cartItem);
				}
			}
		}
		return cartItems;
	}

	/**
	 * 获取促销商品数量
	 * 
	 * @param promotion
	 *            促销
	 * @return 促销商品数量
	 */
	
	private int getQuantity(Promotion promotion) {
		int quantity = 0;
		for (CartItem cartItem : getCartItems(promotion)) {
			if (cartItem != null && cartItem.getQuantity() != null) {
				quantity += cartItem.getQuantity();
			}
		}
		return quantity;
	}

	/**
	 * 获取促销商品赠送积分
	 * 
	 * @param promotion
	 *            促销
	 * @return 促销商品赠送积分
	 */
	
	public long getPoint(Promotion promotion) {
		long point = 0L;
		for (CartItem cartItem : getCartItems(promotion)) {
			if (cartItem != null) {
				point += cartItem.getPoint();
			}
		}
		return point;
	}

	/**
	 * 获取促销商品临时赠送积分
	 * 
	 * @param promotion
	 *            促销
	 * @return 促销商品临时赠送积分
	 */
	
	private long getTempPoint(Promotion promotion) {
		long tempPoint = 0L;
		for (CartItem cartItem : getCartItems(promotion)) {
			if (cartItem != null && cartItem.getTempPoint() != null) {
				tempPoint += cartItem.getTempPoint();
			}
		}
		return tempPoint;
	}

	/**
	 * 获取促销商品价格
	 * 
	 * @param promotion
	 *            促销
	 * @return 促销商品价格
	 */
	
	private BigDecimal getPrice(Promotion promotion) {
		BigDecimal price = new BigDecimal(0);
		for (CartItem cartItem : getCartItems(promotion)) {
			if (cartItem != null && cartItem.getSubtotal() != null) {
				price = price.add(cartItem.getSubtotal());
			}
		}
		return price;
	}

	/**
	 * 获取促销商品临时价格
	 * 
	 * @param promotion
	 *            促销
	 * @return 促销商品临时价格
	 */
	
	private BigDecimal getTempPrice(Promotion promotion) {
		BigDecimal tempPrice = new BigDecimal(0);
		for (CartItem cartItem : getCartItems(promotion)) {
			if (cartItem != null && cartItem.getTempPrice() != null) {
				tempPrice = tempPrice.add(cartItem.getTempPrice());
			}
		}
		return tempPrice;
	}

	/**
	 * 判断促销是否有效
	 * 
	 * @param promotion
	 *            促销
	 * @return 促销是否有效
	 */
	
	private boolean isValid(Promotion promotion) {
		if (promotion == null || !promotion.hasBegun() || promotion.hasEnded()) {
			return false;
		}
		if (promotion.getMemberRanks() == null || promotion.getMemberRanks().isEmpty() || getMember() == null || getMember().getMemberRank() == null || !promotion.getMemberRanks().contains(getMember().getMemberRank())) {
			return false;
		}
		Integer quantity = getQuantity(promotion);
		if ((promotion.getMinimumQuantity() != null && promotion.getMinimumQuantity() > quantity) || (promotion.getMaximumQuantity() != null && promotion.getMaximumQuantity() < quantity)) {
			return false;
		}
		BigDecimal price = getPrice(promotion);
		if ((promotion.getMinimumPrice() != null && promotion.getMinimumPrice().compareTo(price) > 0) || (promotion.getMaximumPrice() != null && promotion.getMaximumPrice().compareTo(price) < 0)) {
			return false;
		}
		return true;
	}

	/**
	 * 判断优惠券是否有效
	 * 
	 * @param coupon
	 *            优惠券
	 * @return 优惠券是否有效
	 */
	
	public boolean isValid(Coupon coupon) {
		if (coupon == null || !coupon.getIsEnabled() || !coupon.hasBegun() || coupon.hasExpired()) {
			return false;
		}
		if ((coupon.getMinimumQuantity() != null && coupon.getMinimumQuantity() > getQuantity()) || (coupon.getMaximumQuantity() != null && coupon.getMaximumQuantity() < getQuantity())) {
			return false;
		}
		if ((coupon.getMinimumPrice() != null && coupon.getMinimumPrice().compareTo(getEffectivePrice()) > 0) || (coupon.getMaximumPrice() != null && coupon.getMaximumPrice().compareTo(getEffectivePrice()) < 0)) {
			return false;
		}
		return true;
	}

	/**
	 * 获取购物车项
	 * 
	 * @param product
	 *            商品
	 * @return 购物车项
	 */
	
	public CartItem getCartItem(Product product) {
		if (product != null && getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				if (cartItem != null && cartItem.getProduct() != null && cartItem.getProduct().equals(product)) {
					return cartItem;
				}
			}
		}
		return null;
	}

	/**
	 * 判断是否包含商品
	 * 
	 * @param product
	 *            商品
	 * @return 是否包含商品
	 */
	
	public boolean contains(Product product) {
		if (product != null && getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				if (cartItem != null && cartItem.getProduct() != null && cartItem.getProduct().equals(product)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取令牌
	 * 
	 * @return 令牌
	 */
	
	public String getToken() {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(17, 37).append(getCartKey());
		if (getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				hashCodeBuilder.append(cartItem.getProduct()).append(cartItem.getQuantity()).append(cartItem.getPrice());
			}
		}
		return DigestUtils.md5Hex(hashCodeBuilder.toString());
	}

	/**
	 * 获取是否库存不足
	 * 
	 * @return 是否库存不足
	 */
	
	public boolean getIsLowStock() {
		if (getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				if (cartItem != null && cartItem.getIsLowStock()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断是否已过期
	 * 
	 * @return 是否已过期
	 */
	public boolean hasExpired() {
		return new Date().after(DateUtils.addSeconds(getLastUpdatedDate(), TIMEOUT));
	}
	
	/**
	 * 判断是否允许使用优惠券
	 * 
	 * @return 是否允许使用优惠券
	 */
	public boolean isCouponAllowed() {
		for (Promotion promotion : getPromotions()) {
			if (promotion != null && !promotion.getIsCouponAllowed()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否为空
	 * 
	 * @return 是否为空
	 */
	public boolean isEmpty() {
		return getCartItems() == null || getCartItems().isEmpty();
	}
	
	/**
	 * 清除过期购物车
	 */
	public void evictExpired() {
		String sql = "DELETE FROM cart WHERE last_updated_date <= ?";
		Db.update(sql, DateUtils.addSeconds(new Date(), -Cart.TIMEOUT));
	}
}
