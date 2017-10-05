package com.jfinalshop.cfg;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.route.AutoBindRoutes;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.plugin.druid.IDruidStatViewAuth;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.FreeMarkerRender;
import com.jfinalshop.model._MappingKit;
import com.jfinalshop.plugin.shiro.core.ShiroInterceptor;
import com.jfinalshop.plugin.shiro.core.ShiroPlugin;
import com.jfinalshop.plugin.shiro.core.SubjectKit;
import com.jfinalshop.plugin.shiro.freemarker.ShiroTags;
import com.jfinalshop.security.MyJdbcAuthzService;
import com.jfinalshop.template.directive.AdPositionDirective;
import com.jfinalshop.template.directive.ArticleCategoryChildrenListDirective;
import com.jfinalshop.template.directive.ArticleCategoryParentListDirective;
import com.jfinalshop.template.directive.ArticleCategoryRootListDirective;
import com.jfinalshop.template.directive.ArticleListDirective;
import com.jfinalshop.template.directive.BrandListDirective;
import com.jfinalshop.template.directive.ConsultationListDirective;
import com.jfinalshop.template.directive.CurrentMemberDirective;
import com.jfinalshop.template.directive.FlashMessageDirective;
import com.jfinalshop.template.directive.FriendLinkListDirective;
import com.jfinalshop.template.directive.MemberAttributeListDirective;
import com.jfinalshop.template.directive.NavigationListDirective;
import com.jfinalshop.template.directive.PaginationDirective;
import com.jfinalshop.template.directive.ProductCategoryChildrenListDirective;
import com.jfinalshop.template.directive.ProductCategoryParentListDirective;
import com.jfinalshop.template.directive.ProductCategoryRootListDirective;
import com.jfinalshop.template.directive.ProductListDirective;
import com.jfinalshop.template.directive.PromotionListDirective;
import com.jfinalshop.template.directive.ReviewListDirective;
import com.jfinalshop.template.directive.SeoDirective;
import com.jfinalshop.template.directive.TagListDirective;
import com.jfinalshop.template.method.AbbreviateMethod;
import com.jfinalshop.template.method.CurrencyMethod;
import com.jfinalshop.template.method.MessageMethod;
import com.jfinalshop.utils.SettingUtils;

import freemarker.template.Configuration;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.StandardCompress;

public class JFWebConfig extends JFinalConfig {

	/**
	 * 供Shiro插件使用。
	 */
	Routes routes;

	@Override
	public void configConstant(Constants constants) {
		loadPropertyFile("application.properties");
		constants.setDevMode(getPropertyToBoolean("devMode", false));
		constants.setEncoding("UTF-8");
		constants.setI18nDefaultBaseName("i18n");
		constants.setI18nDefaultLocale(getProperty("locale"));
		constants.setError401View("/admin/common/error.html");
		constants.setError403View("/admin/common/unauthorized.html");
		constants.setError404View("/admin/common/error.html");
		constants.setError500View("/admin/common/error.html");
	}

	@Override
	public void configRoute(Routes routes) {
		this.routes = routes;
		routes.add(new AutoBindRoutes());
	}

	@Override
	public void configPlugin(Plugins plugins) {
		//String publicKey = getProperty("jdbc.publicKey");
		//String password = getProperty("jdbc.password");
		
		//配置druid连接池
		DruidPlugin druidDefault = new DruidPlugin(
		getProperty("jdbc.url"), 
		getProperty("jdbc.username"),
		//EncriptionKit.passwordDecrypt(publicKey, password),
		getProperty("jdbc.password"),
		getProperty("jdbc.driver"));
		// StatFilter提供JDBC层的统计信息
		druidDefault.addFilter(new StatFilter());

		// WallFilter的功能是防御SQL注入攻击
		WallFilter wallDefault = new WallFilter();

		wallDefault.setDbType("mysql");
		druidDefault.addFilter(wallDefault);
		
		druidDefault.setInitialSize(getPropertyToInt("db.default.poolInitialSize"));
		druidDefault.setMaxPoolPreparedStatementPerConnectionSize(getPropertyToInt("db.default.poolMaxSize"));
		druidDefault.setTimeBetweenConnectErrorMillis(getPropertyToInt("db.default.connectionTimeoutMillis"));
		plugins.add(druidDefault);

		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidDefault);
		plugins.add(arp);
		// 配置属性名(字段名)大小写不敏感容器工厂
		arp.setContainerFactory(new CaseInsensitiveContainerFactory());
		// 显示SQL
		arp.setShowSql(true);
		// 所有配置在 MappingKit 中搞定
		_MappingKit.mapping(arp);
		//shiro权限框架，添加到plugin
		plugins.add(new ShiroPlugin(routes,new MyJdbcAuthzService()));
		
