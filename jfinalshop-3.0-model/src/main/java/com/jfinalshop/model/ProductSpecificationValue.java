package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseProductSpecificationValue;

/**
 * Dao - 商品规格值
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class ProductSpecificationValue extends BaseProductSpecificationValue<ProductSpecificationValue> {
	public static final ProductSpecificationValue dao = new ProductSpecificationValue();
	
	/**
	 * 根据产品ID删除
	 * @param adminId
	 * @return
	 */
	public boolean delete(Long products) {
		return Db.deleteById("product_specification_value", "products", products);
	}
}
