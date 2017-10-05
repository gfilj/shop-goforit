package com.jfinalshop.service;

import java.math.BigDecimal;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Product.OrderType;

/**
 * Service - 搜索
 * 
 * 
 * 
 */
public class SearchService {

	/** 模糊查询最小相似度 */
	//private static final float FUZZY_QUERY_MINIMUM_SIMILARITY = 0.5F;
	
	
	/**
	 * 创建索引
	 */
	public void index() {
//		index(Article.class);
//		index(Product.class);
	}

	/**
	 * 创建索引
	 * 
	 * @param type
	 *            索引类型
	 */
//	public void index(Class<?> type) {
//		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
//		if (type == Article.class) {
//			for (int i = 0; i < ArticleService.service.count(); i += 20) {
//				List<Article> articles = ArticleService.service.findList(i, 20, null, null);
//				for (Article article : articles) {
//					fullTextEntityManager.index(article);
//				}
//				fullTextEntityManager.flushToIndexes();
//				fullTextEntityManager.clear();
//				articleDao.clear();
//			}
//		} else if (type == Product.class) {
//			for (int i = 0; i < ProductService.service.count(); i += 20) {
//				List<Product> products = ProductService.service.findList(i, 20, null, null);
//				for (Product product : products) {
//					fullTextEntityManager.index(product);
//				}
//				fullTextEntityManager.flushToIndexes();
//				fullTextEntityManager.clear();
//				productDao.clear();
//			}
//		}
//	}

	/**
	 * 创建索引
	 * 
	 * @param article
	 *            文章
	 */
//	public void index(Article article) {
//		if (article != null) {
//			FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
//			fullTextEntityManager.index(article);
//		}
//	}

	/**
	 * 创建索引
	 * 
	 * @param product
	 *            商品
	 */
//	public void index(Product product) {
//		if (product != null) {
//			FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
//			fullTextEntityManager.index(product);
//		}
//	}

	/**
	 * 删除索引
	 */
	public void purge() {
//		purge(Article.class);
//		purge(Product.class);
	}

	/**
	 * 删除索引
	 * 
	 * @param type
	 *            索引类型
	 */
//	public void purge(Class<?> type) {
//		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
//		if (type == Article.class) {
//			fullTextEntityManager.purgeAll(Article.class);
//		} else if (type == Product.class) {
//			fullTextEntityManager.purgeAll(Product.class);
//		}
//	}

	/**
	 * 删除索引
	 * 
	 * @param article
	 *            文章
	 */
//	public void purge(Article article) {
//		if (article != null) {
//			FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
//			fullTextEntityManager.purge(Article.class, article.getId());
//		}
//	}

	/**
	 * 删除索引
	 * 
	 * @param product
	 *            商品
	 */
//	public void purge(Product product) {
//		if (product != null) {
//			FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
//			fullTextEntityManager.purge(Product.class, product.getId());
//		}
//	}

	/**
	 * 搜索文章分页
	 * 
	 * @param keyword
	 *            关键词
	 * @param pageable
	 *            分页信息
	 * @return 文章分页
	 */
	public Page<Article> search(String keyword, Pageable pageable) {
		return null;
		/*if (StringUtils.isEmpty(keyword)) {
			return new Page<Article>();
		}
		if (pageable == null) {
			pageable = new Pageable();
		}
		try {
			String text = QueryParser.escape(keyword);
			QueryParser titleParser = new QueryParser(Version.LUCENE_35, "title", new IKAnalyzer());
			titleParser.setDefaultOperator(QueryParser.AND_OPERATOR);
			Query titleQuery = titleParser.parse(text);
			FuzzyQuery titleFuzzyQuery = new FuzzyQuery(new Term("title", text), FUZZY_QUERY_MINIMUM_SIMILARITY);
			Query contentQuery = new TermQuery(new Term("content", text));
			Query isPublicationQuery = new TermQuery(new Term("isPublication", "true"));
			BooleanQuery textQuery = new BooleanQuery();
			BooleanQuery query = new BooleanQuery();
			textQuery.add(titleQuery, Occur.SHOULD);
			textQuery.add(titleFuzzyQuery, Occur.SHOULD);
			textQuery.add(contentQuery, Occur.SHOULD);
			query.add(isPublicationQuery, Occur.MUST);
			query.add(textQuery, Occur.MUST);
			FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
			FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(query, Article.class);
			fullTextQuery.setSort(new Sort(new SortField[] { new SortField("isTop", SortField.STRING, true), new SortField(null, SortField.SCORE), new SortField("createDate", SortField.LONG, true) }));
			fullTextQuery.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize());
			fullTextQuery.setMaxResults(pageable.getPageSize());
			return new Page<Article>(fullTextQuery.getResultList(), fullTextQuery.getResultSize(), pageable);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Page<Article>();*/
	}