		//ehcache缓存
		plugins.add(new EhCachePlugin());
		
		// 用于缓存news模块的redis服务
//	    String cacheName = getProperty("redis.cacheName");
//	    String host = getProperty("redis.host");
//	    int port = getPropertyToInt("redis.port");
//	    int timeout = getPropertyToInt("redis.timeout");
//		RedisPlugin sessionRedis = new RedisPlugin(cacheName, host, port, timeout);
//		plugins.add(sessionRedis);
		
		//Mongodb插件
		//plugins.add(new MongodbPlugin("192.168.9.43", 27017, "test"));
		
	}

	@Override
	public void configInterceptor(Interceptors interceptors) {
		// 添加shiro的过滤器到interceptor
		interceptors.add(new ShiroInterceptor());
	}

	@Override
	public void configHandler(Handlers handlers) {
		DruidStatViewHandler dvh = new DruidStatViewHandler("/druid", new IDruidStatViewAuth() {
			public boolean isPermitted(HttpServletRequest request) {
				if (SubjectKit.hasRoleAdmin()) {
					return true;
				} else {
					return false;
				}
			}
		});
		handlers.add(dvh);
	}

	@Override
    public void afterJFinalStart() {
        try {
        	Configuration cf = FreeMarkerRender.getConfiguration();
        	Map<String, Object> map = new HashMap<String, Object>();
        	map.put("base", JFinal.me().getContextPath());
			map.put("locale", getProperty("locale"));
			map.put("setting", SettingUtils.get());
        	map.put("systemName", getProperty("system.name"));
        	map.put("systemVersion", getProperty("system.version"));
        	map.put("systemDescription", getProperty("system.description"));
        	map.put("systemShowPowered", getProperty("system.show_powered"));
        	map.put("message", new MessageMethod());
        	map.put("abbreviate", new AbbreviateMethod());
        	map.put("currency", new CurrencyMethod());
        	map.put("flash_message", new FlashMessageDirective());
        	map.put("current_member", new CurrentMemberDirective());
        	map.put("pagination", new PaginationDirective());
        	map.put("seo", new SeoDirective());
        	map.put("ad_position", new AdPositionDirective());
        	map.put("member_attribute_list", new MemberAttributeListDirective());
        	map.put("navigation_list", new NavigationListDirective());
        	map.put("tag_list", new TagListDirective());
        	map.put("friend_link_list", new FriendLinkListDirective());
        	map.put("brand_list", new BrandListDirective());
        	map.put("article_list", new ArticleListDirective());
        	map.put("article_category_root_list", new ArticleCategoryRootListDirective());
        	map.put("article_category_parent_list", new ArticleCategoryParentListDirective());
        	map.put("article_category_children_list", new ArticleCategoryChildrenListDirective());
        	map.put("product_list", new ProductListDirective());
        	map.put("product_category_root_list", new ProductCategoryRootListDirective());
        	map.put("product_category_parent_list", new ProductCategoryParentListDirective());
        	map.put("product_category_children_list", new ProductCategoryChildrenListDirective());
        	map.put("review_list", new ReviewListDirective());
        	map.put("consultation_list", new ConsultationListDirective());
        	map.put("promotion_list", new PromotionListDirective());
        	map.put("compress", StandardCompress.INSTANCE);
        	cf.setSharedVaribles(map);
        	
        	cf.setDefaultEncoding(getProperty("template.encoding"));
        	cf.setURLEscapingCharset(getProperty("url_escaping_charset"));
        	cf.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX);
        	cf.setWhitespaceStripping(true);
        	cf.setClassicCompatible(true);
        	cf.setNumberFormat(getProperty("template.number_format"));
        	cf.setBooleanFormat(getProperty("template.boolean_format"));
        	cf.setDateTimeFormat(getProperty("template.datetime_format"));
        	cf.setDateFormat(getProperty("template.date_format"));
        	cf.setTimeFormat(getProperty("template.time_format"));
        	
        	cf.setSharedVariable("shiro", new ShiroTags());
        	cf.setServletContextForTemplateLoading(JFinal.me().getServletContext(), getProperty("template.loader_path"));
        } catch (TemplateModelException e) {
            e.printStackTrace();
        }
        super.afterJFinalStart();
    }
}
