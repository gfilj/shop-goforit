package com.jfinalshop.model;

import java.util.List;

import com.jfinalshop.model.base.BaseFriendLink;

/**
 * Dao - 友情链接
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class FriendLink extends BaseFriendLink<FriendLink> {
	public static final FriendLink dao = new FriendLink();
	
	/**
	 * 类型
	 */
	public enum Type {

		/** 文本 */
		text,

		/** 图片 */
		image
	}
	
	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public Type getTypeValues() {
		return Type.values()[getType()];
	}
	
	/**
	 * 查找友情链接
	 * 
	 * @param type
	 *            类型
	 * @return 友情链接
	 */
	public List<FriendLink> findList(Type type) {
		String sql = "SELECT * FROM friend_link WHERE type = ? ORDER BY orders ASC";
		return find(sql, type.ordinal());
	}

}
