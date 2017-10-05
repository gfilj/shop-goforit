package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.base.BaseCouponCode;
import com.jfinalshop.utils.AssertUtil;
import com.jfinalshop.utils.DateUtil;

/**
 * Dao - 优惠码
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class CouponCode extends BaseCouponCode<CouponCode> {
	public static final CouponCode dao = new CouponCode();
	
	/**
	 * 判断优惠码是否存在
	 * 
	 * @param code
	 *            号码(忽略大小写)
	 * @return 优惠码是否存在
	 */
	public boolean codeExists(String code) {
		if (code == null) {
			return false;
		}
		String sql = "SELECT count(*) FROM coupon_code where LOWER(code) = LOWER(?)";
		Long count = Db.queryLong(sql, code);
		return count > 0;
	}

	/**
	 * 根据号码查找优惠码
	 * 
	 * @param code
	 *            号码(忽略大小写)
	 * @return 优惠码，若不存在则返回null
	 */
	public CouponCode findByCode(String code) {
		if (code == null) {
			return null;
		}
		try {
			String sql = "SELECT * FROM coupon_code WHERE LOWER(code) = LOWER(?)";
			return findFirst(sql, code);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 生成优惠码
	 * 
	 * @param coupon
	 *            优惠券
	 * @param member
	 *            会员
	 * @return 优惠码
	 */
	public CouponCode build(Coupon coupon, Member member) {
		AssertUtil.notNull(coupon);
		CouponCode couponCode = new CouponCode();
		String uuid = UUID.randomUUID().toString().toUpperCase();
		couponCode.setCode(coupon.getPrefix() + uuid.substring(0, 8) + uuid.substring(9, 13) + uuid.substring(14, 18) + uuid.substring(19, 23) + uuid.substring(24));
		couponCode.setIsUsed(false);
		couponCode.setCouponId(coupon.getId());
		if (member != null) {
			couponCode.setMemberId(member.getId());
		}
		couponCode.save();
		return couponCode;
	}

	/**
	 * 生成优惠码
	 * 
	 * @param coupon
	 *            优惠券
	 * @param member
	 *            会员
	 * @param count
	 *            数量
	 * @return 优惠码
	 */
	public List<CouponCode> build(Coupon coupon, Member member, Integer count) {
		AssertUtil.notNull(coupon);
		AssertUtil.notNull(count);
		List<CouponCode> couponCodes = new ArrayList<CouponCode>();
		for (int i = 0; i < count; i++) {
			CouponCode couponCode = build(coupon, member);
			couponCodes.add(couponCode);
			if (i % 20 == 0) {
			}
		}
		return couponCodes;
	}
	
	/**
	 * 查找优惠码分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 优惠码分页
	 */
	public Page<CouponCode> findPage(Member member, Pageable pageable) {
		String select = " SELECT * ";
		String sqlExceptSelect = " FROM `coupon_code`  WHERE 1 = 1 ";
		if (member != null) {
			sqlExceptSelect += " AND member_id = " + member.getId();
		}
		Page<CouponCode> couponCodes = paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect);
		return couponCodes;
	}

	/**
	 * 查找优惠码数量
	 * 
	 * @param coupon
	 *            优惠券
	 * @param member
	 *            会员
	 * @param hasBegun
	 *            是否已开始
	 * @param hasExpired
	 *            是否已过期
	 * @param isUsed
	 *            是否已使用
	 * @return 优惠码数量
	 */
	public Long count(Coupon coupon, Member member, Boolean hasBegun, Boolean hasExpired, Boolean isUsed) {
		String sql = "SELECT COUNT(1) FROM `coupon_code` cc LEFT JOIN `coupon` c ON cc.`coupon_id` = c.`id` WHERE 1 = 1";
		if (coupon != null) {
			sql += " AND cc.`coupon_id` = " + coupon.getId();
		}
		if (member != null) {
			sql += " AND cc.`member_id` = " + member.getId();
		}
		if (hasBegun != null) {
			if (hasBegun) {
				sql += " AND (c.`begin_date` IS NULL OR c.`begin_date` <= '" + DateUtil.getDateTime(new Date())+ "') ";
			} else {
				sql += " AND (c.`begin_date` IS NOT NULL OR c.`begin_date` > '" + DateUtil.getDateTime(new Date())+ "') ";
			}
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sql += " AND (c.`end_date` IS NOT NULL OR c.`end_date` < '" + DateUtil.getDateTime(new Date())+ "') ";
			} else {
				sql += " AND (c.`end_date` IS NULL OR c.`end_date` >= '" + DateUtil.getDateTime(new Date())+ "') ";
			}
		}
		if (isUsed != null) {
			sql += " AND cc.`is_used` = " + isUsed;
		}
		return Db.queryLong(sql);
	}
	
	/**
	 * 获取优惠券
	 * 
	 * @return 优惠券
	 */
	public Coupon getCoupon() {
		return Coupon.dao.findById(getId());
	}

}
