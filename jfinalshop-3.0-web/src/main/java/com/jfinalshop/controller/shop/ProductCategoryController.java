/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop;

import com.jfinalshop.service.ProductCategoryService;

/**
 * Controller - 商品分类
 * 
 * 
 * 
 */
public class ProductCategoryController extends BaseShopController {

	private ProductCategoryService productCategoryService = enhance(ProductCategoryService.class);

	/**
	 * 首页
	 */
	public void index() {
		setAttr("rootProductCategories", productCategoryService.findRoots());
		render("/shop/product_category/index.html");
	}

}