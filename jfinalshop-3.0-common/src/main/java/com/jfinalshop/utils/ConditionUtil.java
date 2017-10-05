package com.jfinalshop.utils;

import java.util.List;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.kit.StrKit;
import com.jfinalshop.common.Filter;
import com.jfinalshop.common.Filter.Operator;
import com.jfinalshop.common.Order;
import com.jfinalshop.common.Order.Direction;

public class ConditionUtil {

	/**
	 * 用于生成SQL查询语句
	 * @param first
	 * @param count
	 * @param filters
	 * @param orders
	 * @return
	 */
	public static String buildSQL(Integer first, Integer count, List<Filter> filters, List<Order> orders) {
		StringBuilder sb = new StringBuilder();
		if (filters != null && 0 < filters.size()) {
			for (Filter filter : filters) {
				if (filter == null || StringUtils.isEmpty(filter.getProperty())) {
					continue;
				}
				String fieldName = propertyToField(filter.getProperty());
				Object fieldValue = filter.getValue();
				Operator queryOperator= filter.getOperator();
				// 非空的时候进行设置
		        if (StrKit.notNull(fieldValue) && StrKit.notNull(fieldName)) {
		        	if (Operator.eq == queryOperator) {
		        		if (filter.getIgnoreCase() != null && filter.getIgnoreCase() && fieldValue instanceof String) {
		        			 sb.append(" AND `" + fieldName.toLowerCase() + "` = " + String.valueOf(fieldValue).toLowerCase());
		        		} else {
		        			 sb.append(" AND `" + fieldName + "` = " + fieldValue);
		        		}
		            } else if (Operator.ne == queryOperator) {
		            	if (filter.getIgnoreCase() != null && filter.getIgnoreCase() && fieldValue instanceof String) {
		            		 sb.append(" AND `" + fieldName.toLowerCase() + "` != " + String.valueOf(fieldValue).toLowerCase());
		            	} else {
		        			 sb.append(" AND `" + fieldName + "` != " + fieldValue);
		        		}
		            } else if (Operator.gt == queryOperator) {
		                sb.append(" AND `" + fieldName + "` >  " + (Number) fieldValue);
		            } else if (Operator.lt == queryOperator) {
		            	sb.append(" AND `" + fieldName + "` <  " + (Number) fieldValue);
					} else if (Operator.ge == queryOperator) {
						sb.append(" AND `" + fieldName + "` >= " + (Number) fieldValue);
					} else if (Operator.le == queryOperator) {
						sb.append(" AND `" + fieldName + "` <= " + (Number) fieldValue);
					} else if (Operator.like == queryOperator && fieldValue instanceof String) {
						sb.append(" AND `" + fieldName + "` LIKE '%" + fieldValue.toString() + "%' ");
					} else if (Operator.in == queryOperator) {
						sb.append(" AND `" + fieldName + "` IN (" + fieldValue + ") ");
					} else if (Operator.isNull == queryOperator) {
						sb.append(" AND `" + fieldName + "` IS NULL ");
					} else if (Operator.isNotNull == queryOperator) {
						sb.append(" AND `" + fieldName + "` IS NOT NULL ");
					}
		        }
				 
			}
		}
		if (orders != null && 0 < orders.size()) {
			sb.append(" ORDER BY ");
			for (int i = 0; i < orders.size(); i++) {
				String field = propertyToField(orders.get(i).getProperty());
				int size = orders.size() - 1;
				if (orders.get(i).getDirection() == Direction.asc) {
					sb.append("`" + field + "` ASC ").append(i == size ? "" : ", ");
				} else if (orders.get(i).getDirection() == Direction.desc) {
					sb.append("`" + field + "` DESC ").append(i == size ? "" : ", ");
				}
			}
		}
		if (first != null && 0 < first) {
			sb.append(" LIMIT 0, " + first);
		}
		if (count != null && 0 < count) {
			sb.append(" LIMIT 0, " + count);
		}
		return sb.toString();
	}
	
	/**
	 * 过滤条件
	 * @param filters
	 * @return
	 */
	public static String buildSQL(List<Filter> filters) {
		StringBuilder sb = new StringBuilder();
		if (filters != null && 0 < filters.size()) {
			for (Filter filter : filters) {
				if (filter == null || StringUtils.isEmpty(filter.getProperty())) {
					continue;
				}
				String fieldName = propertyToField(filter.getProperty());
				Object fieldValue = filter.getValue();
				Operator queryOperator= filter.getOperator();
				// 非空的时候进行设置
		        if (StrKit.notNull(fieldValue) && StrKit.notNull(fieldName)) {
		        	if (Operator.eq == queryOperator) {
		        		if (filter.getIgnoreCase() != null && filter.getIgnoreCase() && fieldValue instanceof String) {
		        			 sb.append(" AND `" + fieldName.toLowerCase() + "` = " + String.valueOf(fieldValue).toLowerCase());
		        		} else {
		        			 sb.append(" AND `" + fieldName + "` = " + fieldValue);
		        		}
		            } else if (Operator.ne == queryOperator) {
		            	if (filter.getIgnoreCase() != null && filter.getIgnoreCase() && fieldValue instanceof String) {
		            		 sb.append(" AND `" + fieldName.toLowerCase() + "` != " + String.valueOf(fieldValue).toLowerCase());
		            	} else {
		        			 sb.append(" AND `" + fieldName + "` != " + fieldValue);
		        		}
		            } else if (Operator.gt == queryOperator) {
		                sb.append(" AND `" + fieldName + "` >  " + (Number) fieldValue);
		            } else if (Operator.lt == queryOperator) {
		            	sb.append(" AND `" + fieldName + "` <  " + (Number) fieldValue);
					} else if (Operator.ge == queryOperator) {
						sb.append(" AND `" + fieldName + "` >= " + (Number) fieldValue);
					} else if (Operator.le == queryOperator) {
						sb.append(" AND `" + fieldName + "` <= " + (Number) fieldValue);
					} else if (Operator.like == queryOperator && fieldValue instanceof String) {
						sb.append(" AND `" + fieldName + "` LIKE '%" + fieldValue.toString() + "%' ");
					} else if (Operator.in == queryOperator) {
						sb.append(" AND `" + fieldName + "` IN (" + fieldValue + ") ");
					} else if (Operator.isNull == queryOperator) {
						sb.append(" AND `" + fieldName + "` IS NULL ");
					} else if (Operator.isNotNull == queryOperator) {
						sb.append(" AND `" + fieldName + "` IS NOT NULL ");
					}
		        }
				 
			}
		}
		return sb.toString();
	}
	
	/**
	 * 对象属性转换为字段 例如：userName to user_name
	 * 
	 * @param property
	 *            字段名
	 * @return
	 */
	public static String propertyToField(String property) {
		if (null == property) {
			return "";
		}
		char[] chars = property.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (char c : chars) {
			if (CharUtils.isAsciiAlphaUpper(c)) {
				sb.append("_" + StringUtils.lowerCase(CharUtils.toString(c)));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 字段转换成对象属性 例如：user_name to userName
	 * 
	 * @param field
	 * @return
	 */
	public static String fieldToProperty(String field) {
		if (null == field) {
			return "";
		}
		char[] chars = field.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == '_') {
				int j = i + 1;
				if (j < chars.length) {
					sb.append(StringUtils.upperCase(CharUtils.toString(chars[j])));
					i++;
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
}
