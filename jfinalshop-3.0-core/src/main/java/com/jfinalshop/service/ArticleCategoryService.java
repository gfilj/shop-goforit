package com.jfinalshop.service;

import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinalshop.model.ArticleCategory;
import com.jfinalshop.security.ShiroUtil;

/**
 * Service - 文章分类
 * 
 * 
 * 
 */
public class ArticleCategoryService extends BaseService<ArticleCategory> {
	
	public ArticleCategoryService() {
		super(ArticleCategory.class);
	}
	
	public static final ArticleCategoryService service = new ArticleCategoryService();
	
	/**
	 * 查找文章分类树
	 * 
	 * @return 文章分类树
	 */
	public List<ArticleCategory> findTree() {
		return ArticleCategory.dao.findChildren(null,null);
	}

	/**
	 * 查找下级文章分类
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @return 下级文章分类
	 */
	public List<ArticleCategory> findChildren(ArticleCategory articleCategory) {
		return ArticleCategory.dao.findChildren(articleCategory, null);
	}
	
	/**
	 * 查找下级文章分类(缓存)
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @param count
	 *            数量
	 * @param cacheRegion
	 *            缓存区域
	 * @return 下级文章分类(缓存)
	 */
	@CacheName("articleCategory")
	public List<ArticleCategory> findChildren(ArticleCategory articleCategory, Integer count, String cacheRegion) {
		return ArticleCategory.dao.findChildren(articleCategory, count);
	}
	
	/**
	 * 查找上级文章分类(缓存)
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @param count
	 *            数量
	 * @param cacheRegion
	 *            缓存区域
	 * @return 上级文章分类(缓存)
	 */
	@CacheName("articleCategory")
	public List<ArticleCategory> findParents(ArticleCategory articleCategory, Integer count, String cacheRegion) {
		return ArticleCategory.dao.findParents(articleCategory, count);
	}
	
	/**
	 * 查找上级文章分类
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @param count
	 *            数量
	 * @return 上级文章分类
	 */
	public List<ArticleCategory> findParents(ArticleCategory articleCategory, Integer count) {
		return ArticleCategory.dao.findParents(articleCategory, count);
	}
	
	/**
	 * 查找顶级文章分类
	 * 
	 * @param count
	 *            数量
	 * @return 顶级文章分类
	 */
	public List<ArticleCategory> findRoots(Integer count) {
		return ArticleCategory.dao.findRoots(count);
	}
	
	/**
	 * 查找顶级文章分类(缓存)
	 * 
	 * @param count
	 *            数量
	 * @param cacheRegion
	 *            缓存区域
	 * @return 顶级文章分类(缓存)
	 */
	@CacheName("articleCategory")
	public List<ArticleCategory> findRoots(Integer count, String cacheRegion) {
		return ArticleCategory.dao.findRoots(count);
	}
	
	/**
	 * 保存
	 * 
	 */
	@Before(Tx.class)
	public boolean save(ArticleCategory articleCategory) {
		articleCategory.setCreateBy(ShiroUtil.getName());
		articleCategory.setCreationDate(new Date());
		articleCategory.setDeleteFlag(false);
		articleCategory.save();
		
		ArticleCategory parent = ArticleCategory.dao.findById(articleCategory.getParentId());
		// 保存路径
		if (parent != null) {
			String parentPath = parent.getTreePath();
			articleCategory.setTreePath(parentPath + ArticleCategory.TREE_PATH_SEPARATOR + articleCategory.getId());
		} else {
			articleCategory.setTreePath(articleCategory.getId() + "");
		}
		// 保存层级
		int level = articleCategory.getTreePath().split(ArticleCategory.TREE_PATH_SEPARATOR).length - 1;
		articleCategory.setGrade(level);
		return articleCategory.update();
	}
	
}
