package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BaseArticleCategory;

/**
 * Dao - 文章分类
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class ArticleCategory extends BaseArticleCategory<ArticleCategory> {
	public static final ArticleCategory dao = new ArticleCategory();
	
	/** 树路径分隔符 */
	public static final String TREE_PATH_SEPARATOR = ",";

	/** 访问路径前缀 */
	private static final String PATH_PREFIX = "/article/list";
	
	/** 下级分类 */
	private List<ArticleCategory> children = new ArrayList<ArticleCategory>();

	/** 文章 */
	private List<Article> articles = new ArrayList<Article>();
	
	/**
	 * 查找顶级文章分类
	 * 
	 * @param count
	 *            数量
	 * @return 顶级文章分类
	 */
	public List<ArticleCategory> findRoots(Integer count) {
		String sql = "SELECT * FROM article_category WHERE parent_id IS NULL ORDER BY orders ASC";
		if (count != null) {
			sql += " LIMIT 0, " + count;
		}
		return find(sql);
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
		if (articleCategory == null || articleCategory.getParent() == null) {
			return Collections.<ArticleCategory> emptyList();
		}
		String sql = "SELECT * FROM article_category WHERE id IN (?) ORDER BY grade ASC";
		if (count != null) {
			sql += " LIMIT 0, " + count;
		}
		return find(sql,  articleCategory.getTreePaths());
	}
	
	
	/**
	 * 查找下级文章分类
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @param count
	 *            数量
	 * @return 下级文章分类
	 */
	public List<ArticleCategory> findChildren(ArticleCategory articleCategory, Integer count) {
		String sql = "";
		if (articleCategory != null) {
			sql = "SELECT * FROM `article_category` WHERE `tree_path` LIKE '%" + ArticleCategory.TREE_PATH_SEPARATOR + articleCategory.getId() + ArticleCategory.TREE_PATH_SEPARATOR +"%' ORDER BY orders ASC";
		} else {
			sql = "SELECT * FROM `article_category` ORDER BY orders ASC";
		}
		if (count != null) {
			sql += "  LIMIT 0, " + count;
		}
		return sort(find(sql), articleCategory);
	}
	
	/**
	 * 排序文章分类
	 * 
	 * @param articleCategories
	 *            文章分类
	 * @param parent
	 *            上级文章分类
	 * @return 文章分类
	 */
	private List<ArticleCategory> sort(List<ArticleCategory> articleCategories, ArticleCategory parent) {
		List<ArticleCategory> result = new ArrayList<ArticleCategory>();
		if (articleCategories != null) {
			for (ArticleCategory articleCategory : articleCategories) {
				if ((articleCategory.getParent() != null && articleCategory.getParent().equals(parent)) || (articleCategory.getParent() == null && parent == null)) {
					result.add(articleCategory);
					result.addAll(sort(articleCategories, articleCategory));
				}
			}
		}
		return result;
	}
	
	
	/**
	 * 获取上级分类
	 * 
	 * @return 上级分类
	 */
	public ArticleCategory getParent() {
		return ArticleCategory.dao.findById(getParentId());
	}
	
	/**
	 * 获取下级分类
	 * 
	 * @return 下级分类
	 */
	public List<ArticleCategory> getChildren() {
		String sql ="SELECT * FROM article_category WHERE `parent_id` = ?";
		if (children.isEmpty()) {
			ArticleCategory.dao.find(sql, getParentId());
		}
		return children;
	}

	/**
	 * 设置下级分类
	 * 
	 * @param children
	 *            下级分类
	 */
	public void setChildren(List<ArticleCategory> children) {
		this.children = children;
	}

	/**
	 * 获取文章
	 * 
	 * @return 文章
	 */
	public List<Article> getArticles() {
		String sql = "SELECT * FROM article WHERE `article_category_id` = ?";
		if (articles.isEmpty()) {
			articles = Article.dao.find(sql, getId());
		}
		return articles;
	}

	/**
	 * 设置文章
	 * 
	 * @param articles
	 *            文章
	 */
	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}
	
	/**
	 * 获取树路径
	 * 
	 * @return 树路径
	 */
	public List<Long> getTreePaths() {
		List<Long> treePaths = new ArrayList<Long>();
		String[] ids = StringUtils.split(getTreePath(), TREE_PATH_SEPARATOR);
		if (ids != null) {
			for (String id : ids) {
				treePaths.add(Long.valueOf(id));
			}
		}
		return treePaths;
	}
	
	/**
	 * 获取访问路径
	 * 
	 * @return 访问路径
	 */
	public String getPath() {
		if (getId() != null) {
			return PATH_PREFIX + "/" + getId();
		}
		return null;
	}
	
}
