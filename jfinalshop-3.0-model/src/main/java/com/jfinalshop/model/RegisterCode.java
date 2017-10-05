package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseRegisterCode;

/**
 * Dao - 注册验证码
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class RegisterCode extends BaseRegisterCode<RegisterCode> {
	public static final RegisterCode dao = new RegisterCode();
	
	/**
     * @Description: 检查验证码是否有效
     * @param @param loginName
     * @param @param code
     * @param @return   
     * @return boolean
     * @author 李红元
     * @date 2015-7-8 下午3:46:08
     */
	public boolean invalid(String loginName, int code, String op) {
		return false;
		//return dao.countBy("`registerCode`.mobile = ? AND `registerCode`.code = ? AND `registerCode`.op = ?", loginName, code, op) == 0L;
	}
    
    /**
     * 
     * @Description: 删除验证码记录
     * @param @param loginName
     * @param @param code
     * @param @return   
     * @return boolean
     * @author 李红元
     * @date 2015-7-8 下午4:03:24
     */
    public boolean deleteCode(String loginName,int code){
		return Db.update("DELETE FROM register_code WHERE mobile= ? AND code = ?", loginName, code) > 0;
    }
    
	 /**
     * @Description: 检测手机号是否存在验证码
     * @param @return   
     * @return boolean
     * @author 李红元
     * @date 2015-7-8 下午5:04:23
     */
    public boolean isExists(String mobile) {
		return Db.findFirst("SELECT * FROM register_code WHERE mobile=?", mobile) == null;
    }
}
