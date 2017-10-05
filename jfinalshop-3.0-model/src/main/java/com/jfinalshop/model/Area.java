package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseArea;

/**
 * Dao - 地区
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Area extends BaseArea<Area> {
	public static final Area dao = new Area();
	
	/** 树路径分隔符 */
	private static final String TREE_PATH_SEPARATOR = ",";
	
	/** 下级地区 */
	private List<Area> children = new ArrayList<Area>();

	/** 会员 */
	private List<Member> members = new ArrayList<Member>();

	/** 收货地址 */
	private List<Receiver> receivers = new ArrayList<Receiver>();

	/** 订单 */
	private List<Order> orders = new ArrayList<Order>();

	/** 发货点 */
	private List<DeliveryCenter> deliveryCenters = new ArrayList<DeliveryCenter>();
	
	/**
	 * 查找顶级地区
	 * 
	 * @param count
	 *            数量
	 * @return 顶级地区
	 */
	public List<Area> findRoots(Integer count) {
		String sql = "SELECT * FROM area area WHERE area.parent_id IS NULL ORDER BY area.orders ASC ";
		if (count != null) {
			sql += " LIMIT 0," + count;
		}
		List<Area> query = find(sql);
		return query;
	}
	
	/**
	 * 判断是否已经存在地区
	 * 
	 * @return 顶级地区
	 */
	public boolean areaNameExists(String brandname) {
		String sql = "SELECT count(*) FROM area area WHERE LOWER(area.full_name) = LIKE '%?%' ";
		if (StrKit.notBlank(brandname)) {
			return Db.queryLong(sql, brandname) > 0;
		}
		return false;
	}
	
	/**
	 * 获取下级地区
	 * 
	 * @return 下级地区
	 */
	public List<Area> getChildren() {
		String sql = "SELECT * FROM `area` WHERE parent_id = ? ORDER BY orders ASC";
		if (children.isEmpty()) {
			children = Area.dao.find(sql, getId());
		}
		return children;
	}
	
	/**
	 * 设置下级地区
	 * 
	 * @param children
	 *            下级地区
	 */
	public void setChildren(List<Area> children) {
		this.children = children;
	}
	
	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public List<Member> getMembers() {
		String sql = "SELECT * FROM `member`  WHERE `area_id` = ?";
		if (members.isEmpty()) {
			members = Member.dao.find(sql, getId());
		}
		return members;
	}
	
	/**
	 * 设置会员
	 * 
	 * @param members
	 *            会员
	 */
	public void setMembers(List<Member> members) {
		this.members = members;
	}
	
	/**
	 * 获取收货地址
	 * 
	 * @return 收货地址
	 */
	public List<Receiver> getReceivers() {
		String sql = "SELECT * FROM `receiver`  WHERE `area_id` = ?";
		if (receivers.isEmpty()) {
			receivers = Receiver.dao.find(sql, getId());
		}
		return receivers;
	}
	
	/**
	 * 设置收货地址
	 * 
	 * @param receivers
	 *            收货地址
	 */
	public void setReceivers(List<Receiver> receivers) {
		this.receivers = receivers;
	}
	
	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public List<Order> getOrder() {
		String sql = "SELECT * FROM `order`  WHERE `area_id` = ?";
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
	
	/**
	 * 获取发货点
	 * 
	 * @return 发货点
	 */
	public List<DeliveryCenter> getDeliveryCenters() {
		String sql = "SELECT * FROM `delivery_center`  WHERE `area_id` = ?";
		if (deliveryCenters.isEmpty()) {
			deliveryCenters = DeliveryCenter.dao.find(sql, getId());
		}
		return deliveryCenters;
	}
	
	/**
	 * 设置发货点
	 * 
	 * @param deliveryCenters
	 *            发货点
	 */
	public void setDeliveryCenters(List<DeliveryCenter> deliveryCenters) {
		this.deliveryCenters = deliveryCenters;
	}

	/**
	 * 获取上级地区
	 * 
	 * @return 上级地区
	 */
	public Area getParent() {
		return Area.dao.findById(getParentId());
	}
	
	/**
	 * 持久化前处理
	 */
	public boolean save(Area area) {
		Area parent = getParent();
		if (parent != null) {
			area.setFullName(parent.getFullName() + getName());
			area.setTreePath(parent.getTreePath() + parent.getId() + TREE_PATH_SEPARATOR);
		} else {
			area.setFullName(getName());
			area.setTreePath(TREE_PATH_SEPARATOR);
		}
		return area.save();
	}
	
	
	/**
	 * 更新前处理
	 */
	public boolean update(Area area) {
		Area parent = getParent();
		if (parent != null) {
			area.setFullName(parent.getFullName() + getName());
		} else {
			area.setFullName(getName());
		}
		return area.update();
	}
	
}
