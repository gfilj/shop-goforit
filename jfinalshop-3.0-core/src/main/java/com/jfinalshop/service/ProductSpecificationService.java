package com.jfinalshop.service;

import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductSpecification;
import com.jfinalshop.model.Specification;

/**
 * Service - 产品规格
 * 
 * 
 * 
 */
public class ProductSpecificationService extends BaseService<ProductSpecification> {
	public static final ProductSpecificationService service = new ProductSpecificationService();
	
	public ProductSpecificationService() {
		super(ProductSpecification.class);
	}
	
	/**
	 * 保存
	 * 
	 * 
	 */
	public boolean save(Product product) {
		boolean result = false;
		if (!product.getSpecifications().isEmpty()) {
			for (Specification specification : product.getSpecifications()) {
				ProductSpecification productSpecification = new ProductSpecification();
				productSpecification.setProducts(product.getId());
				productSpecification.setSpecifications(specification.getId());
				result = productSpecification.save();
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
		if (!product.getSpecifications().isEmpty()) {
			ProductSpecification.dao.delete(product.getId());
			for (Specification specification : product.getSpecifications()) {
				ProductSpecification productSpecification = new ProductSpecification();
				productSpecification.setProducts(product.getId());
				productSpecification.setSpecifications(specification.getId());
				result = productSpecification.save();
			}
		}
		return result;
	}
}
