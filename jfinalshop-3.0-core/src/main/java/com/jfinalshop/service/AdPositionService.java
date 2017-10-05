package com.jfinalshop.service;

import com.jfinalshop.model.AdPosition;

/**
 * 广告位
 *
 */
public class AdPositionService extends BaseService<AdPosition> {

	public AdPositionService() {
		super(AdPosition.class);
	}
	
	/**
	 * 查找广告位(缓存)
	 * 
	 * @param id
	 *            ID
	 * @param cacheRegion
	 *            缓存区域
	 * @return 广告位(缓存)
	 */
	public AdPosition find(Long id, String cacheRegion) {
		return find(id);
	}

}
