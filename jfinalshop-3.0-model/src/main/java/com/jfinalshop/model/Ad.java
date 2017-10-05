package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseAd;

/**
 * Dao - 广告
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Ad extends BaseAd<Ad> {
	public static final Ad dao = new Ad();
	
	/**
	 * 类型
	 */
	public enum Type {

		/** 文本 */
		text,

		/** 图片 */
		image,

		/** flash */
		flash
	}
	
	/**
	 * 广告位
	 * @return
	 */
	public AdPosition getAdPosition() {
		return AdPosition.dao.findById(getAdPositionId());
	}
	
}
