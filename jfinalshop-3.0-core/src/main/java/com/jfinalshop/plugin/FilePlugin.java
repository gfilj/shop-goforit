/*
 * 
 * 
 * 
 */
package com.jfinalshop.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;

import com.jfinal.core.JFinal;
import com.jfinalshop.common.FileInfo;
import com.jfinalshop.common.Setting;
import com.jfinalshop.utils.SettingUtils;

/**
 * Plugin - 本地文件存储
 * 
 * 
 * 
 */
public class FilePlugin extends StoragePlugin {

	/** servletContext */
	private ServletContext servletContext = JFinal.me().getServletContext();

	@Override
	public String getName() {
		return "本地文件存储";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getAuthor() {
		return SettingUtils.get().getSiteName();
	}

	@Override
	public String getSiteUrl() {
		return SettingUtils.get().getSiteUrl();
	}

	@Override
	public String getInstallUrl() {
		return null;
	}

	@Override
	public String getUninstallUrl() {
		return null;
	}

	@Override
	public String getSettingUrl() {
		return "file/setting";
	}

	@Override
	public void upload(String path, File file, String contentType) {
		File destFile = new File(servletContext.getRealPath(path));
		try {
			FileUtils.moveFile(file, destFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getUrl(String path) {
		Setting setting = SettingUtils.get();
		return setting.getSiteUrl() + path;
	}

	@Override
	public List<FileInfo> browser(String path) {
		Setting setting = SettingUtils.get();
		List<FileInfo> fileInfos = new ArrayList<FileInfo>();
		File directory = new File(servletContext.getRealPath(path));
		if (directory.exists() && directory.isDirectory()) {
			for (File file : directory.listFiles()) {
				FileInfo fileInfo = new FileInfo();
				fileInfo.setName(file.getName());
				fileInfo.setUrl(setting.getSiteUrl() + path + file.getName());
				fileInfo.setIsDirectory(file.isDirectory());
				fileInfo.setSize(file.length());
				fileInfo.setLastModified(new Date(file.lastModified()));
				fileInfos.add(fileInfo);
			}
		}
		return fileInfos;
	}

}