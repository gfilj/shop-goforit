package com.jfinalshop.service;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.ProductCategoryBrand;

/**
 * Service - 商品分类
 * 
 * 
 * 
 */
public class ProductCategoryService extends BaseService<ProductCategory> {
	
	public ProductCategoryService() {
		super(ProductCategory.class);
	}
	
	/**
	 * 查找顶级商品分类
	 * 
	 * @return 顶级商品分类
	 */
	public List<ProductCategory> findRoots() {
		return ProductCategory.dao.findRoots(null);
	}
	
	/**
	 * 查找商品分类树
	 * 
	 * @return 商品分类树
	 */
	public List<ProductCategory> findTree() {
		return ProductCategory.dao.findChildren(null, null);
	}

	/**
	 * 查找下级商品分类
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param count
	 *            数量
	 * @return 下级商品分类
	 */
	public List<ProductCategory> findChildren(ProductCategory productCategory, Integer count) {
		return ProductCategory.dao.findChildren(productCategory, count);
	}
	
	
	/**
	 * 查找下级商品分类(缓存)
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param count
	 *            数量
	 * @param cacheRegion
	 *            缓存区域
	 * @return 下级商品分类(缓存)
	 */
	@CacheName("productCategory")
	public List<ProductCategory> findChildren(ProductCategory productCategory, Integer count, String cacheRegion) {
		return ProductCategory.dao.findChildren(productCategory, count);
	}
	
	
	/**
	 * 查找上级商品分类(缓存)
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param count
	 *            数量
	 * @param cacheRegion
	 *            缓存区域
	 * @return 上级商品分类(缓存)
	 */
	@CacheName("productCategory")
	public List<ProductCategory> findParents(ProductCategory productCategory, Integer count, String cacheRegion) {
		return ProductCategory.dao.findParents(productCategory, count);
	}
	
	/**
	 * 查找上级商品分类
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param count
	 *            数量
	 * @return 上级商品分类
	 */
	public List<ProductCategory> findParents(ProductCategory productCategory, Integer count) {
		return ProductCategory.dao.findParents(productCategory, count);
	}
	
	/**
	 * 查找顶级商品分类(缓存)
	 * 
	 * @param count
	 *            数量
	 * @param cacheRegion
	 *            缓存区域
	 * @return 顶级商品分类(缓存)
	 */
	@CacheName("productCategory")
	public List<ProductCategory> findRoots(Integer count, String cacheRegion) {
		return ProductCategory.dao.findRoots(count);
	}
	
	/**
	 * 查找顶级商品分类
	 * 
	 * @param count
	 *            数量
	 * @return 顶级商品分类
	 */
	public List<ProductCategory> findRoots(Integer count) {
		return ProductCategory.dao.findRoots(count);
	}
	
	
	/**
	 * 保存
	 * 
	 */
	public boolean save(ProductCategory productCategory) {
		boolean result = false;
		result = super.save(setValue(productCategory));
		BrandService.service.save(productCategory);
		return result;
	}
	
	/**
	 * 更新
	 * 
	 */
	public boolean update(ProductCategory productCategory) {
		boolean result = false;
		result = super.update(productCategory);
		BrandService.service.update(productCategory);
		return result;
	}
	
	/**
	 * 设置值
	 * 
	 * @param productCategory
	 *            商品分类
	 */
	private ProductCategory setValue(ProductCategory productCategory) {
		ProductCategory parent = productCategory.getParent();
		if (parent != null) {
			productCategory.setTreePath(parent.getTreePath() + parent.getId() + ProductCategory.TREE_PATH_SEPARATOR);
		} else {
			productCategory.setTreePath(ProductCategory.TREE_PATH_SEPARATOR);
		}
		productCategory.setGrade(productCategory.getTreePaths().size());
		return productCategory;
	}
	
	/**
	 * 删除
	 * 
	 * @param ids
	 * @return
	 */
	@Before(Tx.class)
	public boolean delete(Long id) {
		boolean result = false;
		ProductCategoryBrand.dao.delete(id);
		result = ProductCategory.dao.deleteById(id);
		return result;
	}
}
