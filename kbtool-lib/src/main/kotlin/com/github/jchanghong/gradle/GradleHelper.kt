package com.github.jchanghong.gradle

import cn.hutool.core.comparator.CompareUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.core.util.XmlUtil
import cn.hutool.http.HttpUtil
import java.io.File
import kotlin.math.max

private data class GradleDep(var name: String? = null, var groupAndName: String? = null, var version: String? = null) :
    Comparable<GradleDep> {
    override fun toString(): String {
        return if (!version.isNullOrBlank()) {
            """    ${name}("${groupAndName}:${version}")"""
        } else if (name.isNullOrBlank()) {
            """    ${groupAndName}"""
        } else {
            """    ${name}("${groupAndName}") """
        }
    }

    override fun compareTo(other: GradleDep): Int {
        return CompareUtil.compare(this.groupAndName ?: "", other.groupAndName ?: "")
    }
}

object GradleHelper {
    /** 1.1.a 改成1.1  1.1a -》1.1*/
    fun versionToNumbers(version: String?): String {
        val toRegex = """[0-9]+(\.[0-9]+)*""".toRegex()
        if (version.isNullOrBlank()) return ""
        for (i in version.length downTo 1) {
            val subSequence = version.subSequence(0, i)
            if (toRegex.matches(subSequence)) {
                return subSequence.toString()
            }
        }
        return version
    }

    fun compareVersion(version1: String, version2: String): Int {
        if (version1.isBlank()) return -1
        if (version2.isBlank()) return 1
//        val min1 = min(version1.length, version2.length)
        var v1 = versionToNumbers(version1)
        var v2 = versionToNumbers(version2)
//        for (i in (min1-1) downTo 0){
//            if (v1.last()!=v2.last()) break
//            v1=v1.substring(0,v1.length-1)
//            v2=v2.substring(0,v2.length-1)
//        }
        val splitTrim1 = StrUtil.splitTrim(v1, ".").map { it.toIntOrNull() ?: 0 }
        val splitTrim2 = StrUtil.splitTrim(v2, ".").map { it.toIntOrNull() ?: 0 }
        val max = max(splitTrim1.size, splitTrim2.size)
        for (i in 0 until max) {
            val i1 = splitTrim1.getOrNull(i) ?: 0
            val i2 = splitTrim2.getOrNull(i) ?: 0
            if (i1 > i2) return 1
            if (i1 < i2) return -1
        }
        return 0
    }

    fun goodVersion(version: String?): Boolean {
        if (version.isNullOrBlank()) return false
        return (versionToNumbers(version).trim() == version) || (version == versionToNumbers(version) + ".RELEASE")
                || (version == versionToNumbers(version) + ".RELEASE")
    }

    fun upgradeAllDependency(buildFile: File): Unit {
        val regex = """(\S+)\s*\(\s*["']\s*(\s*\S+\s*:\s*\S+\s*)\s*["']""".toRegex()
        val regexdependencies = """dependencies\s*\{[^}]+}""".toRegex()
        val readText = buildFile.readText()
        println(readText)
//        需要替换的模块
        val dependencies = regexdependencies.find(readText)?.groupValues?.get(0) ?: return
//        val findAll = regex.findAll(dependencies)
        val list = dependencies.lines().mapNotNull { line ->
            val gradleDep = GradleDep()
            if (line.isBlank()) return@mapNotNull null
            if ("project(" in line) {
                gradleDep.groupAndName = line
                gradleDep.version = ""
                gradleDep.name = ""
                return@mapNotNull gradleDep.toString()
            }
            val it = regex.find(line) ?: return@mapNotNull null
//            println(it.groupValues)

            val name = it.groupValues.get(1)
            val tmp = it.groupValues.get(2)
            val splitTrim = StrUtil.splitTrim(tmp, ":")
//       [api("cn.hutool:hutool-all:5.6.2", api, cn.hutool:hutool-all:5.6.2]
            if ("project(" in line) {
                gradleDep.groupAndName = line
                gradleDep.version = ""
                gradleDep.name = ""
            } else {
                gradleDep.name = name
                gradleDep.groupAndName = "${splitTrim.get(0)}:${splitTrim.get(1)}"
                if (splitTrim.size >= 3) {
                    gradleDep.version = splitTrim.get(2)
                }
                println(gradleDep.toString())
                if (!gradleDep.version.isNullOrBlank()) {
                    val message = getMavenVersionAndTime2(gradleDep.groupAndName!!)
                    println(message)
                    if (!message?.first.isNullOrBlank()) {
                        gradleDep.version = message!!.first!!
                    }
                }
            }
            gradleDep.toString()

        }.sorted()
        val joinToString = list.joinToString("\n", prefix = "dependencies {\n", postfix = "\n}")
        println("end===============================")
//        println(joinToString)
        val replace = readText.replace(dependencies, joinToString)
        println(replace)
        buildFile.writeText(replace)
    }

