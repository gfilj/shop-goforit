package com.jfinalshop.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.CommonAttributes;
import com.jfinalshop.common.Filter;
import com.jfinalshop.common.Order;
import com.jfinalshop.common.Order.Direction;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.base.BaseArticle;
import com.jfinalshop.utils.ConditionUtil;
import com.jfinalshop.utils.DateUtil;
import com.jfinalshop.utils.FreemarkerUtils;

import freemarker.template.TemplateException;

/**
 * Dao - 文章
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Article extends BaseArticle<Article> {
	public static final Article dao = new Article();
	
	/** 点击数缓存名称 */
	public static final String HITS_CACHE_NAME = "articleHits";

	/** 点击数缓存更新间隔时间 */
	public static final int HITS_CACHE_INTERVAL = 600000;

	/** 内容分页长度 */
	private static final int PAGE_CONTENT_LENGTH = 800;

	/** 内容分页符 */
	private static final String PAGE_BREAK_SEPARATOR = "<hr class=\"pageBreak\" />";

	/** 段落分隔符配比 */
	private static final Pattern PARAGRAPH_SEPARATOR_PATTERN = Pattern.compile("[,;\\.!?，；。！？]");
	
	/** 页码 */
	private Integer pageNumber;
	
	/** 静态路径 */
	private static String staticPath;
	
	/** 标签 */
	private List<Tag> tags = new ArrayList<Tag>();
	
	static {
		try {
			File shopxxXmlFile = new File(PathKit.getRootClassPath() + CommonAttributes.SHOPXX_XML_PATH);
			org.dom4j.Document document = new SAXReader().read(shopxxXmlFile);
			org.dom4j.Element element = (org.dom4j.Element) document.selectSingleNode("/jfinalshopxx/template[@id='articleContent']");
			staticPath = element.attributeValue("staticPath");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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
	 * @return 文章
	 */
	public List<Article> findList(ArticleCategory articleCategory, List<Tag> tags, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT a.*  FROM `article` `a` LEFT JOIN `article_category` `ac` ON `a`.`article_category_id` = `ac`.`id` WHERE `a`.`is_publication` = true ";
		if (articleCategory != null) {
			sql += " AND `ac`.`tree_path` LIKE '%" + ArticleCategory.TREE_PATH_SEPARATOR + articleCategory.getId() + ArticleCategory.TREE_PATH_SEPARATOR + "%' ";
		}
		if (tags != null && !tags.isEmpty()) {
			sql += " AND EXISTS (SELECT * FROM `article_tag` `at` WHERE `a`.`id` = `at`.`articles` AND `at`.`tags` IN  ( ";
			StringBuffer sb = new StringBuffer();
			int maxSize = tags.size() - 1;
            for (int i = 0; i < tags.size(); i++) {
            	 sb.append(i == maxSize ? tags.get(maxSize).getId() + ")" : tags.get(i).getId() + ",");
            }
			sql += sb.toString();
		}
		Order order = new Order("isTop", Direction.desc);
		orders.add(order);
		sql += ConditionUtil.buildSQL(null, count, filters, orders);
		return find(sql);
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
	 * @return 文章
	 */
	public List<Article> findList(ArticleCategory articleCategory, Date beginDate, Date endDate, Integer first, Integer count) {
		String sql = "SELECT a.*  FROM `article` `a` LEFT JOIN `article_category` `ac` ON `a`.`article_category_id` = `ac`.`id` WHERE `a`.`is_publication` = true ";
		if (articleCategory != null) {
			sql += " AND `ac`.`tree_path` LIKE '%" + ArticleCategory.TREE_PATH_SEPARATOR + articleCategory.getId() + ArticleCategory.TREE_PATH_SEPARATOR + "%' ";
		}
		if (beginDate != null) {
			sql += "  AND `a`.creation_date >= '" + DateUtil.getDateTime(beginDate) + "'";
		}
		
		if (endDate != null) {
			sql += "  AND `a`.creation_date <= '" + DateUtil.getDateTime(beginDate) + "'";
		}
		List<Order> orders = new ArrayList<Order>();
		Order order = new Order("isTop", Direction.desc);
		orders.add(order);
		sql += ConditionUtil.buildSQL(first, count, null, orders);
		return find(sql);
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
	 * @return 文章分页
	 */
	public Page<Article> findPage(ArticleCategory articleCategory, List<Tag> tags, Pageable pageable) {
		String select = " SELECT a.* ";
		String sqlExceptSelect = " FROM `article` `a` LEFT JOIN `article_category` `ac` ON `a`.`article_category_id` = `ac`.`id` WHERE `a`.`is_publication` = true ";
		if (articleCategory != null) {
			sqlExceptSelect += " AND `ac`.`tree_path` LIKE '%" + ArticleCategory.TREE_PATH_SEPARATOR + articleCategory.getId() + ArticleCategory.TREE_PATH_SEPARATOR + "%' ";
		}
		if (tags != null && !tags.isEmpty()) {
			sqlExceptSelect += " AND EXISTS (SELECT * FROM `article_tag` `at` WHERE `a`.`id` = `at`.`articles` AND `at`.`tags` IN  ( ";
			StringBuffer instr = new StringBuffer();
			int maxSize = tags.size() - 1;
            for (int i = 0; i < tags.size(); i++) {
            	 instr.append(tags.get(i).getId());
            	 instr.append(i == maxSize ? tags.get(maxSize) + "))" : tags.get(i) + ",");
            }
			sqlExceptSelect += instr.toString();
		}
		sqlExceptSelect += " ORDER BY `a`.`is_top` DESC ";
		Page<Article> articles = paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect);
		return articles;
	}
	
	
	/**
	 * 获取文章分类
	 * 
	 * @return 文章分类
	 */
	public ArticleCategory getArticleCategory() {
		return ArticleCategory.dao.findById(getArticleCategoryId());
	}
	
	/**
	 * 获取标签
	 * 
	 * @return 标签
	 */
	public List<Tag> getTags() {
		String sql = "SELECT t.* FROM `article_tag` a LEFT JOIN `tag` t ON a.`tags` = t.`id` WHERE a.`articles` = ?";
		if (tags.isEmpty()) {
			tags = Tag.dao.find(sql, getId());
		}
		return tags;
	}

	/**
	 * 设置标签
	 * 
	 * @param tags
	 *            标签
	 */
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	
	/**
	 * 获取访问路径
	 * 
	 * @return 访问路径
	 */
	public String getPath() {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("id", getId());
		model.put("createDate", getCreationDate());
		model.put("lastUpdatedDate", getLastUpdatedDate());
		model.put("title", getTitle());
		model.put("seoTitle", getSeoTitle());
		model.put("seoKeywords", getSeoKeywords());
		model.put("seoDescription", getSeoDescription());
		model.put("pageNumber", getPageNumber());
		model.put("articleCategory", getArticleCategory());
		try {
			return FreemarkerUtils.process(staticPath, model);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 设置页面关键词
	 * 
	 * @param seoKeywords
	 *            页面关键词
	 */
	public void setSeoKeywords(String seoKeywords) {
		if (seoKeywords != null) {
			seoKeywords = seoKeywords.replaceAll("[,\\s]*,[,\\s]*", ",").replaceAll("^,|,$", "");
		}
		this.setSeoKeywords(seoKeywords);
	}
	
	
	/**
	 * 获取文本内容
	 * 
	 * @return 文本内容
	 */
	public String getText() {
		if (getContent() != null) {
			return Jsoup.parse(getContent()).text();
		}
		return null;
	}

	/**
	 * 获取分页内容
	 * 
	 * @return 分页内容
	 */
	public String[] getPageContents() {
		if (StringUtils.isEmpty(getContent())) {
			return new String[] { "" };
		}
		if (getContent().contains(PAGE_BREAK_SEPARATOR)) {
			return getContent().split(PAGE_BREAK_SEPARATOR);
		} else {
			List<String> pageContents = new ArrayList<String>();
			Document document = Jsoup.parse(getContent());
			List<Node> children = document.body().childNodes();
			if (children != null) {
				int textLength = 0;
				StringBuffer html = new StringBuffer();
				for (Node node : children) {
					if (node instanceof Element) {
						Element element = (Element) node;
						html.append(element.outerHtml());
						textLength += element.text().length();
						if (textLength >= PAGE_CONTENT_LENGTH) {
							pageContents.add(html.toString());
							textLength = 0;
							html.setLength(0);
						}
					} else if (node instanceof TextNode) {
						TextNode textNode = (TextNode) node;
						String text = textNode.text();
						String[] contents = PARAGRAPH_SEPARATOR_PATTERN.split(text);
						Matcher matcher = PARAGRAPH_SEPARATOR_PATTERN.matcher(text);
						for (String content : contents) {
							if (matcher.find()) {
								content += matcher.group();
							}
							html.append(content);
							textLength += content.length();
							if (textLength >= PAGE_CONTENT_LENGTH) {
								pageContents.add(html.toString());
								textLength = 0;
								html.setLength(0);
							}
						}
					}
				}
				String pageContent = html.toString();
				if (StringUtils.isNotEmpty(pageContent)) {
					pageContents.add(pageContent);
				}
			}
			return pageContents.toArray(new String[pageContents.size()]);
		}
	}
	
	
	/**
	 * 获取总页数
	 * 
	 * @return 总页数
	 */
	public int getTotalPages() {
		return getPageContents().length;
	}
	
	/**
	 * 获取页码
	 * 
	 * @return 页码
	 */
	public Integer getPageNumber() {
		return pageNumber;
	}

	/**
	 * 设置页码
	 * 
	 * @param pageNumber
	 *            页码
	 */
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	
}
