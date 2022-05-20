package com.hutool

import cn.hutool.core.bean.BeanUtil
import cn.hutool.extra.cglib.CglibUtil
import cn.hutool.log.LogFactory
import com.github.jchanghong.lang.exeTimeAvg
import java.util.function.Supplier

class BeanUtils {
}

private class Bean1(var name: String)
private class Bean2(var name: String)
private class Bean3(var name: Long)

fun main() {
	val b1 = Bean1("1")
	println(CglibUtil.copy(b1, Bean3::class))
	val b2 = Bean2("0")
	println(exeTimeAvg(100) {
		BeanUtil.copyProperties(b1, b2)
		b2.name
	})
	println(exeTimeAvg(100) {
		CglibUtil.copy(b1, b2)
		b2.name
	})
	println(b1.name)
	val copyList = CglibUtil.copyList(listOf(b1, b1), Supplier { Bean2("0") })
	println(copyList.first().name)

	val log = LogFactory.get()
	log.info("sasa")
}
