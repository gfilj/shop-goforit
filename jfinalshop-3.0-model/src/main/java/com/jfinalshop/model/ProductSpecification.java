package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseProductSpecification;

/**
 * Dao - 商品规格
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class ProductSpecification extends BaseProductSpecification<ProductSpecification> {
	public static final ProductSpecification dao = new ProductSpecification();
	
	/**
	 * 根据产品ID删除
	 * @param adminId
	 * @return
	 */
	public boolean delete(Long products) {
		return Db.deleteById("product_specification", "products", products);
	}
}
