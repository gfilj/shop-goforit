package com.jfinalshop.service;

import java.util.List;

import com.jfinal.plugin.ehcache.CacheName;
import com.jfinalshop.common.Filter;
import com.jfinalshop.common.Order;
import com.jfinalshop.model.Navigation;
import com.jfinalshop.model.Navigation.Position;

/**
 * Service - 导航
 * 
 * 
 * 
 */
public class NavigationService extends BaseService<Navigation> {

	public NavigationService() {
		super(Navigation.class);
	}
	
	/**
	 * 查找导航
	 * 
	 * @param position
	 *            位置
	 * @return 导航
	 */
	public List<Navigation> findList(Position position) {
		return Navigation.dao.findList(position);
	}
	
	/**
	 * 查找导航(缓存)
	 * 
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param cacheRegion
	 *            缓存区域
	 * @return 导航(缓存)
	 */
	@CacheName("navigation")
	public List<Navigation> findList(Integer count, List<Filter> filters, List<Order> orders, String cacheRegion) {
		return findList(null, count, filters, orders);
	}
	
}
