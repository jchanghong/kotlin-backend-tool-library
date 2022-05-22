package com.github.jchanghong.io

import cn.hutool.core.io.IoUtil
import cn.hutool.core.io.resource.FileResource
import cn.hutool.core.io.resource.ResourceUtil
import cn.hutool.core.util.CharsetUtil
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternResolver
import java.io.File
import java.io.IOException
import java.io.InputStream

object IOHelper {
    private val resourceResolver: ResourcePatternResolver = PathMatchingResourcePatternResolver()

    /**PathMatchingResourcePatternResolver */
    fun getResources(location: String?): Array<Resource?> {
        return try {
            if (location.isNullOrBlank()) return emptyArray()
            resourceResolver.getResources(location)
        } catch (e: IOException) {
            arrayOfNulls(0)
        }
    }

    /** PathMatchingResourcePatternResolver*/
    fun resolveMapperLocations(vararg mapperLocations: String): Array<Resource?>? {
        if (mapperLocations.isNullOrEmpty()) return emptyArray()
        return mapperLocations.flatMap { getResources(it).toList() }.toTypedArray()
    }

    fun readMore(classPathORfilepath: String?): String {
        var stream: InputStream? = null
        stream = try {
            FileResource(File(classPathORfilepath)).stream
        } catch (e: Exception) {
            ResourceUtil.getStream(classPathORfilepath)
        }
        return IoUtil.read(stream, CharsetUtil.UTF_8)
    }
}
