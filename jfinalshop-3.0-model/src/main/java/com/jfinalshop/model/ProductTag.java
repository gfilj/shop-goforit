package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseProductTag;

/**
 * Dao - 商品标签
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class ProductTag extends BaseProductTag<ProductTag> {
	public static final ProductTag dao = new ProductTag();
	

	/**
	 * 根据产品ID删除
	 * @param productId
	 * @return
	 */
	public boolean delete(Long productId) {
		return Db.deleteById("product_tag", "products", productId);
	}
}
