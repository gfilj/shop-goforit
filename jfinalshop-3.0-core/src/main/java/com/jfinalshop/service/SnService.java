package com.jfinalshop.service;

import com.jfinalshop.model.Sn;
import com.jfinalshop.model.Sn.Type;

/**
 * Service - 序列号
 * 
 * 
 * 
 */
public class SnService extends BaseService<Sn> {
	
	public SnService() {
		super(Sn.class);
	}

	/**
	 * 生成序列号
	 * 
	 * @param type
	 *            类型
	 * @return 序列号
	 */
	public String generate(Type type) {
		return Sn.dao.generate(type);
	}
}
