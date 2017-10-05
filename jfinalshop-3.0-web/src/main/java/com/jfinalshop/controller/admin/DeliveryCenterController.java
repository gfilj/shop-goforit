package com.jfinalshop.controller.admin;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.DeliveryCenter;
import com.jfinalshop.service.DeliveryCenterService;
import com.jfinalshop.validator.DeliveryCenterValidator;

/**
 * Controller - 发货点
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/deliveryCenter")
public class DeliveryCenterController extends BaseAdminController {

	private DeliveryCenterService deliveryCenterService = enhance(DeliveryCenterService.class);
	private DeliveryCenter deliveryCenter;
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<DeliveryCenter> page = deliveryCenterService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/delivery_center/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		render("/admin/delivery_center/add.html");
	}
	
	/**
	 * 保存
	 */
	@Before(DeliveryCenterValidator.class)
	public void save() {
		String _isDefault = getPara("isDefault");
		Boolean isDefault = false;
		if (_isDefault != null) {
			isDefault = StringUtils.equals(_isDefault, "on") ? true : false;
		}
		Long areaId = getParaToLong("areaId");
		deliveryCenter = getModel(DeliveryCenter.class);
		deliveryCenter.setAreaId(areaId);
		deliveryCenter.setIsDefault(isDefault);
		deliveryCenterService.save(deliveryCenter);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("deliveryCenter", deliveryCenterService.find(id));
		render("/admin/delivery_center/edit.html");
	}
	
	/**
	 * 更新
	 */
	@Before(DeliveryCenterValidator.class)
	public void update() {
		String _isDefault = getPara("isDefault");
		Boolean isDefault = false;
		if (_isDefault != null) {
			isDefault = StringUtils.equals(_isDefault, "on") ? true : false;
		}
		deliveryCenter = getModel(DeliveryCenter.class);
		Long areaId = getParaToLong("areaId");
		deliveryCenter.setIsDefault(isDefault);
		deliveryCenter.setAreaId(areaId);
		deliveryCenterService.update(deliveryCenter);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			deliveryCenterService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
