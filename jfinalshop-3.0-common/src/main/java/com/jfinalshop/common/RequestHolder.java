package com.jfinalshop.common;

import javax.servlet.http.HttpServletRequest;

/**
 * 自定义标签中获取request对象
 * 
 *
 */
public class RequestHolder {

	private static final ThreadLocal<HttpServletRequest> requestAttributesHolder = new ThreadLocal<HttpServletRequest>();

	/**
	 * 重新设置当前线程
	 */
	public static void resetRequestAttributes() {
		requestAttributesHolder.remove();
	}

	/**
	 * Bind the given RequestAttributes to the current thread,
	 * <i>not</i> exposing it as inheritable for child threads.
	 * @param attributes the RequestAttributes to expose
	 * @see #setRequestAttributes(RequestAttributes, boolean)
	 */
	public static void setRequestAttributes(HttpServletRequest attributes) {
		setRequestAttributes(attributes, false);
	}

	/**
	 * Bind the given RequestAttributes to the current thread.
	 * @param attributes the RequestAttributes to expose,
	 * or {@code null} to reset the thread-bound context
	 * @param inheritable whether to expose the RequestAttributes as inheritable
	 * for child threads (using an {@link InheritableThreadLocal})
	 */
	public static void setRequestAttributes(HttpServletRequest attributes, boolean inheritable) {
		if (attributes == null) {
			resetRequestAttributes();
		}
		else {
			if (inheritable) {
				requestAttributesHolder.remove();
			}
			else {
				requestAttributesHolder.set(attributes);
			}
		}
	}

	/**
	 * Return the RequestAttributes currently bound to the thread.
	 * @return the RequestAttributes currently bound to the thread,
	 * or {@code null} if none bound
	 */
	public static HttpServletRequest getRequestAttributes() {
		HttpServletRequest attributes = requestAttributesHolder.get();
		return attributes;
	}

}
