package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseParameterGroup;

/**
 * Dao - 参数组
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class ParameterGroup extends BaseParameterGroup<ParameterGroup> {
	public static final ParameterGroup dao = new ParameterGroup();
	
	/** 参数 */
	private List<Parameter> parameters = new ArrayList<Parameter>();
	
	/**
	 * 获取参数
	 * 
	 * @return 参数
	 */
	public List<Parameter> getParameters() {
		String sql = "SELECT * FROM parameter WHERE `parameter_group_id` = ?";
		if (CollectionUtils.isEmpty(parameters)) {
			parameters = Parameter.dao.find(sql, getId());
		}
		return parameters;
	}

	/**
	 * 设置参数
	 * 
	 * @param parameters
	 *            参数
	 */
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * 获取绑定分类
	 * 
	 * @return 绑定分类
	 */
	public ProductCategory getProductCategory() {
		return ProductCategory.dao.findById(getProductCategoryId());
	}
}
