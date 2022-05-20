package org.apache.myfaces.core

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.task.AsyncListenableTaskExecutor
import org.springframework.stereotype.Component
import org.springframework.util.concurrent.FailureCallback
import org.springframework.util.concurrent.SuccessCallback

@Component
class Task(@Qualifier("applicationTaskExecutor") val asyncListenableTaskExecutor: AsyncListenableTaskExecutor) :
	ApplicationRunner {
	override fun run(args: ApplicationArguments?) {
		asyncListenableTaskExecutor.submitListenable {
			println("in " + Thread.currentThread().name)
		}.addCallback(SuccessCallback {
			println("ok")
		}, FailureCallback { println("fail") })
	}
}
