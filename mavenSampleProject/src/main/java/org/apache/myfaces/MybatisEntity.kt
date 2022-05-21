package org.apache.myfaces

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Component

@Mapper
interface MyMybatisMapper : BaseMapper<MybatisEntity>

@Component
@TableName("tag_camera_tag_detail")
open class MybatisEntity {
    @TableId
    @TableField(value = "index_code")
    var indexCode: String? = null

    @TableField(value = "tag_list")
    var tagList: String? = null

    override fun toString(): String =
        "Entity of type: ${javaClass.name} ( " +
            "indexCode = $indexCode " +
            "tagList = $tagList " +
            ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MybatisEntity

        if (indexCode != other.indexCode) return false
        if (tagList != other.tagList) return false

        return true
    }
}
