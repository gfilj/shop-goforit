package com.jfinalshop.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Filter;
import com.jfinalshop.common.Order;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.base.BaseReview;
import com.jfinalshop.utils.ConditionUtil;

/**
 * Dao - 评论
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Review extends BaseReview<Review> {
	public static final Review dao = new Review();
	
	/** 访问路径前缀 */
	private static final String PATH_PREFIX = "/review/content";

	/**
	 * 类型
	 */
	public enum Type {

		/** 好评 */
		positive,

		/** 中评 */
		moderate,

		/** 差评 */
		negative
	}
	
	/**
	 * 查找评论
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param type
	 *            类型
	 * @param isShow
	 *            是否显示
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 评论
	 */
	public List<Review> findList(Member member, Product product, Type type, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT * FROM review WHERE 1 = 1 ";
		if (member != null) {
			sql += " AND member_id = " + member.getId();
		}
		if (product != null) {
			sql += " AND product_id = " + product.getId();
		}
		if (type == Type.positive) {
			sql += " AND score > 4 ";
		} else if (type == Type.moderate) {
			sql += " AND score = 3 ";
		} else if (type == Type.negative) {
			sql += " AND score < 2 ";
		}
		if (isShow != null) {
			sql += " AND is_show = " + isShow;
		}
		sql += ConditionUtil.buildSQL(null, count, filters, orders);
		return find(sql);
	}
	
	/**
	 * 查找评论分页
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param type
	 *            类型
	 * @param isShow
	 *            是否显示
	 * @param pageable
	 *            分页信息
	 * @return 评论分页
	 */
	public Page<Review> findPage(Member member, Product product, Type type, Boolean isShow, Pageable pageable) {
		String select = " SELECT *  ";
		String sqlExceptSelect = " FROM review WHERE 1 = 1 ";
		if (member != null) {
			sqlExceptSelect += " AND member_id = " + member.getId();
		}
		if (product != null) {
			sqlExceptSelect += " AND product_id = " + product.getId();
		}
		if (type == Type.positive) {
			sqlExceptSelect += " AND score > 4 ";
		} else if (type == Type.moderate) {
			sqlExceptSelect += " AND score = 3 ";
		} else if (type == Type.negative) {
			sqlExceptSelect += " AND score < 2 ";
		}
		if (isShow != null) {
			sqlExceptSelect += " AND is_show = " + isShow;
		}
		Page<Review> reviews = paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect);
		return reviews;
	}
	
	/**
	 * 查找评论数量
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param type
	 *            类型
	 * @param isShow
	 *            是否显示
	 * @return 评论数量
	 */
	public Long count(Member member, Product product, Type type, Boolean isShow) {
		String sql = "SELECT * FROM review WHERE 1 = 1 ";
		if (member != null) {
			sql += " AND member_id = " + member.getId();
		}
		if (product != null) {
			sql += " AND product_id = " + product.getId();
		}
		if (type == Type.positive) {
			sql += " AND score > 4 ";
		} else if (type == Type.moderate) {
			sql += " AND score = 3 ";
		} else if (type == Type.negative) {
			sql += " AND score < 2 ";
		}
		if (isShow != null) {
			sql += " AND is_show = " + isShow;
		}
		return Db.queryLong(sql);
	}

	/**
	 * 判断会员是否已评论该商品
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @return 是否已评论该商品
	 */
	public boolean isReviewed(Member member, Product product) {
		if (member == null || product == null) {
			return false;
		}
		String sql = "SELECT count(*) FROM `review` review WHERE review.member_id = ? AND review.product_id = ?";
		Long count = Db.queryLong(sql, member.getId(), product.getId());
		return count > 0;
	}

	/**
	 * 计算商品总评分
	 * 
	 * @param product
	 *            商品
	 * @return 商品总评分，仅计算显示评论
	 */
	public long calculateTotalScore(Product product) {
		if (product == null) {
			return 0L;
		}
		String sql = "SELECT sum(review.score) FROM `review` review WHERE review.product_id = ? AND review.is_show = ?";
		Long totalScore = Db.queryLong(sql, product.getId(), true);
		return totalScore != null ? totalScore : 0L;
	}

	/**
	 * 计算商品评分次数
	 * 
	 * @param product
	 *            商品
	 * @return 商品评分次数，仅计算显示评论
	 */
	public long calculateScoreCount(Product product) {
		if (product == null) {
			return 0L;
		}
		String sql = "SELECT count(*) FROM `review` review WHERE review.product_id = ? AND review.is_show = ?";
		return Db.queryLong(sql, product.getId(), true);
	}
	
	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public Product getProduct() {
		return Product.dao.findById(getProductId());
	}
	/**
	 * 获取访问路径
	 * 
	 * @return 访问路径
	 */
	public String getPath() {
		if (getProduct() != null && getProduct().getId() != null) {
			return PATH_PREFIX + "/" + getProduct().getId();
		}
		return null;
	}
}
