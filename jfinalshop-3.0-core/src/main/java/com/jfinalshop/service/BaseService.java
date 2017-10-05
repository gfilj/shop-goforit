package com.jfinalshop.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import com.jfinalshop.common.Filter;
import com.jfinalshop.common.Order;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.security.ShiroUtil;
import com.jfinalshop.utils.ConditionUtil;
import com.jfinalshop.utils.DateUtil;
import com.jfinalshop.utils.GenericsUtils;

/**
 * Service - 基类
 * 
 * 
 * 
 */
public class BaseService <M extends Model<M>> {
	
	private final Logger log = Logger.getLogger(getClass());
	private M model;
	private Class<M> modelClass;
	
	/**
	 * 查找实体对象
	 * 
	 * @param id
	 *            ID
	 * @return 实体对象，若不存在则返回null
	 */
	public M find(Object id) {
		M m = model.findById(id);
		if (m != null) {
			return m;
		}
		return null;
	}
	
	
	/**
	 * 查找所有实体对象集合
	 * 
	 * @return 所有实体对象集合
	 */
	public List<M> findAll() {
		String sql = "SELECT * FROM `" + getTable() + "` WHERE 1 = 1 ";
		return model.find(sql);
	}
	
	/**
	 * 查找实体对象集合
	 * 
	 * @param ids
	 *            ID
	 * @return 实体对象集合
	 */
	public List<M> findList(Long... ids) {
		List<M> result = new ArrayList<M>();
		if (ids != null) {
			for (Long id : ids) {
				M entity = find(id);
				if (entity != null) {
					result.add(entity);
				}
			}
		}
		return result;
	}
	
	/**
	 * 查找实体对象集合
	 * 
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 实体对象集合
	 */
	public List<M> findList(Integer count, List<Filter> filters, List<Order> orders) {
		return findList(null, count, filters, orders);
	}
	
	/**
	 * 查找实体对象集合
	 * 
	 * @param first
	 *            起始记录
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 实体对象集合
	 */
	public List<M> findList(Integer first, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT * FROM `" + getTable() + "` WHERE 1 = 1 ";
		String condition = ConditionUtil.buildSQL(first, count, filters, orders);
		if (condition != null) {
			sql += condition;
		}
		return model.find(sql);
	}
	
	/**
	 * 查找实体对象分页
	 * 
	 * @param pageable
	 *            分页信息
	 * @return 实体对象分页
	 */
	public Page<M> findPage(Pageable pageable) {
		String select = "SELECT *";
		String sqlExceptSelect = "FROM `" + getTable() + "` WHERE 1 = 1 ";
		if (StrKit.notBlank(pageable.getSearchValue()) && StrKit.notBlank(pageable.getSearchProperty())) {
			sqlExceptSelect += " AND " + pageable.getSearchProperty() + " LIKE '%" + pageable.getSearchValue().trim() + "%'";
		}
		if (StrKit.notBlank(pageable.getOrderProperty()) && StrKit.notBlank(pageable.getOrderDirection())) {
			sqlExceptSelect += " ORDER BY " + pageable.getOrderProperty() + " " + pageable.getOrderDirection();
		}
		return model.paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect);
	}
	
	/**
	 * 查询实体对象总数
	 * 
	 * @return 实体对象总数
	 */
	public long count() {
		String sql = "SELECT COUNT(*) FROM `" + getTable() + "`";
		return Db.queryLong(sql);
	}
	
	/**
	 * 查询实体对象数量
	 * 
	 * @param filters
	 *            筛选
	 * @return 实体对象数量
	 */
	public long count(Filter... filters) {
		if (filters != null) {
			String sql = "SELECT COUNT(*) FROM `" + getTable() + "` WHERE 1 = 1 " + ConditionUtil.buildSQL(Arrays.asList(filters));
			return Db.queryLong(sql);
		}
		return 0;
	}
	
	
	/**
	 * 判断实体对象是否存在
	 * 
	 * @param filters
	 *            筛选
	 * @return 实体对象是否存在
	 */
	public boolean exists(Filter... filters) {
		if (filters != null) {
			String sql = "SELECT COUNT(*) FROM `" + getTable() + "` WHERE 1 = 1 " + ConditionUtil.buildSQL(Arrays.asList(filters));
			return Db.queryLong(sql) > 0;
		}
		return false;
	}
	
	
	/**
	 * 保存实体对象
	 * 
	 * @param model
	 *            实体对象
	 */
	public boolean save(M model) {
		model.set("create_by", getCurrentName());
		model.set("creation_date", getSysDate());
		model.set("delete_flag", false);
		return model.save();
	}
	
	
	/**
	 * 更新实体对象
	 * 
	 * @param model
	 *            实体对象
	 * @return 实体对象
	 */
	public boolean update(M model) {
		model.set("last_updated_by", getCurrentName());
		model.set("last_updated_date", getSysDate());
		return model.update();
	}
	
	/**
	 * 删除实体对象
	 * 
	 * @param id
	 *            ID
	 */
	public boolean delete(Object id) {
		if (id != null) {
			return model.deleteById(id);
		}
		return false;
	}
	
	/**
	 * 删除实体对象
	 * 
	 * @param ids
	 *            ID
	 */
	public boolean delete(Long[] ids) {
		boolean result = false;
		if (ids != null && 0 < ids.length) {
			for(Long id : ids) {
				result = model.deleteById(id);
			}
		}
		return result;
	}
	
	/**
	 * 删除实体对象
	 * 
	 * @param entity
	 *            实体对象
	 */
	public boolean delete(M model) {
		if (model != null) {
			return model.delete();
		}
		return false;
	}
	
	public String getCurrentName() {
		return ShiroUtil.getName();
	}
	
	public Date getSysDate() {
		return DateUtil.getSysDate();
	}
	
	public String getTable() {
		Table table = TableMapping.me().getTable(getModelClass());
		return table.getName();
	}
	
	public Class<M> getModelClass() {
		return modelClass;
	}

	public void setModelClass(Class<M> modelClass) {
		this.modelClass = modelClass;
	}
	
	@SuppressWarnings("unchecked")
	public BaseService(Class<M> entityClass) {
		this.setModelClass(GenericsUtils.getSuperClassGenricType(entityClass));
		try {
			model = modelClass.newInstance();
		} catch (InstantiationException e) {
			log.error("实例化Model失败！" + e);
		} catch (IllegalAccessException e) {
			log.error("实例化Model失败！" + e);
		}
	}
}
