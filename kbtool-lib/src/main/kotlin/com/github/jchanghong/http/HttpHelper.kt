package com.github.jchanghong.http

import cn.hutool.core.util.CharsetUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.core.util.URLUtil
import cn.hutool.crypto.SecureUtil
import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONUtil
import com.github.jchanghong.http.utils.JchCookieJar
import com.github.jchanghong.http.utils.JchTrustAllCerts
import com.github.jchanghong.http.utils.JchTrustAllHostnameVerifier
import com.github.jchanghong.lang.ThreadHelper
import com.google.common.net.MediaType
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.security.SecureRandom
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager

/** okhttp
 * https支持，pvia模拟登陆。
 * pvia开头的函数就是
 * */
object HttpHelper {
    /** 每2分钟获取一次，防止登录过期*/
    fun pvia_startTimer() {
        ThreadHelper.newScheduledThreadPool2.scheduleWithFixedDelay({
            kotlin.runCatching {
                if (systemCsrfUrlMap.isEmpty()) return@runCatching
                for (url in systemCsrfUrlMap.values) {
                    getSyn(url)
                }
            }
        }, 5, 5, TimeUnit.MINUTES)
    }

    private val logger = LoggerFactory.getLogger(HttpHelper::class.java)
    var debug = false

    /** 单点登录url路径 ,比如/portal/cas/loginPage*/
    private var pviaLoginUrlPath = "/portal/cas/loginPage"

    /** 运管登录路径，比如/center/login*/
    private var pviaLoginUrlPathCenter = "/center/login"

    /** 运管密码*/
    private var pviaCenterPassword = ""

    /** 比如https://1.1.1.2*/
    private var pviaIpAndPort = ""

    /** pvia 密码*/
    private var pviaPassword = ""
    private var pviaLoginUser = ""

    /** http://1.1.1.2:8001 */
    private var pviaIpAndPortCenter = ""

    /** pvia配置过*/
    private fun pviaSeted(): Boolean =
        pviaIpAndPort.isNotBlank() && pviaPassword.isNotBlank() && pviaLoginUser.isNotBlank()

    private fun pviaCenterSeted(): Boolean = pviaIpAndPortCenter.isNotBlank() && pviaCenterPassword.isNotBlank()

    /** 需要登陆的pvia url。*/
    private fun pviaNeedLoginUrl(url: String): Boolean {
        val firstPath = getFirstPath(url)
        if (firstPath.isNotBlank() && firstPath in whiteFirstPath) return false
        if (pviaCenterSeted() && "/center/login" in url) return false
        if (pviaCenterSeted() && "/center/api/session" in url) return false
        if (pviaSeted() && pviaIpAndPort in url) return true
        if (pviaCenterSeted() && pviaIpAndPortCenter in url) return true
        return false
    }

    /**
     * 设置pvia的地址和密码等
     * IpAndPort比如https://1.1.1.2
     *
     * 单点登录url路径 ,比如/portal/cas/loginPage
     */
    @JvmOverloads
    fun pviaConfig(
        IpAndPort: String,
        password: String = "Kh@kpi123456",
        loginUser: String = "kpi",
        pviaLoginUrlPath2: String = "/portal/cas/loginPage"
    ) {
        pviaIpAndPort = IpAndPort
        pviaPassword = password
        pviaLoginUser = loginUser
        pviaIpAndPortCenter = pviaLoginUrlPath2
        val ipAndPort = pviaIpAndPort
        val systemCsrfUrlMap2 = mapOf(
            "ibody-web" to "$ipAndPort/ibody-web/index.do",
//    "ctm01appportal" to "$ipAndPort/ctm01appportal/",
//    "portal" to "$ipAndPort/portal/cas/loginPage?service=$ipAndPort/portal",
            "iface-web" to "$ipAndPort/iface-web/index.do",
            "xcascade-web" to "$ipAndPort/xcascade-web/index.do",
            "xmap-web" to "$ipAndPort/xmap-web/third/targetLocation/index.do?mapId=metisFixed&fullScreen=0",
            "ivehicle-web" to "$ipAndPort/ivehicle-web/view/list.do",
            "ibody-web" to "$ipAndPort/ibody-web/index.do?m=bodySearch",
            "balarm" to "$ipAndPort/balarm/",
            "viidba-web" to "$ipAndPort/viidba-web/index.do",
            "idad-web" to "$ipAndPort/idad-web/",
            "ialarm-web" to "$ipAndPort/ialarm-web/search.do",
            "xnamelist-web" to "$ipAndPort/xnamelist-web/manageList/index.html",
            "csearch-web" to "$ipAndPort/csearch-web/community-search",
//        车辆头是 _csrf: QFKgfCqJ-aZCKgnqpbXvfak60seuWo0NXLZY
        )
        systemCsrfUrlMap.clear()
        systemCsrfUrlMap.putAll(systemCsrfUrlMap2)
        systemCsrfUrlMap.putAll(customSystemCsrfUrlMap)
    }

