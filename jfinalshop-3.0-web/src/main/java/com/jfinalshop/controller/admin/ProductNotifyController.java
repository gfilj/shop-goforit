package com.jfinalshop.controller.admin;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.service.ProductNotifyService;

/**
 * Controller - 到货通知
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/productNotify")
public class ProductNotifyController extends BaseAdminController {

	private ProductNotifyService productNotifyService = enhance(ProductNotifyService.class);
	
	/**
	 * 发送到货通知
	 */
	public void send() {
		Long[] ids = getParaValuesToLong("ids");
		int count = productNotifyService.send(ids);
		renderJson(Message.success("admin.productNotify.sentSuccess", count));
	}
	
	
	/**
	 * 列表
	 */
	public void list() {
		Boolean isMarketable = getParaToBoolean("isMarketable");
		Boolean isOutOfStock = getParaToBoolean("isOutOfStock");
		Boolean hasSent = getParaToBoolean("hasSent");
		Pageable pageable = getBean(Pageable.class);
		setAttr("isMarketable", isMarketable);
		setAttr("isOutOfStock", isOutOfStock);
		setAttr("hasSent", hasSent);
		setAttr("pageable", pageable);
		setAttr("page", productNotifyService.findPage(pageable));
		render("/admin/product_notify/list.html");
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			productNotifyService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
