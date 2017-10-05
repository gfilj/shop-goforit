/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Setting;
import com.jfinalshop.interceptor.AuthInterceptor;
import com.jfinalshop.interceptor.LogInterceptor;
import com.jfinalshop.model.Log;
import com.jfinalshop.template.directive.FlashMessageDirective;
import com.jfinalshop.utils.SettingUtils;

/**
 * Controller - 基类
 * 
 * 
 * 
 */
@Before({ AuthInterceptor.class, LogInterceptor.class })
public class BaseAdminController extends Controller {
	
	/** 错误视图 */
	protected static final String ERROR_VIEW = "/common/error";

	/** 错误消息 */
	protected static final Message ERROR_MESSAGE = Message.error("admin.message.error");

	/** 成功消息 */
	protected static final Message SUCCESS_MESSAGE = Message.success("admin.message.success");

	public static final int PAGENUMBER = 1;
	public static final int PAGESIZE = 10;
	
	/**
	 * 获取对象数组
	 * @param modelClass
	 * @return
	 */
	public <T> List<T> getModels(Class<T> modelClass) {
		return getModels(modelClass, StrKit.firstCharToLowerCase(modelClass.getSimpleName()));
	}

	/**
	 * 获取前端传来的数组对象并响应成Model列表
	 */
	public <T> List<T> getModels(Class<T> modelClass, String modelName) {
		List<String> indexes = getIndexes(modelName);
		List<T> list = new ArrayList<T>();
		for (String index : indexes) {
			T m = getModel(modelClass, modelName + "[" + index + "]");
			if (m != null) {
				list.add(m);
			}
		}
		return list;
	}

	/**
	 * 提取model对象数组的标号
	 */
	private List<String> getIndexes(String modelName) {
		// 提取标号
		List<String> list = new ArrayList<String>();
		String modelNameAndLeft = modelName + "[";
		Map<String, String[]> parasMap = getRequest().getParameterMap();
		for (Map.Entry<String, String[]> e : parasMap.entrySet()) {
			String paraKey = e.getKey();
			if (paraKey.startsWith(modelNameAndLeft)) {
				String no = paraKey.substring(paraKey.indexOf("[") + 1, paraKey.indexOf("]"));
				if (!list.contains(no)) {
					list.add(no);
				}
			}
		}
		return list;
	}


	/**
	 * 货币格式化
	 * 
	 * @param amount
	 *            金额
	 * @param showSign
	 *            显示标志
	 * @param showUnit
	 *            显示单位
	 * @return 货币格式化
	 */
	protected String currency(BigDecimal amount, boolean showSign, boolean showUnit) {
		Setting setting = SettingUtils.get();
		String price = setting.setScale(amount).toString();
		if (showSign) {
			price = setting.getCurrencySign() + price;
		}
		if (showUnit) {
			price += setting.getCurrencyUnit();
		}
		return price;
	}

	/**
	 * 获取国际化消息
	 * 
	 * @param code
	 *            代码
	 * @param args
	 *            参数
	 * @return 国际化消息
	 */
	protected String message(String code, Object... args) {
		Res resZh = I18n.use();
		return resZh.format(code, args);
	}

	
	/**
	 * 添加瞬时消息
	 * 
	 * @param redirectAttributes
	 *            RedirectAttributes
	 * @param message
	 *            消息
	 */
	protected void addFlashMessage(Message message) {
		if (message != null) {
			setSessionAttr(FlashMessageDirective.FLASH_MESSAGE_ATTRIBUTE_NAME, message);
		}
	}
	

	/**
	 * 添加日志
	 * 
	 * @param content
	 *            内容
	 */
	protected void addLog(String content) {
		if (content != null) {
			setAttr(Log.LOG_CONTENT_ATTRIBUTE_NAME, content);
		}
	}
	
	/**
	 * 跳转错误页
	 */
	protected void renderError(String url, String msg, Integer... time) {
		this.setAttr("url", url);
		this.setAttr("error", msg);
		this.setAttr("the_time", time.length==0 ? 6 : time[0].intValue()); //默认6秒
		render("/commons/error.html");
	}

}