	/**
	 * 搜索商品分页
	 * 
	 * @param keyword
	 *            关键词
	 * @param startPrice
	 *            最低价格
	 * @param endPrice
	 *            最高价格
	 * @param orderType
	 *            排序类型
	 * @param pageable
	 *            分页信息
	 * @return 商品分页
	 */
	public Page<Product> search(String keyword, BigDecimal startPrice, BigDecimal endPrice, OrderType orderType, Pageable pageable) {
		return null;
		/*if (StringUtils.isEmpty(keyword)) {
			return new Page<Product>();
		}
		if (pageable == null) {
			pageable = new Pageable();
		}
		try {
			String text = QueryParser.escape(keyword);
			TermQuery snQuery = new TermQuery(new Term("sn", text));
			Query keywordQuery = new QueryParser(Version.LUCENE_35, "keyword", new IKAnalyzer()).parse(text);
			QueryParser nameParser = new QueryParser(Version.LUCENE_35, "name", new IKAnalyzer());
			nameParser.setDefaultOperator(QueryParser.AND_OPERATOR);
			Query nameQuery = nameParser.parse(text);
			FuzzyQuery nameFuzzyQuery = new FuzzyQuery(new Term("name", text), FUZZY_QUERY_MINIMUM_SIMILARITY);
			TermQuery introductionQuery = new TermQuery(new Term("introduction", text));
			TermQuery isMarketableQuery = new TermQuery(new Term("isMarketable", "true"));
			TermQuery isListQuery = new TermQuery(new Term("isList", "true"));
			TermQuery isGiftQuery = new TermQuery(new Term("isGift", "false"));
			BooleanQuery textQuery = new BooleanQuery();
			BooleanQuery query = new BooleanQuery();
			textQuery.add(snQuery, Occur.SHOULD);
			textQuery.add(keywordQuery, Occur.SHOULD);
			textQuery.add(nameQuery, Occur.SHOULD);
			textQuery.add(nameFuzzyQuery, Occur.SHOULD);
			textQuery.add(introductionQuery, Occur.SHOULD);
			query.add(isMarketableQuery, Occur.MUST);
			query.add(isListQuery, Occur.MUST);
			query.add(isGiftQuery, Occur.MUST);
			query.add(textQuery, Occur.MUST);
			if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
				BigDecimal temp = startPrice;
				startPrice = endPrice;
				endPrice = temp;
			}
			if (startPrice != null && startPrice.compareTo(new BigDecimal(0)) >= 0 && endPrice != null && endPrice.compareTo(new BigDecimal(0)) >= 0) {
				NumericRangeQuery<Double> numericRangeQuery = NumericRangeQuery.newDoubleRange("price", startPrice.doubleValue(), endPrice.doubleValue(), true, true);
				query.add(numericRangeQuery, Occur.MUST);
			} else if (startPrice != null && startPrice.compareTo(new BigDecimal(0)) >= 0) {
				NumericRangeQuery<Double> numericRangeQuery = NumericRangeQuery.newDoubleRange("price", startPrice.doubleValue(), null, true, false);
				query.add(numericRangeQuery, Occur.MUST);
			} else if (endPrice != null && endPrice.compareTo(new BigDecimal(0)) >= 0) {
				NumericRangeQuery<Double> numericRangeQuery = NumericRangeQuery.newDoubleRange("price", null, endPrice.doubleValue(), false, true);
				query.add(numericRangeQuery, Occur.MUST);
			}
			FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
			FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(query, Product.class);
			SortField[] sortFields = null;
			if (orderType == OrderType.priceAsc) {
				sortFields = new SortField[] { new SortField("price", SortField.DOUBLE, false), new SortField("createDate", SortField.LONG, true) };
			} else if (orderType == OrderType.priceDesc) {
				sortFields = new SortField[] { new SortField("price", SortField.DOUBLE, true), new SortField("createDate", SortField.LONG, true) };
			} else if (orderType == OrderType.salesDesc) {
				sortFields = new SortField[] { new SortField("sales", SortField.INT, true), new SortField("createDate", SortField.LONG, true) };
			} else if (orderType == OrderType.scoreDesc) {
				sortFields = new SortField[] { new SortField("score", SortField.INT, true), new SortField("createDate", SortField.LONG, true) };
			} else if (orderType == OrderType.dateDesc) {
				sortFields = new SortField[] { new SortField("createDate", SortField.LONG, true) };
			} else {
				sortFields = new SortField[] { new SortField("isTop", SortField.STRING, true), new SortField(null, SortField.SCORE), new SortField("modifyDate", SortField.LONG, true) };
			}
			fullTextQuery.setSort(new Sort(sortFields));
			fullTextQuery.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize());
			fullTextQuery.setMaxResults(pageable.getPageSize());
			return new Page<Product>(fullTextQuery.getResultList(), fullTextQuery.getResultSize(), pageable);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Page<Product>();*/
	}

}
