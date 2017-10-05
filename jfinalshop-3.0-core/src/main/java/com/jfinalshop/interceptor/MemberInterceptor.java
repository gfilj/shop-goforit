package com.jfinalshop.interceptor;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinalshop.common.Principal;
import com.jfinalshop.common.RequestHolder;
import com.jfinalshop.service.MemberService;

/**
 * Interceptor - 会员权限
 * 
 * 
 * 
 */
public class MemberInterceptor implements Interceptor {

	/** 重定向视图名称前缀 */
	//private static final String REDIRECT_VIEW_NAME_PREFIX = "redirect:";

	/** "重定向URL"参数名称 */
	private static final String REDIRECT_URL_PARAMETER_NAME = "redirectUrl";

	/** "会员"属性名称 */
	private static final String MEMBER_ATTRIBUTE_NAME = "member";

	/** 默认登录URL */
	private static final String DEFAULT_LOGIN_URL = "/login";

	/** 登录URL */
	private String loginUrl = DEFAULT_LOGIN_URL;
	private MemberService memberService = new MemberService();
	
	Prop prop = PropKit.use("application.properties");
	private String urlEscapingCharset = prop.get("url_escaping_charset");

	@Override
	public void intercept(Invocation inv) {
		Controller controller = inv.getController();
		/** 将request对象绑定当前线程*/
		RequestHolder.setRequestAttributes(controller.getRequest());
		Principal principal = (Principal) controller.getSessionAttr(MemberService.PRINCIPAL_ATTRIBUTE_NAME);
		if (principal != null) {
			inv.invoke();
		} else {
			String requestType = controller.getRequest().getHeader("X-Requested-With");
			if (requestType != null && requestType.equalsIgnoreCase("XMLHttpRequest")) {
				controller.getResponse().addHeader("loginStatus", "accessDenied");
				try {
					controller.getResponse().sendError(HttpServletResponse.SC_FORBIDDEN);
				} catch (IOException e) {
					e.printStackTrace();
				}
				controller.renderJson(false);
				return;
			} else {
				try {
				if (controller.getRequest().getMethod().equalsIgnoreCase("GET")) {
					String redirectUrl = controller.getRequest().getQueryString() != null ? controller.getRequest().getRequestURI() + "?" + controller.getRequest().getQueryString() : controller.getRequest().getRequestURI();
					controller.getResponse().sendRedirect(controller.getRequest().getContextPath() + loginUrl + "?" + REDIRECT_URL_PARAMETER_NAME + "=" + URLEncoder.encode(redirectUrl, urlEscapingCharset));
				} else {
					controller.getResponse().sendRedirect(controller.getRequest().getContextPath() + loginUrl);
				}
				} catch (IOException e) {
					e.printStackTrace();
				}
				controller.renderJson(false);
				return;
			}
		}
		controller.setAttr(MEMBER_ATTRIBUTE_NAME, memberService.getCurrent(controller.getRequest()));
	}

	/**
	 * 获取登录URL
	 * 
	 * @return 登录URL
	 */
	public String getLoginUrl() {
		return loginUrl;
	}

	/**
	 * 设置登录URL
	 * 
	 * @param loginUrl
	 *            登录URL
	 */
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

}
