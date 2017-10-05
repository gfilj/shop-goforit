package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.base.BaseDeposit;

/**
 * Dao - 预存款
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Deposit extends BaseDeposit<Deposit> {
	public static final Deposit dao = new Deposit();
	
	/**
	 * 类型
	 */
	public enum Type {

		/** 会员充值 */
		memberRecharge,

		/** 会员支付 */
		memberPayment,

		/** 后台充值 */
		adminRecharge,

		/** 后台扣费 */
		adminChargeback,

		/** 后台支付 */
		adminPayment,

		/** 后台退款 */
		adminRefunds
	}
	
	
	/**
	 * 查找预存款分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 预存款分页
	 */
	public Page<Deposit> findPage(Member member, Pageable pageable) {
		if (member == null) {
			return null;
		}
		String select = " SELECT *";
		String sqlExceptSelect = " FROM deposit WHERE member_id = ?";
		Page<Deposit> deposits = paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect, member.getId());
		return deposits;
	}
}
