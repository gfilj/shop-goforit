package com.jfinalshop.service;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Deposit;
import com.jfinalshop.model.Member;

/**
 * Service - 预存款
 * 
 * 
 * 
 */
public class DepositService extends BaseService<Deposit> {
	public static final DepositService service = new DepositService();
	public DepositService() {
		super(Deposit.class);
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
		return Deposit.dao.findPage(member, pageable);
	}
}
