package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.base.BaseProductNotify;

/**
 * Dao - 到货通知
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class ProductNotify extends BaseProductNotify<ProductNotify> {
	public static final ProductNotify dao = new ProductNotify();
	
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
		String sql = "SELECT count(*) FROM product_notify WHERE product_id = ? AND LOWER(email) = LOWER(?) AND has_sent = false";
		Long count = Db.queryLong(sql, product.getId(), email);
		return count > 0;
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
		String select = " SELECT pn.* ";
		String sqlExceptSelect = " FROM `product_notify` pn LEFT JOIN `product` p ON pn.`product_id` = p.`id` WHERE 1 = 1";
		if (member != null) {
			sqlExceptSelect += " AND pn.member_id = " + member.getId();
		}
		if (isMarketable != null) {
			sqlExceptSelect += " AND p.`is_marketable` = " + isMarketable;
		}
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				sqlExceptSelect += " AND p.`stock` IS NOT NULL AND p.`stock` <= p.`allocated_stock` ";
			} else {
				sqlExceptSelect += " AND (p.`stock` IS NULL OR p.`stock` > p.`allocated_stock`) ";
			}
		}
		if (hasSent != null) {
			sqlExceptSelect += " AND pn.has_sent = " + hasSent;
		}
		Page<ProductNotify> productNotifys = paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect);
		return productNotifys;
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
		String sql = " SELECT COUNT(*)  FROM `product_notify` pn LEFT JOIN `product` p ON pn.`product_id` = p.`id` WHERE 1 = 1";
		if (member != null) {
			sql += " AND pn.member_id = " + member.getId();
		}
		if (isMarketable != null) {
			sql += " AND p.`is_marketable` = " + isMarketable;
		}
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				sql += " AND p.`stock` IS NOT NULL AND p.`stock` <= p.`allocated_stock` ";
			} else {
				sql += " AND (p.`stock` IS NULL OR p.`stock` > p.`allocated_stock`) ";
			}
		}
		if (hasSent != null) {
			sql += " AND pn.has_sent = " + hasSent;
		}
		return Db.queryLong(sql);
	}
	
	
}
