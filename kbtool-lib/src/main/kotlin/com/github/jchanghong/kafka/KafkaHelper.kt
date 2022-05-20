package com.github.jchanghong.kafka

import cn.hutool.core.thread.ThreadUtil
import cn.hutool.core.util.RandomUtil
import com.github.jchanghong.gson.logger
import com.github.jchanghong.log.kError
import com.github.jchanghong.log.kInfo
import org.apache.kafka.clients.admin.*
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.KafkaFuture
import java.util.*
import java.util.concurrent.*
import java.util.function.Consumer
import kotlin.concurrent.thread

enum class OffsetReset {
    earliest,
    latest
}

/** 一个对象一套kafka配置，多个kafka，需要建立多个对象,earliest,latest
 * */
class KafkaHelper(
    val bootstrap: String,
    val groupId: String,
    val topics: List<String>,
    val action: Consumer<ConsumerRecord<String?, String?>>,
    val offsetReset: OffsetReset = OffsetReset.latest,
    val threadCount: Int,
    val commitintervalms: Int = 60000
) {
    lateinit var threadExecutor: ThreadPoolExecutor
    lateinit var threadList: List<KafkaConsumerRunner>
    lateinit var threadListManualPartition: List<KafkaConsumerRunnerManualPartition>

    //    private val singleThreadExecutor = Executors.newSingleThreadExecutor()
    val mProps: Properties = getAndSetProps(bootstrap, groupId)
    val mProducer: KafkaProducer<String, String> by lazy { KafkaProducer<String, String>(mProps) }

    //    private val mConsumer: KafkaConsumer<String, String> by lazy { KafkaConsumer<String, String>(mProps) }
    val adminClient: AdminClient by lazy { KafkaAdminClient.create(mProps) }

    // 配置Kafka
    private fun getAndSetProps(bootstrap: String, groupId: String? = null): Properties {
        val props = Properties()
        props["bootstrap.servers"] = bootstrap
//        props.put("retries", 2) // 重试次数
        props.put("batch.size", 16384) // 批量发送大小
//        props.put("buffer.memory", 33554432) // 缓存大小，根据本机内存大小配置
//        props.put("linger.ms", 1000) // 发送频率，满足任务一个条件发送

        props.put("acks", "1")
        if (!groupId.isNullOrBlank()) {
            props.setProperty("group.id", groupId)
        }
        props.setProperty("max.partition.fetch.bytes", "52428800")
        props.setProperty("receive.buffer.bytes", "-1")
        props.setProperty("enable.auto.commit", "true")
        props.setProperty("auto.offset.reset", offsetReset.name)
        props.setProperty("auto.commit.interval.ms", commitintervalms.toString())
        props.setProperty("max.poll.records", "500")
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        props["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        props["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        return props
    }

    @JvmOverloads
    fun createTopic(name: String, p: Int = 8, r: Short = 1) {
        val newTopic = NewTopic(name, p, r)
        val newTopicList: MutableCollection<NewTopic> = ArrayList()
        newTopicList.add(newTopic)
        val createTopicsResult = adminClient.createTopics(newTopicList)
        for (entry in createTopicsResult.values()) {
            try {
                entry.value.get()
                Thread.sleep(2000)
            } catch (e: Exception) {
                kError(e.message, e)
            }
            kInfo("createTopic ${entry.key}")
        }
    }

    fun deleteTopic(name: String) {
        val deleteTopicsResult = adminClient.deleteTopics(Arrays.asList(name))
        for ((k, v) in deleteTopicsResult.values()) {
            try {
                v.get()
                Thread.sleep(2000)
            } catch (e: Exception) {
                kError(e.message, e)
            }
            kInfo("deleteTopic $k")
        }
    }

    fun listAllTopic(): Set<String> {
        val result: ListTopicsResult = adminClient.listTopics()
        val names = result.names()
        try {
            return names.get()
        } catch (e: InterruptedException) {
            kError(e.message, e)
        } catch (e: ExecutionException) {
            kError(e.message, e)
        }
        return emptySet()
    }

    fun getTopic(name: String): TopicDescription? {
        val describeTopics: DescribeTopicsResult = adminClient.describeTopics(Arrays.asList(name))
        val values: Collection<KafkaFuture<TopicDescription>> = describeTopics.values().values
        if (values.isEmpty()) {
            kInfo("找不到描述信息")
        } else {
            for (value in values) {
                return value.get()
            }
        }
        return null
    }

    fun produce(topic: String, value: String, key: String? = null): Future<RecordMetadata>? {
        val future =
            mProducer.send(ProducerRecord(topic, key ?: "${System.nanoTime()}${RandomUtil.randomString(20)}", value))
        return future
    }

    fun testProduce(topic: String, number: Long) {
        thread {
            for (i in 1..number) {
                produce(topic, "test$i")?.get()
                ThreadUtil.sleep(1000)
            }
        }
    }


    fun startConsumer() {
        check(threadCount > 0)
        threadExecutor = Executors.newFixedThreadPool(
            threadCount,
            ThreadUtil.newNamedThreadFactory(bootstrap.trim(), false)
        ) as ThreadPoolExecutor
        threadList = (1..threadCount).map {
            val kafkaConsumer = KafkaConsumer<String?, String?>(mProps)
            val kafkaConsumerRunner = KafkaConsumerRunner(kafkaConsumer, topics, action)
            kafkaConsumerRunner
        }
        threadList.forEach { threadExecutor.submit(it) }
    }

    fun startConsumerManualPartition() {
        check(threadCount > 0)
        threadExecutor = Executors.newFixedThreadPool(
            threadCount,
            ThreadUtil.newNamedThreadFactory(bootstrap.trim(), false)
        ) as ThreadPoolExecutor
        threadListManualPartition = (1..threadCount).map {
            val kafkaConsumerRunner = KafkaConsumerRunnerManualPartition(this, topics, it - 1, action)
            kafkaConsumerRunner
        }
        threadListManualPartition.forEach { threadExecutor.submit(it) }
    }

    fun startConsumerManualPartition(partition: Int) {
        check(threadCount > 0)
        threadExecutor = Executors.newFixedThreadPool(
            threadCount,
            ThreadUtil.newNamedThreadFactory(bootstrap.trim(), false)
        ) as ThreadPoolExecutor
        threadListManualPartition = (1..1).map {
            val kafkaConsumerRunner = KafkaConsumerRunnerManualPartition(this, topics, partition, action)
            kafkaConsumerRunner
        }
        threadListManualPartition.forEach { threadExecutor.submit(it) }
    }

    fun shutdown() {
        threadList.forEach { it.shutdown() }
        ThreadUtil.sleep(3000)
        threadExecutor.shutdown()
        threadExecutor.awaitTermination(10, TimeUnit.SECONDS)
        logger.info("shutdown KafkaHelper")
    }
}


fun main() {
//    println(Long.MAX_VALUE / 1000 / 3600)
    val kafkaHelper = KafkaHelper("55555.1.172.137:9092", "jchtest", listOf("ORIGIN_SNAP_IMAGE_INFO_TOPIC"), Consumer {
        val value =
            it.value()
//        println(it.partition().toString()+"offser"+it.offset().toString())
//    val kafkaHelper = KafkaHelper("55555.1.43.110:9092", "jchtest", listOf("camera_status_r2p16"), Consumer {
//        println(it.value())
    }, OffsetReset.latest, 10, 10000)
//    try {
//        kafkaHelper.deleteTopic("ITMS_CoreService_RealtimeDetectorCommand")
//    } catch (e: Exception) {
//    }
    kafkaHelper.startConsumerManualPartition()
    println("11111111111111111111111")
    println("11111111111111111111111222222222222")
//    kafkaHelper.deleteTopic("camera_tag_r2p16")
//    kafkaHelper.createTopic("camera_status_r2p16",8,2)
//    kafkaHelper.createTopic("camera_tag_r2p16",8,2)
//    kafkaHelper.createTopic("ITMS_CoreService_RealtimeDetectorCommand", 8, 2)

//    for (i in (1..8)) {
//        val kafkaHelper = KafkaHelper("55555.1.43.110:9092", "group3", listOf("testr2p8"), Function {
//            kInfo(
//                    it.value()
//                            .toString() + " group1 consumer1 ${it.partition()} ${it.offset()}  ${it.key()}  ${Date(it.timestamp()).toStrOrNow()}"
//            )
//        })
//        kafkaHelper.startConsumer()
//    }
//    (1..10).toList().forEach {
//        kafkaHelper.produce("testr2p8", "1gentest${it}" + DateUtil.now())
//    }
//    kafkaHelper.startConsumer()
//    ThreadUtil.sleep(80000)

//    println("end1")
}
