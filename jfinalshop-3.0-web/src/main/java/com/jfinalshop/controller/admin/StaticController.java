package com.jfinalshop.controller.admin;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.ArticleCategory;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.service.ArticleCategoryService;
import com.jfinalshop.service.ArticleService;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.service.StaticService;

/**
 * Controller - 静态化
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/static")
public class StaticController extends BaseAdminController {

	private ArticleService articleService = enhance(ArticleService.class);
	private ArticleCategoryService articleCategoryService = enhance(ArticleCategoryService.class);
	private ProductService productService = enhance(ProductService.class);
	private ProductCategoryService productCategoryService = enhance(ProductCategoryService.class);
	private StaticService staticService = enhance(StaticService.class);
	
	/**
	 * 生成类型
	 */
	public enum BuildType {
		/**
		 * 首页
		 */
		index,
		/**
		 * 文章
		 */
		article,
		/**
		 * 商品
		 */
		product,
		/**
		 * 其它
		 */
		other
	}
	
	/**
	 * 生成静态
	 */
	public void build() {
		setAttr("buildTypes", BuildType.values());
		setAttr("defaultBeginDate", DateUtils.addDays(new Date(), -7));
		setAttr("defaultEndDate", new Date());
		setAttr("articleCategoryTree", articleCategoryService.findChildren(null));
		setAttr("productCategoryTree", productCategoryService.findChildren(null, null));
		render("/admin/static/build.html");
	}

	/**
	 * 生成静态
	 */
	public void submit() {
		BuildType buildType = BuildType.valueOf(getPara("buildType", ""));
		Long articleCategoryId = getParaToLong("articleCategoryId");
		Long productCategoryId = getParaToLong("productCategoryId");
		Date beginDate = getParaToDate("beginDate");
		Date endDate = getParaToDate("endDate");
		Integer first = getParaToInt("first");
		Integer count = getParaToInt("count");
		
		long startTime = System.currentTimeMillis();
		if (beginDate != null) {
			Calendar calendar = DateUtils.toCalendar(beginDate);
			calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
			calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
			beginDate = calendar.getTime();
		}
		if (endDate != null) {
			Calendar calendar = DateUtils.toCalendar(endDate);
			calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
			calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
			endDate = calendar.getTime();
		}
		if (first == null || first < 0) {
			first = 0;
		}
		if (count == null || count <= 0) {
			count = 50;
		}
		int buildCount = 0;
		boolean isCompleted = true;
		if (buildType == BuildType.index) {
			buildCount = staticService.buildIndex();
		} else if (buildType == BuildType.article) {
			ArticleCategory articleCategory = articleCategoryService.find(articleCategoryId);
			List<Article> articles = articleService.findList(articleCategory, beginDate, endDate, first, count);
			for (Article article : articles) {
				buildCount += staticService.build(article);
			}
			first += articles.size();
			if (articles.size() == count) {
				isCompleted = false;
			}
		} else if (buildType == BuildType.product) {
			ProductCategory productCategory = productCategoryService.find(productCategoryId);
			List<Product> products = productService.findList(productCategory, beginDate, endDate, first, count);
			for (Product product : products) {
				buildCount += staticService.build(product);
			}
			first += products.size();
			if (products.size() == count) {
				isCompleted = false;
			}
		} else if (buildType == BuildType.other) {
			buildCount = staticService.buildOther();
		}
		long endTime = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("first", first);
		map.put("buildCount", buildCount);
		map.put("buildTime", endTime - startTime);
		map.put("isCompleted", isCompleted);
		renderJson(map);
	}
}
