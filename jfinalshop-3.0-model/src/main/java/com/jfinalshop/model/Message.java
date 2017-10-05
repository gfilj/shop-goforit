package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.base.BaseMessage;

/**
 * Dao - 消息
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Message extends BaseMessage<Message> {
	public static final Message dao = new Message();
	
	/** 回复消息 */
	private List<Message> replyMessages = new ArrayList<Message>();
	
	/**
	 * 查找消息分页
	 * 
	 * @param member
	 *            会员，null表示管理员
	 * @param pageable
	 *            分页信息
	 * @return 消息分页
	 */
	public Page<Message> findPage(Member member, Pageable pageable) {
		String select = " SELECT  * ";
		String sqlExceptSelect = " FROM `message`  WHERE for_message IS NULL AND is_draft = false ";
		if (member != null) {
			sqlExceptSelect += " AND ((sender_id = " + member.getId() + " AND sender_delete = false ) OR (receiver_id = " + member.getId() + " AND receiver_delete = false)) ";
		} else {
			sqlExceptSelect += " AND ((sender_id IS NULL AND sender_delete = false ) OR (receiver_id IS NULL AND receiver_delete = false)) ";
		}
		Page<Message> messages = paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect);
		return messages;
	}

	/**
	 * 查找草稿分页
	 * 
	 * @param sender
	 *            发件人，null表示管理员
	 * @param pageable
	 *            分页信息
	 * @return 草稿分页
	 */
	public Page<Message> findDraftPage(Member sender, Pageable pageable) {
		String select = " SELECT  * ";
		String sqlExceptSelect = " FROM message m  WHERE m.`for_message` IS NULL AND is_draft = true ";
		if (sender != null) {
			sqlExceptSelect += " AND sender_id = " + sender.getId();
		} else {
			sqlExceptSelect += " AND sender_id IS NULL ";
		}
		Page<Message> messages = paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect);
		return messages;
	}

	/**
	 * 查找消息数量
	 * 
	 * @param member
	 *            会员，null表示管理员
	 * @param read
	 *            是否已读
	 * @return 消息数量，不包含草稿
	 */
	public Long count(Member member, Boolean read) {
		String sql = " SELECT COUNT(*) FROM message m WHERE m.`for_message` IS NULL AND is_draft = FALSE ";
		if (member != null) {
			if (read != null) {
				sql += " AND ((sender_id = " + member.getId() + " AND sender_delete = false AND sender_read =  " + read + ") OR (receiver_id =  " + member.getId() + " AND receiver_delete = false AND receiver_read =  " + read + ")) ";
			} else {
				sql += " AND ((sender_id = " + member.getId() + " AND sender_delete = false ) OR (receiver_id =  " + member.getId() + " AND receiver_delete = false )) ";
			}
		} else {
			if (read != null) {
				sql += " AND ((sender_id IS NULL AND sender_delete = false AND sender_read =  " + read + ") OR (receiver_id IS NULL AND receiver_delete = false AND receiver_read =  " + read + ")) ";
			} else {
				sql += " AND ((sender_id IS NULL AND sender_delete = false ) OR (receiver_id IS NULL AND receiver_delete = false )) ";
			}
		}
		return Db.queryLong(sql);
	}
	

	/**
	 * 删除消息
	 * 
	 * @param id
	 *            ID
	 * @param member
	 *            执行人，null表示管理员
	 */
	public void remove(Long id, Member member) {
		Message message = findById(id);
		if (message == null || message.getForMessage() != null) {
			return;
		}
		if ((member != null && member.equals(message.getReceiver())) || (member == null && message.getReceiver() == null)) {
			if (!message.getIsDraft()) {
				if (message.getSenderDelete()) {
					message.delete();
				} else {
					message.setReceiverDelete(true);
					message.update();
				}
			}
		} else if ((member != null && member.equals(message.getSender())) || (member == null && message.getSender() == null)) {
			if (message.getIsDraft()) {
				message.delete();
			} else {
				if (message.getReceiverDelete()) {
					message.delete();
				} else {
					message.setSenderDelete(true);
					message.update();
				}
			}
		}
	}
	
	
	/**
	 * 获取发件人
	 * 
	 * @return 发件人
	 */
	public Member getSender() {
		return Member.dao.findById(getSenderId());
	}
	
	/**
	 * 获取收件人
	 * 
	 * @return 收件人
	 */
	public Member getReceiver() {
		return Member.dao.findById(getReceiverId());
	}
	
	/**
	 * 获取回复消息
	 * 
	 * @return 回复消息
	 */
	public List<Message> getReplyMessages() {
		String sql = "SELECT * FROM message WHERE for_message = ? ORDER BY creation_date ASC";
		if (replyMessages.isEmpty()) {
			replyMessages = Message.dao.find(sql, getId());
		}
		return find(sql, getId());
	}
	
	/**
	 * 设置回复消息
	 * 
	 * @param replyMessages
	 *            回复消息
	 */
	public void setReplyMessages(List<Message> replyMessages) {
		this.replyMessages = replyMessages;
	}
}
