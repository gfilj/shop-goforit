package com.jfinalshop.plugin.shiro.core;

import java.util.Map;

import com.jfinalshop.plugin.shiro.core.handler.AuthzHandler;

/**
 * Created by wangrenhui on 14-1-7.
 */
public interface JdbcAuthzService {
	public Map<String, AuthzHandler> getJdbcAuthz();
}
