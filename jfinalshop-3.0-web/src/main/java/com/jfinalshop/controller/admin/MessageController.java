package com.jfinalshop.controller.admin;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Message;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.MessageService;
import com.jfinalshop.validator.MessageValidator;

/**
 * Controller - 消息
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/message")
public class MessageController extends BaseAdminController {

	private MessageService messageService = enhance(MessageService.class);
	private MemberService memberService = enhance(MemberService.class);
	private Message message;
	
	/**
	 * 检查用户名是否合法
	 */
	public void checkUsername() {
		String username = getPara("username");
		if (memberService.usernameExists(username)) {
			renderJson(true);
			return;
		}
		renderJson(false);
	}
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<Message> page = messageService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/message/list.html");
	}
	
	/**
	 * 草稿箱
	 */
	public void draft() {
		Pageable pageable = getBean(Pageable.class);
		Page<Message> page = messageService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/message/draft.html");
	}
	
	/**
	 * 发送
	 */
	public void send() {
		Long draftMessageId = getParaToLong("draftMessageId");
		Message draftMessage = messageService.find(draftMessageId);
		if (draftMessage != null && draftMessage.getIsDraft() && draftMessage.getSenderId() == null) {
			setAttr("draftMessage", draftMessage);
		}
		render("/admin/message/send.html");
	}
	
	/**
	 * 立即发送
	 */
	@Before(MessageValidator.class)
	public void submit() {
		Long draftMessageId = getParaToLong("draftMessageId");
		String username = getPara("username");
		Message message = getModel(Message.class);
		
		Message draftMessage = messageService.find(draftMessageId);
		if (draftMessage != null && draftMessage.getIsDraft() && draftMessage.getSenderId() == null) {
			messageService.delete(draftMessage.getId());
		}
		Member receiver = null;
		if (StringUtils.isNotEmpty(username)) {
			receiver = memberService.findByUsername(username);
			if (receiver == null) {
				renderJson(ERROR_VIEW);
				return;
			}
		}
		message.setIp(getRequest().getRemoteAddr());
		message.setSenderRead(true);
		message.setReceiverRead(false);
		message.setSenderDelete(false);
		message.setReceiverDelete(false);
		message.setSenderId(null);
		message.setReceiverId(receiver.getId());
		message.setForMessage(null);
		messageService.save(message);
		if (message.getIsDraft()) {
			//addFlashMessage(redirectAttributes, com.jfinalshop.common.Message.success("admin.message.saveDraftSuccess"));
			draft();
		} else {
			//addFlashMessage(redirectAttributes, com.jfinalshop.common.Message.success("admin.message.sendSuccess"));
			list();
		}
	}
	
	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		message = messageService.find(id);
		if (message == null || message.getIsDraft() || message.getForMessage() != null) {
			renderJson(ERROR_VIEW);
			return;
		}
		if ((message.getSender() != null && message.getReceiver() != null) || (message.getReceiver() == null && message.getReceiverDelete()) || (message.getSender() == null && message.getSenderDelete())) {
			renderJson(ERROR_VIEW);
			return;
		}
		if (message.getReceiver() == null) {
			message.setReceiverRead(true);
		} else {
			message.setSenderRead(true);
		}
		messageService.update(message);
		setAttr("adminMessage", message);
		render("/admin/message/view.html");
	}
	
	/**
	 * 回复
	 */
	public void reply() {
		Long id = getParaToLong("message.id");
		message = getModel(Message.class);
		Message forMessage = messageService.find(id);
		if (forMessage == null || forMessage.getIsDraft() || forMessage.getForMessage() != null) {
			renderJson(ERROR_VIEW);
			return;
		}
		if ((forMessage.getSenderId() != null && forMessage.getReceiverId() != null) || (forMessage.getReceiverId() == null && forMessage.getReceiverDelete()) || (forMessage.getSenderId() == null && forMessage.getSenderDelete())) {
			renderJson(ERROR_VIEW);
			return;
		}
		message.setTitle("reply: " + forMessage.getTitle());
		message.setIp(getRequest().getRemoteAddr());
		message.setIsDraft(false);
		message.setSenderRead(true);
		message.setReceiverRead(false);
		message.setSenderDelete(false);
		message.setReceiverDelete(false);
		message.setSenderId(null);
		message.setReceiverId(forMessage.getReceiverId() == null ? forMessage.getSenderId() : forMessage.getReceiverId());
		if ((forMessage.getReceiver() == null && !forMessage.getSenderDelete()) || (forMessage.getSender() == null && !forMessage.getReceiverDelete())) {
			message.setForMessage(forMessage.getId());
		}
		//message.setReplyMessages(null);
		messageService.save(message);

		if (forMessage.getSenderId() == null) {
			forMessage.setSenderRead(true);
			forMessage.setReceiverRead(false);
		} else {
			forMessage.setSenderRead(false);
			forMessage.setReceiverRead(true);
		}
		messageService.update(forMessage);

		if ((forMessage.getReceiverId() == null && !forMessage.getSenderDelete()) || (forMessage.getSender() == null && !forMessage.getReceiverDelete())) {
			//addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
			redirect("view?id=" + forMessage.getId());
		} else {
			//addFlashMessage(redirectAttributes, net.shopxx.Message.success("admin.message.replySuccess"));
			list();
		}
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			messageService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(com.jfinalshop.common.Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
