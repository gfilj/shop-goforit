package com.jfinalshop.service;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinalshop.common.Setting;
import com.jfinalshop.model.Shipping;
import com.jfinalshop.utils.SettingUtils;

/**
 * Service - 发货单
 * 
 * 
 * 
 */
public class ShippingService extends BaseService<Shipping> {
	
	public ShippingService() {
		super(Shipping.class);
	}

	
	/**
	 * 根据编号查找发货单
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 若不存在则返回null
	 */
	public Shipping findBySn(String sn) {
		return Shipping.dao.findBySn(sn);
	}

	/**
	 * 查询物流动态
	 * 
	 * @param shipping
	 *            发货单
	 * @return 物流动态
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> query(Shipping shipping) {
		Setting setting = SettingUtils.get();
		Map<String, Object> data = new HashMap<String, Object>();
		if (shipping != null && StringUtils.isNotEmpty(setting.getKuaidi100Key()) && StringUtils.isNotEmpty(shipping.getDeliveryCorpCode()) && StringUtils.isNotEmpty(shipping.getTrackingNo())) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				URL url = new URL("http://api.kuaidi100.com/api?id=" + setting.getKuaidi100Key() + "&com=" + shipping.getDeliveryCorpCode() + "&nu=" + shipping.getTrackingNo() + "&show=0&muti=1&order=asc");
				data = mapper.readValue(url, Map.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	
	/**
	 * 保存发货单
	 * 
	 */
	public boolean save(Shipping shipping) {
		boolean result = false;
		result = super.save(shipping);
		ShippingItemService.service.save(shipping);
		return result;
	}
	
}
