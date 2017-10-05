package com.jfinalshop.service;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Receiver;

/**
 * Service - 收货地址
 * 
 * 
 * 
 */
public class ReceiverService extends BaseService<Receiver> {
	
	public ReceiverService() {
		super(Receiver.class);
	}

	/**
	 * 查找默认收货地址
	 * 
	 * @param member
	 *            会员
	 * @return 默认收货地址，若不存在则返回最新收货地址
	 */
	public Receiver findDefault(Member member) {
		return Receiver.dao.findDefault(member);
	}

	/**
	 * 查找收货地址分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 收货地址分页
	 */
	public Page<Receiver> findPage(Member member, Pageable pageable) {
		return Receiver.dao.findPage(member, pageable);
	}
	
	/**
	 * 保存
	 * 
	 */
	public boolean save(Receiver receiver) {
		receiver.setCreationDate(getSysDate());
		receiver.setDeleteFlag(false);
		return Receiver.dao.save(receiver);
	}
}
