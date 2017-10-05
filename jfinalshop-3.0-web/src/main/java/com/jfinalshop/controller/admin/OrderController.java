package com.jfinalshop.controller.admin;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.DeliveryCorp;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.Order.OrderStatus;
import com.jfinalshop.model.Order.PaymentStatus;
import com.jfinalshop.model.Order.ShippingStatus;
import com.jfinalshop.model.OrderItem;
import com.jfinalshop.model.Payment;
import com.jfinalshop.model.Payment.Status;
import com.jfinalshop.model.Payment.Type;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Refunds;
import com.jfinalshop.model.Refunds.Method;
import com.jfinalshop.model.Returns;
import com.jfinalshop.model.ReturnsItem;
import com.jfinalshop.model.Shipping;
import com.jfinalshop.model.ShippingItem;
import com.jfinalshop.model.ShippingMethod;
import com.jfinalshop.model.Sn;
import com.jfinalshop.security.ShiroUtil;
import com.jfinalshop.service.AreaService;
import com.jfinalshop.service.DeliveryCorpService;
import com.jfinalshop.service.OrderItemService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.PaymentMethodService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.service.ShippingMethodService;
import com.jfinalshop.service.SnService;


/**
 * Controller - 订单
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/order")
public class OrderController extends BaseAdminController {

	private AreaService areaService = enhance(AreaService.class);
	private ProductService productService = enhance(ProductService.class);
	private OrderService orderService = enhance(OrderService.class);
	private OrderItemService orderItemService = enhance(OrderItemService.class);
	private ShippingMethodService shippingMethodService = enhance(ShippingMethodService.class);
	private DeliveryCorpService deliveryCorpService = enhance(DeliveryCorpService.class);
	private PaymentMethodService paymentMethodService = enhance(PaymentMethodService.class);
	private SnService snService = enhance(SnService.class);

	/**
	 * 检查锁定
	 */
	public void checkLock() {
		Long id = getParaToLong("id");
		Order order = orderService.find(id);
		if (order == null) {
			renderJson(Message.warn("admin.common.invalid"));
			return;
		}
		Admin admin = ShiroUtil.getAdmin();
		if (order.isLocked(admin)) {
			if (order.getOperator() != null) {
				renderJson(Message.warn("admin.order.adminLocked", order.getOperator().getUsername()));
			} else {
				renderJson(Message.warn("admin.order.memberLocked"));
			}
		} else {
			order.setLockExpire(DateUtils.addSeconds(new Date(), 20));
			order.setOperatorId(admin.getId());
			orderService.update(order);
			renderJson(SUCCESS_MESSAGE);
		}
	}

	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		setAttr("methods", Payment.Method.values());
		setAttr("refundsMethods", Refunds.Method.values());
		setAttr("paymentMethods", paymentMethodService.findAll());
		setAttr("shippingMethods", shippingMethodService.findAll());
		setAttr("deliveryCorps", deliveryCorpService.findAll());
		setAttr("order", orderService.find(id));
		render("/admin/order/view.html");
	}

	/**
	 * 确认
	 */
	public void confirm() {
		Long id = getParaToLong("id");
		Order order = orderService.find(id);
		Admin admin = ShiroUtil.getAdmin();
		if (order != null && !order.isExpired() && order.getOrderStatus() == OrderStatus.unconfirmed.ordinal() && !order.isLocked(admin)) {
			orderService.confirm(order, admin);
			addFlashMessage(SUCCESS_MESSAGE);
		} else {
			addFlashMessage(Message.warn("admin.common.invalid"));
		}
		redirect("view?id=" + id);
	}

	/**
	 * 完成
	 */
	public void complete() {
		Long id = getParaToLong("id");
		Order order = orderService.find(id);
		Admin admin = ShiroUtil.getAdmin();
		if (order != null && !order.isExpired() && order.getOrderStatus() == OrderStatus.confirmed.ordinal() && !order.isLocked(admin)) {
			orderService.complete(order, admin);
			addFlashMessage(SUCCESS_MESSAGE);
		} else {
			addFlashMessage(Message.warn("admin.common.invalid"));
		}
		redirect("view?id=" + id);
	}

	/**
	 * 取消
	 */
	public void cancel() {
		Long id = getCookieToLong("id");
		Order order = orderService.find(id);
		Admin admin = ShiroUtil.getAdmin();
		if (order != null && !order.isExpired() && order.getOrderStatus() == OrderStatus.unconfirmed.ordinal() && !order.isLocked(admin)) {
			orderService.cancel(order, admin);
			addFlashMessage(SUCCESS_MESSAGE);
		} else {
			addFlashMessage(Message.warn("admin.common.invalid"));
		}
		redirect("view?id=" + id);
	}

	/**
	 * 支付
	 */
	public void payment() {
		Long orderId = getParaToLong("orderId");
		Long paymentMethodId = getParaToLong("paymentMethodId");
		Payment payment = getModel(Payment.class);
		Order order = orderService.find(orderId);
		payment.setOrderId(order.getId());
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		payment.setPaymentMethod(paymentMethod != null ? paymentMethod.getName() : null);
		if (order.isExpired() || order.getOrderStatus() != OrderStatus.confirmed.ordinal()) {
			renderJson(ERROR_VIEW);
			return;
		}
		if (order.getPaymentStatus() != PaymentStatus.unpaid.ordinal() && order.getPaymentStatus() != PaymentStatus.partialPayment.ordinal()) {
			renderJson(ERROR_VIEW);
			return;
		}
		if (payment.getAmount().compareTo(new BigDecimal(0)) <= 0 || payment.getAmount().compareTo(order.getAmountPayable()) > 0) {
			renderJson(ERROR_VIEW);
			return;
		}
		Member member = order.getMember();
		if (payment.getMethod() == Payment.Method.deposit.ordinal() && payment.getAmount().compareTo(member.getBalance()) > 0) {
			renderJson(ERROR_VIEW);
			return;
		}
		Admin admin = ShiroUtil.getAdmin();
		if (order.isLocked(admin)) {
			renderJson(ERROR_VIEW);
			return;
		}
		payment.setSn(snService.generate(Sn.Type.payment));
		payment.setType(Type.payment.ordinal());
		payment.setStatus(Status.success.ordinal());
		payment.setFee(new BigDecimal(0));
		payment.setOperator(admin.getUsername());
		payment.setPaymentDate(new Date());
		payment.setPaymentPluginId(null);
		payment.setExpire(null);
		orderService.payment(order, payment, admin);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("view?id=" + orderId);
	}

	/**
	 * 退款
	 */
	public void refunds() {
		Long orderId = getParaToLong("orderId");
		Long paymentMethodId = getParaToLong("paymentMethodId");
		Method method = StrKit.notBlank(getPara("method")) ?  Method.valueOf(getPara("method")) : null;
		Refunds refunds = getModel(Refunds.class);
		Order order = orderService.find(orderId);
		refunds.setOrderId(order.getId());
		refunds.setMethod(method.ordinal());
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		refunds.setPaymentMethod(paymentMethod != null ? paymentMethod.getName() : null);
		if (order.isExpired() || order.getOrderStatus() != OrderStatus.confirmed.ordinal()) {
			renderJson(ERROR_VIEW);
			return;
		}
		if (order.getPaymentStatus() != PaymentStatus.paid.ordinal() && order.getPaymentStatus() != PaymentStatus.partialPayment.ordinal() && order.getPaymentStatus() != PaymentStatus.partialRefunds.ordinal()) {
			renderJson(ERROR_VIEW);
			return;
		}
		if (refunds.getAmount().compareTo(new BigDecimal(0)) <= 0 || refunds.getAmount().compareTo(order.getAmountPaid()) > 0) {
			renderJson(ERROR_VIEW);
			return;
		}
		Admin admin = ShiroUtil.getAdmin();
		if (order.isLocked(admin)) {
			renderJson(ERROR_VIEW);
			return;
		}
		refunds.setSn(snService.generate(Sn.Type.refunds));
		refunds.setOperator(admin.getUsername());
		orderService.refunds(order, refunds, admin);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("view?id=" + orderId);
	}

	/**
	 * 发货
	 */
	public void shipping() {
		Long orderId = getParaToLong("orderId");
		Long shippingMethodId = getParaToLong("shippingMethodId");
		Long deliveryCorpId = getParaToLong("deliveryCorpId");
		Long areaId = getParaToLong("areaId");
		Shipping shipping = getModel(Shipping.class);
		List<ShippingItem> shippingItems = getModels(ShippingItem.class);
		shipping.setShippingItems(shippingItems);
		Order order = orderService.find(orderId);
		for (Iterator<ShippingItem> iterator = shipping.getShippingItems().iterator(); iterator.hasNext();) {
			ShippingItem shippingItem = iterator.next();
			if (shippingItem == null || StringUtils.isEmpty(shippingItem.getSn()) || shippingItem.getQuantity() == null || shippingItem.getQuantity() <= 0) {
				iterator.remove();
				continue;
			}
			OrderItem orderItem = order.getOrderItem(shippingItem.getSn());
			if (orderItem == null || shippingItem.getQuantity() > orderItem.getQuantity() - orderItem.getShippedQuantity()) {
				renderJson(ERROR_VIEW);
				return;
			}
			if (orderItem.getProduct() != null && orderItem.getProduct().getStock() != null && shippingItem.getQuantity() > orderItem.getProduct().getStock()) {
				renderJson(ERROR_VIEW);
				return;
			}
			shippingItem.setName(orderItem.getFullName());
			shippingItem.setShippingId(shipping.getId());
		}
		shipping.setOrderId(order.getId());
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		shipping.setShippingMethod(shippingMethod != null ? shippingMethod.getName() : null);
		DeliveryCorp deliveryCorp = deliveryCorpService.find(deliveryCorpId);
		shipping.setDeliveryCorp(deliveryCorp != null ? deliveryCorp.getName() : null);
		shipping.setDeliveryCorpUrl(deliveryCorp != null ? deliveryCorp.getUrl() : null);
		shipping.setDeliveryCorpCode(deliveryCorp != null ? deliveryCorp.getCode() : null);
		Area area = areaService.find(areaId);
		shipping.setArea(area != null ? area.getFullName() : null);
		if (order.isExpired() || order.getOrderStatus() != OrderStatus.confirmed.ordinal()) {
			renderJson(ERROR_VIEW);
			return;
		}
		if (order.getShippingStatus() != ShippingStatus.unshipped.ordinal() && order.getShippingStatus() != ShippingStatus.partialShipment.ordinal()) {
			renderJson(ERROR_VIEW);
			return;
		}
		Admin admin = ShiroUtil.getAdmin();
		if (order.isLocked(admin)) {
			renderJson(ERROR_VIEW);
			return;
		}
		shipping.setSn(snService.generate(Sn.Type.shipping));
		shipping.setOperator(admin.getUsername());
		orderService.shipping(order, shipping, admin);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("view?id=" + orderId);
	}

	/**
	 * 退货
	 */
	public void returns() {
		Long orderId = getParaToLong("orderId");
		Long shippingMethodId = getParaToLong("shippingMethodId");
		Long deliveryCorpId = getParaToLong("deliveryCorpId");
		Long areaId = getParaToLong("areaId");
		Returns returns = getModel(Returns.class);
		List<ReturnsItem> returnsItems = getModels(ReturnsItem.class);
		returns.setReturnsItems(returnsItems);
		Order order = orderService.find(orderId);
		if (order == null) {
			renderJson(ERROR_VIEW);
			return;
		}
		for (Iterator<ReturnsItem> iterator = returns.getReturnsItems().iterator(); iterator.hasNext();) {
			ReturnsItem returnsItem = iterator.next();
			if (returnsItem == null || StringUtils.isEmpty(returnsItem.getSn()) || returnsItem.getQuantity() == null || returnsItem.getQuantity() <= 0) {
				iterator.remove();
				continue;
			}
			OrderItem orderItem = order.getOrderItem(returnsItem.getSn());
			if (orderItem == null || returnsItem.getQuantity() > orderItem.getShippedQuantity() - orderItem.getReturnQuantity()) {
				renderJson(ERROR_VIEW);
				return;
			}
			returnsItem.setName(orderItem.getFullName());
			returnsItem.setReturnsId(returns.getId());
		}
		returns.setOrderId(order.getId());
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		returns.setShippingMethod(shippingMethod != null ? shippingMethod.getName() : null);
		DeliveryCorp deliveryCorp = deliveryCorpService.find(deliveryCorpId);
		returns.setDeliveryCorp(deliveryCorp != null ? deliveryCorp.getName() : null);
		Area area = areaService.find(areaId);
		returns.setArea(area != null ? area.getFullName() : null);
		if (order.isExpired() || order.getOrderStatus() != OrderStatus.confirmed.ordinal()) {
			renderJson(ERROR_VIEW);
			return;
		}
		if (order.getShippingStatus() != ShippingStatus.shipped.ordinal() && order.getShippingStatus() != ShippingStatus.partialShipment.ordinal() && order.getShippingStatus() != ShippingStatus.partialReturns.ordinal()) {
			renderJson(ERROR_VIEW);
			return;
		}
		Admin admin = ShiroUtil.getAdmin();
		if (order.isLocked(admin)) {
			renderJson(ERROR_VIEW);
			return;
		}
		returns.setSn(snService.generate(Sn.Type.returns));
		returns.setOperator(admin.getUsername());
		orderService.returns(order, returns, admin);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("view?id=" + orderId);
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("paymentMethods", paymentMethodService.findAll());
		setAttr("shippingMethods", shippingMethodService.findAll());
		setAttr("order", orderService.find(id));
		render("/admin/order/edit.html");
	}

	/**
	 * 订单项添加
	 */
	public void orderItemAdd() {
		String productSn = getPara("productSn");
		Map<String, Object> data = new HashMap<String, Object>();
		Product product = productService.findBySn(productSn);
		if (product == null) {
			data.put("message", Message.warn("admin.order.productNotExist"));
			renderJson(data);
			return;
		}
		if (!product.getIsMarketable()) {
			data.put("message", Message.warn("admin.order.productNotMarketable"));
			renderJson(data);
			return;
		}
		if (product.getIsOutOfStock()) {
			data.put("message", Message.warn("admin.order.productOutOfStock"));
			renderJson(data);
			return;
		}
		data.put("sn", product.getSn());
		data.put("fullName", product.getFullName());
		data.put("price", product.getPrice());
		data.put("weight", product.getWeight());
		data.put("isGift", product.getIsGift());
		data.put("message", SUCCESS_MESSAGE);
		renderJson(data);
	}

	/**
	 * 计算
	 */
	public void calculate() {
		Order order = getModel(Order.class);
		Long areaId = getParaToLong("areaId");
		Long paymentMethodId = getParaToLong("paymentMethodId");
		Long shippingMethodId = getParaToLong("shippingMethodId");
		Map<String, Object> data = new HashMap<String, Object>();
		for (Iterator<OrderItem> iterator = order.getOrderItems().iterator(); iterator.hasNext();) {
			OrderItem orderItem = iterator.next();
			if (orderItem == null || StringUtils.isEmpty(orderItem.getSn())) {
				iterator.remove();
			}
		}
		order.setAreaId(areaId);
		order.setPaymentMethodId(paymentMethodId);
		order.setShippingMethodId(shippingMethodId);
		Order pOrder = orderService.find(order.getId());
		if (pOrder == null) {
			data.put("message", Message.error("admin.common.invalid"));
			renderJson(data);
			return;
		}
		for (OrderItem orderItem : order.getOrderItems()) {
			if (orderItem.getId() != null) {
				OrderItem pOrderItem = orderItemService.find(orderItem.getId());
				if (pOrderItem == null || !pOrder.equals(pOrderItem.getOrder())) {
					data.put("message", Message.error("admin.common.invalid"));
					renderJson(data);
					return;
				}
				Product product = pOrderItem.getProduct();
				if (product != null && product.getStock() != null) {
					if (pOrder.getIsAllocatedStock()) {
						if (orderItem.getQuantity() > product.getAvailableStock() + pOrderItem.getQuantity()) {
							data.put("message", Message.warn("admin.order.lowStock"));
							renderJson(data);
							return;
						}
					} else {
						if (orderItem.getQuantity() > product.getAvailableStock()) {
							data.put("message", Message.warn("admin.order.lowStock"));
							renderJson(data);
							return;
						}
					}
				}
			} else {
				Product product = productService.findBySn(orderItem.getSn());
				if (product == null) {
					data.put("message", Message.error("admin.common.invalid"));
					renderJson(data);
					return;
				}
				if (product.getStock() != null && orderItem.getQuantity() > product.getAvailableStock()) {
					data.put("message", Message.warn("admin.order.lowStock"));
					renderJson(data);
					return;
				}
			}
		}
		Map<String, Object> orderItems = new HashMap<String, Object>();
		for (OrderItem orderItem : order.getOrderItems()) {
			orderItems.put(orderItem.getSn(), orderItem);
		}
		order.setFee(pOrder.getFee());
		order.setPromotionDiscount(pOrder.getPromotionDiscount());
		order.setCouponDiscount(pOrder.getCouponDiscount());
		order.setAmountPaid(pOrder.getAmountPaid());
		data.put("weight", order.getWeight());
		data.put("price", order.getPrice());
		data.put("quantity", order.getQuantity());
		data.put("amount", order.getAmount());
		data.put("orderItems", orderItems);
		data.put("message", SUCCESS_MESSAGE);
		renderJson(data);
	}

	/**
	 * 更新
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void update() throws IllegalAccessException, InvocationTargetException {
		Order order = getModel(Order.class);
		Long areaId = getParaToLong("areaId");
		Long paymentMethodId = getParaToLong("paymentMethodId");
		Long shippingMethodId = getParaToLong("paymentMethodId");
		for (Iterator<OrderItem> iterator = order.getOrderItems().iterator(); iterator.hasNext();) {
			OrderItem orderItem = iterator.next();
			if (orderItem == null || StringUtils.isEmpty(orderItem.getSn())) {
				iterator.remove();
			}
		}
		order.setAreaId(areaId);
		order.setPaymentMethodId(paymentMethodId);
		order.setShippingMethodId(shippingMethodId);
		Order pOrder = orderService.find(order.getId());
		if (pOrder == null) {
			renderJson(ERROR_VIEW);
			return;
		}
		if (pOrder.isExpired() || pOrder.getOrderStatus() != OrderStatus.unconfirmed.ordinal()) {
			renderJson(ERROR_VIEW);
			return;
		}
		Admin admin = ShiroUtil.getAdmin();
		if (pOrder.isLocked(admin)) {
			renderJson(ERROR_VIEW);
			return;
		}
		if (!order.getIsInvoice()) {
			order.setInvoiceTitle(null);
			order.setTax(new BigDecimal(0));
		}
		for (OrderItem orderItem : order.getOrderItems()) {
			if (orderItem.getId() != null) {
				OrderItem pOrderItem = orderItemService.find(orderItem.getId());
				if (pOrderItem == null || !pOrder.equals(pOrderItem.getOrder())) {
					renderJson(ERROR_VIEW);
					return;
				}
				Product product = pOrderItem.getProduct();
				if (product != null && product.getStock() != null) {
					if (pOrder.getIsAllocatedStock()) {
						if (orderItem.getQuantity() > product.getAvailableStock() + pOrderItem.getQuantity()) {
							renderJson(ERROR_VIEW);
							return;
						}
					} else {
						if (orderItem.getQuantity() > product.getAvailableStock()) {
							renderJson(ERROR_VIEW);
							return;
						}
					}
				}
				BigDecimal price = pOrderItem.getPrice();
				Integer quantity = pOrderItem.getQuantity();
				//BeanUtils.copyProperties(pOrderItem, orderItem, new String[] { "price", "quantity" });
				//BeanUtils.copyProperties(pOrderItem, orderItem);
				orderItem._setAttrs(pOrderItem);
				orderItem.remove("price");
				orderItem.remove("quantity");
				
				pOrderItem.setPrice(price);
				pOrderItem.setQuantity(quantity);
				
				if (pOrderItem.getIsGift()) {
					orderItem.setPrice(new BigDecimal(0));
				}
			} else {
				Product product = productService.findBySn(orderItem.getSn());
				if (product == null) {
					renderJson(ERROR_VIEW);
					return;
				}
				if (product.getStock() != null && orderItem.getQuantity() > product.getAvailableStock()) {
					renderJson(ERROR_VIEW);
					return;
				}
				orderItem.setName(product.getName());
				orderItem.setFullName(product.getFullName());
				if (product.getIsGift()) {
					orderItem.setPrice(new BigDecimal(0));
				}
				orderItem.setWeight(product.getWeight());
				orderItem.setThumbnail(product.getThumbnail());
				orderItem.setIsGift(product.getIsGift());
				orderItem.setShippedQuantity(0);
				orderItem.setReturnQuantity(0);
				orderItem.setProductId(product.getId());
				orderItem.setOrderId(pOrder.getId());
			}
		}
		order.setSn(pOrder.getSn());
		order.setOrderStatus(pOrder.getOrderStatus());
		order.setPaymentStatus(pOrder.getPaymentStatus());
		order.setShippingStatus(pOrder.getShippingStatus());
		order.setFee(pOrder.getFee());
		order.setPromotionDiscount(pOrder.getPromotionDiscount());
		order.setCouponDiscount(pOrder.getCouponDiscount());
		order.setAmountPaid(pOrder.getAmountPaid());
		order.setPromotion(pOrder.getPromotion());
		order.setExpire(pOrder.getExpire());
		order.setLockExpire(null);
		order.setIsAllocatedStock(pOrder.getIsAllocatedStock());
		order.setMemberId(pOrder.getMember().getId());
		order.setCouponCode(pOrder.getCouponCode());
//		order.setCoupons(pOrder.getCoupons());
//		order.setOrderLogs(pOrder.getOrderLogs());
//		order.setDeposits(pOrder.getDeposits());
//		order.setPayments(pOrder.getPayments());
//		order.setRefunds(pOrder.getRefunds());
//		order.setShippings(pOrder.getShippings());
//		order.setReturns(pOrder.getReturns());

		orderService.update(order, admin);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		OrderStatus orderStatus = StrKit.notBlank(getPara("orderStatus")) ? OrderStatus.valueOf(getPara("orderStatus")) : null;
		PaymentStatus paymentStatus = StrKit.notBlank(getPara("paymentStatus")) ? PaymentStatus.valueOf(getPara("paymentStatus")) : null;
		ShippingStatus shippingStatus = StrKit.notBlank(getPara("shippingStatus")) ? ShippingStatus.valueOf(getPara("shippingStatus")) : null;
		Boolean hasExpired = StrKit.notBlank(getPara("shippingStatus")) ? getParaToBoolean("hasExpired") : null;
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("orderStatus", orderStatus);
		setAttr("paymentStatus", paymentStatus);
		setAttr("shippingStatus", shippingStatus);
		setAttr("hasExpired", hasExpired);
		setAttr("page", orderService.findPage(orderStatus, paymentStatus, shippingStatus, hasExpired, pageable));
		render("/admin/order/list.html");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			Admin admin = ShiroUtil.getAdmin();
			for (Long id : ids) {
				Order order = orderService.find(id);
				if (order != null && order.isLocked(admin)) {
					renderJson(Message.error("admin.order.deleteLockedNotAllowed", order.getSn()));
					return;
				}
			}
			orderService.delete(ids);
		}
		renderJson(SUCCESS_MESSAGE);
	}

}