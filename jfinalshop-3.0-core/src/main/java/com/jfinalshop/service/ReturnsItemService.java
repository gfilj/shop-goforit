package com.jfinalshop.service;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.Returns;
import com.jfinalshop.model.ReturnsItem;

/**
 * Service - 退货单项
 * 
 * 
 * 
 */
public class ReturnsItemService extends BaseService<ReturnsItem> {
	public static final ReturnsItemService service = new ReturnsItemService();
	
	public ReturnsItemService() {
		super(ReturnsItem.class);
	}
	
	/**
	 * 保存
	 * 
	 * 
	 */
	public boolean save(Returns returns) {
		boolean result = false;
		if (CollectionUtils.isNotEmpty(returns.getReturnsItems())) {
			for(ReturnsItem returnsItem : returns.getReturnsItems()) {
				returnsItem.setReturnsId(returns.getId());
				result = super.save(returnsItem);
			}
		}
		return result;
	}
	
}
