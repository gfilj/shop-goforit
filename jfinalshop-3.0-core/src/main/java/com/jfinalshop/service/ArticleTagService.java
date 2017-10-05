package com.jfinalshop.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.ArticleTag;
import com.jfinalshop.model.Tag;

public class ArticleTagService extends BaseService<ArticleTag> {
	public static final ArticleTagService service = new ArticleTagService();
	
	public ArticleTagService() {
		super(ArticleTag.class);
	}
	
	/**
	 * 保存
	 * 
	 * 
	 */
	public boolean save(Article article) {
		boolean result = false;
		if (!article.getTags().isEmpty()) {
			for (Tag tag : article.getTags()) {
				if (ArticleTag.dao.isNull(article.getId(), tag.getId())) {
					ArticleTag articleTag = new ArticleTag();
					articleTag.setArticles(article.getId());
					articleTag.setTags(tag.getId());
					articleTag.save();
				}
			}
		}
		return result;
	}
	
	/**
	 * 更新
	 * 
	 * 
	 */
	public boolean update(Article article) {
		boolean result = false;
		if (!article.getTags().isEmpty()){
			ArticleTag.dao.deleteArticleTag(article.getId());
			for (Tag tag : article.getTags()) {
				ArticleTag articleTag = new ArticleTag();
				articleTag.setArticles(article.getId());
				articleTag.setTags(tag.getId());
				articleTag.save();
			}
		}
		return result;
	}
	
	/**
	 * 根据ID删除
	 * @param articles
	 * @return
	 */
	public boolean delete(Long articles) {
		return Db.deleteById("article_tag", "articles", articles);
	}
}
