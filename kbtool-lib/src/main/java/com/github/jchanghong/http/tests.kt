package com.github.jchanghong.http

fun main() {
//	jch_okhttp_debug=false
    println(HttpHelper.getSyn("https://www.baidu.com"))

    HttpHelper.debug = true
    HttpHelper.customSystemCsrfUrlMap.put("cvisual-web", "https://50.140.160.1/cvisual-web/cvisual")

    HttpHelper.pviaConfig("https://50.140.160.1")
    println(
        HttpHelper.postJsonStringSyn(
            "https://50.140.160.1/cvisual-web/module/cvisualWebControllerV11X/getImportantPersonEvent.do", """
				{"mapLevel":0,"pageNo":1,"pageSize":20,"publicKey":"\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzUFCORXxcyHWIuTwksK6vyh9N\nFGkpZ7nXcOlxH4p5zXfGdmSgZMfSGHF3WRVpTOrQz0GjEwtGi8CiG3uoKxGodCUs\nSwoSoPS5yE++Q5/IqhKZy1G+u/CXJiy2UMyWbEgnDgmaNlS7MpbPoFdjsnE8dq1B\no0kTMIoM6CzauxqGFwIDAQAB\n","unitIndexcode":"500235"}
		""".trimIndent()
        )
    )
}
