package com.jfinalshop.controller.admin;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Brand.Type;
import com.jfinalshop.service.BrandService;

/**
 * Controller - 品牌
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/brand")
public class BrandController extends BaseAdminController {

	private Brand brand;
	private BrandService brandService = enhance(BrandService.class);
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<Brand> page = brandService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/brand/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		setAttr("types", Type.values());
		render("/admin/brand/add.html");
	}
	
	/**
	 * 保存
	 */
	public void save() {
		brand = getModel(Brand.class);
		if (StrKit.notBlank(getPara("type"))) {
			brand.setType(Type.valueOf(getPara("type")).ordinal());
		}
		if (brand.getType() == Type.text.ordinal()) {
			brand.setLogo(null);
		} else if (StringUtils.isEmpty(brand.getLogo())) {
			renderJson(ERROR_VIEW);
		}
		brandService.save(brand);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("types", Type.values());
		setAttr("brand", brandService.find(id));
		render("/admin/brand/edit.html");
	}
	
	/**
	 * 更新
	 */
	public void update() {
		brand = getModel(Brand.class);
		Type type = getPara("type") != null ? Type.valueOf(getPara("type")) : null;
		brand.setType(type.ordinal());
		if (brand.getType() == Type.text.ordinal()) {
			brand.setLogo(null);
		} else if (StringUtils.isEmpty(brand.getLogo())) {
			renderJson(ERROR_VIEW);
		}
		brandService.update(brand);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			brandService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
