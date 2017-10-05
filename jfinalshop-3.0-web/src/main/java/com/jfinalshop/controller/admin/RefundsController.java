package com.jfinalshop.controller.admin;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Refunds;
import com.jfinalshop.service.RefundsService;

/**
 * Controller - 退款单
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/refunds")
public class RefundsController extends BaseAdminController {

	private RefundsService refundsService = enhance(RefundsService.class);
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<Refunds> page = refundsService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/refunds/list.html");
	}
	
	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		setAttr("refunds", refundsService.find(id));
		render("/admin/refunds/view.html");
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			refundsService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
