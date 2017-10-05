package com.jfinalshop.service;

import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductNotify;

/**
 * Service - 到货通知
 * 
 * 
 * 
 */
public class ProductNotifyService extends BaseService<ProductNotify> {
	
	public ProductNotifyService() {
		super(ProductNotify.class);
	}
	
	MailService mailService = Enhancer.enhance(MailService.class);
	
	/**
	 * 判断到货通知是否存在
	 * 
	 * @param product
	 *            商品
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return 到货通知是否存在
	 */
	public boolean exists(Product product, String email) {
		return ProductNotify.dao.exists(product, email);
	}
	
	/**
	 * 查找到货通知分页
	 * 
	 * @param member
	 *            会员
	 * @param isMarketable
	 *            是否上架
	 * @param isOutOfStock
	 *            商品是否缺货
	 * @param hasSent
	 *            是否已发送.
	 * @param pageable
	 *            分页信息
	 * @return 到货通知分页
	 */
	public Page<ProductNotify> findPage(Member member, Boolean isMarketable, Boolean isOutOfStock, Boolean hasSent, Pageable pageable) {
		return ProductNotify.dao.findPage(member, isMarketable, isOutOfStock, hasSent, pageable);
	}
	
	
	/**
	 * 查找到货通知数量
	 * 
	 * @param member
	 *            会员
	 * @param isMarketable
	 *            是否上架
	 * @param isOutOfStock
	 *            商品是否缺货
	 * @param hasSent
	 *            是否已发送.
	 * @return 到货通知数量
	 */
	public Long count(Member member, Boolean isMarketable, Boolean isOutOfStock, Boolean hasSent) {
		return ProductNotify.dao.count(member, isMarketable, isOutOfStock, hasSent);
	}
	
	
	/**
	 * 发送到货通知
	 * 
	 * @param ids
	 *            ID
	 * @return 发送到货通知数
	 */
	public int send(Long[] ids) {
		List<ProductNotify> productNotifys = findList(ids);
		for (ProductNotify productNotify : productNotifys) {
			//mailService.sendProductNotifyMail(productNotify);
			productNotify.setHasSent(true);
			productNotify.update();
		}
		return productNotifys.size();
	}
	
}
