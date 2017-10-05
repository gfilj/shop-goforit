package com.jfinalshop.model;

import java.math.BigDecimal;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.common.Setting;
import com.jfinalshop.model.base.BaseCartItem;
import com.jfinalshop.utils.SettingUtils;

/**
 * Dao - 购物车项
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class CartItem extends BaseCartItem<CartItem> {
	public static final CartItem dao = new CartItem();
	
	/** 最大数量 */
	public static final Integer MAX_QUANTITY = 10000;
	
	/** 临时商品价格 */
	private BigDecimal tempPrice;

	/** 临时赠送积分 */
	private Long tempPoint;

	/**
	 * 获取临时商品价格
	 * 
	 * @return 临时商品价格
	 */
	public BigDecimal getTempPrice() {
		if (tempPrice == null) {
			return getSubtotal();
		}
		return tempPrice;
	}

	/**
	 * 设置临时商品价格
	 * 
	 * @param tempPrice
	 *            临时商品价格
	 */
	public void setTempPrice(BigDecimal tempPrice) {
		this.tempPrice = tempPrice;
	}
	
	/**
	 * 获取临时赠送积分
	 * 
	 * @return 临时赠送积分
	 */
	public Long getTempPoint() {
		if (tempPoint == null) {
			return getPoint();
		}
		return tempPoint;
	}

	/**
	 * 设置临时赠送积分
	 * 
	 * @param tempPoint
	 *            临时赠送积分
	 */
	public void setTempPoint(Long tempPoint) {
		this.tempPoint = tempPoint;
	}
	
	/**
	 * 获取购物车
	 * 
	 * @return 购物车
	 */
	public Cart getCart() {
		return Cart.dao.findById(getProductId());
	}
	
	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public Product getProduct() {
		return Product.dao.findById(getProductId());
	}
	
	/**
	 * 获取赠送积分
	 * 
	 * @return 赠送积分
	 */
	public long getPoint() {
		if (getProduct() != null && getProduct().getPoint() != null && getQuantity() != null) {
			return getProduct().getPoint() * getQuantity();
		} else {
			return 0L;
		}
	}

	/**
	 * 获取商品重量
	 * 
	 * @return 商品重量
	 */
	public int getWeight() {
		if (getProduct() != null && getProduct().getWeight() != null && getQuantity() != null) {
			return getProduct().getWeight() * getQuantity();
		} else {
			return 0;
		}
	}

	/**
	 * 获取价格
	 * 
	 * @return 价格
	 */
	public BigDecimal getPrice() {
		if (getProduct() != null && getProduct().getPrice() != null) {
			Setting setting = SettingUtils.get();
			if (getCart() != null && getCart().getMember() != null && getCart().getMember().getMemberRank() != null) {
				MemberRank memberRank = getCart().getMember().getMemberRank();
				Map<MemberRank, BigDecimal> memberPrice = getProduct().getMemberPrice();
				if (memberPrice != null && !memberPrice.isEmpty()) {
					if (memberPrice.containsKey(memberRank)) {
						return setting.setScale(memberPrice.get(memberRank));
					}
				}
				if (memberRank.getScale() != null) {
					return setting.setScale(getProduct().getPrice().multiply(new BigDecimal(memberRank.getScale())));
				}
			}
			return setting.setScale(getProduct().getPrice());
		} else {
			return new BigDecimal(0);
		}
	}

	/**
	 * 获取小计
	 * 
	 * @return 小计
	 */
	public BigDecimal getSubtotal() {
		if (getQuantity() != null) {
			return getPrice().multiply(new BigDecimal(getQuantity()));
		} else {
			return new BigDecimal(0);
		}
	}

	/**
	 * 获取是否库存不足
	 * 
	 * @return 是否库存不足
	 */
	public boolean getIsLowStock() {
		if (getQuantity() != null && getProduct() != null && getProduct().getStock() != null && getQuantity() > getProduct().getAvailableStock()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 增加商品数量
	 * 
	 * @param quantity
	 *            数量
	 */
	public void add(int quantity) {
		if (quantity > 0) {
			if (getQuantity() != null) {
				setQuantity(getQuantity() + quantity);
			} else {
				setQuantity(quantity);
			}
		}
	}
	
	/**
	 * 删除
	 * @param cartId
	 * @return
	 */
	public boolean delete(Long cartId) {
		return Db.deleteById("cart_item", "cart_id", cartId);
	}
}
