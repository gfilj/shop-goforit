package com.jfinalshop.plugin.shiro;

/**
 * Created by wangrenhui on 14-1-3.
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.apache.shiro.web.util.WebUtils;

/**
 * Superclass for any filter that controls access to a resource and may redirect
 * the user to the login page if they are not authenticated. This superclass
 * provides the method
 * {@link #saveRequestAndRedirectToLogin(javax.servlet.ServletRequest, javax.servlet.ServletResponse)}
 * which is used by many subclasses as the behavior when a user is
 * unauthenticated.
 *
 * @since 0.9
 */
public abstract class ShiroAccessControlFilter extends PathMatchingFilter {
	/**
	 * Simple default login URL equal to <code>/login.jsp</code>, which can be
	 * overridden by calling the {@link #setLoginUrl(String) setLoginUrl}
	 * method.
	 */
	public static final String DEFAULT_LOGIN_URL = "/login";

	/**
	 * Constant representing the HTTP 'GET' request method, equal to
	 * <code>GET</code>.
	 */
	public static final String GET_METHOD = "GET";

	/**
	 * Constant representing the HTTP 'POST' request method, equal to
	 * <code>POST</code>.
	 */
	public static final String POST_METHOD = "POST";

	/**
	 * The login url to used to authenticate a user, used when redirecting users
	 * if authentication is required.
	 */
	private String failureUrl = DEFAULT_LOGIN_URL;

	public String getFailureUrl() {
		return failureUrl;
	}

	public void setFailureUrl(String failureUrl) {
		this.failureUrl = failureUrl;
	}

	/**
	 * more login
	 */
	private Map<String, String> failureUrlMap;

	public Map<String, String> getFailureUrlMap() {
		return failureUrlMap;
	}

	public void setFailureUrlMap(Map<String, String> failureUrlMap) {
		this.failureUrlMap = failureUrlMap;
	}

	private String loginUrl = DEFAULT_LOGIN_URL;

	/**
	 * Returns the login URL used to authenticate a user.
	 * 
	 * Most Shiro filters use this url as the location to redirect a user when
	 * the filter requires authentication. Unless overridden, the
	 * {@link #DEFAULT_LOGIN_URL DEFAULT_LOGIN_URL} is assumed, which can be
	 * overridden via {@link #setLoginUrl(String) setLoginUrl}.
	 *
	 * @return the login URL used to authenticate a user, used when redirecting
	 *         users if authentication is required.
	 */
	public String getLoginUrl() {
		return loginUrl;
	}

	/**
	 * Sets the login URL used to authenticate a user.
	 * 
	 * Most Shiro filters use this url as the location to redirect a user when
	 * the filter requires authentication. Unless overridden, the
	 * {@link #DEFAULT_LOGIN_URL DEFAULT_LOGIN_URL} is assumed.
	 *
	 * @param loginUrl
	 *            the login URL used to authenticate a user, used when
	 *            redirecting users if authentication is required.
	 */
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	private Map<String, String> loginUrlMap;

	public Map<String, String> getLoginUrlMap() {
		return loginUrlMap;
	}

	public void setLoginUrlMap(Map<String, String> loginUrlMap) {
		this.loginUrlMap = loginUrlMap;
	}

	/**
	 * Convenience method that acquires the Subject associated with the request.
	 * 
	 * The default implementation simply returns
	 * {@link org.apache.shiro.SecurityUtils#getSubject()
	 * SecurityUtils.getSubject()}.
	 *
	 * @param request
	 *            the incoming <code>ServletRequest</code>
	 * @param response
	 *            the outgoing <code>ServletResponse</code>
	 * @return the Subject associated with the request.
	 */
	protected Subject getSubject(ServletRequest request, ServletResponse response) {
		return SecurityUtils.getSubject();
	}

