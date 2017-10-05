package com.jfinalshop.controller.admin;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.render.excel.PoiRender;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Coupon;
import com.jfinalshop.model.CouponCode;
import com.jfinalshop.security.ShiroUtil;
import com.jfinalshop.service.CouponCodeService;
import com.jfinalshop.service.CouponService;
import com.jfinalshop.utils.FreemarkerUtils;

/**
 * Controller - 优惠券
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/coupon")
public class CouponController extends BaseAdminController {

	private CouponService couponService = enhance(CouponService.class);
	private CouponCodeService couponCodeService = enhance(CouponCodeService.class);
	private Coupon coupon;
	
	/**
	 * 检查价格运算表达式是否正确
	 */
	public void checkPriceExpression() {
		String priceExpression = getPara("coupon.price_expression");
		if (StringUtils.isEmpty(priceExpression)) {
			renderJson(false);
		}
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("quantity", 111);
			model.put("price", new BigDecimal(9.99));
			new BigDecimal(FreemarkerUtils.process("#{(" + priceExpression + ");M50}", model));
			renderJson(true);
		} catch (Exception e) {
			renderJson(false);
		}
	}
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<Coupon> page = couponService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/coupon/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		render("/admin/coupon/add.html");
	}
	
	/**
	 * 保存
	 */
	public void save() {
		coupon = getModel(Coupon.class);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		Boolean isExchange = getParaToBoolean("isExchange", false);
		coupon.setIsEnabled(isEnabled);
		coupon.setIsExchange(isExchange);
		if (coupon.getBeginDate() != null && coupon.getEndDate() != null && coupon.getBeginDate().after(coupon.getEndDate())) {
			redirect(ERROR_VIEW);
			return;
		}
		if (coupon.getMinimumQuantity() != null && coupon.getMaximumQuantity() != null && coupon.getMinimumQuantity() > coupon.getMaximumQuantity()) {
			redirect(ERROR_VIEW);
			return;
		}
		if (coupon.getMinimumPrice() != null && coupon.getMaximumPrice() != null && coupon.getMinimumPrice().compareTo(coupon.getMaximumPrice()) > 0) {
			redirect(ERROR_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(coupon.getPriceExpression())) {
			try {
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("quantity", 111);
				model.put("price", new BigDecimal(9.99));
				new BigDecimal(FreemarkerUtils.process("#{(" + coupon.getPriceExpression() + ");M50}", model));
			} catch (Exception e) {
				redirect(ERROR_VIEW);
				return;
			}
		}
		if (coupon.getIsExchange() && coupon.getPoint() == null) {
			redirect(ERROR_VIEW);
			return;
		}
		if (!coupon.getIsExchange()) {
			coupon.setPoint(null);
		}
		couponService.save(coupon);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("coupon", couponService.find(id));
		render("/admin/coupon/edit.html");
	}
	
	
	/**
	 * 更新
	 */
	public void update() {
		coupon = getModel(Coupon.class);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		Boolean isExchange = getParaToBoolean("isExchange", false);
		coupon.setIsEnabled(isEnabled);
		coupon.setIsExchange(isExchange);
		if (coupon.getBeginDate() != null && coupon.getEndDate() != null && coupon.getBeginDate().after(coupon.getEndDate())) {
			redirect(ERROR_VIEW);
			return;
		}
		if (coupon.getMinimumQuantity() != null && coupon.getMaximumQuantity() != null && coupon.getMinimumQuantity() > coupon.getMaximumQuantity()) {
			redirect(ERROR_VIEW);
			return;
		}
		if (coupon.getMinimumPrice() != null && coupon.getMaximumPrice() != null && coupon.getMinimumPrice().compareTo(coupon.getMaximumPrice()) > 0) {
			redirect(ERROR_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(coupon.getPriceExpression())) {
			try {
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("quantity", 111);
				model.put("price", new BigDecimal(9.99));
				new BigDecimal(FreemarkerUtils.process("#{(" + coupon.getPriceExpression() + ");M50}", model));
			} catch (Exception e) {
				redirect(ERROR_VIEW);
				return;
			}
		}
		if (coupon.getIsExchange() && coupon.getPoint() == null) {
			redirect(ERROR_VIEW);
			return;
		}
		if (!coupon.getIsExchange()) {
			coupon.setPoint(null);
		}
		couponService.update(coupon);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			couponService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
	
	/**
	 * 生成优惠码
	 */
	public void build() {
		Long id = getParaToLong("id");
		Coupon coupon = couponService.find(id);
		setAttr("coupon", coupon);
		setAttr("totalCount", couponCodeService.count(coupon, null, null, null, null));
		setAttr("usedCount", couponCodeService.count(coupon, null, null, null, true));
		render("/admin/coupon/build.html");
	}
	
	/**
	 * 下载优惠码
	 */
	public void download() {
		Long id = getParaToLong("id");
		Integer count = getParaToInt("count");
		if (count == null || count <= 0) {
			count = 50;
		}
		Coupon coupon = couponService.find(id);
		List<CouponCode> data = couponCodeService.build(coupon, null, count);
		String filename = "coupon_code_" + new SimpleDateFormat("yyyyMM").format(new Date()) + ".xls";
		String[] contents = new String[4];
		contents[0] = message("admin.coupon.type") + ": " + coupon.getName();
		contents[1] = message("admin.coupon.count") + ": " + count;
		contents[2] = message("admin.coupon.operator") + ": " + ShiroUtil.getName();
		contents[3] = message("admin.coupon.date") + ": " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String[] columns = new String[]{"code"};
	    String[] headers = new String[]{message("admin.coupon.title")};
	    render(PoiRender.me(data).fileName(filename).sheetName(filename).headers(headers).columns(columns).cellWidth(9000));
	}
}
