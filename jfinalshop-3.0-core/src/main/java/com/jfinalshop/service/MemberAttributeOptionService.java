package com.jfinalshop.service;

import com.jfinalshop.model.MemberAttribute;
import com.jfinalshop.model.MemberAttributeOption;

public class MemberAttributeOptionService extends BaseService<MemberAttributeOption> {
	public static final MemberAttributeOptionService service = new MemberAttributeOptionService();
	
	public MemberAttributeOptionService() {
		super(MemberAttributeOption.class);
	}
	
	/**
	 * 保存
	 * 
	 * 
	 */
	public boolean save(MemberAttribute memberAttribute) {
		boolean result = false;
		if(!memberAttribute.getOptions().isEmpty()) {
			for(String option : memberAttribute.getOptions()) {
				MemberAttributeOption memberAttributeOption = new MemberAttributeOption();
				memberAttributeOption.setMemberAttribute(memberAttribute.getId());
				memberAttributeOption.setOptions(option);
				result = memberAttributeOption.save();
			}
		}
		return result;
	}
	
	/**
	 * 更新
	 * 
	 * 
	 */
	public boolean update(MemberAttribute memberAttribute) {
		boolean result = false;
		if(!memberAttribute.getOptions().isEmpty()) {
			MemberAttributeOption.dao.delete(memberAttribute.getId());
			for(String option : memberAttribute.getOptions()) {
				MemberAttributeOption memberAttributeOption = new MemberAttributeOption();
				memberAttributeOption.setMemberAttribute(memberAttribute.getId());
				memberAttributeOption.setOptions(option);
				memberAttributeOption.save();
			}
		}
		return result;
	}
	
	
}
