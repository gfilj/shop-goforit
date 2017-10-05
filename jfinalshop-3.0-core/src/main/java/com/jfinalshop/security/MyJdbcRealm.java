package com.jfinalshop.security;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

import com.jfinal.log.Logger;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.Permission;
import com.jfinalshop.model.Role;
import com.jfinalshop.plugin.shiro.core.SubjectKit;


/**
 * Created by wangrenhui on 14-1-3.
 */
public class MyJdbcRealm extends AuthorizingRealm {
	
	public final Logger log = Logger.getLogger(getClass());
	/**
	 * 登录认证
	 *
	 * @param token
	 * @return
	 * @throws org.apache.shiro.authc.AuthenticationException
	 */
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken userToken = (UsernamePasswordToken) token;
		Admin admin = null;
		String username = userToken.getUsername();
		admin = Admin.dao.getAdminByLoginName(username);
		
		if (admin != null && admin.getIsEnabled()) {
			SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(admin, admin.getPassword(), getName());
			return info;
		} else {
			return null;
		}
	
	}

	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
	 *
	 * @param principals
	 *            用户信息
	 * @return
	 */
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		String loginName = ((Admin) principals.fromRealm(getName()).iterator().next()).getUsername();
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		Set<String> roleSet = new LinkedHashSet<String>(); // 角色集合
		Set<String> permissionSet = new LinkedHashSet<String>(); // 权限集合
		List<Role> roles = null;
		Admin admin = Admin.dao.getAdminByLoginName(loginName);
		if (admin != null) {
			// 遍历角色
			roles = Role.dao.getRoleByAdminId(admin.getId());
		} else {
			SubjectKit.getSubject().logout();
		}

		loadRole(roleSet, permissionSet, roles);
		info.setRoles(roleSet); // 设置角色
		info.setStringPermissions(permissionSet); // 设置权限
		return info;
	}

	/**
	 * @param roleSet
	 * @param permissionSet
	 * @param roles
	 */
	private void loadRole(Set<String> roleSet, Set<String> permissionSet,List<Role> roles) {
		List<Permission> permissions;
		for (Role role : roles) {
			// 角色可用
			if (role.getDeleteFlag() == false) {
				roleSet.add(role.getValue());
				permissions = Permission.dao.getPermissionByRoleId(role.getId());
				loadAuth(permissionSet, permissions);
			}
		}
	} 

	/**  
	 * @param permissionSet
	 * @param permissions
	 */
	private void loadAuth(Set<String> permissionSet,List<Permission> permissions) {
		// 遍历权限
		for (Permission permission : permissions) {
			// 权限可用
			if (permission.getDeleteFlag() == false) {
				permissionSet.add(permission.getValue());
			}
		}
	}

	/**
	 * 更新用户授权信息缓存.
	 */
	public void clearCachedAuthorizationInfo(Object principal) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
		clearCachedAuthorizationInfo(principals);
	}

	/**
	 * 清除所有用户授权信息缓存.
	 */
	public void clearAllCachedAuthorizationInfo() {
		Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
		if (cache != null) {
			for (Object key : cache.keys()) {
				cache.remove(key);
			}
		}
	}
}