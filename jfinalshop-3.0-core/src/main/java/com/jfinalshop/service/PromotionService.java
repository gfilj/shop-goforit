package com.jfinalshop.service;

import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinalshop.common.Filter;
import com.jfinalshop.common.Order;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.model.PromotionBrand;
import com.jfinalshop.model.PromotionCoupon;
import com.jfinalshop.model.PromotionMemberRank;
import com.jfinalshop.model.PromotionProduct;
import com.jfinalshop.model.PromotionProductCategory;
import com.jfinalshop.security.ShiroUtil;

/**
 * Service - 促销
 * 
 * 
 * 
 */
public class PromotionService extends BaseService<Promotion> {
	public static final PromotionService service = new PromotionService();
	
	public PromotionService() {
		super(Promotion.class);
	}
	
	private ProductService productService = Enhancer.enhance(ProductService.class);

	/**
	 * 查找促销(缓存)
	 * 
	 * @param hasBegun
	 *            是否已开始
	 * @param hasEnded
	 *            是否已结束
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param cacheRegion
	 *            缓存区域
	 * @return 促销(缓存)
	 */
	@CacheName("promotion")
	public List<Promotion> findList(Boolean hasBegun, Boolean hasEnded, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion) {
		return Promotion.dao.findList(hasBegun, hasEnded, count, filters, orders);
	}

	/**
	 * 查找促销
	 * 
	 * @param hasBegun
	 *            是否已开始
	 * @param hasEnded
	 *            是否已结束
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 促销
	 */
	public List<Promotion> findList(Boolean hasBegun, Boolean hasEnded, Integer count, List<Filter> filters, List<Order> orders) {
		return Promotion.dao.findList(hasBegun, hasEnded, count, filters, orders);
	}
	
	
	/**
	 * 保存
	 */
	@Before(Tx.class)
	public boolean save(Promotion promotion, Long[] memberRankIds, Long[] productCategoryIds, Long[] brandIds, Long[] couponIds, Long[] productIds) {
		boolean result = false;
		//promotion.setId(SequenceUtil.getId());
		promotion.setCreateBy(ShiroUtil.getName());
		promotion.setCreationDate(new Date());
		promotion.setDeleteFlag(false);
		result = promotion.save();
		Long promotions = promotion.getId();
		// 保存会员等级优惠
		if (memberRankIds != null && 0 < memberRankIds.length) {
			for(Long memberRanks : memberRankIds) {
				PromotionMemberRank promotionMemberRank = new PromotionMemberRank();
				promotionMemberRank.setMemberRanks(memberRanks);
				promotionMemberRank.setPromotions(promotions);
				promotionMemberRank.save();
			}
		}
		// 产品分类优惠
		if (productCategoryIds != null && 0 < productCategoryIds.length) {
			for(Long productCategories : productCategoryIds) {
				PromotionProductCategory promotionProductCategory = new PromotionProductCategory();
				promotionProductCategory.setProductCategories(productCategories);
				promotionProductCategory.setPromotions(promotions);
				promotionProductCategory.save();
			}
		}
		// 品牌优惠
		if (brandIds != null && 0 < brandIds.length) {
			for(Long brands : brandIds) {
				PromotionBrand promotionBrand = new PromotionBrand();
				promotionBrand.setBrands(brands);
				promotionBrand.setPromotions(promotions);
				promotionBrand.save();
			}
		}
		// 优惠券
		if (couponIds != null && 0 < couponIds.length) {
			for(Long coupons :couponIds) {
				PromotionCoupon promotionCoupon = new PromotionCoupon();
				promotionCoupon.setCoupons(coupons);
				promotionCoupon.setPromotions(promotions);
				promotionCoupon.save();
			}
		}
		
		// 产品优惠
		if (productIds != null && 0 < productIds.length) {
			for (Product product : productService.findList(productIds)) {
				if (!product.getIsGift()) {
					PromotionProduct promotionProduct = new PromotionProduct();
					promotionProduct.setProducts(product.getId());
					promotionProduct.setPromotions(promotions);
					promotionProduct.save();
				}
			}
		}
		return result;
	}
	
	/**
	 * 更新
	 */
	public boolean update(Promotion promotion, Long[] memberRankIds, Long[] productCategoryIds, Long[] brandIds, Long[] couponIds, Long[] productIds) {
		boolean result = false;
		promotion.setLastUpdatedBy(ShiroUtil.getName());
		promotion.setLastUpdatedDate(new Date());
		result = promotion.update();
		Long promotions = promotion.getId();
		
		// 保存会员等级优惠
		if (memberRankIds != null && 0 < memberRankIds.length) {
			PromotionMemberRank.dao.delete(promotions);
			for (Long memberRanks : memberRankIds) {
				PromotionMemberRank promotionMemberRank = new PromotionMemberRank();
				promotionMemberRank.setMemberRanks(memberRanks);
				promotionMemberRank.setPromotions(promotions);
				promotionMemberRank.save();
			}
		}
		
		// 产品分类优惠
		if (productCategoryIds != null && 0 < productCategoryIds.length) {
			PromotionProductCategory.dao.delete(promotions);
			for (Long productCategories : productCategoryIds) {
				PromotionProductCategory promotionProductCategory = new PromotionProductCategory();
				promotionProductCategory.setProductCategories(productCategories);
				promotionProductCategory.setPromotions(promotions);
				promotionProductCategory.save();
			}
		}
		
		// 品牌优惠
		if (brandIds != null && 0 < brandIds.length) {
			PromotionBrand.dao.delete(promotions);
			for (Long brands : brandIds) {
				PromotionBrand promotionBrand = new PromotionBrand();
				promotionBrand.setBrands(brands);
				promotionBrand.setPromotions(promotions);
				promotionBrand.save();
			}
		}
		
		// 优惠券
		if (couponIds != null && 0 < couponIds.length) {
			PromotionCoupon.dao.delete(promotions);
			for (Long coupons : couponIds) {
				PromotionCoupon promotionCoupon = new PromotionCoupon();
				promotionCoupon.setCoupons(coupons);
				promotionCoupon.setPromotions(promotions);
				promotionCoupon.save();
			}
		}

		// 产品优惠
		if (productIds != null && 0 < productIds.length) {
			PromotionProduct.dao.delete(promotions);
			for (Product product : productService.findList(productIds)) {
				if (!product.getIsGift()) {
					PromotionProduct promotionProduct = new PromotionProduct();
					promotionProduct.setProducts(product.getId());
					promotionProduct.setPromotions(promotions);
					promotionProduct.save();
				}
			}
		}
		return result;
	}
	
	/**
	 * 删除
	 * 
	 * @param ids
	 * @return
	 */
	@Before(Tx.class)
	public boolean delete(Long[] promotionsIds) {
		boolean result = false;
		for (Long promotions : promotionsIds) {
			PromotionMemberRank.dao.delete(promotions);
			PromotionProductCategory.dao.delete(promotions);
			PromotionBrand.dao.delete(promotions);
			PromotionCoupon.dao.delete(promotions);
			PromotionProduct.dao.delete(promotions);
			Promotion.dao.deleteById(promotions);
		}
		return result;
	}
}
