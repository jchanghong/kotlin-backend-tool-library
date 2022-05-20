//package com.github.jchanghong.elasticsearch
//
//import cn.hutool.json.JSONUtil
//import com.github.jchanghong.gson.jsonByPath
//import com.github.jchanghong.gson.jsonToObject
//import com.github.jchanghong.gson.toJsonStr
//import com.github.jchanghong.http.HttpHelper
//import com.github.jchanghong.http.bodyString
//import org.apache.http.HttpHost
//import org.elasticsearch.action.ActionListener
//import org.elasticsearch.action.delete.DeleteRequest
//import org.elasticsearch.action.delete.DeleteResponse
//import org.elasticsearch.action.get.GetRequest
//import org.elasticsearch.action.get.GetResponse
//import org.elasticsearch.action.index.IndexRequest
//import org.elasticsearch.action.index.IndexResponse
//import org.elasticsearch.action.update.UpdateRequest
//import org.elasticsearch.action.update.UpdateResponse
//import org.elasticsearch.client.Cancellable
//import org.elasticsearch.client.RequestOptions
//import org.elasticsearch.client.RestClient
//import org.elasticsearch.client.RestHighLevelClient
//import org.elasticsearch.client.core.GetSourceRequest
//import org.elasticsearch.client.core.GetSourceResponse
//import org.elasticsearch.common.xcontent.XContentType
//import org.slf4j.LoggerFactory
//import java.util.function.Consumer
//import kotlin.collections.List
//import kotlin.collections.Map
//import kotlin.collections.MutableMap
//import kotlin.collections.first
//import kotlin.collections.map
//import kotlin.collections.mapIndexed
//import kotlin.collections.mapNotNull
//import kotlin.collections.set
//import kotlin.collections.toMap
//import kotlin.collections.toTypedArray
//
//class ElasticsearchHelper(ips: List<String>) {
//    /** http://127.0.0.1:9200*/
//    var serverUrl = "http://${ips.first()}:9200"
//    private val logger = LoggerFactory.getLogger(ElasticsearchHelper::class.java)
//    var debug = true
//    val client: RestHighLevelClient = RestHighLevelClient(
//        RestClient.builder(*ips.map { HttpHost(it, 9200, "http") }.toTypedArray())
//    )
//
//    fun index(index: String, id: String, json: String, consumer: Consumer<IndexResponse>): Cancellable {
//        val indexRequest = IndexRequest(index)
//        indexRequest.id(id)
//        indexRequest.source(json, XContentType.JSON)
//        val indexAsync =
//            client.indexAsync(indexRequest, RequestOptions.DEFAULT, object : ActionListener<IndexResponse> {
//                override fun onResponse(p0: IndexResponse?) {
//                    if (debug) logger.info(p0?.index + " index ok")
//                    consumer.accept(p0 ?: return)
//                }
//
//                override fun onFailure(p0: Exception?) {
//                    logger.error(p0?.localizedMessage, p0)
//                }
//
//            })
//        return indexAsync
//    }
//
//    fun get(index: String, id: String, consumer: Consumer<String>): Cancellable {
//        val indexRequest = GetRequest(index)
//        indexRequest.id(id)
//        val indexAsync = client.getAsync(indexRequest, RequestOptions.DEFAULT, object : ActionListener<GetResponse> {
//            override fun onResponse(getResponse: GetResponse) {
//                if (debug) logger.info(getResponse.sourceAsString + " get ok")
//                if (getResponse.isExists) {
//                    val sourceAsString: String = getResponse.getSourceAsString()
//                    consumer.accept(sourceAsString)
//                } else {
//                }
//            }
//
//            override fun onFailure(p0: Exception?) {
//                logger.error(p0?.localizedMessage, p0)
//            }
//
//        })
//        return indexAsync
//    }
//
//    fun getSource(index: String, id: String, consumer: Consumer<Map<String, Any>>): Cancellable {
//        val getSourceRequest = GetSourceRequest(index, id)
//        val indexAsync =
//            client.getSourceAsync(getSourceRequest, RequestOptions.DEFAULT, object : ActionListener<GetSourceResponse> {
//                override fun onResponse(getResponse: GetSourceResponse) {
//                    if (debug) logger.info(getResponse.source.toString() + "getsource ok")
//                    consumer.accept(getResponse.source)
//                }
//
//                override fun onFailure(p0: Exception?) {
//                    logger.error(p0?.localizedMessage, p0)
//                }
//
//            })
//        return indexAsync
//    }
//
//    fun delete(index: String, id: String): Cancellable {
//        val deleteRequest = DeleteRequest(index, id)
//        val indexAsync =
//            client.deleteAsync(deleteRequest, RequestOptions.DEFAULT, object : ActionListener<DeleteResponse> {
//                override fun onResponse(getResponse: DeleteResponse) {
//                    if (debug) logger.info(getResponse.id + " delete ok")
//                }
//
//                override fun onFailure(p0: Exception?) {
//                    logger.error(p0?.localizedMessage, p0)
//                }
//
//            })
//        return indexAsync
//    }
//
//    fun update(index: String, id: String, json: String): Cancellable {
//        val updateRequest = UpdateRequest(index, id)
//        updateRequest.doc(json, XContentType.JSON)
//        val indexAsync =
//            client.updateAsync(updateRequest, RequestOptions.DEFAULT, object : ActionListener<UpdateResponse> {
//                override fun onResponse(getResponse: UpdateResponse) {
//                    if (debug) logger.info("update ok" + getResponse.id)
//                }
//
//                override fun onFailure(p0: Exception?) {
//                    logger.error(p0?.localizedMessage, p0)
//                }
//
//            })
//        return indexAsync
//    }
//
//    //    fun search(index: String, id: String, json: String): Cancellable {
////        val searchRequest = SearchRequest()
////        val sourceBuilder = SearchSourceBuilder()
////        sourceBuilder.query(QueryBuilders.matchQuery("", ""))
////        sourceBuilder.from(0)
////        sourceBuilder.size(5)
////        sourceBuilder.timeout(TimeValue(60, TimeUnit.SECONDS))
////        val highlightBuilder = HighlightBuilder()
////        val highlightTitle: HighlightBuilder.Field = HighlightBuilder.Field("title")
////        highlightTitle.highlighterType("unified")
////        highlightBuilder.field(highlightTitle)
////        val highlightUser: HighlightBuilder.Field = HighlightBuilder.Field("user")
////        highlightBuilder.field(highlightUser)
////        sourceBuilder.highlighter(highlightBuilder)
////        searchRequest.source(sourceBuilder)
////        val indexAsync = client.searchAsync(searchRequest, RequestOptions.DEFAULT, object : ActionListener<SearchResponse> {
////            override fun onResponse(getResponse: SearchResponse) {
////
////            }
////
////            override fun onFailure(p0: Exception?) {
////                logger.error(p0?.localizedMessage)
////            }
////
////        })
////        return indexAsync
////    }
//    fun showTables(): String {
//        val postJson = HttpHelper.postJsonStringAsyn(
//            "${serverUrl}/_sql?format=txt&pretty", """
//        {
//          "query": "show tables"
//        }
//    """.trimIndent()
//        )
//        return postJson.get().bodyString()
//    }
//
//    fun DESCRIBE(table: String): String {
//        val postJson = HttpHelper.postJsonStringAsyn(
//            "${serverUrl}/_sql?format=txt&pretty", """
//        {
//          "query": "DESCRIBE $table"
//        }
//    """.trimIndent()
//        )
//        return postJson.get().bodyString()
//    }
//
//    fun queryForJson(sql: String): String {
//        val postJson = HttpHelper.postJsonStringAsyn(
//            "${serverUrl}/_sql?format=json&pretty", """
//        {
//          "query": "$sql"
//        }
//    """.trimIndent()
//        )
////        println(postJson)
//        val jsonToObject = postJson.get().bodyString().jsonToObject<Map<String, List<Any>>>()
//        val keys = (jsonToObject?.get("columns") as? List<Map<String, String>>?)?.mapNotNull { it["name"] }
//        val list = jsonToObject?.get("rows")?.mapNotNull { it as? List<Any?> }
//            ?.mapNotNull {
//                val toMap = keys?.mapIndexed { index, s -> s to it[index] }?.toMap()
//                toMap
//            }
//        val toString = list.toJsonStr().toString()
//        val message = JSONUtil.formatJsonStr(toString)
////        println(message)
//        return message
//    }
//
//    fun queryForTxt(sql: String): String {
//        val postJson = HttpHelper.postJsonStringAsyn(
//            "${serverUrl}/_sql?format=txt&pretty", """
//        {
//          "query": "$sql"
//        }
//    """.trimIndent()
//        )
//        return postJson.get().bodyString()
//    }
//
//    fun index(table: String, id: Long, data: String): String {
//        val postJson = HttpHelper.postJsonStringAsyn("${serverUrl}/${table}/_doc/${id}", data).get().bodyString()
//        return postJson
//    }
//
//    fun createIndex(table: String, ik: Boolean = false): String {
//        val postJson = HttpHelper.postJsonStringAsyn(
//            "${serverUrl}/${table}", """
//            {
//              "mappings": {
//                 "properties": {
//                        "content": {
//                            "type": "text"
//                            ${
//                if (ik) """",analyzer": "ik_max_word",
//                            "search_analyzer": "ik_smart"""" else ""
//            }
//                        }
//                    }
//              }
//            }
//        """.trimIndent()
//        )
//        return postJson.get().bodyString()
//    }
//
//    fun createIndexAsyoutype(table: String, ik: Boolean = false): String {
//        val postJson = HttpHelper.postJsonStringAsyn(
//            "${serverUrl}/${table}", """
//            {
//              "mappings": {
//                 "properties": {
//                        "content": {
//                            "type": "search_as_you_type"
//                        }
//                    }
//              }
//            }
//        """.trimIndent()
//        )
//        return postJson.get().bodyString()
//    }
//
//    @JvmOverloads
//    fun query(field: String, query: String, table: String? = null): String {
//        val body = """
//            {
//
//              "query": {
//
//                "match": { "$field": "$query" }
//
//              },
//
//              "highlight": {
//
//                "fields": {
//
//                  "$field": {}
//
//                }
//
//              }
//
//            }
//        """.trimIndent()
//        val s = if (table != null) "${table}/" else ""
//        val postJson = HttpHelper.postJsonStringAsyn("${serverUrl}/${s}_search?pretty", body).get().bodyString()
//        val jsonToObject = postJson.jsonByPath("hits.hits").jsonToObject<List<Map<String, Any?>>>()
//        val mapNotNull = jsonToObject?.mapNotNull {
//            val any = it["_source"] as? MutableMap<String, Any?>?
//            if (any != null) {
//                val get = (it.get("highlight") as? Map<String, Any?>)?.get(field)
//                any["highlight"] = (get as? List<String>?)?.get(0)
//            }
//            any
//        }
//        val toString = mapNotNull.toJsonStr().toString()
//        return JSONUtil.formatJsonStr(toString)
//    }
//}
//
//internal data class D1(var content: String)
//
//fun main() {
//
//
//}