package com.github.jchanghong.kafka

import cn.hutool.core.util.ClassUtil
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer

class KafkaConsumerRunner(
    private val consumer: KafkaConsumer<String?, String?>,
    private val topics: List<String>,
    val messageHandle: Consumer<ConsumerRecord<String?, String?>>
) :
    Runnable {
    private val closed = AtomicBoolean(false)
    private val log = LoggerFactory.getLogger(KafkaConsumerRunner::class.java)
    override fun run() {
        try {
            val method = ClassUtil.getDeclaredMethod(KafkaConsumer::class.java, "subscribe", List::class.java)
            val pollMethod = ClassUtil.getDeclaredMethod(KafkaConsumer::class.java, "poll", Duration::class.java)
            if (method != null) {
//            老版本版本kafka
                method.invoke(consumer, topics)
//            mConsumer.subscribe(topics)
                while (!closed.get()) {
                    val consumerRecords: ConsumerRecords<String?, String?> = if (pollMethod != null) consumer.poll(
                        Duration.ofMillis(
                            3600000
                        )
                    ) else consumer.poll(Duration.ofMillis(3600000))
                    if (consumerRecords.isEmpty) continue
                    consumerRecords.forEach {
                        try {
                            messageHandle.accept(it)
                        } catch (e: Throwable) {
                            log.error(e.localizedMessage, e)
                        }
                    }
                    // Handle new records
                }
            }

        } catch (e: WakeupException) {
            // Ignore exception if closing
            if (!closed.get()) throw e
        } catch (e2: Throwable) {
            // Ignore exception if closing
        } finally {
            consumer.close()
        }
    }

    // Shutdown hook which can be called from a separate thread
    fun shutdown() {
        closed.set(true)
        consumer.wakeup()
    }
}
