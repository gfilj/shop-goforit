package com.jfinalshop.controller.admin;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Returns;
import com.jfinalshop.service.ReturnsService;

/**
 * Controller - 退货单
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/returns")
public class ReturnsController extends BaseAdminController {
	
	private ReturnsService returnsService = enhance(ReturnsService.class);

	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<Returns> page = returnsService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/returns/list.html");
	}
	
	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		setAttr("returns", returnsService.find(id));
		render("/admin/returns/view.html");
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			returnsService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
