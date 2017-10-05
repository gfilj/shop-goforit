package com.jfinalshop.service;

import java.util.List;

import com.jfinal.plugin.ehcache.CacheName;
import com.jfinalshop.common.Filter;
import com.jfinalshop.common.Order;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.ProductCategoryBrand;


/**
 * Service - 品牌
 * 
 * 
 * 
 */
public class BrandService extends BaseService<Brand> {
	public static final BrandService service = new BrandService();
	
	public BrandService() {
		super(Brand.class);
	}
	
	/**
	 * 查找品牌(缓存)
	 * 
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param cacheRegion
	 *            缓存区域
	 * @return 品牌(缓存)
	 */
	@CacheName("brand")
	public List<Brand> findList(Integer count, List<Filter> filters, List<Order> orders, String cacheRegion) {
		return findList(null, count, filters, orders);
	}
	
	
	/**
	 * 保存
	 * 
	 * 
	 */
	public boolean save(ProductCategory productCategory) {
		boolean result = false;
		if (!productCategory.getBrands().isEmpty()) {
			for (Brand brand : productCategory.getBrands()) {
				ProductCategoryBrand productCategoryBrand = new ProductCategoryBrand();
				productCategoryBrand.setBrands(brand.getId());
				productCategoryBrand.setProductCategories(productCategory.getId());
				productCategoryBrand.save();
			}
		}
		return result;
	}
	
	/**
	 * 更新
	 * 
	 * 
	 */
	public boolean update(ProductCategory productCategory) {
		boolean result = false;
		if (!productCategory.getBrands().isEmpty()) {
			ProductCategoryBrand.dao.delete(productCategory.getId());
			for (Brand brand : productCategory.getBrands()) {
				ProductCategoryBrand productCategoryBrand = new ProductCategoryBrand();
				productCategoryBrand.setBrands(brand.getId());
				productCategoryBrand.setProductCategories(productCategory.getId());
				productCategoryBrand.save();
			}
		}
		return result;
	}
	
}