    /**
     *设置运管地址和密码等
     * IpAndPort比如http://1.1.1.2:8001
     *
     * 单点登录url路径 ,比如/center/login
     */
    @JvmOverloads
    fun pviaCenterConfig(IpAndPort: String, password: String, pviaLoginUrlPath2: String = "/center/login") {
        pviaIpAndPortCenter = IpAndPort
        pviaCenterPassword = password
        pviaLoginUrlPathCenter = pviaLoginUrlPath2
    }

    /**  不需要登陆 地址的 firstpath 集合*/
    var whiteFirstPath = mutableSetOf("portal", "bic", "center_cas")

    /** 哪些系统需要获取csrf头 value 是url 比如 "iface-web" to "$ipAndPort/iface-web/index.do", */
    val customSystemCsrfUrlMap = ConcurrentHashMap<String, String>()
    private val systemCsrfUrlMap = ConcurrentHashMap<String, String>()

    lateinit var okHttpClient: OkHttpClient

    /** key is domain+path 判断登陆状态*/
    val cookieStore = ConcurrentHashMap<String, ConcurrentHashMap<String, Cookie>>()

    /**  key是第一路径  判读登陆状态*/
    val cSRFRequestHeadMap = ConcurrentHashMap<String, Pair<String, String>>()

    /** 运管已经登陆？？？？*/
    private var isLoginPviaCenter = false
    private fun info(i: Any?, boolean: Boolean = false) {
        if (debug || boolean) {
            logger.info(i.toString())
        }
    }

    /** 通过url 得到 _csrf_header  _csrf 头和值 登陆之后调后！！！！！！！！！！！！！
     * 车辆  车辆头是 _csrf: QFKgfCqJ-aZCKgnqpbXvfak60seuWo0NXLZY  <meta name="_csrf" content="GEkzR7Hn-yzuDwN_EunrakKKM8w0ZwOuRxPk">
     * */
    private fun getCsrfNameAndValueFromHtml(url: String): Pair<String, String> {
        val firstPath = getFirstPath(url)
        val csrfUrl = systemCsrfUrlMap[firstPath] ?: return Pair("", "")
        val request = Request.Builder().url(csrfUrl).build()
        val html = okHttpClient.newCall(request).execute().bodyString()
        var pair: Pair<String, String>
        var first = ""
        var second = ""
        kotlin.runCatching {
            val document: Document = Jsoup.parse(html)
            val head = document.head()
            val metas = head.getElementsByTag("meta")
            for (meta in metas) {
                val name = meta.attr("name")
                val content = meta.attr("content")
                if ("_csrf" == name.lowercase() && content.isNotBlank()) {
                    second = content
                }
                if ("_csrf_header" == name.lowercase() && StrUtil.isNotBlank(content)) {
                    first = content
                }
            }
        }
        if (first.isBlank() && second.isBlank()) {
            return Pair("", "")
        }
        if (first.isBlank() && "ivehicle-web" == firstPath) {
            pair = Pair("_csrf", second)
        } else {
            pair = Pair(first, second)
        }
        info("getCsrfNameAndValueFromHtml $pair $url", true)
        return pair
    }