	/**
	 * Returns <code>true</code> if the request is allowed to proceed through
	 * the filter normally, or <code>false</code> if the request should be
	 * handled by the
	 * {@link #onAccessDenied(javax.servlet.ServletRequest, javax.servlet.ServletResponse, Object)
	 * onAccessDenied(request,response,mappedValue)} method instead.
	 *
	 * @param request
	 *            the incoming <code>ServletRequest</code>
	 * @param response
	 *            the outgoing <code>ServletResponse</code>
	 * @param mappedValue
	 *            the filter-specific config value mapped to this filter in the
	 *            URL rules mappings.
	 * @return <code>true</code> if the request should proceed through the
	 *         filter normally, <code>false</code> if the request should be
	 *         processed by this filter's
	 *         {@link #onAccessDenied(javax.servlet.ServletRequest, javax.servlet.ServletResponse, Object)}
	 *         method instead.
	 * @throws Exception
	 *             if an error occurs during processing.
	 */
	protected abstract boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception;

	/**
	 * Processes requests where the subject was denied access as determined by
	 * the
	 * {@link #isAccessAllowed(javax.servlet.ServletRequest, javax.servlet.ServletResponse, Object)
	 * isAccessAllowed} method, retaining the {@code mappedValue} that was used
	 * during configuration.
	 * 
	 * This method immediately delegates to
	 * {@link #onAccessDenied(javax.servlet.ServletRequest, javax.servlet.ServletResponse)}
	 * as a convenience in that most post-denial behavior does not need the
	 * mapped config again.
	 *
	 * @param request
	 *            the incoming <code>ServletRequest</code>
	 * @param response
	 *            the outgoing <code>ServletResponse</code>
	 * @param mappedValue
	 *            the config specified for the filter in the matching request's
	 *            filter chain.
	 * @return <code>true</code> if the request should continue to be processed;
	 *         false if the subclass will handle/render the response directly.
	 * @throws Exception
	 *             if there is an error processing the request.
	 * @since 1.0
	 */
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		return onAccessDenied(request, response);
	}

	/**
	 * Processes requests where the subject was denied access as determined by
	 * the
	 * {@link #isAccessAllowed(javax.servlet.ServletRequest, javax.servlet.ServletResponse, Object)
	 * isAccessAllowed} method.
	 *
	 * @param request
	 *            the incoming <code>ServletRequest</code>
	 * @param response
	 *            the outgoing <code>ServletResponse</code>
	 * @return <code>true</code> if the request should continue to be processed;
	 *         false if the subclass will handle/render the response directly.
	 * @throws Exception
	 *             if there is an error processing the request.
	 */
	protected abstract boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception;

	/**
	 * Returns <code>true</code> if
	 * {@link #isAccessAllowed(javax.servlet.ServletRequest, javax.servlet.ServletResponse, Object)
	 * isAccessAllowed(Request,Response,Object)}, otherwise returns the result
	 * of
	 * {@link #onAccessDenied(javax.servlet.ServletRequest, javax.servlet.ServletResponse, Object)
	 * onAccessDenied(Request,Response,Object)}.
	 *
	 * @return <code>true</code> if
	 *         {@link #isAccessAllowed(javax.servlet.ServletRequest, javax.servlet.ServletResponse, Object)
	 *         isAccessAllowed}, otherwise returns the result of
	 *         {@link #onAccessDenied(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 *         onAccessDenied}.
	 * @throws Exception
	 *             if an error occurs.
	 */
	public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		return isAccessAllowed(request, response, mappedValue) || onAccessDenied(request, response, mappedValue);
	}

	/**
	 * Returns <code>true</code> if the incoming request is a login request,
	 * <code>false</code> otherwise.
	 * 
	 * The default implementation merely returns <code>true</code> if the
	 * incoming request matches the configured {@link #getLoginUrl() loginUrl}
	 * by calling
	 * <code>{@link #pathsMatch(String, String) pathsMatch(loginUrl, request)}</code>
	 * .
	 *
	 * @param request
	 *            the incoming <code>ServletRequest</code>
	 * @param response
	 *            the outgoing <code>ServletResponse</code>
	 * @return <code>true</code> if the incoming request is a login request,
	 *         <code>false</code> otherwise.
	 */
	protected boolean isLoginRequest(ServletRequest request, ServletResponse response) {
		boolean match = false;
		// 多点登录
		if (loginUrlMap != null) {
			for (String key : loginUrlMap.keySet()) {
				match = pathsMatch(loginUrlMap.get(key), request);
				// 如果匹配到登录路径
				if (match) {
					break;
				}
			}
		} else {
			match = pathsMatch(getLoginUrl(), request);
		}
		return match;
	}

	/**
	 * Convenience method for subclasses to use when a login redirect is
	 * required.
	 * 
	 * This implementation simply calls
	 * {@link #saveRequest(javax.servlet.ServletRequest) saveRequest(request)}
	 * and then
	 * {@link #redirectToLogin(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 * redirectToLogin(request,response)}.
	 *
	 * @param request
	 *            the incoming <code>ServletRequest</code>
	 * @param response
	 *            the outgoing <code>ServletResponse</code>
	 * @throws java.io.IOException
	 *             if an error occurs.
	 */
	protected void saveRequestAndRedirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
		saveRequest(request);
		redirectToLogin(request, response);
	}

	/**
	 * Convenience method merely delegates to
	 * {@link org.apache.shiro.web.util.WebUtils#saveRequest(javax.servlet.ServletRequest)
	 * WebUtils.saveRequest(request)} to save the request state for reuse later.
	 * This is mostly used to retain user request state when a redirect is
	 * issued to return the user to their originally requested url/resource.
	 * 
	 * If you need to save and then immediately redirect the user to login,
	 * consider using
	 * {@link #saveRequestAndRedirectToLogin(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 * saveRequestAndRedirectToLogin(request,response)} directly.
	 *
	 * @param request
	 *            the incoming ServletRequest to save for re-use later (for
	 *            example, after a redirect).
	 */
	protected void saveRequest(ServletRequest request) {
		WebUtils.saveRequest(request);
	}

	/**
	 * Convenience method for subclasses that merely acquires the
	 * {@link #getLoginUrl() getLoginUrl} and redirects the request to that url.
	 * 
	 * <b>N.B.</b> If you want to issue a redirect with the intention of
	 * allowing the user to then return to their originally requested URL, don't
	 * use this method directly. Instead you should call
	 * {@link #saveRequestAndRedirectToLogin(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 * saveRequestAndRedirectToLogin(request,response)}, which will save the
	 * current request state so that it can be reconstructed and re-used after a
	 * successful login.
	 *
	 * @param request
	 *            the incoming <code>ServletRequest</code>
	 * @param response
	 *            the outgoing <code>ServletResponse</code>
	 * @throws java.io.IOException
	 *             if an error occurs.
	 */
	protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
		String loginUrl = getRealLoginUrl(request);

		WebUtils.issueRedirect(request, response, loginUrl);
	}

	/**
	 * 获取真实的登录url
	 *
	 * @param request
	 *            the incoming <code>ServletRequest</code>
	 * @return url
	 */
	public String getRealLoginUrl(ServletRequest request) {
		String loginUrl = "";
		// 多点登录
		if (loginUrlMap != null) {
			Object config = null;
			for (String path : this.appliedPaths.keySet()) {
				// If the path does match, then pass on to the subclass
				// implementation for specific checks
				// (first match 'wins'):
				if (pathsMatch(path, request)) {
					config = this.appliedPaths.get(path);
					break;
				}
			}
			if (config != null) {
				String[] configArr = (String[]) config;

				for (String key : loginUrlMap.keySet()) {
					// 得到权限key数组
					if (ArrayUtils.contains(configArr, key)) {
						loginUrl = loginUrlMap.get(key);
						break;
					}

				}
			}
		}
		if (loginUrl.isEmpty())
			loginUrl = getLoginUrl();
		return loginUrl;
	}

	/**
	 * 获取真实退出url
	 *
	 * @param request
	 *            the incoming <code>ServletRequest</code>
	 * @return url
	 */
	public String getRealFailureUrl(ServletRequest request) {
		String failureUrl = "";
		// 多点登录
		if (failureUrlMap != null && loginUrlMap != null) {
			String roleKey = null;
			for (String path : loginUrlMap.values()) {
				// If the path does match, then pass on to the subclass
				// implementation for specific checks
				// (first match 'wins'):
				if (pathsMatch(path, request)) {
					roleKey = loginUrlMap.get(path);
					break;
				}
			}
			if (roleKey != null) {
				failureUrl = failureUrlMap.get(roleKey);
			}
		}

		if (failureUrl.isEmpty())
			failureUrl = getFailureUrl();
		return failureUrl;
	}
}
