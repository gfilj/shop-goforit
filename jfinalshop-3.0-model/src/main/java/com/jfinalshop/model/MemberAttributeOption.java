package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseMemberAttributeOption;

/**
 * Dao - 会员注册项选项
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class MemberAttributeOption extends BaseMemberAttributeOption<MemberAttributeOption> {
	public static final MemberAttributeOption dao = new MemberAttributeOption();
	
	/**
	 * 根据memberAttributeId删除参数
	 * @param memberAttributeId
	 * @return
	 */
	public boolean delete(Long memberAttributeId) {
		return Db.deleteById("member_attribute_option", "member_attribute", memberAttributeId);
	}
}
