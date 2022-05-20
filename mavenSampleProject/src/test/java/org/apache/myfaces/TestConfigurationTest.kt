package org.apache.myfaces

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration

@SpringBootTest(args = ["--app.test=one"])
class TestConfigurationTest {
	@Value("\${app.test}")
	lateinit var string: String

	@TestConfiguration
	class TestConfiguration1 {

	}

	@Test
	internal fun TestConfigurationtest() {
		assert("one" == string)
	}
}
