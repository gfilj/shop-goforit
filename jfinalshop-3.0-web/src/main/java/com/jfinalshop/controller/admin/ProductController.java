package com.jfinalshop.controller.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.JsonKit;
import com.jfinal.upload.UploadFile;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.common.Setting;
import com.jfinalshop.model.Attribute;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.model.Parameter;
import com.jfinalshop.model.ParameterGroup;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Product.OrderType;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.ProductImage;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.model.Specification;
import com.jfinalshop.model.SpecificationValue;
import com.jfinalshop.model.Tag;
import com.jfinalshop.model.Tag.Type;
import com.jfinalshop.service.BrandService;
import com.jfinalshop.service.GoodsService;
import com.jfinalshop.service.MemberRankService;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.service.ProductImageService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.service.PromotionService;
import com.jfinalshop.service.SpecificationService;
import com.jfinalshop.service.SpecificationValueService;
import com.jfinalshop.service.TagService;
import com.jfinalshop.utils.SettingUtils;

/**
 * Controller - 商品
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/product")
public class ProductController extends BaseAdminController {
	
	private ProductService productService = enhance(ProductService.class);
	private ProductCategoryService productCategoryService = enhance(ProductCategoryService.class);
	private GoodsService goodsService = enhance(GoodsService.class);
	private BrandService brandService = enhance(BrandService.class);
	private PromotionService promotionService = enhance(PromotionService.class);
	private TagService tagService = enhance(TagService.class);
	private MemberRankService memberRankService = enhance(MemberRankService.class);
	private ProductImageService productImageService = enhance(ProductImageService.class);
	private SpecificationService specificationService = enhance(SpecificationService.class);
	private SpecificationValueService specificationValueService = enhance(SpecificationValueService.class);
	//private FileService fileService = enhance(FileService.class);
	private Product product;
	
	

	/**
	 * 检查编号是否唯一
	 */
	public void checkSn() {
		String previousSn = getPara("product.sn");
		String sn = getPara("sn");
		if (StringUtils.isEmpty(sn)) {
			renderJson(false);
		}
		if (productService.snUnique(previousSn, sn)) {
			renderJson(true);
		} else {
			renderJson(false);
		}
	}
	
	/**
	 * 获取参数组
	 */
	public void parameterGroups() {
		Long id = getParaToLong("id");
		ProductCategory productCategory = productCategoryService.find(id);
		List<ParameterGroup> parameterGroups = productCategory.getParameterGroups();
		JSONArray jsonArray = new JSONArray();
		if (CollectionUtils.isNotEmpty(parameterGroups)) {
			for (ParameterGroup parameterGroup : parameterGroups) { 
				JSONObject jsonObject = JSON.parseObject(JsonKit.toJson(parameterGroup)); 
				jsonObject.put("parameters", parameterGroup.getParameters());
				jsonArray.add(jsonObject);
			}
		}
		renderJson(jsonArray);
	}
	
	/**
	 * 获取属性
	 */
	public void attributes() {
		Long id = getParaToLong("id");
		ProductCategory productCategory = productCategoryService.find(id);
		List<Attribute> attributes = productCategory.getAttributes();
		JSONArray jsonArray = new JSONArray();
		if(CollectionUtils.isNotEmpty(attributes)) {
			for (Attribute attribute : attributes) { 
				JSONObject jsonObject = JSON.parseObject(JsonKit.toJson(attribute)); 
				jsonObject.put("options", attribute.getOptions());
				jsonArray.add(jsonObject);
			}
		}
		renderJson(jsonArray);
	}
	
	
	/**
	 * 添加
	 */
	public void add() {
		setAttr("productCategoryTree", productCategoryService.findTree());
		setAttr("brands", brandService.findAll());
		setAttr("tags", tagService.findList(Type.product));
		setAttr("memberRanks", memberRankService.findAll());
		setAttr("specifications", specificationService.findAll());
		render("/admin/product/add.html");
	}
	

	/**
	 * 保存
	 * 
	 */
	public void save() {
		List<UploadFile> uploadFiles = getFiles();
		product = getModel(Product.class);
		Long productCategoryId = getParaToLong("productCategoryId");
		Long brandId = getParaToLong("brandId");
		Long[] tagIds = getParaValuesToLong("tagIds");
		Long[] specificationIds = getParaValuesToLong("specificationIds");
		
		Boolean isMarketable = getParaToBoolean("isMarketable", false);
		Boolean isList = getParaToBoolean("isList", false);
		Boolean isTop = getParaToBoolean("isTop", false);
		Boolean isGift = getParaToBoolean("isGift", false);
		
		Integer productImageIndex = getModels(ProductImage.class).size();
		List<ProductImage> productImages = new ArrayList<ProductImage>();
		if (CollectionUtils.isNotEmpty(uploadFiles)) {
			for (int i = 0; i < productImageIndex; i++) {
				if (uploadFiles.get(i).getParameterName().equals("productImage[" + i +"].file")) {
					ProductImage productImage = getModel(ProductImage.class, "productImage[" + i +"]");
					productImage.setUploadFile(uploadFiles.get(i));
					productImages.add(productImage);
				}
			}
		}
		product.setProductImages(productImages);
		
		product.setProductCategoryId(productCategoryService.find(productCategoryId).getId());
		product.setTags(tagService.findList(tagIds));
		Brand brand = brandService.find(brandId);
		if (brand != null) {
			product.setBrandId(brand.getId());
		}
		if (StringUtils.isNotEmpty(product.getSn()) && productService.snExists(product.getSn())) {
			redirect(ERROR_VIEW);
			return;
		}
		if (product.getMarketPrice() == null) {
			BigDecimal defaultMarketPrice = calculateDefaultMarketPrice(product.getPrice());
			product.setMarketPrice(defaultMarketPrice);
		}
		if (product.getPoint() == null) {
			long point = calculateDefaultPoint(product.getPrice());
			product.setPoint(point);
		}
		product.setIsGift(isGift);
		product.setIsList(isList);
		product.setIsMarketable(isMarketable);
		product.setIsTop(isTop);
		product.setFullName(null);
		product.setAllocatedStock(0);
		product.setScore(0F);
		product.setTotalScore(0L);
		product.setScoreCount(0L);
		product.setHits(0L);
		product.setWeekHits(0L);
		product.setMonthHits(0L);
		product.setSales(0L);
		product.setWeekSales(0L);
		product.setMonthSales(0L);
		product.setWeekHitsDate(new Date());
		product.setMonthHitsDate(new Date());
		product.setWeekSalesDate(new Date());
		product.setMonthSalesDate(new Date());
		product.setReviews(null);
		product.setConsultations(null);
		product.setFavoriteMembers(null);
		product.setPromotions(null);
		product.setCartItems(null);
		product.setOrderItems(null);
		product.setGiftItems(null);
		product.setProductNotifies(null);

		for (MemberRank memberRank : memberRankService.findAll()) {
			String price = getPara("memberPrice_" + memberRank.getId());
			if (StringUtils.isNotEmpty(price) && new BigDecimal(price).compareTo(new BigDecimal(0)) >= 0) {
				product.getMemberPrice().put(memberRank, new BigDecimal(price));
			} else {
				product.getMemberPrice().remove(memberRank);
			}
		}

		for (ProductImage productImage : product.getProductImages()) {
			productImageService.build(productImage);
		}
		Collections.sort(product.getProductImages());
		if (product.getImage() == null && product.getThumbnail() != null) {
			product.setImage(product.getThumbnail());
		}

		for (ParameterGroup parameterGroup : product.getProductCategory().getParameterGroups()) {
			for (Parameter parameter : parameterGroup.getParameters()) {
				String parameterValue = getPara("parameter_" + parameter.getId());
				if (StringUtils.isNotEmpty(parameterValue)) {
					product.getParameterValue().put(parameter, parameterValue);
				} else {
					product.getParameterValue().remove(parameter);
				}
			}
		}

		for (Attribute attribute : product.getProductCategory().getAttributes()) {
			String attributeValue = getPara("attribute_" + attribute.getId());
			if (StringUtils.isNotEmpty(attributeValue)) {
				product.setAttributeValue(attribute, attributeValue);
			} else {
				product.setAttributeValue(attribute, null);
			}
		}

		Goods goods = new Goods();
		List<Product> products = new ArrayList<Product>();
		if (specificationIds != null && specificationIds.length > 0) {
			for (int i = 0; i < specificationIds.length; i++) {
				Specification specification = specificationService.find(specificationIds[i]);
				String[] specificationValueIds = getParaValues("specification_" + specification.getId());
				if (specificationValueIds != null && specificationValueIds.length > 0) {
					for (int j = 0; j < specificationValueIds.length; j++) {
						if (i == 0) {
							if (j == 0) {
								product.setGoodsId(goods.getId());
								product.setSpecifications(new ArrayList<Specification>());
								product.setSpecificationValues(new ArrayList<SpecificationValue>());
								products.add(product);
							} else {
								Product specificationProduct = new Product();
								//BeanUtils.copyProperties(product, specificationProduct);
								specificationProduct._setAttrs(product);
								specificationProduct.setId(null);
								specificationProduct.setCreationDate(null);
								specificationProduct.setLastUpdatedDate(null);
								specificationProduct.setSn(null);
								specificationProduct.setFullName(null);
								specificationProduct.setAllocatedStock(0);
								specificationProduct.setIsList(false);
								specificationProduct.setScore(0F);
								specificationProduct.setTotalScore(0L);
								specificationProduct.setScoreCount(0L);
								specificationProduct.setHits(0L);
								specificationProduct.setWeekHits(0L);
								specificationProduct.setMonthHits(0L);
								specificationProduct.setSales(0L);
								specificationProduct.setWeekSales(0L);
								specificationProduct.setMonthSales(0L);
								specificationProduct.setWeekHitsDate(new Date());
								specificationProduct.setMonthHitsDate(new Date());
								specificationProduct.setWeekSalesDate(new Date());
								specificationProduct.setMonthSalesDate(new Date());
								specificationProduct.setGoodsId(goods.getId());
								specificationProduct.setReviews(null);
								specificationProduct.setConsultations(null);
								specificationProduct.setFavoriteMembers(null);
								specificationProduct.setSpecifications(new ArrayList<Specification>());
								specificationProduct.setSpecificationValues(new ArrayList<SpecificationValue>());
								specificationProduct.setPromotions(null);
								specificationProduct.setCartItems(null);
								specificationProduct.setOrderItems(null);
								specificationProduct.setGiftItems(null);
								specificationProduct.setProductNotifies(null);
								products.add(specificationProduct);
							}
						}
						Product specificationProduct = products.get(j);
						SpecificationValue specificationValue = specificationValueService.find(Long.valueOf(specificationValueIds[j]));
						specificationProduct.getSpecifications().add(specification);
						specificationProduct.getSpecificationValues().add(specificationValue);
					}
				}
			}
		} else {
			product.setGoodsId(goods.getId());
			product.setSpecifications(null);
			product.setSpecificationValues(null);
			products.add(product);
		}
		//goods.getProducts().clear();
		//goods.getProducts().addAll(products);
		goodsService.save(goods);
		product.setGoodsId(goods.getId());
		productService.save(product);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/product/list");
	}

	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("productCategoryTree", productCategoryService.findTree());
		setAttr("brands", brandService.findAll());
		setAttr("tags", tagService.findList(Type.product));
		setAttr("memberRanks", memberRankService.findAll());
		setAttr("specifications", specificationService.findAll());
		setAttr("product", productService.find(id));
		render("/admin/product/edit.html");
	}
	
	
	
	/**
	 * 更新
	 * 
	 */
	public void update() {
		List<UploadFile> uploadFiles = getFiles();
		product = getModel(Product.class);
		Long productCategoryId = getParaToLong("productCategoryId");
		Long brandId = getParaToLong("brandId");
		Long[] tagIds = getParaValuesToLong("tagIds");
		Long[] specificationIds = getParaValuesToLong("specificationIds");
		Long[] specificationProductIds = getParaValuesToLong("specificationProductIds");
		
		Integer productImageIndex = getModels(ProductImage.class).size();
		List<ProductImage> productImages = new ArrayList<ProductImage>();
		if (0 < productImageIndex) {
			for (int i = 0; i < productImageIndex; i++) {
				ProductImage productImage = getModel(ProductImage.class, "productImage[" + i +"]");
				UploadFile uploadFile = getFile("productImage[" + i +"].file");
				if (uploadFile != null) {
					productImage.setUploadFile(uploadFile);
				} else {
					productImage.setUploadFile(null);
				}
				productImages.add(productImage);
			}
		}
		product.setProductImages(productImages);
		
		product.setProductCategoryId(productCategoryService.find(productCategoryId).getId());
		product.setTags(tagService.findList(tagIds));
		Brand brand = brandService.find(brandId);
		if (brand != null) {
			product.setBrandId(brand.getId());
		}
		Product pProduct = productService.find(product.getId());
		if (pProduct == null) {
			redirect(ERROR_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(product.getSn()) && !productService.snUnique(pProduct.getSn(), product.getSn())) {
			redirect(ERROR_VIEW);
			return;
		}
		if (product.getMarketPrice() == null) {
			BigDecimal defaultMarketPrice = calculateDefaultMarketPrice(product.getPrice());
			product.setMarketPrice(defaultMarketPrice);
		}
		if (product.getPoint() == null) {
			long point = calculateDefaultPoint(product.getPrice());
			product.setPoint(point);
		}

		for (MemberRank memberRank : memberRankService.findAll()) {
			String price = getPara("memberPrice_" + memberRank.getId());
			if (StringUtils.isNotEmpty(price) && new BigDecimal(price).compareTo(new BigDecimal(0)) >= 0) {
				product.getMemberPrice().put(memberRank, new BigDecimal(price));
			} else {
				product.getMemberPrice().remove(memberRank);
			}
		}

		for (ProductImage productImage : product.getProductImages()) {
			productImageService.build(productImage);
		}
		
		Collections.sort(product.getProductImages());
		if (product.getImage() == null && product.getThumbnail() != null) {
			product.setImage(product.getThumbnail());
		}

		for (ParameterGroup parameterGroup : product.getProductCategory().getParameterGroups()) {
			for (Parameter parameter : parameterGroup.getParameters()) {
				String parameterValue = getPara("parameter_" + parameter.getId());
				if (StringUtils.isNotEmpty(parameterValue)) {
					product.getParameterValue().put(parameter, parameterValue);
				} else {
					product.getParameterValue().remove(parameter);
				}
			}
		}

		for (Attribute attribute : product.getProductCategory().getAttributes()) {
			String attributeValue = getPara("attribute_" + attribute.getId());
			if (StringUtils.isNotEmpty(attributeValue)) {
				product.setAttributeValue(attribute, attributeValue);
			} else {
				product.setAttributeValue(attribute, null);
			}
		}

		Goods goods = pProduct.getGoods();
		List<Product> products = new ArrayList<Product>();
		if (specificationIds != null && specificationIds.length > 0) {
			for (int i = 0; i < specificationIds.length; i++) {
				Specification specification = specificationService.find(specificationIds[i]);
				String[] specificationValueIds = getParaValues("specification_" + specification.getId());
				if (specificationValueIds != null && specificationValueIds.length > 0) {
					for (int j = 0; j < specificationValueIds.length; j++) {
						if (i == 0) {
							if (j == 0) {
								pProduct._setAttrs(product);
								pProduct.remove("id");
								pProduct.remove("creation_date");
								pProduct.remove("last_updated_date");
								pProduct.remove("full_name");
								pProduct.remove("allocated_stock");
								pProduct.remove("score");
								pProduct.remove("total_score");
								pProduct.remove("score_count");
								pProduct.remove("hits");
								pProduct.remove("week_hits");
								pProduct.remove("month_hits");
								pProduct.remove("sales");
								pProduct.remove("week_sales");
								pProduct.remove("month_sales");
								pProduct.remove("week_hits_date");
								pProduct.remove("month_hits_date");
								pProduct.remove("week_sales_date");
								pProduct.remove("month_sales_date");
								pProduct.remove("goods_id");
								pProduct.remove("reviews");
								pProduct.remove("consultations");
								pProduct.remove("favoriteMembers");
								pProduct.remove("specifications");
								pProduct.remove("specificationValues");
								pProduct.remove("promotions");
								pProduct.remove("cartItems");
								pProduct.remove("orderItems");
								pProduct.remove("giftItems");
								pProduct.remove("productNotifies");
								pProduct.setSpecifications(new ArrayList<Specification>());
								pProduct.setSpecificationValues(new ArrayList<SpecificationValue>());
								products.add(pProduct);
							} else {
								if (specificationProductIds != null && j < specificationProductIds.length) {
									Product specificationProduct = productService.find(specificationProductIds[j]);
									if (specificationProduct == null || (specificationProduct.getGoods() != null && !specificationProduct.getGoods().equals(goods))) {
										redirect(ERROR_VIEW);
										return;
									}
									specificationProduct.setSpecifications(new ArrayList<Specification>());
									specificationProduct.setSpecificationValues(new ArrayList<SpecificationValue>());
									products.add(specificationProduct);
								} else {
									Product specificationProduct = new Product();
									//BeanUtils.copyProperties(product, specificationProduct);
									specificationProduct._setAttrs(product);
									specificationProduct.setId(null);
									specificationProduct.setCreationDate(null);
									specificationProduct.setLastUpdatedDate(null);
									specificationProduct.setSn(null);
									specificationProduct.setFullName(null);
									specificationProduct.setAllocatedStock(0);
									specificationProduct.setIsList(false);
									specificationProduct.setScore(0F);
									specificationProduct.setTotalScore(0L);
									specificationProduct.setScoreCount(0L);
									specificationProduct.setHits(0L);
									specificationProduct.setWeekHits(0L);
									specificationProduct.setMonthHits(0L);
									specificationProduct.setSales(0L);
									specificationProduct.setWeekSales(0L);
									specificationProduct.setMonthSales(0L);
									specificationProduct.setWeekHitsDate(new Date());
									specificationProduct.setMonthHitsDate(new Date());
									specificationProduct.setWeekSalesDate(new Date());
									specificationProduct.setMonthSalesDate(new Date());
									specificationProduct.setGoodsId(goods.getId());
									specificationProduct.setReviews(null);
									specificationProduct.setConsultations(null);
									specificationProduct.setFavoriteMembers(null);
									specificationProduct.setSpecifications(new ArrayList<Specification>());
									specificationProduct.setSpecificationValues(new ArrayList<SpecificationValue>());
									specificationProduct.setPromotions(null);
									specificationProduct.setCartItems(null);
									specificationProduct.setOrderItems(null);
									specificationProduct.setGiftItems(null);
									specificationProduct.setProductNotifies(null);
									products.add(specificationProduct);
								}
							}
						}
						Product specificationProduct = products.get(j);
						SpecificationValue specificationValue = specificationValueService.find(Long.valueOf(specificationValueIds[j]));
						specificationProduct.getSpecifications().add(specification);
						specificationProduct.getSpecificationValues().add(specificationValue);
					}
				}
			}
		} else {
			product.setSpecifications(null);
			product.setSpecificationValues(null);
			pProduct.remove("id");
			pProduct.remove("creation_date");
			pProduct.remove("last_updated_date");
			pProduct.remove("full_name");
			pProduct.remove("allocated_stock");
			pProduct.remove("score");
			pProduct.remove("total_score");
			pProduct.remove("score_count");
			pProduct.remove("hits");
			pProduct.remove("week_hits");
			pProduct.remove("month_hits");
			pProduct.remove("sales");
			pProduct.remove("week_sales");
			pProduct.remove("month_sales");
			pProduct.remove("week_hits_date");
			pProduct.remove("month_hits_date");
			pProduct.remove("week_sales_date");
			pProduct.remove("month_sales_date");
			pProduct.remove("goods_id");
			pProduct.remove("reviews");
			pProduct.remove("consultations");
			pProduct.remove("favoriteMembers");
			pProduct.remove("specifications");
			pProduct.remove("specificationValues");
			pProduct.remove("promotions");
			pProduct.remove("cartItems");
			pProduct.remove("orderItems");
			pProduct.remove("giftItems");
			pProduct.remove("productNotifies");
			pProduct._setAttrs(product);
			products.add(pProduct);
		}
		//goods.getProducts().clear();
		//goods.getProducts().addAll(products);
		//goodsService.update(goods);
		productService.update(product);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/product/list");
	}
	
	
	/**
	 * 列表
	 */
	public void list() {
		// 是否上架
		Boolean isMarketable = getParaToBoolean("isMarketable");
		setAttr("isMarketable", isMarketable);
		// 是否列出
		Boolean isList = getParaToBoolean("isList");
		setAttr("isList", isList);
		// 是否置顶
		Boolean isTop = getParaToBoolean("isTop");
		setAttr("isTop", isTop);
		// 是否为赠品
		Boolean isGift = getParaToBoolean("isGift");
		setAttr("isGift", isGift);
		// 是否缺货
		Boolean isOutOfStock = getParaToBoolean("isOutOfStock");
		setAttr("isOutOfStock", isOutOfStock);
		// 是否库存警告
		Boolean isStockAlert = getParaToBoolean("isStockAlert");
		setAttr("isStockAlert", isStockAlert);
		// 产品分类
		Long productCategoryId = getParaToLong("productCategoryId");
		setAttr("productCategoryId", productCategoryId);
		setAttr("productCategoryTree", productCategoryService.findTree());
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		// 品牌
		Long brandId = getParaToLong("brandId");
		Brand brand = brandService.find(brandId);
		setAttr("brandId", brandId);
		setAttr("brands", brandService.findAll());
		// 促销
		Long promotionId = getParaToLong("brandId");
		Promotion promotion = promotionService.find(promotionId);
		setAttr("promotions", promotionService.findAll());
		setAttr("promotionId", promotionId);
		// 标签
		Long tagId = getParaToLong("tagId");
		setAttr("tagId", tagId);
		List<Tag> tags = tagService.findList(tagId);
		setAttr("tags", tagService.findList(Type.product));
		
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", productService.findPage(productCategory, brand, promotion, tags, null, null, null, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, OrderType.dateDesc, pageable));
		render("/admin/product/list.html");
	}
	

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			productService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
	
	/**
	 * 计算默认市场价
	 * 
	 * @param price
	 *            价格
	 */
	private BigDecimal calculateDefaultMarketPrice(BigDecimal price) {
		Setting setting = SettingUtils.get();
		Double defaultMarketPriceScale = setting.getDefaultMarketPriceScale();
		return setting.setScale(price.multiply(new BigDecimal(defaultMarketPriceScale.toString())));
	}

	/**
	 * 计算默认积分
	 * 
	 * @param price
	 *            价格
	 */
	private long calculateDefaultPoint(BigDecimal price) {
		Setting setting = SettingUtils.get();
		Double defaultPointScale = setting.getDefaultPointScale();
		return price.multiply(new BigDecimal(defaultPointScale.toString())).longValue();
	}
}
