package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseProductParameterValue;

/**
 * Dao - 商品参数值
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class ProductParameterValue extends BaseProductParameterValue<ProductParameterValue> {
	public static final ProductParameterValue dao = new ProductParameterValue();
	
	/**
	 * 根据产品ID删除
	 * @param adminId
	 * @return
	 */
	public boolean delete(Long products) {
		return Db.deleteById("product_parameter_value", "product", products);
	}
	

	/**
	 * 根据产品ID删除
	 * @param adminId
	 * @return
	 */
	public boolean deleteParameter(Long parameterValueKey) {
		return Db.deleteById("product_parameter_value", "parameter_value_key", parameterValueKey);
	}
}
