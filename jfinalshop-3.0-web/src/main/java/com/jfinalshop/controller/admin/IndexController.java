package com.jfinalshop.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.Product;
import com.jfinalshop.service.ArticleService;
import com.jfinalshop.service.ProductService;

/**
 * Controller - 索引
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/index")
public class IndexController extends BaseAdminController {

	private ArticleService articleService = enhance(ArticleService.class);
	private ProductService productService = enhance(ProductService.class);

	/**
	 * 生成类型
	 */
	public enum BuildType {
		/**
		 * 文章
		 */
		article,
		/**
		 * 商品
		 */
		product
	}
	
	/**
	 * 生成索引
	 */
	public void build() {
		setAttr("buildTypes", BuildType.values());
		render("/admin/index/build.html");
	}
	
	/**
	 * 提交生成索引
	 */
	public void submit() {
		BuildType buildType = BuildType.valueOf(getPara("buildType", ""));
		Boolean isPurge = getParaToBoolean("isPurge");
		Integer first = getParaToInt("first");
		Integer count = getParaToInt("count");
		
		long startTime = System.currentTimeMillis();
		if (first == null || first < 0) {
			first = 0;
		}
		if (count == null || count <= 0) {
			count = 50;
		}
		int buildCount = 0;
		boolean isCompleted = true;
		if (buildType == BuildType.article) {
			if (first == 0 && isPurge != null && isPurge) {
				//searchService.purge(Article.class);
			}
			List<Article> articles = articleService.findList(null, null, null, first, count);
			for (Article article : articles) {
				//searchService.index(article);
				buildCount++;
			}
			first += articles.size();
			if (articles.size() == count) {
				isCompleted = false;
			}
		} else if (buildType == BuildType.product) {
			if (first == 0 && isPurge != null && isPurge) {
				//searchService.purge(Product.class);
			}
			List<Product> products = productService.findList(null, null, null, first, count);
			for (Product product : products) {
				//searchService.index(product);
				buildCount++;
			}
			first += products.size();
			if (products.size() == count) {
				isCompleted = false;
			}
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
