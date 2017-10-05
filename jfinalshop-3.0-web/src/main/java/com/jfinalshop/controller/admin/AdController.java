package com.jfinalshop.controller.admin;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Ad;
import com.jfinalshop.model.Ad.Type;
import com.jfinalshop.service.AdPositionService;
import com.jfinalshop.service.AdService;

/**
 * Controller - 广告
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/ad")
public class AdController extends BaseAdminController {

	AdService adService = enhance(AdService.class);
	AdPositionService adPositionService = enhance(AdPositionService.class);
	private Ad ad;
	
	/**
	 * 列表分页
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<Ad> page = adService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/ad/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		setAttr("types", Type.values());
		setAttr("adPositions", adPositionService.findAll());
		render("/admin/ad/add.html");
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("types", Type.values());
		setAttr("ad", adService.find(id));
		setAttr("adPositions", adPositionService.findAll());
		render("/admin/ad/edit.html");
	}
	
	/**
	 * 保存
	 */
	public void save() {
		ad = getModel(Ad.class);
		Type type = getPara("type") != null ? Type.valueOf(getPara("type")) : null;
		ad.setType(type.ordinal());
		if (ad.getBeginDate() != null && ad.getEndDate() != null && ad.getBeginDate().after(ad.getEndDate())) {
			redirect(ERROR_VIEW);
		}
		if (ad.getType().intValue() == Type.text.ordinal()) {
			ad.setPath(null);
		} else {
			ad.setContent(null);
		}
		adService.save(ad);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 更新
	 */
	public void update() {
		ad = getModel(Ad.class);
		Type type = getPara("type") != null ? Type.valueOf(getPara("type")) : null;
		ad.setType(type.ordinal());
		if (ad.getBeginDate() != null && ad.getEndDate() != null && ad.getBeginDate().after(ad.getEndDate())) {
			redirect(ERROR_VIEW);
		}
		if (ad.getType().intValue() == Type.text.ordinal()) {
			ad.setPath(null);
		} else {
			ad.setContent(null);
		}
		adService.update(ad);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			adService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
