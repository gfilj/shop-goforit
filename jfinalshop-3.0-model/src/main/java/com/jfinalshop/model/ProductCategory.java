package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BaseProductCategory;

/**
 * Dao - 商品分类
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class ProductCategory extends BaseProductCategory<ProductCategory> {
	public static final ProductCategory dao = new ProductCategory();
	
	/** 树路径分隔符 */
	public static final String TREE_PATH_SEPARATOR = ",";

	/** 访问路径前缀 */
	private static final String PATH_PREFIX = "/product/list";
	
	/** 下级分类 */
	private List<ProductCategory> children = new ArrayList<ProductCategory>();

	/** 商品 */
	private List<Product> products = new ArrayList<Product>();

	/** 筛选品牌 */
	private List<Brand> brands = new ArrayList<Brand>();

	/** 参数组 */
	private List<ParameterGroup> parameterGroups = new ArrayList<ParameterGroup>();

	/** 筛选属性 */
	private List<Attribute> attributes = new ArrayList<Attribute>();

	/** 促销 */
	private List<Promotion> promotions = new ArrayList<Promotion>();
	
	/**
	 * 查找顶级商品分类
	 * 
	 * @param count
	 *            数量
	 * @return 顶级商品分类
	 */
	public List<ProductCategory> findRoots(Integer count) {
		String sql = "SELECT * FROM product_category WHERE parent_id IS NULL ORDER BY orders ASC ";
		if (count != null) {
			sql += " LIMIT 0, " + count;
		}
		return find(sql);
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
		if (productCategory == null || productCategory.getParent() == null) {
			return Collections.<ProductCategory> emptyList();
		}
		String sql = "SELECT * FROM product_category productCategory WHERE productCategory.id IN (?) ORDER BY productCategory.grade ASC ";
		if (count != null) {
			sql += " LIMIT 0, " + count;
		}
		return find(sql, productCategory.getId());
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
		String sql = null;
		if (productCategory != null) {
			sql = "SELECT * FROM product_category WHERE tree_path LIKE '%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%' ORDER BY orders ASC ";
		} else {
			sql = "SELECT * FROM product_category ORDER BY orders ASC ";
		}
		if (count != null) {
			sql += " LIMIT 0," + count;
		}
		return find(sql);
	}
	
	/**
	 * 获取上级分类
	 * 
	 * @return 上级分类
	 */
	public ProductCategory getParent() {
		return ProductCategory.dao.findById(getParentId());
	}
	
	
	/**
	 * 获取下级分类
	 * 
	 * @return 下级分类
	 */
	public List<ProductCategory> getChildren() {
		String sql = "SELECT * FROM product_category WHERE `parent_id` = ?";
		if (CollectionUtils.isEmpty(children)) {
			children = ProductCategory.dao.find(sql, getId());
		}
		return children;
	}
	
	/**
	 * 设置下级分类
	 * 
	 * @param children
	 *            下级分类
	 */
	public void setChildren(List<ProductCategory> children) {
		this.children = children;
	}
	
	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public List<Product> getProducts() {
		String sql = "SELECT * FROM product WHERE `product_category_id` = ?";
		if (CollectionUtils.isEmpty(products)) {
			products = Product.dao.find(sql, getId());
		}
		return products;
	}
	
	/**
	 * 设置商品
	 * 
	 * @param products
	 *            商品
	 */
	public void setProducts(List<Product> products) {
		this.products = products;
	}
	
	/**
	 * 获取筛选品牌
	 * 
	 * @return 筛选品牌
	 */
	public List<Brand> getBrands() {
		String sql = "SELECT b.* FROM product_category_brand pcb INNER JOIN brand b ON pcb.`brands`= b.`id` WHERE pcb.`product_categories` = ?";
		if (CollectionUtils.isEmpty(brands)) {
			brands = Brand.dao.find(sql, getId());
		}
		return brands;
	}
	

	/**
	 * 设置筛选品牌
	 * 
	 * @param brands
	 *            筛选品牌
	 */
	public void setBrands(List<Brand> brands) {
		this.brands = brands;
	}

	
	/**
	 * 获取参数组
	 * 
	 * @return 参数组
	 */
	public List<ParameterGroup> getParameterGroups() {
		String sql = "SELECT * FROM parameter_group  WHERE `product_category_id` = ?";
		if (CollectionUtils.isEmpty(parameterGroups)) {
			parameterGroups = ParameterGroup.dao.find(sql, getId());
		}
		return parameterGroups;
	}
	
	/**
	 * 设置参数组
	 * 
	 * @param parameterGroups
	 *            参数组
	 */
	public void setParameterGroups(List<ParameterGroup> parameterGroups) {
		this.parameterGroups = parameterGroups;
	}
	
	/**
	 * 获取筛选属性
	 * 
	 * @return 筛选属性
	 */
	public List<Attribute> getAttributes() {
		String sql = "SELECT * FROM attribute WHERE `product_category_id` = ?";
		if (CollectionUtils.isEmpty(attributes)) {
			attributes = Attribute.dao.find(sql, getId());
		}
		return attributes;
	}

	/**
	 * 设置筛选属性
	 * 
	 * @param attributes
	 *            筛选属性
	 */
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	/**
	 * 获取促销
	 * 
	 * @return 促销
	 */
	public List<Promotion> getPromotions() {
		String sql = "SELECT p.* FROM promotion_product_category ppc INNER JOIN promotion p ON ppc.`promotions` = p.`id` WHERE ppc.`product_categories` = ?";
		if (CollectionUtils.isEmpty(promotions)) {
			promotions = Promotion.dao.find(sql, getId());
		}
		return promotions;
	}
	
	/**
	 * 设置促销
	 * 
	 * @param promotions
	 *            促销
	 */
	public void setPromotions(List<Promotion> promotions) {
		this.promotions = promotions;
	}

	/**
	 * 获取树路径
	 * 
	 * @return 树路径
	 */
	public List<Long> getTreePaths() {
		List<Long> treePaths = new ArrayList<Long>();
		String[] ids = StringUtils.split(getTreePath(), TREE_PATH_SEPARATOR);
		if (ids != null) {
			for (String id : ids) {
				treePaths.add(Long.valueOf(id));
			}
		}
		return treePaths;
	}

	/**
	 * 获取访问路径
	 * 
	 * @return 访问路径
	 */
	public String getPath() {
		if (getId() != null) {
			return PATH_PREFIX + "/" + getId();
		}
		return null;
	}
	
	/**
	 * 删除前处理
	 */
	public void preRemove() {
		List<Promotion> promotions = getPromotions();
		if (promotions != null) {
			for (Promotion promotion : promotions) {
				List<ProductCategory> productCategories = promotion.getProductCategories();
				for (ProductCategory productCategory : productCategories) {
					productCategory.delete();
				}
			}
		}
	}
}
