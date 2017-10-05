package com.jfinalshop.controller.admin;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.ShippingMethod;
import com.jfinalshop.service.DeliveryCorpService;
import com.jfinalshop.service.ShippingMethodService;
import com.jfinalshop.validator.ShippingMethodValidator;

/**
 * Controller - 配送方式
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/shippingMethod")
public class ShippingMethodController extends BaseAdminController {

	private ShippingMethodService shippingMethodService = enhance(ShippingMethodService.class);
	private DeliveryCorpService deliveryCorpService = enhance(DeliveryCorpService.class);
	private ShippingMethod shippingMethod;
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<ShippingMethod> page = shippingMethodService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/shipping_method/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		setAttr("deliveryCorps", deliveryCorpService.findAll());
		render("/admin/shipping_method/add.html");
	}
	
	/**
	 * 保存
	 */
	@Before(ShippingMethodValidator.class)
	public void save() {
		shippingMethod = getModel(ShippingMethod.class);
		shippingMethodService.save(shippingMethod);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("deliveryCorps", deliveryCorpService.findAll());
		setAttr("shippingMethod", shippingMethodService.find(id));
		render("/admin/shipping_method/edit.html");
	}
	
	/**
	 * 更新
	 */
	@Before(ShippingMethodValidator.class)
	public void update() {
		shippingMethod = getModel(ShippingMethod.class);
		shippingMethodService.update(shippingMethod);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			shippingMethodService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
