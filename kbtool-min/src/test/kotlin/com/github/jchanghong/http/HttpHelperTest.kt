package com.github.jchanghong.http

fun main() {
	HttpHelper.debug = true
	HttpHelper.pviaConfig("https://55555.1.172.2")
	val syn = HttpHelper.postJsonStringAsyn(
		"https://55555.1.172.2/xcascade-web/p/getPagedLowerPlatforms.do", """
        {"pageNo":1,"pageSize":20}
    """.trimIndent()
	).get().bodyString()
	println(syn)
}
//internal class HttpHelperTest {
//    //    val testIP = "50.62.244.1"
//    val testIP = "55555.1.172.2"
//
//    @Test
//    fun testtieba() {
//        println(HttpHelper.getAsyn("http://tieba.baidu.com/f/like/mylike?v=1616037775130").get().bodyString())
//    }
//
//    @Before
//    fun setUp() {
//        println("setup=========================")
//        HttpHelper.debug = true
//        HttpHelper.pviaConfig("https://55555.1.172.2")
//        HttpHelper.pviaCenterConfig("http://55555.1.172.2:8001","sj@12345++")
//    }
//
//    @Test
//    fun testcookie() {
//        println(HttpHelper.getFirstPath("https://55555.1.172.2/ivehicle-web/view/index.do") == "ivehicle-web")
//        println("_csrf_header" in HttpHelper.getAsyn("https://$testIP/ivehicle-web/view/index.do").get().bodyString())
//        assertEquals(true,"_csrf_header" in HttpHelper.getAsyn("https://$testIP/ivehicle-web/view/index.do").get().bodyString())
//    }
//
//    @Test
//    fun testbaidu() {
//        println(HttpHelper.getAsyn("http://www.baidu.com").get().bodyString())
//        println(HttpHelper.getAsyn("http://www.baidu.com").get().bodyString())
//    }
//
//    @Test
//    fun testcenterlog() {
//        println(Date(1615946073608).toStrOrNow())
//        HttpHelper.debug=true
//        val syn = HttpHelper.getSyn("http://55555.1.172.2:8001/center/api/status")
//        println(syn)
//        assertEquals(true,syn.pviaOkBody())
//        val url = "http://55555.1.172.4:8080/logservice/api/logs?actionType=&actionResult=succeed&keyword=&type=business&userId=&clientIP=&pageNo=1&pageSize=100&sort=DESCENDING&begin=2021-05-31T01:14:17.633Z&end=2021-05-31T01:29:17.633Z&timestamp=1622424569555"
//        println(HttpHelper.getAsyn(url).get())
//        val bodyString = HttpHelper.getAsyn(url).get().bodyString()
//        println(bodyString)
//        assertEquals(true,bodyString.pviaOkBody())
//    }
//
//    @Test
//    fun centerlog2() {
//        HttpHelper.debug=true
////        println(HttpHelper.getSyn("http://55555.1.172.2:8001/center/api/status"))
////        assertEquals(true,HttpHelper.getSynRetry6Times("http://55555.1.172.2:8001/center/api/status").pviaOkBody())
//        val url="http://55555.1.172.4:8080/logservice/api/logs?actionType=&actionResult=succeed&keyword=&type=business&userId=&clientIP=&pageNo=1&pageSize=100&sort=DESCENDING&begin=2021-05-31T01:14:17.633Z&end=2021-05-31T01:29:17.633Z&timestamp=1622424569555"
//        val times = HttpHelper.getSynRetry6Times(url)
//        println(times)
//        assertEquals(true,times.pviaOkBody())
//    }
//
//    //    @Test
//    fun testall() {
//        testcenterlog()
//        println(HttpHelper.postJsonStringAsyn("https://$testIP/iface-web/faceSearch/findSnapFaceBySpecific.do", """
//                    {"beginFaceTime":"2021-03-17T00:00:00.000+08:00","endFaceTime":"2021-03-17T23:59:59.000+08:00",
//                    "deviceInfos":[],"orgInfos":[],"filterLink":"all","faceIQA":"true",
//                    "ageGroup":"","glass":"","gender":"","smile":"","key":1615951067968,"pageNo":1,"pageSize":20}
//                """.trimIndent()).get().bodyString())
//        val url = "http://55555.1.172.4:8080/logservice/api/logs?actionType=&actionResult=succeed,failed,partial_succeed&keyword=&type=business&userId=&clientIP=&pageNo=1&pageSize=100&sort=DESCENDING&begin=2021-03-17T01:37:46.282Z&end=2021-03-17T01:52:46.282Z&timestamp=1615946073608"
//        println(HttpHelper.getAsyn(url).get().bodyString())
//        println(HttpHelper.getAsyn(url).get().bodyString())
//        println(HttpHelper.postJsonStringAsyn("https://$testIP/iface-web/faceSearch/findSnapFaceBySpecific.do", """
//                    {"beginFaceTime":"2021-03-17T00:00:00.000+08:00","endFaceTime":"2021-03-17T23:59:59.000+08:00",
//                    "deviceInfos":[],"orgInfos":[],"filterLink":"all","faceIQA":"true",
//                    "ageGroup":"","glass":"","gender":"","smile":"","key":1615951067968,"pageNo":1,"pageSize":20}
//                """.trimIndent()).get().bodyString())
//        println(HttpHelper.postJsonStringAsyn("https://$testIP/ivehicle-web/web/vehiclequery/list.do", """
//                   {"data":{"plateNo":"渝","beginTime":"2021-03-17T00:00:00.000+08:00",
//                   "endTime":"2021-03-17T23:59:59.000+08:00","multiTimePeriod":false,
//                   "crossIndexCodes":"","orgIndexCodes":"","treeCode":"0","laneNo":"","directionIndex":"",
//                   "queryType":"vehiclepass","vehicleColor":"","vehicleType":"","plateType":"",
//                   "plateColor":"","vehicleHead":"","sunroof":"","pdvs":"",
//                   "luggageRack":"","vehicleSprayPainted":"",
//                   "vehicleLamp":"","spareTire":"","pilotSunvisor":"",
//                   "vicePilotSunvisor":"","label":"","envproSign":"","dangMark":"","pendant":"","decoration":"","tissueBox":"","card":"","tempPlateNo":"","pilotSafebelt":"","vicePilotSafebelt":"","uphone":"","copilot":"","frontChild":"","muckTruck":"","coverPlate":"","labelNum":"",
//                   "vehicleSpeed":"","hasLinkFaceVehicle":""},"metadata":{"pageNo":1,"pageSize":20,"isAuth":true}}
//                """.trimIndent()).get().bodyString())
//
//        println(HttpHelper.postJsonStringAsyn("https://$testIP/ibody-web/web/search/findIntelliSearchResult.do", """
//                  {"startTime":"2021-03-17T00:00:00.000+08:00","endTime":"2021-03-17T23:59:59.000+08:00"
//                  ,"otherConditionJson":"{\"rideStatus\":false,\"direction\":\"\",\"gender\":\"\",\"glass\":\"\",\"hairStyle\":\"\",\"ageGroup\":\"\",\"cyclingPersonNumber\":\"\",\"targetSize\":\"\",\"jacketType\":\"\",\"mask\":\"\",\"bag\":\"\",\"ride\":\"\",\"cyclingType\":\"\",\"speed\":\"\",\"trousersType\":\"\",\"hat\":\"\",\"things\":\"\",\"vehicleType\":\"\",\"jacketColor\":\"\",\"trousersColor\":\"\"}",
//                  "resourceType":"CAMERA","filterLink":"all","key":1615962809803,"pageNo":1,"pageSize":20}
//                """.trimIndent()).get().bodyString())
//    }
//
//    @Test
//    fun testXFACE() {
//        val bodyString = HttpHelper.postJsonStringAsyn(
//            "https://$testIP/iface-web/faceSearch/findSnapFaceBySpecific.do", """
//                    {"beginFaceTime":"2021-03-17T00:00:00.000+08:00","endFaceTime":"2021-03-17T23:59:59.000+08:00",
//                    "deviceInfos":[],"orgInfos":[],"filterLink":"all","faceIQA":"true",
//                    "ageGroup":"","glass":"","gender":"","smile":"","key":1615951067968,"pageNo":1,"pageSize":20}
//                """.trimIndent()
//        ).get().bodyString()
//        println(bodyString)
//        assertEquals(true,bodyString.pviaOkBody())
////        println(HttpHelper.postJsonStringAsyn("https://$testIP/iface-web/faceSearch/findSnapFaceBySpecific.do", """
////                    {"beginFaceTime":"2021-03-17T00:00:00.000+08:00","endFaceTime":"2021-03-17T23:59:59.000+08:00",
////                    "deviceInfos":[],"orgInfos":[],"filterLink":"all","faceIQA":"true",
////                    "ageGroup":"","glass":"","gender":"","smile":"","key":1615951067968,"pageNo":1,"pageSize":20}
////                """.trimIndent()).get().bodyString())
////        println(HttpHelper.postJsonStringAsyn("https://$testIP/iface-web/faceSearch/findSnapFaceBySpecific.do", """
////                    {"beginFaceTime":"2021-03-17T00:00:00.000+08:00","endFaceTime":"2021-03-17T23:59:59.000+08:00",
////                    "deviceInfos":[],"orgInfos":[],"filterLink":"all","faceIQA":"true",
////                    "ageGroup":"","glass":"","gender":"","smile":"","key":1615951067968,"pageNo":1,"pageSize":20}
////                """.trimIndent()).get().bodyString())
//    }
//
//    @Test
//    fun testivehicle() {
//        HttpHelper.debug=true
//        val bodyString = HttpHelper.postJsonStringAsyn(
//            "https://$testIP/ivehicle-web/web/vehiclequery/list.do", """
//                   {"data":{"plateNo":"渝","beginTime":"2021-03-17T00:00:00.000+08:00",
//                   "endTime":"2021-03-17T23:59:59.000+08:00","multiTimePeriod":false,
//                   "crossIndexCodes":"","orgIndexCodes":"","treeCode":"0","laneNo":"","directionIndex":"",
//                   "queryType":"vehiclepass","vehicleColor":"","vehicleType":"","plateType":"",
//                   "plateColor":"","vehicleHead":"","sunroof":"","pdvs":"",
//                   "luggageRack":"","vehicleSprayPainted":"",
//                   "vehicleLamp":"","spareTire":"","pilotSunvisor":"",
//                   "vicePilotSunvisor":"","label":"","envproSign":"","dangMark":"","pendant":"","decoration":"","tissueBox":"","card":"","tempPlateNo":"","pilotSafebelt":"","vicePilotSafebelt":"","uphone":"","copilot":"","frontChild":"","muckTruck":"","coverPlate":"","labelNum":"",
//                   "vehicleSpeed":"","hasLinkFaceVehicle":""},"metadata":{"pageNo":1,"pageSize":20,"isAuth":true}}
//                """.trimIndent()
//        ).get().bodyString()
//        println(bodyString)
//        assertEquals(true, bodyString.pviaOkBody())
////        println(HttpHelper.postJsonStringAsyn("https://$testIP/ivehicle-web/web/vehiclequery/list.do", """
////                   {"data":{"plateNo":"渝","beginTime":"2021-03-17T00:00:00.000+08:00",
////                   "endTime":"2021-03-17T23:59:59.000+08:00","multiTimePeriod":false,
////                   "crossIndexCodes":"","orgIndexCodes":"","treeCode":"0","laneNo":"","directionIndex":"",
////                   "queryType":"vehiclepass","vehicleColor":"","vehicleType":"","plateType":"",
////                   "plateColor":"","vehicleHead":"","sunroof":"","pdvs":"",
////                   "luggageRack":"","vehicleSprayPainted":"",
////                   "vehicleLamp":"","spareTire":"","pilotSunvisor":"",
////                   "vicePilotSunvisor":"","label":"","envproSign":"","dangMark":"","pendant":"","decoration":"","tissueBox":"","card":"","tempPlateNo":"","pilotSafebelt":"","vicePilotSafebelt":"","uphone":"","copilot":"","frontChild":"","muckTruck":"","coverPlate":"","labelNum":"",
////                   "vehicleSpeed":"","hasLinkFaceVehicle":""},"metadata":{"pageNo":1,"pageSize":20,"isAuth":true}}
////                """.trimIndent()).get().bodyString())
//    }
//
//        @Test
//    fun testibody() {
//            val message = HttpHelper.postJsonStringAsyn(
//                "https://$testIP/ibody-web/web/search/findIntelliSearchResult.do", """
//                 {"startTime":"2021-03-17T00:00:00.000+08:00","endTime":"2021-03-17T23:59:59.000+08:00","otherConditionJson":"{\"rideStatus\":false,\"direction\":\"\",\"gender\":\"\",\"glass\":\"\",\"hairStyle\":\"\",\"ageGroup\":\"\",\"cyclingPersonNumber\":\"\",\"targetSize\":\"\",\"jacketType\":\"\",\"mask\":\"\",\"bag\":\"\",\"ride\":\"\",\"cyclingType\":\"\",\"speed\":\"\",\"trousersType\":\"\",\"hat\":\"\",\"things\":\"\",\"vehicleType\":\"\",\"jacketColor\":\"\",\"trousersColor\":\"\"}",
//                 "resourceType":"CAMERA","filterLink":"all","key":1615962809803,"pageNo":1,"pageSize":20}
//                """.trimIndent()
//            ).get().bodyString()
//            println(message)
//            assertEquals(true,message.pviaOkBody())
//            val string = HttpHelper.postJsonStringAsyn(
//                "https://$testIP/ibody-web/web/search/findIntelliSearchResult.do", """
//                  {"startTime":"2021-03-1700:00:00.000+08:00","endTime":"2021-03-17T23:59:59.000+08:00"
//                  ,"otherConditionJson":"{\"rideStatus\":false,\"direction\":\"\",\"gender\":\"\",\"glass\":\"\",\"hairStyle\":\"\",\"ageGroup\":\"\",\"cyclingPersonNumber\":\"\",\"targetSize\":\"\",\"jacketType\":\"\",\"mask\":\"\",\"bag\":\"\",\"ride\":\"\",\"cyclingType\":\"\",\"speed\":\"\",\"trousersType\":\"\",\"hat\":\"\",\"things\":\"\",\"vehicleType\":\"\",\"jacketColor\":\"\",\"trousersColor\":\"\"}",
//                  "resourceType":"CAMERA","filterLink":"all","key":1615962809803,"pageNo":1,"pageSize":20}
//                """.trimIndent()
//            ).get().bodyString()
//            println(string)
//            assertEquals(false,string.pviaOkBody())
//    }
//
//    @Test
//    fun testialarm() {
//        HttpHelper.debug=true
//        val message = HttpHelper.postJsonStringAsyn(
//            "https://$testIP/ialarm-web/alarm/findAlarmBaseInfoPage.do", """
//              {"componentType":"TARGET_ALARM","page":1,"limit":10,"alarmEventComponentId":"","alarmEventObjName":"","alarmEventType":"","deploySubType":"",
//              "deployType":"","deployName":"","alarmTimeEnd":"","alarmTimeStart":"","endPointType":"1"}
//                """.trimIndent()
//        ).get().bodyString()
//        println(message)
//        assertEquals(true,message.pviaOkBody())
//
////        println(HttpHelper.postJsonStringAsyn("https://$testIP/ialarm-web/alarm/findAlarmBaseInfoPage.do", """
////                {"componentType":"TARGET_ALARM","page":1,"limit":10,"alarmEventComponentId":"",
////                "alarmEventObjName":"","alarmEventType":"","deploySubType":"",
////                "deployType":"","deployName":"","alarmTimeEnd":"","alarmTimeStart":"","endPointType":"1"}
////                """.trimIndent()).get().bodyString())
//    }
//
//    @Test
//    fun xnamelistweb() {
//        HttpHelper.debug=true
//        val message = HttpHelper.postJsonStringSynRetry6Times(
//            "https://55555.1.172.2/xnamelist-web/lib/list.do", """
//            {"type":0,"pageNo":1,"pageSize":16,"hasDefault":true}
//        """.trimIndent()
//        )
//        println(message)
//        assertEquals(true,message.pviaOkBody())
//        val bodyString = HttpHelper.getAsyn("https://55555.1.172.2/xnamelist-web/config/tagList.do").get().bodyString()
//        println(bodyString)
//        assertEquals(true,bodyString.pviaOkBody())
//    }
//
//    @Test
//    fun testConfig() {
//    }
//}
