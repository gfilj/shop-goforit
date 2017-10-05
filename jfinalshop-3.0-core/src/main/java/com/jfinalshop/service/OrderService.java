package com.jfinalshop.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Filter;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.common.Setting;
import com.jfinalshop.common.Setting.StockAllocationTime;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.CartItem;
import com.jfinalshop.model.Coupon;
import com.jfinalshop.model.CouponCode;
import com.jfinalshop.model.Deposit;
import com.jfinalshop.model.GiftItem;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.Order.OrderStatus;
import com.jfinalshop.model.Order.PaymentStatus;
import com.jfinalshop.model.Order.ShippingStatus;
import com.jfinalshop.model.OrderItem;
import com.jfinalshop.model.OrderLog;
import com.jfinalshop.model.OrderLog.Type;
import com.jfinalshop.model.Payment;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.model.Receiver;
import com.jfinalshop.model.Refunds;
import com.jfinalshop.model.Returns;
import com.jfinalshop.model.ReturnsItem;
import com.jfinalshop.model.Shipping;
import com.jfinalshop.model.ShippingItem;
import com.jfinalshop.model.ShippingMethod;
import com.jfinalshop.model.Sn;
import com.jfinalshop.utils.AssertUtil;
import com.jfinalshop.utils.SettingUtils;

/**
 * Service - 订单
 * 
 * 
 * 
 */
public class OrderService extends BaseService<Order> {

	public OrderService() {
		super(Order.class);
	}
	
	/** 静态化 */
	private StaticService staticService = Enhancer.enhance(StaticService.class);
	/** 购物车 */
	private CartService cartService = new CartService();
	/** 发货单 */
	private ShippingService shippingService = Enhancer.enhance(ShippingService.class);
	/** 退货单 */
	private ReturnsService returnsService = Enhancer.enhance(ReturnsService.class);
	
	/**
	 * 根据订单编号查找订单
	 * 
	 * @param sn
	 *            订单编号(忽略大小写)
	 * @return 若不存在则返回null
	 */
	public Order findBySn(String sn) {
		return Order.dao.findBySn(sn);
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
		return Order.dao.findList(member, count, filters, orders);
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
		return Order.dao.findPage(member, pageable);
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
		return Order.dao.findPage(orderStatus, paymentStatus, shippingStatus, hasExpired, pageable);
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
		return Order.dao.count(orderStatus, paymentStatus, shippingStatus, hasExpired);
	}
	
