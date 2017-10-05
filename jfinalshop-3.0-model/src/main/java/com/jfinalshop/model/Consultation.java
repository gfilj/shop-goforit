package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Filter;
import com.jfinalshop.common.Order;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.base.BaseConsultation;
import com.jfinalshop.utils.ConditionUtil;

/**
 * Dao - 咨询
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Consultation extends BaseConsultation<Consultation> {
	public static final Consultation dao = new Consultation();
	
	/** 访问路径前缀 */
	private static final String PATH_PREFIX = "/admin/consultation/content";

	/** 回复 */
	private List<Consultation> replyConsultations = new ArrayList<Consultation>();
	
	/**
	 * 查找咨询
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param isShow
	 *            是否显示
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 咨询
	 */
	public List<Consultation> findList(Member member, Product product, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = " SELECT * FROM consultation WHERE for_consultation IS NULL ";
		if (member != null) {
			sql += " AND member_id = " + member.getId();
		}
		if (product != null) {
			sql += " AND product_id = " + product.getId();
		}
		if (isShow != null) {
			sql += " AND is_show = " + isShow;
		}
		sql += ConditionUtil.buildSQL(null, count, filters, orders);
		return find(sql);
	}
	
	/**
	 * 查找咨询分页
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param isShow
	 *            是否显示
	 * @param pageable
	 *            分页信息
	 * @return 咨询分页
	 */
	public Page<Consultation> findPage(Member member, Product product, Boolean isShow, Pageable pageable) {
		String select = " SELECT * ";
		String sqlExceptSelect = " FROM `consultation` WHERE for_consultation IS NULL  ";
		if (member != null) {
			sqlExceptSelect += " AND member_id = " + member.getId();
		}
		if (product != null) {
			sqlExceptSelect += " AND product_id = " + product.getId();
		}
		if (isShow != null) {
			sqlExceptSelect += " AND is_show = " + isShow;
		}
		Page<Consultation> consultations = paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect);
		return consultations;
	}

	/**
	 * 查找咨询数量
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param isShow
	 *            是否显示
	 * @return 咨询数量
	 */
	public Long count(Member member, Product product, Boolean isShow) {
		String sql = "SELECT count(*) FROM `consultation` WHERE for_consultation IS NULL ";
		if (member != null) {
			sql += " AND member_id = " + member.getId();
		}
		if (product != null) {
			sql += " AND product_id = " + product.getId();
		}
		if (isShow != null) {
			sql += " AND is_show = " + isShow;
		}
		return Db.queryLong(sql);
	}
	
	/**
	 * 获取回复
	 * 
	 * @return 回复
	 */
	public List<Consultation> getReplyConsultations() {
		String sql = "SELECT * FROM `consultation` WHERE for_consultation = ?";
		if (CollectionUtils.isEmpty(replyConsultations)) {
			replyConsultations = Consultation.dao.find(sql, getId());
		}
		return replyConsultations;
	}

	/**
	 * 设置回复
	 * 
	 * @param replyConsultations
	 *            回复
	 */
	public void setReplyConsultations(List<Consultation> replyConsultations) {
		this.replyConsultations = replyConsultations;
	}
	
	
	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public Member getMember() {
		return Member.dao.findById(getMemberId());
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
			return PATH_PREFIX + "/" + getProductId();
		}
		return null;
	}
}
