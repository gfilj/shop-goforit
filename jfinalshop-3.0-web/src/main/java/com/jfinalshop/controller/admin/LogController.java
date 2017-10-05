package com.jfinalshop.controller.admin;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Log;
import com.jfinalshop.service.LogService;

/**
 * Controller - 管理日志
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/log")
public class LogController extends BaseAdminController {

	private LogService logService = enhance(LogService.class);
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<Log> page = logService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/log/list.html");
	}
	
	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		setAttr("log", logService.find(id));
		render("/admin/log/view.html");
	}

	/**
	 * 清空
	 */
	public void clear() {
		logService.clear();
		renderJson(SUCCESS_MESSAGE);
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			logService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
