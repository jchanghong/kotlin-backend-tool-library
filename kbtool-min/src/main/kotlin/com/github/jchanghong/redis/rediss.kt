package com.github.jchanghong.redis

import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.api.sync.RedisCommands
import io.lettuce.core.pubsub.RedisPubSubAdapter
import java.util.function.Consumer

val json = """
    {
        "okNtp": 0,
        "channelNtpAt": "",
        "recordPercentage": 0,
        "okStatus": 0,
        "okRecord": 0,
        "recordLength": 0,
        "indexCode": "",
        "okQuality": 0,
        "recordLists": [
            {
                "length": 0,
                "hour": 0,
                "percentage": 0
            }
        ],
        "channelNtpImgUrl": "",
        "channelCapatureAt": ""
    }
""".trimIndent().replace("""\s+""".toRegex(), "")

fun main() {
	val helper = RedisHelper("")
	helper.subscribe(listOf("topic2")) {
		println(it.first + "   " + it.second)
	}
	helper.redisCommands.publish("topic2", "sasa")

// application flow continues

}

/** 一个对象对应一个redis服务器*/
class RedisHelper(ip: String, port: Int = 6379, password: String? = null) {
	val redisClient by lazy {
		val builder = RedisURI.builder().withHost(ip)
			.withPort(port)
		if (!password.isNullOrBlank()) builder.withPassword(password.subSequence(0,password.length))
		RedisClient.create(builder.build())
	}
	val connect: StatefulRedisConnection<String, String> = redisClient.connect()
	val pubSubConnection by lazy { redisClient.connectPubSub() }
	val redisCommands: RedisCommands<String, String> = connect.sync()
	val asyncCommands: RedisAsyncCommands<String, String> = connect.async()
	fun subscribe(topics: List<String>, consumer: Consumer<Pair<String, String?>>): Unit {
		pubSubConnection.addListener(object : RedisPubSubAdapter<String, String>() {
			override fun message(channel: String, message: String?) {
				consumer.accept(channel to message)
			}
		})
		pubSubConnection.sync().subscribe(*topics.toTypedArray())
	}

	fun close() {
		connect.close()
		pubSubConnection.close()
		redisClient.shutdown()
	}
}
