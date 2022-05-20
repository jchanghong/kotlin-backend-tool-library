package com.github.jchanghong.gson


fun main() {
    val json = """
         {
        "devType": 1,
        "collectTime": 1598605222000,
        "msgType": 0,
        "devNo": "100086",
        "latitude": 0,
        "version": "1.0",
        "voltageLow": 0,
        "linkageDevCode": "",
        "siteNo": "",
        "vendor": "HIKVISION",
        "rfidIdentifier": "01500234024416",
        "longitude": 0
    }
    """.trimIndent()

    println(JsonHelper.jsonToKotlinClass(listOf(json)))
}
