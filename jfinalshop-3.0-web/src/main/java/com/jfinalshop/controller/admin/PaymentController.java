package com.jfinalshop.controller.admin;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Payment;
import com.jfinalshop.service.PaymentService;

/**
 * Controller - 收款单
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/payment")
public class PaymentController extends BaseAdminController {
	
	private PaymentService paymentService = enhance(PaymentService.class);

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<Payment> page = paymentService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/payment/list.html");
	}
	
	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		setAttr("payment", paymentService.find(id));
		render("/admin/payment/view.html");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			for (Long id : ids) {
				Payment payment = paymentService.find(id);
				if (payment != null && payment.getExpire() != null && !payment.hasExpired()) {
					renderJson(Message.error("admin.payment.deleteUnexpiredNotAllowed"));
				}
			}
			paymentService.delete(ids);
		}
		renderJson(SUCCESS_MESSAGE);
	}

}
