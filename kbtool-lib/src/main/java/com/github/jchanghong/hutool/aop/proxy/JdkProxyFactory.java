package com.github.jchanghong.hutool.aop.proxy;

import com.github.jchanghong.hutool.aop.aspects.Aspect;
import com.github.jchanghong.hutool.aop.interceptor.JdkInterceptor;
import com.github.jchanghong.hutool.aop.ProxyUtil;

/**
 * JDK实现的切面代理
 *
 * @author looly
 */
public class JdkProxyFactory extends ProxyFactory {
	private static final long serialVersionUID = 1L;

	@Override
	public <T> T proxy(T target, Aspect aspect) {
		return ProxyUtil.newProxyInstance(//
				target.getClass().getClassLoader(), //
				new JdkInterceptor(target, aspect), //
				target.getClass().getInterfaces());
	}
}
