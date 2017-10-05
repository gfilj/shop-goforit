package com.jfinalshop.controller.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Setting;
import com.jfinalshop.service.CacheService;
import com.jfinalshop.utils.SettingUtils;

/**
 * Controller - 统计
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/statistics")
public class StatisticsController extends BaseAdminController {

	private CacheService cacheService = enhance(CacheService.class);
			
	/**
	 * 查看
	 */
	public void view() {
		render("/admin/statistics/view.html");
	}
	
	/**
	 * 设置
	 */
	public void setting() {
		render("/admin/statistics/setting.html");
	}
	
	/**
	 * 设置
	 */
	public void submit() {
		Boolean isEnabled = getParaToBoolean("isEnabled");
		Setting setting = SettingUtils.get();
		if (isEnabled) {
			if (StringUtils.isEmpty(setting.getCnzzSiteId()) || StringUtils.isEmpty(setting.getCnzzPassword())) {
				try {
					String createAccountUrl = "http://intf.cnzz.com/user/companion/shopxx.php?domain=" + setting.getSiteUrl() + "&key=" + DigestUtils.md5Hex(setting.getSiteUrl() + "Lfg4uP0H");
					URLConnection urlConnection = new URL(createAccountUrl).openConnection();
					BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
					String line = null;
					while ((line = in.readLine()) != null) {
						if (line.contains("@")) {
							break;
						}
					}
					if (line != null) {
						setting.setCnzzSiteId(StringUtils.substringBefore(line, "@"));
						setting.setCnzzPassword(StringUtils.substringAfter(line, "@"));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		setting.setIsCnzzEnabled(isEnabled);
		SettingUtils.set(setting);
		cacheService.clear();
		addFlashMessage(SUCCESS_MESSAGE);
		setting();
	}
}
