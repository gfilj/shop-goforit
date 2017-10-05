/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.admin;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.utils.DateUtil;

/**
 * Controller - 销售统计
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/sales")
public class SalesController extends BaseAdminController {

	/** 最大统计数 */
	private static final int MAX_SIZE = 12;

	/**
	 * 统计类型
	 */
	public enum Type {
		/**
		 * 年度统计
		 */
		year,
		/**
		 * 月度统计
		 */
		month
	}

	private OrderService orderService = new OrderService();

	/**
	 * 查看
	 */
	public void view() {
		Type type =  StrKit.notBlank(getPara("type")) ? Type.valueOf(getPara("type", "")) : null;
		Date beginDate = StrKit.notBlank(getPara("beginDate")) ? DateUtil.strToDate(getPara("beginDate")) : null;
		Date endDate = StrKit.notBlank(getPara("beginDate")) ? DateUtil.strToDate(getPara("endDate")) : null;
		if (type == null) {
			type = Type.month;
		}
		if (beginDate == null) {
			beginDate = DateUtils.addMonths(new Date(), -11);
		}
		if (endDate == null) {
			endDate = new Date();
		}
		Map<Date, BigDecimal> salesAmountMap = new LinkedHashMap<Date, BigDecimal>();
		Map<Date, Integer> salesVolumeMap = new LinkedHashMap<Date, Integer>();
		Calendar beginCalendar = DateUtils.toCalendar(beginDate);
		Calendar endCalendar = DateUtils.toCalendar(endDate);
		int beginYear = beginCalendar.get(Calendar.YEAR);
		int endYear = endCalendar.get(Calendar.YEAR);
		int beginMonth = beginCalendar.get(Calendar.MONTH);
		int endMonth = endCalendar.get(Calendar.MONTH);
		for (int year = beginYear; year <= endYear; year++) {
			if (salesAmountMap.size() >= MAX_SIZE) {
				break;
			}
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, year);
			if (type == Type.year) {
				calendar.set(Calendar.MONTH, calendar.getActualMinimum(Calendar.MONTH));
				calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
				calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
				calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
				calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
				Date begin = calendar.getTime();
				calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));
				calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
				calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
				calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
				calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
				Date end = calendar.getTime();
				BigDecimal salesAmount = orderService.getSalesAmount(begin, end);
				Integer salesVolume = orderService.getSalesVolume(begin, end);
				salesAmountMap.put(begin, salesAmount != null ? salesAmount : BigDecimal.ZERO);
				salesVolumeMap.put(begin, salesVolume != null ? salesVolume : 0);
			} else {
				for (int month = year == beginYear ? beginMonth : calendar.getActualMinimum(Calendar.MONTH); month <= (year == endYear ? endMonth : calendar.getActualMaximum(Calendar.MONTH)); month++) {
					if (salesAmountMap.size() >= MAX_SIZE) {
						break;
					}
					calendar.set(Calendar.MONTH, month);
					calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
					calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
					calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
					calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
					Date begin = calendar.getTime();
					calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
					calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
					calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
					calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
					Date end = calendar.getTime();
					BigDecimal salesAmount = orderService.getSalesAmount(begin, end);
					Integer salesVolume = orderService.getSalesVolume(begin, end);
					salesAmountMap.put(begin, salesAmount != null ? salesAmount : BigDecimal.ZERO);
					salesVolumeMap.put(begin, salesVolume != null ? salesVolume : 0);
				}
			}
		}
		setAttr("types", Type.values());
		setAttr("type", type);
		setAttr("beginDate", beginDate);
		setAttr("endDate", endDate);
		setAttr("salesAmountMap", salesAmountMap);
		setAttr("salesVolumeMap", salesVolumeMap);
		render("/admin/sales/view.html");
	}

}