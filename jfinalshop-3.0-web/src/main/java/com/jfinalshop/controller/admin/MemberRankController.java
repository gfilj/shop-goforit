package com.jfinalshop.controller.admin;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.service.MemberRankService;

/**
 * Controller - 会员等级
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/memberRank")
public class MemberRankController extends BaseAdminController {

	private MemberRankService memberRankService = enhance(MemberRankService.class);
	private MemberRank memberRank;
	
	/**
	 * 检查名称是否唯一
	 */
	public void checkName() {
		String previousName = getPara("previousName");
		String name = getPara("memberRank.name");
		if (StringUtils.isEmpty(name)) {
			renderJson(false);
			return;
		}
		if (memberRankService.nameUnique(previousName, name)) {
			renderJson(true);
			return;
		} else {
			renderJson(false);
			return;
		}
	}
	
	/**
	 * 检查消费金额是否唯一
	 */
	public void checkAmount() {
		BigDecimal previousAmount = new BigDecimal(getPara("previousAmount", "0"));  
		BigDecimal amount = new BigDecimal(getPara("memberRank.amount", "0"));    
		if (memberRankService.amountUnique(previousAmount, amount)) {
			renderJson(true);
			return;
		} else {
			renderJson(false);
			return;
		}
	}
	
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Page<MemberRank> page = memberRankService.findPage(pageable);
		setAttr("page", page);
		setAttr("pageable", pageable);
		render("/admin/member_rank/list.html");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		render("/admin/member_rank/add.html");
	}
	
	
	/**
	 * 保存
	 */
	public void save() {
		memberRank = getModel(MemberRank.class);
		if (memberRankService.nameExists(memberRank.getName())) {
			renderJson(ERROR_VIEW);
			return;
		}
		if (memberRank.getIsSpecial()) {
			memberRank.setAmount(null);
		} else if (memberRank.getAmount() == null || memberRankService.amountExists(memberRank.getAmount())) {
			renderJson(ERROR_VIEW);
			return;
		}
		memberRankService.save(memberRank);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("memberRank", memberRankService.find(id));
		render("/admin/member_rank/edit.html");
	}
	
	/**
	 * 更新
	 */
	public void update() {
		memberRank = getModel(MemberRank.class);
		MemberRank pMemberRank = memberRankService.find(memberRank.getId());
		if (pMemberRank == null) {
			renderJson(ERROR_VIEW);
			return;
		}
		if (!memberRankService.nameUnique(pMemberRank.getName(), memberRank.getName())) {
			renderJson(ERROR_VIEW);
			return;
		}
		if (pMemberRank.getIsDefault()) {
			memberRank.setIsDefault(true);
		}
		if (memberRank.getIsSpecial()) {
			memberRank.setAmount(null);
		} else if (memberRank.getAmount() == null || !memberRankService.amountUnique(pMemberRank.getAmount(), memberRank.getAmount())) {
			renderJson(ERROR_VIEW);
			return;
		}
		memberRankService.update(memberRank);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}

	
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length > 0) {
			memberRankService.delete(ids);
			renderJson(SUCCESS_MESSAGE);
		} else {
			renderJson(com.jfinalshop.common.Message.error("admin.common.deleteAllNotAllowed"));
		}
	}
}
