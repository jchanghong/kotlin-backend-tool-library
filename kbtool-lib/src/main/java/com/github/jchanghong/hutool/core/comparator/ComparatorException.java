package com.github.jchanghong.hutool.core.comparator;

import com.github.jchanghong.hutool.core.exceptions.ExceptionUtil;
import com.github.jchanghong.hutool.core.util.StrUtil;

/**
 * 比较异常
 * @author xiaoleilu
 */
public class ComparatorException extends RuntimeException{
	private static final long serialVersionUID = 4475602435485521971L;

	public ComparatorException(Throwable e) {
		super(ExceptionUtil.getMessage(e), e);
	}

	public ComparatorException(String message) {
		super(message);
	}

	public ComparatorException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public ComparatorException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public ComparatorException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
