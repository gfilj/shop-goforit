package com.jfinalshop.model;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.io.SAXReader;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinalshop.common.CommonAttributes;
import com.jfinalshop.common.Filter;
import com.jfinalshop.common.Order;
import com.jfinalshop.common.Order.Direction;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.common.Setting;
import com.jfinalshop.model.Order.OrderStatus;
import com.jfinalshop.model.Sn.Type;
import com.jfinalshop.model.base.BaseProduct;
import com.jfinalshop.utils.AssertUtil;
import com.jfinalshop.utils.ConditionUtil;
import com.jfinalshop.utils.DateUtil;
import com.jfinalshop.utils.FreemarkerUtils;
import com.jfinalshop.utils.SettingUtils;

import freemarker.template.TemplateException;

/**
 * Dao - 商品
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Product extends BaseProduct<Product> {
	public static final Product dao = new Product();
	
	private static final Pattern pattern = Pattern.compile("\\d*");
	
	/** 点击数缓存名称 */
	public static final String HITS_CACHE_NAME = "productHits";

	/** 点击数缓存更新间隔时间 */
	public static final int HITS_CACHE_INTERVAL = 600000;

	/** 商品属性值属性个数 */
	public static final int ATTRIBUTE_VALUE_PROPERTY_COUNT = 20;

	/** 商品属性值属性名称前缀 */
	public static final String ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX = "attributeValue";

	/** 全称规格前缀 */
	public static final String FULL_NAME_SPECIFICATION_PREFIX = "[";

	/** 全称规格后缀 */
	public static final String FULL_NAME_SPECIFICATION_SUFFIX = "]";

	/** 全称规格分隔符 */
	public static final String FULL_NAME_SPECIFICATION_SEPARATOR = " ";

	/** 静态路径 */
	private static String staticPath;

	/**
	 * 排序类型
	 */
	public enum OrderType {

		/** 置顶降序 */
		topDesc,

		/** 价格升序 */
		priceAsc,

		/** 价格降序 */
		priceDesc,

		/** 销量降序 */
		salesDesc,

		/** 评分降序 */
		scoreDesc,

		/** 日期降序 */
		dateDesc
	}
	
	/** 商品图片 */
	private List<ProductImage> productImages = new ArrayList<ProductImage>();

	/** 评论 */
	private List<Review> reviews = new ArrayList<Review>();

	/** 咨询 */
	private List<Consultation> consultations = new ArrayList<Consultation>();

	/** 标签 */
	private List<Tag> tags = new ArrayList<Tag>();

	/** 收藏会员 */
	private List<Member> favoriteMembers = new ArrayList<Member>();

	/** 规格 */
	private List<Specification> specifications = new ArrayList<Specification>();

	/** 规格值 */
	private List<SpecificationValue> specificationValues = new ArrayList<SpecificationValue>();

	/** 促销 */
	private List<Promotion> promotions = new ArrayList<Promotion>();

	/** 购物车项 */
	private List<CartItem> cartItems = new ArrayList<CartItem>();

	/** 订单项 */
	private List<OrderItem> orderItems = new ArrayList<OrderItem>();

	/** 赠品项 */
	private List<GiftItem> giftItems = new ArrayList<GiftItem>();

	/** 到货通知 */
	private List<ProductNotify> productNotifies = new ArrayList<ProductNotify>();

	/** 会员价 */
	private Map<MemberRank, BigDecimal> memberPrice = new HashMap<MemberRank, BigDecimal>();

	/** 参数值 */
	private Map<Parameter, String> parameterValue = new HashMap<Parameter, String>();
	
	static {
		try {
			File shopxxXmlFile = new File(PathKit.getRootClassPath() + CommonAttributes.SHOPXX_XML_PATH);
			org.dom4j.Document document = new SAXReader().read(shopxxXmlFile);
			org.dom4j.Element element = (org.dom4j.Element) document.selectSingleNode("/jfinalshopxx/template[@id='productContent']");
			staticPath = element.attributeValue("staticPath");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 判断商品编号是否存在
	 * 
	 * @param sn
	 *            商品编号(忽略大小写)
	 * @return 商品编号是否存在
	 */
	public boolean snExists(String sn) {
		if (sn == null) {
			return false;
		}
		String sql = "SELECT COUNT(*) FROM product WHERE LOWER(sn) = LOWER(?)";
		Long count = Db.queryLong(sql, sn);
		return count > 0;
	}
	
	/**
	 * 根据商品编号查找商品
	 * 
	 * @param sn
	 *            商品编号(忽略大小写)
	 * @return 商品，若不存在则返回null
	 */
	public Product findBySn(String sn) {
		if (sn == null) {
			return null;
		}
		String sql = "SELECT * FROM product WHERE LOWER(sn) = LOWER(?)";
		try {
			return findFirst(sql, sn);
		} catch (Exception e) {
			return null;
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
		if (StringUtils.isEmpty(keyword)) {
			return null;
		}
		String sql = "SELECT * FROM product WHERE 1 = 1 ";
		if (pattern.matcher(keyword).matches()) {
			sql += " AND (id = " + Long.valueOf(keyword) + " OR sn LIKE '%" + keyword + "%' OR full_name LIKE '%" + keyword + "%') ";
		} else {
			sql += " AND (sn LIKE '%" + keyword + "%' OR full_name LIKE '%" + keyword + "%') ";
		}
		if (isGift != null) {
			sql += " AND is_gift = " + isGift;
		}
		sql += " ORDER BY `is_top` DESC ";
		return find(sql);
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
		String sql = "SELECT p0.* FROM `product` p0 ";
		
		if (productCategory != null) {
			sql += " LEFT JOIN product_category pc ON pc.`id` = p0.`product_category_id` ";
			sql += " WHERE (p0.product_category_id = " + productCategory.getId() + " OR pc.tree_path LIKE '%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%')"; 
		} else {
			sql += " WHERE 1 = 1";
		}
		
		if (brand != null) {
			sql += " AND p0.brand_id = " + brand.getId();
		}
		if (promotion != null) {
			sql += "  AND (EXISTS "
					+ "         (SELECT p1.id "
					+ "          FROM product p1 "
					+ "          INNER JOIN promotion_product pp ON p1.id = pp.products "
					+ "          INNER JOIN promotion pom ON pp.promotions = pom.id "
					+ "          WHERE p1.id = p0.id  AND pom.id = " + promotion.getId() + ") "
					+ "       OR EXISTS "
					+ "         (SELECT p2.id "
					+ "          FROM product p2 "
					+ "          INNER JOIN product_category pc ON p2.product_category_id = pc.id "
					+ "          INNER JOIN promotion_product_category ppc ON pc.id = ppc.product_categories "
					+ "          INNER JOIN promotion pom2 ON ppc.promotions = pom2.id "
					+ "          WHERE p2.id = p0.id  AND pom2.id = " + promotion.getId() + ") "
					+ "       OR EXISTS "
					+ "         (SELECT p3.id "
					+ "          FROM product p3 "
					+ "          INNER JOIN brand b ON p3.brand_id = b.id "
					+ "          INNER JOIN promotion_brand pb ON b.id = pb.brands "
					+ "          INNER JOIN promotion pom3 ON pb.promotions = pom3.id "
					+ "          WHERE p3.id = p0.id  AND pom3.id = " + promotion.getId() + ")) ";
		}
		
		if (CollectionUtils.isNotEmpty(tags)) {
			sql += " AND EXISTS (SELECT * FROM `product_tag` pt WHERE pt.`products` = p0.`id` AND pt.`tags` IN (";
			StringBuffer instr = new StringBuffer();
			int maxSize = tags.size() - 1;
            for (int i = 0; i < tags.size(); i++) {
            	 instr.append(i == maxSize ? tags.get(maxSize).getId() + "))" : tags.get(i).getId() + ",");
            }
            sql += instr.toString();
		}
		
		if (attributeValue != null) {
			for (Entry<Attribute, String> entry : attributeValue.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				sql += " AND p0." + propertyName + " = '" + entry.getValue() + "'";
			}
		}
		
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		
		if (startPrice != null && startPrice.compareTo(new BigDecimal(0)) >= 0) {
			sql += " AND p0.price >= " + startPrice;
		}
		
		if (endPrice != null && endPrice.compareTo(new BigDecimal(0)) >= 0) {
			sql += " AND p0.price <= " + endPrice;
		}
		
		if (isMarketable != null) {
			sql += " AND p0.is_marketable = " + isMarketable;
		}
		if (isList != null) {
			sql += " AND p0.is_list = " + isList;
		}
		if (isTop != null) {
			sql += " AND p0.is_top = " + isTop;
		}
		if (isGift != null) {
			sql += " AND p0.is_gift = " + isGift;
		}
		
		if (isStockAlert != null) {
			Setting setting = SettingUtils.get();
			if (isStockAlert) {
				sql += " AND (p0.stock IS NOT NULL) AND p0.stock <= p0.allocated_stock + " +  setting.getStockAlertCount();
			} else {
				sql += " AND (p0.stock IS NULL OR p0.stock > p0.allocated_stock + " + setting.getStockAlertCount() + ")";
			}
		}
	        
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				sql += " AND (p0.stock IS NULL OR p0.stock > p0.allocated_stock) ";
			} else {
				sql += " AND (p0.stock IS NOT NULL ) AND p0.stock <= p0.allocated_stock ";
			}
		}
		if (orderType == OrderType.priceAsc) {
			orders.add(Order.asc("price"));
		} else if (orderType == OrderType.priceDesc) {
			orders.add(Order.desc("price"));
		} else if (orderType == OrderType.salesDesc) {
			orders.add(Order.desc("sales"));
		} else if (orderType == OrderType.scoreDesc) {
			orders.add(Order.desc("score"));
		} else if (orderType == OrderType.dateDesc) {
			orders.add(Order.desc("creationDate"));
		} else {
			orders.add(Order.desc("isTop"));
		}
		sql += ConditionUtil.buildSQL(null, count, filters, orders);
		return find(sql);
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
		String sql = "SELECT p0.* FROM `product` p0 ";
		if (productCategory != null) {
			sql += "LEFT JOIN product_category pc ON pc.`id` = p0.`product_category_id` WHERE p0.is_marketable = true ";
			sql += "AND ( p0.product_category = pc.id OR pc.tree_path LIKE '%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%' ) ";
		} else {
			sql += " WHERE p0.is_marketable = true  ";
		}
		if (beginDate != null) {
			sql += " AND p0.creation_date >= '" + DateUtil.getDateTime(beginDate) + "'";
		}
		if (endDate != null) {
			sql += " AND p0.creation_date <= '" + DateUtil.getDateTime(endDate) + "'";
		}
		List<Order> orders = new ArrayList<Order>();
		Order order = new Order("isTop", Direction.desc);
		orders.add(order);
		sql += ConditionUtil.buildSQL(first, count, null, orders);
		return find(sql);
	}
	
	/**
	 * 查找商品
	 * 
	 * @param goods
	 *            货品
	 * @param excludes
	 *            排除商品
	 * @return 商品
	 */
	public List<Product> findList(Goods goods, List<Product> excludes) {
		String sql = "SELECT * FROM `product` p0 WHERE 1 = 1 ";
		if (goods != null) {
			sql += " AND p0.goods_id = " + goods.getId();
		}
		if (excludes != null && !excludes.isEmpty()) {
			sql += " AND p0.`id` NOT IN (";
			StringBuffer instr = new StringBuffer();
			int maxSize = excludes.size() - 1;
            for (int i = 0; i < excludes.size(); i++) {
            	 instr.append(i == maxSize ? excludes.get(maxSize).getId() + ")" : excludes.get(i).getId() + ",");
            }
            sql += instr.toString();
		}
		return find(sql);
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
		String sql = ""
				+ "SELECT p.`id`, "
				+ "       p.`sn`, "
				+ "       p.`name`, "
				+ "       p.`full_name`, "
				+ "       i.`price`, "
				+ "       SUM(i.`quantity`) AS quantity, "
				+ "       SUM(i.`quantity` * i.`price`) AS total "
				+ "FROM `product` p "
				+ "INNER JOIN `order_item` i ON p.`id` = i.`product_id` "
				+ "INNER JOIN `order` o ON o.`id` = i.`order_id` "
				+ "WHERE order_status = 1 ";
		if (beginDate != null) {
			sql += " AND o.creation_date >= '" + DateUtil.getDateTime(beginDate) + "'"; 
		}
		if (endDate != null) {
			sql += " AND o.creation_date <= '" + DateUtil.getDateTime(endDate) + "'";
		}
			sql += ""
				+ "GROUP BY p.`id`, "
				+ "         p.`sn`, "
				+ "         p.`name`, "
				+ "         p.`full_name`, "
				+ "         i.`price` "
				+ "ORDER BY total DESC ";
		if (count != null && count >= 0) {
			sql += " LIMIT 0, " + count;
		}
		return Db.find(sql);
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
		String select = "SELECT p0.* ";
		String sqlExceptSelect = "FROM `product` p0 ";
		if (productCategory != null) {
			sqlExceptSelect += " LEFT JOIN product_category pc ON pc.`id` = p0.`product_category_id` ";
			sqlExceptSelect += " WHERE (p0.product_category_id = " + productCategory.getId() + " OR pc.tree_path LIKE '%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%')"; 
		} else {
			sqlExceptSelect += " WHERE 1 = 1 ";
		}
		if (brand != null) {
			sqlExceptSelect += " AND p0.brand_id = " + brand.getId();
		}
		if (promotion != null) {
			sqlExceptSelect += "  AND (EXISTS "
					+ "         (SELECT p1.id "
					+ "          FROM product p1 "
					+ "          INNER JOIN promotion_product pp ON p1.id = pp.products "
					+ "          INNER JOIN promotion pom ON pp.promotions = pom.id "
					+ "          WHERE p1.id = p0.id  AND pom.id = " + promotion.getId() + ") "
					+ "       OR EXISTS "
					+ "         (SELECT p2.id "
					+ "          FROM product p2 "
					+ "          INNER JOIN product_category pc ON p2.product_category_id = pc.id "
					+ "          INNER JOIN promotion_product_category ppc ON pc.id = ppc.product_categories "
					+ "          INNER JOIN promotion pom2 ON ppc.promotions = pom2.id "
					+ "          WHERE p2.id = p0.id  AND pom2.id = " + promotion.getId() + ") "
					+ "       OR EXISTS "
					+ "         (SELECT p3.id "
					+ "          FROM product p3 "
					+ "          INNER JOIN brand b ON p3.brand_id = b.id "
					+ "          INNER JOIN promotion_brand pb ON b.id = pb.brands "
					+ "          INNER JOIN promotion pom3 ON pb.promotions = pom3.id "
					+ "          WHERE p3.id = p0.id  AND pom3.id = " + promotion.getId() + ")) ";
		}
		if (tags != null && !tags.isEmpty()) {
			sqlExceptSelect += " AND EXISTS (SELECT * FROM `product_tag` pt WHERE pt.`products` = p0.`id` AND pt.`tags` IN (";
			StringBuffer instr = new StringBuffer();
			int maxSize = tags.size() - 1;
            for (int i = 0; i < tags.size(); i++) {
            	 instr.append(i == maxSize ? tags.get(maxSize).getId() + "))" : tags.get(i).getId() + ",");
            }
            sqlExceptSelect += instr.toString();
		}
		if (attributeValue != null) {
			for (Entry<Attribute, String> entry : attributeValue.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				sqlExceptSelect += " AND p0." + propertyName + " = '" + entry.getValue() + "'";
			}
		}
		
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		
		if (startPrice != null && startPrice.compareTo(new BigDecimal(0)) >= 0) {
			sqlExceptSelect += " AND p0.price >= " + startPrice;
		}
		
		if (endPrice != null && endPrice.compareTo(new BigDecimal(0)) >= 0) {
			sqlExceptSelect += " AND p0.price <= " + endPrice;
		}
		
		if (isMarketable != null) {
			sqlExceptSelect += " AND p0.is_marketable = " + isMarketable;
		}
		if (isList != null) {
			sqlExceptSelect += " AND p0.is_list = " + isList;
		}
		if (isTop != null) {
			sqlExceptSelect += " AND p0.is_top = " + isTop;
		}
		if (isGift != null) {
			sqlExceptSelect += " AND p0.is_gift = " + isGift;
		}
		if (isStockAlert != null) {
			Setting setting = SettingUtils.get();
			if (isStockAlert) {
				sqlExceptSelect += " AND (p0.stock IS NOT NULL) AND p0.stock <= p0.allocated_stock + " +  setting.getStockAlertCount();
			} else {
				sqlExceptSelect += " AND (p0.stock IS NULL OR p0.stock > p0.allocated_stock + " + setting.getStockAlertCount() + " )";
			}
		}
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				sqlExceptSelect += " AND (p0.stock IS NULL OR p0.stock > p0.allocated_stock) ";
			} else {
				sqlExceptSelect += " AND (p0.stock IS NOT NULL ) AND p0.stock <= p0.allocated_stock ";
			}
		}
		List<Order> orders = pageable.getOrders();
		if (orderType == OrderType.priceAsc) {
			orders.add(Order.asc("price"));
		} else if (orderType == OrderType.priceDesc) {
			orders.add(Order.desc("price"));
		} else if (orderType == OrderType.salesDesc) {
			orders.add(Order.desc("sales"));
		} else if (orderType == OrderType.scoreDesc) {
			orders.add(Order.desc("score"));
		} else if (orderType == OrderType.dateDesc) {
			orders.add(Order.desc("creationDate"));
		} else {
			orders.add(Order.desc("isTop"));
		}
		sqlExceptSelect += ConditionUtil.buildSQL(null, null, null, orders);
		Page<Product> pager = Product.dao.paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect);
		return pager;
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
		if (member == null) {
			return null;
		}
		String select = " SELECT p.* ";
		String sqlExceptSelect = ""
			+ " FROM member_favorite_product mfp "
			+ " INNER JOIN product p ON mfp.`favorite_products` = p.`id` "
			+ " WHERE mfp.`favorite_members` = ?";
		Page<Product> products = paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect, member.getId());
		return products;
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
		String sql = "SELECT COUNT(*) FROM product p WHERE 1 = 1 ";
				
		if (favoriteMember != null) {
			sql += " AND EXISTS (SELECT mfp.`favorite_products` FROM member_favorite_product mfp WHERE mfp.`favorite_products` = p.`id` AND mfp.`favorite_members` = " + favoriteMember.getId() + ")"; 
		}
		if (isMarketable != null) {
			sql += " AND p.`is_marketable` = " + isMarketable;
		}
		if (isList != null) {
			sql += " AND p.`is_list` = " + isList;
		}
		if (isTop != null) {
			sql += " AND p.`is_top` = " + isTop;
		}
		if (isGift != null) {
			sql += " AND p.`is_gift` = " + isGift;
		}
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				sql += " AND p.`stock` IS NOT NULL AND p.`stock` <= p.`allocated_stock` ";
			} else {
				sql += " AND (p.`stock` IS NULL OR p.`stock` > p.`allocated_stock`) ";
			}
		}
		if (isStockAlert != null) {
			Setting setting = SettingUtils.get();
			if (isStockAlert) {
				sql += " AND p.`stock` IS NOT NULL AND p.`stock` <= p.`allocated_stock` + " + setting.getStockAlertCount();
			} else {
				sql += " AND (p.`stock` IS NULL OR p.`stock` > p.`allocated_stock` + " + setting.getStockAlertCount() +")";
			}
		}
		return Db.queryLong(sql);
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
		if (member == null || product == null) {
			return false;
		}
		String sql = "SELECT COUNT(*) FROM `order_item` WHERE product_id = ? AND order_id IN (SELECT o.`id`  FROM `order` o  WHERE o.`member_id` = ?   AND o.`order_status` = ?)";
		Long count = Db.queryLong(sql, product.getId(), member.getId(), OrderStatus.completed.ordinal());
		return count > 0;
	}
	
	/**
	 * 获取商品图片
	 * 
	 * @return 商品图片
	 */
	public List<ProductImage> getProductImages() {
		String sql = "SELECT * FROM `product_image` WHERE `product_id`= ?";
		if (CollectionUtils.isEmpty(productImages)) {
			productImages = ProductImage.dao.find(sql, getId());
		}
		return productImages;
	}

	/**
	 * 设置商品图片
	 * 
	 * @param productImages
	 *            商品图片
	 */
	public void setProductImages(List<ProductImage> productImages) {
		this.productImages = productImages;
	}
	
	/**
	 * 获取评论
	 * 
	 * @return 评论
	 */
	public List<Review> getReviews() {
		String sql = "SELECT * FROM `review` WHERE `product_id`= ?";
		if (CollectionUtils.isEmpty(reviews)) {
			reviews = Review.dao.find(sql, getId());
		}
		return reviews;
	}
	
	/**
	 * 设置评论
	 * 
	 * @param reviews
	 *            评论
	 */
	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
	
	/**
	 * 获取咨询
	 * 
	 * @return 咨询
	 */
	public List<Consultation> getConsultations() {
		String sql = "SELECT * FROM `consultation` WHERE `product_id`= ?";
		if (CollectionUtils.isEmpty(consultations)) {
			consultations = Consultation.dao.find(sql, getId());
		}
		return consultations;
	}
	
	/**
	 * 设置咨询
	 * 
	 * @param consultations
	 *            咨询
	 */
	public void setConsultations(List<Consultation> consultations) {
		this.consultations = consultations;
	}
	
	/**
	 * 获取标签
	 * 
	 * @return 标签
	 */
	public List<Tag> getTags() {
		String sql = "SELECT t.* FROM product_tag pt INNER JOIN tag t ON pt.`tags` = t.`id` WHERE pt.products = ?";
		if (CollectionUtils.isEmpty(tags)) {
			tags = Tag.dao.find(sql, getId());
		}
		return tags;
	}
	
	/**
	 * 设置标签
	 * 
	 * @param tags
	 *            标签
	 */
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	
	
	/**
	 * 获取收藏会员
	 * 
	 * @return 收藏会员
	 */
	public List<Member> getFavoriteMembers() {
		String sql = "SELECT m.* FROM member_favorite_product mfp INNER JOIN member m ON mfp.`favorite_members` = m.`id` WHERE mfp.`favorite_products` = ?";
		if (CollectionUtils.isEmpty(favoriteMembers)) {
			favoriteMembers = Member.dao.find(sql, getId());
		}
		return favoriteMembers;
	}
	
	/**
	 * 设置收藏会员
	 * 
	 * @param favoriteMembers
	 *            收藏会员
	 */
	public void setFavoriteMembers(List<Member> favoriteMembers) {
		this.favoriteMembers = favoriteMembers;
	}
	
	/**
	 * 获取规格
	 * 
	 * @return 规格
	 */
	public List<Specification> getSpecifications() {
		String sql = "SELECT s.* FROM product_specification ps INNER JOIN specification s ON ps.`specifications` = s.id WHERE ps.`products` = ?";
		if (CollectionUtils.isEmpty(specifications)) {
			specifications = Specification.dao.find(sql, getId());
		}
		return specifications;
	}
	
	/**
	 * 设置规格
	 * 
	 * @param specifications
	 *            规格
	 */
	public void setSpecifications(List<Specification> specifications) {
		this.specifications = specifications;
	}
	
	/**
	 * 获取规格值
	 * 
	 * @return 规格值
	 */
	public List<SpecificationValue> getSpecificationValues() {
		String sql = "SELECT sv.* FROM product_specification_value psv INNER JOIN specification_value sv ON psv.`specification_values` = sv.`id` WHERE psv.`products` = ?";
		if (CollectionUtils.isEmpty(specificationValues)) {
			specificationValues = SpecificationValue.dao.find(sql, getId());
		}
		return specificationValues;
	}
	
	/**
	 * 设置规格值
	 * 
	 * @param specificationValues
	 *            规格值
	 */
	public void setSpecificationValues(List<SpecificationValue> specificationValues) {
		this.specificationValues = specificationValues;
	}
	
	/**
	 * 获取促销
	 * 
	 * @return 促销
	 */
	public List<Promotion> getPromotions() {
		String sql = "SELECT p.* FROM promotion_product pp INNER JOIN promotion p ON pp.`promotions` = p.`id` WHERE pp.`products` = ?";
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
	 * 获取购物车项
	 * 
	 * @return 购物车项
	 */
	public List<CartItem> getCartItems() {
		String sql ="SELECT * FROM `cart_item` WHERE `product_id`= ?";
		if (CollectionUtils.isEmpty(cartItems)) {
			cartItems = CartItem.dao.find(sql, getId());
		}
		return cartItems;
	}
	
	/**
	 * 设置购物车项
	 * 
	 * @param cartItems
	 *            购物车项
	 */
	public void setCartItems(List<CartItem> cartItems) {
		this.cartItems = cartItems;
	}
	
	/**
	 * 获取订单项
	 * 
	 * @return 订单项
	 */
	public List<OrderItem> getOrderItems() {
		String sql = "SELECT * FROM `order_item` WHERE `product_id`= ?";
		if (CollectionUtils.isEmpty(orderItems)) {
			orderItems = OrderItem.dao.find(sql, getId());
		}
		return orderItems;
	}
	
	/**
	 * 设置订单项
	 * 
	 * @param orderItems
	 *            订单项
	 */
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	
	/**
	 * 获取赠品项
	 * 
	 * @return 赠品项
	 */
	public List<GiftItem> getGiftItems() {
		String sql = "SELECT * FROM `gift_item` WHERE gift = ?";
		if (CollectionUtils.isEmpty(giftItems)) {
			giftItems = GiftItem.dao.find(sql, getId());
		}
		return giftItems;
	}
	
	/**
	 * 设置赠品项
	 * 
	 * @param giftItems
	 *            赠品项
	 */
	public void setGiftItems(List<GiftItem> giftItems) {
		this.giftItems = giftItems;
	}

	/**
	 * 获取到货通知
	 * 
	 * @return 到货通知
	 */
	public List<ProductNotify> getProductNotifies() {
		String sql = "SELECT * FROM `product_notify` WHERE `product_id`= ?";
		if (CollectionUtils.isEmpty(productNotifies)) {
			productNotifies = ProductNotify.dao.find(sql, getId());
		}
		return productNotifies;
	}

	/**
	 * 设置到货通知
	 * 
	 * @param productNotifies
	 *            到货通知
	 */
	public void setProductNotifies(List<ProductNotify> productNotifies) {
		this.productNotifies = productNotifies;
	}
	
	/**
	 * 获取会员价
	 * 
	 * @return 会员价
	 */
	public Map<MemberRank, BigDecimal> getMemberPrice() {
		if (memberPrice.isEmpty()) {
			String sql = "SELECT * FROM `product_member_price` WHERE `product` = ?";
			List<ProductMemberPrice> productMemberPrices = ProductMemberPrice.dao.find(sql, getId());
			if(productMemberPrices != null && 0 < productMemberPrices.size()) {
				for (ProductMemberPrice productMemberPrice : productMemberPrices) {
					MemberRank memberRank = MemberRank.dao.findById(productMemberPrice.getMemberPriceKey());
					memberPrice.put(memberRank, productMemberPrice.getMemberPrice());
				}
			}
		}
		return memberPrice;
	}
	
	/**
	 * 设置会员价
	 * 
	 * @param memberPrice
	 *            会员价
	 */
	public void setMemberPrice(Map<MemberRank, BigDecimal> memberPrice) {
		this.memberPrice = memberPrice;
	}
	
	/**
	 * 获取参数值
	 * 
	 * @return 参数值
	 */
	public Map<Parameter, String> getParameterValue() {
		if (parameterValue.isEmpty()) {
			 String sql = "SELECT * FROM `product_parameter_value` WHERE `product`= ?";
			 List<ProductParameterValue> productParameterValues = ProductParameterValue.dao.find(sql, getId());
			 if(productParameterValues != null && 0 < productParameterValues.size()) {
				 for (ProductParameterValue productParameterValue : productParameterValues) {
					 Parameter parameter = Parameter.dao.findById(productParameterValue.getParameterValueKey());
					 parameterValue.put(parameter, productParameterValue.getParameterValue());
				 }
			 }
		}
		return parameterValue;
	}
	
	/**
	 * 设置参数值
	 * 
	 * @param parameterValue
	 *            参数值
	 */
	public void setParameterValue(Map<Parameter, String> parameterValue) {
		this.parameterValue = parameterValue;
	}
	
	/**
	 * 获取商品属性值
	 * 
	 * @param attribute
	 *            商品属性
	 * @return 商品属性值
	 */
	public String getAttributeValue(Attribute attribute) {
		if (attribute != null && attribute.getPropertyIndex() != null) {
			try {
				String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + attribute.getPropertyIndex();
				return (String) PropertyUtils.getProperty(this, propertyName);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 设置商品属性值
	 * 
	 * @param attribute
	 *            商品属性
	 * @param value
	 *            商品属性值
	 */
	public void setAttributeValue(Attribute attribute, String value) {
		if (attribute != null && attribute.getPropertyIndex() != null) {
			if (StringUtils.isEmpty(value)) {
				value = null;
			}
			if (value == null || (attribute.getOptions() != null && attribute.getOptions().contains(value))) {
				try {
					String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + attribute.getPropertyIndex();
					PropertyUtils.setProperty(this, propertyName, value);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取货品
	 * 
	 * @return 货品
	 */
	public Goods getGoods() {
		return Goods.dao.findById(getGoodsId());
	}
	
	/**
	 * 获取品牌
	 * 
	 * @return 品牌
	 */
	public Brand getBrand() {
		return Brand.dao.findById(getBrandId());
	}
	
	/**
	 * 获取同货品商品
	 * 
	 * @return 同货品商品，不包含自身
	 */
	public List<Product> getSiblings() {
		List<Product> siblings = new ArrayList<Product>();
		if (getGoods() != null && getGoods().getProducts() != null) {
			for (Product product : getGoods().getProducts()) {
				if (!this.equals(product)) {
					siblings.add(product);
				}
			}
		}
		return siblings;
	}
	

	/**
	 * 获取有效促销
	 * 
	 * @return 有效促销
	 */
	public Set<Promotion> getValidPromotions() {
		Set<Promotion> allPromotions = new HashSet<Promotion>();
		if (getPromotions() != null) {
			allPromotions.addAll(getPromotions());
		}
		if (getProductCategory() != null && getProductCategory().getPromotions() != null) {
			allPromotions.addAll(getProductCategory().getPromotions());
		}
		if (getBrandId() != null && getBrand().getPromotions() != null) {
			allPromotions.addAll(getBrand().getPromotions());
		}
		Set<Promotion> validPromotions = new TreeSet<Promotion>();
		for (Promotion promotion : allPromotions) {
			if (promotion != null && promotion.hasBegun() && !promotion.hasEnded() && promotion.getMemberRanks() != null && !promotion.getMemberRanks().isEmpty()) {
				validPromotions.add(promotion);
			}
		}
		return validPromotions;
	}

	/**
	 * 获取可用库存
	 * 
	 * @return 可用库存
	 */
	public Integer getAvailableStock() {
		Integer availableStock = null;
		if (getStock() != null && getAllocatedStock() != null) {
			availableStock = getStock() - getAllocatedStock();
			if (availableStock < 0) {
				availableStock = 0;
			}
		}
		return availableStock;
	}

	/**
	 * 获取是否缺货
	 * 
	 * @return 是否缺货
	 */
	public Boolean getIsOutOfStock() {
		return getStock() != null && getAllocatedStock() != null && getAllocatedStock() >= getStock();
	}

	/**
	 * 判断促销是否有效
	 * 
	 * @param promotion
	 *            促销
	 * @return 促销是否有效
	 */
	public boolean isValid(Promotion promotion) {
		if (promotion == null || !promotion.hasBegun() || promotion.hasEnded() || promotion.getMemberRanks() == null || promotion.getMemberRanks().isEmpty()) {
			return false;
		}
		if (getValidPromotions().contains(promotion)) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * 获取访问路径
	 * 
	 * @return 访问路径
	 */
	public String getPath() {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("id", getId());
		model.put("createDate", getCreationDate());
		model.put("modifyDate", getLastUpdatedDate());
		model.put("sn", getSn());
		model.put("name", getName());
		model.put("fullName", getFullName());
		model.put("seoTitle", getSeoTitle());
		model.put("seoKeywords", getSeoKeywords());
		model.put("seoDescription", getSeoDescription());
		model.put("productCategory", getProductCategoryId());
		try {
			return FreemarkerUtils.process(staticPath, model);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	/**
	 * 获取缩略图
	 * 
	 * @return 缩略图
	 */
	public String getThumbnail() {
		if (getProductImages() != null && !getProductImages().isEmpty()) {
			return getProductImages().get(0).getThumbnail();
		}
		return null;
	}
	
	/**
	 * 获取商品分类
	 * 
	 * @return 商品分类
	 */
	public ProductCategory getProductCategory() {
		return ProductCategory.dao.findById(getProductCategoryId());
	}
	
	
	/**
	 * 保存
	 */
	public boolean save(Product product) {
		if (!product.getIsGift()) {
			String sql = "DELETE FROM gift_item WHERE `gift` = ?";
			Db.update(sql, getId());
		}
		if (!product.getIsMarketable() || product.getIsGift()) {
			String sql = "DELETE FROM cart_item  WHERE `product_id` = ?";
			Db.update(sql, getId());
		}
		if (product.getStock() == null) {
			product.setAllocatedStock(0);
		}
		product.setScore(0F);
		return setValue(product).save();
	}
	
	/**
	 * 设置值并更新
	 * 
	 * @param product
	 *            商品
	 * @return 商品
	 */
	public boolean update(Product product) {
		AssertUtil.notNull(product);
		if (product.getIsGift() != null && !product.getIsGift()) {
			String sql = "DELETE FROM gift_item WHERE gift = ?";
			Db.update(sql, product.getId());
		}
		if ((product.getIsMarketable() != null && !product.getIsMarketable()) || (product.getIsGift() != null && product.getIsGift())) {
			String sql = "DELETE FROM cart_item WHERE product_id = ?";
			Db.update(sql, product.getId());
		}
		if (product.getStock() == null) {
			product.setAllocatedStock(0);
		}
		if (product.getTotalScore() != null && product.getScoreCount() != null && product.getScoreCount() != 0) {
			product.setScore((float) product.getTotalScore() / product.getScoreCount());
		} else {
			product.setScore(0F);
		}
		setValue(product);
		return product.update();
	}
	
	
	/**
	 * 设置值
	 * 
	 * @param product
	 *            商品
	 */
	private Product setValue(Product product) {
		if (product == null) {
			return null;
		}
		if (StringUtils.isEmpty(product.getSn())) {
			String sn;
			do {
				sn = Sn.dao.generate(Type.product);
			} while (snExists(sn));
			product.setSn(sn);
		}
		StringBuffer fullName = new StringBuffer(product.getName());
		if (product.getSpecificationValues() != null && !product.getSpecificationValues().isEmpty()) {
			List<SpecificationValue> specificationValues = new ArrayList<SpecificationValue>(product.getSpecificationValues());
			/*Collections.sort(specificationValues, new Comparator<SpecificationValue>() {
				public int compare(SpecificationValue a1, SpecificationValue a2) {
					return new CompareToBuilder().append(a1.getSpecification(), a2.getSpecification()).toComparison();
				}
			});*/
			fullName.append(Product.FULL_NAME_SPECIFICATION_PREFIX);
			int i = 0;
			for (Iterator<SpecificationValue> iterator = specificationValues.iterator(); iterator.hasNext(); i++) {
				if (i != 0) {
					fullName.append(Product.FULL_NAME_SPECIFICATION_SEPARATOR);
				}
				fullName.append(iterator.next().getName());
			}
			fullName.append(Product.FULL_NAME_SPECIFICATION_SUFFIX);
		}
		product.setFullName(fullName.toString());
		return product;
	}
	
}
