package org.apache.myfaces

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import javax.persistence.*

interface MyJpaReposotory : JpaRepository<JpaEntity, String>

@Entity
@Component
@Table(name = "tag_camera_tag_detail")
open class JpaEntity {
	@get:Id
	@get:Column(name = "index_code", nullable = false)
	var indexCode: String? = null

	@get:Basic
	@get:Column(name = "tag_list", nullable = true)
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
		other as JpaEntity

		if (indexCode != other.indexCode) return false
		if (tagList != other.tagList) return false

		return true
	}

}

