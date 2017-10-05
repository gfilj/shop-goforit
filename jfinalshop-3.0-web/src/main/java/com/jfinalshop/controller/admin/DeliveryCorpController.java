package com.jfinalshop.controller.admin;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.DeliveryCorp;
import com.jfinalshop.service.DeliveryCorpService;
import com.jfinalshop.validator.DeliveryCorpValidator;

/**
 * Controller - 物流公司
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/deliveryCorp")
public class DeliveryCorpController extends BaseAdminController {
	
	private DeliveryCorpService deliveryCorpService = enhance(DeliveryCorpService.class);
	private DeliveryCorp deliveryCorp;
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<DeliveryCorp> page = deliveryCorpService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/delivery_corp/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		render("/admin/delivery_corp/add.html");
	}

	/**
	 * 保存
	 */
	@Before(DeliveryCorpValidator.class)
	public void save() {
		deliveryCorp = getModel(DeliveryCorp.class);
		deliveryCorpService.save(deliveryCorp);
		//addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("deliveryCorp", deliveryCorpService.find(id));
		render("/admin/delivery_corp/edit.html");
	}
	
	/**
	 * 更新
	 */
	
	@Before(DeliveryCorpValidator.class)
	public void update() {
		deliveryCorp = getModel(DeliveryCorp.class);
		deliveryCorpService.update(deliveryCorp);
		//addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			deliveryCorpService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
