/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop.member;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.controller.shop.BaseShopController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Receiver;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ReceiverService;

/**
 * Controller - 会员中心 - 收货地址
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/receiver")
@Before(MemberInterceptor.class)
public class ReceiverController extends BaseShopController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	private MemberService memberService = enhance(MemberService.class);
	private ReceiverService receiverService = enhance(ReceiverService.class);
	private Receiver receiver;
	
	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent(getRequest());
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", receiverService.findPage(member, pageable));
		render("/shop/member/receiver/list.html");
	}

	/**
	 * 添加
	 */
	public void add() {
		Member member = memberService.getCurrent(getRequest());
		if (Receiver.MAX_RECEIVER_COUNT != null && member.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT) {
			addFlashMessage(Message.warn("shop.member.receiver.addCountNotAllowed", Receiver.MAX_RECEIVER_COUNT));
			list();
		}
		render("/shop/member/receiver/add.html");
	}

	/**
	 * 保存
	 */
	public void save() {
		receiver = getModel(Receiver.class);
		Long areaId = getParaToLong("areaId");
		receiver.setAreaId(areaId);
		Boolean isDefault = getParaToBoolean("isDefault", false);
		receiver.setIsDefault(isDefault);
		Member member = memberService.getCurrent(getRequest());
		if (Receiver.MAX_RECEIVER_COUNT != null && member.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT) {
			renderJson(ERROR_VIEW);
		}
		receiver.setMemberId(member.getId());
		receiverService.save(receiver);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		Receiver receiver = receiverService.find(id);
		if (receiver == null) {
			renderJson(ERROR_VIEW);
		}
		Member member = memberService.getCurrent(getRequest());
		if (!member.getId().equals(receiver.getMemberId())) {
			renderJson(ERROR_VIEW);
		}
		setAttr("receiver", receiver);
		render("/shop/member/receiver/edit.html");
	}

	/**
	 * 更新
	 */
	public void update() {
		receiver = getModel(Receiver.class);
		 Long id = getParaToLong("id");
		 Long areaId = getParaToLong("areaId");
		receiver.setAreaId(areaId);
		Receiver pReceiver = receiverService.find(id);
		if (pReceiver == null) {
			renderJson(ERROR_VIEW);
			return;
		}
		Member member = memberService.getCurrent(getRequest());
		if (!member.getId().equals(pReceiver.getMemberId())) {
			renderJson(ERROR_VIEW);
			return;
		}
		receiverService.update(receiver);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long id = getParaToLong("id");
		Receiver receiver = receiverService.find(id);
		if (receiver == null) {
			renderJson(ERROR_VIEW);
			return;
		}
		Member member = memberService.getCurrent(getRequest());
		if (!member.getId().equals(receiver.getMemberId())) {
			renderJson(ERROR_VIEW);
			return;
		}
		receiverService.delete(id);
		renderJson(SUCCESS_MESSAGE);
	}

}