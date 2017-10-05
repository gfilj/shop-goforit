package com.jfinalshop.service;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Message;

/**
 * Service - 消息
 * 
 * 
 * 
 */
public class MessageService extends BaseService<Message> {

	public MessageService() {
		super(Message.class);
	}
	
	/**
	 * 查找消息分页
	 * 
	 * @param member
	 *            会员,null表示管理员
	 * @param pageable
	 *            分页信息
	 * @return 消息分页
	 */
	public Page<Message> findPage(Member member, Pageable pageable) {
		return Message.dao.findPage(member, pageable);
	}

	/**
	 * 查找草稿分页
	 * 
	 * @param sender
	 *            发件人,null表示管理员
	 * @param pageable
	 *            分页信息
	 * @return 草稿分页
	 */
	public Page<Message> findDraftPage(Member sender, Pageable pageable) {
		return Message.dao.findDraftPage(sender, pageable);
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
		return Message.dao.count(member, read);
	}

	/**
	 * 删除消息
	 * 
	 * @param id
	 *            ID
	 * @param member
	 *            执行人,null表示管理员
	 */
	public void delete(Long id, Member member) {
		Message.dao.remove(id, member);
	}

	/**
	 * 保存
	 * 
	 */
	public boolean save(Message message) {
		message.setCreationDate(getSysDate());
		message.setDeleteFlag(false);
		return message.save();
	}
	
	/**
	 * 更新
	 * 
	 */
	public boolean update(Message message) {
		message.setLastUpdatedDate(getSysDate());
		return message.update();
	}
}
