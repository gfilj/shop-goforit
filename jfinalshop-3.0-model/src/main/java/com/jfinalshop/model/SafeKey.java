package com.jfinalshop.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Entity - 安全密钥
 * 
 * 
 */
public class SafeKey implements Serializable {

	private static final long serialVersionUID = -8536541568286987548L;

	/** 密钥 */
	private String value;

	/** 到期时间 */
	private Date expire;

	/**
	 * 获取密钥
	 * 
	 * @return 密钥
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 设置密钥
	 * 
	 * @param value
	 *            密钥
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 获取到期时间
	 * 
	 * @return 到期时间
	 */
	public Date getExpire() {
		return expire;
	}

	/**
	 * 设置到期时间
	 * 
	 * @param expire
	 *            到期时间
	 */
	public void setExpire(Date expire) {
		this.expire = expire;
	}

	/**
	 * 判断是否已过期
	 * 
	 * @return 是否已过期
	 */
	public boolean hasExpired() {
		return getExpire() != null && new Date().after(getExpire());
	}

}