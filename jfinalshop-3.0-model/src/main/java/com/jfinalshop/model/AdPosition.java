package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import com.jfinalshop.model.base.BaseAdPosition;

/**
 * Dao - 广告位
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class AdPosition extends BaseAdPosition<AdPosition> {
	public static final AdPosition dao = new AdPosition();
	
	/** 广告 */
	private List<Ad> ads = new ArrayList<Ad>();
	
	/**
	 * 获取广告
	 * 
	 * @return 广告
	 */
	public List<Ad> getAds() {
		String sql = "SELECT * FROM ad WHERE `ad_position_id` = ?";
		if (ads.isEmpty()) {
			ads = Ad.dao.find(sql, getId());
		}
		return ads;
	}

	/**
	 * 设置广告
	 * 
	 * @param ads
	 *            广告
	 */
	public void setAds(List<Ad> ads) {
		this.ads = ads;
	}
	
}
