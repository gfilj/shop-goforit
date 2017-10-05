package com.jfinalshop.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinalshop.common.Filter;
import com.jfinalshop.common.Order;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Attribute;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Product.OrderType;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.ProductImage;
import com.jfinalshop.model.ProductMemberPrice;
import com.jfinalshop.model.ProductParameterValue;
import com.jfinalshop.model.ProductSpecification;
import com.jfinalshop.model.ProductSpecificationValue;
import com.jfinalshop.model.ProductTag;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.model.Tag;
import com.jfinalshop.security.ShiroUtil;
import com.jfinalshop.utils.AssertUtil;

/**
 * Service - 商品
 * 
 * 
 * 
 */
public class ProductService extends BaseService<Product> {
	
	public ProductService() {
		super(Product.class);
	}
	
	public static final ProductService service = new ProductService();
	
	/** 查看点击数时间 */
	private long viewHitsTime = System.currentTimeMillis();
	/** 缓存 */
	private CacheManager cacheManager = CacheKit.getCacheManager();
	
	private StaticService staticService = new StaticService();
	
	/**
	 * 判断商品编号是否存在
	 * 
	 * @param sn
	 *            商品编号(忽略大小写)
	 * @return 商品编号是否存在
	 */
	public boolean snExists(String sn) {
		return Product.dao.snExists(sn);
	}
	
	/**
	 * 根据商品编号查找商品
	 * 
	 * @param sn
	 *            商品编号(忽略大小写)
	 * @return 商品，若不存在则返回null
	 */
	public Product findBySn(String sn) {
		return Product.dao.findBySn(sn);
	}

	/**
	 * 判断商品编号是否唯一
	 * 
	 * @param previousSn
	 *            修改前商品编号(忽略大小写)
	 * @param currentSn
	 *            当前商品编号(忽略大小写)
	 * @return 商品编号是否唯一
	 */
	public boolean snUnique(String previousSn, String currentSn) {
		if (StringUtils.equalsIgnoreCase(previousSn, currentSn)) {
			return true;
		} else {
			if (Product.dao.snExists(currentSn)) {
				return false;
			} else {
				return true;
			}
		}
	}
	
	/**
	 * 通过ID、编号、全称查找商品
	 * 
	 * @param keyword
	 *            关键词
	 * @param isGift
	 *            是否为赠品
	 * @param count
	 *            数量
	 * @return 商品
	 */
	public List<Product> search(String keyword, Boolean isGift, Integer count) {
		return Product.dao.search(keyword, isGift, count);
	}
	
	/**
	 * 查找商品
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param brand
	 *            品牌
	 * @param promotion
	 *            促销
	 * @param tags
	 *            标签
	 * @param attributeValue
	 *            属性值
	 * @param startPrice
	 *            最低价格
	 * @param endPrice
	 *            最高价格
	 * @param isMarketable
	 *            是否上架
	 * @param isList
	 *            是否列出
	 * @param isTop
	 *            是否置顶
	 * @param isGift
	 *            是否为赠品
	 * @param isOutOfStock
	 *            是否缺货
	 * @param isStockAlert
	 *            是否库存警告
	 * @param orderType
	 *            排序类型
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 商品
	 */
	public List<Product> findList(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType, Integer count, List<Filter> filters, List<Order> orders) {
		return Product.dao.findList(productCategory, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, orderType, count, filters, orders);
	}
	
	/**
	 * 查找商品(缓存)
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param brand
	 *            品牌
	 * @param promotion
	 *            促销
	 * @param tags
	 *            标签
	 * @param attributeValue
	 *            属性值
	 * @param startPrice
	 *            最低价格
	 * @param endPrice
	 *            最高价格
	 * @param isMarketable
	 *            是否上架
	 * @param isList
	 *            是否列出
	 * @param isTop
	 *            是否置顶
	 * @param isGift
	 *            是否为赠品
	 * @param isOutOfStock
	 *            是否缺货
	 * @param isStockAlert
	 *            是否库存警告
	 * @param orderType
	 *            排序类型
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 商品(缓存)
	 */
	@CacheName("product")
	public List<Product> findList(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType, Integer count, List<Filter> filters, List<Order> orders,
			String cacheRegion) {
		return Product.dao.findList(productCategory, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, orderType, count, filters, orders);
	}
	