    /** https://1.1.172.2:80/portal/login/ajax/verifyCode.do 返回 portal */
    fun getFirstPath(url: String): String {
        return try {
            URLUtil.getPath(url).removePrefix("/").substringBefore("/")
        } catch (e: Exception) {
            info(e.localizedMessage)
            ""
        }
    }

    private fun createSSLSocketFactory(): SSLSocketFactory {
        val sc: SSLContext = SSLContext.getInstance("TLS")
        sc.init(null, arrayOf<TrustManager>(JchTrustAllCerts()), SecureRandom())
        return sc.socketFactory
    }

    /** 默认*/
    private var cSRFRequestHeadDefault = Pair("", "")

    /** 登陆返回登陆状态*/
    private fun loginPvia(user: String, password: String): Boolean {
        try {
            val formBody = FormBody.Builder()
                .add("userName", pviaLoginUser)
                .build()
            val response = okHttpClient.newCall(
                Request.Builder().url("${pviaIpAndPort}/portal/login/ajax/postLoginData.do")
                    .post(formBody).build()
            ).execute()
            val message = response.bodyString()
            info("login postLoginData.do:" + message)
            val parseObj1 = JSONUtil.parseObj(message)
            val parseObj = parseObj1.getJSONObject("data")
            val vcodestr = parseObj.getStr("vCode")
            val salt = parseObj.getStr("salt")
            val passtmp = SecureUtil.sha256(SecureUtil.sha256(password + salt) + vcodestr)
            val formBodyLogin = FormBody.Builder()
                .add("userName", user)
                .add("password", passtmp)
                .add("serviceUrl", """${pviaIpAndPort}/portal/cas/loginPage?service=${pviaIpAndPort}/portal""")
                .add("imageCode", "")
                .add("codeId", parseObj.getStr("codeId"))
                .add("userType", "0")
                .add("lang", "zh_CN")
                .build()
            val response1 = okHttpClient.newCall(
                Request.Builder().url("${pviaIpAndPort}/portal/login/ajax/submit.do")
                    .post(formBodyLogin).build()
            ).execute()
            val loginMessage = response1.bodyString()
            info("login/ajax/submit.do" + loginMessage)
            val url2 = JSONUtil.parseObj(loginMessage).getStr("data") ?: ""
            return if ("http" in url2) {
                val response2 = okHttpClient.newCall(Request.Builder().url(url2).build()).execute()
                response.close()
                response1.close()
                if (response2.code == 200) {
                    println("登陆pvia成功 $response2")
                    response2.close()
                    true
                } else {
                    response2.close()
                    false
                }
            } else {
                println("登陆pvia失败")
                response.close()
                response1.close()
                false
            }
        } catch (e: Exception) {
            info(e.localizedMessage, true)
            return false
        }

    }

    init {
        initClient()
    }

