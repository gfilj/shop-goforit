package com.jfinalshop.model;

import java.io.IOException;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.common.CommonAttributes;
import com.jfinalshop.model.base.BaseSn;
import com.jfinalshop.utils.AssertUtil;
import com.jfinalshop.utils.FreemarkerUtils;

import freemarker.template.TemplateException;

/**
 * Dao - 序列号
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class Sn extends BaseSn<Sn> {
	public static final Sn dao = new Sn();
	
	Prop prop = PropKit.use(CommonAttributes.SHOPXX_PROPERTIES_PATH);
	private String productPrefix = prop.get("sn.product.prefix");
	private int productMaxLo = prop.getInt("sn.product.maxLo");
	
	private String orderPrefix = prop.get("sn.order.prefix");
	private int orderMaxLo = prop.getInt("sn.order.maxLo");
	
	private String paymentPrefix = prop.get("sn.payment.prefix");
	private int paymentMaxLo = prop.getInt("sn.payment.maxLo");
	
	private String refundsPrefix = prop.get("sn.refunds.prefix");
	private int refundsMaxLo = prop.getInt("sn.refunds.maxLo");
	
	private String shippingPrefix = prop.get("sn.shipping.prefix");
	private int shippingMaxLo = prop.getInt("sn.shipping.maxLo");
	
	private String returnsPrefix = prop.get("sn.returns.prefix");
	private int returnsMaxLo = prop.getInt("sn.returns.maxLo");
	
	private HiloOptimizer productHiloOptimizer = new HiloOptimizer(Type.product, productPrefix, productMaxLo);
	private HiloOptimizer orderHiloOptimizer = new HiloOptimizer(Type.order, orderPrefix, orderMaxLo);
	private HiloOptimizer paymentHiloOptimizer = new HiloOptimizer(Type.payment, paymentPrefix, paymentMaxLo);
	private HiloOptimizer refundsHiloOptimizer = new HiloOptimizer(Type.refunds, refundsPrefix, refundsMaxLo);
	private HiloOptimizer shippingHiloOptimizer = new HiloOptimizer(Type.shipping, shippingPrefix, shippingMaxLo);
	private HiloOptimizer returnsHiloOptimizer = new HiloOptimizer(Type.returns, returnsPrefix, returnsMaxLo);
	
	
	/**
	 * 类型
	 */
	public enum Type {
		/** 商品 */
		product,

		/** 订单 */
		order,

		/** 收款单 */
		payment,

		/** 退款单 */
		refunds,

		/** 发货单 */
		shipping,

		/** 退货单 */
		returns
	}
	
	public String generate(Type type) {
		AssertUtil.notNull(type);
		if (type == Type.product) {
			return productHiloOptimizer.generate();
		} else if (type == Type.order) {
			return orderHiloOptimizer.generate();
		} else if (type == Type.payment) {
			return paymentHiloOptimizer.generate();
		} else if (type == Type.refunds) {
			return refundsHiloOptimizer.generate();
		} else if (type == Type.shipping) {
			return shippingHiloOptimizer.generate();
		} else if (type == Type.returns) {
			return returnsHiloOptimizer.generate();
		}
		return null;
	}
	
	/**
	 * 获取最后的值
	 * @param type
	 * @return
	 */
	private long getLastValue(Type type) {
		String sql = "SELECT * FROM `sn` WHERE `type` = ?";
		Sn sn = findFirst(sql, type.ordinal());
		Long lastValue = sn.getLastValue();
		// 更新已取过的值
		String _sql = "UPDATE `sn` SET `last_value` = ? WHERE `id` = ? AND `last_value` = ?";
		Db.update(_sql, (lastValue + 1L), sn.getId(), lastValue);
		return lastValue;
	}
	
	/**
	 * 高低位算法
	 */
	private class HiloOptimizer {

		private Type type;
		private String prefix;
		private int maxLo;
		private int lo;
		private long hi;
		private long lastValue;

		public HiloOptimizer(Type type, String prefix, int maxLo) {
			this.type = type;
			this.prefix = prefix != null ? prefix.replace("{", "${") : "";
			this.maxLo = maxLo;
			this.lo = maxLo + 1;
		}

		/*每次生成都进行下面的判断*/
		public synchronized String generate() {
			if (lo > maxLo) {// 当低位超过最大高位
				lastValue = getLastValue(type); // 表示hi位的进位次数，从数据库id管理表获取
				lo = lastValue == 0 ? 1 : 0;// 低位归0
				hi = lastValue * (maxLo + 1);// 高位进位
			}
			try {
				return FreemarkerUtils.process(prefix, null) + (hi + lo++);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TemplateException e) {
				e.printStackTrace();
			}
			return String.valueOf(hi + lo++);
		}
	}
}
