package com.jfinalshop.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseParameter;

/**
 * Dao - 参数
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Parameter extends BaseParameter<Parameter> {
	public static final Parameter dao = new Parameter();
	
	
	/**
	 * 查找参数
	 * 
	 * @param parameterGroup
	 *            参数组
	 * @param excludes
	 *            排除参数
	 * @return 参数
	 */
	public List<Parameter> findList(ParameterGroup parameterGroup, List<Parameter> excludes) {
		String sql = "SELECT * FROM `parameter` p WHERE 1 = 1 ";
		if (parameterGroup != null) {
			sql += " AND p.`parameter_group_id` = " + parameterGroup.getId();
		}
		if (excludes != null && !excludes.isEmpty()) {
			sql += " AND p.id NOT IN( ";
			StringBuffer sb = new StringBuffer();
			int maxSize = excludes.size() - 1;
            for (int i = 0; i < excludes.size(); i++) {
            	 sb.append(excludes.get(i).getId());
            	 sb.append(i == maxSize ? excludes.get(maxSize) + ")" : excludes.get(i) + ",");
            }
			sql += sb.toString();
		}
		return find(sql);
	}
	

	/**
	 * 根据parameterGroupId删除参数
	 * @param adminId
	 * @return
	 */
	public boolean delete(Long parameterGroupId) {
		return Db.deleteById("parameter", "parameter_group_id", parameterGroupId);
	}
}
