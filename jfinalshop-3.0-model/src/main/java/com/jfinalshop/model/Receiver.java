package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.base.BaseReceiver;
import com.jfinalshop.utils.AssertUtil;

/**
 * Dao - 收货地址
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Receiver extends BaseReceiver<Receiver> {
	public static final Receiver dao = new Receiver();
	
	/** 收货地址最大保存数 */
	public static final Integer MAX_RECEIVER_COUNT = 8;
	
	/**
	 * 查找默认收货地址
	 * 
	 * @param member
	 *            会员
	 * @return 默认收货地址，若不存在则返回最新收货地址
	 */
	public Receiver findDefault(Member member) {
		if (member == null) {
			return null;
		}
		try {
			String sql = "SELECT * FROM receiver WHERE member_id = ? AND is_default = true";
			return Receiver.dao.findFirst(sql, member.getId());
		} catch (Exception e) {
			try {
				String sql = "SELECT * FROM receiver WHERE member_id = ? ORDER BY last_updated_date DESC";
				return Receiver.dao.findFirst(sql, member.getId());
			} catch (Exception e1) {
				return null;
			}
		}
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
		String select = " SELECT * ";
		String sqlExceptSelect = " FROM receiver WHERE 1 = 1 ";
		if (member != null) {
			sqlExceptSelect += " AND member_id = " + member.getId();
		}
		Page<Receiver> receivers = paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect);
		return receivers;
	}
	
	/**
	 * 获取地区
	 * 
	 * @return 地区
	 */
	public Area getArea() {
		return Area.dao.findById(getAreaId());
	}
	
	
	/**
	 * 更新并处理默认
	 * 
	 * @param receiver
	 *            收货地址
	 * @return 收货地址
	 */
	public boolean save(Receiver receiver) {
		AssertUtil.notNull(receiver);
		if (receiver.getIsDefault()) {
			String sql = "UPDATE receiver SET is_default = false WHERE member_id = ? AND is_default = true AND id != ?";
			Db.update(sql, receiver.getMemberId(), receiver.getId());
		}
		String areaName = Area.dao.findById(receiver.getAreaId()).getFullName();
		receiver.setAreaName(areaName);
		return receiver.save();
	}
	
	
}
