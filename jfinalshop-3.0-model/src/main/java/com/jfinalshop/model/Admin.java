package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseAdmin;

/**
 * Dao - 管理员
 * 
 * 
 */
public class Admin extends BaseAdmin<Admin> {
	private static final long serialVersionUID = -2513795356530741819L;
	public static final Admin dao = new Admin();
	
	/** 角色 */
	private List<Role> roles = new ArrayList<Role>();

	/** 订单 */
	private List<Order> orders = new ArrayList<Order>();
	
	/**
	 * 根据用户名查找
	 * @param username
	 * @return
	 */
	public Admin getAdminByLoginName(String username) {
		String sql = "SELECT * FROM admin WHERE username = ? AND delete_flag = false";
		return findFirst(sql, username);
	}
	
	/**
	 * 判断用户名是否存在
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 用户名是否存在
	 */
	public boolean usernameExists(String username) {
		String sql = "SELECT COUNT(*) FROM admin WHERE LOWER(username) = ?";
		if (StrKit.notBlank(username)) {
			return Db.queryLong(sql, username) > 0;
		}
		return false;
	}
	
	/**
	 * 根据用户名查找管理员
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 管理员，若不存在则返回null
	 */
	public Admin findByUsername(String username) {
		String sql = "SELECT * FROM admin WHERE LOWER(username) = ?";
		if (StrKit.notBlank(username)) {
			return findFirst(sql, username);
		}
		return null;
	}
	
	/**
	 * 获取角色
	 * 
	 * @return 角色
	 */
	public List<Role> getRoles() {
		String sql = "SELECT r.*  FROM role r LEFT JOIN admin_role ar ON r.id = ar.roles WHERE ar.admins = ?";
		if (roles.isEmpty()) {
			roles = Role.dao.find(sql, getId());
		}
		return roles;
	}
	
	/**
	 * 设置角色
	 * 
	 * @param roles
	 *            角色
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public List<Order> getOrders() {
		String sql = "SELECT * FROM `order` WHERE `operator_id` = ?";
		if (orders.isEmpty()) {
			orders = Order.dao.find(sql, getId());
		}
		return orders;
	}

	/**
	 * 设置订单
	 * 
	 * @param orders
	 *            订单
	 */
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
}
