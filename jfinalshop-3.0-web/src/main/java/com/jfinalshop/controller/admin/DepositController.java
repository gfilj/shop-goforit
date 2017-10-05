package com.jfinalshop.controller.admin;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.DepositService;
import com.jfinalshop.service.MemberService;

/**
 * Controller - 预存款
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/deposit")
public class DepositController extends BaseAdminController {

	private DepositService depositService = enhance(DepositService.class);
	private MemberService memberService = enhance(MemberService.class);
	
	/**
	 * 列表
	 */
	public void list() {
		Long memberId = getParaToLong("memberId");
		Member member = memberService.find(memberId);
		Pageable pageable = getBean(Pageable.class);
		if (member != null) {
			setAttr("member", member);
			setAttr("page", depositService.findPage(member, pageable));
		} else {
			setAttr("page", depositService.findPage(pageable));
		}
		setAttr("pageable", pageable);
		render("/admin/deposit/list.html");
	}
}
