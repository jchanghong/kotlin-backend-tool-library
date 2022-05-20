package com.github.jchanghong.kafka

import cn.hutool.core.thread.ThreadUtil
import cn.hutool.core.util.RandomUtil
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.listener.ConsumerAwareErrorHandler
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.support.SendResult
import org.springframework.util.concurrent.ListenableFuture
import java.util.function.Consumer
import kotlin.concurrent.thread

/** 随机key*/
fun KafkaTemplate<String, String?>?.sendAutoKey(
	topic: String,
	data: String?
): ListenableFuture<SendResult<String?, String?>?>? {
	return this?.send(topic, "${RandomUtil.randomString(20)}${System.nanoTime()}", data ?: "")
}

class SpringKafkaHelper @JvmOverloads constructor(private val ip: String, private val port: Int = 9092) {
	val logger: Logger = LoggerFactory.getLogger(SpringKafkaHelper::class.java)
	var mMessageListenerContainer: ConcurrentMessageListenerContainer<String?, String?>? = null
	fun startConsumerAsyn(
		topics: List<String>,
		group: String,
		concurrency: Int,
		consumer: Consumer<ConsumerRecord<String, String?>>,
		config: Consumer<ContainerProperties>? = null
	) {
		if (mMessageListenerContainer != null) {
			mMessageListenerContainer!!.start()
			return
		}
		val containerProps = ContainerProperties(*topics.toTypedArray())
		config?.accept(containerProps)
		containerProps.messageListener = object : MessageListener<String, String?> {
			override fun onMessage(message: ConsumerRecord<String, String?>) {
				consumer.accept(message)
			}
		}
		val container: ConcurrentMessageListenerContainer<String?, String?> = createContainer(containerProps, group)
		container.setBeanName("kafkaMessageListenerContainerJCH")
		container.concurrency = concurrency
//		container.setErrorHandler(object : ConsumerAwareErrorHandler {
//			override fun handle(
//				thrownException: Exception?,
//				data: ConsumerRecord<*, *>?,
//				consumer: org.apache.kafka.clients.consumer.Consumer<*, *>?
//			) {
//				if (data == null || consumer == null) return
//				consumer.seek(TopicPartition(data.topic(), data.partition()), data.offset() + 1)
//			}
//		})
		mMessageListenerContainer = container
		container.start()
	}

	/** 后台现场，1秒插入一条数据*/
	fun testAutoCommit(topic: String) {
		val template = createTemplate()
		thread {
			for (i in (1..2000)) {
				template.sendAutoKey(topic, i.toString())?.get()
				ThreadUtil.sleep(1000)
			}
		}
	}

	fun kafkaListenerContainerFactory(group: String): ConcurrentKafkaListenerContainerFactory<String, String> {
		val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
		factory.setConsumerFactory(DefaultKafkaConsumerFactory(consumerProps(group)))
		return factory
	}

	fun createContainer(
		containerProps: ContainerProperties,
		group: String
	): ConcurrentMessageListenerContainer<String?, String?> {
		val props = consumerProps(group)
		val cf = DefaultKafkaConsumerFactory<String?, String?>(props)
		val listenerContainer = ConcurrentMessageListenerContainer(cf, containerProps)
		return listenerContainer
	}

	fun createTemplate(): KafkaTemplate<String, String?> {
		val senderProps = senderProps()
		val pf = DefaultKafkaProducerFactory<String, String?>(senderProps)
		return KafkaTemplate(pf)
	}

	fun consumerProps(group: String): Map<String, Any> {
		val props: MutableMap<String, Any> = HashMap()
		props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "${ip}:${port}"
		props[ConsumerConfig.GROUP_ID_CONFIG] = group
		props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = true
		props[ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG] = "5000"
//        props[ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG] = "15000"
		props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
		props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
		return props
	}

	fun senderProps(): Map<String, Any> {
		val props: MutableMap<String, Any> = HashMap()
		props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "${ip}:${port}"
//        props[ProducerConfig.RETRIES_CONFIG] = 1
//        props[ProducerConfig.BATCH_SIZE_CONFIG] = 16384
//        props[ProducerConfig.LINGER_MS_CONFIG] = 1
//        props[ProducerConfig.BUFFER_MEMORY_CONFIG] = 33554432
		props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
		props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
		return props
	}
}

fun main() {
	val helper = SpringKafkaHelper("55555.1.43.109")
	helper.startConsumerAsyn(listOf("test", "test1"), "test", 9, {
		println(it.value() + "======${it.topic()}======" + Thread.currentThread().name)
	})
	helper.testAutoCommit("test")
	helper.testAutoCommit("test1")

//    -----test2 55555.1.172.137:9092
//    val helper = SpringKafkaHelper("55555.1.172.137",9092)
//    helper.startConsumerAsyn("ORIGIN_SNAP_IMAGE_INFO_TOPIC", "jchtest",  8, {
//        println(it.value() + "============" + Thread.currentThread().name)
//        ThreadUtil.sleep(2000)
//    })
}