    private fun initClient() {
        val builder = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .addInterceptor { chain ->
                val oldRequest = chain.request()
                val oldRequestUrl = oldRequest.url.toString()
                if (!pviaNeedLoginUrl(oldRequestUrl)) {
                    return@addInterceptor chain.proceed(oldRequest)
                }
                info("用户请求pviaNeedLoginUrl===========" + oldRequest)
//                运管api报浏览器错误！！先调用/center/api/status接口就不会
//                if ("logservice/api" in oldRequest.url.toString()) {
//                    val request = oldRequest.newBuilder().url("${pviaIpAndPortCenter}/center/api/status").build()
//                    this.okHttpClient.newCall(request).execute().body?.close()
//                    chain.proceed(request).body?.close()
//                    return@addInterceptor chain.proceed()
//                }
                val firstPath = getFirstPath(oldRequestUrl)
                if (pviaIpAndPort in oldRequestUrl && cSRFRequestHeadMap.isEmpty()) {
//                    需要登陆
                    info("需要登陆pvia.........", true)
                    return@addInterceptor doLoginPviaAndGetCsrf(chain, oldRequest)
                }
                if (pviaIpAndPortCenter in oldRequestUrl && !isLoginPviaCenter) {
//                    需要登陆
                    info("需要登陆pvia运管.........", true)
                    return@addInterceptor doLoginPviaCenter(chain.proceed(oldRequest), chain, oldRequest)
                }
                val oldRes = chain.proceed(oldRequest)
                return@addInterceptor if (pviaLoginUrlPath in oldRequestUrl) {
//                        跳到cas登录页面，需要登录
                    info("需要登陆pvia.........", true)
                    doLoginPviaAndGetCsrf(chain, oldRequest)
                } else if (pviaLoginUrlPathCenter in oldRequestUrl) {
//                        跳到cas登录页面，需要登录
                    info("需要登陆pvia运管.........", true)
                    doLoginPviaCenter(oldRes, chain, oldRequest)
                } else if (pviaLoginUrlPath in oldRes.request.url.toString() && oldRes.code == 403 && systemCsrfUrlMap.containsKey(
                        firstPath
                    )
                ) {
//                        跳到cas登录页面，需要登录
                    cookieStore.clear()
                    cSRFRequestHeadMap.clear()
                    doLoginPviaAndGetCsrf(chain, oldRequest)
                } else {
                    oldRes
                }
            }
            .addNetworkInterceptor { chain ->
                var oldRequest = chain.request()
                val oldRequestUrl = chain.request().url.toString()
                if (!pviaSeted() && !pviaCenterSeted()) {
                    return@addNetworkInterceptor chain.proceed(chain.request())
                }
//                Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36
//                User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36 Edg/90.0.818.66
                oldRequest = oldRequest.newBuilder().header(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36"
                ).build()
                if (pviaIpAndPort in oldRequestUrl) {
                    val firstPath = getFirstPath(oldRequestUrl)
                    val csfrPair = cSRFRequestHeadMap[firstPath] ?: Pair("", "")
                    if (csfrPair.first.isNotBlank() && csfrPair.second.isNotBlank()) {
                        val header = oldRequest.header(csfrPair.first)
                        if (header.isNullOrBlank()) {
                            val request = oldRequest.newBuilder().header(csfrPair.first, csfrPair.second).build()
                            info("add csrf head  $csfrPair $oldRequestUrl")
                            return@addNetworkInterceptor chain.proceed(request)
                        }
                    }
                    val oldResponse = chain.proceed(oldRequest)
//                for (header in oldRequest.headers) {
//                    info(header)
//                }
                    info("服务器返回：$oldResponse")
//                info(cSRFRequestHeadMap.keys().toJsonStr())
                    oldResponse
                } else if (pviaIpAndPortCenter in oldRequestUrl) {
                    val oldResponse = chain.proceed(oldRequest)
//                for (header in oldRequest.headers) {
//                    info(header)
//                }
                    info("服务器返回：$oldResponse")
//                info(cSRFRequestHeadMap.keys().toJsonStr())
                    oldResponse
                } else {
                    return@addNetworkInterceptor chain.proceed(chain.request())
                }

            }
            .cookieJar(JchCookieJar(cookieStore))
            .sslSocketFactory(createSSLSocketFactory(), JchTrustAllCerts())
            .hostnameVerifier(JchTrustAllHostnameVerifier())
//            .dispatcher(
//                Dispatcher(
//                    executorService = Executors.newFixedThreadPool(
//                        64,
//                        ThreadUtil.newNamedThreadFactory("OKHttpDispatcher", true)
//                    )
//                ).apply {
//                    this.maxRequests = 6400
//                    this.maxRequestsPerHost = 6300
//                })
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .callTimeout(180, TimeUnit.SECONDS)
//
//        kotlin.runCatching {
//            val cacheDirectory = File(".${System.getProperty(SystemUtil.FILE_SEPARATOR)}okhttpCacheResponseTmp")
//            if (!cacheDirectory.exists()) {
//                FileUtil.mkdir(cacheDirectory)
//            }
//            builder.cache(
//                Cache(
//                    directory = cacheDirectory,
//                    maxSize = 50L * 1024L * 1024L // 1 MiB
//                )
//            )
//        }
        okHttpClient = builder.build()
    }

