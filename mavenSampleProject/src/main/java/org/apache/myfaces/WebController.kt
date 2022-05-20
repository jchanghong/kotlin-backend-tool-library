package org.apache.myfaces

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WebController {
	@GetMapping("/hello")
	fun hello(): String {
		return "hello"
	}
}

@FeignClient(value = "testCommonAllApp", path = "/test")
interface FeignClientTest {
    @GetMapping("/hello")
	fun hello(): String
}
