package com.github.jchanghong.aop.proxy;

import com.github.jchanghong.aop.aspects.Aspect;
import com.github.jchanghong.aop.interceptor.JdkInterceptor;
import com.github.jchanghong.aop.ProxyUtil;

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
