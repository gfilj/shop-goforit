package com.jfinalshop.controller.admin;


import java.util.Arrays;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Attribute;
import com.jfinalshop.model.Product;
import com.jfinalshop.service.AttributeService;
import com.jfinalshop.service.ProductCategoryService;

/**
 * Controller - 属性
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/attribute")
public class AttributeController extends BaseAdminController {

	private AttributeService attributeService = enhance(AttributeService.class);
	private ProductCategoryService productCategoryService = enhance(ProductCategoryService.class);
	private Attribute attribute;
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<Attribute> page = attributeService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/attribute/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		setAttr("productCategoryTree", productCategoryService.findTree());
		setAttr("attributeValuePropertyCount", Product.ATTRIBUTE_VALUE_PROPERTY_COUNT);
		render("/admin/attribute/add.html");
	}
	
	/**
	 * 保存
	 */
	public void save() {
		Long productCategoryId = getParaToLong("productCategoryId");
		String[] options = getParaValues("options");
		attribute = getModel(Attribute.class);
		attribute.setOptions(Arrays.asList(options));
		attribute.setProductCategoryId(productCategoryId);
		if (attribute.getProductCategory().getAttributes().size() >= Product.ATTRIBUTE_VALUE_PROPERTY_COUNT) {
			addFlashMessage(Message.error("admin.attribute.addCountNotAllowed", Product.ATTRIBUTE_VALUE_PROPERTY_COUNT));
			return;
		} else {
			attributeService.save(attribute);
			addFlashMessage(SUCCESS_MESSAGE);
		}
		list();
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("productCategoryTree", productCategoryService.findTree());
		setAttr("attributeValuePropertyCount", Product.ATTRIBUTE_VALUE_PROPERTY_COUNT);
		setAttr("attribute", attributeService.find(id));
		render("/admin/attribute/edit.html");
	}
	
	/**
	 * 更新
	 */
	public void update() {
		String[] options = getParaValues("options");
		attribute = getModel(Attribute.class);
		attribute.setOptions(Arrays.asList(options));
		attributeService.update(attribute);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			attributeService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(com.jfinalshop.common.Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
