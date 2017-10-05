package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BasePromotionMemberRank;

/**
 * Dao - 促销会员等级
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class PromotionMemberRank extends BasePromotionMemberRank<PromotionMemberRank> {
	public static final PromotionMemberRank dao = new PromotionMemberRank();
	
	/**
	 * 根据促销ID删除
	 * @param adminId
	 * @return
	 */
	public boolean delete(Long promotions) {
		return Db.deleteById("promotion_member_rank", "promotions", promotions);
	}
}
