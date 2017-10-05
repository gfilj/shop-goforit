/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop.member;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.common.Setting;
import com.jfinalshop.common.Message.Type;
import com.jfinalshop.controller.shop.BaseShopController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.Coupon;
import com.jfinalshop.model.CouponCode;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.Order.OrderStatus;
import com.jfinalshop.model.Order.PaymentStatus;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.model.Receiver;
import com.jfinalshop.model.Shipping;
import com.jfinalshop.model.ShippingMethod;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.service.CartService;
import com.jfinalshop.service.CouponCodeService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.PaymentMethodService;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.service.ReceiverService;
import com.jfinalshop.service.ShippingMethodService;
import com.jfinalshop.service.ShippingService;
import com.jfinalshop.utils.SettingUtils;

/**
 * Controller - 会员中心 - 订单
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/order")
@Before(MemberInterceptor.class)
public class OrderController extends BaseShopController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	private MemberService memberService = enhance(MemberService.class);
	private ReceiverService receiverService = enhance(ReceiverService.class);
	private CartService cartService = enhance(CartService.class);
	private PaymentMethodService paymentMethodService = enhance(PaymentMethodService.class);
	private ShippingMethodService shippingMethodService = enhance(ShippingMethodService.class);
	private CouponCodeService couponCodeService =  enhance(CouponCodeService.class);
	private OrderService orderService =  enhance(OrderService.class);
	private ShippingService shippingService =  enhance(ShippingService.class);
	private PluginService pluginService = new PluginService();

	/**
	 * 保存收货地址
	 */
	public void saveReceiver() {
		Receiver receiver = getModel(Receiver.class);
		Long areaId = getParaToLong("areaId");
		Boolean isDefault = getParaToBoolean("isDefault", false);
		receiver.setIsDefault(isDefault);
		receiver.setAreaId(areaId);
		Member member = memberService.getCurrent(getRequest());
		Map<String, Object> data = new HashMap<String, Object>();
		if (Receiver.MAX_RECEIVER_COUNT != null && member.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT) {
			data.put("message", Message.error("shop.order.addReceiverCountNotAllowed", Receiver.MAX_RECEIVER_COUNT));
			renderJson(data);
			return;
		}
		receiver.setMemberId(member.getId());
		receiver.setCreateBy(member.getUsername());
		receiverService.save(receiver);
		data.put("message", SUCCESS_MESSAGE);
		data.put("receiver", receiver);
		renderJson(data);
	}

	/**
	 * 订单锁定
	 */
	public void lock() {
		String sn = getPara("sn");
		com.jfinalshop.model.Order order = orderService.findBySn(sn);
		if (order != null && memberService.getCurrent(getRequest()).equals(order.getMember()) && !order.isExpired() && !order.isLocked(null) && order.getPaymentMethod() != null && order.getPaymentMethod().getMethod() == PaymentMethod.Method.online.ordinal() && (order.getPaymentStatus() == PaymentStatus.unpaid.ordinal() || order.getPaymentStatus() == PaymentStatus.partialPayment.ordinal())) {
			order.setLockExpire(DateUtils.addSeconds(new Date(), 20));
			order.setOperatorId(null);
			orderService.update(order);
			renderJson(true);
		}
		renderJson(false);
	}

	/**
	 * 检查支付
	 */
	public void checkPayment() {
		String sn = getPara("sn");
		com.jfinalshop.model.Order order = orderService.findBySn(sn);
		if (order != null && memberService.getCurrent(getRequest()).equals(order.getMember()) && order.getPaymentStatus() == PaymentStatus.paid.ordinal()) {
			renderJson(true);
		}
		renderJson(false);
	}

	/**
	 * 优惠券信息
	 */
	public void couponInfo(String code) {
		Map<String, Object> data = new HashMap<String, Object>();
		Cart cart = cartService.getCurrent(getRequest());
		if (cart == null || cart.isEmpty()) {
			data.put("message", Message.warn("shop.order.cartNotEmpty"));
			renderJson(data);
			return;
		}
		if (!cart.isCouponAllowed()) {
			data.put("message", Message.warn("shop.order.couponNotAllowed"));
			renderJson(data);
			return;
		}
		CouponCode couponCode = couponCodeService.findByCode(code);
		if (couponCode != null && couponCode.getCoupon() != null) {
			Coupon coupon = couponCode.getCoupon();
			if (!coupon.getIsEnabled()) {
				data.put("message", Message.warn("shop.order.couponDisabled"));
				renderJson(data);
				return;
			}
			if (!coupon.hasBegun()) {
				data.put("message", Message.warn("shop.order.couponNotBegin"));
				renderJson(data);
				return;
			}
			if (coupon.hasExpired()) {
				data.put("message", Message.warn("shop.order.couponHasExpired"));
				renderJson(data);
				return;
			}
			if (!cart.isValid(coupon)) {
				data.put("message", Message.warn("shop.order.couponInvalid"));
				renderJson(data);
				return;
			}
			if (couponCode.getIsUsed()) {
				data.put("message", Message.warn("shop.order.couponCodeUsed"));
				renderJson(data);
				return;
			}
			data.put("message", SUCCESS_MESSAGE);
			data.put("couponName", coupon.getName());
			renderJson(data);
			return;
		} else {
			data.put("message", Message.warn("shop.order.couponCodeNotExist"));
			renderJson(data);
			return;
		}
	}

	/**
	 * 信息
	 */
	public void info() {
		Cart cart = cartService.getCurrent(getRequest());
		if (cart == null || cart.isEmpty()) {
			redirect("/cart/list");
			return;
		}
		Order order = orderService.build(cart, null, null, null, null, false, null, false, null);
		setAttr("order", order);
		setAttr("cartToken", cart.getToken());
		setAttr("paymentMethods", paymentMethodService.findAll());
		setAttr("shippingMethods", shippingMethodService.findAll());
		render("/shop/member/order/info.html");
	}

	/**
	 * 计算
	 */
	public void calculate() {
		Long paymentMethodId = getParaToLong("paymentMethodId");
		Long shippingMethodId = getParaToLong("shippingMethodId");
		String code = getPara("code");
		Boolean isInvoice = getParaToBoolean("Boolean isInvoice", false);
		String invoiceTitle = getPara("invoiceTitle");
		Boolean useBalance = getParaToBoolean("useBalance", false);
		String memo = getPara("memo");
		
		Map<String, Object> data = new HashMap<String, Object>();
		Cart cart = cartService.getCurrent(getRequest());
		if (cart == null || cart.isEmpty()) {
			data.put("message", Message.error("shop.order.cartNotEmpty"));
			renderJson(data);
			return;
		}
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		CouponCode couponCode = couponCodeService.findByCode(code);
		Order order = orderService.build(cart, null, paymentMethod, shippingMethod, couponCode, isInvoice, invoiceTitle, useBalance, memo);

		data.put("message", SUCCESS_MESSAGE);
		data.put("quantity", order.getQuantity());
		data.put("price", order.getPrice());
		data.put("freight", order.getFreight());
		data.put("promotionDiscount", order.getPromotionDiscount());
		data.put("couponDiscount", order.getCouponDiscount());
		data.put("tax", order.getTax());
		data.put("amountPayable", order.getAmountPayable());
		renderJson(data);
	}

	/**
	 * 创建
	 */
	public void create() {
		String cartToken = getPara("cartToken");
		Long receiverId = getParaToLong("receiverId");
		Long paymentMethodId = getParaToLong("paymentMethodId");
		Long shippingMethodId = getParaToLong("shippingMethodId");
		String code = getPara("code");
		Boolean isInvoice = getParaToBoolean("isInvoice", false);
		String invoiceTitle = getPara("invoiceTitle");
		Boolean useBalance = getParaToBoolean("useBalance", false);
		String memo = getPara("memo");
		
		Cart cart = cartService.getCurrent(getRequest());
		if (cart == null || cart.isEmpty()) {
			renderJson(Message.warn("shop.order.cartNotEmpty"));
			return;
		}
		if (!StringUtils.equals(cart.getToken(), cartToken)) {
			renderJson(Message.warn("shop.order.cartHasChanged"));
			return;
		}
		if (cart.getIsLowStock()) {
			renderJson(Message.warn("shop.order.cartLowStock"));
			return;
		}
		Receiver receiver = receiverService.find(receiverId);
		if (receiver == null) {
			renderJson(Message.error("shop.order.receiverNotExsit"));
			return;
		}
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		if (paymentMethod == null) {
			renderJson(Message.error("shop.order.paymentMethodNotExsit"));
			return;
		}
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		if (shippingMethod == null) {
			renderJson(Message.error("shop.order.shippingMethodNotExsit"));
			return;
		}
		List<ShippingMethod> shippingMethods = paymentMethod.getShippingMethods();
		if (!shippingMethods.contains(shippingMethod)) {
			renderJson(Message.error("shop.order.deliveryUnsupported"));
			return;
		}
		CouponCode couponCode = couponCodeService.findByCode(code);
		Order order = orderService.create(cart, receiver, paymentMethod, shippingMethod, couponCode, isInvoice, invoiceTitle, useBalance, memo, null);
		Message message = new Message(Type.success, order.getSn());
		renderJson(message);
	}

	/**
	 * 支付
	 */
	public void payment() {
		String sn = getPara("sn");
		Order order = orderService.findBySn(sn);
		if (order == null || !memberService.getCurrent(getRequest()).equals(order.getMember()) || order.isExpired() || order.getPaymentMethod() == null) {
			renderJson(ERROR_VIEW);
			return;
		}
		if (order.getPaymentMethod().getMethod() == PaymentMethod.Method.online.ordinal()) {
			List<PaymentPlugin> paymentPlugins = pluginService.getPaymentPlugins(true);
			if (!paymentPlugins.isEmpty()) {
				PaymentPlugin defaultPaymentPlugin = paymentPlugins.get(0);
				if (order.getPaymentStatus() == PaymentStatus.unpaid.ordinal() || order.getPaymentStatus() == PaymentStatus.partialPayment.ordinal()) {
					setAttr("fee", defaultPaymentPlugin.calculateFee(order.getAmountPayable()));
					setAttr("amount", defaultPaymentPlugin.calculateAmount(order.getAmountPayable()));
				}
				setAttr("defaultPaymentPlugin", defaultPaymentPlugin);
				setAttr("paymentPlugins", paymentPlugins);
			}
		}
		setAttr("order", order);
		render("/shop/member/order/payment.html");
	}

	/**
	 * 计算支付金额
	 */
	public void calculateAmount() {
		String paymentPluginId = getPara("paymentPluginId");
		String sn = getPara("sn");
		Map<String, Object> data = new HashMap<String, Object>();
		Order order = orderService.findBySn(sn);
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		if (order == null || !memberService.getCurrent(getRequest()).equals(order.getMember()) || order.isExpired() || order.isLocked(null) || order.getPaymentMethod() == null || order.getPaymentMethod().getMethod() == PaymentMethod.Method.offline.ordinal() || paymentPlugin == null || !paymentPlugin.getIsEnabled()) {
			data.put("message", ERROR_MESSAGE);
			renderJson(data);
			return;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("fee", paymentPlugin.calculateFee(order.getAmountPayable()));
		data.put("amount", paymentPlugin.calculateAmount(order.getAmountPayable()));
		renderJson(data);
		return;
	}

	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent(getRequest());
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", orderService.findPage(member, pageable));
		render("/shop/member/order/list.html");
	}

	/**
	 * 查看
	 */
	public void view() {
		String sn = getPara("sn");
		Order order = orderService.findBySn(sn);
		if (order == null) {
			renderJson(ERROR_VIEW);
			return;
		}
		Member member = memberService.getCurrent(getRequest());
		if (!member.getOrders().contains(order)) {
			renderJson(ERROR_VIEW);
			return;
		}
		setAttr("order", order);
		render("/shop/member/order/view.html");
	}

	/**
	 * 取消
	 */
	public void cancel() {
		String sn = getPara("sn");
		Order order = orderService.findBySn(sn);
		if (order != null && memberService.getCurrent(getRequest()).equals(order.getMember()) && !order.isExpired() && order.getOrderStatus() == OrderStatus.unconfirmed.ordinal() && order.getPaymentStatus() == PaymentStatus.unpaid.ordinal()) {
			if (order.isLocked(null)) {
				renderJson(Message.warn("shop.member.order.locked"));
			}
			orderService.cancel(order, null);
			renderJson(SUCCESS_MESSAGE);
		}
		renderJson(ERROR_MESSAGE);
	}

	/**
	 * 物流动态
	 */
	public void deliveryQuery() {
		String sn = getPara("sn");
		Map<String, Object> data = new HashMap<String, Object>();
		Shipping shipping = shippingService.findBySn(sn);
		Setting setting = SettingUtils.get();
		if (shipping != null && shipping.getOrder() != null && memberService.getCurrent(getRequest()).equals(shipping.getOrder().getMember()) && StringUtils.isNotEmpty(setting.getKuaidi100Key()) && StringUtils.isNotEmpty(shipping.getDeliveryCorpCode()) && StringUtils.isNotEmpty(shipping.getTrackingNo())) {
			data = shippingService.query(shipping);
		}
		renderJson(data);
	}

}