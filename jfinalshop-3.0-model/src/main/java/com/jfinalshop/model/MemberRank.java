package com.jfinalshop.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseMemberRank;
import com.jfinalshop.utils.AssertUtil;

/**
 * Dao - 会员等级
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class MemberRank extends BaseMemberRank<MemberRank> {
	public static final MemberRank dao = new MemberRank();
	
	/** 会员 */
	private List<Member> members = new ArrayList<Member>();

	/** 促销 */
	private List<Promotion> promotions = new ArrayList<Promotion>();
	
	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public List<Member> getMembers() {
		String sql = "SELECT * FROM member WHERE member_rank_id = ?";
		if (members.isEmpty()) {
			members = Member.dao.find(sql, getId());
		}
		return members;
	}

	/**
	 * 设置会员
	 * 
	 * @param members
	 *            会员
	 */
	public void setMembers(List<Member> members) {
		this.members = members;
	}

	/**
	 * 获取促销
	 * 
	 * @return 促销
	 */
	public List<Promotion> getPromotions() {
		String sql = "SELECT p.* FROM `promotion_member_rank`  pmr LEFT JOIN `promotion` p ON pmr.`promotions` = p.`id`  WHERE pmr.`member_ranks` = ?";
		if (promotions.isEmpty()) {
			promotions = Promotion.dao.find(sql, getId());
		}
		return promotions;
	}

	/**
	 * 设置促销
	 * 
	 * @param promotions
	 *            促销
	 */
	public void setPromotions(List<Promotion> promotions) {
		this.promotions = promotions;
	}
	
	/**
	 * 判断名称是否存在
	 * 
	 * @param name
	 *            名称(忽略大小写)
	 * @return 名称是否存在
	 */
	public boolean nameExists(String name) {
		if (name == null) {
			return false;
		}
		String sql = "SELECT COUNT(*) FROM member_rank WHERE LOWER(name) = LOWER(?)";
		Long count = Db.queryLong(sql, name);
		return count > 0;
	}
	
	/**
	 * 判断消费金额是否存在
	 * 
	 * @param amount
	 *            消费金额
	 * @return 消费金额是否存在
	 */
	public boolean amountExists(BigDecimal amount) {
		if (amount == null) {
			return false;
		}
		String sql = "SELECT count(*) FROM member_rank WHERE amount = ?";
		Long count = Db.queryLong(sql, amount);
		return count > 0;
	}
	
	/**
	 * 查找默认会员等级
	 * 
	 * @return 默认会员等级，若不存在则返回null
	 */
	public MemberRank findDefault() {
		try {
			String sql = "SELECT * FROM member_rank WHERE `is_default` = true";
			return findFirst(sql);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 根据消费金额查找符合此条件的最高会员等级
	 * 
	 * @param amount
	 *            消费金额
	 * @return 会员等级，不包含特殊会员等级，若不存在则返回null
	 */
	public MemberRank findByAmount(BigDecimal amount) {
		if (amount == null) {
			return null;
		}
		String sql = "SELECT * FROM member_rank WHERE `is_special` = FALSE   AND amount <= ? ORDER BY amount DESC";
		return findFirst(sql, amount);
	}
	
	/**
	 * 处理默认并保存
	 * 
	 * @param memberRank
	 *            会员等级
	 */
	public boolean save(MemberRank memberRank) {
		AssertUtil.notNull(memberRank);
		if (memberRank.getIsDefault()) {
			String sql = "UPDATE member_rank SET is_default = false WHERE is_default = true";
			Db.update(sql);
		}
		return memberRank.save();
	}
	
	/**
	 * 处理默认并更新
	 * 
	 * @param memberRank
	 *            会员等级
	 * @return 会员等级
	 */
	public boolean update(MemberRank memberRank) {
		AssertUtil.notNull(memberRank);
		if (memberRank.getIsDefault()) {
			String sql = "UPDATE member_rank SET is_default = false WHERE is_default = true AND `id` != ?";
			Db.update(sql, memberRank.getId());
		}
		return memberRank.update();
	}
		
	
	/**
	 * 忽略默认、清除会员价并删除
	 * 
	 * @param memberRank
	 *            会员等级
	 */
	public void remove(MemberRank memberRank) {
		if (memberRank != null && !memberRank.getIsDefault()) {
			String sql = "SELECT p.* FROM product p LEFT JOIN product_member_price pmp ON p.`id` = pmp.`product`  WHERE pmp.`member_price_key` = ?";
			List<Product> products = Product.dao.find(sql, memberRank.getId());
			for (int i = 0; i < products.size(); i++) {
				Product product = products.get(i);
				product.getMemberPrice().remove(memberRank);
				if (i % 20 == 0) {
					super.clear();
				}
			}
			memberRank.update();
		}
	}
	
}
