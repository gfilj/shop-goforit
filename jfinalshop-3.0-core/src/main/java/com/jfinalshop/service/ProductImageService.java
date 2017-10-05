package com.jfinalshop.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.jfinal.core.JFinal;
import com.jfinal.upload.UploadFile;
import com.jfinalshop.common.Setting;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductImage;
import com.jfinalshop.plugin.FilePlugin;
import com.jfinalshop.plugin.FtpPlugin;
import com.jfinalshop.plugin.OssPlugin;
import com.jfinalshop.plugin.StoragePlugin;
import com.jfinalshop.utils.FreemarkerUtils;
import com.jfinalshop.utils.ImageUtils;
import com.jfinalshop.utils.SettingUtils;

/**
 * Service - 商品图片
 * 
 * 
 * 
 */
public class ProductImageService extends BaseService<ProductImage> {
	public static final ProductImageService service = new ProductImageService();

	public ProductImageService() {
		super(ProductImage.class);
	}
	
	/** 目标扩展名 */
	private static final String DEST_EXTENSION = "jpg";
	/** 目标文件类型 */
	private static final String DEST_CONTENT_TYPE = "image/jpeg";

	/** servletContext */
	private ServletContext servletContext = JFinal.me().getServletContext();

	private static List<StoragePlugin> storagePlugins = new ArrayList<StoragePlugin>();

	static {
		FilePlugin filePlugin = new FilePlugin();
		FtpPlugin ftpPlugin = new FtpPlugin();
		OssPlugin ossPlugin = new OssPlugin();
		
		storagePlugins.add(filePlugin);
		storagePlugins.add(ftpPlugin);
		storagePlugins.add(ossPlugin);
	}
	
	/**
	 * 添加图片处理任务
	 * 
	 * @param sourcePath
	 *            原图片上传路径
	 * @param largePath
	 *            图片文件(大)上传路径
	 * @param mediumPath
	 *            图片文件(小)上传路径
	 * @param thumbnailPath
	 *            图片文件(缩略)上传路径
	 * @param tempFile
	 *            原临时文件
	 * @param contentType
	 *            原文件类型
	 */
	private void addTask(final String sourcePath, final String largePath, final String mediumPath, final String thumbnailPath, final File tempFile, final String contentType) {
		try {
			Collections.sort(storagePlugins);
			for (StoragePlugin storagePlugin : storagePlugins) {
				if (storagePlugin.getIsEnabled()) {
					Setting setting = SettingUtils.get();
					String tempPath = System.getProperty("java.io.tmpdir");
					File watermarkFile = new File(servletContext.getRealPath(setting.getWatermarkImage()));
					File largeTempFile = new File(tempPath + "/upload_" + UUID.randomUUID() + "." + DEST_EXTENSION);
					File mediumTempFile = new File(tempPath + "/upload_" + UUID.randomUUID() + "." + DEST_EXTENSION);
					File thumbnailTempFile = new File(tempPath + "/upload_" + UUID.randomUUID() + "." + DEST_EXTENSION);
					try {
						ImageUtils.zoom(tempFile, largeTempFile, setting.getLargeProductImageWidth(), setting.getLargeProductImageHeight());
						ImageUtils.addWatermark(largeTempFile, largeTempFile, watermarkFile, setting.getWatermarkPosition(), setting.getWatermarkAlpha());
						ImageUtils.zoom(tempFile, mediumTempFile, setting.getMediumProductImageWidth(), setting.getMediumProductImageHeight());
						ImageUtils.addWatermark(mediumTempFile, mediumTempFile, watermarkFile, setting.getWatermarkPosition(), setting.getWatermarkAlpha());
						ImageUtils.zoom(tempFile, thumbnailTempFile, setting.getThumbnailProductImageWidth(), setting.getThumbnailProductImageHeight());
						storagePlugin.upload(sourcePath, tempFile, contentType);
						storagePlugin.upload(largePath, largeTempFile, DEST_CONTENT_TYPE);
						storagePlugin.upload(mediumPath, mediumTempFile, DEST_CONTENT_TYPE);
						storagePlugin.upload(thumbnailPath, thumbnailTempFile, DEST_CONTENT_TYPE);
					} finally {
						FileUtils.deleteQuietly(tempFile);
						FileUtils.deleteQuietly(largeTempFile);
						FileUtils.deleteQuietly(mediumTempFile);
						FileUtils.deleteQuietly(thumbnailTempFile);
					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成商品图片
	 * 
	 * @param productImage
	 *            商品图片
	 */
	public void build(ProductImage productImage) {
		UploadFile uploadFile = productImage.getUploadFile();
		if (uploadFile != null) {
			try {
				Setting setting = SettingUtils.get();
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("uuid", UUID.randomUUID().toString());
				String uploadPath = FreemarkerUtils.process(setting.getImageUploadPath(), model);
				String uuid = UUID.randomUUID().toString();
				String sourcePath = uploadPath + uuid + "-source." + FilenameUtils.getExtension(uploadFile.getOriginalFileName());
				String largePath = uploadPath + uuid + "-large." + DEST_EXTENSION;
				String mediumPath = uploadPath + uuid + "-medium." + DEST_EXTENSION;
				String thumbnailPath = uploadPath + uuid + "-thumbnail." + DEST_EXTENSION;

				Collections.sort(storagePlugins);
				for (StoragePlugin storagePlugin : storagePlugins) {
					if (storagePlugin.getIsEnabled()) {
						File targetDir = new File(System.getProperty("java.io.tmpdir"));
						if (!targetDir.exists()) {
							targetDir.mkdirs();
						}
						File tempFile = new File(targetDir, "/upload_" + UUID.randomUUID() + ".tmp");
						if (!tempFile.exists()) {
							tempFile.createNewFile();
						}
						
						FileUtils.copyFile(uploadFile.getFile(), tempFile);
						addTask(sourcePath, largePath, mediumPath, thumbnailPath, tempFile, uploadFile.getContentType());
						productImage.setSource(storagePlugin.getUrl(sourcePath));
						productImage.setLarge(storagePlugin.getUrl(largePath));
						productImage.setMedium(storagePlugin.getUrl(mediumPath));
						productImage.setThumbnail(storagePlugin.getUrl(thumbnailPath));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 保存
	 * 
	 * 
	 */
	public boolean save(Product product) {
		boolean result = false;
		if (CollectionUtils.isNotEmpty(product.getProductImages())) {
			for(ProductImage productImage : product.getProductImages()) {
				productImage.setProductId(product.getId());
				result = productImage.save();
			}
		}
		return result;
	}
	
	/**
	 * 更新
	 * 
	 * 
	 */
	public boolean update(Product product) {
		boolean result = false;
		if (CollectionUtils.isNotEmpty(product.getProductImages())) {
			ProductImage.dao.delete(product.getId());
			for(ProductImage productImage : product.getProductImages()) {
				productImage.setProductId(product.getId());
				result = productImage.save();
			}
		}
		return result;
	}

}
