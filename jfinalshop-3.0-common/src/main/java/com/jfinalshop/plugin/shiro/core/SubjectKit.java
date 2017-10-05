package com.jfinalshop.plugin.shiro.core;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.subject.Subject;

import com.jfinal.plugin.activerecord.Model;
import com.jfinalshop.utils.EncriptionKit;


/**
 * Created by wangrenhui on 14-4-24.
 */
public class SubjectKit {

	private static String[] baseRole = new String[] { "R_ADMIN", "R_MANAGER", "R_MEMBER", "R_USER" };

	private SubjectKit() {
	}

	public static Subject getSubject() {
		return SecurityUtils.getSubject();
	}

	public static Session getSession() {
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		if (session == null) {
			throw new UnknownSessionException("Unable found required Session");
		} else {
			return session;
		}
	}

	/**
	 * 获取用户对象
	 *
	 * @param <T>
	 *            User
	 * @return T User
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Model<?>> T getAdmin() {
		Subject subject = getSubject();
		Object user = subject.getPrincipal();
		if (user == null)
			return null;
		else {
			return (T) user;
		}
	}

	/**
	 * login user
	 *
	 * @param username
	 *            用户名
	 * @param password
	 *            密码 // * @param user 完整用户对象 // * @param T User
	 * @return bolean
	 */
	public static boolean login(String username, String password) {
		return login(username, password, false);
	}

	public static boolean login(String username, String password, boolean rememberMe) {
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		try {
			token.setRememberMe(rememberMe);
			SecurityUtils.getSubject().login(token);
			return true;
		} catch (AuthenticationException e) {
			return false;
		}
	}

	/**
	 * 验证验证码
	 *
	 * @param captchaName
	 *            captchaName
	 * @param captchaToken
	 *            token
	 * @return boolean
	 */
	public static boolean doCaptcha(String captchaName, String captchaToken) {
		Session session = getSession();
		if (session.getAttribute(captchaName) != null) {
			String captcha = session.getAttribute(captchaName).toString();
			if (captchaToken != null && captcha.equalsIgnoreCase(EncriptionKit.encrypt(captchaToken))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否已经登录
	 *
	 * @return boolean
	 */
	public static boolean isAuthed() {
		Subject subject = getSubject();
		if (subject == null || subject.getPrincipal() == null || (!subject.isAuthenticated() && !subject.isRemembered())) {
			return false;
		} else
			return true;
	}

	public static boolean wasBaseRole(String roleValue) {
		if (ArrayUtils.contains(baseRole, roleValue)) {
			return true;
		}
		return false;
	}

	public String[] getBaseRole() {
		return baseRole;
	}

	public void setBaseRole(String... baseRole) {
		SubjectKit.baseRole = baseRole;
	}
	
	/**
	 * 是否管理员
	 * @return
	 */
	public static boolean hasRoleAdmin() {
		Subject subject = SubjectKit.getSubject();
		if(subject.hasRole("R_ADMIN") || subject.hasRole("R_MANAGER")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 当前是否是HR角色
	 * @return
	 */
	public static boolean hasRoleHR() {
		Subject subject = SubjectKit.getSubject();
		if(subject.hasRole("R_ADMIN") || subject.hasRole("R_HR") || subject.hasRole("R_SNOOPY")) {
			return true;
		}
		return false;
	}
	/**
	 * 当前是否是呼叫中心话务角色
	 * @return
	 */
	public static boolean hasRoleCall() {
		Subject subject = SubjectKit.getSubject();
		if(subject.hasRole("R_CALLCENTER")) {
			return true;
		}
		return false;
	}
}