package com.jfinalshop.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;

import com.jfinal.core.JFinal;
import com.jfinal.render.FreeMarkerRender;
import com.jfinalshop.common.Template;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.utils.AssertUtil;
import com.jfinalshop.utils.FreemarkerUtils;

import freemarker.template.Configuration;

/**
 * Service - 静态化
 * 
 * 
 * 
 */
public class StaticService {
	
	private ServletContext servletContext = JFinal.me().getServletContext();
	private Configuration configuration = FreeMarkerRender.getConfiguration();
	
	public static final StaticService service = new StaticService();
	
	/** Sitemap最大地址数 */
	private static final Integer SITEMAP_MAX_SIZE = 40000;
	
	/**
	 * 生成静态
	 * 
	 * @param templatePath
	 *            模板文件路径
	 * @param staticPath
	 *            静态文件路径
	 * @param model
	 *            数据
	 * @return 生成数量
	 */
	public int build(String templatePath, String staticPath, Map<String, Object> model) {
		AssertUtil.hasText(templatePath);
		AssertUtil.hasText(staticPath);

		FileOutputStream fileOutputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		Writer writer = null;
		try {
			freemarker.template.Template template = configuration.getTemplate(templatePath);
			File staticFile = new File(servletContext.getRealPath(staticPath));
			File staticDirectory = staticFile.getParentFile();
			if (!staticDirectory.exists()) {
				staticDirectory.mkdirs();
			}
			fileOutputStream = new FileOutputStream(staticFile);
			outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
			writer = new BufferedWriter(outputStreamWriter);
			template.process(model, writer);
			writer.flush();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(outputStreamWriter);
			IOUtils.closeQuietly(fileOutputStream);
		}
		return 0;
	}
	
	/**
	 * 生成静态
	 * 
	 * @param templatePath
	 *            模板文件路径
	 * @param staticPath
	 *            静态文件路径
	 * @return 生成数量
	 */
	public int build(String templatePath, String staticPath) {
		return build(templatePath, staticPath, null);
	}
	
	/**
	 * 生成静态
	 * 
	 * @param article
	 *            文章
	 * @return 生成数量
	 */
	public int build(Article article) {
		AssertUtil.notNull(article);

		delete(article);
		Template template = TemplateService.service.get("articleContent");
		int buildCount = 0;
		if (article.getIsPublication()) {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("article", article);
			for (int pageNumber = 1; pageNumber <= article.getTotalPages(); pageNumber++) {
				article.setPageNumber(pageNumber);
				buildCount += build(template.getTemplatePath(), article.getPath(), model);
			}
			article.setPageNumber(null);
		}
		return buildCount;
	}
	
	/**
	 * 生成静态
	 * 
	 * @param product
	 *            商品
	 * @return 生成数量
	 */
	public int build(Product product) {
		AssertUtil.notNull(product);
		delete(product);
		Template template = TemplateService.service.get("productContent");
		int buildCount = 0;
		if (product.getIsMarketable()) {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("product", product);
			buildCount += build(template.getTemplatePath(), product.getPath(), model);
		}
		return buildCount;
	}

	
	/**
	 * 生成首页静态
	 * 
	 * @return 生成数量
	 */
	public int buildIndex() {
		Template template = TemplateService.service.get("index");
		return build(template.getTemplatePath(), template.getStaticPath());
	}
	