    private fun doLoginPviaCenter(
        oldRes: Response,
        chain: Interceptor.Chain,
        oldRequest: Request
    ): Response {
        info("登陆pviaLoginUrlPathCenter.........", true)
//        close
        oldRes.bodyString()
        val message = getAsyn("${pviaIpAndPortCenter}/center/api/session?userId=sysadmin").get().bodyString()
        info("${pviaIpAndPortCenter}/center/api/session?userId=sysadmin" + message)
        val jsonObject = JSONUtil.parseObj(message)
        val password = SecureUtil.sha256(
            SecureUtil.sha256(pviaCenterPassword + jsonObject.getByPath("data.salt"))
                    + jsonObject.getByPath("data.challenge.code")
        )
        val response1 = postJsonStringAsyn(
            "${pviaIpAndPortCenter}/center/api/session", """
                {"user":{"id":"sysadmin"},"password":"${password}",
                "captcha":"","salt":"${jsonObject.getByPath("data.salt")}",
                "challenge":{"code":"${jsonObject.getByPath("data.challenge.code")}",
                "id":"${jsonObject.getByPath("data.challenge.id")}"}}
            """.trimIndent()
        ).get()
        val headers = response1.headers("refresh-url").firstOrNull() ?: ""
        response1.close()
        if (headers.isNotBlank()) {
            val response2 = getAsyn(headers).get()
            response2.close()
            info("登录运管成功" + response2, true)
            isLoginPviaCenter = true
        }
        oldRes.close()
        return chain.proceed(oldRequest)
    }

    /** 登陆pvia，然后再请求old url*/
    private fun doLoginPviaAndGetCsrf(chain: Interceptor.Chain, oldRequest: Request): Response {
        val loginurlRequest = Request.Builder()
            .url("${pviaIpAndPort}/portal/cas/loginPage?service=${pviaIpAndPort}/portal").build()
        val body = chain.proceed(loginurlRequest).bodyString()
        val regex = """enableCsrf\s+\=\s+JSON.parse\('([^']+)'\)""".toRegex()
        val get = regex.find(body)!!.groupValues[1]
        info("loginPage:" + get)
        //    {"token":"7Reae4fy-BTLJ_QoGKxkpykAyyus6E4aVA6U","parameterName":"_csrf","headerName":"X-CSRF-TOKEN"}
        val jsonObject = JSONUtil.parseObj(get)
        cSRFRequestHeadDefault =
            cSRFRequestHeadDefault.copy(jsonObject.getStr("headerName"), jsonObject.getStr("token"))
        cSRFRequestHeadMap["portal"] =
            cSRFRequestHeadDefault.copy(jsonObject.getStr("headerName"), jsonObject.getStr("token"))
        val login = loginPvia(pviaLoginUser, pviaPassword)
        if (login) {
            info("登陆成功，获取所有csfr", true)
            for ((firstpath1, url1) in systemCsrfUrlMap) {
                val csrfNameAndValueFromHtml1 = getCsrfNameAndValueFromHtml(url1)
                if (csrfNameAndValueFromHtml1.first.isBlank()) continue
                cSRFRequestHeadMap[firstpath1] = csrfNameAndValueFromHtml1
            }
        }
        return chain.proceed(oldRequest)
    }

    fun getAsyn(url: String, callback: Callback, headers: Map<String, String>? = null) {
        val request = Request.Builder()
            .url(url)
            .addHeaders(headers)
            .build()
        okHttpClient.newCall(request).enqueue(callback)
    }

    fun getAsyn(url: String, headers: Map<String, String>? = null): CompletableFuture<Response> {
        val future = CompletableFuture<Response>()
        val request = Request.Builder()
            .url(url)
            .addHeaders(headers)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                future.completeExceptionally(e)
            }

