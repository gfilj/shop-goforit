package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseSpecification;

/**
 * Dao - 规格
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Specification extends BaseSpecification<Specification> {
	public static final Specification dao = new Specification();
	
	/**
	 * 类型
	 */
	public enum Type {

		/** 文本 */
		text,

		/** 图片 */
		image
	};
	
	/** 规格值 */
	private List<SpecificationValue> specificationValues = new ArrayList<SpecificationValue>();

	/** 商品 */
	private List<Product> products = new ArrayList<Product>();
	
	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public Type getTypeValues() {
		return Type.values()[getType()];
	}
	
	
	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public List<Product> getProducts() {
		String sql = "SELECT * FROM product_specification ps INNER JOIN product p ON ps.products = p.id WHERE ps.specifications = ?";
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
	
	/**
	 * 获取规格值
	 * 
	 * @return 规格值
	 */
	public List<SpecificationValue> getSpecificationValues() {
		String sql = "SELECT * FROM specification_value WHERE `specification_id` = ?";
		if (CollectionUtils.isEmpty(specificationValues)) {
			specificationValues = SpecificationValue.dao.find(sql, getId());
		}
		return specificationValues;
	}
	
	/**
	 * 设置规格值
	 * 
	 * @param specificationValues
	 *            规格值
	 */
	public void setSpecificationValues(List<SpecificationValue> specificationValues) {
		this.specificationValues = specificationValues;
	}
}
