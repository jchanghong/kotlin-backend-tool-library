package org.apache.myfaces.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@JdbcTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Slf4j
public class MyJdbcTransactionalTests {
	@Test
	void name(@Autowired DataSource dataSource) {
		Assertions.assertTrue(dataSource != null);
	}
}
