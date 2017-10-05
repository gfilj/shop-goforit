package com.jfinalshop.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.jfinalshop.common.RequestHolder;
import com.jfinalshop.plugin.shiro.core.SubjectKit;

public class AuthInterceptor implements Interceptor {
	private final static Logger logger = Logger.getLogger(AuthInterceptor.class);
	
	public void intercept(Invocation ai) {
		Controller controller = ai.getController();
		controller.setAttr("base", controller.getRequest().getContextPath());
		/** 将request对象绑定当前线程*/
		RequestHolder.setRequestAttributes(controller.getRequest());
		logger.info("IP : " + controller.getRequest().getRemoteHost());
		if (SubjectKit.isAuthed()) {
			ai.invoke();
		} else {
			controller.redirect("/admin/login");
		}
	}

}
