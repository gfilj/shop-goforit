package com.jfinalshop.security;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.jfinalshop.model.Permission;
import com.jfinalshop.model.Role;
import com.jfinalshop.plugin.shiro.core.JdbcAuthzService;
import com.jfinalshop.plugin.shiro.core.handler.AuthzHandler;
import com.jfinalshop.plugin.shiro.core.handler.JdbcPermissionAuthzHandler;

/**
 * Created by wangrenhui on 14-1-7.
 */
public class MyJdbcAuthzService implements JdbcAuthzService {
	
  @Override
  public Map<String, AuthzHandler> getJdbcAuthz() {
    //加载数据库的url配置
    //按长度倒序排列
    Map<String, AuthzHandler> authzJdbcMaps = Collections.synchronizedMap(new TreeMap<String, AuthzHandler>(
        new Comparator<String>() {
          public int compare(String k1, String k2) {
            int result = k2.length() - k1.length();
            if (result == 0) {
              return k1.compareTo(k2);
            }
            return result;
          }
        }));
    //遍历角色
    List<Role> roles = Role.dao.getAll();
    for (Role role : roles) {
      //角色可用
      if (role.getDeleteFlag() == false) {
    	List<Permission> permissions = Permission.dao.getPermissionByRoleId(role.getId());
        //遍历权限
        for (Permission permission : permissions) {
          //权限可用
          if (permission.getDeleteFlag() == false) {
            if (permission.getUrl() != null && !permission.getUrl().isEmpty()) {
            	authzJdbcMaps.put(permission.getUrl(), new JdbcPermissionAuthzHandler(permission.getValue()));
            }
          } 
        }
      }
    }
    return authzJdbcMaps;
  }
}
