package com.jfinalshop.controller.shop;

import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;

/**
 *
 * 前台类 - 首页
 *
 * 
 */
@ControllerBind(controllerKey = "/")
public class IndexController extends Controller {

	// 首页
	public void index(){
		redirect("/index.html");
	}
}
