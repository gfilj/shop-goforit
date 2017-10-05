/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop.member;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.controller.shop.BaseShopController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Message;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.MessageService;

/**
 * Controller - 会员中心 - 消息
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/message")
@Before(MemberInterceptor.class)
public class MessageController extends BaseShopController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	MessageService messageService = enhance(MessageService.class);
	MemberService memberService = enhance(MemberService.class);

	/**
	 * 检查用户名是否合法
	 */
	public void checkUsername() {
		String username = getPara("username");
		if (!StringUtils.equalsIgnoreCase(username, memberService.getCurrentUsername(getRequest())) && memberService.usernameExists(username)) {
			renderJson(true);
			return;
		}
		renderJson(false);
	}

	/**
	 * 发送
	 */
	public void send() {
		Long draftMessageId = getParaToLong("draftMessageId");
		Message draftMessage = messageService.find(draftMessageId);
		if (draftMessage != null && draftMessage.getIsDraft() && memberService.getCurrent(getRequest()).equals(draftMessage.getSender())) {
			setAttr("draftMessage", draftMessage);
		}
		render("/shop/member/message/send.html");
	}

	/**
	 * 发送
	 */
	public void submit() {
		Long draftMessageId = getParaToLong("draftMessageId");
		String username = getPara("username");
		String title = getPara("title");
		String content = getPara("content");
		Boolean isDraft = getParaToBoolean("isDraft", false);
		
		Member member = memberService.getCurrent(getRequest());
		Message draftMessage = messageService.find(draftMessageId);
		if (draftMessage != null && draftMessage.getIsDraft() && member.equals(draftMessage.getSender())) {
			messageService.delete(draftMessage);
		}
		Member receiver = null;
		if (StringUtils.isNotEmpty(username)) {
			receiver = memberService.findByUsername(username);
			if (member.equals(receiver)) {
				renderJson(ERROR_VIEW);
				return;
			}
		}
		
		Message message = new Message();
		message.setTitle(title);
		message.setContent(content);
		message.setIp(getRequest().getRemoteAddr());
		message.setIsDraft(isDraft);
		message.setSenderRead(true);
		message.setReceiverRead(false);
		message.setSenderDelete(false);
		message.setReceiverDelete(false);
		message.setSenderId((member.getId()));
		message.setReceiverId(receiver != null ? receiver.getId() : null);
		messageService.save(message);
		if (isDraft) {
			addFlashMessage(com.jfinalshop.common.Message.success("shop.member.message.saveDraftSuccess"));
			redirect("/member/message/draft");
		} else {
			addFlashMessage(com.jfinalshop.common.Message.success("shop.member.message.sendSuccess"));
			redirect("/member/message/list");
		}
	}

	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		Message message = messageService.find(id);
		if (message == null || message.getIsDraft() || message.getForMessage() != null) {
			renderJson(ERROR_VIEW);
			return;
		}
		Member member = memberService.getCurrent(getRequest());
		if ((!member.equals(message.getSender()) && !member.equals(message.getReceiver())) || (member.equals(message.getReceiver()) && message.getReceiverDelete()) || (member.equals(message.getSender()) && message.getSenderDelete())) {
			renderJson(ERROR_VIEW);
			return;
		}
		if (member.equals(message.getReceiver())) {
			message.setReceiverRead(true);
		} else {
			message.setSenderRead(true);
		}
		messageService.update(message);
		setAttr("memberMessage", message);
		render("/shop/member/message/view.html");
	}

	/**
	 * 回复
	 */
	public void reply() {
		Long id = getParaToLong("id");
		String content = getPara("content");
		Message forMessage = messageService.find(id);
		if (forMessage == null || forMessage.getIsDraft() || forMessage.getForMessage() != null) {
			renderJson(ERROR_VIEW);
			return;
		}
		Member member = memberService.getCurrent(getRequest());
		if ((!member.equals(forMessage.getSender()) && !member.equals(forMessage.getReceiver())) || (member.equals(forMessage.getReceiver()) && forMessage.getReceiverDelete()) || (member.equals(forMessage.getSender()) && forMessage.getSenderDelete())) {
			renderJson(ERROR_VIEW);
			return;
		}
		Message message = new Message();
		message.setTitle("reply: " + forMessage.getTitle());
		message.setContent(content);
		message.setIp(getRequest().getRemoteAddr());
		message.setIsDraft(false);
		message.setSenderRead(true);
		message.setReceiverRead(false);
		message.setSenderDelete(false);
		message.setReceiverDelete(false);
		message.setSenderId(member.getId());
		message.setReceiverId(member.getId().equals(forMessage.getReceiver().getId()) ? forMessage.getSender().getId() : forMessage.getReceiver().getId());
		if ((member.equals(forMessage.getReceiver()) && !forMessage.getSenderDelete()) || (member.equals(forMessage.getSender()) && !forMessage.getReceiverDelete())) {
			message.setForMessage(forMessage.getId());
		}
		messageService.save(message);

		if (member.equals(forMessage.getSender())) {
			forMessage.setSenderRead(true);
			forMessage.setReceiverRead(false);
		} else {
			forMessage.setSenderRead(false);
			forMessage.setReceiverRead(true);
		}
		messageService.update(forMessage);

		if ((member.equals(forMessage.getReceiver()) && !forMessage.getSenderDelete()) || (member.equals(forMessage.getSender()) && !forMessage.getReceiverDelete())) {
			addFlashMessage(SUCCESS_MESSAGE);
			redirect("view?id=" + forMessage.getId());
		} else {
			addFlashMessage(com.jfinalshop.common.Message.success("shop.member.message.replySuccess"));
			redirect("/member/message/list");
		}
	}

	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		Member member = memberService.getCurrent(getRequest());
		setAttr("page", messageService.findPage(member, pageable));
		render("/shop/member/message/list.html");
	}

	/**
	 * 草稿箱
	 */
	public void draft() {
		Integer pageNumber = getParaToInt("pageNumber");
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		Member member = memberService.getCurrent(getRequest());
		setAttr("page", messageService.findDraftPage(member, pageable));
		render("/shop/member/message/draft.html");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long id = getParaToLong("id");
		Member member = memberService.getCurrent(getRequest());
		messageService.delete(id, member);
		renderJson(SUCCESS_MESSAGE);
	}

}