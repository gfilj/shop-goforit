/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop;

import java.math.BigDecimal;

import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Setting;
import com.jfinalshop.template.directive.FlashMessageDirective;
import com.jfinalshop.utils.SettingUtils;


/**
 * Controller - 基类
 * 
 * 
 * 
 */
public class BaseShopController extends Controller {

	/** 错误视图 */
	protected static final String ERROR_VIEW = "/shop/common/error";

	/** 错误消息 */
	protected static final Message ERROR_MESSAGE = Message.error("shop.message.error");

	/** 成功消息 */
	protected static final Message SUCCESS_MESSAGE = Message.success("shop.message.success");


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
			setAttr(FlashMessageDirective.FLASH_MESSAGE_ATTRIBUTE_NAME, message);
		}
	}

}