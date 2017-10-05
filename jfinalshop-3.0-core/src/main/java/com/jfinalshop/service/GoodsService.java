package com.jfinalshop.service;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Product;
import com.jfinalshop.utils.AssertUtil;

/**
 * Service - 货品
 * 
 * 
 * 
 */
public class GoodsService extends BaseService<Goods> {

	public GoodsService() {
		super(Goods.class);
	}
	
	private StaticService staticService = new StaticService();
	
	/**
	 * 保存
	 * 
	 */
	public boolean save(Goods goods) {
		AssertUtil.notNull(goods);
		
		boolean result = false;
		goods.setCreationDate(getSysDate());
		result = goods.save();
		/*if (goods.getProducts() != null) {
			for (Product product : goods.getProducts()) {
				staticService.build(product);
			}
		}*/
		return result;
	}
	
	/**
	 * 更新
	 * 
	 */
	public boolean update(Goods goods) {
		AssertUtil.notNull(goods);

		boolean result = false;
		List<Product> excludes = new ArrayList<Product>();
		CollectionUtils.select(goods.getProducts(), new Predicate() {
			public boolean evaluate(Object object) {
				Product product = (Product) object;
				return product != null && product.getId() != null;
			}
		}, excludes);
		List<Product> products = Product.dao.findList(goods, excludes);
		for (Product product : products) {
			staticService.delete(product);
		}
		goods.setLastUpdatedDate(getSysDate());
		result = goods.update();
		Goods pGoods = this.find(goods.getId());
		if (pGoods.getProducts() != null) {
			for (Product product : pGoods.getProducts()) {
				staticService.build(product);
			}
		}
		return result;
	}
	
	/**
	 * 删除
	 * 
	 */
	public boolean delete(Goods goods) {
		if (goods != null && goods.getProducts() != null) {
			for (Product product : goods.getProducts()) {
				staticService.delete(product);
			}
		}
		return super.delete(goods);
	}
}
