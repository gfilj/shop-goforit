package com.jfinalshop.model;

import java.util.List;

import com.jfinalshop.model.base.BaseNavigation;

/**
 * Dao - 导航
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Navigation extends BaseNavigation<Navigation> {
	public static final Navigation dao = new Navigation();
	
	/**
	 * 位置
	 */
	public enum Position {

		/** 顶部 */
		top,

		/** 中间 */
		middle,

		/** 底部 */
		bottom
	}
	
	/**
	 * 查找导航
	 * 
	 * @param position
	 *            位置
	 * @return 导航
	 */
	public List<Navigation> findList(Position position) {
		String sql = "SELECT * FROM navigation n WHERE n.position = ? ORDER BY n.orders ASC";
		return find(sql, position.ordinal());
	}
	
	/**
	 * 获取位置
	 * 
	 * @return 位置
	 */
	public Position getPositionValues() {
		return Position.values()[getPosition()];
	}

}
