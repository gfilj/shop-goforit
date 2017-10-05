package com.jfinalshop.service;

import com.jfinalshop.model.Area;
import com.jfinalshop.model.DeliveryCenter;

/**
 * Service - 发货点
 * 
 * 
 * 
 */
public class DeliveryCenterService extends BaseService<DeliveryCenter> {

	public DeliveryCenterService() {
		super(DeliveryCenter.class);
	}
	
	/**
	 * 查找默认发货点
	 * 
	 * @return 默认发货点，若不存在则返回null
	 */
	public DeliveryCenter findDefault() {
		return DeliveryCenter.dao.findDefault();
	}
	
	/**
	 * 保存
	 */
	public boolean save(DeliveryCenter deliveryCenter) {
		Area area = Area.dao.findById(deliveryCenter.getAreaId());
		if (area != null) {
			deliveryCenter.setAreaName(area.getFullName());
			deliveryCenter.setCreateBy(getCurrentName());
			deliveryCenter.setCreationDate(getSysDate());
			deliveryCenter.setDeleteFlag(false);
			return DeliveryCenter.dao.save(deliveryCenter);
		}
		return false;
	}
	
	/**
	 * 更新
	 */
	public boolean update(DeliveryCenter deliveryCenter) {
		Area area = Area.dao.findById(deliveryCenter.getAreaId());
		if (area != null) {
			deliveryCenter.setAreaName(area.getFullName());
			deliveryCenter.setLastUpdatedBy(getCurrentName());
			deliveryCenter.setLastUpdatedDate(getSysDate());
			deliveryCenter.setDeleteFlag(false);
			return DeliveryCenter.dao.update(deliveryCenter);
		}
		return false;
	}
	
}
