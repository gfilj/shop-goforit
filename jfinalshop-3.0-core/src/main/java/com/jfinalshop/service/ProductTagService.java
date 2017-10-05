package com.jfinalshop.service;

import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductTag;
import com.jfinalshop.model.Tag;

/**
 * Service - 产品标签
 * 
 * 
 * 
 */
public class ProductTagService extends BaseService<ProductTag> {
	public static final ProductTagService service = new ProductTagService();
	
	public ProductTagService() {
		super(ProductTag.class);
	}
	
	/**
	 * 保存
	 * 
	 * 
	 */
	public boolean save(Product product) {
		boolean result = false;
		if (!product.getTags().isEmpty()) {
			for(Tag tag : product.getTags()) {
				ProductTag productTag = new ProductTag();
				productTag.setProducts(product.getId());
				productTag.setTags(tag.getId());
				result = productTag.save();
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
		if (!product.getTags().isEmpty()) {
			ProductTag.dao.delete(product.getId());
			for(Tag tag : product.getTags()) {
				ProductTag productTag = new ProductTag();
				productTag.setProducts(product.getId());
				productTag.setTags(tag.getId());
				result = productTag.save();
			}
		}
		return result;
	}
}
