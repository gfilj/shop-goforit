package com.jfinalshop.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinalshop.common.CommonAttributes;
import com.jfinalshop.common.LogConfig;

/**
 * Service - 日志配置
 * 
 * 
 * 
 */
public class LogConfigService {

	/**
	 * 获取所有日志配置
	 * 
	 * @return 所有日志配置
	 */
	@SuppressWarnings("unchecked")
	@CacheName("logConfig")
	public List<LogConfig> getAll() {
		try {
			File shopxxXmlFile = new File(PathKit.getRootClassPath() + CommonAttributes.SHOPXX_XML_PATH);
			Document document = new SAXReader().read(shopxxXmlFile);
			List<org.dom4j.Element> elements = document.selectNodes("/jfinalshopxx/logConfig");
			List<LogConfig> logConfigs = new ArrayList<LogConfig>();
			for (org.dom4j.Element element : elements) {
				String operation = element.attributeValue("operation");
				String urlPattern = element.attributeValue("urlPattern");
				LogConfig logConfig = new LogConfig();
				logConfig.setOperation(operation);
				logConfig.setUrlPattern(urlPattern);
				logConfigs.add(logConfig);
			}
			return logConfigs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
