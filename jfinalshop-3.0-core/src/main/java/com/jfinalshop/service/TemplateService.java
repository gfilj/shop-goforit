package com.jfinalshop.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Logger;
import com.jfinalshop.common.CommonAttributes;
import com.jfinalshop.common.Template;
import com.jfinalshop.common.Template.Type;

/**
 * Service - 模板
 * 
 * 
 * 
 */
public class TemplateService {
	
	public final Logger log = Logger.getLogger(getClass());
	
	public static final TemplateService service = new TemplateService();
	
	private ServletContext servletContext = JFinal.me().getServletContext();
	
	String templateLoaderPaths = PropKit.use("application.properties").get("template.loader_path");
	
	/**
	 * 获取模板
	 * 
	 * @param type
	 *            类型
	 * @return 模板
	 */
	public List<Template> getList(Type type) {
		if (type != null) {
			try {
				File shopxxXmlFile = new File(PathKit.getRootClassPath() + CommonAttributes.SHOPXX_XML_PATH);
				Document document = new SAXReader().read(shopxxXmlFile);
				List<Template> templates = new ArrayList<Template>();
				@SuppressWarnings("unchecked")
				List<Element> elements = document.selectNodes("/jfinalshopxx/template[@type='" + type + "']");
				for (Element element : elements) {
					Template template = getTemplate(element);
					templates.add(template);
				}
				return templates;
			} catch (Exception e) {
				log.debug("获取模板" + e);
				e.printStackTrace();
				return null;
			}
		} else {
			return getAll();
		}
	}
	
	/**
	 * 获取所有模板
	 * 
	 * @return 所有模板
	 */
	public List<Template> getAll() {
		try {
			File shopxxXmlFile = new File(PathKit.getRootClassPath() + CommonAttributes.SHOPXX_XML_PATH);
			Document document = new SAXReader().read(shopxxXmlFile);
			List<Template> templates = new ArrayList<Template>();
			@SuppressWarnings("unchecked")
			List<Element> elements = document.selectNodes("/jfinalshopxx/template");
			for (Element element : elements) {
				Template template = getTemplate(element);
				templates.add(template);
			}
			return templates;
		} catch (Exception e) {
			log.debug("获取所有模板" + e);
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取模板
	 * 
	 * @param element
	 *            元素
	 */
	private Template getTemplate(Element element) {
		String id = element.attributeValue("id");
		String type = element.attributeValue("type");
		String name = element.attributeValue("name");
		String templatePath = element.attributeValue("templatePath");
		String staticPath = element.attributeValue("staticPath");
		String description = element.attributeValue("description");

		Template template = new Template();
		template.setId(id);
		template.setType(Type.valueOf(type));
		template.setName(name);
		template.setTemplatePath(templatePath);
		template.setStaticPath(staticPath);
		template.setDescription(description);
		return template;
	}
	
	/**
	 * 获取模板
	 * 
	 * @param id
	 *            ID
	 * @return 模板
	 */
	public Template get(String id) {
		try {
			File shopxxXmlFile = new File(PathKit.getRootClassPath() + CommonAttributes.SHOPXX_XML_PATH);
			Document document = new SAXReader().read(shopxxXmlFile);
			Element element = (Element) document.selectSingleNode("/jfinalshopxx/template[@id='" + id + "']");
			Template template = getTemplate(element);
			return template;
		} catch (Exception e) {
			log.debug("获取模板" + e);
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取模板
	 * 
	 * @param id
	 *            ID
	 * @return 模板
	 */
	public String read(String id) {
		Template template = get(id);
		return read(template);
	}
	
	/**
	 * 读取模板文件内容
	 * 
	 * @param id
	 *            ID
	 * @return 模板文件内容
	 */
	public String read(Template template) {
		String templatePath = servletContext.getRealPath(templateLoaderPaths + template.getTemplatePath());
		File templateFile = new File(templatePath);
		String templateContent = null;
		try {
			templateContent = FileUtils.readFileToString(templateFile, "UTF-8");
		} catch (IOException e) {
			log.debug("读取模板文件内容" + e);
			e.printStackTrace();
		}
		return templateContent;
	}
	
	/**
	 * 写入模板文件内容
	 * 
	 * @param id
	 *            Id
	 * @param content
	 *            模板文件内容
	 */
	public void write(String id, String content) {
		Template template = get(id);
		write(template, content);
	}

	/**
	 * 写入模板文件内容
	 * 
	 * @param template
	 *            模板
	 * @param content
	 *            模板文件内容
	 */
	public void write(Template template, String content) {
		String templatePath = servletContext.getRealPath(templateLoaderPaths + template.getTemplatePath());
		File templateFile = new File(templatePath);
		try {
			FileUtils.writeStringToFile(templateFile, content, "UTF-8");
		} catch (IOException e) {
			log.debug("写入模板文件内容" + e);
			e.printStackTrace();
		}
	}
}
