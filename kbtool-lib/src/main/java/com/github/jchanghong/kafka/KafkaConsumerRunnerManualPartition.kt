package com.github.jchanghong.kafka

import cn.hutool.core.thread.ThreadUtil
import okhttp3.internal.closeQuietly
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.TopicPartition
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.function.Consumer

/** 手动分配分区，。从0开始*/
class KafkaConsumerRunnerManualPartition(
    val kafkaHelper: KafkaHelper,
    topics: List<String>,
    private val partition: Int,
    val messageHandle: Consumer<ConsumerRecord<String?, String?>>
) :
    Runnable {
    private val log = LoggerFactory.getLogger(KafkaConsumerRunnerManualPartition::class.java)
    private val list: List<TopicPartition> = topics.map { TopicPartition(it, partition) }
    private var consumer: KafkaConsumer<String?, String?>? = null

    /** 异常后重新初始化！！！！！*/
    fun reInitKafkaConsumer(): Unit {
        kotlin.runCatching { consumer?.commitSync() }
        kotlin.runCatching { consumer?.close() }
        kotlin.runCatching { consumer?.closeQuietly() }
        consumer = null
        consumer = KafkaConsumer<String?, String?>(kafkaHelper.mProps)
        consumer?.assign(list)
    }

    override fun run() {
        reInitKafkaConsumer()
        while (true) {
            try {
                if (consumer == null) {
                    reInitKafkaConsumer()
                }
                pollData()
            } catch (e: Throwable) {
                log.error(e.localizedMessage + "kafka分区${partition} poll异常,重新初始化消费者后停止3秒")
                reInitKafkaConsumer()
                ThreadUtil.sleep(3000)
            }
        }
    }

    private fun pollData() {
        consumer ?: return
        val consumerRecords: ConsumerRecords<String?, String?> = consumer!!.poll(Duration.ofMillis(30000)) ?: return
        if (consumerRecords.isEmpty) return
        consumerRecords.forEach {
            try {
                if (it == null) return@forEach
                messageHandle.accept(it)
            } catch (e: Throwable) {
                log.error(e.localizedMessage + "kafka消息处理异常 ${it.topic()} ${it.partition()}")
            }
        }
    }
}
