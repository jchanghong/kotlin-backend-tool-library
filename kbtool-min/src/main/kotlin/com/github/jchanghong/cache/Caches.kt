package com.github.jchanghong.cache

import kotlin.properties.Delegates

class Caches {
	var s by Delegates.observable(1) { property, oldValue, newValue ->
		println("$property $oldValue $newValue")
	}
}

fun main() {
	val caches = Caches()
	println(caches.s)
	caches.s = 11
}