	/**
	 * 查找已上架商品
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @param first
	 *            起始记录
	 * @param count
	 *            数量
	 * @return 已上架商品
	 */
	public List<Product> findList(ProductCategory productCategory, Date beginDate, Date endDate, Integer first, Integer count) {
		return Product.dao.findList(productCategory, beginDate, endDate, first, count);
	}
	
	
	/**
	 * 查找商品销售信息
	 * 
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @param count
	 *            数量
	 * @return 商品销售信息
	 */
	public List<Record> findSalesList(Date beginDate, Date endDate, Integer count) {
		return Product.dao.findSalesList(beginDate, endDate, count);
	}
	
	
	/**
	 * 查找商品分页
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param brand
	 *            品牌
	 * @param promotion
	 *            促销
	 * @param tags
	 *            标签
	 * @param attributeValue
	 *            属性值
	 * @param startPrice
	 *            最低价格
	 * @param endPrice
	 *            最高价格
	 * @param isMarketable
	 *            是否上架
	 * @param isList
	 *            是否列出
	 * @param isTop
	 *            是否置顶
	 * @param isGift
	 *            是否为赠品
	 * @param isOutOfStock
	 *            是否缺货
	 * @param isStockAlert
	 *            是否库存警告
	 * @param orderType
	 *            排序类型
	 * @param pageable
	 *            分页信息
	 * @return 商品分页
	 */
	public Page<Product> findPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType, Pageable pageable) {
		return Product.dao.findPage(productCategory, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, orderType, pageable);
	}
	
	/**
	 * 查找收藏商品分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 收藏商品分页
	 */
	public Page<Product> findPage(Member member, Pageable pageable) {
		return Product.dao.findPage(member, pageable);
	}
	
	
	/**
	 * 查询商品数量
	 * 
	 * @param favoriteMember
	 *            收藏会员
	 * @param isMarketable
	 *            是否上架
	 * @param isList
	 *            是否列出
	 * @param isTop
	 *            是否置顶
	 * @param isGift
	 *            是否为赠品
	 * @param isOutOfStock
	 *            是否缺货
	 * @param isStockAlert
	 *            是否库存警告
	 * @return 商品数量
	 */
	public Long count(Member favoriteMember, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert) {
		return Product.dao.count(favoriteMember, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert);
	}
	
	
	/**
	 * 判断会员是否已购买该商品
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @return 是否已购买该商品
	 */
	public boolean isPurchased(Member member, Product product) {
		return Product.dao.isPurchased(member, product);
	}
	
	/**
	 * 查看并更新点击数
	 * 
	 * @param id
	 *            ID
	 * @return 点击数
	 */
	public long viewHits(Long id) {
		Ehcache cache = cacheManager.getEhcache(Product.HITS_CACHE_NAME);
		Element element = cache.get(id);
		Long hits;
		if (element != null) {
			hits = (Long) element.getObjectValue();
		} else {
			Product product = Product.dao.findById(id);
			if (product == null) {
				return 0L;
			}
			hits = product.getHits();
		}
		hits++;
		cache.put(new Element(id, hits));
		long time = System.currentTimeMillis();
		if (time > viewHitsTime + Product.HITS_CACHE_INTERVAL) {
			viewHitsTime = time;
			updateHits();
			cache.removeAll();
		}
		return hits;
	}
	
	/**
	 * 更新点击数
	 */
	@SuppressWarnings("unchecked")
	private void updateHits() {
		Ehcache cache = cacheManager.getEhcache(Product.HITS_CACHE_NAME);
		List<Long> ids = cache.getKeys();
		for (Long id : ids) {
			Product product = Product.dao.findById(id);
			if (product != null) {
				Element element = cache.get(id);
				long hits = (Long) element.getObjectValue();
				long increment = hits - product.getHits();
				Calendar nowCalendar = Calendar.getInstance();
				Calendar weekHitsCalendar = DateUtils.toCalendar(product.getWeekHitsDate());
				Calendar monthHitsCalendar = DateUtils.toCalendar(product.getMonthHitsDate());
				if (nowCalendar.get(Calendar.YEAR) != weekHitsCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.WEEK_OF_YEAR) > weekHitsCalendar.get(Calendar.WEEK_OF_YEAR)) {
					product.setWeekHits(increment);
				} else {
					product.setWeekHits(product.getWeekHits() + increment);
				}
				if (nowCalendar.get(Calendar.YEAR) != monthHitsCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.MONTH) > monthHitsCalendar.get(Calendar.MONTH)) {
					product.setMonthHits(increment);
				} else {
					product.setMonthHits(product.getMonthHits() + increment);
				}
				product.setHits(hits);
				product.setWeekHitsDate(new Date());
				product.setMonthHitsDate(new Date());
				product.update();
			}
		}
	}
	
	/**
	 * 保存
	 * 
	 * 
	 */
	public boolean save(Product product) {
		AssertUtil.notNull(product);

		boolean result = false;
		product.setCreateBy(ShiroUtil.getName());
		product.setCreationDate(new Date());
		product.setDeleteFlag(false);
		result = Product.dao.save(product);
		
		ProductTagService.service.save(product);
		ProductMemberPriceService.service.save(product);
		ProductParameterValueService.service.save(product);
		ProductSpecificationService.service.save(product);
		ProductSpecificationValueService.service.save(product);
		ProductImageService.service.save(product);
		
		staticService.build(product);
		return result;
	}
	
	/**
	 * 更新
	 * 
	 * 
	 */
	public boolean update(Product product) {
		AssertUtil.notNull(product);

		boolean result = false;
		product.setLastUpdatedBy(ShiroUtil.getName());
		product.setLastUpdatedDate(new Date());
		product.setDeleteFlag(false);
		result = Product.dao.update(product);
		
		ProductTagService.service.update(product);
		ProductMemberPriceService.service.update(product);
		ProductParameterValueService.service.update(product);
		ProductSpecificationService.service.update(product);
		ProductSpecificationValueService.service.update(product);
		ProductImageService.service.update(product);
		
		//staticService.build(product);
		return result;
	}
	

	/**
	 * 删除
	 * 
	 * 
	 */
	@Before(Tx.class)
	public boolean delete(Long[] products) {
		boolean resutl = false;
		for (Long product : products) {
			ProductTag.dao.delete(product);
			ProductMemberPrice.dao.delete(product);
			ProductSpecification.dao.delete(product);
			ProductSpecificationValue.dao.delete(product);
			ProductParameterValue.dao.delete(product);
			ProductImage.dao.delete(product);
			resutl = Product.dao.deleteById(product);
		}
		return resutl;
	}
	
	/**
	 * 删除
	 * 
	 * 
	 */
	public boolean delete(Product product) {
		if (product != null) {
			staticService.delete(product);
		}
		return super.delete(product);
	}
}
