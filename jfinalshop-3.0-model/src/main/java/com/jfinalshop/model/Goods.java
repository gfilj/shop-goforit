package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jfinalshop.model.base.BaseGoods;

/**
 * Dao - 货品
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Goods extends BaseGoods<Goods> {
	public static final Goods dao = new Goods();
	
	/** 商品 */
	private List<Product> products = new ArrayList<Product>();
	
	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public List<Product> getProducts() {
		String sql = "SELECT * FROM `product` where `goods_id` = ?";
		if (products.isEmpty()) {
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
	public Set<SpecificationValue> getSpecificationValues() {
		Set<SpecificationValue> specificationValues = new HashSet<SpecificationValue>();
		if (getProducts() != null) {
			for (Product product : getProducts()) {
				specificationValues.addAll(product.getSpecificationValues());
			}
		}
		return specificationValues;
	}
}
