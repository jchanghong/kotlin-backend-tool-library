package com.github.jchanghong.autoconfig.db.mybatis

import com.github.jchanghong.log.kInfo
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanNameGenerator

class JBeanNameGenerator : BeanNameGenerator {
	override fun generateBeanName(definition: BeanDefinition, registry: BeanDefinitionRegistry): String {
		kInfo(definition.beanClassName)
		return definition.beanClassName!!
	}
}
