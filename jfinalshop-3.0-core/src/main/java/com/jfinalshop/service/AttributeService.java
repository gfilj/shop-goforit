package com.jfinalshop.service;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.model.Attribute;
import com.jfinalshop.model.AttributeOption;

/**
 * Service - 属性
 * 
 * 
 * 
 */
public class AttributeService extends BaseService<Attribute> {

	public AttributeService() {
		super(Attribute.class);
	}
	
	/**
	 * 保存
	 */
	@Before(Tx.class)
	public boolean save(Attribute attribute) {
		boolean result = false;
		result = super.save(attribute);
		AttributeOptionService.service.save(attribute);
		return result;
	}
	
	
	/**
	 * 更新
	 */
	public boolean update(Attribute attribute) {
		boolean result = false;
		result = super.update(attribute);
		AttributeOptionService.service.update(attribute);
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
			AttributeOption.dao.delete(id);
			result = Attribute.dao.deleteById(id);
		}
		return result;
	}
}
