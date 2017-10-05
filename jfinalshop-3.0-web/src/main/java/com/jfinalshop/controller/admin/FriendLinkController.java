package com.jfinalshop.controller.admin;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.FriendLink;
import com.jfinalshop.model.FriendLink.Type;
import com.jfinalshop.service.FriendLinkService;
import com.jfinalshop.validator.FriendLinkValidator;

/**
 * Controller - 友情链接
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/friendLink")
public class FriendLinkController extends BaseAdminController {

	private FriendLinkService friendLinkService = enhance(FriendLinkService.class);
	private FriendLink friendLink;
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<FriendLink> page = friendLinkService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/friend_link/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		setAttr("types", Type.values());
		render("/admin/friend_link/add.html");
	}
	
	/**
	 * 保存
	 */
	@Before(FriendLinkValidator.class)
	public void save() {
		friendLink = getModel(FriendLink.class);
		String type = getPara("type","");
		friendLink.setType(Type.valueOf(type).ordinal());
		if (friendLink.getType() == Type.text.ordinal()) {
			friendLink.setLogo(null);
		} else if (StringUtils.isEmpty(friendLink.getLogo())) {
			renderJson(ERROR_VIEW);
		}
		friendLinkService.save(friendLink);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 更新
	 */
	@Before(FriendLinkValidator.class)
	public void update() {
		friendLink = getModel(FriendLink.class);
		String type = getPara("type","");
		friendLink.setType(Type.valueOf(type).ordinal());
		if (friendLink.getType() == Type.text.ordinal()) {
			friendLink.setLogo(null);
		} else if (StringUtils.isEmpty(friendLink.getLogo())) {
			renderJson(ERROR_VIEW);
		}
		friendLinkService.update(friendLink);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("types", Type.values());
		setAttr("friendLink", friendLinkService.find(id));
		render("/admin/friend_link/edit.html");
	}
	
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			friendLinkService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
