package com.jfinalshop.controller.admin;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.DeliveryTemplate;
import com.jfinalshop.service.DeliveryTemplateService;
import com.jfinalshop.validator.DeliveryTemplateValidator;

/**
 * Controller - 快递单模板
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/deliveryTemplate")
public class DeliveryTemplateController extends BaseAdminController {

	private DeliveryTemplateService deliveryTemplateService = enhance(DeliveryTemplateService.class);
	private DeliveryTemplate deliveryTemplate;
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<DeliveryTemplate> page = deliveryTemplateService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/delivery_template/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		render("/admin/delivery_template/add.html");
	}
	
	
	/**
	 * 保存
	 */
	@Before(DeliveryTemplateValidator.class)
	public void save() {
		String _isDefault = getPara("isDefault");
		Boolean isDefault = false;
		if (_isDefault != null) {
			isDefault = StringUtils.equals(_isDefault, "on") ? true : false;
		}
		deliveryTemplate = getModel(DeliveryTemplate.class);
		deliveryTemplate.setIsDefault(isDefault);
		deliveryTemplateService.save(deliveryTemplate);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("deliveryTemplate", deliveryTemplateService.find(id));
		render("/admin/delivery_template/edit.html");
	}
	
	/**
	 * 更新
	 */

	@Before(DeliveryTemplateValidator.class)
	public void update() {
		String _isDefault = getPara("isDefault");
		Boolean isDefault = false;
		if (_isDefault != null) {
			isDefault = StringUtils.equals(_isDefault, "on") ? true : false;
		}
		deliveryTemplate = getModel(DeliveryTemplate.class);
		deliveryTemplate.setIsDefault(isDefault);
		deliveryTemplateService.update(deliveryTemplate);
		//addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			deliveryTemplateService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
