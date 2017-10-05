package com.jfinalshop.controller.admin;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.AdPosition;
import com.jfinalshop.service.AdPositionService;
import com.jfinalshop.validator.AdPositionValidator;

/**
 * 广告位
 *
 */
@ControllerBind(controllerKey = "/admin/adPosition")
public class AdPositionController extends BaseAdminController {
	
	AdPositionService adPositionService = enhance(AdPositionService.class);
	private AdPosition adPosition;
	
	/**
	 * 列表 
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<AdPosition> page = adPositionService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/ad_position/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		render("/admin/ad_position/add.html");
	}
	
	/**
	 * 保存
	 */
	@Before(AdPositionValidator.class)
	public void save() {
		adPosition = getModel(AdPosition.class);
		adPositionService.save(adPosition);
		//addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("adPosition", adPositionService.find(id));
		render("/admin/ad_position/edit.html");
	}
	
	/**
	 * 更新
	 */
	@Before(AdPositionValidator.class)
	public void update() {
		adPosition = getModel(AdPosition.class);
		adPositionService.update(adPosition);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			adPositionService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
