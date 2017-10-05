package com.jfinalshop.model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.MemberAttribute.Type;
import com.jfinalshop.model.Order.OrderStatus;
import com.jfinalshop.model.Order.PaymentStatus;
import com.jfinalshop.model.base.BaseMember;
import com.jfinalshop.utils.DateUtil;
import com.jfinalshop.utils.JsonUtils;

/**
 * Dao - 会员
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Member extends BaseMember<Member> {
	public static final Member dao = new Member();
	
	/** "用户名"Cookie名称 */
	public static final String USERNAME_COOKIE_NAME = "username";

	/** 会员注册项值属性个数 */
	public static final int ATTRIBUTE_VALUE_PROPERTY_COUNT = 10;

	/** 会员注册项值属性名称前缀 */
	public static final String ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX = "attributeValue";

	/** 最大收藏商品数 */
	public static final Integer MAX_FAVORITE_COUNT = 10;
	
	/**
	 * 性别
	 */
	public enum Gender {

		/** 男 */
		male,

		/** 女 */
		female
	}
	
	/** 订单 */
	private List<Order> orders = new ArrayList<Order>();

	/** 预存款 */
	private List<Deposit> deposits = new ArrayList<Deposit>();

	/** 收款单 */
	private List<Payment> payments = new ArrayList<Payment>();

	/** 优惠码 */
	private List<CouponCode> couponCodes = new ArrayList<CouponCode>();

	/** 收货地址 */
	private List<Receiver> receivers = new ArrayList<Receiver>();

	/** 评论 */
	private List<Review> reviews = new ArrayList<Review>();

	/** 咨询 */
	private List<Consultation> consultations = new ArrayList<Consultation>();

	/** 收藏商品 */
	private List<Product> favoriteProducts = new ArrayList<Product>();

	/** 到货通知 */
	private List<ProductNotify> productNotifies = new ArrayList<ProductNotify>();

	/** 接收的消息 */
	private List<Message> inMessages = new ArrayList<Message>();

	/** 发送的消息 */
	private List<Message> outMessages = new ArrayList<Message>();
	
	/**
	 * 判断用户名是否存在
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 用户名是否存在
	 */
	public boolean usernameExists(String username) {
		if (username == null) {
			return false;
		}
		String sql = "SELECT COUNT(*) FROM member WHERE LOWER(username) = LOWER(?)";
		Long count = Db.queryLong(sql, username);
		return count > 0;
	}
	
	/**
	 * 根据用户名查找会员
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 会员，若不存在则返回null
	 */
	public Member findByUsername(String username) {
		if (username == null) {
			return null;
		}
		try {
			String sql = "SELECT * FROM member WHERE lower(username) = lower(?)";
			return findFirst(sql, username);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 获取会员等级
	 * 
	 * @return 会员等级
	 */
	public MemberRank getMemberRank() {
		return MemberRank.dao.findById(getMemberRankId());
	}
	
	/**
	 * 判断E-mail是否存在
	 * 
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return E-mail是否存在
	 */
	public boolean emailExists(String email) {
		if (email == null) {
			return false;
		}
		String sql = "select count(*) from member members where lower(members.email) = lower(?)";
		Long count = Db.queryLong(sql, email);
		return count > 0;
	}
	
	
	/**
	 * 根据E-mail查找会员
	 * 
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return 会员，若不存在则返回null
	 */
	public List<Member> findListByEmail(String email) {
		if (email == null) {
			return Collections.<Member> emptyList();
		}
		String sql = "SELECT * FROM member members WHERE lower(members.email) = lower(?)";
		return find(sql, email);
	}
	
	/**
	 * 查找会员消费信息
	 * 
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @param count
	 *            数量
	 * @return 会员消费信息
	 */
	public List<Object[]> findPurchaseList(Date beginDate, Date endDate, Integer count) {
		String sql = ""
				+ "SELECT m.`id`, "
				+ "       m.`username`, "
				+ "       m.`email`, "
				+ "       m.`point`, "
				+ "       m.`amount`, "
				+ "       m.`balance`, "
				+ "       SUM(o.`amount_paid`) AS amountPaid "
				+ "FROM `member` m "
				+ "INNER JOIN `order` o ON m.`id` = o.`member_id` "
				+ "WHERE o.`order_status` = " + OrderStatus.completed.ordinal()
				+ " AND  o.`payment_status` = " + PaymentStatus.paid.ordinal();
		if (beginDate != null) {
			sql += " AND o.`creation_date` >= '" + DateUtil.getDateTime(beginDate) + "'";
		}
		if (endDate != null) {
			sql += " AND o.`creation_date` <= '" + DateUtil.getDateTime(endDate) + "'";
		}
		sql += " GROUP BY m.`id`, "
				+ "      m.`username`, "
				+ "      m.`email`, "
				+ "      m.`point`, "
				+ "      m.`amount`, "
				+ "      m.`balance` "
				+ "ORDER BY amountPaid DESC ";
		if (count != null && count >= 0) {
			sql += "LIMIT 0, " + count;
		}
		return Db.query(sql);
	}
	
	
	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public List<Order> getOrders() {
		String sql = "SELECT * FROM `order` WHERE member_id = ? ";
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
	 * 获取预存款
	 * 
	 * @return 预存款
	 */
	public List<Deposit> getDeposits() {
		String sql = "SELECT * FROM `deposit` WHERE member_id = ? ";
		if (deposits.isEmpty()) {
			deposits = Deposit.dao.find(sql, getId());
		}
		return deposits;
	}

	/**
	 * 设置预存款
	 * 
	 * @param deposits
	 *            预存款
	 */
	public void setDeposits(List<Deposit> deposits) {
		this.deposits = deposits;
	}

	
	/**
	 * 获取收款单
	 * 
	 * @return 收款单
	 */
	public List<Payment> getPayments() {
		String sql = "SELECT * FROM `payment` WHERE member_id = ? ";
		if (payments.isEmpty()) {
			payments = Payment.dao.find(sql, getId());
		}
		return payments;
	}

	/**
	 * 设置收款单
	 * 
	 * @param payments
	 *            收款单
	 */
	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}
	
	/**
	 * 获取优惠码
	 * 
	 * @return 优惠码
	 */
	public List<CouponCode> getCouponCodes() {
		String sql = "SELECT * FROM `coupon_code` WHERE member_id = ? ";
		if (couponCodes.isEmpty()) {
			couponCodes = CouponCode.dao.find(sql, getId());
		}
		return couponCodes;
	}
	
	/**
	 * 设置优惠码
	 * 
	 * @param couponCodes
	 *            优惠码
	 */
	public void setCouponCodes(List<CouponCode> couponCodes) {
		this.couponCodes = couponCodes;
	}
	
	/**
	 * 获取收货地址
	 * 
	 * @return 收货地址
	 */
	public List<Receiver> getReceivers() {
		String sql = " SELECT * FROM `receiver` WHERE member_id = ? ORDER BY is_default,creation_date DESC ";
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
	 * 获取评论
	 * 
	 * @return 评论
	 */
	public List<Review> getReviews() {
		String sql = "SELECT * FROM `review` WHERE `member_id` = ? ";
		if (reviews.isEmpty()) {
			reviews = Review.dao.find(sql, getId());
		}
		return reviews;
	}
	
	/**
	 * 设置评论
	 * 
	 * @param reviews
	 *            评论
	 */
	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
	/**
	 * 获取咨询
	 * 
	 * @return 咨询
	 */
	public List<Consultation> getConsultations() {
		String sql = "SELECT * FROM consultation WHERE `member_id` = ?";
		if (consultations.isEmpty()) {
			consultations = Consultation.dao.find(sql, getId());
		}
		return consultations;
	}
	
	/**
	 * 设置咨询
	 * 
	 * @param consultations
	 *            咨询
	 */
	public void setConsultations(List<Consultation> consultations) {
		this.consultations = consultations;
	}
	
	/**
	 * 获取收藏商品
	 * 
	 * @return 收藏商品
	 */
	public List<Product> getFavoriteProducts() {
		String sql = "SELECT p.* FROM member_favorite_product mfp INNER JOIN product p ON mfp.favorite_products = p.id WHERE mfp.favorite_members = ?";
		if (favoriteProducts.isEmpty()) {
			favoriteProducts = Product.dao.find(sql, getId());
		}
		return favoriteProducts;
	}
	
	/**
	 * 设置收藏商品
	 * 
	 * @param favoriteProducts
	 *            收藏商品
	 */
	public void setFavoriteProducts(List<Product> favoriteProducts) {
		this.favoriteProducts = favoriteProducts;
	}
	
	/**
	 * 获取到货通知
	 * 
	 * @return 到货通知
	 */
	public List<ProductNotify> getProductNotifies() {
		String sql = "SELECT * FROM `product_notify` WHERE member_id = ? ";
		if (productNotifies.isEmpty()) {
			productNotifies = ProductNotify.dao.find(sql, getId());
		}
		return productNotifies;
	}
	
	/**
	 * 设置到货通知
	 * 
	 * @param productNotifies
	 *            到货通知
	 */
	public void setProductNotifies(List<ProductNotify> productNotifies) {
		this.productNotifies = productNotifies;
	}
	
	/**
	 * 获取接收的消息
	 * 
	 * @return 接收的消息
	 */
	public List<Message> getInMessages() {
		String sql = "SELECT * FROM `message` WHERE receiver_id = ? ";
		if (inMessages.isEmpty()) {
			inMessages = Message.dao.find(sql, getId());
		}
		return inMessages;
	}
	
	/**
	 * 设置接收的消息
	 * 
	 * @param inMessages
	 *            接收的消息
	 */
	public void setInMessages(List<Message> inMessages) {
		this.inMessages = inMessages;
	}
	
	/**
	 * 获取发送的消息
	 * 
	 * @return 发送的消息
	 */
	public List<Message> getOutMessages() {
		String sql = "SELECT * FROM `message` WHERE sender_id = ? ";
		if (outMessages.isEmpty()) {
			outMessages = Message.dao.find(sql, getId());
		}
		return outMessages;
	}
	
	/**
	 * 设置发送的消息
	 * 
	 * @param outMessages
	 *            发送的消息
	 */
	public void setOutMessages(List<Message> outMessages) {
		this.outMessages = outMessages;
	}
	
	/**
	 * 获取购物车
	 * 
	 * @return 购物车
	 */
	public Cart getCart() {
		String sql = "SELECT * FROM cart c WHERE c.`member_id` = ?";
		return Cart.dao.findFirst(sql, getId());
	}
	
	/**
	 * 获取会员注册项值
	 * 
	 * @param memberAttribute
	 *            会员注册项
	 * @return 会员注册项值
	 */
	public Object getAttributeValue(MemberAttribute memberAttribute) {
		if (memberAttribute != null) {
			if (memberAttribute.getType() == Type.name.ordinal()) {
				return getName();
			} else if (memberAttribute.getType() == Type.gender.ordinal()) {
				return getGender();
			} else if (memberAttribute.getType() == Type.birth.ordinal()) {
				return getBirth();
			} else if (memberAttribute.getType() == Type.area.ordinal()) {
				return getAreaId();
			} else if (memberAttribute.getType() == Type.address.ordinal()) {
				return getAddress();
			} else if (memberAttribute.getType() == Type.zipCode.ordinal()) {
				return getZipCode();
			} else if (memberAttribute.getType() == Type.phone.ordinal()) {
				return getPhone();
			} else if (memberAttribute.getType() == Type.mobile.ordinal()) {
				return getMobile();
			} else if (memberAttribute.getType() == Type.checkbox.ordinal()) {
				if (memberAttribute.getPropertyIndex() != null) {
					try {
						String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + memberAttribute.getPropertyIndex();
						String propertyValue = (String) PropertyUtils.getProperty(this, propertyName);
						if (propertyValue != null) {
							return JsonUtils.toObject(propertyValue, List.class);
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
				}
			} else {
				if (memberAttribute.getPropertyIndex() != null) {
					try {
						String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + memberAttribute.getPropertyIndex();
						return (String) PropertyUtils.getProperty(this, propertyName);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	/**
	 * 设置会员注册项值
	 * 
	 * @param memberAttribute
	 *            会员注册项
	 * @param attributeValue
	 *            会员注册项值
	 */
	public void setAttributeValue(MemberAttribute memberAttribute, Object attributeValue) {
		if (memberAttribute != null) {
			if (attributeValue instanceof String && StringUtils.isEmpty((String) attributeValue)) {
				attributeValue = null;
			}
			if (memberAttribute.getType() == Type.name.ordinal() && (attributeValue instanceof String || attributeValue == null)) {
				setName((String) attributeValue);
			} else if (memberAttribute.getType() == Type.gender.ordinal() && (attributeValue instanceof Gender || attributeValue == null)) {
				setGender((Integer) attributeValue);
			} else if (memberAttribute.getType() == Type.birth.ordinal() && (attributeValue instanceof Date || attributeValue == null)) {
				setBirth((Date) attributeValue);
			} else if (memberAttribute.getType() == Type.area.ordinal() && (attributeValue instanceof Area || attributeValue == null)) {
				getArea((Long) attributeValue);
			} else if (memberAttribute.getType() == Type.address.ordinal() && (attributeValue instanceof String || attributeValue == null)) {
				setAddress((String) attributeValue);
			} else if (memberAttribute.getType() == Type.zipCode.ordinal() && (attributeValue instanceof String || attributeValue == null)) {
				setZipCode((String) attributeValue);
			} else if (memberAttribute.getType() == Type.phone.ordinal() && (attributeValue instanceof String || attributeValue == null)) {
				setPhone((String) attributeValue);
			} else if (memberAttribute.getType() == Type.mobile.ordinal() && (attributeValue instanceof String || attributeValue == null)) {
				setMobile((String) attributeValue);
			} else if (memberAttribute.getType() == Type.checkbox.ordinal() && (attributeValue instanceof List || attributeValue == null)) {
				if (memberAttribute.getPropertyIndex() != null) {
					if (attributeValue == null || (memberAttribute.getOptions() != null && memberAttribute.getOptions().containsAll((List<?>) attributeValue))) {
						try {
							String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + memberAttribute.getPropertyIndex();
							PropertyUtils.setProperty(this, propertyName, JsonUtils.toJson(attributeValue));
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				if (memberAttribute.getPropertyIndex() != null) {
					try {
						String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + memberAttribute.getPropertyIndex();
						PropertyUtils.setProperty(this, propertyName, attributeValue);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 移除所有会员注册项值
	 */
	public void removeAttributeValue() {
		setName(null);
		setGender(null);
		setBirth(null);
		getArea(null);
		setAddress(null);
		setZipCode(null);
		setPhone(null);
		setMobile(null);
		for (int i = 0; i < ATTRIBUTE_VALUE_PROPERTY_COUNT; i++) {
			String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + i;
			try {
				PropertyUtils.setProperty(this, propertyName, null);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取地区
	 * 
	 * @return 地区
	 */
	public Area getArea(Long id) {
		return Area.dao.findById(id);
	}

	/**
	 * 获取安全密匙
	 * 
	 * @return 安全密匙
	 */
	public SafeKey getSafeKey() {
		return getSafeKey();
	}
}
