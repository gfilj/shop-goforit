package com.jfinalshop.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;

public class FileUtil {

	/**
	 * 上传处理
	 * @param file
	 * @return
	 */
	public static String upload(UploadFile file, String modelName, String name) {
		File source = file.getFile();
		String fileName = file.getFileName();
		String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
		String prefix;
		if (".png".equals(extension) || ".jpg".equals(extension) || ".gif".equals(extension)) {
			prefix = "image";
			if(StrKit.isBlank(name)) {
				fileName = fileName + "_" + generateWord() + extension;
			} else {
				fileName = RandomUtils.randomNumbers(6) + "_" + generateWord() + extension;
			}
		} else {
			prefix = "file";
		}
		String url = "";
		try {
			FileInputStream fis = new FileInputStream(source);
			//boolean isLinux = OSinfo.isLinux();
			//String rootPath = isLinux ? Const.UPLOAD_IMAGE_DIR : PathKit.getWebRootPath();
			url = "/upload/" + prefix + "/"+ modelName;
			File targetDir = new File(PathKit.getWebRootPath() + url);
			if (!targetDir.exists()) {
				targetDir.mkdirs();
			}
			File target = new File(targetDir, fileName);
			if (!target.exists()) {
				target.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(target);
			byte[] bts = new byte[300];
			while (fis.read(bts, 0, 300) != -1) {
				fos.write(bts, 0, 300);
			}
			fos.close();
			fis.close();
			source.delete();
			url += "/" + fileName;
		} catch (FileNotFoundException e) {
			url = "上传出现错误，请稍后再上传";
		} catch (IOException e) {
			url = "文件写入服务器出现错误，请稍后再上传";
		}
		return url;
	}
	
	private static String generateWord() {
        String[] beforeShuffle = new String[] { "2", "3", "4", "5", "6", "7",
                "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z" };
        List<String> list = Arrays.asList(beforeShuffle);
        Collections.shuffle(list);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
        }
        String afterShuffle = sb.toString();
        String result = afterShuffle.substring(5, 9);
        return result;
    }
	
}