	/**
	 * 查询等待支付订单数量
	 * 
	 * @param member
	 *            会员
	 * @return 等待支付订单数量
	 */
	public Long waitingPaymentCount(Member member) {
		return Order.dao.waitingPaymentCount(member);
	}
	
	
	/**
	 * 查询等待发货订单数量
	 * 
	 * @param member
	 *            会员
	 * @return 等待发货订单数量
	 */
	public Long waitingShippingCount(Member member) {
		return Order.dao.waitingShippingCount(member);
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
		return Order.dao.getSalesAmount(beginDate, endDate);
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
		return Order.dao.getSalesVolume(beginDate, endDate);
	}
	
	
	/**
	 * 释放过期订单库存
	 */
	public void releaseStock() {
		Order.dao.releaseStock();
	}
	
	
	/**
	 * 生成订单
	 * 
	 * @param cart
	 *            购物车
	 * @param receiver
	 *            收货地址
	 * @param paymentMethod
	 *            支付方式
	 * @param shippingMethod
	 *            配送方式
	 * @param couponCode
	 *            优惠码
	 * @param isInvoice
	 *            是否开据发票
	 * @param invoiceTitle
	 *            发票抬头
	 * @param useBalance
	 *            是否使用余额
	 * @param memo
	 *            附言
	 * @return 订单
	 */
	public Order build(Cart cart, Receiver receiver, PaymentMethod paymentMethod, ShippingMethod shippingMethod, CouponCode couponCode, boolean isInvoice, String invoiceTitle, boolean useBalance, String memo) {
		AssertUtil.notNull(cart);
		AssertUtil.notNull(cart.getMember());
		AssertUtil.notEmpty(cart.getCartItems());

		Order order = new Order();
		order.setShippingStatus(ShippingStatus.unshipped.ordinal());
		order.setFee(new BigDecimal(0));
		order.setPromotionDiscount(cart.getDiscount());
		order.setCouponDiscount(new BigDecimal(0));
		order.setOffsetAmount(new BigDecimal(0));
		order.setPoint(cart.getEffectivePoint());
		order.setMemo(memo);
		order.setMemberId(cart.getMemberId());

		if (receiver != null) {
			order.setConsignee(receiver.getConsignee());
			order.setAreaName(receiver.getAreaName());
			order.setAddress(receiver.getAddress());
			order.setZipCode(receiver.getZipCode());
			order.setPhone(receiver.getPhone());
			order.setAreaId(receiver.getAreaId());
		}

		if (!cart.getPromotions().isEmpty()) {
			StringBuffer promotionName = new StringBuffer();
			for (Promotion promotion : cart.getPromotions()) {
				if (promotion != null && promotion.getName() != null) {
					promotionName.append(" " + promotion.getName());
				}
			}
			if (promotionName.length() > 0) {
				promotionName.deleteCharAt(0);
			}
			order.setPromotion(promotionName.toString());
		}
		
		if (paymentMethod != null) {
			//order.setShippingMethodId(paymentMethod.getId());
			order.setPaymentMethodId(paymentMethod.getId());
		}

		if (shippingMethod != null && paymentMethod != null && paymentMethod.getShippingMethods().contains(shippingMethod)) {
			BigDecimal freight = shippingMethod.calculateFreight(cart.getWeight());
			for (Promotion promotion : cart.getPromotions()) {
				if (promotion.getIsFreeShipping()) {
					freight = new BigDecimal(0);
					break;
				}
			}
			order.setFreight(freight);
			order.setShippingMethodId(shippingMethod.getId());
		} else {
			order.setFreight(new BigDecimal(0));
		}

		if (couponCode != null && cart.isCouponAllowed()) {
			if (!couponCode.getIsUsed() && couponCode.getCoupon() != null && cart.isValid(couponCode.getCoupon())) {
				BigDecimal couponDiscount = cart.getEffectivePrice().subtract(couponCode.getCoupon().calculatePrice(cart.getQuantity(), cart.getEffectivePrice()));
				couponDiscount = couponDiscount.compareTo(new BigDecimal(0)) > 0 ? couponDiscount : new BigDecimal(0);
				order.setCouponDiscount(couponDiscount);
				order.setCouponCode(couponCode.getId());
			}
		}

		List<OrderItem> orderItems = order.getOrderItems();
		for (CartItem cartItem : cart.getCartItems()) {
			if (cartItem != null && cartItem.getProduct() != null) {
				Product product = cartItem.getProduct();
				OrderItem orderItem = new OrderItem();
				orderItem.setSn(product.getSn());
				orderItem.setName(product.getName());
				orderItem.setFullName(product.getFullName());
				orderItem.setPrice(cartItem.getPrice());
				orderItem.setWeight(product.getWeight());
				orderItem.setThumbnail(product.getThumbnail());
				orderItem.setIsGift(false);
				orderItem.setQuantity(cartItem.getQuantity());
				orderItem.setShippedQuantity(0);
				orderItem.setReturnQuantity(0);
				orderItem.setProductId(product.getId());
				orderItem.setOrderId(order.getId());
				orderItems.add(orderItem);
			}
		}

		for (GiftItem giftItem : cart.getGiftItems()) {
			if (giftItem != null && giftItem.getGift() != null) {
				Product gift = Product.dao.findById(giftItem.getGift());
				OrderItem orderItem = new OrderItem();
				orderItem.setSn(gift.getSn());
				orderItem.setName(gift.getName());
				orderItem.setFullName(gift.getFullName());
				orderItem.setPrice(new BigDecimal(0));
				orderItem.setWeight(gift.getWeight());
				orderItem.setThumbnail(gift.getThumbnail());
				orderItem.setIsGift(true);
				orderItem.setQuantity(giftItem.getQuantity());
				orderItem.setShippedQuantity(0);
				orderItem.setReturnQuantity(0);
				orderItem.setProductId(gift.getId());
				orderItem.setOrderId(order.getId());
				orderItems.add(orderItem);
			}
		}
		order.setOrderItems(orderItems);
		
		Setting setting = SettingUtils.get();
		if (setting.getIsInvoiceEnabled() && isInvoice && StringUtils.isNotEmpty(invoiceTitle)) {
			order.setIsInvoice(true);
			order.setInvoiceTitle(invoiceTitle);
			order.setTax(order.calculateTax());
		} else {
			order.setIsInvoice(false);
			order.setTax(new BigDecimal(0));
		}

		if (useBalance) {
			Member member = cart.getMember();
			if (member.getBalance().compareTo(order.getAmount()) >= 0) {
				order.setAmountPaid(order.getAmount());
			} else {
				order.setAmountPaid(member.getBalance());
			}
		} else {
			order.setAmountPaid(new BigDecimal(0));
		}

		if (order.getAmountPayable().compareTo(new BigDecimal(0)) == 0) {
			order.setOrderStatus(OrderStatus.confirmed.ordinal());
			order.setPaymentStatus(PaymentStatus.paid.ordinal());
		} else if (order.getAmountPayable().compareTo(new BigDecimal(0)) > 0 && order.getAmountPaid().compareTo(new BigDecimal(0)) > 0) {
			order.setOrderStatus(OrderStatus.confirmed.ordinal());
			order.setPaymentStatus(PaymentStatus.partialPayment.ordinal());
		} else {
			order.setOrderStatus(OrderStatus.unconfirmed.ordinal());
			order.setPaymentStatus(PaymentStatus.unpaid.ordinal());
		}

		if (paymentMethod != null && paymentMethod.getTimeout() != null && order.getPaymentStatus() == PaymentStatus.unpaid.ordinal()) {
			order.setExpire(DateUtils.addMinutes(new Date(), paymentMethod.getTimeout()));
		}
		return order;
	}
	
