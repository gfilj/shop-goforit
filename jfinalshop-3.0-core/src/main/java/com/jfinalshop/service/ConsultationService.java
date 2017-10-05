package com.jfinalshop.service;

import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinalshop.common.Filter;
import com.jfinalshop.common.Order;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Consultation;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;

/**
 * Service - 咨询
 * 
 * 
 * 
 */
public class ConsultationService extends BaseService<Consultation> {
	
	public ConsultationService() {
		super(Consultation.class);
	}
	
	/** 静态化 */
	private StaticService staticService = new StaticService();
	
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
	 * @return 咨询,不包含咨询回复
	 */
	public List<Consultation> findList(Member member, Product product, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders) {
		return Consultation.dao.findList(member, product, isShow, count, filters, orders);
	}

	/**
	 * 查找咨询(缓存)
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
	 * @param cacheRegion
	 *            缓存区域
	 * @return 咨询(缓存),不包含咨询回复
	 */
	@CacheName("consultation")
	public List<Consultation> findList(Member member, Product product, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion) {
		return Consultation.dao.findList(member, product, isShow, count, filters, orders);
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
	 * @return 不包含咨询回复
	 */
	public Page<Consultation> findPage(Member member, Product product, Boolean isShow, Pageable pageable) {
		return Consultation.dao.findPage(member, product, isShow, pageable);
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
		return Consultation.dao.count(member, product, isShow);
	}

	/**
	 * 咨询回复
	 * 
	 * @param consultation
	 *            咨询
	 * @param replyConsultation
	 *            回复咨询
	 */
	public void reply(Consultation consultation, Consultation replyConsultation) {
		if (consultation == null || replyConsultation == null) {
			return;
		}
		consultation.setIsShow(true);
		consultation.update();

		replyConsultation.setIsShow(true);
		replyConsultation.setProductId(consultation.getProductId());
		replyConsultation.setForConsultation(consultation.getId());
		save(replyConsultation);

		Product product = consultation.getProduct();
		if (product != null) {
			staticService.build(product);
		}
	}
	
}
