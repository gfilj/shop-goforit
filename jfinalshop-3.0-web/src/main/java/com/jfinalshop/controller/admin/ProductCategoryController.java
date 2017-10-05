package com.jfinalshop.controller.admin;

import java.util.List;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.common.Message;
import com.jfinalshop.common.Pageable;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.service.BrandService;
import com.jfinalshop.service.ProductCategoryService;

/**
 * Controller - 商品分类
 * 
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/productCategory")
public class ProductCategoryController extends BaseAdminController {
	
	private ProductCategoryService productCategoryService = enhance(ProductCategoryService.class);
	private BrandService brandService = enhance(BrandService.class);
	private ProductCategory productCategory;
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", productCategoryService.findPage(pageable));
		setAttr("productCategoryTree", productCategoryService.findTree());
		render("/admin/product_category/list.html");
	}

	/**
	 * 添加
	 */
	public void add() {
		setAttr("productCategoryTree", productCategoryService.findTree());
		setAttr("brands", brandService.findAll());
		render("/admin/product_category/add.html");
	}
	
	/**
	 * 保存
	 */
	public void save() {
		productCategory = getModel(ProductCategory.class);
		Long[] brandIds = getParaValuesToLong("brandIds");
		if (productCategory == null) {
			renderJson(ERROR_VIEW);
			return;
		}
		productCategory.setBrands(brandService.findList(brandIds));
		productCategoryService.save(productCategory);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		productCategory = productCategoryService.find(id);
		setAttr("productCategoryTree", productCategoryService.findTree());
		setAttr("brands", brandService.findAll());
		setAttr("productCategory", productCategory);
		setAttr("children", productCategoryService.findChildren(productCategory, null));
		render("/admin/product_category/edit.html");
	}
	
	
	/**
	 * 更新
	 */
	public void update() {
		productCategory = getModel(ProductCategory.class);
		Long[] brandIds = getParaValuesToLong("brandIds");
		if (productCategory.getParent() != null) {
			ProductCategory parent = productCategory.getParent();
			if (parent.equals(productCategory)) {
				renderJson(ERROR_VIEW);
				return;
			}
			List<ProductCategory> children = productCategoryService.findChildren(parent, null);
			if (children != null && children.contains(parent)) {
				renderJson(ERROR_VIEW);
				return;
			}
		}
		productCategory.setBrands(brandService.findList(brandIds));
		productCategoryService.update(productCategory);
		addFlashMessage(SUCCESS_MESSAGE);
		list();
	}
	
	
	/**
	 * 删除
	 */
	public void delete() {
		Long id = getParaToLong("id");
		ProductCategory productCategory = productCategoryService.find(id);
		if (productCategory == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		List<ProductCategory> children = productCategory.getChildren();
		if (children != null && !children.isEmpty()) {
			renderJson(Message.error("admin.productCategory.deleteExistChildrenNotAllowed"));
			return;
		}
		List<Product> products = productCategory.getProducts();
		if (products != null && !products.isEmpty()) {
			renderJson(Message.error("admin.productCategory.deleteExistProductNotAllowed"));
			return;
		}
		productCategoryService.delete(id);
		renderJson(SUCCESS_MESSAGE);
	}
}
