package com.jfinalshop.service;

import com.jfinalshop.model.Log;

/**
 * Service - 日志
 * 
 * 
 * 
 */
public class LogService extends BaseService<Log> {

	public LogService() {
		super(Log.class);
	}
	
	public void clear() {
		Log.dao.removeAll();
	}
}
