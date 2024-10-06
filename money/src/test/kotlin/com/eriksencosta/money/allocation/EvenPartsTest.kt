/*
 * This file is part of the Money package.
 *
 * Copyright (c) Eriksen Costa <eriksencosta@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eriksencosta.money.allocation

import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class EvenPartsTest {
    @Test
    fun `Throw exception when parts are not positive`() = assertThrows<IllegalArgumentException> {
        EvenParts(0)
    }.run {
        assertEquals("The number of parts to allocate must be greater than 0", message)
    }

    @Test
    fun `Check for equality`() {
        val parts1 = EvenParts(1)
        val parts2 = EvenParts(1)
        val parts3 = EvenParts(2)

        assertEquals(parts1, parts1, "Same instance")
        assertEquals(parts1, parts2, "Same values")
        assertNotEquals(parts1, parts3, "Different values")

        @Suppress("EqualsNullCall")
        assertFalse(parts1.equals(null), "Different instances types: null")
        assertFalse(parts1.equals(1), "Different instances types: Number")
    }

    @TestFactory
    fun `Calculate the hash code`() = listOf(
        EvenParts(1) to 1,
        EvenParts(2) to 2,
        EvenParts(3) to 3,
    ).map { (parts, expected) ->
        dynamicTest("given $parts then the hash code should be $expected") {
            assertEquals(expected, parts.hashCode())
        }
    }

    @TestFactory
    fun `Convert EvenParts to string`() = listOf(
        EvenParts(1) to "1",
        EvenParts(2) to "2",
        EvenParts(3) to "3",
    ).map { (parts, expected) ->
        dynamicTest("given $parts when I convert it to a string then I should get $expected") {
            assertEquals(expected, parts.toString())
        }
    }
}
