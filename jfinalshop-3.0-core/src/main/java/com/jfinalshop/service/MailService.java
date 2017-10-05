package com.jfinalshop.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeUtility;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinalshop.common.Setting;
import com.jfinalshop.model.ProductNotify;
import com.jfinalshop.model.SafeKey;
import com.jfinalshop.utils.AssertUtil;
import com.jfinalshop.utils.SettingUtils;

/**
 * Service - 邮件
 * 
 * 
 * 
 */
public class MailService {
	
	private Email javaMailSender = new SimpleEmail();
	//private TaskExecutor taskExecutor;
	private TemplateService templateService = new TemplateService();
	private Res resZh = I18n.use();
	
	/**
	 * 添加邮件发送任务
	 * 
	 * @param mimeMessage
	 *            MimeMessage
	 */
//	private void addSendTask(final MimeMessage mimeMessage) {
//		try {
//			taskExecutor.execute(new Runnable() {
//				public void run() {
//					javaMailSender.send(mimeMessage);
//				}
//			});
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	
	/**
	 * 发送邮件
	 * 
	 * @param smtpFromMail
	 *            发件人邮箱
	 * @param smtpHost
	 *            SMTP服务器地址
	 * @param smtpPort
	 *            SMTP服务器端口
	 * @param smtpUsername
	 *            SMTP用户名
	 * @param smtpPassword
	 *            SMTP密码
	 * @param toMail
	 *            收件人邮箱
	 * @param subject
	 *            主题
	 * @param templatePath
	 *            模板路径
	 * @param model
	 *            数据
	 * @param async
	 *            是否异步
	 */
	public void send(String smtpFromMail, String smtpHost, Integer smtpPort, String smtpUsername, String smtpPassword, String toMail, String subject, String templatePath, Map<String, Object> model, boolean async) {
		AssertUtil.hasText(smtpFromMail);
		AssertUtil.hasText(smtpHost);
		AssertUtil.notNull(smtpPort);
		AssertUtil.hasText(smtpUsername);
		AssertUtil.hasText(smtpPassword);
		AssertUtil.hasText(toMail);
		AssertUtil.hasText(subject);
		AssertUtil.hasText(templatePath);
		try {
			Setting setting = SettingUtils.get();
			//Configuration configuration = FreeMarkerRender.getConfiguration();
			// Template template = configuration.getTemplate(templatePath);
			// String text = FreemarkerUtils.processTemplateIntoString(template, model);
			javaMailSender.setHostName(smtpHost);
			javaMailSender.setSmtpPort(smtpPort);
			javaMailSender.setAuthentication(smtpUsername, smtpPassword);
			javaMailSender.setFrom(MimeUtility.encodeWord(setting.getSiteName()) + " <" + smtpFromMail + ">");
			javaMailSender.setSubject(subject);
			javaMailSender.addTo(toMail);
			javaMailSender.setMsg("");
			if (async) {
				//addSendTask(mimeMessage);
			} else {
				javaMailSender.send();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (EmailException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 发送邮件
	 * 
	 * @param toMail
	 *            收件人邮箱
	 * @param subject
	 *            主题
	 * @param templatePath
	 *            模板路径
	 * @param model
	 *            数据
	 * @param async
	 *            是否异步
	 */
	public void send(String toMail, String subject, String templatePath, Map<String, Object> model, boolean async) {
		Setting setting = SettingUtils.get();
		send(setting.getSmtpFromMail(), setting.getSmtpHost(), setting.getSmtpPort(), setting.getSmtpUsername(), setting.getSmtpPassword(), toMail, subject, templatePath, model, async);
	}

	/**
	 * 发送邮件(异步)
	 * 
	 * @param toMail
	 *            收件人邮箱
	 * @param subject
	 *            主题
	 * @param templatePath
	 *            模板路径
	 * @param model
	 *            数据
	 */
	public void send(String toMail, String subject, String templatePath, Map<String, Object> model) {
		Setting setting = SettingUtils.get();
		send(setting.getSmtpFromMail(), setting.getSmtpHost(), setting.getSmtpPort(), setting.getSmtpUsername(), setting.getSmtpPassword(), toMail, subject, templatePath, model, true);
	}

	/**
	 * 发送邮件(异步)
	 * 
	 * @param toMail
	 *            收件人邮箱
	 * @param subject
	 *            主题
	 * @param templatePath
	 *            模板路径
	 */
	public void send(String toMail, String subject, String templatePath) {
		Setting setting = SettingUtils.get();
		send(setting.getSmtpFromMail(), setting.getSmtpHost(), setting.getSmtpPort(), setting.getSmtpUsername(), setting.getSmtpPassword(), toMail, subject, templatePath, null, true);
	}

	/**
	 * 发送测试邮件
	 * 
	 * @param smtpFromMail
	 *            发件人邮箱
	 * @param smtpHost
	 *            SMTP服务器地址
	 * @param smtpPort
	 *            SMTP服务器端口
	 * @param smtpUsername
	 *            SMTP用户名
	 * @param smtpPassword
	 *            SMTP密码
	 * @param toMail
	 *            收件人邮箱
	 */
	public void sendTestMail(String smtpFromMail, String smtpHost, Integer smtpPort, String smtpUsername, String smtpPassword, String toMail) {
		Setting setting = SettingUtils.get();
		//String subject = SpringUtils.getMessage("admin.setting.testMailSubject", setting.getSiteName());
		String subject = resZh.format("admin.setting.testMailSubject", setting.getSiteName());
		com.jfinalshop.common.Template testMailTemplate = templateService.get("testMail");
		send(smtpFromMail, smtpHost, smtpPort, smtpUsername, smtpPassword, toMail, subject, testMailTemplate.getTemplatePath(), null, false);
	}

	/**
	 * 发送找回密码邮件
	 * 
	 * @param toMail
	 *            收件人邮箱
	 * @param username
	 *            用户名
	 * @param safeKey
	 *            安全密匙
	 */
	public void sendFindPasswordMail(String toMail, String username, SafeKey safeKey) {
		Setting setting = SettingUtils.get();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("username", username);
		model.put("safeKey", safeKey);
		//String subject = SpringUtils.getMessage("shop.password.mailSubject", setting.getSiteName());
		String subject = resZh.format("shop.password.mailSubject", setting.getSiteName());
		com.jfinalshop.common.Template findPasswordMailTemplate = templateService.get("findPasswordMail");
		send(toMail, subject, findPasswordMailTemplate.getTemplatePath(), model);
	}

	/**
	 * 发送到货通知邮件
	 * 
	 * @param productNotify
	 *            到货通知
	 */
	public void sendProductNotifyMail(ProductNotify productNotify) {
		Setting setting = SettingUtils.get();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("productNotify", productNotify);
		//String subject = SpringUtils.getMessage("admin.productNotify.mailSubject", setting.getSiteName());
		String subject = resZh.format("admin.productNotify.mailSubject", setting.getSiteName());
		com.jfinalshop.common.Template productNotifyMailTemplate = templateService.get("productNotifyMail");
		send(productNotify.getEmail(), subject, productNotifyMailTemplate.getTemplatePath(), model);
	}
}