	/**
	 * 创建订单
	 * 
	 * @param cart
	 *            购物车
	 * @param receiver
	 *            收货地址
	 * @param paymentMethod
	 *            支付方式
	 * @param shippingMethod
	 *            配送方式
	 * @param couponCode
	 *            优惠码
	 * @param isInvoice
	 *            是否开据发票
	 * @param invoiceTitle
	 *            发票抬头
	 * @param useBalance
	 *            是否使用余额
	 * @param memo
	 *            附言
	 * @param operator
	 *            操作员
	 * @return 订单
	 */
	public Order create(Cart cart, Receiver receiver, PaymentMethod paymentMethod, ShippingMethod shippingMethod, CouponCode couponCode, boolean isInvoice, String invoiceTitle, boolean useBalance, String memo, Admin operator) {
		AssertUtil.notNull(cart);
		AssertUtil.notNull(cart.getMember());
		AssertUtil.notEmpty(cart.getCartItems());
		AssertUtil.notNull(receiver);
		AssertUtil.notNull(paymentMethod);
		AssertUtil.notNull(shippingMethod);

		Order order = build(cart, receiver, paymentMethod, shippingMethod, couponCode, isInvoice, invoiceTitle, useBalance, memo);

		order.setSn(Sn.dao.generate(Sn.Type.order));
		if (paymentMethod.getMethod() == PaymentMethod.Method.online.ordinal()) {
			order.setLockExpire(DateUtils.addSeconds(new Date(), 20));
			//order.setOperator(operator);
		}

		if (order.getCouponCode() != null) {
			couponCode.setIsUsed(true);
			couponCode.setUsedDate(new Date());
			couponCode.update();
			//couponCodeDao.merge(couponCode);
		}

		for (Promotion promotion : cart.getPromotions()) {
			for (Coupon coupon : promotion.getCoupons()) {
				order.getCoupons().add(coupon);
			}
		}

		Setting setting = SettingUtils.get();
		if (setting.getStockAllocationTime() == StockAllocationTime.order || (setting.getStockAllocationTime() == StockAllocationTime.payment && (order.getPaymentStatus() == PaymentStatus.partialPayment.ordinal() || order.getPaymentStatus() == PaymentStatus.paid.ordinal()))) {
			order.setIsAllocatedStock(true);
		} else {
			order.setIsAllocatedStock(false);
		}

		//orderDao.persist(order);
		// 订单保存
		save(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.create.ordinal());
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrderId(order.getId());
		orderLog.setCreationDate(getSysDate());
		//orderLogDao.persist(orderLog);
		orderLog.save();

		Member member = cart.getMember();
		if (order.getAmountPaid().compareTo(new BigDecimal(0)) > 0) {
			//memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);
			member.setBalance(member.getBalance().subtract(order.getAmountPaid()));
			//memberDao.merge(member);
			member.update();

			Deposit deposit = new Deposit();
			deposit.setType(operator != null ? Deposit.Type.adminPayment.ordinal() : Deposit.Type.memberPayment.ordinal());
			deposit.setCredit(new BigDecimal(0));
			deposit.setDebit(order.getAmountPaid());
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername() : null);
			deposit.setMemberId(member.getId());
			deposit.setOrderId(order.getId());
			//depositDao.persist(deposit);
			deposit.save();
		}

