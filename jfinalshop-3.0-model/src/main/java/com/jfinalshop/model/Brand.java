package com.jfinalshop.model;


import java.util.ArrayList;
import java.util.List;

import com.jfinalshop.model.base.BaseBrand;

/**
 * Dao - 品牌
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Brand extends BaseBrand<Brand> {
	public static final Brand dao = new Brand();
	
	/** 访问路径前缀 */
	private static final String PATH_PREFIX = "/brand/content";

	/**
	 * 类型
	 */
	public enum Type {

		/** 文本 */
		text,

		/** 图片 */
		image
	}
	
	/** 商品 */
	private List<Product> products = new ArrayList<Product>();

	/** 商品分类 */
	private List<ProductCategory> productCategories = new ArrayList<ProductCategory>();

	/** 促销 */
	private List<Promotion> promotions = new ArrayList<Promotion>();
	
	
	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public List<Product> getProducts() {
		String sql = "SELECT * FROM product WHERE `brand_id` = ?";
		if (products.isEmpty()) {
			products = Product.dao.find(sql, getId());
		}
		return products;
	}

	/**
	 * 设置商品
	 * 
	 * @param products
	 *            商品
	 */
	public void setProducts(List<Product> products) {
		this.products = products;
	}
	
	/**
	 * 获取商品分类
	 * 
	 * @return 商品分类
	 */
	public List<ProductCategory> getProductCategories() {
		String sql = "SELECT pc.* FROM product_category_brand pcb INNER JOIN product_category pc ON pcb.`product_categories` = pc.`id` WHERE pcb.`brands` = ?";
		if (productCategories.isEmpty()) {
			productCategories = ProductCategory.dao.find(sql, getId());
		}
		return productCategories;
	}
	
	/**
	 * 设置商品分类
	 * 
	 * @param productCategories
	 *            商品分类
	 */
	public void setProductCategories(List<ProductCategory> productCategories) {
		this.productCategories = productCategories;
	}
	
	
	/**
	 * 获取促销
	 * 
	 * @return 促销
	 */
	public List<Promotion> getPromotions() {
		String sql = "SELECT p.*  FROM promotion_brand pb INNER JOIN promotion p ON pb.`promotions` = p.`id` WHERE pb.`brands` = ?";
		if (promotions.isEmpty()) {
			promotions = Promotion.dao.find(sql, getId());
		}
		return promotions;
	}
	
	/**
	 * 设置促销
	 * 
	 * @param promotions
	 *            促销
	 */
	public void setPromotions(List<Promotion> promotions) {
		this.promotions = promotions;
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
	
}
