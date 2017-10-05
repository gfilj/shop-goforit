package com.jfinalshop.service;

import java.util.Date;

import com.jfinal.plugin.ehcache.CacheName;
import com.jfinalshop.model.Seo;
import com.jfinalshop.model.Seo.Type;
import com.jfinalshop.security.ShiroUtil;

/**
 * Service - SEO设置
 * 
 * 
 * 
 */
public class SeoService extends BaseService<Seo> {

	public SeoService() {
		super(Seo.class);
	}
	
	/**
	 * 查找SEO设置
	 * 
	 * @param type
	 *            类型
	 * @return SEO设置
	 */
	public Seo find(Type type) {
		return Seo.dao.find(type);
	}
	
	/**
	 * 查找SEO设置(缓存)
	 * 
	 * @param type
	 *            类型
	 * @param cacheRegion
	 *            缓存区域
	 * @return SEO设置(缓存)
	 */
	@CacheName("seo")
	public Seo find(Type type, String cacheRegion) {
		return Seo.dao.find(type);
	}
	
	/**
	 * 更新
	 */
	public boolean update(Seo seo) {
		seo.setLastUpdatedBy(ShiroUtil.getName());
		seo.setLastUpdatedDate(new Date());
		seo.setDeleteFlag(false);
		return seo.update();
	}
}
