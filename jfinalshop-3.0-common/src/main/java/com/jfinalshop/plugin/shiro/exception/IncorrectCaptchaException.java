package com.jfinalshop.plugin.shiro.exception;

import org.apache.shiro.authc.AuthenticationException;

/**
 * Created by wangrenhui on 14-1-3.
 */
public class IncorrectCaptchaException extends AuthenticationException {

	private static final long serialVersionUID = -5869863812596886399L;

	public IncorrectCaptchaException() {
		super();
	}

	public IncorrectCaptchaException(String message, Throwable cause) {
		super(message, cause);
	}

	public IncorrectCaptchaException(String message) {
		super(message);
	}

	public IncorrectCaptchaException(Throwable cause) {
		super(cause);
	}
}