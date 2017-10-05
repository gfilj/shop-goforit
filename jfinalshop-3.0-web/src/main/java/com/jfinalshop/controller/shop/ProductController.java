/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.kit.StrKit;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.common.ResourceNotFoundException;
import com.jfinalshop.model.Attribute;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Product.OrderType;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.model.Tag;
import com.jfinalshop.service.BrandService;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.service.PromotionService;
import com.jfinalshop.service.SearchService;
import com.jfinalshop.service.TagService;

/**
 * Controller - 商品
 * 
 * 
 * 
 */
public class ProductController extends BaseShopController {

	private ProductService productService = enhance(ProductService.class);
	private ProductCategoryService productCategoryService = enhance(ProductCategoryService.class);
	private BrandService brandService = enhance(BrandService.class);
	private PromotionService promotionService = enhance(PromotionService.class);
	private TagService tagService = enhance(TagService.class);
	private SearchService searchService = enhance(SearchService.class);

	/**
	 * 浏览记录
	 */
	public void history() {
		Long[] ids = getParaValuesToLong("ids");
		renderJson(productService.findList(ids));
	}
	
	public void demo(){
		renderText("Hello World!!");
	}

	/**
	 * 列表
	 */
	public void list() {
		Long productCategoryId = getParaToLong(0);
		Long brandId = getParaToLong("brandId");
		Long promotionId = getParaToLong("promotionId");
		Long[] tagIds = getParaValuesToLong("tagIds");
		BigDecimal startPrice = new BigDecimal(getPara("startPrice","0"));
		BigDecimal endPrice = new BigDecimal(getPara("endPrice","0"));
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Integer pageSize = getParaToInt("pageSize", 10);
		OrderType orderType = StrKit.notBlank(getPara("orderType")) ? OrderType.valueOf(getPara("orderType")) : null;
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		if (productCategory == null) {
			throw new ResourceNotFoundException();
		}
		Brand brand = brandService.find(brandId);
		Promotion promotion = promotionService.find(promotionId);
		List<Tag> tags = tagService.findList(tagIds);
		Map<Attribute, String> attributeValue = new HashMap<Attribute, String>();
		if (productCategory != null) {
			List<Attribute> attributes = productCategory.getAttributes();
			for (Attribute attribute : attributes) {
				String value = getPara("attribute_" + attribute.getId());
				if (StringUtils.isNotEmpty(value) && attribute.getOptions().contains(value)) {
					attributeValue.put(attribute, value);
				}
			}
		}
		Pageable pageable = new Pageable(pageNumber, pageSize);
		setAttr("orderTypes", OrderType.values());
		setAttr("productCategory", productCategory);
		setAttr("brand", brand);
		setAttr("promotion", promotion);
		setAttr("tags", tags);
		setAttr("attributeValue", attributeValue);
		setAttr("startPrice", startPrice);
		setAttr("endPrice", endPrice);
		setAttr("orderType", orderType);
		setAttr("pageNumber", pageNumber);
		setAttr("pageSize", pageSize);
		setAttr("page", productService.findPage(productCategory, brand, promotion, tags, attributeValue, startPrice, endPrice, true, true, null, false, null, null, orderType, pageable));
		render("/shop/product/list.html");
	}

	/**
	 * 列表
	 */
	public void list2() {
		Long brandId = getParaToLong("brandId");
		Long promotionId = getParaToLong("promotionId");
		Long[] tagIds = getParaValuesToLong("tagIds");
		BigDecimal startPrice = new BigDecimal(getPara("startPrice","0"));
		BigDecimal endPrice = new BigDecimal(getPara("endPrice", "0"));
		OrderType orderType = getPara("orderType") != null ? OrderType.valueOf(getPara("orderType")) : null;
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Integer pageSize = getParaToInt("pageSize", 10);
		Brand brand = brandService.find(brandId);
		Promotion promotion = promotionService.find(promotionId);
		List<Tag> tags = tagService.findList(tagIds);
		Pageable pageable = new Pageable(pageNumber, pageSize);
		setAttr("orderTypes", OrderType.values());
		setAttr("brand", brand);
		setAttr("promotion", promotion);
		setAttr("tags", tags);
		setAttr("startPrice", startPrice);
		setAttr("endPrice", endPrice);
		setAttr("orderType", orderType);
		setAttr("pageNumber", pageNumber);
		setAttr("pageSize", pageSize);
		setAttr("page", productService.findPage(null, brand, promotion, tags, null, startPrice, endPrice, true, true, null, false, null, null, orderType, pageable));
		render("/shop/product/list.html");
	}

	/**
	 * 搜索
	 */
	public void search() {
		String keyword = getPara("keyword");
		BigDecimal startPrice = new BigDecimal(getPara("startPrice"));
		BigDecimal endPrice = new BigDecimal(getPara("endPrice"));
		OrderType orderType = OrderType.valueOf(getPara("orderType"));
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Integer pageSize = getParaToInt("pageSize", 10);
		if (StringUtils.isEmpty(keyword)) {
			renderJson(ERROR_VIEW);
		}
		Pageable pageable = new Pageable(pageNumber, pageSize);
		setAttr("orderTypes", OrderType.values());
		setAttr("productKeyword", keyword);
		setAttr("startPrice", startPrice);
		setAttr("endPrice", endPrice);
		setAttr("orderType", orderType);
		setAttr("page", searchService.search(keyword, startPrice, endPrice, orderType, pageable));
		render("/shop/product/search.html");
	}

	/**
	 * 点击数
	 */
	public void hits() {
		Long id = getParaToLong(0);
		renderJson(productService.viewHits(id));
	}

}