    /** 根据group:name 获取最新版本和时间*/
    fun getMavenVersionAndTime2(key: String): Pair<String?, String?>? {
        val not = listOf("SNAPSHOT", "M", "ALPHA", "BE", "RC", "ANDROID")
        try {
            val path = key.trim().replace(":", ".", true).split(".").joinToString(separator = "/")
            val runCatching = runCatching {
                val url = "https://maven.aliyun.com/repository/public/$path/maven-metadata.xml"
                println(url)
                val message: String = HttpUtil.createGet(url).setMaxRedirectCount(100).execute().body() ?: ""
                val xmlToMap: MutableMap<String, Any?>? = XmlUtil.xmlToMap(message)
                var version = (xmlToMap?.get("versioning") as? Map<String, Any?>?)?.get("release")?.toString()
                var lastUpdated = (xmlToMap?.get("versioning") as? Map<String, Any?>?)?.get("lastUpdated")?.toString()
                val versions =
                    ((xmlToMap?.get("versioning") as? Map<String, Any?>?)?.get("versions") as? Map<String, List<String>>?)?.get(
                        "version"
                    ) ?: emptyList()

                val sortedDescending = versions.filter {
                    val firstOrNull = not.firstOrNull { a -> a in it.uppercase() }
                    val splitTrim = StrUtil.splitTrim(it, ".")
                    val toString = splitTrim.get(0) ?: ""
                    (firstOrNull == null) && splitTrim.size > 1 && toString.length < 5
                }.sortedWith(object : Comparator<String> {
                    override fun compare(o1: String?, o2: String?): Int {
                        return (compareVersion(o1 ?: "", o2 ?: ""))
                    }
                }).reversed()
                if (!sortedDescending.isNullOrEmpty() && !goodVersion(version)) {
                    version = sortedDescending.get(0)
                }
                version to lastUpdated
            }
            if (runCatching.isSuccess) return runCatching.getOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}

fun main() {

    println(GradleHelper.goodVersion("1.0"))
    println(GradleHelper.goodVersion("1.0.0"))
    println(GradleHelper.goodVersion("1.0.11.RELEASE"))
    println(GradleHelper.goodVersion("1.0.a"))
    println(GradleHelper.goodVersion("1.0.1a"))
    println(GradleHelper.versionToNumbers("1.0"))
    println(GradleHelper.versionToNumbers(""))
    println(GradleHelper.versionToNumbers("1.0.a"))
    println(GradleHelper.versionToNumbers("1.0.2a"))
    println(GradleHelper.versionToNumbers("1.0.2-a"))
    println(GradleHelper.versionToNumbers("1.0.2_a"))
    val toRegex = """[0-9]+(\.[0-9]+)*""".toRegex()

    println(toRegex.matches("1.4.212"))
    println(toRegex.matches("1.4.212.1"))
    println(GradleHelper.compareVersion("1.4.21-2", "1.4.32"))
    println(GradleHelper.compareVersion("1.4.32-2", "1.4.32.1"))
    println(GradleHelper.compareVersion("1.4.32.1-2", "1.4.32"))
//    println(GradleHelper.getMavenVersionAndTime2("io.netty:netty-all"))
//    GradleHelper.upgradeAllDependency(File("D:\\gitee\\kotlin-lib\\kotlin-lib\\test.kts"))
    GradleHelper.upgradeAllDependency(File("D:\\gitee\\kotlin-lib\\buildSrc\\build.gradle.kts"))
}
