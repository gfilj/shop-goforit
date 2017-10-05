package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseSeo;

/**
 * Dao - SEO设置
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Seo extends BaseSeo<Seo> {
	public static final Seo dao = new Seo();
	
	/**
	 * 类型
	 */
	public enum Type {

		/** 首页 */
		index,

		/** 商品列表 */
		productList,

		/** 商品搜索 */
		productSearch,

		/** 商品页 */
		productContent,

		/** 文章列表 */
		articleList,

		/** 文章搜索 */
		articleSearch,

		/** 文章页 */
		articleContent,

		/** 品牌列表 */
		brandList,

		/** 品牌页 */
		brandContent
	}
	
	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public Type getTypeValues() {
		return Type.values()[getType()];
	}
	
	/**
	 * 查找SEO设置
	 * 
	 * @param type
	 *            类型
	 * @return SEO设置
	 */
	public Seo find(Type type) {
		if (type == null) {
			return null;
		}
		try {
			String sql = "SELECT * FROM seo WHERE type = ?";
			return findFirst(sql, type.ordinal());
		} catch (Exception e) {
			return null;
		}
	}
	
}