            override fun onResponse(call: Call, response: Response) {
                future.complete(response)
            }
        })
        return future
    }

    /** 同步请求*/
    @JvmOverloads
    fun getSyn(url: String, headers: Map<String, String>? = null): String {
        val request = Request.Builder()
            .url(url)
            .addHeaders(headers)
            .build()
        return okHttpClient.newCall(request).executeForUtf8()
    }

    /** 同步请求*/
    @JvmOverloads
    fun getSynForObject(url: String, headers: Map<String, String>? = null): Response {
        val request = Request.Builder()
            .url(url)
            .addHeaders(headers)
            .build()
        return okHttpClient.newCall(request).execute()
    }

    /** 同步请求*/
    @JvmOverloads
    fun getSynRetry6Times(url: String, headers: Map<String, String>? = null): String {
        var result = ""
        (1..3).forEach {
            try {
                val synWithForm = getSyn(url, headers)
                result = synWithForm
                if (synWithForm.pviaOkBody()) {
                    return synWithForm
                }
            } catch (e: Exception) {
                info(e.localizedMessage, true)
            }
        }
        cSRFRequestHeadMap.clear()
        cookieStore.clear()
        (1..3).forEach {
            try {
                val synWithForm = getSyn(url, headers)
                result = synWithForm
                if (synWithForm.pviaOkBody()) {
                    return synWithForm
                }
            } catch (e: Exception) {
                info(e.localizedMessage, true)
            }
        }
        return result
    }

    /** 将表单数据加到URL中（用于GET表单提交） 表单的键值对会被url编码，但是url中原参数不会被编码*/
    @JvmOverloads
    fun getAsynWithForm(
        url: String,
        form: Map<String, String>,
        headers: Map<String, String>? = null
    ): CompletableFuture<Response> {
        val future = CompletableFuture<Response>()
        val request = Request.Builder()
            .url(HttpUtil.urlWithForm(url, form, CharsetUtil.CHARSET_UTF_8, true))
            .addHeaders(headers)
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                future.completeExceptionally(e)
            }

            override fun onResponse(call: Call, response: Response) {
                future.complete(response)
            }
        })
        return future
    }

    /** 将表单数据加到URL中（用于GET表单提交） 表单的键值对会被url编码，但是url中原参数不会被编码*/
    @JvmOverloads
    fun getSynWithForm(
        url: String,
        form: Map<String, String>,
        headers: Map<String, String>? = null
    ): String {
        val request = Request.Builder()
            .url(HttpUtil.urlWithForm(url, form, CharsetUtil.CHARSET_UTF_8, true))
            .addHeaders(headers)
            .build()
        return okHttpClient.newCall(request).executeForUtf8()
    }

    /** 将表单数据加到URL中（用于GET表单提交） 表单的键值对会被url编码，但是url中原参数不会被编码
     * 先重试5次，5次后，清空登陆信息，再重试5次
     * whatBodyIsError 是错误消息里面存在的字符串
     * */
    @JvmOverloads
    fun getSynWithFormTry10Times(
        url: String,
        form: Map<String, String>,
        headers: Map<String, String>? = null
    ): String {
        var result = ""
        (1..3).forEach {
            try {
                val synWithForm = getSynWithForm(url, form, headers)
                result = synWithForm
                if (synWithForm.pviaOkBody()) {
                    return synWithForm
                }
            } catch (e: Exception) {
                info(e.localizedMessage, true)
            }
        }
        cSRFRequestHeadMap.clear()
        cookieStore.clear()
        (1..3).forEach {
            try {
                val synWithForm = getSynWithForm(url, form, headers)
                result = synWithForm
                if (synWithForm.pviaOkBody()) {
                    return synWithForm
                }
            } catch (e: Exception) {
                info(e.localizedMessage, true)
            }
        }
        return result
    }