		if (setting.getStockAllocationTime() == StockAllocationTime.order || (setting.getStockAllocationTime() == StockAllocationTime.payment && (order.getPaymentStatus() == PaymentStatus.partialPayment.ordinal() || order.getPaymentStatus() == PaymentStatus.paid.ordinal()))) {
			for (OrderItem orderItem : order.getOrderItems()) {
				if (orderItem != null) {
					Product product = orderItem.getProduct();
					//productDao.lock(product, LockModeType.PESSIMISTIC_WRITE);
					if (product != null && product.getStock() != null) {
						product.setAllocatedStock(product.getAllocatedStock() + (orderItem.getQuantity() - orderItem.getShippedQuantity()));
						//productDao.merge(product);
						product.update();
						//orderDao.flush();
						staticService.build(product);
					}
				}
			}
		}

		//cartDao.remove(cart);
		cartService.clear(cart.getId());
		return order;
	}

	/**
	 * 更新订单
	 * 
	 * @param order
	 *            订单
	 * @param operator
	 *            操作员
	 */
	public void update(Order order, Admin operator) {
		AssertUtil.notNull(order);

		Order pOrder = Order.dao.findById(order.getId());

		if (pOrder.getIsAllocatedStock()) {
			for (OrderItem orderItem : pOrder.getOrderItems()) {
				if (orderItem != null) {
					Product product = orderItem.getProduct();
					if (product != null && product.getStock() != null) {
						product.setAllocatedStock(product.getAllocatedStock() - (orderItem.getQuantity() - orderItem.getShippedQuantity()));
						product.update();
						staticService.build(product);
					}
				}
			}
			for (OrderItem orderItem : order.getOrderItems()) {
				if (orderItem != null) {
					Product product = orderItem.getProduct();
					if (product != null && product.getStock() != null) {
						product.setAllocatedStock(product.getAllocatedStock() + (orderItem.getQuantity() - orderItem.getShippedQuantity()));
						product.update();
						staticService.build(product);
					}
				}
			}
		}

		order.update();

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.modify.ordinal());
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrderId(order.getId());
		orderLog.setCreationDate(getSysDate());
		orderLog.save();
	}
	
	/**
	 * 订单确认
	 * 
	 * @param order
	 *            订单
	 * @param operator
	 *            操作员
	 */
	public void confirm(Order order, Admin operator) {
		AssertUtil.notNull(order);

		order.setOrderStatus(OrderStatus.confirmed.ordinal());
		order.update();

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.confirm.ordinal());
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrderId(order.getId());
		orderLog.setCreationDate(getSysDate());
		orderLog.save();
	}
	
	/**
	 * 订单完成
	 * 
	 * @param order
	 *            订单
	 * @param operator
	 *            操作员
	 */
	public void complete(Order order, Admin operator) {
		AssertUtil.notNull(order);

		Member member = order.getMember();

		if (order.getShippingStatus() == ShippingStatus.partialShipment.ordinal() || order.getShippingStatus() == ShippingStatus.shipped.ordinal()) {
			member.setPoint(member.getPoint() + order.getPoint());
			for (Coupon coupon : order.getCoupons()) {
				CouponCode.dao.build(coupon, member);
			}
		}

		if (order.getShippingStatus() == ShippingStatus.unshipped.ordinal() || order.getShippingStatus() == ShippingStatus.returned.ordinal()) {
			CouponCode couponCode = CouponCode.dao.findById(order.getCouponCode());
			if (couponCode != null) {
				couponCode.setIsUsed(false);
				couponCode.setUsedDate(null);
				couponCode.update();

				order.setCouponCode(null);
				order.update();
			}
		}

		member.setAmount(member.getAmount().add(order.getAmountPaid()));
		if (!member.getMemberRank().getIsSpecial()) {
			MemberRank memberRank = MemberRank.dao.findByAmount(member.getAmount());
			if (memberRank != null && memberRank.getAmount().compareTo(member.getMemberRank().getAmount()) > 0) {
				member.setMemberRankId(memberRank.getId());
			}
		}
		member.update();

		if (order.getIsAllocatedStock()) {
			for (OrderItem orderItem : order.getOrderItems()) {
				if (orderItem != null) {
					Product product = orderItem.getProduct();
					if (product != null && product.getStock() != null) {
						product.setAllocatedStock(product.getAllocatedStock() - (orderItem.getQuantity() - orderItem.getShippedQuantity()));
						product.update();
						staticService.build(product);
					}
				}
			}
			order.setIsAllocatedStock(false);
		}

		for (OrderItem orderItem : order.getOrderItems()) {
			if (orderItem != null) {
				Product product = orderItem.getProduct();
				if (product != null) {
					Integer quantity = orderItem.getQuantity();
					Calendar nowCalendar = Calendar.getInstance();
					Calendar weekSalesCalendar = DateUtils.toCalendar(product.getWeekSalesDate());
					Calendar monthSalesCalendar = DateUtils.toCalendar(product.getMonthSalesDate());
					if (nowCalendar.get(Calendar.YEAR) != weekSalesCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.WEEK_OF_YEAR) > weekSalesCalendar.get(Calendar.WEEK_OF_YEAR)) {
						product.setWeekSales((long) quantity);
					} else {
						product.setWeekSales(product.getWeekSales() + quantity);
					}
					if (nowCalendar.get(Calendar.YEAR) != monthSalesCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.MONTH) > monthSalesCalendar.get(Calendar.MONTH)) {
						product.setMonthSales((long) quantity);
					} else {
						product.setMonthSales(product.getMonthSales() + quantity);
					}
					product.setSales(product.getSales() + quantity);
					product.setWeekSalesDate(new Date());
					product.setMonthSalesDate(new Date());
					product.update();
					staticService.build(product);
				}
			}
		}

		order.setOrderStatus(OrderStatus.completed.ordinal());
		order.setExpire(null);
		order.update();

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.complete.ordinal());
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrderId(order.getId());
		orderLog.setCreationDate(getSysDate());
		orderLog.save();
	}
	
	/**
	 * 订单取消
	 * 
	 * @param order
	 *            订单
	 * @param operator
	 *            操作员
	 */
	public void cancel(Order order, Admin operator) {
		AssertUtil.notNull(order);

		CouponCode couponCode = CouponCode.dao.findById(order.getCouponCode());
		if (couponCode != null) {
			couponCode.setIsUsed(false);
			couponCode.setUsedDate(null);
			couponCode.update();

			order.setCouponCode(null);
			order.update();
		}

		if (order.getIsAllocatedStock()) {
			for (OrderItem orderItem : order.getOrderItems()) {
				if (orderItem != null) {
					Product product = orderItem.getProduct();
					if (product != null && product.getStock() != null) {
						product.setAllocatedStock(product.getAllocatedStock() - (orderItem.getQuantity() - orderItem.getShippedQuantity()));
						product.update();
						staticService.build(product);
					}
				}
			}
			order.setIsAllocatedStock(false);
		}

		order.setOrderStatus(OrderStatus.cancelled.ordinal());
		order.setExpire(null);
		order.update();

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.cancel.ordinal());
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrderId(order.getId());
		orderLog.setCreationDate(getSysDate());
		orderLog.save();
	}

	
	
	/**
	 * 订单支付
	 * 
	 * @param order
	 *            订单
	 * @param payment
	 *            收款单
	 * @param operator
	 *            操作员
	 */
	public void payment(Order order, Payment payment, Admin operator) {
		AssertUtil.notNull(order);
		AssertUtil.notNull(payment);

		payment.setOrderId(order.getId());
		payment.update();
		if (payment.getMethod() == Payment.Method.deposit.ordinal()) {
			Member member = order.getMember();
			member.setBalance(member.getBalance().subtract(payment.getAmount()));
			member.update();

			Deposit deposit = new Deposit();
			deposit.setType(operator != null ? Deposit.Type.adminPayment.ordinal() : Deposit.Type.memberPayment.ordinal());
			deposit.setCredit(new BigDecimal(0));
			deposit.setDebit(payment.getAmount());
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername() : null);
			deposit.setMemberId(member.getId());
			deposit.setOrderId(order.getId());
			deposit.save();
		}

		Setting setting = SettingUtils.get();
		if (!order.getIsAllocatedStock() && setting.getStockAllocationTime() == StockAllocationTime.payment) {
			for (OrderItem orderItem : order.getOrderItems()) {
				if (orderItem != null) {
					Product product = orderItem.getProduct();
					if (product != null && product.getStock() != null) {
						product.setAllocatedStock(product.getAllocatedStock() + (orderItem.getQuantity() - orderItem.getShippedQuantity()));
						product.update();
						staticService.build(product);
					}
				}
			}
			order.setIsAllocatedStock(true);
		}

		order.setAmountPaid(order.getAmountPaid().add(payment.getAmount()));
		order.setFee(payment.getFee());
		order.setExpire(null);
		if (order.getAmountPaid().compareTo(order.getAmount()) >= 0) {
			order.setOrderStatus(OrderStatus.confirmed.ordinal());
			order.setPaymentStatus(PaymentStatus.paid.ordinal());
		} else if (order.getAmountPaid().compareTo(new BigDecimal(0)) > 0) {
			order.setOrderStatus(OrderStatus.confirmed.ordinal());
			order.setPaymentStatus(PaymentStatus.partialPayment.ordinal());
		}
		order.update();

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.payment.ordinal());
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrderId(order.getId());
		orderLog.setCreationDate(getSysDate());
		orderLog.save();
	}

	/**
	 * 订单退款
	 * 
	 * @param order
	 *            订单
	 * @param refunds
	 *            退款单
	 * @param operator
	 *            操作员
	 */
	public void refunds(Order order, Refunds refunds, Admin operator) {
		AssertUtil.notNull(order);
		AssertUtil.notNull(refunds);

		refunds.setOrderId(order.getId());
		refunds.setCreateBy(operator.getUsername());
		refunds.setCreationDate(new Date());
		refunds.setDeleteFlag(false);
		refunds.save();
		if (refunds.getMethod() == Refunds.Method.deposit.ordinal()) {
			Member member = order.getMember();
			member.setBalance(member.getBalance().add(refunds.getAmount()));
			member.update();

			Deposit deposit = new Deposit();
			deposit.setType(Deposit.Type.adminRefunds.ordinal());
			deposit.setCredit(refunds.getAmount());
			deposit.setDebit(new BigDecimal(0));
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername() : null);
			deposit.setMemberId(member.getId());
			deposit.setOrderId(order.getId());
			deposit.save();
		}

		order.setAmountPaid(order.getAmountPaid().subtract(refunds.getAmount()));
		order.setExpire(null);
		if (order.getAmountPaid().compareTo(new BigDecimal(0)) == 0) {
			order.setPaymentStatus(PaymentStatus.refunded.ordinal());
		} else if (order.getAmountPaid().compareTo(new BigDecimal(0)) > 0) {
			order.setPaymentStatus(PaymentStatus.partialRefunds.ordinal());
		}
		order.update();

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.refunds.ordinal());
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrderId(order.getId());
		orderLog.setCreationDate(getSysDate());
		orderLog.save();
	}
	
	/**
	 * 订单发货
	 * 
	 * @param order
	 *            订单
	 * @param shipping
	 *            发货单
	 * @param operator
	 *            操作员
	 */
	public void shipping(Order order, Shipping shipping, Admin operator) {
		AssertUtil.notNull(order);
		AssertUtil.notNull(shipping);
		AssertUtil.notEmpty(shipping.getShippingItems());

		Setting setting = SettingUtils.get();
		if (!order.getIsAllocatedStock() && setting.getStockAllocationTime() == StockAllocationTime.ship) {
			for (OrderItem orderItem : order.getOrderItems()) {
				if (orderItem != null) {
					Product product = orderItem.getProduct();
					if (product != null && product.getStock() != null) {
						product.setAllocatedStock(product.getAllocatedStock() + (orderItem.getQuantity() - orderItem.getShippedQuantity()));
						product.update();
						staticService.build(product);
					}
				}
			}
			order.setIsAllocatedStock(true);
		}

		shipping.setOrderId(order.getId());
		shippingService.save(shipping);
		for (ShippingItem shippingItem : shipping.getShippingItems()) {
			OrderItem orderItem = order.getOrderItem(shippingItem.getSn());
			if (orderItem != null) {
				Product product = orderItem.getProduct();
				if (product != null) {
					if (product.getStock() != null) {
						product.setStock(product.getStock() - shippingItem.getQuantity());
						if (order.getIsAllocatedStock()) {
							product.setAllocatedStock(product.getAllocatedStock() - shippingItem.getQuantity());
						}
					}
					product.update();
					staticService.build(product);
				}
				orderItem.setShippedQuantity(orderItem.getShippedQuantity() + shippingItem.getQuantity());
			}
		}
		if (order.getShippedQuantity() >= order.getQuantity()) {
			order.setShippingStatus(ShippingStatus.shipped.ordinal());
			order.setIsAllocatedStock(false);
		} else if (order.getShippedQuantity() > 0) {
			order.setShippingStatus(ShippingStatus.partialShipment.ordinal());
		}
		order.setExpire(null);
		order.update();

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.shipping.ordinal());
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrderId(order.getId());
		orderLog.setCreationDate(getSysDate());
		orderLog.save();
	}
	
	/**
	 * 订单退货
	 * 
	 * @param order
	 *            订单
	 * @param returns
	 *            退货单
	 * @param operator
	 *            操作员
	 */
	public void returns(Order order, Returns returns, Admin operator) {
		AssertUtil.notNull(order);
		AssertUtil.notNull(returns);
		AssertUtil.notEmpty(returns.getReturnsItems());

		returns.setOrderId(order.getId());
		returns.setCreateBy(operator.getUsername());
		returns.setCreationDate(getSysDate());
		returns.setDeleteFlag(false);
		returnsService.save(returns);
		for (ReturnsItem returnsItem : returns.getReturnsItems()) {
			OrderItem orderItem = order.getOrderItem(returnsItem.getSn());
			if (orderItem != null) {
				orderItem.setReturnQuantity(orderItem.getReturnQuantity() + returnsItem.getQuantity());
			}
		}
		if (order.getReturnQuantity() >= order.getShippedQuantity()) {
			order.setShippingStatus(ShippingStatus.returned.ordinal());
		} else if (order.getReturnQuantity() > 0) {
			order.setShippingStatus(ShippingStatus.partialReturns.ordinal());
		}
		order.setExpire(null);
		order.update();

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.returns.ordinal());
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrderId(order.getId());
		orderLog.setCreationDate(getSysDate());
		orderLog.save();
	}
	
	/**
	 * 删除
	 * @param order
	 */
	public boolean delete(Order order) {
		if (order.getIsAllocatedStock()) {
			for (OrderItem orderItem : order.getOrderItems()) {
				if (orderItem != null) {
					Product product = orderItem.getProduct();
					if (product != null && product.getStock() != null) {
						product.setAllocatedStock(product.getAllocatedStock() - (orderItem.getQuantity() - orderItem.getShippedQuantity()));
						product.update();
						staticService.build(product);
					}
				}
			}
		}
		return super.delete(order);
	}
	
	/**
	 * 持久化前处理
	 */
	public boolean save(Order order) {
		boolean result = false;
		if (order.getAreaId() != null) {
			String fullName = Area.dao.findById(order.getAreaId()).getFullName();
			order.setAreaName(fullName);
		}
		if (order.getPaymentMethodId() != null) {
			String name = PaymentMethod.dao.findById(order.getPaymentMethodId()).getName();
			order.setPaymentMethodName(name);
		}
		if (order.getShippingMethodId() != null) {
			String name = ShippingMethod.dao.findById(order.getShippingMethodId()).getName();
			order.setShippingMethodName(name);
		}
		order.setCreationDate(getSysDate());;
		order.setDeleteFlag(false);
		result = order.save();
		if (order != null && order.getOrderItems() != null) {
			for (OrderItem orderItem : order.getOrderItems()) {
				if (orderItem != null) {
					orderItem.setOrderId(order.getId());
					OrderItem.dao.save(orderItem);
				}
			}
		}
		return result;
	}
	
	/**
	 * 更新前处理
	 */
	public boolean update(Order order) {
		if (order.getAreaId() != null) {
			String fullName = Area.dao.findById(order.getAreaId()).getFullName();
			order.setAreaName(fullName);
		}
		if (order.getPaymentMethodId() != null) {
			String name = PaymentMethod.dao.findById(order.getPaymentMethodId()).getName();
			order.setPaymentMethodName(name);
		}
		if (order.getShippingMethodId() != null) {
			String name = ShippingMethod.dao.findById(order.getShippingMethodId()).getName();
			order.setShippingMethodName(name);
		}
		order.setLastUpdatedDate(getSysDate());
		return order.update();
	}
}
