package com.jfinalshop.controller.admin;

import java.util.List;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Specification;
import com.jfinalshop.model.Specification.Type;
import com.jfinalshop.model.SpecificationValue;
import com.jfinalshop.service.SpecificationService;

/**
 * Controller - 规格
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/specification")
public class SpecificationController extends BaseAdminController {

	private SpecificationService specificationService = enhance(SpecificationService.class);
	private Specification specification;
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", specificationService.findPage(pageable));
		render("/admin/specification/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		setAttr("types", Type.values());
		render("/admin/specification/add.html");
	}
	
	/**
	 * 保存
	 */
	public void save() {
		specification = getModel(Specification.class);
		String type = getPara("type", "");
		specification.setType(Type.valueOf(type).ordinal());
		List<SpecificationValue> specificationValues = getModels(SpecificationValue.class);
		specificationService.save(specification, specificationValues);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("types", Type.values());
		setAttr("specification", specificationService.find(id));
		render("/admin/specification/edit.html");
	}
	
	
	/**
	 * 更新
	 */
	public void update() {
		specification = getModel(Specification.class);
		String type = getPara("type", "");
		specification.setType(Type.valueOf(type).ordinal());
		List<SpecificationValue> specificationValues = getModels(SpecificationValue.class);
		specificationService.update(specification, specificationValues);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			for (Long id : ids) {
				Specification specification = specificationService.find(id);
				if (specification != null && specification.getProducts() != null && !specification.getProducts().isEmpty()) {
					renderJson(Message.error("admin.specification.deleteExistProductNotAllowed", specification.getName()));
				}
			}
			specificationService.delete(ids);
		}
		renderJson(SUCCESS_MESSAGE);
	}
}
