package com.hutool

import cn.hutool.extra.ftp.Ftp
import cn.hutool.system.SystemUtil
import java.io.File


fun main() {
	println(SystemUtil.getHostInfo().address)
	println(SystemUtil.getOsInfo().name)
	val ftp = Ftp("55555.1.64.130", 10019, "root", "Hik@12345++")
	println(ftp.init())
	ftp.cd("/opt/ftp")
	ftp.ls("/opt/ftp").forEach {
		println(it)
	}
	ftp.download("/opt/ftp/视频网建立", "index.html", File("okhttpCacheResponseTmp/"))
	ftp.close()
}
