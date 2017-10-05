package com.jfinalshop.utils;

import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

import com.jfinal.plugin.redis.Redis;

public class RedisUtil {
	/** session 在redis过期时间是30分钟30*60 */
	private static int expireTime = 1800;

	/** 计数器的过期时间默认2天 */
	private static int countExpireTime = 2 * 24 * 3600;

	private static Logger logger = Logger.getLogger(RedisUtil.class);

	// 从连接池获取redis连接
	public static Jedis getJedis() {
		Jedis jedis = null;
		try {
			jedis = Redis.use("db2").getJedis();
		} catch (Exception e) {
			logger.error(e);
		}
		return jedis;
	}
	
	// 回收redis连接
	public static void recycleJedis(Jedis jedis) {
		if (jedis != null) {
			try {
				jedis.close();
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	// 保存字符串数据
	public static void setString(String key, String value) {
		Jedis jedis = getJedis();
		if (jedis != null) {
			try {
				jedis.set(key, value);
			} catch (Exception e) {
				logger.error(e);
			} finally {
				recycleJedis(jedis);
			}
		}

	}

	// 获取字符串类型的数据
	public static String getString(String key) {
		Jedis jedis = getJedis();
		String result = "";
		if (jedis != null) {
			try {
				result = jedis.get(key);
			} catch (Exception e) {
				logger.error(e);
			} finally {
				recycleJedis(jedis);
			}
		}
		return result;
	}

	// 删除字符串数据
	public static void delString(String key) {
		Jedis jedis = getJedis();
		if (jedis != null) {
			try {
				jedis.del(key);
			} catch (Exception e) {
				logger.error(e);
			} finally {
				recycleJedis(jedis);
			}
		}
	}

	// 保存byte类型数据
	public static void setObject(byte[] key, byte[] value) {
		Jedis jedis = getJedis();
		if (jedis != null) {
			try {
				if (!jedis.exists(key)) {
					jedis.set(key, value);
				}
				jedis.expire(key, expireTime);// redis中session过期时间
			} catch (Exception e) {
				logger.error(e);
			} finally {
				recycleJedis(jedis);
			}
		}
	}

	// 获取byte类型数据
	public static byte[] getObject(byte[] key) {
		Jedis jedis = getJedis();
		byte[] bytes = null;
		if (jedis != null) {
			try {
				bytes = jedis.get(key);
			} catch (Exception e) {
				logger.error(e);
			} finally {
				recycleJedis(jedis);
			}
		}
		return bytes;
	}

	// 更新byte类型的数据，主要更新过期时间
	public static void updateObject(byte[] key) {
		Jedis jedis = getJedis();
		if (jedis != null) {
			try {
				jedis.expire(key, expireTime); // redis中session过期时间
			} catch (Exception e) {
				logger.error(e);
			} finally {
				recycleJedis(jedis);
			}
		}

	}

	// key对应的整数value加1
	public static void inc(String key) {
		Jedis jedis = getJedis();
		if (jedis != null) {
			try {
				if (!jedis.exists(key)) {
					jedis.set(key, "1");
					jedis.expire(key, countExpireTime);
				} else {
					jedis.incr(key);// 加1
				}
			} catch (Exception e) {
				logger.error(e);
			} finally {
				recycleJedis(jedis);
			}
		}
	}

	// 获取所有keys
	public static Set<String> getAllKeys(String pattern) {
		Jedis jedis = getJedis();
		if (jedis != null) {
			try {
				return jedis.keys(pattern);
			} catch (Exception e) {
				logger.error(e);
			} finally {
				recycleJedis(jedis);
			}
		}
		return null;
	}
	
}