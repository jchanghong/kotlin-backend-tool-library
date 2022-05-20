package org.apache.myfaces.kafka;


import org.apache.myfaces.MavenAppTest;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.concurrent.ExecutionException;

@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "spring.kafka.bootstrap-servers")
public class MyApplicationKafkaTests extends MavenAppTest {
	//    @Test
	void testkafka(@Autowired KafkaTemplate<String, String> kafkaTemplate) throws ExecutionException, InterruptedException {
		final SendResult<String, String> result = kafkaTemplate.send("test", "test").get();
		Assertions.assertTrue(result.getRecordMetadata() != null);
	}
}
