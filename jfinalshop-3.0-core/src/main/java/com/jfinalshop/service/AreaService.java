package com.jfinalshop.service;

import java.util.Date;
import java.util.List;

import com.jfinalshop.model.Area;
import com.jfinalshop.security.ShiroUtil;

/**
 * Service - 地区
 * 
 * 
 * 
 */
public class AreaService extends BaseService<Area>{

	public AreaService() {
		super(Area.class);
	}
	
	/**
	 * 查找顶级地区
	 * 
	 * @return 顶级地区
	 */
	public List<Area> findRoots() {
		return Area.dao.findRoots(null);
	}

	/**
	 * 查找顶级地区
	 * 
	 * @param count
	 *            数量
	 * @return 顶级地区
	 */
	public List<Area> findRoots(Integer count) {
		return Area.dao.findRoots(count);
	}
	
	/**
	 * 保存
	 */
	public boolean save(Area area) {
		//area.setId(SequenceUtil.getId());
		area.setCreateBy(ShiroUtil.getName());
		area.setCreationDate(new Date());
		area.setDeleteFlag(false);
		return Area.dao.save(area);
	}
	
	
	
	/**
	 * 更新
	 */
	public boolean update(Area area) {
		area.remove("fullName");
		area.setLastUpdatedBy(ShiroUtil.getName());
		area.setLastUpdatedDate(new Date());
		area.setDeleteFlag(false);
		return Area.dao.update(area);
	}
	/**
	 * 判断是否已经存在地区
	 * 
	 * @return 顶级地区
	 */
	public boolean areanameExists(String brandname) {
		return Area.dao.areaNameExists(brandname);
	}
}
