/*
 * 
 * 
 * 
 */
package com.jfinalshop.template.directive;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.render.FreeMarkerRender;
import com.jfinalshop.model.AdPosition;
import com.jfinalshop.service.AdPositionService;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 广告位
 * 
 * 
 * 
 */
public class AdPositionDirective extends BaseDirective {

	/** 变量名称 */
	private static final String VARIABLE_NAME = "adPosition";

	private Configuration freeMarkerConfigurer = FreeMarkerRender.getConfiguration();
	private AdPositionService adPositionService = new AdPositionService();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		AdPosition adPosition;
		Long id = getId(params);
		boolean useCache = useCache(env, params);
		String cacheRegion = getCacheRegion(env, params);
		if (useCache) {
			adPosition = adPositionService.find(id, cacheRegion);
		} else {
			adPosition = adPositionService.find(id);
		}
		if (body != null) {
			setLocalVariable(VARIABLE_NAME, adPosition, env, body);
		} else {
			if (adPosition != null && adPosition.getTemplate() != null) {
				try {
					Map<String, Object> model = new HashMap<String, Object>();
					model.put(VARIABLE_NAME, adPosition);
					Writer out = env.getOut();
					new Template("adTemplate", new StringReader(adPosition.getTemplate()), freeMarkerConfigurer).process(model, out);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}