package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseDeliveryCenter;
import com.jfinalshop.utils.AssertUtil;

/**
 * Dao - 发货点
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class DeliveryCenter extends BaseDeliveryCenter<DeliveryCenter> {
	public static final DeliveryCenter dao = new DeliveryCenter();
	
	/**
	 * 获取地区
	 * 
	 * @return 地区
	 */
	public Area getArea() {
		return Area.dao.findById(getAreaId());
	}
	
	/**
	 * 查找默认发货点
	 * 
	 * @return 默认发货点，若不存在则返回null
	 */
	public DeliveryCenter findDefault() {
		try {
			String sql = "SELECT * FROM delivery_center WHERE is_default = true";
			return findFirst(sql);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 处理默认并保存
	 * 
	 * @param deliveryCenter
	 *            发货点
	 */
	public boolean save(DeliveryCenter deliveryCenter) {
		AssertUtil.notNull(deliveryCenter);
		if (deliveryCenter.getIsDefault()) {
			String sql = "UPDATE delivery_center SET is_default = false WHERE is_default = true";
			Db.update(sql);
		}
		return deliveryCenter.save();
	}

	/**
	 * 处理默认并更新
	 * 
	 * @param deliveryCenter
	 *            发货点
	 * @return 发货点
	 */
	public boolean update(DeliveryCenter deliveryCenter) {
		AssertUtil.notNull(deliveryCenter);
		if (deliveryCenter.getIsDefault()) {
			String sql = "UPDATE delivery_center SET is_default = false WHERE is_default = true AND id = ?";
			Db.update(sql, deliveryCenter.getId());
		}
		return deliveryCenter.update();
	}
	
}
