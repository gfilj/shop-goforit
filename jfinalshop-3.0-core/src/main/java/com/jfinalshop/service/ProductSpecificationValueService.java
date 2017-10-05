package com.jfinalshop.service;

import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductSpecificationValue;
import com.jfinalshop.model.SpecificationValue;

/**
 * Service - 产品规格值
 * 
 * 
 * 
 */
public class ProductSpecificationValueService extends BaseService<ProductSpecificationValue> {
	public static final ProductSpecificationValueService service = new ProductSpecificationValueService();
	
	public ProductSpecificationValueService() {
		super(ProductSpecificationValue.class);
	}
	
	/**
	 * 保存
	 * 
	 * 
	 */
	public boolean save(Product product) {
		boolean result = false;
		if (!product.getSpecificationValues().isEmpty()) {
			for (SpecificationValue specificationValue : product.getSpecificationValues()) {
				ProductSpecificationValue productSpecificationValue = new ProductSpecificationValue();
				productSpecificationValue.setProducts(product.getId());
				productSpecificationValue.setSpecificationValues(specificationValue.getId());
				result = productSpecificationValue.save();
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
		if (!product.getSpecificationValues().isEmpty()) {
			ProductSpecificationValue.dao.delete(product.getId());
			for (SpecificationValue specificationValue : product.getSpecificationValues()) {
				ProductSpecificationValue productSpecificationValue = new ProductSpecificationValue();
				productSpecificationValue.setProducts(product.getId());
				productSpecificationValue.setSpecificationValues(specificationValue.getId());
				result = productSpecificationValue.save();
			}
		}
		return result;
	}
}
