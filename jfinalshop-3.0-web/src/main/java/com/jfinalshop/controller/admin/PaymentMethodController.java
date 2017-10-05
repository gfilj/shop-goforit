package com.jfinalshop.controller.admin;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.model.PaymentMethod.Method;
import com.jfinalshop.service.PaymentMethodService;
import com.jfinalshop.service.ShippingMethodService;

/**
 * Controller - 支付方式
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/paymentMethod")
public class PaymentMethodController extends BaseAdminController {

	private PaymentMethodService paymentMethodService = enhance(PaymentMethodService.class);
	private ShippingMethodService shippingMethodService = enhance(ShippingMethodService.class);
	private PaymentMethod paymentMethod;
	
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<PaymentMethod> page = paymentMethodService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/payment_method/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		setAttr("methods", Method.values());
		setAttr("shippingMethods", shippingMethodService.findAll());
		render("/admin/payment_method/add.html");
	}
	
	/**
	 * 保存
	 */
	public void save() {
		Long[] shippingMethodIds = getParaValuesToLong("shippingMethodIds");
		paymentMethod = getModel(PaymentMethod.class);
		Method method= getPara("method") != null ? Method.valueOf(getPara("method")) : null;
		paymentMethod.setMethod(method.ordinal());
		paymentMethod.setShippingMethods(shippingMethodService.findList(shippingMethodIds));
		paymentMethodService.save(paymentMethod);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("methods", Method.values());
		setAttr("shippingMethods", shippingMethodService.findAll());
		setAttr("paymentMethod", paymentMethodService.find(id));
		render("/admin/payment_method/edit.html");
	}
	
	/**
	 * 更新
	 */
	public void update() {
		Long[] shippingMethodIds = getParaValuesToLong("shippingMethodIds");
		paymentMethod = getModel(PaymentMethod.class);
		Method method= getPara("method") != null ? Method.valueOf(getPara("method")) : null;
		paymentMethod.setMethod(method.ordinal());
		paymentMethod.setShippingMethods(shippingMethodService.findList(shippingMethodIds));
		paymentMethodService.update(paymentMethod);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			paymentMethodService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
