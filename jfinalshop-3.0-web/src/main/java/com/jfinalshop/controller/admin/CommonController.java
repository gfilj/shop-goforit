package com.jfinalshop.controller.admin;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.render.FreeMarkerRender;
import com.jfinalshop.model.Area;
import com.jfinalshop.service.AreaService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.MessageService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.ProductService;

import freemarker.template.Configuration;

/**
 * Controller - 共用
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/common")
public class CommonController extends BaseAdminController {

	/** servletContext */
	private ServletContext servletContext;
	
	private AreaService areaService = enhance(AreaService.class);
	private OrderService orderService = enhance(OrderService.class);
	private ProductService productService = enhance(ProductService.class);
	private MemberService memberService = enhance(MemberService.class);
	private MessageService messageService = enhance(MessageService.class);
	
	/**
	 * 主页
	 */
	public void main() {
		render("/admin/common/main.html");
	}
	
	/**
	 * 首页
	 */
	public void index() {
		Configuration cf = FreeMarkerRender.getConfiguration();
		servletContext = getSession().getServletContext();
		setAttr("systemName", cf.getSharedVariable("systemName"));
		setAttr("systemVersion", cf.getSharedVariable("systemVersion"));
		setAttr("systemDescription", cf.getSharedVariable("systemDescription"));
		setAttr("systemShowPowered", cf.getSharedVariable("systemShowPowered"));
		setAttr("javaVersion", System.getProperty("java.version"));
		setAttr("javaHome", System.getProperty("java.home"));
		setAttr("osName", System.getProperty("os.name"));
		setAttr("osArch", System.getProperty("os.arch"));
		setAttr("serverInfo", servletContext.getServerInfo());
		setAttr("servletVersion", servletContext.getMajorVersion() + "." + servletContext.getMinorVersion());
		setAttr("waitingPaymentOrderCount", orderService.waitingPaymentCount(null));
		setAttr("waitingShippingOrderCount", orderService.waitingShippingCount(null));
		setAttr("marketableProductCount", productService.count(null, true, null, null, false, null, null));
		setAttr("notMarketableProductCount", productService.count(null, false, null, null, false, null, null));
		setAttr("stockAlertProductCount", productService.count(null, null, null, null, false, null, true));
		setAttr("outOfStockProductCount", productService.count(null, null, null, null, false, true, null));
		setAttr("memberCount", memberService.count());
		setAttr("unreadMessageCount", messageService.count(null, false));
		render("/admin/common/index.html");
	}
	
	/**
	 * 错误视图
	 */
	public void error() {
		render("/admin/common/error.html");
	}
	
	/**
	 * 权限错误
	 */
	public void unauthorized() {
		String requestType = getRequest().getHeader("X-Requested-With");
		if (requestType != null && requestType.equalsIgnoreCase("XMLHttpRequest")) {
			getResponse().addHeader("loginStatus", "unauthorized");
			try {
				getResponse().sendError(HttpServletResponse.SC_FORBIDDEN);
			} catch (IOException e) {
				e.printStackTrace();
			}
			renderNull();
		}
		render("/admin/common/unauthorized.html");
	}
	
	/**
	 * 地区
	 */
	public void area() {
		Long parentId = getParaToLong("parentId");
		List<Area> areas = new ArrayList<Area>();
		Area parent = areaService.find(parentId);
		if (parent != null) {
			areas = new ArrayList<Area>(parent.getChildren());
		} else {
			areas = areaService.findRoots();
		}
		Map<Long, String> options = new HashMap<Long, String>();
		for (Area area : areas) {
			options.put(area.getId(), area.getName());
		}
		renderJson(options);
	}
	
}
