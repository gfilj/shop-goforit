package com.jfinalshop.controller.admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;
import com.jfinalshop.common.FileInfo;
import com.jfinalshop.common.FileInfo.FileType;
import com.jfinalshop.common.FileInfo.OrderType;
import com.jfinalshop.common.Message;
import com.jfinalshop.service.FileService;
import com.jfinalshop.utils.JsonUtils;

/**
 * Controller - 文件处理
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/file")
public class FileController extends BaseAdminController {

	private FileService fileService = enhance(FileService.class);

	/**
	 * 上传
	 */
	public void upload() {
		UploadFile uploadFile = getFile();
		FileType fileType = StrKit.notBlank(getPara("fileType")) ? FileType.valueOf(getPara("fileType")) : null;
		Map<String, Object> data = new HashMap<String, Object>();
		if (!fileService.isValid(fileType, uploadFile)) {
			data.put("message", Message.warn("admin.upload.invalid"));
		} else {
			String url = fileService.upload(fileType, uploadFile);
			if (url == null) {
				data.put("message", Message.warn("admin.upload.error"));
			} else {
				data.put("message", SUCCESS_MESSAGE);
				data.put("url", url);
			}
		}
		renderJson(data);
		try {
			getResponse().setContentType("text/html; charset=UTF-8");
			JsonUtils.writeValue(getResponse().getWriter(), data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 浏览
	 */
	public void browser() {
		String path = getPara("path");
		FileType fileType = StrKit.notBlank(getPara("fileType")) ? FileType.valueOf(getPara("fileType")) : null;
		OrderType orderType = StrKit.notBlank(getPara("orderType")) ? OrderType.valueOf(getPara("orderType")) : null;
		List<FileInfo> fileInfo = fileService.browser(path, fileType, orderType);
		renderJson(fileInfo);
	}

}