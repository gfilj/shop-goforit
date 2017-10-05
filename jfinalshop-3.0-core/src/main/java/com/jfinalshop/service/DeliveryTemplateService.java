package com.jfinalshop.service;

import com.jfinalshop.model.DeliveryTemplate;

public class DeliveryTemplateService extends BaseService<DeliveryTemplate> {

	public DeliveryTemplateService() {
		super(DeliveryTemplate.class);
	}
	
	/**
	 * 查找默认快递单模板
	 * 
	 * @return 默认快递单模板，若不存在则返回null
	 */
	public DeliveryTemplate findDefault() {
		return DeliveryTemplate.dao.findDefault();
	}

	/**
	 * 保存
	 */
	public boolean save(DeliveryTemplate deliveryTemplate) {
		deliveryTemplate.setCreateBy(getCurrentName());
		deliveryTemplate.setCreationDate(getSysDate());
		deliveryTemplate.setDeleteFlag(false);
		return DeliveryTemplate.dao.save(deliveryTemplate);
	}
	
	/**
	 * 更新
	 */
	public boolean update(DeliveryTemplate deliveryTemplate) {
		deliveryTemplate.setLastUpdatedBy(getCurrentName());
		deliveryTemplate.setLastUpdatedDate(getSysDate());
		deliveryTemplate.setDeleteFlag(false);
		return DeliveryTemplate.dao.update(deliveryTemplate);
	}
	
}
