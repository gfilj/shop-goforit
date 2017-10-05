package com.jfinalshop.service;

import com.jfinalshop.model.Returns;

/**
 * Service - 退货单
 * 
 * 
 * 
 */
public class ReturnsService extends BaseService<Returns> {

	public ReturnsService() {
		super(Returns.class);
	}
	
	/**
	 * 保存退货单
	 * 
	 */
	public boolean save(Returns returns) {
		boolean result = false;
		result = super.save(returns);
		ReturnsItemService.service.save(returns);
		return result;
	}
	
	
}
