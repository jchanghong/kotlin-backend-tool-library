package com.github.jchanghong.file

import cn.hutool.core.io.FileUtil
import com.github.jchanghong.log.kInfo
import java.io.File

/**
 *
 * @author : jiangchanghong
 *
 * @version : 2020-01-08 16:46
 **/
object FileHelper {
	fun compareContent(file1: File, file2: File): String {
		val set1 = file1.readLines().map { it.trim() }.toHashSet()
		val lines2 = file2.readLines()
		val builder = StringBuilder()
		builder.append("大小分别是 ${set1.size} ${lines2.size} \n")
		for (l in lines2) {
			if (l.trim() !in set1) {
				builder.append(l + "\n")
			}
		}
		return builder.toString()
	}

	fun copyFiles(root: String, destPath: String, vararg pathRegex: String) {
		val map = pathRegex.map { it.toRegex() }
		val desp = File(destPath)
		if (!FileUtil.isDirectory(root)) {
			error("$root 不是目录")
		}
		File(root).walkBottomUp().forEach {
			if (it.isFile) {
				val firstOrNull =
					map.firstOrNull { regex -> !(regex.find(it.absolutePath)?.groupValues.isNullOrEmpty()) }
				if (firstOrNull != null) {
					val removePrefix = it.absolutePath.removePrefix(root).removePrefix("/").removePrefix("\\")
					val target = File(desp, removePrefix)

					println(it.absolutePath + "-> " + target.absolutePath)
					it.copyTo(target, true)
				}
			}
		}
	}

	private fun File.myDelete(): Unit {
		if (this.isDirectory) return
		val name = this.name.trimEnd()
		if (name.endsWith(".lastUpdated") || name.endsWith("_remote.repositories")) {
			this.delete()
			kInfo(this.absolutePath.toString() + "已删除")
		}
	}

	@JvmOverloads
			/**删除文件   .lastUpdated _remote.repositories */
	fun removeMavenLastAndRemoteFiles(path: String, group: String = ""): Unit {
		var parent = File(path)
		if (!parent.exists() || parent.isFile) {
			error(path + "目录不存在")
		}
		if (group.isNotEmpty()) {
			parent = File(parent, group.split(".").joinToString(separator = "/"))
			if (!parent.exists() || parent.isFile) {
				error(path + "目录不存在")
			}
		}
		parent.walkTopDown().forEach { it.myDelete() }
	}
}


fun main() {
	println(
		FileHelper.compareContent(
			File("D:\\暂时\\check2_camera_info渝中.csv"),
			File("D:\\暂时\\check2_camera_info渝中2.csv")
		)
	)

}
