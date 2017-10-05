package com.jfinalshop.utils;

/**
 * 生成token号码
 * @return token号码
 */
public class TokenUtil {
	
    public static String generateToken() {
        return RandomUtils.randomCustomUUID().concat(RandomUtils.randomString(6));
    }
}
