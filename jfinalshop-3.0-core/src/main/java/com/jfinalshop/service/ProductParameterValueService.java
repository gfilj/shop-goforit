package com.jfinalshop.service;

import java.util.Map;

import com.jfinalshop.model.Parameter;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductParameterValue;

/**
 * Service - 产品参数值
 * 
 * 
 * 
 */
public class ProductParameterValueService extends BaseService<ProductParameterValue> {
	public static final ProductParameterValueService service = new ProductParameterValueService();
	
	public ProductParameterValueService() {
		super(ProductParameterValue.class);
	}
	
	/**
	 * 保存
	 * 
	 * 
	 */
	public boolean save(Product product) {
		boolean result = false;
		if (product.getParameterValue() != null && 0 < product.getParameterValue().size()) {
			for (Map.Entry<Parameter, String> entry : product.getParameterValue().entrySet()) {
				ProductParameterValue productParameterValue = new ProductParameterValue();
				productParameterValue.setProduct(product.getId());
				productParameterValue.setParameterValue(entry.getValue());
				productParameterValue.setParameterValueKey( entry.getKey().getId());
				result = productParameterValue.save();
			}
		}
		return result;
	}
	
	/**
	 * 更新
	 * 
	 * 
	 */
	public boolean update(Product product) {
		boolean result = false;
		if (product.getParameterValue() != null && 0 < product.getParameterValue().size()) {
			ProductParameterValue.dao.delete(product.getId());
			for (Map.Entry<Parameter, String> entry : product.getParameterValue().entrySet()) {
				ProductParameterValue productParameterValue = new ProductParameterValue();
				productParameterValue.setProduct(product.getId());
				productParameterValue.setParameterValue(entry.getValue());
				productParameterValue.setParameterValueKey( entry.getKey().getId());
				result = productParameterValue.save();
			}
		}
		return result;
	}
}
