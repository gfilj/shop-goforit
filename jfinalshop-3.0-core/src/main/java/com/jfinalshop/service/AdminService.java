package com.jfinalshop.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.AdminRole;
import com.jfinalshop.plugin.shiro.core.SubjectKit;
import com.jfinalshop.plugin.shiro.hasher.Hasher;
import com.jfinalshop.plugin.shiro.hasher.HasherInfo;
import com.jfinalshop.plugin.shiro.hasher.HasherKit;
import com.jfinalshop.security.ShiroUtil;

/**
 * Service - 管理员
 * 
 * 
 * 
 */
public class AdminService extends BaseService<Admin> {
	
	private static Logger log = Logger.getLogger(AdminService.class);
	
	public static final String STATUS = "status";
	public static final String WARN = "warn";
	public static final String SUCCESS = "success";
	public static final String ERROR = "error";
	public static final String MESSAGE = "message";
	public static final String CONTENT = "content";
	
	
	public AdminService() {
		super(Admin.class);
	}
	
	/**
	 * 登陆验证
	 * @param loginName
	 * @param password
	 * @param rememberMe
	 * @param captchaToken
	 * @return
	 */
	public Map<String, String> login(String loginName, String password, boolean rememberMe, String captchaToken) {
		Map<String, String> map = new HashMap<String, String>();
		if (!SubjectKit.doCaptcha("captcha", captchaToken)) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "温馨提示：您录入的验证码错误!");
			return map;
		} else 
		if (SubjectKit.login(loginName, password, rememberMe)) {
			map.put(STATUS, SUCCESS);
			map.put(MESSAGE, "温馨提示：用户登录成功!");
			log.info("用户【" + loginName + "】用户登录成功......");
			return map;
		} else {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "温馨提示：您的用户名或密码错误!");
			return map;
		}
	}
	
	/**
	 * 检查用户是否存在
	 * 
	 * @param username
	 * @return
	 */
	public boolean usernameExists(String username) {
		return Admin.dao.usernameExists(username);
	}

	/**
	 * 根据登录名查找
	 * 
	 * @param username
	 * @return
	 */
	public Admin findByUsername(String username) {
		return Admin.dao.findByUsername(username);
	}

	/**
	 * 保存
	 * 
	 * @param admin
	 * @param roleIds
	 * @return
	 */
	@Before(Tx.class)
	public boolean save(Admin admin) {
		boolean result = false;
		HasherInfo passwordInfo = HasherKit.hash(admin.getPassword(), Hasher.DEFAULT);
		admin.setUsername(admin.getUsername().toLowerCase());
		admin.setPassword(passwordInfo.getHashResult());
		admin.setHasher(passwordInfo.getHasher().value());
		admin.setSalt(passwordInfo.getSalt());
		admin.setLoginFailureCount(0);
		admin.setCreateBy(ShiroUtil.getName());
		admin.setCreationDate(new Date());
		admin.setDeleteFlag(false);
		admin.setIsLocked(false);
		admin.setLoginFailureCount(0);
		admin.setLockedDate(null);
		admin.setLoginDate(null);
		admin.setLoginIp(null);
		result = admin.save();
		AdminRoleService.service.save(admin);
		return result;
	}

	/**
	 * 更新
	 * 
	 * @param admin
	 * @return
	 */
	@Before(Tx.class)
	public boolean update(Admin admin) {
		boolean result = false;
		Admin pAdmin = find(admin.getId());
		if (pAdmin == null) {
			return false;
		}
		if (StringUtils.isNotEmpty(admin.getPassword())) {
			HasherInfo passwordInfo = HasherKit.hash(admin.getPassword(), Hasher.DEFAULT);
			admin.setPassword(passwordInfo.getHashResult());
			admin.setHasher(passwordInfo.getHasher().value());
			admin.setSalt(passwordInfo.getSalt());
		} else {
			admin.remove("password");
		}
		if (pAdmin.getIsLocked() && !admin.getIsLocked()) {
			admin.setLoginFailureCount(0);
			admin.setLockedDate(null);
		} else {
			admin.remove("is_locked");
			admin.remove("login_failure_count");
			admin.remove("locked_date");
		}
		admin.remove("username");
		admin.remove("loginDate");
		admin.remove("loginIp");
		admin.setLastUpdatedBy(ShiroUtil.getName());
		admin.setLastUpdatedDate(new Date());
		result = admin.update();
		AdminRoleService.service.update(admin);
		return result;
	}

	
	/**
	 * 删除
	 * 
	 * @param ids
	 * @return
	 */
	@Before(Tx.class)
	public boolean delete(Long[] ids) {
		boolean result = false;
		for (Long id : ids) {
			AdminRole.dao.deleteRole(id);
			result = Admin.dao.deleteById(id);
		}
		return result;
	}

}
