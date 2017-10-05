package com.jfinalshop.controller.admin;


import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Review;
import com.jfinalshop.model.Review.Type;
import com.jfinalshop.service.ReviewService;

/**
 * Controller - 评论
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/review")
public class ReviewController extends BaseAdminController {

	private ReviewService reviewService = enhance(ReviewService.class);
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<Review> page = reviewService.findPage(pageable);
		//setAttr("type", type);
		setAttr("types", Type.values());
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/review/list.html");
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("review", reviewService.find(id));
		render("/admin/review/edit.html");
	}
	
	/**
	 * 更新
	 */
	public void update() {
		Long id = getParaToLong("id");
		Review review = reviewService.find(id);
		if (review == null) {
			render(ERROR_VIEW);
			return;
		}
		reviewService.update(review);
		//addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		list();
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			reviewService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
