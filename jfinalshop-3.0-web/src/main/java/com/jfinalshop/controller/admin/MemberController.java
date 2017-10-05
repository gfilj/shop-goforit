package com.jfinalshop.controller.admin;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Member.Gender;
import com.jfinalshop.service.MemberAttributeService;
import com.jfinalshop.service.MemberRankService;
import com.jfinalshop.service.MemberService;

/**
 * Controller - 会员
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/member")
public class MemberController extends BaseAdminController {

	private MemberService memberService = enhance(MemberService.class);
	private MemberRankService memberRankService = enhance(MemberRankService.class);
	private MemberAttributeService memberAttributeService = enhance(MemberAttributeService.class);
	
	/**
	 * 检查用户名是否被禁用或已存在
	 */
	public void checkUsername(String username) {
		if (StringUtils.isEmpty(username)) {
			renderJson(false);
		}
		if (memberService.usernameDisabled(username) || memberService.usernameExists(username)) {
			renderJson(false);
		} else {
			renderJson(true);
		}
	}
	
	/**
	 * 检查E-mail是否唯一
	 */
	public void checkEmail() {
		String previousEmail = getPara("previousEmail");
		String email = getPara("email");
		if (StringUtils.isEmpty(email)) {
			renderJson(false);
		}
		if (memberService.emailUnique(previousEmail, email)) {
			renderJson(true);
		} else {
			renderJson(false);
		}
	}
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("memberRanks", memberRankService.findAll());
		setAttr("memberAttributes", memberAttributeService.findAll());
		setAttr("page", memberService.findPage(pageable));
		setAttr("pageable", pageable);
		render("/admin/member/list.html");
	}
	
	
	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		setAttr("genders", Gender.values());
		setAttr("memberAttributes", memberAttributeService.findList());
		setAttr("member", memberService.find(id));
		render("/admin/member/view.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		setAttr("genders", Gender.values());
		setAttr("memberRanks", memberRankService.findAll());
		setAttr("memberAttributes", memberAttributeService.findList());
		render("/admin/member/add.html");
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("genders", Gender.values());
		setAttr("memberRanks", memberRankService.findAll());
		setAttr("memberAttributes", memberAttributeService.findList());
		setAttr("member", memberService.find(id));
		render("/admin/member/edit.html");
	}
	
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			for (Long id : ids) {
				Member member = memberService.find(id);
				if (member != null && member.getBalance().compareTo(new BigDecimal(0)) > 0) {
					renderJson(Message.error("admin.member.deleteExistDepositNotAllowed", member.getUsername()));
				}
			}
			memberService.delete(ids);
		}
		renderJson(SUCCESS_MESSAGE);
	}
}
