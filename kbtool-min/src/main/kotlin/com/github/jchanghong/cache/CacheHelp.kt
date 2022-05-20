package com.github.jchanghong.cache

import cn.hutool.cron.CronUtil
import com.github.jchanghong.log.kDebug
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Supplier

/** 后台定时更新cache，如果提供了 supplier,只能用get put方法*/
class CronMap<K, V>(val cron: String) : ConcurrentHashMap<K, V>() {
	//    调度id map
	private val schedulerIdMap = ConcurrentHashMap<K, String>()
	private val supplierMap = ConcurrentHashMap<K, Supplier<V>>()

	//    put 为false，不调度，一旦调用过get。就调度
	private val lateinitMap = ConcurrentHashMap<K, Boolean>()

	init {
		try {
			CronUtil.setMatchSecond(true)
			CronUtil.start()
		} catch (e: Throwable) {
		}
	}

	@Synchronized
	@JvmOverloads
	fun get2(key: K, supplier: Supplier<V>? = null, cron: String? = null): V? {
		if (supplier != null) {
			supplierMap[key] = supplier
			lateinitMap[key] = true
			return getAndSchudulValue(supplier, key, cron)
		}
		return super.get(key)
	}

	private fun getAndSchudulValue(supplier: Supplier<V>, key: K, cron: String? = null): V? {
		schedulerIdMap[key]?.let { CronUtil.remove(it) }
		val scheduleId = CronUtil.schedule(cron ?: this.cron, Runnable {
			if (lateinitMap[key] == true) {
				val value = supplier.get()
				this[key] = value
				kDebug("调度 已更新key  $key -> $value")
			}
		})
		schedulerIdMap[key] = scheduleId
		if (lateinitMap[key] == true) {
			val value = supplier.get()
			put(key, value)
			return value
		}
		return null
	}

	@Synchronized
	@JvmOverloads
	fun put2(key: K, supplier: Supplier<V>, cron: String? = null): V? {
		supplierMap[key] = supplier
		if (!lateinitMap.containsKey(key)) {
			lateinitMap[key] = false
		}
		getAndSchudulValue(supplier, key, cron)
		return null
	}

	@Synchronized
	override fun get(key: K): V? {
		lateinitMap[key] = true
		val v = super.get(key)
		if (v == null && supplierMap.containsKey(key)) {
			val get = supplierMap[key]?.get()?.apply { put(key, this) }
			return get
		}
		return v
	}
}

object CacheHelp {
	/**
	 * CronMap
	</V></K> */
	fun <K, V> newCronMap(cron: String): CronMap<K, V> {
		return CronMap(cron)
	}
}

fun main() {
	val cache = CacheHelp.newCronMap<String, String>("1/5 * * * * ?")
	cache.put2("test", Supplier { "sasa" })
	println(cache.get("test"))
	println(cache.get("test"))
	Thread.sleep(2000)
	println(cache.get("test"))
	println(cache.get("test"))
}
