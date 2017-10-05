package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseAttribute;

/**
 * Dao - 属性
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Attribute extends BaseAttribute<Attribute> {
	public static final Attribute dao = new Attribute();
	
	/** 可选项 */
	private List<String> options = new ArrayList<String>();
	
	/**
	 * 获取绑定分类
	 * 
	 * @return 绑定分类
	 */
	public ProductCategory getProductCategory() {
		return ProductCategory.dao.findById(getProductCategoryId());
	}
	
	/**
	 * 获取可选项
	 * 
	 * @return 可选项
	 */
	public List<String> getOptions() {
		String sql = "select `options` from attribute_option where `attribute` = ?";
		if (options.isEmpty()) {
			options = Db.query(sql, getId());
		}
		return options;
	}
	
	/**
	 * 设置可选项
	 * 
	 * @param options
	 *            可选项
	 */
	public void setOptions(List<String> options) {
		this.options = options;
	}
	
	/**
	 * 设置propertyIndex并保存
	 * 
	 * @param attribute
	 *            属性
	 */
	public boolean save(Attribute attribute) {
		boolean result = false;
		String sql = "SELECT property_index FROM attribute WHERE product_category_id = ?";
		List<Attribute> attributes = find(sql, attribute.getProductCategoryId());
		
		List<Integer> propertyIndexs = new ArrayList<Integer>(); ;
		for(Attribute pAttribute : attributes) {
			propertyIndexs.add(pAttribute.getPropertyIndex());
		}
		for (int i = 0; i < Product.ATTRIBUTE_VALUE_PROPERTY_COUNT; i++) {
			if (!propertyIndexs.contains(i)) {
				attribute.setPropertyIndex(i);
				result = attribute.save();
				break;
			}
		}
		return result;
	}
}