	/**
	 * 生成Sitemap
	 * 
	 * @return 生成数量
	 */
	public int buildSitemap() {
		int buildCount = 0;
		Template sitemapIndexTemplate = TemplateService.service.get("sitemapIndex");
		Template sitemapTemplate = TemplateService.service.get("sitemap");
		Map<String, Object> model = new HashMap<String, Object>();
		List<String> staticPaths = new ArrayList<String>();
		for (int step = 0, index = 0, first = 0, count = SITEMAP_MAX_SIZE;;) {
			try {
				model.put("index", index);
				String templatePath = sitemapTemplate.getTemplatePath();
				String staticPath = FreemarkerUtils.process(sitemapTemplate.getStaticPath(), model);
				if (step == 0) {
					List<Article> articles = ArticleService.service.findList(first, count, null, null);
					model.put("articles", articles);
					if (articles.size() < count) {
						step++;
						first = 0;
						count -= articles.size();
					} else {
						buildCount += build(templatePath, staticPath, model);
						//Article.dao.clear();
						//Article.dao.flush();
						staticPaths.add(staticPath);
						model.clear();
						index++;
						first += articles.size();
						count = SITEMAP_MAX_SIZE;
					}
				} else if (step == 1) {
					List<Product> products = ProductService.service.findList(first, count, null, null);
					model.put("products", products);
					if (products.size() < count) {
						step++;
						first = 0;
						count -= products.size();
					} else {
						buildCount += build(templatePath, staticPath, model);
						//productDao.clear();
						//productDao.flush();
						staticPaths.add(staticPath);
						model.clear();
						index++;
						first += products.size();
						count = SITEMAP_MAX_SIZE;
					}
				} else if (step == 2) {
					List<Brand> brands = BrandService.service.findList(first, count, null, null);
					model.put("brands", brands);
					if (brands.size() < count) {
						step++;
						first = 0;
						count -= brands.size();
					} else {
						buildCount += build(templatePath, staticPath, model);
						//brandDao.clear();
						//brandDao.flush();
						staticPaths.add(staticPath);
						model.clear();
						index++;
						first += brands.size();
						count = SITEMAP_MAX_SIZE;
					}
				} else if (step == 3) {
					List<Promotion> promotions = PromotionService.service.findList(first, count, null, null);
					model.put("promotions", promotions);
					buildCount += build(templatePath, staticPath, model);
					//promotionDao.clear();
					//promotionDao.flush();
					staticPaths.add(staticPath);
					if (promotions.size() < count) {
						model.put("staticPaths", staticPaths);
						buildCount += build(sitemapIndexTemplate.getTemplatePath(), sitemapIndexTemplate.getStaticPath(), model);
						break;
					} else {
						model.clear();
						index++;
						first += promotions.size();
						count = SITEMAP_MAX_SIZE;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return buildCount;
	}
	
	
	/**
	 * 生成其它静态
	 * 
	 * @return 生成数量
	 */
	public int buildOther() {
		int buildCount = 0;
		Template shopCommonJsTemplate = TemplateService.service.get("shopCommonJs");
		Template adminCommonJsTemplate = TemplateService.service.get("adminCommonJs");
		buildCount += build(shopCommonJsTemplate.getTemplatePath(), shopCommonJsTemplate.getStaticPath());
		buildCount += build(adminCommonJsTemplate.getTemplatePath(), adminCommonJsTemplate.getStaticPath());
		return buildCount;
	}
	
	
	/**
	 * 生成所有静态
	 * 
	 * @return 生成数量
	 */
	public int buildAll() {
		int buildCount = 0;
		for (int i = 0; i < ArticleService.service.count(); i += 20) {
			List<Article> articles = ArticleService.service.findList(i, 20, null, null);
			for (Article article : articles) {
				buildCount += build(article);
			}
			Article.dao.clear();
		}
		for (int i = 0; i < ProductService.service.count(); i += 20) {
			List<Product> products = ProductService.service.findList(i, 20, null, null);
			for (Product product : products) {
				buildCount += build(product);
			}
			Product.dao.clear();
		}
		buildIndex();
		buildSitemap();
		buildOther();
		return buildCount;
	}
	
	/**
	 * 删除静态
	 * 
	 * @param staticPath
	 *            静态文件路径
	 * @return 删除数量
	 */
	public int delete(String staticPath) {
		AssertUtil.hasText(staticPath);
		File staticFile = new File(servletContext.getRealPath(staticPath));
		if (staticFile.exists()) {
			staticFile.delete();
			return 1;
		}
		return 0;
	}
	
	/**
	 * 删除静态
	 * 
	 * @param article
	 *            文章
	 * @return 删除数量
	 */
	public int delete(Article article) {
		AssertUtil.notNull(article);

		int deleteCount = 0;
		for (int pageNumber = 1; pageNumber <= article.getTotalPages() + 1000; pageNumber++) {
			article.setPageNumber(pageNumber);
			int count = delete(article.getPath());
			if (count < 1) {
				break;
			}
			deleteCount += count;
		}
		article.setPageNumber(null);
		return deleteCount;
	}
	
	/**
	 * 删除静态
	 * 
	 * @param product
	 *            商品
	 * @return 删除数量
	 */
	public int delete(Product product) {
		AssertUtil.notNull(product);
		return delete(product.getPath());
	}
	/**
	 * 删除首页静态
	 * 
	 * @return 删除数量
	 */
	public int deleteIndex() {
		Template template = TemplateService.service.get("index");
		return delete(template.getStaticPath());
	}
	
	/**
	 * 删除其它静态
	 * 
	 * @return 删除数量
	 */
	public int deleteOther() {
		int deleteCount = 0;
		Template shopCommonJsTemplate = TemplateService.service.get("shopCommonJs");
		Template adminCommonJsTemplate = TemplateService.service.get("adminCommonJs");
		deleteCount += delete(shopCommonJsTemplate.getStaticPath());
		deleteCount += delete(adminCommonJsTemplate.getStaticPath());
		return deleteCount;
	}
	
}
