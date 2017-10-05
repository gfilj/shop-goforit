package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseLog;

/**
 * Dao - 日志
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Log extends BaseLog<Log> {
	public static final Log dao = new Log();
	
	/** "日志内容"属性名称 */
	public static final String LOG_CONTENT_ATTRIBUTE_NAME = Log.class.getName() + ".CONTENT";
	
	/**
	 * 删除所有日志
	 */
	public void removeAll() {
		String sql = "delete from Log";
		Db.update(sql);
	}
}
