package com.jfinalshop.service;

import java.util.List;

import com.jfinal.plugin.ehcache.CacheName;
import com.jfinalshop.common.Filter;
import com.jfinalshop.common.Order;
import com.jfinalshop.model.FriendLink;
import com.jfinalshop.model.FriendLink.Type;

/**
 * Service - 友情链接
 * 
 * 
 * 
 */
public class FriendLinkService extends BaseService<FriendLink> {

	public FriendLinkService() {
		super(FriendLink.class);
	}
	
	/**
	 * 查找友情链接
	 * 
	 * @param type
	 *            类型
	 * @return 友情链接
	 */
	public List<FriendLink> findList(Type type) {
		return FriendLink.dao.findList(type);
	}
	
	
	/**
	 * 查找友情链接(缓存)
	 * 
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param cacheRegion
	 *            缓存区域
	 * @return 友情链接(缓存)
	 */
	@CacheName("friendLink")
	public List<FriendLink> findList(Integer count, List<Filter> filters, List<Order> orders, String cacheRegion) {
		return findList(null, count, filters, orders);
	}
	
}
