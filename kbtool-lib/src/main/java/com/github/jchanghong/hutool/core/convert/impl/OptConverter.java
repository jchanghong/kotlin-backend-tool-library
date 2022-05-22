package com.github.jchanghong.hutool.core.convert.impl;

import com.github.jchanghong.hutool.core.convert.AbstractConverter;
import com.github.jchanghong.hutool.core.lang.Opt;

/**
 *
 * {@link Opt}对象转换器
 *
 * @author Looly
 * @since 5.7.16
 */
public class OptConverter extends AbstractConverter<Opt<?>> {
	private static final long serialVersionUID = 1L;

	@Override
	protected Opt<?> convertInternal(Object value) {
		return Opt.ofNullable(value);
	}

}
