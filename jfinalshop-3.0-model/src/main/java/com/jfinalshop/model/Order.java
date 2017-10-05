package com.jfinalshop.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Filter;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.common.Setting;
import com.jfinalshop.model.base.BaseOrder;
import com.jfinalshop.utils.ConditionUtil;
import com.jfinalshop.utils.DateUtil;
import com.jfinalshop.utils.SettingUtils;

/**
 * Dao - 订单
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Order extends BaseOrder<Order> {
	public static final Order dao = new Order();
	
	/** 订单名称分隔符 */
	private static final String NAME_SEPARATOR = " ";

	/**
	 * 订单状态
	 */
	public enum OrderStatus {

		/** 未确认 */
		unconfirmed,

		/** 已确认 */
		confirmed,

		/** 已完成 */
		completed,

		/** 已取消 */
		cancelled
	}

	/**
	 * 支付状态
	 */
	public enum PaymentStatus {

		/** 未支付 */
		unpaid,

		/** 部分支付 */
		partialPayment,

		/** 已支付 */
		paid,

		/** 部分退款 */
		partialRefunds,

		/** 已退款 */
		refunded
	}

	/**
	 * 配送状态
	 */
	public enum ShippingStatus {

		/** 未发货 */
		unshipped,

		/** 部分发货 */
		partialShipment,

		/** 已发货 */
		shipped,

		/** 部分退货 */
		partialReturns,

		/** 已退货 */
		returned
	}
	
	/**
	 * 订单状态
	 */
	public OrderStatus getOrderStatusValues() {
		return OrderStatus.values()[getOrderStatus()];
	}
	
	/**
	 * 支付状态
	 */
	public PaymentStatus getPaymentStatusValues() {
		return PaymentStatus.values()[getPaymentStatus()];
	}
	
	/**
	 * 配送状态
	 */
	public ShippingStatus getShippingStatusValues() {
		return ShippingStatus.values()[getShippingStatus()];
	}
	
	/**
	 * 获取支付方式
	 * 
	 * @return 支付方式
	 */
	public PaymentMethod getPaymentMethod() {
		return PaymentMethod.dao.findById(getPaymentMethodId());
	}
	
	/** 优惠券 */
	private List<Coupon> coupons = new ArrayList<Coupon>();

	/** 订单项 */
	private List<OrderItem> orderItems = new ArrayList<OrderItem>();

	/** 订单日志 */
	private List<OrderLog> orderLogs = new ArrayList<OrderLog>();

	/** 预存款 */
	private List<Deposit> deposits = new ArrayList<Deposit>();

	/** 收款单 */
	private List<Payment> payments = new ArrayList<Payment>();

	/** 退款单 */
	private List<Refunds> refunds = new ArrayList<Refunds>();

	/** 发货单 */
	private List<Shipping> shippings = new ArrayList<Shipping>();

	/** 退货单 */
	private List<Returns> returns = new ArrayList<Returns>();
	
	/**
	 * 获取优惠券
	 * 
	 * @return 优惠券
	 */
	public List<Coupon> getCoupons() {
		String sql = "SELECT c.* FROM order_coupon oc INNER JOIN coupon c ON oc.`coupons` = c.`id` WHERE oc.`orders` = ?";
		if (coupons.isEmpty()) {
			coupons = Coupon.dao.find(sql, getId());
		}
		return coupons;
	}
	
	/**
	 * 设置优惠券
	 * 
	 * @param coupons
	 *            优惠券
	 */
	public void setCoupons(List<Coupon> coupons) {
		this.coupons = coupons;
	}
	
	/**
	 * 获取订单项
	 * 
	 * @return 订单项
	 */
	public List<OrderItem> getOrderItems() {
		String sql = "SELECT * FROM order_item  WHERE `order_id` = ?";
		if(orderItems.isEmpty()) {
			orderItems = OrderItem.dao.find(sql, getId());
		}
		return orderItems;
	}
	
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	
	/**
	 * 获取订单日志
	 * 
	 * @return 订单日志
	 */
	public List<OrderLog> getOrderLogs() {
		String sql = "SELECT * FROM order_log WHERE `order_id` = ?";
		if (orderLogs.isEmpty()) {
			orderLogs = OrderLog.dao.find(sql, getId());
		}
		return orderLogs;
	}
	
	/**
	 * 设置订单日志
	 * 
	 * @param orderLogs
	 *            订单日志
	 */
	public void setOrderLogs(List<OrderLog> orderLogs) {
		this.orderLogs = orderLogs;
	}
	
	/**
	 * 获取预存款
	 * 
	 * @return 预存款
	 */
	public List<Deposit> getDeposits() {
		String sql = "SELECT * FROM deposit d WHERE d.`order_id` = ?";
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
		String sql = "SELECT * FROM payment WHERE `order_id` = ?";
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
	 * 获取退款单
	 * 
	 * @return 退款单
	 */
	public List<Refunds> getRefunds() {
		String sql = "SELECT * FROM refunds WHERE `order_id` = ?";
		if (refunds.isEmpty()) {
			refunds = Refunds.dao.find(sql, getId());
		}
		return refunds;
	}
	
	/**
	 * 设置退款单
	 * 
	 * @param refunds
	 *            退款单
	 */
	public void setRefunds(List<Refunds> refunds) {
		this.refunds = refunds;
	}

	/**
	 * 获取发货单
	 * 
	 * @return 发货单
	 */
	public List<Shipping> getShippings() {
		String sql = "SELECT * FROM shipping WHERE `order_id` = ?";
		if (shippings.isEmpty()) {
			shippings = Shipping.dao.find(sql, getId());
		}
		return shippings;
	}
	
	/**
	 * 设置发货单
	 * 
	 * @param shippings
	 *            发货单
	 */
	public void setShippings(List<Shipping> shippings) {
		this.shippings = shippings;
	}
	
	/**
	 * 获取退货单
	 * 
	 * @return 退货单
	 */
	public List<Returns> getReturns() {
		String sql = "SELECT * FROM returns WHERE `order_id` = ?";
		if (returns.isEmpty()) {
			returns = Returns.dao.find(sql, getId());
		}
		return returns;
	}
	
	/**
	 * 设置退货单
	 * 
	 * @param returns
	 *            退货单
	 */
	public void setReturns(List<Returns> returns) {
		this.returns = returns;
	}
	
	
	/**
	 * 获取操作员
	 * 
	 * @return 操作员
	 */
	public Admin getOperator() {
		return Admin.dao.findById(getOperatorId());
	}
	
	/**
	 * 根据订单编号查找订单
	 * 
	 * @param sn
	 *            订单编号(忽略大小写)
	 * @return 订单，若不存在则返回null
	 */
	public Order findBySn(String sn) {
		if (sn == null) {
			return null;
		}
		String sql = "SELECT * FROM `order` WHERE LOWER(sn) = LOWER(?)";
		try {
			return findFirst(sql, sn);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 查找订单
	 * 
	 * @param member
	 *            会员
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 订单
	 */
	public List<Order> findList(Member member, Integer count, List<Filter> filters, List<com.jfinalshop.common.Order> orders) {
		if (member == null) {
			return null;
		}
		String sql = "SELECT * FROM `order` WHERE member_id = ?";
		sql += ConditionUtil.buildSQL(null, count, filters, orders);
		return find(sql, member.getId());
	}
	
	/**
	 * 查找订单分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 订单分页
	 */
	public Page<Order> findPage(Member member, Pageable pageable) {
		if (member == null) {
			return null;
		}
		String select = " SELECT * ";
		String sqlExceptSelect = " FROM `order` WHERE member_id = ? ";
		Page<Order> orders = paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect, member.getId());
		return orders;
	}
	
	/**
	 * 查找订单分页
	 * 
	 * @param orderStatus
	 *            订单状态
	 * @param paymentStatus
	 *            支付状态
	 * @param shippingStatus
	 *            配送状态
	 * @param hasExpired
	 *            是否已过期
	 * @param pageable
	 *            分页信息
	 * @return 商品分页
	 */
	public Page<Order> findPage(OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable) {
		String select = " SELECT  * ";
		String sqlExceptSelect = " FROM `order` WHERE 1 = 1 ";
		if (orderStatus != null) {
			sqlExceptSelect += " AND order_status = " + orderStatus.ordinal();
		}
		if (paymentStatus != null) {
			sqlExceptSelect += " AND payment_status = " + paymentStatus.ordinal();
		}
		if (shippingStatus != null) {
			sqlExceptSelect += " AND shipping_status = " + shippingStatus.ordinal();
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sqlExceptSelect += " AND (expire IS NOT NULL OR expire < '" + DateUtil.getDateTime(new Date()) + "')";
			} else {
				sqlExceptSelect += " AND (expire IS NULL OR expire <= '" + DateUtil.getDateTime(new Date()) + "')";
			}
		}
		Page<Order> orders = paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect);
		return orders;
	}
	
	/**
	 * 查询订单数量
	 * 
	 * @param orderStatus
	 *            订单状态
	 * @param paymentStatus
	 *            支付状态
	 * @param shippingStatus
	 *            配送状态
	 * @param hasExpired
	 *            是否已过期
	 * @return 订单数量
	 */
	public Long count(OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired) {
		String sql = "SELECT count(*) FROM `order` WHERE 1 = 1 ";
		if (orderStatus != null) {
			sql += " AND order_status = " + orderStatus.ordinal();
		}
		if (paymentStatus != null) {
			sql += " AND payment_status = " + paymentStatus.ordinal();
		}
		if (shippingStatus != null) {
			sql += " AND shipping_status = " + shippingStatus.ordinal();
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sql += " AND (expire IS NOT NULL OR expire < '" + DateUtil.getDateTime(new Date()) + "')";
			} else {
				sql += " AND (expire IS NULL OR expire <= '" + DateUtil.getDateTime(new Date()) + "')";
			}
		}
		return Db.queryLong(sql);
	}

	/**
	 * 查询等待支付订单数量
	 * 
	 * @param member
	 *            会员
	 * @return 等待支付订单数量
	 */
	public Long waitingPaymentCount(Member member) {
		String sql = "SELECT count(*) FROM `order` WHERE order_status != ? AND  order_status != ? OR (payment_status = ? AND payment_status = ?) AND expire <= ?";
		if (member != null) {
			sql += " AND member_id = " + member.getId();
		}
		return Db.queryLong(sql, OrderStatus.completed.ordinal(), OrderStatus.cancelled.ordinal(), PaymentStatus.unpaid.ordinal(), PaymentStatus.partialPayment.ordinal(), new Date());
	}
	
	
	/**
	 * 查询等待发货订单数量
	 * 
	 * @param member
	 *            会员
	 * @return 等待发货订单数量
	 */
	public Long waitingShippingCount(Member member) {
		String sql = "SELECT count(*) FROM `order` WHERE order_status != ? AND  order_status != ? AND payment_status = ? AND shipping_status = ? AND expire <= ?";
		if (member != null) {
			sql += " AND member_id = " + member.getId();
		}
		return Db.queryLong(sql, OrderStatus.completed.ordinal(), OrderStatus.cancelled.ordinal(), PaymentStatus.paid.ordinal(), ShippingStatus.unshipped.ordinal(), new Date());
	}
	
	
	/**
	 * 获取销售额
	 * 
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @return 销售额
	 */
	public BigDecimal getSalesAmount(Date beginDate, Date endDate) {
		String sql = "SELECT sum(amount_paid) FROM `order` WHERE order_status = ? ";
		if (beginDate != null) {
			sql += " AND creation_date >= '" + DateUtil.getDateTime(beginDate) + "'";
		}
		if (endDate != null) {
			sql += " AND creation_date <= '" + DateUtil.getDateTime(endDate) + "'";
		}
		return Db.queryBigDecimal(sql, OrderStatus.completed.ordinal());
	}
	
	/**
	 * 获取销售量
	 * 
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @return 销售量
	 */
	public Integer getSalesVolume(Date beginDate, Date endDate) {
		String sql = "SELECT SUM(i.shipped_quantity) FROM order_item i  LEFT JOIN `order` o ON i.order_id = o.id  WHERE o.order_status = ? ";
		if (beginDate != null) {
			sql += " AND o.creation_date >= '" + DateUtil.getDateTime(beginDate) + "'";
		}
		if (endDate != null) {
			sql += " AND o.creation_date <= '" + DateUtil.getDateTime(endDate) + "'";
		}
		return OrderItem.dao.findFirst(sql, OrderStatus.completed.ordinal()).getShippedQuantity();
	}
	
	/**
	 * 释放过期订单库存
	 */
	public void releaseStock() {
		String sql = "SELECT * FROM `order` WHERE is_allocated_stock = ? AND expire IS NOT NULL AND expire <= ?";
		List<Order> orders = find(sql, true, new Date());
		if (orders != null) {
			for (Order order : orders) {
				if (order != null && order.getOrderItems() != null) {
					for (OrderItem orderItem : order.getOrderItems()) {
						if (orderItem != null) {
							Product product = orderItem.getProduct();
							if (product != null) {
								product.setAllocatedStock(product.getAllocatedStock() - (orderItem.getQuantity() - orderItem.getShippedQuantity()));
								product.update();
							}
						}
					}
					order.setIsAllocatedStock(false);
					order.update();
				}
			}
		}
	}
	
	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public Member getMember() {
		return Member.dao.findById(getMemberId());
	}
	
	/**
	 * 获取订单名称
	 * 
	 * @return 订单名称
	 */
	public String getName() {
		StringBuffer name = new StringBuffer();
		if (getOrderItems() != null) {
			for (OrderItem orderItem : getOrderItems()) {
				if (orderItem != null && orderItem.getFullName() != null) {
					name.append(NAME_SEPARATOR).append(orderItem.getFullName());
				}
			}
			if (name.length() > 0) {
				name.deleteCharAt(0);
			}
		}
		return name.toString();
	}

	/**
	 * 获取商品重量
	 * 
	 * @return 商品重量
	 */
	public int getWeight() {
		int weight = 0;
		if (getOrderItems() != null) {
			for (OrderItem orderItem : getOrderItems()) {
				if (orderItem != null) {
					weight += orderItem.getTotalWeight();
				}
			}
		}
		return weight;
	}

	/**
	 * 获取商品数量
	 * 
	 * @return 商品数量
	 */
	public int getQuantity() {
		int quantity = 0;
		if (getOrderItems() != null) {
			for (OrderItem orderItem : getOrderItems()) {
				if (orderItem != null && orderItem.getQuantity() != null) {
					quantity += orderItem.getQuantity();
				}
			}
		}
		return quantity;
	}

	/**
	 * 获取已发货数量
	 * 
	 * @return 已发货数量
	 */
	public int getShippedQuantity() {
		int shippedQuantity = 0;
		if (getOrderItems() != null) {
			for (OrderItem orderItem : getOrderItems()) {
				if (orderItem != null && orderItem.getShippedQuantity() != null) {
					shippedQuantity += orderItem.getShippedQuantity();
				}
			}
		}
		return shippedQuantity;
	}

	/**
	 * 获取已退货数量
	 * 
	 * @return 已退货数量
	 */
	public int getReturnQuantity() {
		int returnQuantity = 0;
		if (getOrderItems() != null) {
			for (OrderItem orderItem : getOrderItems()) {
				if (orderItem != null && orderItem.getReturnQuantity() != null) {
					returnQuantity += orderItem.getReturnQuantity();
				}
			}
		}
		return returnQuantity;
	}

	/**
	 * 获取商品价格
	 * 
	 * @return 商品价格
	 */
	public BigDecimal getPrice() {
		BigDecimal price = new BigDecimal(0);
		if (getOrderItems() != null) {
			for (OrderItem orderItem : getOrderItems()) {
				if (orderItem != null && orderItem.getSubtotal() != null) {
					price = price.add(orderItem.getSubtotal());
				}
			}
		}
		return price;
	}

	/**
	 * 获取订单金额
	 * 
	 * @return 订单金额
	 */
	public BigDecimal getAmount() {
		BigDecimal amount = getPrice();
		if (getFee() != null) {
			amount = amount.add(getFee());
		}
		if (getFreight() != null) {
			amount = amount.add(getFreight());
		}
		if (getPromotionDiscount() != null) {
			amount = amount.subtract(getPromotionDiscount());
		}
		if (getCouponDiscount() != null) {
			amount = amount.subtract(getCouponDiscount());
		}
		if (getOffsetAmount() != null) {
			amount = amount.add(getOffsetAmount());
		}
		if (getTax() != null) {
			amount = amount.add(getTax());
		}
		return amount.compareTo(new BigDecimal(0)) > 0 ? amount : new BigDecimal(0);
	}

	/**
	 * 获取应付金额
	 * 
	 * @return 应付金额
	 */
	public BigDecimal getAmountPayable() {
		BigDecimal amountPayable = getAmount().subtract(getAmountPaid());
		return amountPayable.compareTo(new BigDecimal(0)) > 0 ? amountPayable : new BigDecimal(0);
	}

	/**
	 * 是否已过期
	 * 
	 * @return 是否已过期
	 */
	public boolean isExpired() {
		return getExpire() != null && new Date().after(getExpire());
	}

	/**
	 * 获取订单项
	 * 
	 * @param sn
	 *            商品编号
	 * @return 订单项
	 */
	public OrderItem getOrderItem(String sn) {
		if (sn != null && getOrderItems() != null) {
			for (OrderItem orderItem : getOrderItems()) {
				if (orderItem != null && sn.equalsIgnoreCase(orderItem.getSn())) {
					return orderItem;
				}
			}
		}
		return null;
	}

	/**
	 * 判断是否已锁定
	 * 
	 * @param operator
	 *            操作员
	 * @return 是否已锁定
	 */
	public boolean isLocked(Admin operator) {
		return getLockExpire() != null && new Date().before(getLockExpire()) && ((operator != null && !operator.equals(getOperator())) || (operator == null && getOperator() != null));
	}

	/**
	 * 计算税金
	 * 
	 * @return 税金
	 */
	public BigDecimal calculateTax() {
		BigDecimal tax = new BigDecimal(0);
		Setting setting = SettingUtils.get();
		if (setting.getIsTaxPriceEnabled()) {
			BigDecimal amount = getPrice();
			if (getPromotionDiscount() != null) {
				amount = amount.subtract(getPromotionDiscount());
			}
			if (getCouponDiscount() != null) {
				amount = amount.subtract(getCouponDiscount());
			}
			if (getOffsetAmount() != null) {
				amount = amount.add(getOffsetAmount());
			}
			tax = amount.multiply(new BigDecimal(setting.getTaxRate().toString()));
		}
		return setting.setScale(tax);
	}
	
}
