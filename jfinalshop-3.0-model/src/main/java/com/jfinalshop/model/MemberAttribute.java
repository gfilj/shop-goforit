package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseMemberAttribute;

/**
 * Dao - 会员注册项
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class MemberAttribute extends BaseMemberAttribute<MemberAttribute> {
	public static final MemberAttribute dao = new MemberAttribute();
	
	/**
	 * 类型
	 */
	public enum Type {

		/** 姓名 */
		name,

		/** 性别 */
		gender,

		/** 出生日期 */
		birth,

		/** 地区 */
		area,

		/** 地址 */
		address,

		/** 邮编 */
		zipCode,

		/** 电话 */
		phone,

		/** 手机 */
		mobile,

		/** 文本 */
		text,

		/** 单选项 */
		select,

		/** 多选项 */
		checkbox
	}
	
	/** 可选项 */
	private List<String> options = new ArrayList<String>();

	/**
	 * 查找未使用的对象属性序号
	 * 
	 * @return 未使用的对象属性序号，若无可用序号则返回null
	 */
	public Integer findUnusedPropertyIndex() {
		for (int i = 0; i < Member.ATTRIBUTE_VALUE_PROPERTY_COUNT; i++) {
			String sql = "SELECT COUNT(*) FROM member_attribute WHERE property_index = 1";
			Long count = Db.queryLong(sql, i);
			if (count == 0) {
				return i;
			}
		}
		return null;
	}
	
	/**
	 * 清除会员注册项值
	 * 
	 * @param memberAttribute
	 *            会员注册项
	 */
	public void remove(MemberAttribute memberAttribute) {
		if (memberAttribute != null && (memberAttribute.getType() == Type.text.ordinal() || memberAttribute.getType() == Type.select.ordinal() || memberAttribute.getType() == Type.checkbox.ordinal())) {
			String propertyName = Member.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + memberAttribute.getPropertyIndex();
			String sql = "UPDATE `member` m SET m." + propertyName + " = null";
			Db.update(sql);
		}
	}
	
	
	/**
	 * 查找已启用会员注册项
	 * 
	 * @return 已启用会员注册项
	 */
	public List<MemberAttribute> findList() {
		String sql = "SELECT * FROM member_attribute WHERE is_enabled = true ORDER BY orders ASC";
		return find(sql);
	}
	
	/**
	 * 查询实体对象数量
	 * 
	 * @param filters
	 *            筛选
	 * @return 实体对象数量
	 */
	public long count() {
		String sql = "SELECT COUNT(*) FROM member_attribute";
		return Db.queryLong(sql);
	}
	
	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public Type getTypeValues() {
		return Type.values()[getType()];
	}
	
	/**
	 * 获取可选项
	 * 
	 * @return 可选项
	 */
	public List<String> getOptions() {
		String sql = "SELECT * FROM member_attribute_option WHERE `member_attribute` = ?";
		if (options.isEmpty()) {
			List<MemberAttributeOption> memberAttributeOptions = MemberAttributeOption.dao.find(sql, getId());
			for (MemberAttributeOption memberAttributeOption : memberAttributeOptions) {
				options.add(memberAttributeOption.getOptions());
			}
		}
		return options;
	}
	
	/**
	 * 设置可选项
	 * 
	 * @param options
	 *            可选项
	 */
	public void setOptions(List<String> options) {
		this.options = options;
	}
}
