package com.jfinalshop.service;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.model.Parameter;
import com.jfinalshop.model.ParameterGroup;
import com.jfinalshop.model.ProductParameterValue;

public class ParameterService extends BaseService<Parameter> {
	public static final ParameterService service = new ParameterService();
	
	public ParameterService() {
		super(Parameter.class);
	}
	
	/**
	 * 保存
	 * 
	 * 
	 */
	public boolean save(ParameterGroup parameterGroup) {
		boolean result = false;
		if(!parameterGroup.getParameters().isEmpty()) {
			for(Parameter parameter : parameterGroup.getParameters()) {
				parameter.setParameterGroupId(parameterGroup.getId());
				super.save(parameter);
			}
		}
		return result;
	}
	
	/**
	 * 更新
	 * 
	 * 
	 */
	@Before(Tx.class)
	public boolean update(ParameterGroup parameterGroup) {
		boolean result = false;
		if(!parameterGroup.getParameters().isEmpty()) {
			String sql = "SELECT * FROM parameter WHERE parameter_group_id = ?";
			Parameter pParameter = Parameter.dao.findFirst(sql, parameterGroup.getId());
			ProductParameterValue.dao.deleteParameter(pParameter.getId());
			Parameter.dao.delete(parameterGroup.getId());
			for(Parameter parameter : parameterGroup.getParameters()) {
				parameter.setParameterGroupId(parameterGroup.getId());
				super.save(parameter);
			}
		}
		return result;
	}
}
