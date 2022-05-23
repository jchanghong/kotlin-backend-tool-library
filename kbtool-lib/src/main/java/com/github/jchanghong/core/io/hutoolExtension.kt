package com.github.jchanghong.core.io

import cn.hutool.core.io.resource.ClassPathResource
import cn.hutool.core.io.resource.Resource
import cn.hutool.core.io.resource.ResourceUtil

/**
create at 2022-05-2022/5/23-14:44
@author jiangchanghong
 */
fun String.toClassPathResource(): ClassPathResource {
    return ClassPathResource(this)
}
fun String.toResourceObj(): Resource {
    return ResourceUtil.getResourceObj(this)
}
