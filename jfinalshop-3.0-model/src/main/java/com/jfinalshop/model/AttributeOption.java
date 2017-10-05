package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseAttributeOption;

/**
 * Dao - 属性选项
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class AttributeOption extends BaseAttributeOption<AttributeOption> {
	public static final AttributeOption dao = new AttributeOption();
	
	/**
	 * 根据attributeId删除参数
	 * @param attributeId
	 * @return
	 */
	public boolean delete(Long attributeId) {
		return Db.deleteById("attribute_option", "attribute", attributeId);
	}
}
