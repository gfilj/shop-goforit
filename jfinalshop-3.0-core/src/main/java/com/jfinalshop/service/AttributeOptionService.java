package com.jfinalshop.service;

import com.jfinalshop.model.Attribute;
import com.jfinalshop.model.AttributeOption;

public class AttributeOptionService extends BaseService<AttributeOption> {
	public static final AttributeOptionService service = new AttributeOptionService();
	
	public AttributeOptionService() {
		super(AttributeOption.class);
	}
	
	/**
	 * 保存
	 * 
	 * 
	 */
	public boolean save(Attribute attribute) {
		boolean result = false;
		if(!attribute.getOptions().isEmpty()) {
			for(String option : attribute.getOptions()) {
				AttributeOption attributeOption = new AttributeOption();
				attributeOption.setAttribute(attribute.getId());
				attributeOption.setOptions(option);
				result = attributeOption.save();
			}
		}
		return result;
	}
	
	/**
	 * 更新
	 * 
	 * 
	 */
	public boolean update(Attribute attribute) {
		boolean result = false;
		if(!attribute.getOptions().isEmpty()) {
			AttributeOption.dao.delete(attribute.getId());
			for(String option : attribute.getOptions()) {
				AttributeOption attributeOption = new AttributeOption();
				attributeOption.setAttribute(attribute.getId());
				attributeOption.setOptions(option);
				result = attributeOption.save();
			}
		}
		return result;
	}
}
