package com.jfinalshop.controller.admin;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Shipping;
import com.jfinalshop.service.ShippingService;

/**
 * Controller - 发货单
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/shipping")
public class ShippingController extends BaseAdminController {

	private ShippingService shippingService = enhance(ShippingService.class);
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<Shipping> page = shippingService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/shipping/list.html");
	}
	
	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		setAttr("shipping", shippingService.find(id));
		render("/admin/shipping/view.html");
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			shippingService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
