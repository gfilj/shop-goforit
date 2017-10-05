package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseProductMemberPrice;

/**
 * Dao - 商品会员价
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class ProductMemberPrice extends BaseProductMemberPrice<ProductMemberPrice> {
	public static final ProductMemberPrice dao = new ProductMemberPrice();
	
	/**
	 * 根据产品ID删除
	 * @param adminId
	 * @return
	 */
	public boolean delete(Long products) {
		return Db.deleteById("product_member_price", "product", products);
	}
}
