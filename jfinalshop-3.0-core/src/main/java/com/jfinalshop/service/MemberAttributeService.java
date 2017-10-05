package com.jfinalshop.service;

import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberAttribute;
import com.jfinalshop.model.MemberAttributeOption;
import com.jfinalshop.security.ShiroUtil;

/**
 * Service - 会员注册项
 * 
 * 
 * 
 */
public class MemberAttributeService extends BaseService<MemberAttribute> {
	
	public MemberAttributeService() {
		super(MemberAttribute.class);
	}

	/**
	 * 查询实体对象总数
	 * 
	 * @return 实体对象总数
	 */
	public long count() {
		return MemberAttribute.dao.count();
	}
	
	/**
	 * 查找会员注册项
	 * 
	 * @return 会员注册项，仅包含已启用会员注册项
	 */
	public List<MemberAttribute> findList() {
		return MemberAttribute.dao.findList();
	}
	
	/**
	 * 查找会员注册项(缓存)
	 * 
	 * @param cacheRegion
	 *            缓存区域
	 * @return 会员注册项(缓存)，仅包含已启用会员注册项
	 */
	@CacheName("memberAttribute")
	public List<MemberAttribute> findList(String cacheRegion) {
		return findList();
	}
	
	/**
	 * 查找未使用的对象属性序号
	 * 
	 * @return 未使用的对象属性序号，若无可用序号则返回null
	 */
	public Integer findUnusedPropertyIndex() {
		for (int i = 0; i < Member.ATTRIBUTE_VALUE_PROPERTY_COUNT; i++) {
			String sql = "select count(*) from member_attribute memberAttribute where memberAttribute.property_index = ?";
			Long count = Db.queryLong(sql, i);
			if (count == 0) {
				return i;
			}
		}
		return null;
	}
	

	/**
	 * 保存
	 */
	public boolean save(MemberAttribute memberAttribute) {
		boolean result = false;
		memberAttribute.setCreateBy(ShiroUtil.getName());
		memberAttribute.setCreationDate(new Date());
		memberAttribute.setDeleteFlag(false);
		result = memberAttribute.save();
		MemberAttributeOptionService.service.save(memberAttribute);
		return result;
	}
	
	/**
	 * 更新
	 */
	public boolean update(MemberAttribute memberAttribute) {
		boolean result = false;
		memberAttribute.setLastUpdatedBy(ShiroUtil.getName());
		memberAttribute.setLastUpdatedDate(new Date());
		result = memberAttribute.update();
		MemberAttributeOptionService.service.update(memberAttribute);
		return result;
	}
	
	
	/**
	 * 删除
	 * 
	 * @param ids
	 * @return
	 */
	@Before(Tx.class)
	public boolean delete(Long[] ids) {
		boolean result = false;
		for (Long id : ids) {
			MemberAttributeOption.dao.delete(id);
			result = MemberAttribute.dao.deleteById(id);
		}
		return result;
	}
}
