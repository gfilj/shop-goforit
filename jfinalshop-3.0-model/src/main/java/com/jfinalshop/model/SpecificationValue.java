package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseSpecificationValue;

/**
 * Dao - 规格值
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class SpecificationValue extends BaseSpecificationValue<SpecificationValue> {
	public static final SpecificationValue dao = new SpecificationValue();
	
	/** 商品 */
	private List<Product> products = new ArrayList<Product>();
	
	/**
	 * 根据specificationId删除参数
	 * @param specificationId
	 * @return
	 */
	public boolean delete(Long specificationId) {
		return Db.deleteById("specification_value", "specification_id", specificationId);
	}
	
	/**
	 * 获取规格
	 * 
	 * @return 规格
	 */
	public Specification getSpecification() {
		return Specification.dao.findById(getSpecificationId());
	}
	
	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public List<Product> getProducts() {
		String sql = "SELECT p.* FROM `product_specification_value` psv INNER JOIN `product` p ON psv.`products` = p.`id` WHERE psv.`specification_values` = ?";
		if (CollectionUtils.isEmpty(products)) {
			products = Product.dao.find(sql, getId());
		}
		return products;
	}

	/**
	 * 设置商品
	 * 
	 * @param products
	 *            商品
	 */
	public void setProducts(List<Product> products) {
		this.products = products;
	}
}
