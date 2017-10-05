package com.jfinalshop.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.common.Principal;
import com.jfinalshop.common.Setting;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.Deposit;
import com.jfinalshop.model.Member;
import com.jfinalshop.security.ShiroUtil;
import com.jfinalshop.utils.AssertUtil;
import com.jfinalshop.utils.SettingUtils;

/**
 * Service - 会员
 * 
 * 
 * 
 */
public class MemberService extends BaseService<Member> {

	public MemberService() {
		super(Member.class);
	}

	public static final MemberService service = new MemberService();
	
	/** "身份信息"参数名称 */
	public static final String PRINCIPAL_ATTRIBUTE_NAME = MemberInterceptor.class.getName() + ".PRINCIPAL";
	
			
	/**
	 * 判断用户名是否存在
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 用户名是否存在
	 */
	public boolean usernameExists(String username) {
		return Member.dao.usernameExists(username);
	}

	/**
	 * 判断用户名是否禁用
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 用户名是否禁用
	 */
	public boolean usernameDisabled(String username) {
		AssertUtil.hasText(username);
		Setting setting = SettingUtils.get();
		if (setting.getDisabledUsernames() != null) {
			for (String disabledUsername : setting.getDisabledUsernames()) {
				if (StringUtils.containsIgnoreCase(username, disabledUsername)) {
					return true;
				}
			}
		}
		return false;
	}
	

	/**
	 * 判断E-mail是否存在
	 * 
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return E-mail是否存在
	 */
	public boolean emailExists(String email) {
		return Member.dao.emailExists(email);
	}

	/**
	 * 判断E-mail是否唯一
	 * 
	 * @param previousEmail
	 *            修改前E-mail(忽略大小写)
	 * @param currentEmail
	 *            当前E-mail(忽略大小写)
	 * @return E-mail是否唯一
	 */
	public boolean emailUnique(String previousEmail, String currentEmail) {
		if (StringUtils.equalsIgnoreCase(previousEmail, currentEmail)) {
			return true;
		} else {
			if (Member.dao.emailExists(currentEmail)) {
				return false;
			} else {
				return true;
			}
		}
	}

	/**
	 * 保存会员
	 * 
	 * @param member
	 *            会员
	 * @param operator
	 *            操作员
	 */
	public void save(Member member, Admin operator) {
		AssertUtil.notNull(member);
		member.setCreateBy(ShiroUtil.getName());
		member.setCreationDate(new Date());
		member.setDeleteFlag(false);
		member.save();
		if (member.getBalance().compareTo(new BigDecimal(0)) > 0) {
			Deposit deposit = new Deposit();
			deposit.setType(operator != null ? Deposit.Type.adminRecharge.ordinal() : Deposit.Type.memberRecharge.ordinal());
			deposit.setCredit(member.getBalance());
			deposit.setDebit(new BigDecimal(0));
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername() : null);
			deposit.setMemberId(member.getId());
			deposit.setCreateBy(ShiroUtil.getName());
			deposit.setCreationDate(new Date());
			deposit.setDeleteFlag(false);
			deposit.save();
		}
	}

	/**
	 * 更新会员
	 * 
	 * @param member
	 *            会员
	 * @param modifyPoint
	 *            修改积分
	 * @param modifyBalance
	 *            修改余额
	 * @param depositMemo
	 *            修改余额备注
	 * @param operator
	 *            操作员
	 */
	public void update(Member member, Integer modifyPoint, BigDecimal modifyBalance, String depositMemo, Admin operator) {
		AssertUtil.notNull(member);
		if (modifyPoint != null && modifyPoint != 0 && member.getPoint() + modifyPoint >= 0) {
			member.setPoint(member.getPoint() + modifyPoint);
		}
		if (modifyBalance != null && modifyBalance.compareTo(new BigDecimal(0)) != 0 && member.getBalance().add(modifyBalance).compareTo(new BigDecimal(0)) >= 0) {
			member.setBalance(member.getBalance().add(modifyBalance));
			Deposit deposit = new Deposit();
			if (modifyBalance.compareTo(new BigDecimal(0)) > 0) {
				deposit.setType(operator != null ? Deposit.Type.adminRecharge.ordinal() : Deposit.Type.memberRecharge.ordinal());
				deposit.setCredit(modifyBalance);
				deposit.setDebit(new BigDecimal(0));
			} else {
				deposit.setType(operator != null ? Deposit.Type.adminChargeback.ordinal() : Deposit.Type.memberPayment.ordinal());
				deposit.setCredit(new BigDecimal(0));
				deposit.setDebit(modifyBalance);
			}
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername() : null);
			deposit.setMemo(depositMemo);
			deposit.setMemberId(member.getId());
			deposit.setLastUpdatedBy(ShiroUtil.getName());
			deposit.setLastUpdatedDate(new Date());
			deposit.update();
		}
		member.setLastUpdatedBy(ShiroUtil.getName());
		member.setLastUpdatedDate(new Date());
		member.update();
	}

	/**
	 * 根据用户名查找会员
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 会员，若不存在则返回null
	 */
	public Member findByUsername(String username) {
		return Member.dao.findByUsername(username);
	}

	/**
	 * 根据E-mail查找会员
	 * 
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return 会员，若不存在则返回null
	 */
	public List<Member> findListByEmail(String email) {
		return Member.dao.findListByEmail(email);
	}

	/**
	 * 查找会员消费信息
	 * 
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @param count
	 *            数量
	 * @return 会员消费信息
	 */
	public List<Object[]> findPurchaseList(Date beginDate, Date endDate,Integer count) {
		return Member.dao.findPurchaseList(beginDate, endDate, count);
	}

	/**
	 * 判断会员是否登录
	 * 
	 * @return 会员是否登录
	 */
	public boolean isAuthenticated(HttpServletRequest request) {
		Principal principal = (Principal) request.getSession().getAttribute(PRINCIPAL_ATTRIBUTE_NAME);
		if (principal != null) {
			return true;
		}
		return false;
	}

	/**
	 * 获取当前登录会员
	 * 
	 * @return 当前登录会员，若不存在则返回null
	 */
	public Member getCurrent(HttpServletRequest request) {
		if (request != null) {
			Principal principal = (Principal) request.getSession().getAttribute(PRINCIPAL_ATTRIBUTE_NAME);
			if (principal != null) {
				return Member.dao.findById(principal.getId());
			}
		}
		return null;
	}

	/**
	 * 获取当前登录用户名
	 * 
	 * @return 当前登录用户名，若不存在则返回null
	 */
	public String getCurrentUsername(HttpServletRequest request) {
		if (request != null) {
			Principal principal = (Principal) request.getSession().getAttribute(PRINCIPAL_ATTRIBUTE_NAME);
			if (principal != null) {
				return principal.getUsername();
			}
		}
		return null;
	}
	
}
