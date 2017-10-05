package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseTag;

/**
 * Dao - 标签
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Tag extends BaseTag<Tag> {
	public static final Tag dao = new Tag();
	
	/**
	 * 类型
	 */
	public enum Type {

		/** 文章标签 */
		article,

		/** 商品标签 */
		product
	};
	
	/** 文章 */
	private List<Article> articles = new ArrayList<Article>();

	/** 商品 */
	private List<Product> products = new ArrayList<Product>();
	
	/**
	 * 查找标签
	 * 
	 * @param type
	 *            类型
	 * @return 标签
	 */
	public List<Tag> findList(Type type) {
		String sql = "SELECT *  FROM tag  WHERE type = ? ORDER BY orders ASC";
		return find(sql, type.ordinal());
	}
	
	/**
	 * 获取标签
	 * 
	 * @return 标签
	 */
	public List<Tag> getTags(Long articleId) {
		String sql = "SELECT * FROM article_tag WHERE articles = ? ";
		return find(sql, articleId);
	}
	
	/**
	 * 获取文章
	 * 
	 * @return 文章
	 */
	public List<Article> getArticles() {
		String sql = "SELECT a.* FROM `article_tag` t LEFT JOIN `article` a ON t.`articles` = a.`id` WHERE t.`articles` = ?";
		if (CollectionUtils.isEmpty(articles)) {
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
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public List<Product> getProducts() {
		String sql = "SELECT p.* FROM `product_tag` pt LEFT JOIN `product` p ON pt.`products` = p.`id` WHERE pt.`tags` = ?";
		if (CollectionUtils.isEmpty(products)) {
			products = Product.dao.find(sql, getId());
		}
		return products;
	}

	/**
	 * 设置商品
	 * 
	 * @param products
	 *            商品
	 */
	public void setProducts(List<Product> products) {
		this.products = products;
	}

	/**
	 * 删除前处理
	 */
	public void preRemove() {
		List<Article> articles = getArticles();
		if (articles != null) {
			for (Article article : articles) {
				article.getTags().remove(this);
			}
		}
		List<Product> products = getProducts();
		if (products != null) {
			for (Product product : products) {
				product.getTags().remove(this);
			}
		}
	}

}