//    fun getSyn(url: String, headers: Map<String, String>? = null): Response {
//        val request = Request.Builder()
//                .url(url)
//                .addHeaders(headers)
//                .build()
//        return client.newCall(request).execute()
//    }

    fun postFileAsyn(
        url: String,
        file: File,
        mediaType: MediaType? = null,
        headers: Map<String, String>? = null
    ): CompletableFuture<Response> {
        val future = CompletableFuture<Response>()
        val request = Request.Builder()
            .url(url)
            .post(file.asRequestBody(mediaType?.toString()?.toMediaType()))
            .addHeaders(headers)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                future.completeExceptionally(e)
            }

            override fun onResponse(call: Call, response: Response) {
                future.complete(response)
            }
        })
        return future
    }

    fun postFormAsyn(
        url: String,
        form: Map<String, String>,
        headers: Map<String, String>? = null
    ): CompletableFuture<Response> {
        val future = CompletableFuture<Response>()
        val formbuilder = FormBody.Builder()
        for ((k, v) in form) {
            formbuilder.add(k, v)
        }
        val formBody = formbuilder.build()
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .addHeaders(headers)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                future.completeExceptionally(e)
            }

            override fun onResponse(call: Call, response: Response) {
                future.complete(response)
            }
        })
        return future
    }

    fun postStringAsyn(
        url: String,
        postBody: String,
        mediaType: MediaType? = null,
        headers: Map<String, String>? = null
    ): CompletableFuture<Response> {
        val future = CompletableFuture<Response>()

        val request = Request.Builder()
            .url(url)
            .post(postBody.toRequestBody((mediaType ?: MediaType.PLAIN_TEXT_UTF_8).toString().toMediaType()))
            .addHeaders(headers)
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                future.completeExceptionally(e)
            }

            override fun onResponse(call: Call, response: Response) {
                future.complete(response)
            }
        })
        return future
    }

    @JvmOverloads
    fun postJsonStringAsyn(
        url: String,
        jsonBody: String,
        headers: Map<String, String>? = null,
        form: Map<String, String>? = null
    ): CompletableFuture<Response> {
        val future = CompletableFuture<Response>()
        val urls = if (form.isNullOrEmpty()) url else HttpUtil.urlWithForm(url, form, CharsetUtil.CHARSET_UTF_8, true)
        val request = Request.Builder()
            .url(urls)
            .post(jsonBody.toRequestBody(MediaType.JSON_UTF_8.toString().toMediaType()))
            .addHeaders(headers)
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                future.completeExceptionally(e)
            }

            override fun onResponse(call: Call, response: Response) {
                future.complete(response)
            }
        })
        return future
    }

    @JvmOverloads
    fun postJsonStringSyn(
        url: String,
        jsonBody: String,
        headers: Map<String, String>? = null,
        form: Map<String, String>? = null
    ): String {
        val urls = if (form.isNullOrEmpty()) url else HttpUtil.urlWithForm(url, form, CharsetUtil.CHARSET_UTF_8, true)
        val request = Request.Builder()
            .url(urls)
            .post(jsonBody.toRequestBody(MediaType.JSON_UTF_8.toString().toMediaType()))
            .addHeaders(headers)
            .build()
        return okHttpClient.newCall(request).executeForUtf8()
    }

    fun postJsonStringSynRetry6Times(
        url: String,
        jsonBody: String,
        headers: Map<String, String>? = null,
        form: Map<String, String>? = null
    ): String {
        var result = ""
        (1..3).forEach {
            try {
                val synWithForm: String = postJsonStringSyn(url, jsonBody, headers, form)
                result = synWithForm
                if (synWithForm.pviaOkBody()) {
                    return synWithForm
                }
            } catch (e: Exception) {
                info(e.localizedMessage, true)
            }
        }
        cSRFRequestHeadMap.clear()
        cookieStore.clear()
        (1..3).forEach {
            try {
                val synWithForm: String = postJsonStringSyn(url, jsonBody, headers, form)
                result = synWithForm
                if (synWithForm.pviaOkBody()) {
                    return synWithForm
                }
            } catch (e: Exception) {
                info(e.localizedMessage, true)
            }
        }
        return result
    }

    /** form 可以是string  和file类型*/
    @JvmOverloads
    fun postMultipartSyn(
        url: String,
        form: Map<String, Any>,
        headers: Map<String, String>? = null
    ): String {
//         val IMGUR_CLIENT_ID = "9199fdef135c122"
        // Use the imgur image upload API as documented at https://api.imgur.com/endpoints/image
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
        for ((k, v) in form) {
            val file = v as? File
            if (file == null) {
                builder.addFormDataPart(k, v.toString())
            } else {
                builder.addFormDataPart(k, file.name, file.asRequestBody())
            }
        }
        val requestBody = builder
//                .addFormDataPart("title", "Square Logo")
//                .addFormDataPart("image", "logo-square.png",
//                        File("docs/images/logo-square.png").asRequestBody(PostMultipart.MEDIA_TYPE_PNG))
            .build()

        val request = Request.Builder()
//                .header("Authorization", "Client-ID ${PostMultipart.IMGUR_CLIENT_ID}")
            .url(url)
            .post(requestBody)
            .addHeaders(headers)
            .build()

        return okHttpClient.newCall(request).executeForUtf8()
    }

    /** form 可以是string  和file类型*/
    @JvmOverloads
    fun postMultipartAsyn(
        url: String,
        form: Map<String, Any>,
        headers: Map<String, String>? = null
    ): CompletableFuture<Response> {
//         val IMGUR_CLIENT_ID = "9199fdef135c122"
        val future = CompletableFuture<Response>()
        // Use the imgur image upload API as documented at https://api.imgur.com/endpoints/image
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
        for ((k, v) in form) {
            val file = v as? File
            if (file == null) {
                builder.addFormDataPart(k, v.toString())
            } else {
                builder.addFormDataPart(k, file.name, file.asRequestBody())
            }
        }
        val requestBody = builder
//                .addFormDataPart("title", "Square Logo")
//                .addFormDataPart("image", "logo-square.png",
//                        File("docs/images/logo-square.png").asRequestBody(PostMultipart.MEDIA_TYPE_PNG))
            .build()

        val request = Request.Builder()
//                .header("Authorization", "Client-ID ${PostMultipart.IMGUR_CLIENT_ID}")
            .url(url)
            .post(requestBody)
            .addHeaders(headers)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                future.completeExceptionally(e)
            }

            override fun onResponse(call: Call, response: Response) {
                future.complete(response)
            }
        })
        return future
    }

    /** form 可以是string  和file类型 失败重试*/
    @JvmOverloads
    fun postMultipartSynRetry6Times(
        url: String,
        form: Map<String, Any>,
        headers: Map<String, String>? = null
    ): String {
        var result = ""
        (1..3).forEach {
            try {
                val synWithForm = postMultipartSyn(url, form, headers)
                result = synWithForm
                if (synWithForm.pviaOkBody()) {
                    return synWithForm
                }
            } catch (e: Exception) {
                info(e.localizedMessage, true)
            }
        }
        cSRFRequestHeadMap.clear()
        cookieStore.clear()
        (1..3).forEach {
            try {
                val synWithForm = postMultipartSyn(url, form, headers)
                result = synWithForm
                if (synWithForm.pviaOkBody()) {
                    return synWithForm
                }
            } catch (e: Exception) {
                info(e.localizedMessage, true)
            }
        }
        return result
    }
}

fun Request.Builder.addHeaders(headers: Map<String, String>?): Request.Builder {
    headers ?: return this
    for ((k, v) in headers.entries) {
        this.addHeader(k, v)
    }
    return this
}

/** 调用close*/
fun Response.bodyString(): String {
    return this.use {
        if (!it.isSuccessful) ""
        else it.body?.string() ?: ""
    }
}

fun main() {
    println(
        HttpUtil.encodeParams(
            HttpUtil.urlWithForm(
                "http://www.baidu.com/是",
                mapOf("ss" to "s飒飒 1"),
                CharsetUtil.CHARSET_UTF_8,
                true
            ), CharsetUtil.CHARSET_UTF_8
        )
    )
    HttpHelper.debug = true
//    val newCall = HttpHelper.client.newCall(Request.Builder().url("http://www.baidu.com").build()).execute()
//    println(newCall.body?.string())
    println(HttpUtil.urlWithForm("http://www.baidu.com/是", mapOf("ss" to "s飒飒 1"), CharsetUtil.CHARSET_UTF_8, true))
    println(HttpHelper.getAsyn("https://www.zhihu.com/creator").get().bodyString())
}

