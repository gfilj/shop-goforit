package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseMemberFavoriteProduct;

/**
 * Dao - 会员收藏产品
 * 
 * 
 * 
 */
public class MemberFavoriteProduct extends BaseMemberFavoriteProduct<MemberFavoriteProduct> {
	private static final long	serialVersionUID	= 1648969053473641788L;
	public static final MemberFavoriteProduct dao = new MemberFavoriteProduct();
	
	/**
	 * 根据favoriteProducts删除参数
	 * @param favoriteProducts
	 * @return
	 */
	public boolean delete(Long favoriteProducts) {
		return Db.deleteById("member_favorite_product", "favorite_products", favoriteProducts);
	}
}
