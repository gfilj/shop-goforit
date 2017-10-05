package com.jfinalshop.service;

import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Coupon;
import com.jfinalshop.model.CouponCode;
import com.jfinalshop.model.Member;
import com.jfinalshop.utils.AssertUtil;

/**
 * Service - 优惠码
 * 
 * 
 * 
 */
public class CouponCodeService extends BaseService<CouponCode>{
	
	public CouponCodeService() {
		super(CouponCode.class);
	}

	/**
	 * 判断优惠码是否存在
	 * 
	 * @param code
	 *            号码(忽略大小写)
	 * @return 优惠码是否存在
	 */
	public boolean codeExists(String code) {
		return CouponCode.dao.codeExists(code);
	}

	/**
	 * 根据号码查找优惠码
	 * 
	 * @param code
	 *            号码(忽略大小写)
	 * @return 优惠码，若不存在则返回null
	 */
	public CouponCode findByCode(String code) {
		return CouponCode.dao.findByCode(code);
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
		return CouponCode.dao.build(coupon, member);
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
		return CouponCode.dao.build(coupon, member, count);
	}


	/**
	 * 兑换优惠码
	 * 
	 * @param coupon
	 *            优惠券
	 * @param member
	 *            会员
	 * @return 优惠码
	 */
	public CouponCode exchange(Coupon coupon, Member member) {
		AssertUtil.notNull(coupon);
		AssertUtil.notNull(member);

		member.setPoint(member.getPoint() - coupon.getPoint());
		member.update();

		return CouponCode.dao.build(coupon, member);
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
		return CouponCode.dao.findPage(member, pageable);
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
		return CouponCode.dao.count(coupon, member, hasBegun, hasExpired, isUsed);
	}
}
