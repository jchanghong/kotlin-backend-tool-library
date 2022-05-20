package org.apache.myfaces.json;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.myfaces.MavenAppTest;
import org.apache.myfaces.beans.Bean1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MyJsonTests extends MavenAppTest {
	@Test
	void jsontest(@Autowired ObjectMapper objectMapper) throws JsonProcessingException {
		final Bean1 bean1 = new Bean1();
		bean1.setName("hello");
		Assertions.assertTrue(objectMapper.writeValueAsString(bean1).equals(JSONUtil.toJsonStr(bean1)) == true);
	}
}
