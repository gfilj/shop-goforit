package com.jfinalshop.service;

import java.math.BigDecimal;
import java.util.Map.Entry;

import com.jfinalshop.model.MemberRank;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductMemberPrice;

/**
 * Service - 会员价
 * 
 * 
 * 
 */
public class ProductMemberPriceService extends BaseService<ProductMemberPrice> {
	public static final ProductMemberPriceService service = new ProductMemberPriceService();
	
	public ProductMemberPriceService() {
		super(ProductMemberPrice.class);
	}
	
	/**
	 * 保存
	 * 
	 * 
	 */
	public boolean save(Product product) {
		boolean result = false;
		if (product.getMemberPrice() != null && 0 < product.getMemberPrice().size()) {
			for (Entry<MemberRank, BigDecimal> entry : product.getMemberPrice().entrySet()) {
				ProductMemberPrice productMemberPrice = new ProductMemberPrice();
				productMemberPrice.setProduct(product.getId());
				productMemberPrice.setMemberPrice(entry.getValue());
				productMemberPrice.setMemberPriceKey(entry.getKey().getId());
				result = productMemberPrice.save();
			}
		}
		return result;
	}
	
	/**
	 * 更新
	 * 
	 * 
	 */
	public boolean update(Product product) {
		boolean result = false;
		if (product.getMemberPrice() != null && 0 < product.getMemberPrice().size()) {
			ProductMemberPrice.dao.delete(product.getId());
			for (Entry<MemberRank, BigDecimal> entry : product.getMemberPrice().entrySet()) {
				ProductMemberPrice productMemberPrice = new ProductMemberPrice();
				productMemberPrice.setProduct(product.getId());
				productMemberPrice.setMemberPrice(entry.getValue());
				productMemberPrice.setMemberPriceKey(entry.getKey().getId());
				result = productMemberPrice.save();
			}
		}
		return result;
	}
}
