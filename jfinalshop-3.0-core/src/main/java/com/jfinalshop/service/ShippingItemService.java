package com.jfinalshop.service;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.Shipping;
import com.jfinalshop.model.ShippingItem;

/**
 * Service - 发货单项
 * 
 * 
 * 
 */
public class ShippingItemService extends BaseService<ShippingItem> {
	public static final ShippingItemService service = new ShippingItemService();
	
	public ShippingItemService() {
		super(ShippingItem.class);
	}
	
	/**
	 * 保存
	 * 
	 * 
	 */
	public boolean save(Shipping shipping) {
		boolean result = false;
		if (CollectionUtils.isNotEmpty(shipping.getShippingItems())) {
			for(ShippingItem shippingItem : shipping.getShippingItems()) {
				shippingItem.setShippingId(shipping.getId());
				result = super.save(shippingItem);
			}
		}
		return result;
	}
	
}
