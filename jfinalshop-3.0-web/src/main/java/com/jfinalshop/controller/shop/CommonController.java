/*
 * 
 * 
 * 
 */
package com.jfinalshop.controller.shop;

import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;

import com.jfinal.aop.Clear;
import com.jfinalshop.common.Setting;
import com.jfinalshop.model.Area;
import com.jfinalshop.plugin.captcha.CaptchaRender;
import com.jfinalshop.service.AdminService;
import com.jfinalshop.service.AreaService;
import com.jfinalshop.service.RSAService;
import com.jfinalshop.utils.SettingUtils;

/**
 * Controller - 共用
 * 
 * 
 * 
 */
public class CommonController extends BaseShopController {

	private AreaService areaService = enhance(AreaService.class);
	private RSAService rsaService = new RSAService();
	private AdminService adminService = enhance(AdminService.class);

	/**
	 * 网站关闭
	 */
	public void siteClose() {
		Setting setting = SettingUtils.get();
		if (setting.getIsSiteEnabled()) {
			redirect("/"); 
		} else {
			render("/shop/common/site_close.html");
		}
	}
	
	/**
	 * 公钥
	 */
	@Clear
	public void publicKey() {
		HttpServletRequest request = getRequest();
		RSAPublicKey publicKey = rsaService.generateKey(request);
		Map<String, String> data = new HashMap<String, String>();
		data.put("modulus", Base64.encodeBase64String(publicKey.getModulus().toByteArray()));
		data.put("exponent", Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray()));
		renderJson(data);
	}

	/** 
	 * 登录验证
	 */
	@Clear
	public void submit() {
		String loginName = getPara("username", "");
		String password = rsaService.decryptParameter("enPassword", getRequest());
		rsaService.removePrivateKey(getRequest());
		boolean rememberMe = getParaToBoolean("rememberMe", false);
		String captchaToken = getPara("captchaToken");
		Map<String, String> map = adminService.login(loginName, password, rememberMe, captchaToken);
		renderJson(map);
	}

	/**
	 * 地区
	 */
	@Clear
	public void area() {
		Long parentId = getParaToLong("parentId");
		List<Area> areas = new ArrayList<Area>();
		Area parent = areaService.find(parentId);
		if (parent != null) {
			areas = new ArrayList<Area>(parent.getChildren());
		} else {
			areas = areaService.findRoots();
		}
		Map<Long, String> options = new HashMap<Long, String>();
		for (Area area : areas) {
			options.put(area.getId(), area.getName());
		}
		renderJson(options);
	}

	/**
	 * 验证码
	 */
	@Clear
	public void captcha() {
		int width = 0, height = 0, minnum = 0, maxnum = 0, fontsize = 0;
		CaptchaRender captcha = new CaptchaRender();
		if (isParaExists("width")) {
			width = getParaToInt("width");
		}
		if (isParaExists("height")) {
			height = getParaToInt("height");
		}
		if (width > 0 && height > 0)
			captcha.setImgSize(width, height);
		if (isParaExists("minnum")) {
			minnum = getParaToInt("minnum");
		}
		if (isParaExists("maxnum")) {
			maxnum = getParaToInt("maxnum");
		}
		if (minnum > 0 && maxnum > 0)
			captcha.setFontNum(minnum, maxnum);
		if (isParaExists("fontsize")) {
			fontsize = getParaToInt("fontsize");
		}
		if (fontsize > 0)
			captcha.setFontSize(fontsize, fontsize);
		// 干扰线数量 默认0
		captcha.setLineNum(2);
		// 噪点数量 默认50
		captcha.setArtifactNum(30);
		// 使用字符 去掉0和o 避免难以确认
		captcha.setCode("123456789");
		 //验证码在session里的名字 默认 captcha,创建时间为：名字_time
		// captcha.setCaptchaName("captcha");
	    //验证码颜色 默认黑色
		// captcha.setDrawColor(new Color(255,0,0));
	    //背景干扰物颜色  默认灰
		// captcha.setDrawBgColor(new Color(0,0,0));
	    //背景色+透明度 前三位数字是rgb色，第四个数字是透明度  默认透明
		// captcha.setBgColor(new Color(225, 225, 0, 100));
	    //滤镜特效 默认随机特效 //曲面Curves //大理石纹Marble //弯折Double //颤动Wobble //扩散Diffuse
		captcha.setFilter(CaptchaRender.FilterFactory.Curves);
		// 随机色 默认黑验证码 灰背景元素
		captcha.setRandomColor(true);
		render(captcha);
	}

	/**
	 * 错误提示
	 */
	@Clear
	public void error() {
		render("/shop/common/error.html");
	}

	/**
	 * 资源不存在
	 */
	@Clear
	public void resourceNotFound() {
		render("/shop/common/resource_not_found.html");
	}

}