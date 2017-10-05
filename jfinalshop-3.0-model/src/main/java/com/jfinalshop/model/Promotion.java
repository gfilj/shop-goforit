package com.jfinalshop.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.common.Filter;
import com.jfinalshop.common.Order;
import com.jfinalshop.model.base.BasePromotion;
import com.jfinalshop.utils.ConditionUtil;
import com.jfinalshop.utils.DateUtil;
import com.jfinalshop.utils.FreemarkerUtils;

import freemarker.template.TemplateException;

/**
 * Dao - 促销
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Promotion extends BasePromotion<Promotion> {
	public static final Promotion dao = new Promotion();
	
	/** 访问路径前缀 */
	private static final String PATH_PREFIX = "/promotion/content";
	
	/** 允许参加会员等级 */
	private List<MemberRank> memberRanks = new ArrayList<MemberRank>();

	/** 允许参与商品分类 */
	private List<ProductCategory> productCategories = new ArrayList<ProductCategory>();

	/** 允许参与商品 */
	private List<Product> products = new ArrayList<Product>();

	/** 允许参与品牌 */
	private List<Brand> brands = new ArrayList<Brand>();

	/** 赠送优惠券 */
	private List<Coupon> coupons = new ArrayList<Coupon>();

	/** 赠品 */
	private List<GiftItem> giftItems = new ArrayList<GiftItem>();
	
	/**
	 * 查找促销
	 * 
	 * @param hasBegun
	 *            是否已开始
	 * @param hasEnded
	 *            是否已结束
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 促销
	 */
	public List<Promotion> findList(Boolean hasBegun, Boolean hasEnded, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = " SELECT * FROM promotion WHERE 1 = 1";
		if (hasBegun != null) {
			if (hasBegun) {
				sql += " AND (begin_date IS NULL OR begin_date <= '" + DateUtil.getDateTime() + "') ";
			} else {
				sql += " AND (begin_date IS NOT NULL OR begin_date >= '" + DateUtil.getDateTime() + "') ";
			}
		}
		if (hasEnded != null) {
			if (hasEnded) {
				sql += " AND (end_date IS NOT NULL OR end_date <= '" + DateUtil.getDateTime() + "') ";
			} else {
				sql += " AND (end_date IS NULL OR end_date >= '" + DateUtil.getDateTime() + "')";
			}
		}
		sql += ConditionUtil.buildSQL(null, count, filters, orders);
		return find(sql);
	}
	
	/**
	 * 获取允许参加会员等级
	 * 
	 * @return 允许参加会员等级
	 */
	public List<MemberRank> getMemberRanks() {
		String sql = "SELECT mr.* FROM promotion_member_rank pmr INNER JOIN member_rank mr ON pmr.`member_ranks` = mr.`id` WHERE pmr.`promotions` = ?";
		if (CollectionUtils.isEmpty(memberRanks)) {
			memberRanks = MemberRank.dao.find(sql, getId());
		}
		return memberRanks;
	}
	
	/**
	 * 设置允许参加会员等级
	 * 
	 * @param memberRanks
	 *            允许参加会员等级
	 */
	public void setMemberRanks(List<MemberRank> memberRanks) {
		this.memberRanks = memberRanks;
	}
	
	/**
	 * 获取允许参与商品分类
	 * 
	 * @return 允许参与商品分类
	 */
	public List<ProductCategory> getProductCategories() {
		String sql = "SELECT pc.* FROM `promotion_product_category` ppc INNER JOIN `product_category` pc ON ppc.`product_categories` = pc.`id` WHERE ppc.`promotions` = ?";
		if (CollectionUtils.isEmpty(productCategories)) {
			productCategories = ProductCategory.dao.find(sql, getId());
		}
		return productCategories;
	}
	
	/**
	 * 设置允许参与商品分类
	 * 
	 * @param productCategories
	 *            允许参与商品分类
	 */
	public void setProductCategories(List<ProductCategory> productCategories) {
		this.productCategories = productCategories;
	}
	
	/**
	 * 获取允许参与商品
	 * 
	 * @return 允许参与商品
	 */
	public List<Product> getProducts() {
		String sql = "SELECT p.* FROM promotion_product pp INNER JOIN product p ON pp.`products` = p.`id` WHERE pp.`promotions` = ?";
		if (CollectionUtils.isEmpty(products)) {
			products = Product.dao.find(sql, getId());
		}
		return products;
	}
	
	/**
	 * 设置允许参与商品
	 * 
	 * @param products
	 *            允许参与商品
	 */
	public void setProducts(List<Product> products) {
		this.products = products;
	}
	
	/**
	 * 获取允许参与品牌
	 * 
	 * @return 允许参与品牌
	 */
	public List<Brand> getBrands() {
		String sql = "SELECT b.* FROM promotion_brand pb INNER JOIN brand b ON pb.`brands` = b.`id` WHERE pb.`promotions` = ?";
		if (CollectionUtils.isEmpty(brands)) {
			brands = Brand.dao.find(sql, getId());
		}
		return brands;
	}

	/**
	 * 设置允许参与品牌
	 * 
	 * @param brands
	 *            允许参与品牌
	 */
	public void setBrands(List<Brand> brands) {
		this.brands = brands;
	}
	
	/**
	 * 获取赠送优惠券
	 * 
	 * @return 赠送优惠券
	 */
	public List<Coupon> getCoupons() {
		String sql = "SELECT c.* FROM promotion_coupon pc INNER JOIN coupon c ON pc.`coupons` = c.`id` WHERE pc.`promotions` = ?";
		if (CollectionUtils.isEmpty(coupons)) {
			coupons = Coupon.dao.find(sql, getId());
		}
		return coupons;
	}
	
	/**
	 * 设置赠送优惠券
	 * 
	 * @param coupons
	 *            赠送优惠券
	 */
	public void setCoupons(List<Coupon> coupons) {
		this.coupons = coupons;
	}
	
	/**
	 * 获取赠品
	 * 
	 * @return 赠品
	 */
	public List<GiftItem> getGiftItems() {
		String sql = "SELECT * FROM gift_item WHERE `promotion_id` = ?";
		if (CollectionUtils.isEmpty(giftItems)) {
			giftItems = GiftItem.dao.find(sql, getId());
		}
		return giftItems;
	}
	
	/**
	 * 设置赠品
	 * 
	 * @param giftItems
	 *            赠品
	 */
	public void setGiftItems(List<GiftItem> giftItems) {
		this.giftItems = giftItems;
	}
	
	/**
	 * 判断是否已开始
	 * 
	 * @return 是否已开始
	 */
	public boolean hasBegun() {
		return getBeginDate() == null || new Date().after(getBeginDate());
	}

	/**
	 * 判断是否已结束
	 * 
	 * @return 是否已结束
	 */
	public boolean hasEnded() {
		return getEndDate() != null && new Date().after(getEndDate());
	}

	/**
	 * 获取访问路径
	 * 
	 * @return 访问路径
	 */
	public String getPath() {
		if (getId() != null) {
			return PATH_PREFIX + "/" + getId();
		}
		return null;
	}

	/**
	 * 计算促销价格
	 * 
	 * @param quantity
	 *            商品数量
	 * @param price
	 *            商品价格
	 * @return 促销价格
	 */
	public BigDecimal calculatePrice(Integer quantity, BigDecimal price) {
		if (price == null || StringUtils.isEmpty(getPriceExpression())) {
			return price;
		}
		BigDecimal result = new BigDecimal(0);
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("quantity", quantity);
			model.put("price", price);
			result = new BigDecimal(FreemarkerUtils.process("#{(" + getPriceExpression() + ");M50}", model));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
		if (result.compareTo(price) > 0) {
			return price;
		}
		return result.compareTo(new BigDecimal(0)) > 0 ? result : new BigDecimal(0);
	}

	/**
	 * 计算促销赠送积分
	 * 
	 * @param quantity
	 *            商品数量
	 * @param point
	 *            赠送积分
	 * @return 促销赠送积分
	 */
	public Long calculatePoint(Integer quantity, Long point) {
		if (point == null || StringUtils.isEmpty(getPointExpression())) {
			return point;
		}
		Long result = 0L;
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("quantity", quantity);
			model.put("point", point);
			result = Double.valueOf(FreemarkerUtils.process("#{(" + getPointExpression() + ");M50}", model)).longValue();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (result < point) {
			return point;
		}
		return result > 0L ? result : 0L;
	}

}
