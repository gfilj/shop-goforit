/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop;

import com.jfinalshop.common.Pageable;
import com.jfinalshop.common.ResourceNotFoundException;
import com.jfinalshop.model.Brand;
import com.jfinalshop.service.BrandService;

/**
 * Controller - 品牌
 * 
 * 
 * 
 */
public class BrandController extends BaseShopController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 40;

	private BrandService brandService = enhance(BrandService.class);

	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", brandService.findPage(pageable));
		render("/shop/brand/list");
	}

	/**
	 * 内容
	 */
	public void content() {
		Long id = getParaToLong("id");
		Brand brand = brandService.find(id);
		if (brand == null) {
			throw new ResourceNotFoundException();
		}
		setAttr("brand", brand);
		render("/shop/brand/content.html"); ;
	}

}