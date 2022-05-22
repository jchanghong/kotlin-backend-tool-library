package com.github.jchanghong.http.utils

import com.github.jchanghong.http.okhttpLog
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.security.cert.X509Certificate
import java.util.concurrent.ConcurrentHashMap
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession
import javax.net.ssl.X509TrustManager

class JchCookieJar(private val cookieStore: ConcurrentHashMap<String, ConcurrentHashMap<String, Cookie>>) : CookieJar {
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        for (cookie in cookies) {
            val hashMap = cookieStore.getOrPut(cookie.domain + cookie.path) { ConcurrentHashMap() }
            hashMap[cookie.name] = cookie
        }
        okhttpLog("cookieJar saveFromResponse ${url.host}" + cookies.joinToString { it.name + it.value })
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val pathOne = url.pathSegments.firstOrNull()
        val hashMap = ConcurrentHashMap<String, Cookie>()
        val rootHashMap = cookieStore.getOrPut(url.host + "/") { ConcurrentHashMap() }
        hashMap.putAll(rootHashMap)
        if (!pathOne.isNullOrBlank()) {
            val rootHashMap2 = cookieStore.getOrPut(url.host + "/$pathOne") { ConcurrentHashMap() }
            hashMap.putAll(rootHashMap2)
        }
        okhttpLog("cookieJar loadForRequest ${url.host}$pathOne :${hashMap.values.joinToString { it.name }}")
        return hashMap.values.toList()
    }
}

class JchTrustAllCerts : X509TrustManager {
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String?) {
//            okhttpLog(authType+chain.firstOrNull().toString())
    }

    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String?) {
//            okhttpLog(authType+chain.firstOrNull().toString())
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return arrayOf()
    }
}

class JchTrustAllHostnameVerifier : HostnameVerifier {
    override fun verify(hostname: String?, session: SSLSession?): Boolean {
        okhttpLog("TrustAllHostnameVerifier " + hostname + session?.peerPort)
        return true
    }
}
