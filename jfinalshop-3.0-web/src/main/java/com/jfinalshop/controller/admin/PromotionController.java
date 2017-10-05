package com.jfinalshop.controller.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.service.BrandService;
import com.jfinalshop.service.CouponService;
import com.jfinalshop.service.MemberRankService;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.service.PromotionService;
import com.jfinalshop.utils.FreemarkerUtils;

/**
 * Controller - 促销
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/promotion")
public class PromotionController extends BaseAdminController {

	private PromotionService promotionService = enhance(PromotionService.class);
	private MemberRankService memberRankService = enhance(MemberRankService.class);
	private ProductCategoryService productCategoryService = enhance(ProductCategoryService.class);
	private ProductService productService = enhance(ProductService.class);
	private BrandService brandService = enhance(BrandService.class);
	private CouponService couponService = enhance(CouponService.class);
	private Promotion promotion;
	
	/**
	 * 检查价格运算表达式是否正确
	 */
	public void checkPriceExpression() {
		String priceExpression = getPara("promotion.price_expression");
		if (StringUtils.isEmpty(priceExpression)) {
			renderJson(false);
			return;
		}
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("quantity", 111);
			model.put("price", new BigDecimal(9.99));
			new BigDecimal(FreemarkerUtils.process("#{(" + priceExpression + ");M50}", model));
			renderJson(true);
			return;
		} catch (Exception e) {
			renderJson(false);
			return;
		}
	}
	
	
	/**
	 * 检查积分运算表达式是否正确
	 */
	public void checkPointExpression() {
		String pointExpression = getPara("promotion.point_expression");
		if (StringUtils.isEmpty(pointExpression)) {
			renderJson(false);
			return;
		}
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("quantity", 111);
			model.put("point", 999L);
			Double.valueOf(FreemarkerUtils.process("#{(" + pointExpression + ");M50}", model)).longValue();
			renderJson(true);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(false);
			return;
		}
	}
	
	/**
	 * 商品选择
	 */
	public void  productSelect() {
		String q = getPara("q");
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		if (StringUtils.isNotEmpty(q)) {
			List<Product> products = productService.search(q, false, 20);
			for (Product product : products) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", product.getId());
				map.put("sn", product.getSn());
				map.put("fullName", product.getFullName());
				map.put("path", product.getPath());
				data.add(map);
			}
		}
		renderJson(data);
	}
	
	/**
	 * 赠品选择
	 */
	public void giftSelect(String q) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		if (StringUtils.isNotEmpty(q)) {
			List<Product> products = productService.search(q, true, 20);
			for (Product product : products) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", product.getId());
				map.put("sn", product.getSn());
				map.put("fullName", product.getFullName());
				map.put("path", product.getPath());
				data.add(map);
			}
		}
		renderJson(data);
	}
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<Promotion> page = promotionService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/promotion/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		setAttr("memberRanks", memberRankService.findAll());
		setAttr("productCategories", productCategoryService.findAll());
		setAttr("brands", brandService.findAll());
		setAttr("coupons", couponService.findAll());
		render("/admin/promotion/add.html");
	}
	
	
	/**
	 * 保存
	 */
	public void save() {
		promotion = getModel(Promotion.class);
		Long[] memberRankIds = getParaValuesToLong("memberRankIds");
		Long[] productCategoryIds = getParaValuesToLong("productCategoryIds");
		Long[] brandIds = getParaValuesToLong("brandIds");
		Long[] couponIds = getParaValuesToLong("couponIds");
		Long[] productIds = getParaValuesToLong("productIds");
		Boolean isFreeShipping = getParaToBoolean("isFreeShipping", false);
		Boolean isCouponAllowed = getParaToBoolean("isCouponAllowed", false);
		promotion.setIsFreeShipping(isFreeShipping);
		promotion.setIsCouponAllowed(isCouponAllowed);
		
		if (promotion.getBeginDate() != null && promotion.getEndDate() != null && promotion.getBeginDate().after(promotion.getEndDate())) {
			redirect(ERROR_VIEW);
			return;
		}
		if (promotion.getMinimumQuantity() != null && promotion.getMaximumQuantity() != null && promotion.getMinimumQuantity() > promotion.getMaximumQuantity()) {
			redirect(ERROR_VIEW);
			return;
		}
		if (promotion.getMinimumPrice() != null && promotion.getMaximumPrice() != null && promotion.getMinimumPrice().compareTo(promotion.getMaximumPrice()) > 0) {
			redirect(ERROR_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(promotion.getPriceExpression())) {
			try {
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("quantity", 111);
				model.put("price", new BigDecimal(9.99));
				new BigDecimal(FreemarkerUtils.process("#{(" + promotion.getPriceExpression() + ");M50}", model));
			} catch (Exception e) {
				redirect(ERROR_VIEW);
			}
		}
		if (StringUtils.isNotEmpty(promotion.getPointExpression())) {
			try {
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("quantity", 111);
				model.put("point", 999L);
				Double.valueOf(FreemarkerUtils.process("#{(" + promotion.getPointExpression() + ");M50}", model)).longValue();
			} catch (Exception e) {
				redirect(ERROR_VIEW);
				return;
			}
		}
		promotionService.save(promotion, memberRankIds, productCategoryIds, brandIds, couponIds, productIds);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("promotion", promotionService.find(id));
		setAttr("memberRanks", memberRankService.findAll());
		setAttr("productCategories", productCategoryService.findAll());
		setAttr("brands", brandService.findAll());
		setAttr("coupons", couponService.findAll());
		render("/admin/promotion/edit.html");
	}
	
	
	/**
	 * 更新
	 */
	public void update() {
		promotion = getModel(Promotion.class);
		Long[] memberRankIds = getParaValuesToLong("memberRankIds");
		Long[] productCategoryIds = getParaValuesToLong("productCategoryIds");
		Long[] brandIds = getParaValuesToLong("brandIds");
		Long[] couponIds = getParaValuesToLong("couponIds");
		Long[] productIds = getParaValuesToLong("productIds");
		Boolean isFreeShipping = getParaToBoolean("isFreeShipping", false);
		Boolean isCouponAllowed = getParaToBoolean("isCouponAllowed", false);
		promotion.setIsFreeShipping(isFreeShipping);
		promotion.setIsCouponAllowed(isCouponAllowed);
		
		if (promotion.getBeginDate() != null && promotion.getEndDate() != null && promotion.getBeginDate().after(promotion.getEndDate())) {
			redirect(ERROR_VIEW);
			return;
		}
		if (promotion.getMinimumQuantity() != null && promotion.getMaximumQuantity() != null && promotion.getMinimumQuantity() > promotion.getMaximumQuantity()) {
			redirect(ERROR_VIEW);
			return;
		}
		if (promotion.getMinimumPrice() != null && promotion.getMaximumPrice() != null && promotion.getMinimumPrice().compareTo(promotion.getMaximumPrice()) > 0) {
			redirect(ERROR_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(promotion.getPriceExpression())) {
			try {
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("quantity", 111);
				model.put("price", new BigDecimal(9.99));
				new BigDecimal(FreemarkerUtils.process("#{(" + promotion.getPriceExpression() + ");M50}", model));
			} catch (Exception e) {
				redirect(ERROR_VIEW);
				return;
			}
		}
		if (StringUtils.isNotEmpty(promotion.getPointExpression())) {
			try {
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("quantity", 111);
				model.put("point", 999L);
				Double.valueOf(FreemarkerUtils.process("#{(" + promotion.getPointExpression() + ");M50}", model)).longValue();
			} catch (Exception e) {
				redirect(ERROR_VIEW);
				return;
			}
		}
		promotionService.update(promotion, memberRankIds, productCategoryIds, brandIds, couponIds, productIds);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}

	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			promotionService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
