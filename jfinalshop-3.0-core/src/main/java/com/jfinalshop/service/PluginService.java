package com.jfinalshop.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.jfinalshop.plugin.AlipayBankPlugin;
import com.jfinalshop.plugin.AlipayDirectPlugin;
import com.jfinalshop.plugin.AlipayDualPlugin;
import com.jfinalshop.plugin.AlipayPartnerPlugin;
import com.jfinalshop.plugin.FilePlugin;
import com.jfinalshop.plugin.FtpPlugin;
import com.jfinalshop.plugin.OssPlugin;
import com.jfinalshop.plugin.Pay99billBankPlugin;
import com.jfinalshop.plugin.Pay99billPlugin;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.plugin.PaypalPlugin;
import com.jfinalshop.plugin.StoragePlugin;
import com.jfinalshop.plugin.TenpayBankPlugin;
import com.jfinalshop.plugin.TenpayDirectPlugin;
import com.jfinalshop.plugin.TenpayPartnerPlugin;
import com.jfinalshop.plugin.UnionpayPlugin;
import com.jfinalshop.plugin.YeepayPlugin;

/**
 * Service - 插件
 * 
 * 
 * 
 */
public class PluginService  {

	private static final List<PaymentPlugin> paymentPlugins = new ArrayList<PaymentPlugin>();
	private static final List<StoragePlugin> storagePlugins = new ArrayList<StoragePlugin>();
	private static final Map<String, PaymentPlugin> paymentPluginMap = new HashMap<String, PaymentPlugin>();
	private static final Map<String, StoragePlugin> storagePluginMap = new HashMap<String, StoragePlugin>();

	public static final PluginService service = new PluginService();

	static {
		AlipayBankPlugin alipayBankPlugin = new AlipayBankPlugin();
		AlipayDirectPlugin alipayDirectPlugin = new AlipayDirectPlugin();
		AlipayDualPlugin alipayDualPlugin = new AlipayDualPlugin();
		AlipayPartnerPlugin alipayPartnerPlugin = new AlipayPartnerPlugin();
		Pay99billBankPlugin pay99billBankPlugin = new Pay99billBankPlugin();
		Pay99billPlugin pay99billPlugin = new Pay99billPlugin();
		PaypalPlugin paypalPlugin = new PaypalPlugin();
		TenpayBankPlugin tenpayBankPlugin = new TenpayBankPlugin();
		TenpayDirectPlugin tenpayDirectPlugin = new TenpayDirectPlugin();
		TenpayPartnerPlugin tenpayPartnerPlugin = new TenpayPartnerPlugin();
		UnionpayPlugin unionpayPlugin = new UnionpayPlugin();
		YeepayPlugin yeepayPlugin = new YeepayPlugin();
		
		paymentPlugins.add(alipayBankPlugin);
		paymentPlugins.add(alipayDirectPlugin);
		paymentPlugins.add(alipayDualPlugin);
		paymentPlugins.add(alipayPartnerPlugin);
		paymentPlugins.add(pay99billBankPlugin);
		paymentPlugins.add(pay99billPlugin);
		paymentPlugins.add(paypalPlugin);
		paymentPlugins.add(tenpayBankPlugin);
		paymentPlugins.add(tenpayDirectPlugin);
		paymentPlugins.add(tenpayPartnerPlugin);
		paymentPlugins.add(unionpayPlugin);
		paymentPlugins.add(yeepayPlugin);
		
		paymentPluginMap.put(alipayBankPlugin.getId(), alipayBankPlugin);
		paymentPluginMap.put(alipayDirectPlugin.getId(), alipayDirectPlugin);
		paymentPluginMap.put(alipayDualPlugin.getId(), alipayDualPlugin);
		paymentPluginMap.put(alipayPartnerPlugin.getId(), alipayPartnerPlugin);
		paymentPluginMap.put(alipayPartnerPlugin.getId(), alipayPartnerPlugin);
		paymentPluginMap.put(pay99billBankPlugin.getId(), pay99billBankPlugin);
		paymentPluginMap.put(pay99billPlugin.getId(), pay99billPlugin);
		paymentPluginMap.put(paypalPlugin.getId(), paypalPlugin);
		paymentPluginMap.put(tenpayBankPlugin.getId(), tenpayBankPlugin);
		paymentPluginMap.put(tenpayDirectPlugin.getId(), tenpayDirectPlugin);
		paymentPluginMap.put(tenpayPartnerPlugin.getId(), tenpayPartnerPlugin);
		paymentPluginMap.put(unionpayPlugin.getId(), unionpayPlugin);
		paymentPluginMap.put(yeepayPlugin.getId(), yeepayPlugin);
		
		
		FilePlugin filePlugin = new FilePlugin();
		FtpPlugin ftpPlugin = new FtpPlugin();
		OssPlugin ossPlugin = new OssPlugin();
		
		storagePlugins.add(filePlugin);
		storagePlugins.add(ftpPlugin);
		storagePlugins.add(ossPlugin);
		
		storagePluginMap.put(filePlugin.getId(), filePlugin);
		storagePluginMap.put(ftpPlugin.getId(), ftpPlugin);
		storagePluginMap.put(ossPlugin.getId(), ossPlugin);
	}
	
	/**
	 * 获取支付插件
	 * 
	 * @return 支付插件
	 */
	public List<PaymentPlugin> getPaymentPlugins() {
		Collections.sort(paymentPlugins);
		return paymentPlugins;
	}

	/**
	 * 获取存储插件
	 * 
	 * @return 存储插件
	 */
	public List<StoragePlugin> getStoragePlugins() {
		Collections.sort(storagePlugins);
		return storagePlugins;
	}

	/**
	 * 获取支付插件
	 * 
	 * @param isEnabled
	 *            是否启用
	 * @return 支付插件
	 */
	public List<PaymentPlugin> getPaymentPlugins(final boolean isEnabled) {
		List<PaymentPlugin> result = new ArrayList<PaymentPlugin>();
		CollectionUtils.select(paymentPlugins, new Predicate() {
			public boolean evaluate(Object object) {
				PaymentPlugin paymentPlugin = (PaymentPlugin) object;
				return paymentPlugin.getIsEnabled() == isEnabled;
			}
		}, result);
		Collections.sort(result);
		return result;
	}

	/**
	 * 获取存储插件
	 * 
	 * @param isEnabled
	 *            是否启用
	 * @return 存储插件
	 */
	public List<StoragePlugin> getStoragePlugins(final boolean isEnabled) {
		List<StoragePlugin> result = new ArrayList<StoragePlugin>();
		CollectionUtils.select(storagePlugins, new Predicate() {
			public boolean evaluate(Object object) {
				StoragePlugin storagePlugin = (StoragePlugin) object;
				return storagePlugin.getIsEnabled() == isEnabled;
			}
		}, result);
		Collections.sort(result);
		return result;
	}

	/**
	 * 获取支付插件
	 * 
	 * @param id
	 *            ID
	 * @return 支付插件
	 */
	public PaymentPlugin getPaymentPlugin(String id) {
		return paymentPluginMap.get(id);
	}

	/**
	 * 获取存储插件
	 * 
	 * @param id
	 *            ID
	 * @return 存储插件
	 */
	public StoragePlugin getStoragePlugin(String id) {
		return storagePluginMap.get(id);
	}

}
