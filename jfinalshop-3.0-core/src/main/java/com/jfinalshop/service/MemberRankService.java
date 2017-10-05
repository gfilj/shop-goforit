package com.jfinalshop.service;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.MemberRank;

/**
 * Service - 会员等级
 * 
 * 
 * 
 */
public class MemberRankService extends BaseService<MemberRank> {

	public MemberRankService() {
		super(MemberRank.class);
	}

	/**
	 * 判断名称是否存在
	 * 
	 * @param name
	 *            名称(忽略大小写)
	 * @return 名称是否存在
	 */
	public boolean nameExists(String name) {
		return MemberRank.dao.nameExists(name);
	}

	
	/**
	 * 判断名称是否唯一
	 * 
	 * @param previousName
	 *            修改前名称(忽略大小写)
	 * @param currentName
	 *            当前名称(忽略大小写)
	 * @return 名称是否唯一
	 */
	public boolean nameUnique(String previousName, String currentName) {
		if (StringUtils.equalsIgnoreCase(previousName, currentName)) {
			return true;
		} else {
			if (MemberRank.dao.nameExists(currentName)) {
				return false;
			} else {
				return true;
			}
		}
	}
	
	/**
	 * 判断消费金额是否存在
	 * 
	 * @param amount
	 *            消费金额
	 * @return 消费金额是否存在
	 */
	public boolean amountExists(BigDecimal amount) {
		return MemberRank.dao.amountExists(amount);
	}
	
	/**
	 * 判断消费金额是否唯一
	 * 
	 * @param previousAmount
	 *            修改前消费金额
	 * @param currentAmount
	 *            当前消费金额
	 * @return 消费金额是否唯一
	 */
	public boolean amountUnique(BigDecimal previousAmount, BigDecimal currentAmount) {
		if (previousAmount != null && previousAmount.compareTo(currentAmount) == 0) {
			return true;
		} else {
			if (MemberRank.dao.amountExists(currentAmount)) {
				return false;
			} else {
				return true;
			}
		}
	}
	
	/**
	 * 查找默认会员等级
	 * 
	 * @return 默认会员等级，若不存在则返回null
	 */
	public MemberRank findDefault() {
		return MemberRank.dao.findDefault();
	}
	
	/**
	 * 根据消费金额查找符合此条件的最高会员等级
	 * 
	 * @param amount
	 *            消费金额
	 * @return 会员等级，不包含特殊会员等级，若不存在则返回null
	 */
	public MemberRank findByAmount(BigDecimal amount) {
		return MemberRank.dao.findByAmount(amount);
	}
	
}
