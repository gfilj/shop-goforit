package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseProductCategoryBrand;

/**
 * Dao - 商品分类品牌
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class ProductCategoryBrand extends BaseProductCategoryBrand<ProductCategoryBrand> {
	public static final ProductCategoryBrand dao = new ProductCategoryBrand();
	
	/**
	 * 根据specificationId删除参数
	 * @param specificationId
	 * @return
	 */
	public boolean delete(Long productCategoryId) {
		return Db.deleteById("product_category_brand", "product_categories", productCategoryId);
	}
}
