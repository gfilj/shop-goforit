package com.jfinalshop.service;

import java.util.Date;
import java.util.List;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinalshop.common.Filter;
import com.jfinalshop.common.Order;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.ArticleCategory;
import com.jfinalshop.model.ArticleTag;
import com.jfinalshop.model.Tag;

/**
 * Service - 文章
 * 
 * 
 * 
 */
public class ArticleService extends BaseService<Article> {
	public static final ArticleService service = new ArticleService();
	public ArticleService() {
		super(Article.class);
	}
	
	/** 查看点击数时间 */
	private long viewHitsTime = System.currentTimeMillis();
	/** 缓存 */
	private CacheManager cacheManager = CacheKit.getCacheManager();
	
	/**
	 * 查找文章
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @param tags
	 *            标签
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 仅包含已发布文章
	 */
	public List<Article> findList(ArticleCategory articleCategory, List<Tag> tags, Integer count, List<Filter> filters, List<Order> orders) {
		return  Article.dao.findList(articleCategory, tags, count, filters, orders);
	}
	
	/**
	 * 查找文章(缓存)
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @param tags
	 *            标签
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param cacheRegion
	 *            缓存区域
	 * @return 仅包含已发布文章
	 */
	@CacheName("article")
	public List<Article> findList(ArticleCategory articleCategory, List<Tag> tags, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion) {
		return Article.dao.findList(articleCategory, tags, count, filters, orders);
	}
	
	/**
	 * 查找文章
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @param first
	 *            起始记录
	 * @param count
	 *            数量
	 * @return 仅包含已发布文章
	 */
	public List<Article> findList(ArticleCategory articleCategory, Date beginDate, Date endDate, Integer first, Integer count) {
		return Article.dao.findList(articleCategory, beginDate, endDate, first, count);
	}
	
	/**
	 * 查找文章分页
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @param tags
	 *            标签
	 * @param pageable
	 *            分页信息
	 * @return 仅包含已发布文章
	 */
	public Page<Article> findPage(ArticleCategory articleCategory, List<Tag> tags, Pageable pageable) {
		return Article.dao.findPage(articleCategory, tags, pageable);
	}

	
	/**
	 * 查看并更新点击数
	 * 
	 * @param id
	 *            ID
	 * @return 点击数
	 */
	public long viewHits(Long id) {
		Ehcache cache = cacheManager.getEhcache(Article.HITS_CACHE_NAME);
		Element element = cache.get(id);
		Long hits;
		if (element != null) {
			hits = (Long) element.getObjectValue();
		} else {
			Article article = Article.dao.findById(id);
			if (article == null) {
				return 0L;
			}
			hits = article.getHits();
		}
		hits++;
		cache.put(new Element(id, hits));
		long time = System.currentTimeMillis();
		if (time > viewHitsTime + Article.HITS_CACHE_INTERVAL) {
			viewHitsTime = time;
			updateHits();
			cache.removeAll();
		}
		return hits;
	}
	
	/**
	 * 更新点击数
	 */
	@SuppressWarnings("unchecked")
	private void updateHits() {
		Ehcache cache = cacheManager.getEhcache(Article.HITS_CACHE_NAME);
		List<Long> ids = cache.getKeys();
		for (Long id : ids) {
			Article article = Article.dao.findById(id);
			if (article != null) {
				Element element = cache.get(id);
				long hits = (Long) element.getObjectValue();
				article.setHits(hits);
				article.update();
			}
		}
	}
	
	/**
	 * 保存
	 * 
	 */
	public boolean save(Article article) {
		boolean result = false;
		result = super.save(article);
		ArticleTagService.service.save(article);
		StaticService.service.build(article);
		return result;
	}
	
	/**
	 * 更新
	 * 
	 */
	public boolean update(Article article) {
		boolean result = false;
		result = super.update(article);
		ArticleTagService.service.update(article);
		StaticService.service.build(article);
		return result;
	}
	
	/**
	 * 删除
	 * 
	 */
	@Before(Tx.class)
	public boolean delete(Long[] ids) {
		boolean result = false;
		for (Long id : ids) {
			ArticleTag.dao.deleteArticleTag(id);
			result = Article.dao.deleteById(id);
		}
		return result;
	}
}
