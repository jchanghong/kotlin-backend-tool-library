package com.github.jchanghong

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext

@SpringBootApplication
class BootTest : CommandLineRunner {
	@Autowired
	lateinit var applicationContext: ApplicationContext
	override fun run(vararg args: String?) {

	}
}

fun main() {
	runApplication<BootTest>()
}
