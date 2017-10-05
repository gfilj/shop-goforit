package com.jfinalshop.service;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.render.FreeMarkerRender;
import com.jfinalshop.utils.SettingUtils;

import freemarker.template.Configuration;
import freemarker.template.TemplateModelException;

/**
 * Service - 缓存
 * 
 * 
 * 
 */
public class CacheService  {
	/** 缓存管理 */
	private CacheManager cacheManager = CacheKit.getCacheManager();
	/** 配置 */
	private Configuration freeMarkerConfigurer = FreeMarkerRender.getConfiguration();

	/**
	 * 获取缓存存储路径
	 * 
	 * @return 缓存存储路径
	 */
	public String getDiskStorePath() {
		return cacheManager.getConfiguration().getDiskStoreConfiguration().getPath();
	}


	/**
	 * 获取缓存数
	 * 
	 * @return 缓存数
	 */
	public int getCacheSize() {
		int cacheSize = 0;
		String[] cacheNames = cacheManager.getCacheNames();
		if (cacheNames != null) {
			for (String cacheName : cacheNames) {
				Ehcache cache = cacheManager.getEhcache(cacheName);
				if (cache != null) {
					cacheSize += cache.getSize();
				}
			}
		}
		return cacheSize;
	}

	/**
	 * 清除缓存
	 */
	public void clear() {
		try {
			freeMarkerConfigurer.setSharedVariable("setting", SettingUtils.get());
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}
		freeMarkerConfigurer.clearTemplateCache();
	}
}
