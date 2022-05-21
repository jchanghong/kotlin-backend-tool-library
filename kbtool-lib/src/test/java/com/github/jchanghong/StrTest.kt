package com.github.jchanghong

import com.github.jchanghong.str.toCamelCase
import com.github.jchanghong.str.toUnderlineCase
import com.github.jchanghong.str.upperFirst
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
create at 2022-05-2022/5/21-14:19
@author jiangchanghong
 */
class StrTest {
    @Test
    internal fun testString() {
        val str = "test_string"
        assertNotNull(str)
        assertEquals(str.upperFirst(), "Test_string")
        assertEquals(str.toCamelCase(), "testString")
        assertEquals("testString".toUnderlineCase(), "test_string")
    }
}
