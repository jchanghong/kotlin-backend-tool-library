package org.apache.myfaces.jpa;

import org.apache.myfaces.MyJpaReposotory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MyRepositoryTests {

	@Autowired
	private MyJpaReposotory entityManager;


	@Test
	void testExample() throws Exception {

	}

}
