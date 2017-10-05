package com.jfinalshop.service;

import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.model.Specification;
import com.jfinalshop.model.SpecificationValue;
import com.jfinalshop.security.ShiroUtil;

/**
 * Service - 规格
 * 
 * 
 * 
 */
public class SpecificationService extends BaseService<Specification> {
	private SpecificationValueService specificationValueService = Enhancer.enhance(SpecificationValueService.class);
	
	public SpecificationService() {
		super(Specification.class);
	}
	
	
	
	/**
	 * 保存
	 */
	public boolean save(Specification specification, List<SpecificationValue> specificationValues) {
		boolean result = false;
		specification.setCreateBy(ShiroUtil.getName());
		specification.setCreationDate(new Date());
		specification.setDeleteFlag(false);
		result = specification.save();
		if(specificationValues != null && 0 < specificationValues.size()) {
			for(SpecificationValue specificationValue : specificationValues) {
				specificationValue.setSpecificationId(specification.getId());
				specificationValueService.save(specificationValue);
			}
		}
		return result;
	}
	
	/**
	 * 更新
	 */
	public boolean update(Specification specification, List<SpecificationValue> specificationValues) {
		boolean result = false;
		specification.setLastUpdatedBy(ShiroUtil.getName());
		specification.setLastUpdatedDate(new Date());
		result = specification.update();
		if(specificationValues != null && 0 < specificationValues.size()) {
			specificationValueService.delete(specification.getId());
			for(SpecificationValue specificationValue : specificationValues) {
				specificationValue.setSpecificationId(specification.getId());
				specificationValueService.save(specificationValue);
			}
		}
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
		boolean result1 = false, result2 = false;
		for (Long id : ids) {
			result1 = specificationValueService.delete(id);
			result2 = Specification.dao.deleteById(id);
		}
		return result1 && result2;
	}
}
