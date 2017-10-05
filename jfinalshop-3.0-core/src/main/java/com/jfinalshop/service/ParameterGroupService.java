package com.jfinalshop.service;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.model.Parameter;
import com.jfinalshop.model.ParameterGroup;

/**
 * Service - 参数组
 * 
 * 
 * 
 */
public class ParameterGroupService extends BaseService<ParameterGroup> {

	public ParameterGroupService() {
		super(ParameterGroup.class);
	}
	
	/**
	 * 保存
	 */
	@Before(Tx.class)
	public boolean save(ParameterGroup parameterGroup) {
		boolean result = false;
		result = super.save(parameterGroup);
		ParameterService.service.save(parameterGroup);
		return result;
	}
	
	/**
	 * 更新
	 */
	public boolean update(ParameterGroup parameterGroup) {
		boolean result = false;
		result = super.update(parameterGroup);
		ParameterService.service.update(parameterGroup);
		return result;
	}
	
	/**
	 * 删除
	 * 
	 * @param ids
	 * @return
	 */
	@Before(Tx.class)
	public boolean delete(Long[] ids) {
		boolean result = false;
		for (Long id : ids) {
			Parameter.dao.delete(id);
			result = ParameterGroup.dao.deleteById(id);
		}
		return result;
	}
}
