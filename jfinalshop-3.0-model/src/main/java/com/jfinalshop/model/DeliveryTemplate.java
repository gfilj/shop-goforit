package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseDeliveryTemplate;
import com.jfinalshop.utils.AssertUtil;

/**
 * Dao - 快递单模板
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class DeliveryTemplate extends BaseDeliveryTemplate<DeliveryTemplate> {
	public static final DeliveryTemplate dao = new DeliveryTemplate();
	
	
	/**
	 * 查找默认快递单模板
	 * 
	 * @return 默认快递单模板，若不存在则返回null
	 */
	public DeliveryTemplate findDefault() {
		try {
			String sql = "SELECT * FROM delivery_template WHERE is_default = true";
			return findFirst(sql);
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * 处理默认并保存
	 * 
	 * @param deliveryTemplate
	 *            快递单模板
	 */
	public boolean save(DeliveryTemplate deliveryTemplate) {
		AssertUtil.notNull(deliveryTemplate);
		if (deliveryTemplate.getIsDefault()) {
			String sql = "UPDATE delivery_template SET is_default = false WHERE is_default = true";
			Db.update(sql);
		}
		return deliveryTemplate.save();
	}

	/**
	 * 处理默认并更新
	 * 
	 * @param deliveryTemplate
	 *            快递单模板
	 * @return 快递单模板
	 */
	public boolean update(DeliveryTemplate deliveryTemplate) {
		AssertUtil.notNull(deliveryTemplate);
		if (deliveryTemplate.getIsDefault()) {
			String sql = "UPDATE delivery_template SET is_default = false WHERE is_default = true AND id != ?";
			Db.update(sql, deliveryTemplate.getId());
		}
		return deliveryTemplate.update();
	}
	
}
