package com.jfinalshop.service;

import java.util.List;

import com.jfinal.plugin.ehcache.CacheName;
import com.jfinalshop.common.Filter;
import com.jfinalshop.common.Order;
import com.jfinalshop.model.Tag;
import com.jfinalshop.model.Tag.Type;

/**
 * Service - 标签
 * 
 * 
 * 
 */
public class TagService extends BaseService<Tag> {
	public static final TagService service = new TagService();
	
	public TagService() {
		super(Tag.class);
	}
	
	/**
	 * 查找标签
	 * 
	 * @param type
	 *            类型
	 * @return 标签
	 */
	public List<Tag> findList(Type type) {
		return Tag.dao.findList(type);
	}
	
	
	/**
	 * 查找标签(缓存)
	 * 
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param cacheRegion
	 *            缓存区域
	 * @return 标签(缓存)
	 */
	@CacheName("tag")
	public List<Tag> findList(Integer count, List<Filter> filters, List<Order> orders, String cacheRegion) {
		return findList(null, count, filters, orders);
	}
	
}
