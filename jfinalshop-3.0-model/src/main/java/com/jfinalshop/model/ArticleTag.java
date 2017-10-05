package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseArticleTag;

/**
 * Dao - 文章标签
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class ArticleTag extends BaseArticleTag<ArticleTag> {
	public static final ArticleTag dao = new ArticleTag();
	
	/**
	 * 检测是否已存在
	 * @return
	 */
	public boolean isNull(Long articles, Long tags) {
		String sql = "SELECT COUNT(*) FROM article_tag WHERE articles = ? AND tags = ?";
		return Db.queryLong(sql, articles, tags) == 0L;
	}
	
	/**
	 * 根据文章删除
	 * @param articles
	 * @return
	 */
	public boolean deleteArticleTag(Long articles) {
		return Db.deleteById("article_tag", "articles", articles);
	}
	
}
