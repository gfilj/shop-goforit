package com.jfinalshop.controller.admin;

import java.util.List;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Parameter;
import com.jfinalshop.model.ParameterGroup;
import com.jfinalshop.service.ParameterGroupService;
import com.jfinalshop.service.ProductCategoryService;

/**
 * Controller - 参数
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/parameterGroup")
public class ParameterGroupController extends BaseAdminController {
	
	private ParameterGroupService parameterGroupService = enhance(ParameterGroupService.class);
	private ProductCategoryService productCategoryService = enhance(ProductCategoryService.class);
	private ParameterGroup parameterGroup;
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<ParameterGroup> page = parameterGroupService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/parameter_group/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		setAttr("productCategoryTree", productCategoryService.findTree());
		render("/admin/parameter_group/add.html");
	}
	
	/**
	 * 保存
	 */
	public void save() {
		Long productCategoryId = getParaToLong("productCategoryId");
		parameterGroup = getModel(ParameterGroup.class);
		List<Parameter> parameters = getModels(Parameter.class);
		parameterGroup.setParameters(parameters);
		parameterGroup.setProductCategoryId(productCategoryService.find(productCategoryId).getId());
		parameterGroupService.save(parameterGroup);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("parameterGroup", parameterGroupService.find(id));
		setAttr("productCategoryTree", productCategoryService.findTree());
		render("/admin/parameter_group/edit.html");
	}
	
	/**
	 * 更新
	 */
	public void update() {
		Long productCategoryId = getParaToLong("productCategoryId");
		parameterGroup = getModel(ParameterGroup.class);
		List<Parameter> parameters = getModels(Parameter.class);
		parameterGroup.setParameters(parameters);
		parameterGroup.setProductCategoryId(productCategoryService.find(productCategoryId).getId());
		parameterGroupService.update(parameterGroup);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			parameterGroupService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(com.jfinalshop.common.Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
