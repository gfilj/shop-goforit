package com.jfinalshop.controller.admin;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;
import com.jfinalshop.common.FileInfo.FileType;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Setting;
import com.jfinalshop.common.Setting.AccountLockType;
import com.jfinalshop.common.Setting.CaptchaType;
import com.jfinalshop.common.Setting.ConsultationAuthority;
import com.jfinalshop.common.Setting.ReviewAuthority;
import com.jfinalshop.common.Setting.RoundType;
import com.jfinalshop.common.Setting.StockAllocationTime;
import com.jfinalshop.common.Setting.WatermarkPosition;
import com.jfinalshop.service.CacheService;
import com.jfinalshop.service.FileService;
import com.jfinalshop.service.StaticService;
import com.jfinalshop.utils.ObjectUtils;
import com.jfinalshop.utils.SettingUtils;

/**
 * Controller - 系统设置
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/setting")
public class SettingController extends BaseAdminController {
	
	private FileService fileService = new FileService();
	private CacheService cacheService = new CacheService();
	private StaticService staticService = new StaticService();
	
	/**
	 * 编辑
	 */
	public void edit() {
		setAttr("watermarkPositions", WatermarkPosition.values());
		setAttr("roundTypes", RoundType.values());
		setAttr("captchaTypes", CaptchaType.values());
		setAttr("accountLockTypes", AccountLockType.values());
		setAttr("stockAllocationTimes", StockAllocationTime.values());
		setAttr("reviewAuthorities", ReviewAuthority.values());
		setAttr("consultationAuthorities", ConsultationAuthority.values());
		setAttr("setting", SettingUtils.get());
		render("/admin/setting/edit.html");
	}
	
	/**
	 * 更新
	 */
	public void update() {
		UploadFile watermarkImageFile = getFile("watermarkImageFile");
		Setting setting = getBean(Setting.class);
		setting.setIsSiteEnabled(getParaToBoolean("isSiteEnabled", false));
		setting.setIsShowMarketPrice(getParaToBoolean("isShowMarketPrice", false));
		setting.setIsRegisterEnabled(getParaToBoolean("isRegisterEnabled", false));
		setting.setIsDuplicateEmail(getParaToBoolean("isDuplicateEmail", false));
		setting.setIsEmailLogin(getParaToBoolean("isEmailLogin", false));
		setting.setIsReviewEnabled(getParaToBoolean("isReviewEnabled", false));
		setting.setIsReviewCheck(getParaToBoolean("isReviewCheck", false));
		setting.setIsConsultationEnabled(getParaToBoolean("isConsultationEnabled", false));
		setting.setIsConsultationCheck(getParaToBoolean("isConsultationCheck", false));
		setting.setIsInvoiceEnabled(getParaToBoolean("isInvoiceEnabled", false));
		setting.setIsTaxPriceEnabled(getParaToBoolean("isTaxPriceEnabled", false));
		WatermarkPosition watermarkPosition = StrKit.notBlank(getPara("watermarkPosition")) ? WatermarkPosition.valueOf(getPara("watermarkPosition")) : null;
		setting.setWatermarkPosition(watermarkPosition);
		
		RoundType priceRoundType = StrKit.notBlank(getPara("priceRoundType")) ? RoundType.valueOf(getPara("priceRoundType")) : null;
		setting.setPriceRoundType(priceRoundType);
		
		ReviewAuthority reviewAuthority = StrKit.notBlank(getPara("reviewAuthority")) ? ReviewAuthority.valueOf(getPara("reviewAuthority")) : null;
		setting.setReviewAuthority(reviewAuthority);
		
		ConsultationAuthority consultationAuthority = StrKit.notBlank(getPara("consultationAuthority")) ? ConsultationAuthority.valueOf(getPara("consultationAuthority")) : null;
		setting.setConsultationAuthority(consultationAuthority);
		
		StockAllocationTime stockAllocationTime = StrKit.notBlank(getPara("stockAllocationTime")) ? StockAllocationTime.valueOf(getPara("stockAllocationTime")) : null;
		setting.setStockAllocationTime(stockAllocationTime);
		
		// 验证码类型
		String[] captchaTypeValues = getParaValues("captchaTypes");
		if (!ObjectUtils.isEmpty(captchaTypeValues)) {
			int length = captchaTypeValues.length;
			CaptchaType [] captchaTypes = new CaptchaType [length];
			for (int i = 0; i < length; i++) {  
				captchaTypes[i] = CaptchaType.valueOf(captchaTypeValues[i]); 
			}  
			setting.setCaptchaTypes(captchaTypes);
		}
		
		// 账号锁定类型
		String [] accountLockTypeValues = getParaValues("accountLockTypes");
		if (!ObjectUtils.isEmpty(accountLockTypeValues)) {
			int length = accountLockTypeValues.length;
			AccountLockType [] accountLockTypes = new AccountLockType [length];
			for (int i = 0; i < length; i++) {  
				accountLockTypes[i] = AccountLockType.valueOf(accountLockTypeValues[i]); 
			}  
			setting.setAccountLockTypes(accountLockTypes);
		}
		
		if (setting.getUsernameMinLength() > setting.getUsernameMaxLength() || setting.getPasswordMinLength() > setting.getPasswordMinLength()) {
			renderJson(ERROR_VIEW);
			return;
		}
		Setting srcSetting = SettingUtils.get();
		if (StringUtils.isEmpty(setting.getSmtpPassword())) {
			setting.setSmtpPassword(srcSetting.getSmtpPassword());
		}
		if (watermarkImageFile != null) {
			if (!fileService.isValid(FileType.image, watermarkImageFile)) {
				addFlashMessage(Message.error("admin.upload.invalid"));
				redirect("/admin/setting/edit");
				return;
			}
			String watermarkImage = fileService.uploadLocal(FileType.image, watermarkImageFile);
			setting.setWatermarkImage(watermarkImage);
		} else {
			setting.setWatermarkImage(srcSetting.getWatermarkImage());
		}
		setting.setCnzzSiteId(srcSetting.getCnzzSiteId());
		setting.setCnzzPassword(srcSetting.getCnzzPassword());
		SettingUtils.set(setting);
		cacheService.clear();
		staticService.buildIndex();
		staticService.buildOther();
		
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/setting/edit");
	}
	
}
