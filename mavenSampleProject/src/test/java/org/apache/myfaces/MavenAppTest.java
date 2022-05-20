package org.apache.myfaces;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class MavenAppTest {

	@Test
	void 测试app() {
		assertTrue(1 == 1);
	}
}
